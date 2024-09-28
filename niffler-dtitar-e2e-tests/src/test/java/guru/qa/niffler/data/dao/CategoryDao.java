package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.util.Optional;

public interface CategoryDao {
    CategoryEntity create(CategoryEntity entity);

    Optional<CategoryEntity> findCategoryById(String id);
}