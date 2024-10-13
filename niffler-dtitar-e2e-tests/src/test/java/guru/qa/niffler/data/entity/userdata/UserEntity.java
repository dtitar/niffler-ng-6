package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Data
public class UserEntity implements Serializable {
    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String firstname;
    private String surname;
    private String fullName;
    private byte[] photo;
    private byte[] photoSmall;

    public static UserEntity fromJson(UserJson json) {
        UserEntity entity = new UserEntity();
        entity.setId(json.id());
        entity.setUsername(json.username());
        entity.setCurrency(json.currency());
        entity.setFirstname(json.firstname());
        entity.setSurname(json.surname());
        entity.setPhoto(json.photo() != null ? json.photo()
                                                   .getBytes(StandardCharsets.UTF_8) : null);
        entity.setPhotoSmall(json.photoSmall() != null ? json.photoSmall()
                                                             .getBytes(StandardCharsets.UTF_8) : null);
        return entity;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", currency=" + currency +
                ", firstname='" + firstname + '\'' +
                ", surname='" + surname + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}