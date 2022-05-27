package ru.digital.services.sp.Generators;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.BenefitRequests.BenefitRequests;
import ru.digital.services.sp.Dictionaries.BeneficiaryTypes;
import ru.digital.services.sp.Dictionaries.BenefitCodes;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.SQL.PostgreSQLConnection;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.BenefitUtils;
import java.time.LocalDate;

public class GenerateBenefitsForUser {

    @Test(testName = "Генерация данных о льготах пользователю для калькулятора льгот")
    public void generateCalcBenefit() throws InterruptedException {
        User user = Users.getFakeUser("11691002627", "Test2000");
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        JSONObject responseBody = BenefitUtils.GenerateResponseBenefits(requestId);

        BenefitRequests.sendBenefits(responseBody.toString());

        boolean isReady = false;
        while (!isReady) {
            Response response = BenefitRequests.getRequestsStatus(user);
            isReady = "\"RESPONSE_RECEIVED\"".equals(response.body().asString());

            if (!isReady) {
                Thread.sleep(10000);
            }
        }
        Response response = BenefitRequests.getBenefits(user);
    }

    @Test
    public void generateEmptyBenefit() throws InterruptedException {
        User user = Users.getFakeUser("11691002627", "Test2000");
        PostgreSQLConnection.changeDataBySQL(db.benefit,
                "DELETE FROM benefit WHERE snils = '" + user.snils + "';");
    }

    @DataProvider(name = "формы втт")
    public Object[][] createWithMinimumFieldsParams() {
        return new Object[][]{
                {"Ф.6"},
                {"Ф.6В"},
                {"Ф.11"},
                {"Ф.4"},
                {"Ф.4У"},
                {"Ф.10"},
                {"Ф.9"},
        };
    }

    public JSONArray benefits = new JSONArray();

    @Test(testName = "T448 - Содержимое Подробнее для Льготный проезд", dataProvider = "формы втт")
    public void addAdditionalInfoForVTT(String formType) throws InterruptedException {

        User user = Users.getFakeUser("11691002627", "Test2000");
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        benefits.put(new JSONObject()
                .put("code", BenefitCodes.Travel.getValue())
                .put("year", LocalDate.now().getYear())
                .put("sumComp", 16000.99)
                .put("sumPart", 0)
                .put("formTicket", formType)
                .put("fromStation", "078109 ВЕЦУМИ-ПУРВМАЛА ГРАНИЦ")
                .put("toStation", "087201 ПЕЧОРЫ ПСКОВ-ПИУЗА ГР.")
                .put("benefDate", "2022-01-20")
                .put("benefType", BeneficiaryTypes.employee.getValue()));

        benefits.put(new JSONObject()
                .put("code", BenefitCodes.Travel.getValue())
                .put("year", LocalDate.now().getYear())
                .put("sumComp", 16000.99)
                .put("sumPart", 0)
                .put("formTicket", formType)
                .put("fromStation", "078109 ВЕЦУМИ-ПУРВМАЛА ГРАНИЦ")
                .put("toStation", "087201 ПЕЧОРЫ ПСКОВ-ПИУЗА ГР.")
                .put("benefDate", "2022-01-21")
                .put("benefType", BeneficiaryTypes.dependent.getValue()));

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefits)
                .toString());

        boolean isReady = false;
        while (!isReady) {
            Response response = BenefitRequests.getRequestsStatus(user);
            isReady = "\"RESPONSE_RECEIVED\"".equals(response.body().asString());

            if (!isReady) {
                Thread.sleep(10000);
            }
        }
    }
}