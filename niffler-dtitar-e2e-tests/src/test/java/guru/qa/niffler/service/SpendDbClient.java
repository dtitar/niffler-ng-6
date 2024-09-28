package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.List;
import java.util.UUID;

public class SpendDbClient {
    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory()
                       .getId() == null) {
            CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(spendDao.create(spendEntity));
    }

    public SpendJson findSpendById(UUID id) {
        return SpendJson.fromEntity(spendDao.findSpendById(id)
                                            .orElseThrow(() -> new RuntimeException("Spend not found")));
    }

    public List<SpendJson> findAllByUsername(String username) {
        return spendDao.findAllByUsername(username)
                       .stream()
                       .map(SpendJson::fromEntity)
                       .toList();
    }

    public int deleteSpend(SpendEntity spend) {
        return spendDao.deleteSpend(spend);
    }

    public CategoryJson createCategory(CategoryJson category) {
        return CategoryJson.fromEntity(categoryDao.create(CategoryEntity.fromJson(category)));
    }

    public CategoryJson findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return CategoryJson.fromEntity(categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName)
                                                  .orElseThrow(() -> new RuntimeException("Category not found")));
    }

    public List<CategoryJson> findAllCategoriesByUsername(String username) {
        return categoryDao.findAllByUsername(username)
                          .stream()
                          .map(CategoryJson::fromEntity)
                          .toList();
    }

    public int deleteCategory(CategoryEntity category) {
        return categoryDao.deleteCategory(category);
    }
}