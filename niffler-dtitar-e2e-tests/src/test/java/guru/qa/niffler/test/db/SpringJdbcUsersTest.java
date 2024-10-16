package guru.qa.niffler.test.db;

import com.github.javafaker.Faker;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

public class SpringJdbcUsersTest {

    protected static final Faker faker = new Faker();
    private final UsersDbClient usersDbClient = new UsersDbClient();

    @Test
    void createUserChainedTransactionManagerTest() {
        usersDbClient.createUserChainedTransactionManager(new UserJson(
                null,
                "randy" + faker.number()
                                  .randomNumber(),
                CurrencyValues.KZT,
                faker.name()
                     .firstName(),
                faker.name()
                     .lastName(),
                faker.name()
                     .fullName(),
                null,
                null,
                null
        ));
    }

    @Test
    void createUserTest() {
        usersDbClient.createUserSpringJdbc(new UserJson(
                null,
                "randy-spring-jdbc-transaction-3",
                CurrencyValues.KZT,
                faker.name()
                     .firstName(),
                faker.name()
                     .lastName(),
                faker.name()
                     .fullName(),
                null,
                null,
                null
        ));
    }

    @Test
    void findAllAuthoritiesSpringJdbcTest() {
        usersDbClient.findAllAuthoritiesSpringJdbc()
                     .forEach(System.out::println);
    }

    @Test
    void findAllAuthUsersSpringJdbcTest() {
        usersDbClient.findAllAuthUsersSpringJdbc()
                     .forEach(System.out::println);
    }

    @Test
    void findAllUsersSpringJdbcTest() {
        usersDbClient.findAllUdUsersSpringJdbc()
                     .forEach(System.out::println);
    }
}
