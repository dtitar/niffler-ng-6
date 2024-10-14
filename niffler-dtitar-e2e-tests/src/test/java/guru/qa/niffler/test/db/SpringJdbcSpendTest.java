package guru.qa.niffler.test.db;

import com.github.javafaker.Faker;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.enums.NifflerUser;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

public class SpringJdbcSpendTest {

    protected static final Faker faker = new Faker();
    private final SpendDbClient spendDbClient = new SpendDbClient();

    @Test
    void createSpendSpringJdbcTest() {
        SpendJson spend = spendDbClient.createSpendSpringJdbc(new SpendJson(
                null,
                new Date(),
                new CategoryJson(null,
                                 "category-spring-jdbc-3",
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
        System.out.println(spendDbClient.findSpendByIdSpringJdbc(UUID.fromString("e482e1bc-7226-49ff-aff5-271c271a96a5")));
    }

    @Test
    void findAllSpendsByUsernameTest() {
        spendDbClient.findAllByUsernameSpringJdbc(NifflerUser.ERIC.getUsername())
                     .forEach(System.out::println);
    }

    @Test
    void deleteSpendTest() {
        SpendJson spend = spendDbClient.createSpendSpringJdbc(new SpendJson(
                null,
                new Date(),
                new CategoryJson(null,
                                 "category-spring-jdbc-5",
                                 NifflerUser.ERIC.getUsername(),
                                 false),
                CurrencyValues.RUB,
                100.0,
                "Holiday",
                NifflerUser.ERIC.getUsername()
        ));
        System.out.println(spend);
        int deletedRows = spendDbClient.deleteSpendSpringJdbc(SpendEntity.fromJson(spend));
        System.out.println(deletedRows);
    }

    @Test
    void deleteCategoryTest() {
        CategoryJson createdCategory = spendDbClient.createCategorySpringJdbc(new CategoryJson(
                null,
                faker.country()
                     .name(),
                NifflerUser.ERIC.getUsername(),
                false
        ));
        System.out.println(createdCategory);
        int deletedRows = spendDbClient.deleteCategorySpringJdbc(createdCategory);
        System.out.println(deletedRows);
    }

    @Test
    void updateCategoryTest() {
        CategoryJson createdCategory = spendDbClient.createCategorySpringJdbc(new CategoryJson(
                null,
                faker.country()
                     .name(),
                NifflerUser.ERIC.getUsername(),
                false
        ));
        System.out.println(createdCategory);
        CategoryJson updatedCategory = spendDbClient.updateCategory(new CategoryJson(
                createdCategory.id(),
                createdCategory.name(),
                createdCategory.username(),
                true
        ));
        System.out.println(updatedCategory);
    }

    @Test
    void findAllCategoriesSpringJdbcTest() {
        spendDbClient.findAllCategoriesSpringJdbc()
                     .forEach(System.out::println);
    }

    @Test
    void findAllSpendsSpringJdbcTest() {
        spendDbClient.findAllSpendsSpringJdbc()
                     .forEach(System.out::println);
    }
}
