package guru.qa.niffler.test.web._temp;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.enums.NifflerUser;
import guru.qa.niffler.jupiter.annotation.DbCategory;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.test.web.BaseTest;
import org.junit.jupiter.api.Test;

public class DbCategoryExtensionTest extends BaseTest {
    private static final NifflerUser kyle = NifflerUser.KYLE;

    @DbCategory(
            username = "kyle",
            archived = true
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(kyle.getUsername(), kyle.getPassword())
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkArchivedCategoryExists(category.name());
    }

    @DbCategory(
            username = "kyle",
            archived = false
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(kyle.getUsername(), kyle.getPassword())
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkCategoryExists(category.name());
    }
}
