package ru.digital.services.sp.Benefit.ManualHelpers;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.BenefitRequests.BenefitRequests;
import ru.digital.services.sp.Dictionaries.BeneficiaryTypes;
import ru.digital.services.sp.Dictionaries.BenefitCodes;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.SQL.PostgreSQLConnection;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//Набор генераторов данных для тестирования страницы ИПЛ со стороны фронтенда вручную
public class CompensationDiagramTestHelper {

    public User user = Users.getUser("02022035663");
    public JSONArray benefits = new JSONArray();

    @Test(testName = "T-444 Шаг 1 Льготная ипотека", priority = 0)
    public void addMortgage() throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        benefits.put(new JSONObject()
                .put("code", BenefitCodes.HousingPolicy.getValue())
                .put("year", LocalDate.now().getYear())
                .put("sumComp", 100000)
                .put("sumPart", 0)
                .put("benefType", BeneficiaryTypes.employee.getValue()));

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefits).toString());

        boolean isReady = false;
        while (!isReady) {
            Response response = BenefitRequests.getRequestsStatus(user);
            isReady = "\"RESPONSE_RECEIVED\"".equals(response.body().asString());

            if (!isReady) {
                Thread.sleep(10000);
            }
        }
    }

    @Test(testName = "T-444 Шаг 3 ВТТ", priority = 1)
    public void addVTT() throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        benefits.put(new JSONObject()
                .put("code", BenefitCodes.Travel.getValue())
                .put("year", LocalDate.now().getYear())
                .put("sumComp", 50000)
                .put("sumPart", 0)
                .put("formTicket", "Ф.6В")
                .put("fromStation", "Зуево")
                .put("toStation", "Кукуево")
                .put("benefDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                .put("benefType", BeneficiaryTypes.employee.getValue()));

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefits).toString());

        boolean isReady = false;
        while (!isReady) {
            Response response = BenefitRequests.getRequestsStatus(user);
            isReady = "\"RESPONSE_RECEIVED\"".equals(response.body().asString());

            if (!isReady) {
                Thread.sleep(10000);
            }
        }
    }

    @Test(testName = "T-444 Шаг 6 КСП", priority = 2)
    public void addCSP() throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        benefits.put(new JSONObject()
                .put("code", BenefitCodes.CSP.getValue())
                .put("year", LocalDate.now().getYear())
                .put("sumComp", 30000)
                .put("sumPart", 25000)
                .put("benefType", BeneficiaryTypes.employee.getValue()));

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefits).toString());

        boolean isReady = false;
        while (!isReady) {
            Response response = BenefitRequests.getRequestsStatus(user);
            isReady = "\"RESPONSE_RECEIVED\"".equals(response.body().asString());

            if (!isReady) {
                Thread.sleep(10000);
            }
        }
    }

    @Test(testName = "T-444 Шаг 8 СанКур", priority = 3)
    public void addSanKur() throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        benefits.put(new JSONObject()
                .put("code", BenefitCodes.SanKur.getValue())
                .put("year", LocalDate.now().getYear())
                .put("sumComp", 15000)
                .put("sumPart", 0)
                .put("benefType", BeneficiaryTypes.employee.getValue()));

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefits).toString());

        boolean isReady = false;
        while (!isReady) {
            Response response = BenefitRequests.getRequestsStatus(user);
            isReady = "\"RESPONSE_RECEIVED\"".equals(response.body().asString());

            if (!isReady) {
                Thread.sleep(10000);
            }
        }
    }

    @Test(testName = "T-444 Шаг 10 КПО", priority = 4)
    public void addPension() throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        benefits.put(new JSONObject()
                .put("code", BenefitCodes.Pension.getValue())
                .put("year", LocalDate.now().getYear())
                .put("sumComp", 10000)
                .put("sumPart", 500)
                .put("benefType", BeneficiaryTypes.employee.getValue()));

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefits).toString());

        boolean isReady = false;
        while (!isReady) {
            Response response = BenefitRequests.getRequestsStatus(user);
            isReady = "\"RESPONSE_RECEIVED\"".equals(response.body().asString());

            if (!isReady) {
                Thread.sleep(10000);
            }
        }
    }

    @Test(testName = "T-444 Шаг 12 Детский отдых", priority = 5)
    public void addChildrenRest() throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        benefits.put(new JSONObject()
                .put("code", BenefitCodes.ChildRest.getValue())
                .put("year", LocalDate.now().getYear())
                .put("sumComp", 9000)
                .put("sumPart", 0)
                .put("benefType", BeneficiaryTypes.employee.getValue()));

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefits).toString());

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