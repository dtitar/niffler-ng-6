package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class UsersDbClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserDaoSpringJdbc = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDaoSpringJdbc = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDaoSpringJdbc = new UdUserDaoSpringJdbc();

    private final AuthUserDao authUserDaoJdbc = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDaoJdbc = new AuthAuthorityDaoSpringJdbc();
    private final UdUserDao udUserDaoJdbc = new UdUserDaoJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    TransactionTemplate txTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            DataSources.dataSource(CFG.authJdbcUrl())
                    ),
                    new JdbcTransactionManager(
                            DataSources.dataSource(CFG.userdataJdbcUrl())
                    )
            )
    );

    //<editor-fold desc="Spring JDBC">
    public UserJson createUserChainedTransactionManager(UserJson user) {
        return txTemplate.execute(status -> {
                                      AuthUserEntity authUser = new AuthUserEntity();
                                      authUser.setUsername(user.username());
                                      authUser.setPassword(pe.encode("123123ee"));
                                      authUser.setEnabled(true);
                                      authUser.setAccountNonExpired(true);
                                      authUser.setAccountNonLocked(true);
                                      authUser.setCredentialsNonExpired(true);

                                      AuthUserEntity createdAuthUser = authUserDaoSpringJdbc.create(authUser);
                                      AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values())
                                                                                  .map(
                                                                                          e -> {
                                                                                              AuthorityEntity ae = new AuthorityEntity();
//                                                                                              ae.setUserId(createdAuthUser.getId());
                                                                                              ae.setUserId(null);
                                                                                              ae.setAuthority(e);
                                                                                              return ae;
                                                                                          }
                                                                                  )
                                                                                  .toArray(AuthorityEntity[]::new);
                                      authAuthorityDaoSpringJdbc.create(authorityEntities);
                                      return UserJson.fromEntity(
                                              udUserDaoSpringJdbc.create(UserEntity.fromJson(user)),
                                              null
                                      );
                                  }
        );
    }

    //<editor-fold desc="Spring JDBC">
    public UserJson createUserSpringJdbcWithAtomikosTransaction(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                                                 AuthUserEntity authUser = new AuthUserEntity();
                                                 authUser.setUsername(user.username());
                                                 authUser.setPassword(pe.encode("123123ee"));
                                                 authUser.setEnabled(true);
                                                 authUser.setAccountNonExpired(true);
                                                 authUser.setAccountNonLocked(true);
                                                 authUser.setCredentialsNonExpired(true);

                                                 AuthUserEntity createdAuthUser = authUserDaoSpringJdbc.create(authUser);
                                                 AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values())
                                                                                             .map(a -> {
                                                                                                      AuthorityEntity ae = new AuthorityEntity();
                                                                                                      ae.setUserId(createdAuthUser.getId());
                                                                                                      ae.setAuthority(a);
                                                                                                      return ae;
                                                                                                  }
                                                                                             )
                                                                                             .toArray(AuthorityEntity[]::new);
                                                 authAuthorityDaoSpringJdbc.create(authorityEntities);
                                                 return UserJson.fromEntity(
                                                         udUserDaoSpringJdbc.create(UserEntity.fromJson(user)),
                                                         null
                                                 );
                                             }
        );
    }

    public UserJson createUserSpringJdbcWithoutTransaction(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("123123ee"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserDaoSpringJdbc.create(authUser);
        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values())
                                                    .map(a -> {
                                                             AuthorityEntity ae = new AuthorityEntity();
                                                             ae.setUserId(createdAuthUser.getId());
                                                             ae.setAuthority(a);
                                                             return ae;
                                                         }
                                                    )
                                                    .toArray(AuthorityEntity[]::new);
        authAuthorityDaoSpringJdbc.create(authorityEntities);
        return UserJson.fromEntity(
                udUserDaoSpringJdbc.create(UserEntity.fromJson(user)),
                null
        );
    }

    public List<AuthorityEntity> findAllAuthoritiesSpringJdbc() {
        return authAuthorityDaoJdbc.findAll();
    }

    public List<AuthUserEntity> findAllAuthUsersSpringJdbc() {
        return authUserDaoSpringJdbc.findAll();
    }

    public List<UserEntity> findAllUdUsersSpringJdbc() {
        return udUserDaoJdbc.findAll();
    }
//</editor-fold>

    //<editor-fold desc="JDBC">
    public UserJson createUserJdbcWithTransaction(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                                                 AuthUserEntity authUser = new AuthUserEntity();
                                                 authUser.setUsername(user.username());
                                                 authUser.setPassword(pe.encode("123123ee"));
                                                 authUser.setEnabled(true);
                                                 authUser.setAccountNonExpired(true);
                                                 authUser.setAccountNonLocked(true);
                                                 authUser.setCredentialsNonExpired(true);

                                                 AuthUserEntity createdAuthUser = authUserDaoJdbc.create(authUser);
                                                 AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values())
                                                                                             .map(a -> {
                                                                                                      AuthorityEntity ae = new AuthorityEntity();
                                                                                                      ae.setUserId(createdAuthUser.getId());
                                                                                                      ae.setAuthority(a);
                                                                                                      return ae;
                                                                                                  }
                                                                                             )
                                                                                             .toArray(AuthorityEntity[]::new);
                                                 authAuthorityDaoJdbc.create(authorityEntities);
                                                 return UserJson.fromEntity(
                                                         udUserDaoJdbc.create(UserEntity.fromJson(user)),
                                                         null
                                                 );
                                             }
        );
    }

    public UserJson createUserJdbcWithoutTransaction(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("123123ee"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        authUserDaoJdbc.create(authUser);
        authAuthorityDaoJdbc.create(
                Arrays.stream(Authority.values())
                      .map(a -> {
                               AuthorityEntity ae = new AuthorityEntity();
                               ae.setUserId(authUser.getId());
                               ae.setAuthority(a);
                               return ae;
                           }
                      )
                      .toArray(AuthorityEntity[]::new));
        UserEntity ue = new UserEntity();
        ue.setUsername(user.username());
        ue.setFullName(user.fullname());
        ue.setCurrency(user.currency());
        udUserDaoJdbc.create(ue);
        return UserJson.fromEntity(ue, null);
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