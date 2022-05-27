package ru.digital.services.sp.Users;

import org.bson.Document;
import ru.digital.services.sp.Dictionaries.AdminRoles;
import ru.digital.services.sp.Mongo.MongoConnection;
import ru.digital.services.sp.Mongo.MongoDBs;

import javax.activation.UnsupportedDataTypeException;

import java.util.ArrayList;
import java.util.UUID;

import static ru.digital.services.sp.Dictionaries.AdminRoles.adminEvent;

//Класс работы с получением юзеров из базы или еще откуда-нибудь
public class Users {

    public static User getUser(String snils) {
        String query = "{snils : \"" + snils + "\"}";
        Document result = MongoConnection.getDocuments(
                MongoDBs.autotests, "users",
                query).get(0);

        return getUser(result);
    }

    public static User getUserByRole(AdminRoles role) {
        Document result = MongoConnection.getDocuments(
                MongoDBs.autotests, "users", "{roles : [\"" + role + "\"]}").get(0);

        return getUser(result);
    }

    //Нужно в кейсах, когда не надо идти в монгу, а просто иметь класс Юзер с паролем верным
    public static User getFakeUser(String snils, String password)
    {
        return new User(snils, password, null, null, null);
    }

    private static User getUser(Document result) {
        return new User(
                result.getString("snils"),
                result.getString("password"),
                result.getString("userId"),
                result.getString("sndPrn"),
                result.getList("roles", AdminRoles.class));
    }
}

