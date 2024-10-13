package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {

    private final Connection connection;

    public AuthUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            ps.executeUpdate();
            final UUID generatedUserId;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedUserId = rs.getObject("id", UUID.class);
                } else {
                    throw new IllegalStateException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedUserId);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM \"user\" WHERE id = ?")) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity ae = new AuthUserEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    ae.setUsername(rs.getString("username"));
                    ae.setPassword(rs.getString("password"));
                    ae.setEnabled(rs.getBoolean("enabled"));
                    ae.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    ae.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    ae.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(ae);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                List<AuthUserEntity> authUsers = new ArrayList<>();
                while (rs.next()) {
                    AuthUserEntity ae = new AuthUserEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    ae.setUsername(rs.getString("username"));
                    ae.setPassword(rs.getString("password"));
                    ae.setEnabled(rs.getBoolean("enabled"));
                    ae.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    ae.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    ae.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    authUsers.add(ae);
                }
                return authUsers;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
