package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {
    private static final Config CFG = Config.getInstance();

    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final SpendDaoJdbc spendDao = new SpendDaoJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    //<editor-fold desc="Spring JDBC">
    public SpendJson createSpendSpringJdbc(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory()
                       .getId() == null) {
            CategoryEntity categoryEntity = new CategoryDaoSpringJdbc().create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(new SpendDaoSpringJdbc().create(spendEntity));
    }

    public SpendJson findSpendByIdSpringJdbc(UUID id) {
        return SpendJson.fromEntity(new SpendDaoSpringJdbc().findSpendById(id)
                                                            .orElseThrow(() -> new RuntimeException("Spend not found")));
    }

    public List<SpendJson> findAllByUsernameSpringJdbc(String username) {
        return new SpendDaoSpringJdbc().findAllByUsername(username)
                                       .stream()
                                       .map(SpendJson::fromEntity)
                                       .toList();
    }

    public int deleteSpendSpringJdbc(SpendEntity spend) {
        return new SpendDaoSpringJdbc().deleteSpend(spend);
    }

    public CategoryJson createCategorySpringJdbc(CategoryJson category) {
        return CategoryJson.fromEntity(new CategoryDaoSpringJdbc().create(CategoryEntity.fromJson(category)));
    }

    public CategoryJson findCategoryByUsernameAndCategoryNameSpringJdbc(String username, String categoryName) {
        return CategoryJson.fromEntity(new CategoryDaoSpringJdbc().findCategoryByUsernameAndCategoryName(username, categoryName)
                                                                  .orElseThrow(() -> new RuntimeException("Category not found")));
    }

    public List<CategoryJson> findAllCategoriesByUsernameSpringJdbc(String username) {
        return new CategoryDaoSpringJdbc().findAllByUsername(username)
                                          .stream()
                                          .map(CategoryJson::fromEntity)
                                          .toList();
    }

    public int deleteCategorySpringJdbc(CategoryJson category) {
        return new CategoryDaoSpringJdbc().deleteCategory(CategoryEntity.fromJson(category));
    }

    public CategoryJson updateCategorySpringJdbc(CategoryJson category) {
        return CategoryJson.fromEntity(new CategoryDaoSpringJdbc().updateCategory(CategoryEntity.fromJson(category))
                                                                  .orElseThrow(() -> new RuntimeException("Category not found")));
    }

    public List<CategoryEntity> findAllCategoriesSpringJdbc() {
        return new CategoryDaoSpringJdbc().findAll();
    }

    public List<SpendEntity> findAllSpendsSpringJdbc() {
        return new SpendDaoSpringJdbc().findAll();
    }
    //</editor-fold>

    //<editor-fold desc="JDBC">
    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
                                          SpendEntity spendEntity = SpendEntity.fromJson(spend);
                                          if (spendEntity.getCategory()
                                                         .getId() == null) {
                                              CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
                                              spendEntity.setCategory(categoryEntity);
                                          }
                                          return SpendJson.fromEntity(spendDao.create(spendEntity));
                                      }
        );
    }

    public SpendJson findSpendById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
                                          return SpendJson.fromEntity(spendDao.findSpendById(id)
                                                                              .orElseThrow(() -> new RuntimeException("Spend not found")));
                                      }
        );
    }

    public List<SpendJson> findAllByUsername(String username) {
        return jdbcTxTemplate.execute(() -> {
            return new SpendDaoJdbc().findAllByUsername(username)
                                     .stream()
                                     .map(SpendJson::fromEntity)
                                     .toList();
        });
    }

    public int deleteSpend(SpendEntity spend) {
        return transaction(connection -> {
            return new SpendDaoJdbc().deleteSpend(spend);
        }, CFG.spendJdbcUrl());
    }

    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc().create(CategoryEntity.fromJson(category)));
        });
    }

    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return jdbcTxTemplate.execute(() -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc().findCategoryByUsernameAndCategoryName(username, categoryName)
                                                                .orElseThrow(() -> new RuntimeException("Category not found")));
        });
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return jdbcTxTemplate.execute(() -> {
            return new CategoryDaoJdbc().findAllByUsername(username)
                                        .stream()
                                        .map(CategoryJson::fromEntity)
                                        .toList();
        });
    }

    public int deleteCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
            return new CategoryDaoJdbc().deleteCategory(CategoryEntity.fromJson(category));
        });
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc().updateCategory(CategoryEntity.fromJson(category))
                                                                .orElseThrow(() -> new RuntimeException("Category not found")));
        });
    }

    public List<CategoryEntity> findAllCategories() {
        return jdbcTxTemplate.execute(() -> {
            return new CategoryDaoJdbc().findAll();
        });
    }

    public List<SpendEntity> findAllSpends() {
        return jdbcTxTemplate.execute(() -> {
            return new SpendDaoJdbc().findAll();
        });
    }
    //</editor-fold>
}