package ru.digital.services.sp.HousingPolicy.Applications;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.HousingPolicyRequests.NewHousingPolicyRequests;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.SQL.PostgreSQLConnection;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.HousingPolicyUtils;
import ru.digital.services.sp.Utils.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ApplicationTests {
    private static User user = Users.getUser("02022035663");
    private static NewHousingPolicyRequests httpRequestSender = new NewHousingPolicyRequests();
    private List<String> appsToClean = new ArrayList<>();

    @Test
    public void createBasicFieldsApplicationTest() {
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

        //go to DB
        JSONArray dbResults = PostgreSQLConnection.selectFromDatabase(db.housingPolicy,
                "SELECT * FROM housing_applications" +
                        " WHERE id = '" + id + "';");

        JSONObject resultRow = dbResults.getJSONObject(0);

        Assert.assertEquals(resultRow.get("employee_guid"), user.userId);
        Assert.assertEquals(resultRow.get("mobile_phone"), application.get("mobilePhone"));
        Assert.assertEquals(resultRow.get("email"), application.get("email"));
        Assert.assertEquals(resultRow.get("locality_acquisition"), application.get("localityAcquisition"));
        Assert.assertEquals(resultRow.get("family_monthly_income").toString(),
                application.get("familyMonthlyIncome").toString());
        Assert.assertEquals(resultRow.get("is_scpdhc"), application.get("isSCPDHC"));
        Assert.assertEquals(resultRow.get("is_scppde"), application.get("isSCPPDE"));
        Assert.assertEquals(resultRow.get("is_sfcrpcs"), application.get("isSFCRPCS"));
        Assert.assertEquals(resultRow.get("norm_square_id"), application.get("normSquareId"));
    }

    @Test
    public void createFieldsApplicationTest() {
        //fill payload
        JSONObject application = HousingPolicyUtils.createApplication();
        //send
        httpRequestSender.getBearerRequestWithCreds(user)
                .body(application.toString())
                .log().all()
                .post(NewHousingPolicyRequests.createApplicationUrl)
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        Response response = httpRequestSender.getBearerRequestWithCreds(user)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        String id = response.body().jsonPath().getString("id");
        appsToClean.add(id);

        //go to DB
        JSONArray dbResults = PostgreSQLConnection.selectFromDatabase(db.housingPolicy,
                "SELECT * FROM housing_applications" +
                        " WHERE id = '" + id + "';");

        JSONObject resultRow = dbResults.getJSONObject(0);

        System.out.println(resultRow);
        Assert.assertEquals(resultRow.get("employee_guid"), user.userId);
        Assert.assertEquals(resultRow.get("mobile_phone"), application.get("mobilePhone"));
        Assert.assertEquals(resultRow.get("home_phone"), application.get("homePhone"));
        Assert.assertEquals(resultRow.get("work_phone"), application.get("workPhone"));
        Assert.assertEquals(resultRow.get("leased_area").toString(), application.get("leasedArea").toString());
        Assert.assertEquals(resultRow.get("property_area").toString(), application.get("propertyArea").toString());
        Assert.assertEquals(resultRow.get("cause"), application.get("cause"));
        Assert.assertEquals(resultRow.get("is_young_specialist"), application.get("isYoungSpecialist"));
        Assert.assertEquals(resultRow.get("approved_profession_code"), application.get("approvedProfessionCode"));
        Assert.assertEquals(resultRow.get("work_experience"), application.get("workExperience"));
        Assert.assertEquals(resultRow.get("work_experience_post"), application.get("workExperiencePost"));
        Assert.assertEquals(resultRow.get("separate_category_id").toString(), application.get("separateCategoryId").toString());
        Assert.assertEquals(resultRow.get("is_alienated_housing"), application.get("isAlienatedHousing"));
        Assert.assertEquals(resultRow.get("email"), application.get("email"));
        Assert.assertEquals(resultRow.get("locality_acquisition"), application.get("localityAcquisition"));
        Assert.assertEquals(resultRow.get("family_monthly_income").toString(),
                application.get("familyMonthlyIncome").toString());
        Assert.assertEquals(resultRow.get("is_scpdhc"), application.get("isSCPDHC"));
        Assert.assertEquals(resultRow.get("is_scppde"), application.get("isSCPPDE"));
        Assert.assertEquals(resultRow.get("is_sfcrpcs"), application.get("isSFCRPCS"));
        Assert.assertEquals(resultRow.get("norm_square_id"), application.get("normSquareId"));

        dbResults = PostgreSQLConnection.selectFromDatabase(db.housingPolicy,
                "SELECT * FROM housing_application_relatives"
                        + " WHERE application_vo_id = '" + id + "';");

        Assert.assertEquals(dbResults.length(), application.getJSONArray("relatives").length());

        Assert.assertEquals(dbResults.getJSONObject(0).get("lfm"),
                application.getJSONArray("relatives").getJSONObject(0).get("LFM"));
        Assert.assertEquals(dbResults.getJSONObject(0).get("kinship_id"),
                application.getJSONArray("relatives").getJSONObject(0).get("kinshipId"));
        Assert.assertEquals(dbResults.getJSONObject(0).get("date_birth").toString(),
                application.getJSONArray("relatives").getJSONObject(0).get("dateBirth").toString());
    }

    @Test
    public void createApplicationWithSeveralRelatives() {
        //fill payload
        JSONObject application = HousingPolicyUtils.createBasicApplication();

        application.put("relatives", new JSONArray()
                .put(new JSONObject()
                        .put("LFM", Utils.randomRussianWords(3))
                        .put("kinshipId", "0") //ToDo
                        .put("dateBirth", LocalDate.now().minusYears(15).format(DateTimeFormatter.ISO_LOCAL_DATE))
                ).put(new JSONObject()
                        .put("LFM", Utils.randomRussianWords(3))
                        .put("kinshipId", "0") //ToDo
                        .put("dateBirth", LocalDate.now().minusYears(12).format(DateTimeFormatter.ISO_LOCAL_DATE))
                ).put(new JSONObject()
                        .put("LFM", Utils.randomRussianWords(3))
                        .put("kinshipId", "0") //ToDo
                        .put("dateBirth", LocalDate.now().minusYears(10).format(DateTimeFormatter.ISO_LOCAL_DATE))
                ));
        //send
        httpRequestSender.getBearerRequestWithCreds(user)
                .body(application.toString())
                .post(NewHousingPolicyRequests.createApplicationUrl)
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        Response response = httpRequestSender.getBearerRequestWithCreds(user)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        String id = response.body().jsonPath().getString("id");
        appsToClean.add(id);

        //go to DB
        JSONArray dbResults = PostgreSQLConnection.selectFromDatabase(db.housingPolicy,
                "SELECT * FROM housing_application_relatives"
                        + " WHERE application_vo_id = '" + id + "';");

        Assert.assertEquals(dbResults.length(), application.getJSONArray("relatives").length());
    }

    @Test
    public void checkCreatingNewApplicationTest() {
        //fill payload
        JSONObject application = HousingPolicyUtils.createBasicApplication();
        //send
        httpRequestSender.getBearerRequestWithCreds(user)
                .body(application.toString())
                .post(NewHousingPolicyRequests.createApplicationUrl)
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        Response response = httpRequestSender.getBearerRequestWithCreds(user)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        String id = response.body().jsonPath().getString("id");
        appsToClean.add(id);

        //fill payload
        application = HousingPolicyUtils.createBasicApplication();
        //send
        httpRequestSender.getBearerRequestWithCreds(user)
                .body(application.toString())
                .post(NewHousingPolicyRequests.createApplicationUrl)
                .then().assertThat().statusCode(HttpStatus.SC_OK);

        response = httpRequestSender.getBearerRequestWithCreds(user)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        String secondId = response.body().jsonPath().getString("id");
        appsToClean.add(id);
        Assert.assertNotEquals(id, secondId);

        response = httpRequestSender.getBearerRequestWithCreds(user)
                .queryParam("id", id)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        String newId = response.body().jsonPath().getString("id");
        Assert.assertEquals(id, newId);
        Assert.assertNotEquals(newId, secondId);

        Response listResponse = httpRequestSender.getBearerRequestWithCreds(user)
                .get(NewHousingPolicyRequests.getApplicationRequestsUrl)
                .then().log().body().extract().response();

        Assert.assertTrue(listResponse.body().jsonPath().getList("list.id").contains(id));
        Assert.assertTrue(listResponse.body().jsonPath().getList("list.id").contains(secondId));
    }

    @AfterClass
    public void cleanUp() {
        HousingPolicyUtils.deleteApplicationsByJob(appsToClean);
    }
}
