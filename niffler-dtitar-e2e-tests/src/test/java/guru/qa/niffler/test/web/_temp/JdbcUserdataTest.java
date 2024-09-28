package guru.qa.niffler.test.web._temp;

import com.github.javafaker.Faker;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserdataDbClient;
import org.junit.jupiter.api.Test;

public class JdbcUserdataTest {

    protected static final Faker faker = new Faker();
    private final UserdataDbClient userdataDbClient = new UserdataDbClient();

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
                null,
                null,
                faker.name()
                     .fullName()
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

    @Test
    void deleteUserTest() {
        UserJson createdUser = userdataDbClient.createUser(new UserJson(
                null,
                faker.name()
                     .username(),
                CurrencyValues.KZT,
                faker.name()
                     .firstName(),
                faker.name()
                     .lastName(),
                null,
                null,
                faker.name()
                     .fullName()
        ));
        System.out.println(createdUser);
        userdataDbClient.deleteUser(createdUser);
    }
}