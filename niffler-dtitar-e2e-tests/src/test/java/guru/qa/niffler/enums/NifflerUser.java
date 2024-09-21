package guru.qa.niffler.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NifflerUser {
    ERIC("eric", "123123ee"),
    STAN("stan", "123123ee"),
    KYLE("kyle", "123123ee"),
    KENNY("kenny", "123123ee");

    private final String username;
    private final String password;
    }
