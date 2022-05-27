package ru.digital.services.sp.Benefit.ManualHelpers;

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

import java.time.LocalDate;

//Набор генераторов данных для тестирования кнопки Подробнее на странице Мои компенсации
public class AdditionalButtonTestHelper {

    public User user = Users.getUser("02022035663");

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

    @Test(testName = "T448 - Содержимое Подробнее для Льготный проезд", dataProvider = "формы втт")
    public void addAdditionalInfoForVTT(String formType) throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        JSONArray benefits = new JSONArray()
                .put(new JSONObject()
                        .put("code", BenefitCodes.Travel.getValue())
                        .put("year", LocalDate.now().getYear())
                        .put("sumComp", 10000)
                        .put("sumPart", 0)
                        .put("formTicket", formType)
                        .put("fromStation", "Откудова")
                        .put("toStation", "Дотудова")
                        .put("benefDate", "2022-01-20")
                        .put("benefType", BeneficiaryTypes.employee.getValue()));

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

        //шаг 3
        BenefitRequests.sendRequest(user);
        result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        requestId = result.getJSONObject(0).getString("uuid");

        benefits.put(new JSONObject()
                        .put("code", BenefitCodes.Travel.getValue())
                        .put("year", LocalDate.now().getYear())
                        .put("sumComp", 1000)
                        .put("sumPart", 0)
                        .put("formTicket", formType)
                        .put("fromStation", "Откудова")
                        .put("toStation", "Дотудова")
                        .put("benefDate", "2022-01-21")
                        .put("benefType", BeneficiaryTypes.dependent.getValue()));

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefits)
                .toString());

        isReady = false;
        while (!isReady) {
            Response response = BenefitRequests.getRequestsStatus(user);
            isReady = "\"RESPONSE_RECEIVED\"".equals(response.body().asString());

            if (!isReady) {
                Thread.sleep(10000);
            }
        }

        //шаг 5
        BenefitRequests.sendRequest(user);
        result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        requestId = result.getJSONObject(0).getString("uuid");

        benefits.put(new JSONObject()
                        .put("code", BenefitCodes.Travel.getValue())
                        .put("year", LocalDate.now().getYear())
                        .put("sumComp", 700)
                        .put("sumPart", 0)
                        .put("formTicket", formType)
                        .put("fromStation", "Откудова")
                        .put("toStation", "Дотудова")
                        .put("benefDate", "2022-01-10")
                        .put("benefType", BeneficiaryTypes.dependent.getValue()));

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefits)
                .toString());

        isReady = false;
        while (!isReady) {
            Response response = BenefitRequests.getRequestsStatus(user);
            isReady = "\"RESPONSE_RECEIVED\"".equals(response.body().asString());

            if (!isReady) {
                Thread.sleep(10000);
            }
        }
    }

    @Test(testName = "T445 - Содержимое Подробнее для Отдых и оздоровление")
    public void addAdditionalInfoForSanKur() throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", new JSONArray()
                        .put(new JSONObject()
                                .put("code", BenefitCodes.SanKur.getValue())
                                .put("year", LocalDate.now().getYear())
                                .put("sumComp", 5000)
                                .put("sumPart", 0)
                                .put("benefType", BeneficiaryTypes.employee.getValue()))
                        .put(new JSONObject()
                                .put("code", BenefitCodes.SanKur.getValue())
                                .put("year", LocalDate.now().getYear())
                                .put("sumComp", 350)
                                .put("sumPart", 0)
                                .put("benefType", BeneficiaryTypes.dependent.getValue())))
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

    @Test(testName = "T446 - Содержимое Подробнее для Корпоративное пенсионное обеспечение")
    public void addAdditionalInfoForPension() throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", new JSONArray()
                        .put(new JSONObject()
                                .put("code", BenefitCodes.Pension.getValue())
                                .put("year", LocalDate.now().getYear())
                                .put("sumComp", 5000)
                                .put("sumPart", 1000)
                                .put("benefType", BeneficiaryTypes.employee.getValue())))
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

    @Test(testName = "T447 - Содержимое Подробнее для Компенсируемый социальный пакет")
    public void addAdditionalInfoForCSP() throws InterruptedException {
        BenefitRequests.sendRequest(user);
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.benefit,
                "SELECT uuid FROM requests WHERE snils = '" + user.snils + "'ORDER BY send_time DESC LIMIT 1");

        String requestId = result.getJSONObject(0).getString("uuid");

        BenefitRequests.sendBenefits(new JSONObject()
                .put("uuid", requestId)
                .put("benefit", new JSONArray()
                        .put(new JSONObject()
                                .put("code", BenefitCodes.CSP.getValue())
                                .put("year", LocalDate.now().getYear())
                                .put("sumComp", 5000)
                                .put("sumPart", 1000)
                                .put("benefType", BeneficiaryTypes.employee.getValue())))
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
