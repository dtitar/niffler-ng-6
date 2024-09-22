package guru.qa.niffler.test.web;

import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;

public abstract class BaseTest {

    protected static final String DEFAULT_PASSWORD = "123123ee";
    protected static final Config CFG = Config.getInstance();
    protected static final Faker faker = new Faker();
}
