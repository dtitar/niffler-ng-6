package guru.qa.niffler.test.web;

import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;

@WebTest
public abstract class BaseTest {

    protected static final String DEFAULT_PASSWORD = "123123ee";
    protected static final Config CFG = Config.getInstance();
    protected static final Faker faker = new Faker();
}
