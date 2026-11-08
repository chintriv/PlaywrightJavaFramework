package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class LoginPage {
    Page page;
    String base_url;
    private static final String email_placeholder = "you@email.com";
    private static final String password_label = "Password";

    public LoginPage(Page Page, String baseUrl){
        this.page = page;
        this.base_url = baseUrl;

    }

    public DashboardPage loginToApplication(){
        page.navigate(base_url);

        page.getByPlaceholder(email_placeholder).fill("chinmaytrivedi2000@yahoo.co.in");
        page.getByLabel(password_label).fill("Admin@1702");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In")).click();
        DashboardPage dashboardPage = new DashboardPage(page);
        return dashboardPage;
    }

}
