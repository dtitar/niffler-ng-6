package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class UsersDbClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    //<editor-fold desc="Spring JDBC">
    public UserJson createUserSpringJdbc(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                                                 AuthUserEntity authUser = new AuthUserEntity();
                                                 authUser.setUsername(user.username());
                                                 authUser.setPassword(pe.encode("123123ee"));
                                                 authUser.setEnabled(true);
                                                 authUser.setAccountNonExpired(true);
                                                 authUser.setAccountNonLocked(true);
                                                 authUser.setCredentialsNonExpired(true);

                                                 AuthUserEntity createdAuthUser = authUserDao.create(authUser);
                                                 AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values())
                                                                                             .map(a -> {
                                                                                                      AuthorityEntity ae = new AuthorityEntity();
                                                                                                      ae.setUserId(createdAuthUser.getId());
                                                                                                      ae.setAuthority(a);
                                                                                                      return ae;
                                                                                                  }
                                                                                             )
                                                                                             .toArray(AuthorityEntity[]::new);
                                                 authAuthorityDao.create(authorityEntities);
                                                 return UserJson.fromEntity(
                                                         udUserDao.create(UserEntity.fromJson(user)),
                                                         null
                                                 );
                                             }
        );
    }

    public List<AuthorityEntity> findAllAuthoritiesSpringJdbc() {
        return authAuthorityDao.findAll();
    }

    public List<AuthUserEntity> findAllAuthUsersSpringJdbc() {
        return authUserDao.findAll();
    }

    public List<UserEntity> findAllUdUsersSpringJdbc() {
        return udUserDao.findAll();
    }
    //</editor-fold>

    //<editor-fold desc="JDBC">
    public UserJson createUser(UserJson user, String userPassword) {
        return UserJson.fromEntity(
                xaTransaction(
                        new Databases.XaFunction<>(
                                con -> {
                                    AuthUserEntity authUser = new AuthUserEntity();
                                    authUser.setUsername(user.username());
                                    authUser.setPassword(pe.encode(userPassword));
                                    authUser.setEnabled(true);
                                    authUser.setAccountNonExpired(true);
                                    authUser.setAccountNonLocked(true);
                                    authUser.setCredentialsNonExpired(true);

                                    new AuthUserDaoJdbc().create(authUser);
                                    new AuthAuthorityDaoJdbc().create(
                                            Arrays.stream(Authority.values())
                                                  .map(a -> {
                                                           AuthorityEntity ae = new AuthorityEntity();
                                                           ae.setUserId(authUser.getId());
                                                           ae.setAuthority(a);
                                                           return ae;
                                                       }
                                                  )
                                                  .toArray(AuthorityEntity[]::new));
                                    return null;
                                },
                                CFG.authJdbcUrl()
                        ),
                        new Databases.XaFunction<>(
                                con -> {
                                    UserEntity ue = new UserEntity();
                                    ue.setUsername(user.username());
                                    ue.setFullName(user.fullname());
                                    ue.setCurrency(user.currency());
                                    new UdUserDaoJdbc().create(ue);
                                    return ue;
                                },
                                CFG.userdataJdbcUrl()
                        )
                ),
                null);
    }

    public UserJson findById(UUID id) {
        return transaction(connection -> {
            return new UdUserDaoJdbc().findById(id)
                                      .map(u -> UserJson.fromEntity(u, null))
                                      .orElseThrow(() -> new RuntimeException(String.format("User with provided id '%s' not found", id.toString())));
        }, CFG.spendJdbcUrl());
    }

    public UserJson findByUsername(String username) {
        return transaction(connection -> {
            return new UdUserDaoJdbc().findByUsername(username)
                                      .map(u -> UserJson.fromEntity(u, null))
                                      .orElseThrow(() -> new RuntimeException(String.format("User with provided username '%s' not found", username)));
        }, CFG.spendJdbcUrl());
    }

    public List<AuthorityEntity> findAllAuthorities() {
        return transaction(connection -> {
            return new AuthAuthorityDaoJdbc().findAll();
        }, CFG.authJdbcUrl());
    }

    public List<AuthUserEntity> findAllAuthUsers() {
        return transaction(connection -> {
            return new AuthUserDaoJdbc().findAll();
        }, CFG.authJdbcUrl());
    }

    public List<UserJson> findAllUdUsers() {
        return transaction(connection -> {
            return new UdUserDaoJdbc().findAll()
                                      .stream()
                                      .map(u -> UserJson.fromEntity(u, null))
                                      .toList();
        }, CFG.userdataJdbcUrl());
    }
    //</editor-fold>
}