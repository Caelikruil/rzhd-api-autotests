package ru.digital.services.sp.Utils;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.digital.services.sp.API.HousingPolicyRequests.NewHousingPolicyRequests;
import ru.digital.services.sp.Dictionaries.housingpolicy.HousingPolicyDictionaryCodes;
import ru.digital.services.sp.SQL.PostgreSQLConnection;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.Users.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class HousingPolicyUtils {

    private static NewHousingPolicyRequests httpRequestSender = new NewHousingPolicyRequests();

    //Ищет значение в словарях сервиса housing-policy
    public static boolean isPresentInDictionary(User user, HousingPolicyDictionaryCodes dictionaryCode, String searchedValue) {

        Response dictionary = httpRequestSender
                .getBearerRequestWithCreds(user)
                .queryParam("dictCode", dictionaryCode.toString())
                .post(NewHousingPolicyRequests.getDictUrl);

        List<List> dictionaryEntries = dictionary.body().jsonPath().getList("elements", List.class);

        return dictionaryEntries.stream().filter(list -> list.contains(searchedValue)).findAny().isPresent();
    }

    public static boolean isPresentInDictionaryByString(User user, HousingPolicyDictionaryCodes dictionaryCodes, String searchedValue)
    {
        Response dictionary = httpRequestSender
                .getBearerRequestWithCreds(user)
                .queryParam("dictCode", dictionaryCodes)
                .post(NewHousingPolicyRequests.getDictUrl);

        List<String> dictionaryEntries = dictionary.body().jsonPath().getList("elements", String.class);
        return dictionaryEntries.contains(searchedValue);
    }

    public static String sendApplication(User user, boolean isBasic) {
        httpRequestSender.getBearerRequestWithCreds(user)
                .body(isBasic ? createBasicApplication().toString()
                        : createApplication().toString())
                .post(NewHousingPolicyRequests.createApplicationUrl);

        return httpRequestSender
                .getBearerRequestWithCreds(user)
                .get(NewHousingPolicyRequests.getApplicationUrl)
                .body().jsonPath().getString("id");
    }

    public static JSONObject createApplication() {
        //https://git.corp.myservices.digital/rzhd-portal/housingpolicyservice/-/blob/develop/rest.yaml#L121
        //ApplicationRegistration
        JSONObject applicationBody = new JSONObject();

        applicationBody.put("mobilePhone", Utils.randomRussianPhone());
        applicationBody.put("homePhone", Utils.randomRussianPhone());
        applicationBody.put("email", Utils.randomEmail(null));
        applicationBody.put("workPhone", Utils.randomRussianPhone());
        applicationBody.put("relatives", new JSONArray()
                .put(new JSONObject()
                        .put("LFM", Utils.randomRussianWords(3))
                        .put("kinshipId", "e7ed00f8-e6fa-4106-9ac5-6d4ee366fc43")//son
                        .put("dateBirth", LocalDate.now().minusYears(15).format(DateTimeFormatter.ISO_LOCAL_DATE))
                ));
        applicationBody.put("leasedArea", Utils.randomFloat(2));
        applicationBody.put("propertyArea", Utils.randomFloat(2));
        applicationBody.put("cause", Utils.randomRussianWords(20));
        applicationBody.put("isYoungSpecialist", true);
        applicationBody.put("approvedProfessionCode", "1");
        applicationBody.put("workExperience", Utils.randomInt(1));
        applicationBody.put("workExperiencePost", Utils.randomInt(2));
        applicationBody.put("localityAcquisition", "Москва");
        applicationBody.put("familyMonthlyIncome", Utils.randomFloat(5));
        applicationBody.put("separateCategoryId", UUID.randomUUID());
        applicationBody.put("isAlienatedHousing", false);
        applicationBody.put("isSCPDHC", true);
        applicationBody.put("isSCPPDE", true);
        applicationBody.put("isSFCRPCS", true);
        applicationBody.put("normSquareId", "c10abe5b-1413-46bc-8599-0759d17311de");

        return applicationBody;
    }

    public static JSONObject createBasicApplication() {
        //https://git.corp.myservices.digital/rzhd-portal/housingpolicyservice/-/blob/develop/rest.yaml#L121
        //ApplicationRegistration
        JSONObject applicationBody = new JSONObject();

        applicationBody.put("mobilePhone", Utils.randomRussianPhone());
        applicationBody.put("email", Utils.randomEmail(null));
        applicationBody.put("localityAcquisition", "Москва");
        applicationBody.put("familyMonthlyIncome", Utils.randomFloat(5));
        applicationBody.put("isSCPDHC", true);
        applicationBody.put("isSCPPDE", true);
        applicationBody.put("isSFCRPCS", true);
        applicationBody.put("normSquareId", "c10abe5b-1413-46bc-8599-0759d17311de");

        return applicationBody;
    }

    public static void deleteApplicationsByJob(List<String> appsToClean) {
        if (appsToClean.isEmpty())
            return;

        //чистим обе базы, через механизм удаления устаревших заявлений
        PostgreSQLConnection.changeDataBySQL(db.housingPolicyInt,
                "UPDATE hpi_applications SET created_date = '" +
                        LocalDateTime.now().minusYears(6).format(DateTimeFormatter.ISO_LOCAL_DATE)
                        + "' WHERE id in ('" + String.join("', '", appsToClean) + "');");

        PostgreSQLConnection.changeDataBySQL(db.housingPolicy,
                "UPDATE housing_applications SET created_date = '" +
                        LocalDateTime.now().minusYears(6).format(DateTimeFormatter.ISO_LOCAL_DATE)
                        + "' WHERE id in ('" + String.join("', '", appsToClean) + "');");
    }
}
