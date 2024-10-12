package guru.qa.niffler.test.web._temp;

import com.github.javafaker.Faker;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

public class JdbcUserdataTest {

    protected static final Faker faker = new Faker();
    private final UsersDbClient userdataDbClient = new UsersDbClient();

    @Test
    void createUserTest() {
        UserJson createdUser = userdataDbClient.createUser(new UserJson(
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
        ), "123123ee");
        System.out.println(createdUser);
    }

    @Test
    void springJdbcTest() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson createdUser = usersDbClient.createUserSpringJdbc(new UserJson(
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
        UserJson existentUser = userdataDbClient.findByUsername("eric");
        System.out.println(userdataDbClient.findById(existentUser.id()));
    }

    @Test
    void findUserByUsernameTest() {
        System.out.println(userdataDbClient.findByUsername("eric"));
    }
}