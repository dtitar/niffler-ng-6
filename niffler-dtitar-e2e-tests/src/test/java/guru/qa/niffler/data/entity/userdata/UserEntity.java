package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UserEntity implements Serializable {
    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String firstname;
    private String surname;
    private byte[] photo;
    private byte[] photoSmall;
    private String fullName;

    public static UserEntity fromJson(UserJson json) {
        UserEntity entity = new UserEntity();
        entity.setId(json.id());
        entity.setUsername(json.username());
        entity.setCurrency(json.currency());
        entity.setFirstname(json.firstname());
        entity.setSurname(json.surname());
        entity.setPhoto(json.photo());
        entity.setPhotoSmall(json.photoSmall());
        entity.setFullName(json.fullname());
        return entity;
    }
}