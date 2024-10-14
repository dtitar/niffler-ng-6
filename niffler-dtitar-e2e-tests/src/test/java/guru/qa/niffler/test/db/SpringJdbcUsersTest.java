package guru.qa.niffler.test.db;

import com.github.javafaker.Faker;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

public class SpringJdbcUsersTest {

    protected static final Faker faker = new Faker();
    private final UsersDbClient usersDbClient = new UsersDbClient();

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
