package ru.digital.services.sp.Event.Users.Region;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import ru.digital.services.sp.API.EventRequests.EventRequests;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

public class UserRegionTests {

    private static User user = Users.getUser("02022035663");

    @BeforeClass
    public void выставить_регион_пользователю()
    {
        EventRequests.setUserRegion(user, "56");
    }


    @Test(testName = "Смена региона пользователя")
    public void смена_региона_пользователю()
    {
        String regionCode = "02";

        //Установим пользователю код региона Башкирии
        EventRequests.setUserRegion(user, regionCode);

        Response response = EventRequests.getUserRegion(user);
        String currentRegionCode = response.getBody().asString();

        Assert.assertEquals(regionCode, currentRegionCode, "Добавленный код не совпадает с ожидаемым");
    }
}
