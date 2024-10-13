package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private final DataSource dataSource;

    public CategoryDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CategoryEntity create(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO category (username, name, archived) " +
                            "VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys()
                                           .get("id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject("SELECT * FROM category WHERE id = ?",
                                            CategoryEntityRowMapper.instance,
                                            id)
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM category WHERE username = ? AND name = ?",
                                                               CategoryEntityRowMapper.instance, username, categoryName));
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query("SELECT * FROM category WHERE username = ?",
                                  CategoryEntityRowMapper.instance, username);
    }

    @Override
    public int deleteCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.update("DELETE FROM category WHERE id = ?", category.getId());
    }

    @Override
    public Optional<CategoryEntity> updateCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update("UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?",
                            category.getName(), category.getUsername(), category.isArchived(), category.getId());
        return Optional.of(category);
    }
}