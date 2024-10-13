package guru.qa.niffler.data.entity.auth;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class AuthUserEntity implements Serializable {
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
}
