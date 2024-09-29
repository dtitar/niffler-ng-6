package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class UserdataDbClient {
    private static final Config CFG = Config.getInstance();

    public UserJson createUser(UserJson user) {
        return transaction(connection -> {
            UserEntity userEntity = UserEntity.fromJson(user);
            return UserJson.fromEntity(new UserdataUserDaoJdbc(connection).create(userEntity));
        }, CFG.spendJdbcUrl());
    }

    public UserJson findById(UUID id) {
        return transaction(connection -> {
            return new UserdataUserDaoJdbc(connection).findById(id)
                                                      .map(UserJson::fromEntity)
                                                      .orElseThrow(() -> new RuntimeException(String.format("User with provided id '%s' not found", id.toString())));
        }, CFG.spendJdbcUrl());
    }

    public UserJson findByUsername(String username) {
        return transaction(connection -> {
            return new UserdataUserDaoJdbc(connection).findByUsername(username)
                                                      .map(UserJson::fromEntity)
                                                      .orElseThrow(() -> new RuntimeException(String.format("User with provided username '%s' not found", username)));
        }, CFG.spendJdbcUrl());
    }

    public int deleteUser(UserJson user) {
        return transaction(connection -> {
            return new UserdataUserDaoJdbc(connection).delete(UserEntity.fromJson(user));
        }, CFG.spendJdbcUrl());
    }
}