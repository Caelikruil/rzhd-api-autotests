package ru.digital.services.sp.Users;

import ru.digital.services.sp.Dictionaries.AdminRoles;

import java.util.List;
import java.util.UUID;

//Класс пользователя со всей известной о нем информацией
public class User {

    public String snils;
    public String password;
    public String userId;
    public List<AdminRoles> roles;
    public String sndPrn;

    public User(String snils, String password, String userId, String sndPrn, List<AdminRoles> roles) {
        this.snils = snils;
        this.password = password;
        this.userId = userId;
        this.roles = roles;
        this.sndPrn = sndPrn;
    }

    public String getSnilsXmlFileName() {
        return sndPrn + "/" +
                snils.substring(0, 3) + "-"
                + snils.substring(3, 6) + "-"
                + snils.substring(6) + ".xml";
    }
}
