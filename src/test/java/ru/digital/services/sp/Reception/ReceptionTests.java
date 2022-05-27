package ru.digital.services.sp.Reception;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.ReceptionRequests.ReceptionRequests;
import ru.digital.services.sp.Dictionaries.WebPlatforms;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import java.util.HashMap;

public class ReceptionTests {
    public User user = Users.getUser("11994683915");

    //Todo("Вынести в User")
    public String usersManager = "e9f9839c-afb2-4639-bf8c-2b8797fa0eb8";

    @DataProvider(name = "Список полигонов для проверки")
    public Object[][] poligons() {
        return new Object[][]{
                {
                        WebPlatforms.WEB
                },
                {
                        WebPlatforms.MOBILE
                }
        };
    }

    @Test(testName = "CPO-T304 Доступ к функционалу Онлайн-приемной", dataProvider = "Список полигонов для проверки")
    public void checkFeatureAvailabilityTest(WebPlatforms platform) {
        //Отправить запрос на проверку доступа к функционалу Онлайн-приемной
        Response response = ReceptionRequests.checkReceptionAvailable(user, platform);

        Assert.assertEquals(response.body().asPrettyString(), "true",
                "Полигон " +platform +" не доступен для пользователя, ожидали доступно");
    }

    @Test(testName = "CPO-T304 Получение руководителя сотрудника")
    public void getUserManagerTest() {
        //Отправить запрос на определение руководителя
        HashMap<String, String> queryParams = new HashMap<String, String>() {{
            put("parent", "false");
        }};
        Response response = ReceptionRequests.getManager(user, queryParams);

        Assert.assertEquals(response.body().asPrettyString(), usersManager,
                "Руководитель пользователя отличается от ожидаемого");
    }
}
