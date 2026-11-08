package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class FrameworkBuildTest extends TestBase {


    @Test(groups = {"framework"}, description = "create event - book that event and verify the booking")
    public void DemoTest(){
        String eventTitle = "Playwright Framework Test";
        LoginPage loginPage = new LoginPage(page, base_url);
        DashboardPage dashboardPage=loginPage.loginToApplication();
        //DashboardPage dashboardPage = new DashboardPage(page);
        //loginPage.loginToApplication();

        dashboardPage.waitForEventsToLoad();

        AdminEventsPage adminEventsPage = new AdminEventsPage(page);
        adminEventsPage.goTo();
        // Step 1  - create event from admin page
        adminEventsPage.createEvent(
                eventTitle,
                "Playwright Test event",
                "Test City",
                "Test Venue",
                "2027-12-18T07:25",
                "100",
                "50"
        );

        // Step 2 - find newly created event on the events page
        EventsPage eventsPage = new EventsPage(page);
        eventsPage.goTo();
        Locator targetCard = eventsPage.findEventCard(eventTitle);
        int seatsNumBeforeBooking = eventsPage.getSeatsCount(targetCard);
        BookingFormPage bookingFormPage = eventsPage.proceedToBookingEvent(targetCard);

        //Book the ticket for event
        bookingFormPage.fillAndConfirm("Test Student",
                "test.student@example.com",
                "9876543210"
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
