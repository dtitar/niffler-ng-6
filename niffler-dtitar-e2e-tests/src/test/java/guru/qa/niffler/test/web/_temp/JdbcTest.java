package guru.qa.niffler.test.web._temp;

import com.github.javafaker.Faker;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.enums.NifflerUser;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

public class JdbcTest {

    protected static final Faker faker = new Faker();

    @Test
    void createSpendTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spend = spendDbClient.createSpend(new SpendJson(
                null,
                new Date(),
                new CategoryJson(null,
                                 faker.country()
                                      .name(),
                                 NifflerUser.ERIC.getUsername(),
                                 false),
                CurrencyValues.RUB,
                100.0,
                "Holiday",
                NifflerUser.ERIC.getUsername()
        ));
        System.out.println(spend);

    }

    @Test
    void findSpendByIdTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        System.out.println(spendDbClient.findSpendById(UUID.fromString("e482e1bc-7226-49ff-aff5-271c271a96a5")));
    }

    @Test
    void findAllSpendsByUsernameTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        spendDbClient.findAllByUsername(NifflerUser.ERIC.getUsername())
                     .forEach(System.out::println);
    }

    @Test
    void deleteSpendTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        SpendJson spend = spendDbClient.createSpend(new SpendJson(
                null,
                new Date(),
                spendDbClient.findCategoryByUsernameAndCategoryName(NifflerUser.ERIC.getUsername(), "food"),
                CurrencyValues.RUB,
                100.0,
                "Burgers",
                NifflerUser.ERIC.getUsername()
        ));
        System.out.println(spend);
        int deletedRows = spendDbClient.deleteSpend(SpendEntity.fromJson(spend));
        System.out.println(deletedRows);
    }

    @Test
    void findAllCategoriesByUsernameTest() {
        SpendDbClient spendDbClient = new SpendDbClient();
        spendDbClient.findAllCategoriesByUsername(NifflerUser.ERIC.getUsername())
                     .forEach(System.out::println);
    }

    @Test
    void deleteCategoryTest() throws InterruptedException {
        SpendDbClient spendDbClient = new SpendDbClient();
        CategoryJson createdCategory = spendDbClient.createCategory(new CategoryJson(
                null,
                faker.country()
                     .name(),
                NifflerUser.ERIC.getUsername(),
                false
        ));
        System.out.println(createdCategory);
        int deletedRows = spendDbClient.deleteCategory(CategoryEntity.fromJson(createdCategory));
        System.out.println(deletedRows);
    }
}
