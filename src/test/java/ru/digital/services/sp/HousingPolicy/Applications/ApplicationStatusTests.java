package ru.digital.services.sp.HousingPolicy.Applications;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.HousingPolicyRequests.HousingPolicyIntRequests;
import ru.digital.services.sp.API.HousingPolicyRequests.NewHousingPolicyRequests;
import ru.digital.services.sp.API.NotificationRequests.NotificationRequests;
import ru.digital.services.sp.API.RequestPayload;
import ru.digital.services.sp.API.RequestPayloadBuilder;
import ru.digital.services.sp.Dictionaries.NotificationsTypes;
import ru.digital.services.sp.Dictionaries.housingpolicy.ApplicationRequestsStatuses;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.HousingPolicyUtils;
import ru.digital.services.sp.Utils.NotificationUtils;
import ru.digital.services.sp.Utils.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ApplicationStatusTests {
    private static User user = Users.getUser("02022035663");
    private static NewHousingPolicyRequests housingHttpClient = new NewHousingPolicyRequests();
    private static NotificationRequests notificationsHttpClient = new NotificationRequests();
    private List<String> appsToClean = new ArrayList<>();
    private String applicationId;

    @BeforeMethod
    public void beforeTests() {
        //Одним из условий теста является состояние пользователя - 0 непрочитанных
        NotificationUtils.readAllUserNotifications(user);
        applicationId = HousingPolicyUtils.sendApplication(user, true);
        appsToClean.add(applicationId);
    }

    //ToDO: Проверки header, subHeader для страницы заявок

    @Test()
    public void approveRequestsStatusesTest() throws InterruptedException {
        JSONObject changeStatusBody = new JSONObject()
                .put("id", applicationId)
                .put("date", LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"Z")
                .put("status", ApplicationRequestsStatuses.SENDED.getId())
                .put("housingCommission", new JSONObject()
                        .put("name", Utils.randomRussianWords(2))
                        .put("contacts", new JSONArray()
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))));

        RequestPayload StatusChangePayload = RequestPayloadBuilder.builder()
                .setWithAuth()
                .setBody(changeStatusBody.toString())
                .build();
        HousingPolicyIntRequests.changeApplicationRequestStatus(StatusChangePayload);

        Thread.sleep(5000);

        Response applicationRequest = housingHttpClient.
                getBearerRequestWithCreds(user)
                .queryParam("id", applicationId)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        Assert.assertEquals(applicationRequest.body().jsonPath().getString("id"), applicationId);
        Assert.assertTrue(applicationRequest.body().jsonPath().getList("statusHistory.status").contains(
                        ApplicationRequestsStatuses.SENDED.toString()),
                applicationRequest.body().jsonPath().getList("statusHistory.status").toString());
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.name"),
                changeStatusBody.getJSONObject("housingCommission").get("name"));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].address"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("address").toString());
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].phone"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("phone").toString());

        Thread.sleep(2000);

        Response notifications = notificationsHttpClient.getBearerRequestWithCreds(user)
                .param("mode", "unread")
                .get(NotificationRequests.getNotificationsUrl);

        Assert.assertTrue(notifications.body().jsonPath().getList("data.type")
                .contains(NotificationsTypes.HOUSING_POLICY_FILED.toString()));
        String filedNotification = "По заявке для постановки на учет пришла информация о жилищной комиссии.";
        Assert.assertTrue(notifications.body().jsonPath().getList("data.text")
                        .contains(filedNotification),
                Utils.logSearchingInArrayAssertMessage(notifications.body().jsonPath().getList("data.text"),
                        filedNotification));

        changeStatusBody.put("status", ApplicationRequestsStatuses.UNDER_CONSIDERATION.getId())
                .put("comment", ApplicationRequestsStatuses.UNDER_CONSIDERATION + Utils.randomRussianWords(5))
                .put("regNumber", Utils.randomInt(8) + "/" + Utils.randomInt(2))
                .put("housingCommission", new JSONObject()
                        .put("name", Utils.randomRussianWords(2))
                        .put("contacts", new JSONArray()
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))));

        StatusChangePayload = RequestPayloadBuilder.builder()
                .setWithAuth()
                .setBody(changeStatusBody.toString())
                .build();
        HousingPolicyIntRequests.changeApplicationRequestStatus(StatusChangePayload);

        Thread.sleep(5000);

        applicationRequest = housingHttpClient.
                getBearerRequestWithCreds(user)
                .queryParam("id", applicationId)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        Assert.assertEquals(applicationRequest.body().jsonPath().getString("id"), applicationId);
        Assert.assertTrue(applicationRequest.body().jsonPath().getList("statusHistory.status").contains(
                        ApplicationRequestsStatuses.UNDER_CONSIDERATION.toString()),
                applicationRequest.body().jsonPath().getList("statusHistory.status").toString());
        Assert.assertTrue(applicationRequest.body().jsonPath().getList("statusHistory.comment").contains(
                changeStatusBody.get("comment")));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("regNumber"),
                changeStatusBody.get("regNumber"));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.name"),
                changeStatusBody.getJSONObject("housingCommission").get("name"));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].address"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("address").toString());
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].phone"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("phone").toString());

        Thread.sleep(3000);

        notifications = notificationsHttpClient.getBearerRequestWithCreds(user)
                .param("mode", "unread")
                .get(NotificationRequests.getNotificationsUrl);

        Assert.assertTrue(notifications.body().jsonPath().getList("data.type")
                .contains(NotificationsTypes.HOUSING_POLICY_UNDER_CONSIDERATION.toString()));

        String underConsiderationNotification = "Заявление для постановки на учет в жилищную комиссию на рассмотрении. " +
                "Номер заявления: " + changeStatusBody.get("regNumber") + ".";
        Assert.assertTrue(notifications.body().jsonPath().getList("data.text")
                        .contains(underConsiderationNotification),
                Utils.logSearchingInArrayAssertMessage(notifications.body().jsonPath().getList("data.text"),
                        underConsiderationNotification));

        changeStatusBody
                .put("status", ApplicationRequestsStatuses.APPROVED.getId())
                .put("comment", ApplicationRequestsStatuses.APPROVED + Utils.randomRussianWords(5))
                .put("regNumber", Utils.randomInt(8) + "/" + Utils.randomInt(2))
                .put("housingCommission", new JSONObject()
                        .put("name", Utils.randomRussianWords(2))
                        .put("contacts", new JSONArray()
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))));

        StatusChangePayload = RequestPayloadBuilder.builder()
                .setWithAuth()
                .setBody(changeStatusBody.toString())
                .build();

        HousingPolicyIntRequests.changeApplicationRequestStatus(StatusChangePayload);

        Thread.sleep(5000);

        applicationRequest = housingHttpClient
                .getBearerRequestWithCreds(user)
                .queryParam("id", applicationId)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        Assert.assertEquals(applicationRequest.body().jsonPath().getString("id"), applicationId);
        Assert.assertTrue(applicationRequest.body().jsonPath().getList("statusHistory.status").contains(
                        ApplicationRequestsStatuses.APPROVED.toString()),
                applicationRequest.body().jsonPath().getList("statusHistory.status").toString());
        Assert.assertTrue(applicationRequest.body().jsonPath().getList("statusHistory.comment").contains(
                changeStatusBody.get("comment")));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("regNumber"),
                changeStatusBody.get("regNumber"));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.name"),
                changeStatusBody.getJSONObject("housingCommission").get("name"));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].address"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("address").toString());
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].phone"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("phone").toString());

        Thread.sleep(3000);

        notifications = notificationsHttpClient.getBearerRequestWithCreds(user)
                .param("mode", "unread")
                .get(NotificationRequests.getNotificationsUrl);

        Assert.assertTrue(notifications.body().jsonPath().getList("data.type")
                .contains(NotificationsTypes.HOUSING_POLICY_APPROVED.toString()));
        String approvedNotification = "Заявление для постановки на учет в жилищную комиссию одобрено.";
        Assert.assertTrue(notifications.body().jsonPath().getList("data.text")
                .contains(approvedNotification),
                Utils.logSearchingInArrayAssertMessage(notifications.body().jsonPath().getList("data.text"),
                        approvedNotification));
    }

    @Test()
    public void rejectRequestsStatusesTest() throws InterruptedException {
        JSONObject changeStatusBody = new JSONObject()
                .put("id", applicationId)
                .put("date", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)+"Z")
                .put("status", ApplicationRequestsStatuses.SENDED.getId())
                .put("housingCommission", new JSONObject()
                        .put("name", Utils.randomRussianWords(2))
                        .put("contacts", new JSONArray()
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))));

        RequestPayload StatusChangePayload = RequestPayloadBuilder.builder()
                .setWithAuth()
                .setBody(changeStatusBody.toString())
                .build();
        HousingPolicyIntRequests.changeApplicationRequestStatus(StatusChangePayload);

        Thread.sleep(5000);

        Response applicationRequest = housingHttpClient.
                getBearerRequestWithCreds(user)
                .queryParam("id", applicationId)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        Assert.assertEquals(applicationRequest.body().jsonPath().getString("id"), applicationId);
        Assert.assertTrue(applicationRequest.body().jsonPath().getList("statusHistory.status").contains(
                        ApplicationRequestsStatuses.SENDED.toString()),
                applicationRequest.body().jsonPath().getList("statusHistory.status").toString());
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.name"),
                changeStatusBody.getJSONObject("housingCommission").get("name"));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].address"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("address").toString());
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].phone"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("phone").toString());

        changeStatusBody.put("status", ApplicationRequestsStatuses.UNDER_CONSIDERATION.getId())
                .put("comment", ApplicationRequestsStatuses.UNDER_CONSIDERATION + Utils.randomRussianWords(5))
                .put("regNumber", Utils.randomInt(8) + "/" + Utils.randomInt(2))
                .put("housingCommission", new JSONObject()
                        .put("name", Utils.randomRussianWords(2))
                        .put("contacts", new JSONArray()
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))));

        StatusChangePayload = RequestPayloadBuilder.builder()
                .setWithAuth()
                .setBody(changeStatusBody.toString())
                .build();
        HousingPolicyIntRequests.changeApplicationRequestStatus(StatusChangePayload);

        Thread.sleep(5000);

        applicationRequest = housingHttpClient.
                getBearerRequestWithCreds(user)
                .queryParam("id", applicationId)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        Assert.assertEquals(applicationRequest.body().jsonPath().getString("id"), applicationId);

        changeStatusBody
                .put("status", ApplicationRequestsStatuses.REJECTED.getId())
                .put("comment", ApplicationRequestsStatuses.REJECTED + Utils.randomRussianWords(5))
                .put("regNumber", Utils.randomInt(8) + "/" + Utils.randomInt(2))
                .put("housingCommission", new JSONObject()
                        .put("name", Utils.randomRussianWords(2))
                        .put("contacts", new JSONArray()
                                .put(new JSONObject()
                                        .put("address", Utils.randomRussianWords(4))
                                        .put("phone", Utils.randomRussianPhone()))));

        StatusChangePayload = RequestPayloadBuilder.builder()
                .setWithAuth()
                .setBody(changeStatusBody.toString())
                .build();

        HousingPolicyIntRequests.changeApplicationRequestStatus(StatusChangePayload);

        Thread.sleep(5000);

        applicationRequest = housingHttpClient.
                getBearerRequestWithCreds(user)
                .queryParam("id", applicationId)
                .get(NewHousingPolicyRequests.getApplicationUrl);

        Assert.assertEquals(applicationRequest.body().jsonPath().getString("id"), applicationId);
        Assert.assertTrue(applicationRequest.body().jsonPath().getList("statusHistory.status").contains(
                        ApplicationRequestsStatuses.REJECTED.toString()),
                applicationRequest.body().jsonPath().getList("statusHistory.status").toString());
        Assert.assertTrue(applicationRequest.body().jsonPath().getList("statusHistory.comment").contains(
                changeStatusBody.get("comment")));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("regNumber"),
                changeStatusBody.get("regNumber"));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.name"),
                changeStatusBody.getJSONObject("housingCommission").get("name"));
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].address"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("address").toString());
        Assert.assertEquals(applicationRequest.body().jsonPath().getString("housingCommission.contacts[0].phone"),
                changeStatusBody.getJSONObject("housingCommission")
                        .getJSONArray("contacts").getJSONObject(0).get("phone").toString());
        Thread.sleep(3000);

        Response notifications = notificationsHttpClient.getBearerRequestWithCreds(user)
                .param("mode", "unread")
                .get(NotificationRequests.getNotificationsUrl);

        Assert.assertTrue(notifications.body().jsonPath().getList("data.type")
                .contains(NotificationsTypes.HOUSING_POLICY_APPROVED.toString()));

        String rejectedNotification = "Заявление для постановки на учет в жилищную комиссию отклонено с комментарием: " +
                changeStatusBody.get("comment");
        Assert.assertTrue(notifications.body().jsonPath().getList("data.text")
                        .contains(rejectedNotification),
                Utils.logSearchingInArrayAssertMessage(notifications.body().jsonPath().getList("data.text"),
                        rejectedNotification));
    }

    @AfterClass
    public void cleanUp() {

        //HousingPolicyUtils.deleteApplicationsByJob(appsToClean);
    }
}
