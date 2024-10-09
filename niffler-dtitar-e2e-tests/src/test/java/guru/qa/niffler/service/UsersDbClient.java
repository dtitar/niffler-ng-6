package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class UsersDbClient {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

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
                                    new AuthUserDaoJdbc(con).create(authUser);
                                    new AuthAuthorityDaoJdbc(con).create(
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
                                    new UdUserDaoJdbc(con).create(ue);
                                    return ue;
                                },
                                CFG.userdataJdbcUrl()
                        )
                ));
    }

    public UserJson findById(UUID id) {
        return transaction(connection -> {
            return new UdUserDaoJdbc(connection).findById(id)
                                                .map(UserJson::fromEntity)
                                                .orElseThrow(() -> new RuntimeException(String.format("User with provided id '%s' not found", id.toString())));
        }, CFG.spendJdbcUrl());
    }

    public UserJson findByUsername(String username) {
        return transaction(connection -> {
            return new UdUserDaoJdbc(connection).findByUsername(username)
                                                .map(UserJson::fromEntity)
                                                .orElseThrow(() -> new RuntimeException(String.format("User with provided username '%s' not found", username)));
        }, CFG.spendJdbcUrl());
    }
}