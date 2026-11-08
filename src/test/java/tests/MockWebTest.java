package tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.microsoft.playwright.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MockWebTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeMethod
    public void setUp() {
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

    @Test(description = "sandbox banner is shown when 6 events are registered")
    public void DemoTest() {

        page.getByPlaceholder("you@email.com").fill("chinmaytrivedi2000@yahoo.co.in");
        page.getByLabel("Password").fill("Admin@1702");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        // System.out.println("login done");
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();
        //page.route("**/api/events**", route->route.fulfill(
        //new Route.FulfillOptions().setPath(Paths.get("src/test/resources/events_6.json"))));
        page.route("**/api/events**", route -> route.fulfill(
                new Route.FulfillOptions().setPath(Paths.get("src/test/resources/events_4.json"))));
        page.navigate("https://eventhub.rahulshettyacademy.com/events");
        //page.waitForTimeout(7000);

        Locator eventCards = page.getByTestId("event-card");
        assertThat(eventCards.first()).isVisible();
        //Assert.assertEquals(eventCards.count(), 6);
        Assert.assertEquals(eventCards.count(), 4);

        //Assert.assertTrue(page.locator(".mx-1").isVisible());
        assertThat(page.locator(".mx-1").first()).isHidden();
    }

        @Test
        public void routeResumeTest()
    {
            // route.resume
            page.getByPlaceholder("you@email.com").fill("chinmaytrivedi2000@yahoo.co.in");
            page.getByLabel("Password").fill("Admin@1702");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
            // System.out.println("login done");
            assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Browse Events →"))).isVisible();

            page.getByTestId("nav-bookings").click();
            page.route("**/api/bookings**", route -> route.resume(
                    new Route.ResumeOptions().setUrl("\n" + "https://api.eventhub.rahulshettyacademy.com/api/bookings/72116")
            ));
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("View Details")).first().click();
            assertThat(page.getByText("Access Denied")).isVisible();
        }
    }


