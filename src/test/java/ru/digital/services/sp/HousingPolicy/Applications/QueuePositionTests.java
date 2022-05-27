package ru.digital.services.sp.HousingPolicy.Applications;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.HousingPolicyRequests.NewHousingPolicyRequests;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.HousingPolicyUtils;
import java.util.ArrayList;
import java.util.List;

public class QueuePositionTests {

    private static User user = Users.getUser("02022035663");
    private static NewHousingPolicyRequests httpRequestSender = new NewHousingPolicyRequests();
    public List<String> appsToClean = new ArrayList<>();

    @Test
    public void getQueuePosition() {
        //fill payload
        JSONObject application = HousingPolicyUtils.createBasicApplication();

        //send
        httpRequestSender.getBearerRequestWithCreds(user)
                .body(application.toString())
                .log().all()
                .post(NewHousingPolicyRequests.createApplicationUrl)
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        Response response = httpRequestSender.getBearerRequestWithCreds(user)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);
        String id = response.body().jsonPath().getString("id");
        appsToClean.add(id);

        Response positionResponse = httpRequestSender.getBearerRequestWithCreds(user)
                .get(NewHousingPolicyRequests.queuePositionUrl);

        Assert.assertEquals(positionResponse.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(positionResponse.body().jsonPath().getString("status"), "SENDED");
        Assert.assertEquals(positionResponse.body().jsonPath().getString("position"), null);

        positionResponse = httpRequestSender.getBearerRequestWithCreds(user)
                .queryParam("isCache", true)
                .get(NewHousingPolicyRequests.queuePositionUrl);

        Assert.assertEquals(positionResponse.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(positionResponse.body().jsonPath().getString("status"), "SENDED");//ToDO хз что тут будет
        Assert.assertEquals(positionResponse.body().jsonPath().getString("position"), null);

        //Добавить в БД запись, с нужным значением очереди
        int position = 45;

        positionResponse = httpRequestSender.getBearerRequestWithCreds(user)
                .queryParam("isCache", true)
                .get(NewHousingPolicyRequests.queuePositionUrl);

        Assert.assertEquals(positionResponse.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(positionResponse.body().jsonPath().getString("status"), "RECIVED");
        Assert.assertEquals(positionResponse.body().jsonPath().getString("position"), position);

        //Дергаем не из кэша, чтобы данные получить от интеграции
        positionResponse = httpRequestSender.getBearerRequestWithCreds(user)
                .queryParam("isCache", true)
                .get(NewHousingPolicyRequests.queuePositionUrl);

        Assert.assertEquals(positionResponse.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(positionResponse.body().jsonPath().getString("status"), "RECIVED");
        Assert.assertEquals(positionResponse.body().jsonPath().getString("position"), 0);//ToDo значение
    }

    @AfterClass
    public void cleanUp(){
        HousingPolicyUtils.deleteApplicationsByJob(appsToClean);
    }
}