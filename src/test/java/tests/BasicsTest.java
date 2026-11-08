package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BasicsTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeMethod
    public void setUp(){
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
        //Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
        //Browser browser = playwright.chromium().launch();
        page = browser.newPage();
        page.setDefaultTimeout(5000);
        page.navigate("https://eventhub.rahulshettyacademy.com/login");
        PlaywrightAssertions.setDefaultAssertionTimeout(7000);
    }

    @Test(description = "create event - book that event and verify the booking")
    public void DemoTest(){
        //assertThat(page).hasTitle("EventHub — Discover & Book Events");
        //page.getByLabel("Email").fill("chinmay.trivedi@gmail.com");
        page.getByPlaceholder("you@email.com").fill("chinmaytrivedi2000@yahoo.co.in");
        page.getByLabel("Password").fill("Admin@1702");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();

        assertThat(page.getByRole(AriaRole.LINK,new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

        page.navigate("https://eventhub.rahulshettyacademy.com/admin/events");
        page.locator("#event-title-input").fill("QA Summit Chinmay", new Locator.FillOptions().setTimeout(10000));
        page.locator("#admin-event-form textarea").fill("Test content filling");
        page.getByLabel("Category").selectOption("Concert");
        page.getByLabel("City").fill("Test City");
        page.getByLabel("Venue").fill("Test Vnu");
        page.getByLabel("Event Date & Time").fill("2026-12-18T07:25");
        page.getByLabel("Price ($)").fill("100");
        page.getByLabel("Total Seats").fill("50");
        //page.waitForTimeout(3000);
        page.locator("#add-event-btn").click(new Locator.ClickOptions().setTimeout(12000));
        assertThat(page.getByText("Event created!")).isVisible();

        // find newly created event on the events page
        page.locator("#nav-events").click();
        Locator eventCards = page.getByTestId("event-card");
        System.out.println(eventCards.count());

        Locator targetCard = eventCards.filter(new Locator.FilterOptions().setHasText("QA Summit Chinmay"));
        //assertThat(targetCard).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
        assertThat(targetCard).isVisible();
        String seatsText = targetCard.getByText("seats").innerText();
        System.out.println(seatsText);
        int seatsNumBeforeBooking =  Integer.parseInt(seatsText.split(" ")[0]);
        targetCard.getByTestId("book-now-btn").click();

        //Book the ticket for event
        page.getByLabel("Full Name").fill("Test Student");
        page.locator("#customer-email").fill("test.student@examp.com");
        page.getByPlaceholder("+91 98765 43210").fill("9800098000");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm Booking")).click();
        assertThat(page.getByText("Your tickets are reserved")).isVisible();

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
