package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.UUID;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private static final Faker faker = new Faker();


    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                         .ifPresent(annotation -> {
                             CategoryJson createdCategory = spendApiClient.createCategory(new CategoryJson(
                                     UUID.randomUUID(),
                                     annotation.name().isBlank() ? faker.country().name() : annotation.name(),
                                     annotation.username(),
                                     false));
                             if (annotation.archived()) {
                                 CategoryJson archivedCategory = new CategoryJson(
                                         createdCategory.id(),
                                         createdCategory.name(),
                                         createdCategory.username(),
                                         true);
                                 createdCategory = spendApiClient.updateCategory(archivedCategory);
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
        spendApiClient.updateCategory(new CategoryJson(
                createdCategory.id(),
                createdCategory.name(),
                createdCategory.username(),
                true));
    }
}
