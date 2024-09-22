package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.enums.NifflerUser;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest extends BaseTest {
    private LoginPage loginPage;
    private static final NifflerUser eric = NifflerUser.ERIC;

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(eric.getUsername(), eric.getPassword())
                .checkThatPageLoaded();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.login(faker.name()
                             .username(), "BAD");
        loginPage.checkError("Bad credentials");
    }
}
