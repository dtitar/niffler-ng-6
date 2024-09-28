package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.UserJson;

import java.util.UUID;

public class UserdataDbClient {
    private final UserdataUserDao userDao = new UserdataUserDaoJdbc();

    public UserJson createUser(UserJson user) {
        UserEntity userEntity = UserEntity.fromJson(user);
        return UserJson.fromEntity(userDao.create(userEntity));
    }

    public UserJson findById(UUID id) {
        return userDao.findById(id)
                      .map(UserJson::fromEntity)
                      .orElseThrow(() -> new RuntimeException(String.format("User with provided id '%s' not found", id.toString())));
    }

    public UserJson findByUsername(String username) {
        return userDao.findByUsername(username)
                      .map(UserJson::fromEntity)
                      .orElseThrow(() -> new RuntimeException(String.format("User with provided username '%s' not found", username)));
    }

    public int deleteUser(UserJson user) {
        return userDao.delete(UserEntity.fromJson(user));
    }
}