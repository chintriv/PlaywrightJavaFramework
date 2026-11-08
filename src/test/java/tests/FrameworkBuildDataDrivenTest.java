package tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.*;
import utils.DataProviderUtil;

import java.io.IOException;
import java.util.HashMap;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameworkBuildDataDrivenTest extends TestBase {

    @DataProvider(name="eventBookingData")
    public Object[][] eventBookingData() throws IOException {
        return DataProviderUtil.getJsonDataToMap("/src/test/resources/eventBookingData.json");


    }

    @Test(groups = {"framework"}, dataProvider = "eventBookingData", description = "create event - book that event and verify the booking")
    public void DemoTest(HashMap<String, String> data){

        LoginPage loginPage = new LoginPage(page, base_url);
        DashboardPage dashboardPage=loginPage.loginToApplication();
        //DashboardPage dashboardPage = new DashboardPage(page);
        //loginPage.loginToApplication();

        dashboardPage.waitForEventsToLoad();

        AdminEventsPage adminEventsPage = new AdminEventsPage(page);
        adminEventsPage.goTo();
        // Step 1  - create event from admin page
        adminEventsPage.createEvent(
                data.get("titlePrefix"),
                data.get("description"),
                data.get("city"),
                data.get("venue"),
                data.get("dateTime"),
                data.get("price"),
                data.get("totalSeats")
        );

        // Step 2 - find newly created event on the events page
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();
        Locator targetCard = eventsPage.findEventCard(data.get("titlePrefix"));
        int seatsNumBeforeBooking = eventsPage.getSeatsCount(targetCard);
        BookingFormPage bookingFormPage = eventsPage.proceedToBookingEvent(targetCard);

        //Book the ticket for event
        bookingFormPage.fillAndConfirm(data.get("fullName"),
                data.get("email"),
                data.get("phone")
        );


        //assertThat(targetCard).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));





        String bookingRef = page.locator(".booking-ref").innerText();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View My Bookings")).click();

        //verify in booking history
        Locator bookingCards = page.locator("#booking-card");
        Locator targetBookingCard = bookingCards.filter(new Locator.FilterOptions().setHasText(bookingRef));
        assertThat(targetBookingCard).isVisible();
        //seat count reduction check
        page.locator("#nav-events").click();
        page.waitForTimeout(1000);

        Locator eventCardsAfterBooking = page.getByTestId("event-card");
        Locator targetCardAfterBooking = eventCardsAfterBooking.filter(new Locator.FilterOptions().setHasText("QA Summit Chinmay"));
        //assertThat(targetCard).isVisible();
        String seatsTextAfterBooking = targetCardAfterBooking.getByText("seats").innerText();
        System.out.println(seatsTextAfterBooking);
        //After booking seats < before booking
        int seatsNumAfterBooking =  Integer.parseInt(seatsTextAfterBooking.split(" ")[0]);

        Assert.assertTrue(seatsNumBeforeBooking > seatsNumAfterBooking);

    }


}
