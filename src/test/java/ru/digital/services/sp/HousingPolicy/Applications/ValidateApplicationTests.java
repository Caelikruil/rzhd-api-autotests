package ru.digital.services.sp.HousingPolicy.Applications;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.HousingPolicyRequests.NewHousingPolicyRequests;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.HousingPolicyUtils;

import java.util.ArrayList;
import java.util.List;

public class ValidateApplicationTests {
    private static User user = Users.getUser("02022035663");
    private static NewHousingPolicyRequests httpRequestSender = new NewHousingPolicyRequests();
    private static List<String> requiredFields = new ArrayList<String>() {{
        add("mobilePhone");
        add("email");
        add("localityAcquisition");
        add("familyMonthlyIncome");
        add("isSCPDHC");
        add("isSCPPDE");
        add("isSFCRPCS");
        add("normSquareId");
    }};

    //Список параметров для проверки метода добавления нормы стоимости м2 жилья в регионе
    @DataProvider(name = "RequiredApplicationFields")
    public Object[][] requiredApplicationFields() {

        Object[][] fields = new Object[requiredFields.size()][1];
        for (int i = 0; i < requiredFields.size(); i++) {
            fields[i][0] = requiredFields.get(i);
        }

        return fields;
    }

    @Test(dataProvider = "RequiredApplicationFields")
    public static void checkErrorsForMissingFieldsTest(String requiredField) {
        JSONObject application = HousingPolicyUtils.createBasicApplication();
        application.remove(requiredField);

        Response response = httpRequestSender.getBearerRequestWithCreds(user)
                .body(application.toString())
                .post(NewHousingPolicyRequests.createApplicationUrl);

        Assert.assertNotEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertEquals(response.body().jsonPath().getString("apierror.subErrors[0].field"), requiredField);
        Assert.assertEquals(response.body().jsonPath().getString("apierror.subErrors[0].message"), "must not be null");
    }

    @Test(testName = "проверка валидации на пустое тело запроса")
    public static void checkAllRequiredFieldsMissing() {
        Response response = httpRequestSender.getBearerRequestWithCreds(user)
                .body("{}")
                .post(NewHousingPolicyRequests.createApplicationUrl)
                .then().log().body().extract().response();

        Assert.assertNotEquals(response.statusCode(), HttpStatus.SC_OK);
        Assert.assertTrue(response.body().jsonPath().getList("apierror.subErrors.field").containsAll(requiredFields));
    }
}
