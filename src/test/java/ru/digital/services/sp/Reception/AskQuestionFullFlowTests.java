package ru.digital.services.sp.Reception;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.ReceptionRequests.ReceptionRequests;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.Utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class AskQuestionFullFlowTests {

    public User user = Users.getUser("11994683915");
    public String orderId;
    public String questionId;

    private String fileName = "dots5x5.jpg";
    private String fileMimeType = "image/jpeg";
    private int fileSize = 796;
    private String fileData = Utils.getBase64ImageDate();

    @Test(testName = "CPO-T305 Отправка обращения")
    public void createQuestionTest() {
        String requestBody = new JSONObject()
                .put("subject", "61ad2d40-a49d-4c34-94fc-eb0b50deac73")
                .put("questionText", Utils.randomRussianWords(5))
                .put("files", new JSONArray()
                        .put(new JSONObject()
                                .put("name", fileName)
                                .put("mimeType", fileMimeType)
                                .put("size", fileSize)
                                .put("fileData", fileData)
                        )).toString();

        //Отправить запрос на отправку вопроса в онлайн-приемную
        Response response = ReceptionRequests.createQuestion(user, requestBody);

        orderId = response.jsonPath().getString("id");

        //Отправить запрос на получение списка заявок в онлайн-приемную
        HashMap<String, String> queryParams = new HashMap<String, String>() {{
            put("start", "0");
        }};
        Response orderList = ReceptionRequests.getOrderList(user, queryParams);

        Assert.assertTrue(orderList.jsonPath().getList("list.id").contains(orderId));

        //Отправить запрос на получение заявки
        Response order = ReceptionRequests.getOrder(user, orderId);
        questionId = order.jsonPath().getString("parentId");

        //Отправить запрос на получение вопроса
        ReceptionRequests.getQuestion(user, questionId);
    }

    @Test(testName = "CPO-T311 Ответ на вопрос", priority = 1, dependsOnMethods = "createQuestionTest")
    public void answerQuestionTest() throws InterruptedException {
        JSONObject answerBody = new JSONObject()
                .put("answerText", Utils.randomRussianWords(10))
                .put("answerDate", OffsetDateTime.ofInstant(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset()),
                        ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES));

        //Отправить запрос по получению ответа на вопрос
        ReceptionRequests.sendAnswer(questionId, answerBody.toString());

        Thread.sleep(180000);

        //Отправить запрос на получение заявки
        Response order = ReceptionRequests.getOrder(user, orderId);
        Assert.assertEquals(order.jsonPath().getString("answer"), answerBody.get("answerText"));

        //Отправить запрос на получение вопроса
        Response question = ReceptionRequests.getQuestion(user, questionId);
        Assert.assertEquals(question.jsonPath().getString("answer.text"), answerBody.get("answerText"));
    }

    @Test(testName = "CPO-T312 Оценка ответа на вопрос", priority = 2, dependsOnMethods = "answerQuestionTest")
    public void rateAnswerTest() {
        //Отправить запрос на получение вопроса, по которому был получен ответ
        //ранее в тестах проверяли этот шаг

        //Отправить запрос на определение руководителя
        Response response = ReceptionRequests.getManager(user, new HashMap<String, String>() {{
            put("escaladeQuestionId", questionId);
        }});
        String managerId = response.body().asPrettyString();

        //Отправить запрос на доступность эскалации вопроса
        Response isCanEscalate = ReceptionRequests.isCanEscalate(user);
        Assert.assertEquals(isCanEscalate.body().asPrettyString(), "false");

        //Отправить запрос на оценку ответа без дальнейшей эскалации вопроса
        JSONObject rateBody = new JSONObject()
                .put("markValue", 5)
                .put("commentId", new JSONArray()
                        .put("80d8a59a-46e1-470c-bcbf-c6f90d7c5cfa")
                        .put("e1a8abc1-6272-458b-bb5b-fa66a8d05d18")
                        .put("9f5d3c69-301b-4a1d-9e15-16783f1f7d5a"))
                .put("otherText", Utils.randomRussianWords(5))
                .put("needEscalate", false);

        ReceptionRequests.rateAnswer(user, orderId, rateBody.toString());

        Response order = ReceptionRequests.getOrder(user, orderId);

        Assert.assertNotNull(order.jsonPath().getString("markDate"));
        Assert.assertEquals(order.jsonPath().getString("markValue"), rateBody.get("markValue"));

        //Отправить запрос на получение вопроса после проставления оценки ответу
        Response question = ReceptionRequests.getQuestion(user, questionId);

        Assert.assertNotNull(order.jsonPath().getString("answer.rate.date"));
        Assert.assertEquals(order.jsonPath().getString("answer.rate.value"), rateBody.get("markValue"));
        Assert.assertEquals(order.jsonPath().getString("answer.rate.needEscalate"), rateBody.get("needEscalate"));
        Assert.assertEquals(order.jsonPath().getString("answer.rate.otherText"), rateBody.get("otherText"));
        Assert.assertEquals(order.jsonPath().getList("answer.rate.criterionId"), rateBody.get("commentId"));
    }

    @Test(testName = "CPO-T313 Скачивание прикрепленных файлов", priority = 1, dependsOnMethods = "createQuestionTest")
    public void downloadAttachmentsTest() {
        //Отправить запрос на получение вопроса
        Response question = ReceptionRequests.getQuestion(user, questionId);

        String fileId = question.jsonPath().getString("files[0].id");

        //Отправить запрос на получение файла
        Response file = ReceptionRequests.getFile(user, fileId);

        Assert.assertEquals(file.jsonPath().getString("name"), fileName);
        Assert.assertEquals(file.jsonPath().getString("fileSize"), String.valueOf(fileSize));
        Assert.assertEquals(file.jsonPath().getString("mimeType"), fileMimeType);
        Assert.assertEquals(file.jsonPath().getString("fileData"), fileData);
    }
}
