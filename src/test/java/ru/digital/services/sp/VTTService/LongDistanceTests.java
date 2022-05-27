package ru.digital.services.sp.VTTService;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.RequestPayload;
import ru.digital.services.sp.API.RequestPayloadBuilder;
import ru.digital.services.sp.API.VTTRequests.VTTIntRequests;
import ru.digital.services.sp.API.VTTRequests.VTTRequests;
import ru.digital.services.sp.Dictionaries.VTTStatuses;
import ru.digital.services.sp.Dictionaries.VttStatusEnum;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.SQL.PostgreSQLConnection;
import ru.digital.services.sp.Users.FamilyMember;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.EmployeeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class LongDistanceTests {

    public String childFamsa = "6";
    public String childSerNum = "666";
    public User user = Users.getUser("02022035663");
    public boolean childCreated = false;

    @BeforeClass
    public void beforeLong() throws Exception {
        //create a child
        FamilyMember child = new FamilyMember(childFamsa, childSerNum);

        childCreated = EmployeeUtils.addFamilyMemberForEmployee(user, child);
        Assert.assertTrue(childCreated, "Не выполнились предусловия");
        Thread.sleep(60000);
    }


    @Test(testName = "Выписка ВТТ на иждивенца")
    public void scnario1() throws InterruptedException {
        //create body
        JSONObject vttForm = new JSONObject()
                .put("year", LocalDate.now().getYear())
                .put("there", new JSONArray()
                        .put(new JSONObject()
                                .put("station", "2031239")
                                .put("type", "source"))
                        .put(new JSONObject()
                                .put("station", "2031239")
                                .put("type", "dest")))
                .put("return", new JSONArray()
                        .put(new JSONObject()
                                .put("station", "2031239")
                                .put("type", "source"))
                        .put(new JSONObject()
                                .put("station", "2031239")
                                .put("type", "dest")))
                .put("passengers", new JSONObject()
                        .put("isEmployee", false)
                        .put("familyMembers", new JSONArray()
                                .put(new JSONObject()
                                        .put("Famsa", childFamsa)
                                        .put("SerNum", childSerNum)
                                )));

        RequestPayload createVttPayload =
                RequestPayloadBuilder.builder().setUser(user).setBody(vttForm.toString()).build();

        //send
        VTTRequests.createVtt(createVttPayload);

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
        Assert.assertEquals(vttList.jsonPath().getString("data[0].longDistanceOrder.passenger.famsa"), childFamsa);
        Assert.assertEquals(vttList.jsonPath().getString("data[0].longDistanceOrder.passenger.serNum"), childSerNum);

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

        /*
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
        */

        //check
        vttList = VTTRequests.getVttList(vttListRequest);
        Assert.assertEquals(vttList.jsonPath().getString("data[0].longDistanceOrder.status.statusId"),
                VttStatusEnum._30.getId());
    }


    //ToDo делать после избавления от автоответчика
    public void scenario2() {
        //create
        //send
        //set status looking
        //set status approved
        //check
    }

    public void scenario3() {
        //create
        //send
        //delete before looking
    }

    @AfterClass
    public void afterClass() throws Exception {
        //remote a child
        if (childCreated) {
            EmployeeUtils.removeFamilyMemberBySerNum(user, childSerNum);
            Thread.sleep(30000);
        }

        PostgreSQLConnection.changeDataBySQL(db.vtt,
                "DELETE from vtt_status where vtt_uuid in (select vtt_uuid from vtt where snils = '"
                        + user.snils + "' and ser_num = '" + childFamsa + "');");
        PostgreSQLConnection.changeDataBySQL(db.vtt,
                "delete from vtt where snils = '" + user.snils + "' and ser_num = '" + childFamsa + "';");
    }
}
