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
    void createUserWithRepository() {
        UserJson createdUser = usersDbClient.createUserRepository(new UserJson(
                null,
                faker.name()
                     .username(),
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
        System.out.println(createdUser);
    }

    @Test
    void createUserChainedTransactionManagerTest() {
        usersDbClient.createUserChainedTransactionManager(new UserJson(
                null,
                "randy" + faker.number()
                                  .randomNumber(),
                CurrencyValues.KZT.KZT,
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
    void createUseSpringJdbcWithAtomikosTransactionTest() {
        usersDbClient.createUserSpringJdbcWithAtomikosTransaction(new UserJson(
                null,
                "randy" + faker.number()
                               .randomNumber(),
                CurrencyValues.KZT.KZT,
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
    void createUseSpringJdbcWithoutTransactionTest() {
        usersDbClient.createUserSpringJdbcWithoutTransaction(new UserJson(
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
    void createUseJdbcWithTransactionTest() {
        usersDbClient.createUserJdbcWithTransaction(new UserJson(
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
    void createUseJdbcWithoutTransactionTest() {
        usersDbClient.createUserJdbcWithoutTransaction(new UserJson(
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
    void createUseRepositoryTest() {
        usersDbClient.createUserRepository(new UserJson(
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
