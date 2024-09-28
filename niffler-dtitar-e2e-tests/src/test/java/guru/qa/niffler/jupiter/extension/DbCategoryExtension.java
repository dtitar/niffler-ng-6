package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.jupiter.annotation.DbCategory;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.UUID;

public class DbCategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DbCategoryExtension.class);
    private static final Faker faker = new Faker();
    private final SpendDbClient spendDbClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), DbCategory.class)
                         .ifPresent(annotation -> {
                             CategoryJson createdCategory = spendDbClient.createCategory(new CategoryJson(
                                     UUID.randomUUID(),
                                     annotation.name()
                                               .isBlank() ? faker.country()
                                                                 .name() : annotation.name(),
                                     annotation.username(),
                                     false));
                             if (annotation.archived()) {
                                 CategoryJson archivedCategory = new CategoryJson(
                                         createdCategory.id(),
                                         createdCategory.name(),
                                         createdCategory.username(),
                                         true);
                                 createdCategory = spendDbClient.updateCategory(archivedCategory);
                             }
                             context.getStore(NAMESPACE)
                                    .put(context.getUniqueId(), createdCategory);
                         });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                               .getType()
                               .isAssignableFrom(CategoryJson.class);
    }

    @Override
    public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE)
                               .get(extensionContext.getUniqueId(), CategoryJson.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        CategoryJson createdCategory = context.getStore(NAMESPACE)
                                              .get(context.getUniqueId(), CategoryJson.class);
        spendDbClient.updateCategory(new CategoryJson(
                createdCategory.id(),
                createdCategory.name(),
                createdCategory.username(),
                true));
    }
}
