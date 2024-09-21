package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.enums.NifflerUser;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class RegistrationTest extends BaseTest {
    private LoginPage loginPage;
    private static final NifflerUser eric = NifflerUser.ERIC;

    @Test
    void shouldRegisterNewUser() {
        String newUsername = faker.name()
                                  .username();
        String password = DEFAULT_PASSWORD;
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doRegister()
                .fillRegisterPage(newUsername, password, password)
                .successSubmit()
                .successLogin(newUsername, password)
                .checkThatPageLoaded();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.doRegister()
                 .fillRegisterPage(eric.getUsername(), eric.getPassword(), eric.getPassword())
                 .submit();
        loginPage.checkError("Username `" + eric.getUsername() + "` already exists");
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String newUsername = faker.name()
                                  .username();

        loginPage = Selenide.open(CFG.frontUrl(), LoginPage.class);
        loginPage.doRegister()
                 .fillRegisterPage(newUsername, eric.getPassword(), "bad password submit")
                 .submit();
        loginPage.checkError("Passwords should be equal");
    }
}
