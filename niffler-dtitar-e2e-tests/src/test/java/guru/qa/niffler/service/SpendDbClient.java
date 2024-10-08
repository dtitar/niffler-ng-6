package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {
    private static final Config CFG = Config.getInstance();

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
}