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

import java.util.HashMap;

public class AskQuestionWithEscalationTests {

    private User user = Users.getUser("11994683915");
    //Todo("Вынести в User")
    private String usersManager = "e9f9839c-afb2-4639-bf8c-2b8797fa0eb8";

    private String fileName = "dots5x5.jpg";
    private String fileMimeType = "image/jpeg";
    private int fileSize = 796;
    private String fileData = Utils.getBase64ImageDate();

    @Test(testName = "CPO-T321 Эскалация вопроса вышестоящему руководителю")
    public void escalateQuestionTest() {
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

        String orderId = response.jsonPath().getString("id");

        //Отправить запрос на получение заявки
        Response order = ReceptionRequests.getOrder(user, orderId);
        String questionId = order.jsonPath().getString("parentId");
        String questionNumber = order.jsonPath().getString("questionNumber");

        //Отправить запрос на получение вопроса
        ReceptionRequests.getQuestion(user, questionId);

        //Отправить запрос на доступность эскалации вопроса
        Response isCanEscalate = ReceptionRequests.isCanEscalate(user);
        Assert.assertEquals(isCanEscalate.body().asPrettyString(), "true");

        //Отправить запрос на определение руководителя
        HashMap<String, String> queryParams = new HashMap<String, String>() {{
            put("escaladeQuestionId", questionId);
        }};
        Response manager = ReceptionRequests.getManager(user, queryParams);

        Assert.assertEquals(manager.body().asPrettyString(), usersManager,
                "Руководитель пользователя отличается от ожидаемого");

        //Отправить запрос на оценку ответа без дальнейшей эскалации вопроса
        JSONObject rateBody = new JSONObject()
                .put("markValue", 2)
                .put("commentId", new JSONArray()
                        .put("9ac9a528-95c0-4946-ad16-bce8d941b0d3"))
                .put("otherText", Utils.randomRussianWords(5))
                .put("needEscalate", true);

        ReceptionRequests.rateAnswer(user, orderId, rateBody.toString());

        //Отправить запрос на получение заявки
        Response question = ReceptionRequests.getQuestion(user, questionId);

        Assert.assertEquals(question.jsonPath().getString("questionNumber"), questionNumber+"-2");
    }

}
