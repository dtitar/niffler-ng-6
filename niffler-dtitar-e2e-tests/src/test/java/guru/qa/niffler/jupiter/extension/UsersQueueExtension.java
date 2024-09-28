package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome) {
    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUESTS_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUESTS_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("eric", "123123ee", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("stan", "123123ee", "kyle", null, null));
        WITH_INCOME_REQUESTS_USERS.add(new StaticUser("kyle", "123123ee", null, "kenny", null));
        WITH_OUTCOME_REQUESTS_USERS.add(new StaticUser("kenny", "123123ee", null, null, "butters"));
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;

        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }

    }


    @Override
    @SuppressWarnings("unchecked")
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        Arrays.stream(context.getRequiredTestMethod()
                             .getParameters())
              .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
              .map(p -> p.getAnnotation(UserType.class))
              .forEach(ut -> {
                  Optional<StaticUser> user = Optional.empty();
                  StopWatch sw = StopWatch.createStarted();
                  while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                      user = Optional.ofNullable(getQueueForUserType(ut.value()).poll());
                  }
                  Allure.getLifecycle()
                        .updateTestCase(testCase -> {
                            testCase.setStart(new Date().getTime());
                        });
                  user.ifPresentOrElse(
                          u ->
                                  ((Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                                                                      .getOrComputeIfAbsent(
                                                                              context.getUniqueId(),
                                                                              key -> new HashMap<>()
                                                                      )).put(ut, u),
                          () -> {
                              throw new IllegalStateException("Can`t obtain user after 30s.");
                          }
                  );
              });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Map<UserType, StaticUser> map = context.getStore(NAMESPACE)
                                               .get(context.getUniqueId(), Map.class);
        for (Map.Entry<UserType, StaticUser> e : map.entrySet()) {
            getQueueForUserType(e.getKey()
                                 .value()).add(e.getValue());
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                               .getType()
                               .isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return (StaticUser) extensionContext.getStore(NAMESPACE)
                                            .get(extensionContext.getUniqueId(), Map.class)
                                            .get(AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class)
                                                                  .get());
    }

    private Queue<StaticUser> getQueueForUserType(UserType.Type type) {
        return switch (type) {
            case EMPTY -> EMPTY_USERS;
            case WITH_FRIEND -> WITH_FRIEND_USERS;
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUESTS_USERS;
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUESTS_USERS;
        };
    }
}