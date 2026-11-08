package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class BookingFormPage {

Page page;

    private static final String FULL_NAME_LABEL = "#event-title-input";
    private static final String CUSTOMER_EMAIL_ID = "#admin-event-form textarea";
    private static final String PHONE_PLACEHOLDER = "City";

    public BookingFormPage(Page page){
        this.page = page;

    }

    public void fillAndConfirm(String fullName, String email, String phone){

        page.getByLabel(FULL_NAME_LABEL).fill(fullName);
        page.locator(CUSTOMER_EMAIL_ID).fill(email);
        page.getByPlaceholder(PHONE_PLACEHOLDER).fill(phone);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm Booking")).click();
        assertThat(page.getByText("Your tickets are reserved")).isVisible();
    }
}
