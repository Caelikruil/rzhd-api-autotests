package ru.digital.services.sp.Education;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.EducationRequests.EducationRequests;
import ru.digital.services.sp.Utils.EducationUtils;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import java.util.LinkedHashMap;

public class ReceiveCourseValidationTests {

    private User user = Users.getUser("02022035663");

    @DataProvider(name = "Невалидные снилсы")
    public Object[][] invalidSnils() {
        return new Object[][]{
                {"0202203566"},   //короткий
                {"020220356633"}, //длинный
                {"00000000001"},  //несуществующий
                {"020-220-356-63"}//с лишними символами
        };
    }

    @Test(testName = "Проверка валидации снилс на длину при получении курса", dataProvider = "Невалидные снилсы")
    public void check_course_snils_test(String invalidSnils) {
        String courseId = Long.toString(Math.round(Math.random() * 1000000));
        //Формируем тело запроса
        JSONArray requestBody = new JSONArray().put(new JSONObject()
                .put("courseId", courseId)                          //уникальный ИД курса
                .put("snils", invalidSnils)                         //снилс пользователя
                .put("title", "Автотестовый курс " + courseId));    //название курса

        //Назначаем новый курс пользователю
        Response response = EducationRequests.sendCourseFromSDOtoSP(requestBody.toString(),
                HttpStatus.SC_UNPROCESSABLE_ENTITY);
        Assert.assertTrue(response.jsonPath().getList("snils").contains(invalidSnils),
                "В списке невалидных курсов отсутствует ожидаемый");
    }


    @Test(testName = "Проверка валидации снилсов на валидность при получении списка курсов")
    public void check_multiple_courses_test() throws InterruptedException {
        String courseId = Long.toString(Math.round(Math.random() * 1000000));

        //Формируем тело запроса
        JSONArray requestBody = new JSONArray().put(new JSONObject() //валидный курс
                        .put("courseId", courseId)                        //уникальный ИД курса
                        .put("snils", user.snils)                         //снилс пользователя
                        .put("title", "Автотестовый курс " + courseId))   //название курса
                .put(new JSONObject()                                //невалидный курс
                        .put("courseId", "12345")
                        .put("snils", "020220356") //невалидный снилс
                        .put("title", "Невалидный автотестовый курс")
                );


        //Назначаем новый курс пользователю
        Response response = EducationRequests.sendCourseFromSDOtoSP(requestBody.toString(),
                HttpStatus.SC_UNPROCESSABLE_ENTITY);

        Assert.assertTrue(response.jsonPath().getList("snils").contains("020220356"));

        //Подождать, есть временной лаг на обработку данных
        Thread.sleep(40000);

        LinkedHashMap course = EducationUtils.getUserCourse(courseId, user);

        //курс присутствует в списке назначенных
        Assert.assertNotNull(course, "Курс не найден в списке назначенных");
    }
}
