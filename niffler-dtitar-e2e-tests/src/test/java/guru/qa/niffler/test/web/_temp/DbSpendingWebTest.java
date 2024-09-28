package guru.qa.niffler.test.web._temp;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.enums.NifflerUser;
import guru.qa.niffler.jupiter.annotation.DbSpending;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.test.web.BaseTest;
import org.junit.jupiter.api.Test;

@WebTest
public class DbSpendingWebTest extends BaseTest {
    private static final NifflerUser stan = NifflerUser.STAN;

    @DbSpending(
            username = "stan",
            category = "Обучение",
            description = "Обучение Advanced 2.0",
            amount = 79990
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTable(SpendJson spend) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(stan.getUsername(), stan.getPassword())
                .editSpending(spend.description())
                .setNewSpendingDescription(newDescription)
                .save();

        new MainPage().checkThatTableContainsSpending(newDescription);
    }
}
