package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection()
                                                                 .prepareStatement(
                                                                         "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                                                                                 "VALUES (?, ?, ?, ?, ?, ?)",
                                                                         PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection()
                                                                      .prepareStatement(
                                                                              "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)")) {
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getPassword());
            userPs.setBoolean(3, user.getEnabled());
            userPs.setBoolean(4, user.getAccountNonExpired());
            userPs.setBoolean(5, user.getAccountNonLocked());
            userPs.setBoolean(6, user.getCredentialsNonExpired());
            userPs.executeUpdate();
            final UUID generatedUserId;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedUserId = rs.getObject("id", UUID.class);
                } else {
                    throw new IllegalStateException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedUserId);

            for (AuthorityEntity a : user.getAuthorities()) {
                authorityPs.setObject(1, generatedUserId);
                authorityPs.setString(2, a.getAuthority()
                                          .name());
                authorityPs.addBatch();
                authorityPs.clearParameters();
            }
            authorityPs.executeBatch();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection()
                                                             .prepareStatement("SELECT * FROM \"user\" u JOIN authority a ON u.id = a.user_id WHERE u.id = ?")) {
            ps.setObject(1, id);
            ps.execute();

            AuthUserEntity user = null;
            List<AuthorityEntity> authorities = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.INSTANCE.mapRow(rs, 1);
                    }

                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setId(rs.getObject("a.id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorities.add(ae);


                    AuthUserEntity aue = new AuthUserEntity();
                    aue.setId(rs.getObject("id", UUID.class));
                    aue.setUsername(rs.getString("username"));
                    aue.setPassword(rs.getString("password"));
                    aue.setEnabled(rs.getBoolean("enabled"));
                    aue.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    aue.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    aue.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));

                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(authorities);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection()
                                                             .prepareStatement(
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
