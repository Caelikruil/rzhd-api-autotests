package ru.digital.services.sp.VTTService;

import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.RequestPayload;
import ru.digital.services.sp.API.RequestPayloadBuilder;
import ru.digital.services.sp.API.VTTRequests.VTTIntRequests;
import ru.digital.services.sp.API.VTTRequests.VTTRequests;
import ru.digital.services.sp.Dictionaries.VTTStatuses;
import ru.digital.services.sp.Dictionaries.VttStatusEnum;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.SQL.PostgreSQLConnection;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
public class FromResidenceToWorkTests {

    public User user = Users.getUser("02022035663");
    public ArrayList<String> createdVtt = new ArrayList<>();

    @Test(testName = "Выписка ВТТ на проезд на работу")
    public void scnario1() throws InterruptedException {
        //create body
        JSONObject vttForm = new JSONObject()
                .put("year", LocalDate.now().getYear())
                .put("from", "2031239")
                .put("to", "2031239");

        RequestPayload createVttPayload =
                RequestPayloadBuilder.builder().setUser(user).setBody(vttForm.toString()).build();

        //send
        VTTRequests.createVttFromResidenceToWork(createVttPayload);

        RequestPayload vttListRequest = RequestPayloadBuilder.builder()
                .setUser(user)
                .setQueryParams(new HashMap<String, String>() {{
                    put("start", "0");
                    put("count", "1");
                }})
                .build();

        //check
        Response vttList = VTTRequests.getVttList(vttListRequest);

        String vttId = vttList.jsonPath().getString("data[0].longDistanceOrder.id");
        Assert.assertNotNull(vttId);

        RequestPayload getVttRequest = RequestPayloadBuilder.builder().setUser(user).setRouteParam(vttId).build();

        Response fromResidenceToWorkVtt = VTTRequests.getFromResidenceToWorkVtt(getVttRequest);
        Assert.assertEquals(fromResidenceToWorkVtt.jsonPath().getString("to"), vttForm.get("to"));
        Assert.assertEquals(fromResidenceToWorkVtt.jsonPath().getString("from"), vttForm.get("from"));
        Assert.assertEquals(fromResidenceToWorkVtt.jsonPath().getString("year"), vttForm.get("year"));

        Number daIditeVyNaxyi = LocalDate.now().getYear() * 100000000
                + LocalDate.now().getMonthValue() * 1000000
                + LocalDate.now().getDayOfMonth() * 10000
                + LocalDateTime.now().getHour() * 100
                + LocalDateTime.now().getMinute();

        //set status looking
        JSONObject changeVttStatusBody = new JSONObject()
                .put("uuid", vttId) //ToDo не уверен что работает именно так
                .put("timeStamp", daIditeVyNaxyi)
                .put("status", VTTStatuses.CONSIDERATION)
                .put("statusDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .put("commentEx", "Запуск автотестов от " + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .put("orgTxt", "Автотестошная")
                .put("planTxt", "Раннер автотестовый")
                .put("fio", "Ресташуров Ява Фреймворкович")
                .put("infoEx", "+7(800)555-35-35");

        RequestPayload changeVttStatus = RequestPayloadBuilder.builder()
                .setWithAuth()
                .setBody(changeVttStatusBody.toString())
                .build();

        VTTIntRequests.changeVttStatus(changeVttStatus);

        Thread.sleep(120000);
        //check
        vttList = VTTRequests.getVttList(vttListRequest);

        //в коде стоит какой-то автоответчик, покрывать сложно
        Assert.assertEquals(vttList.jsonPath().getString("data[0].longDistanceOrder.status.statusId"),
                VttStatusEnum._03.getId());

        //set status declined
        changeVttStatusBody.put("status", VTTStatuses.REJECTION);
        changeVttStatus = RequestPayloadBuilder.builder()
                .setWithAuth()
                .setBody(changeVttStatusBody.toString())
                .build();

        VTTIntRequests.changeVttStatus(changeVttStatus);

        Thread.sleep(120000);

        //check
        vttList = VTTRequests.getVttList(vttListRequest);
        Assert.assertEquals(vttList.jsonPath().getString("data[0].longDistanceOrder.status.statusId"),
                VttStatusEnum._30.getId());
    }


    @AfterClass
    public void afterClass() {
        if (createdVtt.stream().count() > 0)
            PostgreSQLConnection.changeDataBySQL(db.vtt,
                    "DELETE from order_from_residence_to_work where id in (" + String.join(",", createdVtt) + ");");
    }
}