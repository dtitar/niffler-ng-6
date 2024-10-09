package guru.qa.niffler.test.db;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

public class CreateUserDbTest {

    @Test
    void createWithTransaction() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson createdUser = usersDbClient.createUser(new UserJson(
                null,
                "created2",
                CurrencyValues.KZT,
                null,
                null,
                null,
                null,
                null
        ), "123123ee");
    }
}
