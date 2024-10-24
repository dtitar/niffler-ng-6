package guru.qa.niffler.test.db;

import com.github.javafaker.Faker;
import guru.qa.niffler.data.entity.userdata.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

public class JdbcUsersTest {

    protected static final Faker faker = new Faker();
    private final UsersDbClient usersDbClient = new UsersDbClient();

    @Test
    void createUserTest() {
        UserJson createdUser = usersDbClient.createUserJdbcWithTransaction(new UserJson(
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
    void springJdbcTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson createdUser = usersDbClient.createUserSpringJdbcWithAtomikosTransaction(new UserJson(
                null,
                "randy-2",
                CurrencyValues.USD,
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
    void findUserByIdTest() {
        UserJson existentUser = usersDbClient.findByUsername("eric");
        System.out.println(usersDbClient.findById(existentUser.id()));
    }

    @Test
    void findUserByUsernameTest() {
        System.out.println(usersDbClient.findByUsername("eric"));
    }

    @Test
    void findAllAuthoritiesTest() {
        usersDbClient.findAllAuthorities()
                     .forEach(System.out::println);
    }

    @Test
    void findAllAuthUsersTest() {
        usersDbClient.findAllAuthUsers()
                     .forEach(System.out::println);
    }

    @Test
    void findAllUdUsersTest() {
        usersDbClient.findAllUdUsers()
                     .forEach(System.out::println);
    }
}