package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.dataSource;
import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {
    private static final Config CFG = Config.getInstance();

    //<editor-fold desc="JDBC">
    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
                               SpendEntity spendEntity = SpendEntity.fromJson(spend);
                               if (spendEntity.getCategory()
                                              .getId() == null) {
                                   CategoryEntity categoryEntity = new CategoryDaoJdbc(connection).create(spendEntity.getCategory());
                                   spendEntity.setCategory(categoryEntity);
                               }
                               return SpendJson.fromEntity(new SpendDaoJdbc(connection).create(spendEntity));
                           },
                           CFG.spendJdbcUrl());
    }

    public SpendJson findSpendById(UUID id) {
        return transaction(connection -> {
            return SpendJson.fromEntity(new SpendDaoJdbc(connection).findSpendById(id)
                                                                    .orElseThrow(() -> new RuntimeException("Spend not found")));
        }, CFG.spendJdbcUrl());
    }

    public List<SpendJson> findAllByUsername(String username) {
        return transaction(connection -> {
            return new SpendDaoJdbc(connection).findAllByUsername(username)
                                               .stream()
                                               .map(SpendJson::fromEntity)
                                               .toList();
        }, CFG.spendJdbcUrl());
    }

    public int deleteSpend(SpendEntity spend) {
        return transaction(connection -> {
            return new SpendDaoJdbc(connection).deleteSpend(spend);
        }, CFG.spendJdbcUrl());
    }

    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).create(CategoryEntity.fromJson(category)));
        }, CFG.spendJdbcUrl());
    }

    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return transaction(connection -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).findCategoryByUsernameAndCategoryName(username, categoryName)
                                                                          .orElseThrow(() -> new RuntimeException("Category not found")));
        }, CFG.spendJdbcUrl());
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return transaction(connection -> {
            return new CategoryDaoJdbc(connection).findAllByUsername(username)
                                                  .stream()
                                                  .map(CategoryJson::fromEntity)
                                                  .toList();
        }, CFG.spendJdbcUrl());
    }

    public int deleteCategory(CategoryJson category) {
        return transaction(connection -> {
            return new CategoryDaoJdbc(connection).deleteCategory(CategoryEntity.fromJson(category));
        }, CFG.spendJdbcUrl());
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return transaction(connection -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).updateCategory(CategoryEntity.fromJson(category))
                                                                          .orElseThrow(() -> new RuntimeException("Category not found")));
        }, CFG.spendJdbcUrl());
    }

    public List<CategoryEntity> findAllCategories() {
        return transaction(connection -> {
            return new CategoryDaoJdbc(connection).findAll();
        }, CFG.spendJdbcUrl());
    }

    public List<SpendEntity> findAllSpends() {
        return transaction(connection -> {
            return new SpendDaoJdbc(connection).findAll();
        }, CFG.spendJdbcUrl());
    }
    //</editor-fold>


    //<editor-fold desc="Spring JDBC">
    public SpendJson createSpendSpringJdbc(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory()
                       .getId() == null) {
            CategoryEntity categoryEntity = new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).create(spendEntity));
    }

    public SpendJson findSpendByIdSpringJdbc(UUID id) {
        return SpendJson.fromEntity(new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findSpendById(id)
                                                                                          .orElseThrow(() -> new RuntimeException("Spend not found")));
    }

    public List<SpendJson> findAllByUsernameSpringJdbc(String username) {
        return new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findAllByUsername(username)
                                                                     .stream()
                                                                     .map(SpendJson::fromEntity)
                                                                     .toList();
    }

    public int deleteSpendSpringJdbc(SpendEntity spend) {
        return new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).deleteSpend(spend);
    }

    public CategoryJson createCategorySpringJdbc(CategoryJson category) {
        return CategoryJson.fromEntity(new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).create(CategoryEntity.fromJson(category)));
    }

    public CategoryJson findCategoryByUsernameAndCategoryNameSpringJdbc(String username, String categoryName) {
        return CategoryJson.fromEntity(new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findCategoryByUsernameAndCategoryName(username, categoryName)
                                                                                                .orElseThrow(() -> new RuntimeException("Category not found")));
    }

    public List<CategoryJson> findAllCategoriesByUsernameSpringJdbc(String username) {
        return new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findAllByUsername(username)
                                                                        .stream()
                                                                        .map(CategoryJson::fromEntity)
                                                                        .toList();
    }

    public int deleteCategorySpringJdbc(CategoryJson category) {
        return new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).deleteCategory(CategoryEntity.fromJson(category));
    }

    public CategoryJson updateCategorySpringJdbc(CategoryJson category) {
        return CategoryJson.fromEntity(new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).updateCategory(CategoryEntity.fromJson(category))
                                                                                                .orElseThrow(() -> new RuntimeException("Category not found")));
    }

    public List<CategoryEntity> findAllCategoriesSpringJdbc() {
        return new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findAll();
    }

    public List<SpendEntity> findAllSpendsSpringJdbc() {
        return new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findAll();
    }
    //</editor-fold>
}