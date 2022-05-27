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
import ru.digital.services.sp.Dictionaries.EducationCourseStatuses;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import java.util.LinkedHashMap;

public class ChangeCourseStatusValidationTests {

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

    @Test(testName = "Проверка валидации снилс на длину при получении статуса курса", dataProvider = "Невалидные снилсы")
    public void change_course_status_snils_validation_test(String invalidSnils) {
        String courseId = Long.toString(Math.round(Math.random() * 1000000));
        //Формируем тело запроса
        String requestBody = new JSONArray()
                .put(new JSONObject()
                        .put("courseId", courseId)
                        .put("snils", invalidSnils)
                        .put("status", EducationCourseStatuses.FINISHED)
                ).toString();

        //Назначаем новый курс пользователю
        Response response = EducationRequests.sendCourseFromSDOtoSP(requestBody,
                HttpStatus.SC_UNPROCESSABLE_ENTITY);
        Assert.assertTrue(response.jsonPath().getList("snils").contains(invalidSnils),
                "В списке невалидных курсов отсутствует ожидаемый");
    }


    @Test(testName = "Проверка валидации снилсов на валидность при получении списка курсов")
    public void check_multiple_courses_test() throws InterruptedException {
        String courseId = Long.toString(Math.round(Math.random() * 1000000));

        //Формируем тело запроса
        JSONArray requestBody = new JSONArray().put(new JSONObject()
                .put("courseId", courseId)                      //уникальный ИД курса
                .put("snils", user.snils)                       //снилс пользователя
                .put("title", "Автотестовый курс " + courseId));   //название курса

        //Назначаем новый курс пользователю
        EducationRequests.sendCourseFromSDOtoSP(requestBody.toString(), HttpStatus.SC_OK);

        Thread.sleep(40000);

        LinkedHashMap course = EducationUtils.getUserCourse(courseId, user);

        Assert.assertNotNull(course);
        Assert.assertEquals(course.get("status").toString(), EducationCourseStatuses.FINISHED.getValue());

        String statusRequestBody = new JSONArray()
                .put(new JSONObject()
                        .put("courseId", courseId)
                        .put("snils", user.snils)
                        .put("status", EducationCourseStatuses.FINISHED)
                )
                .put(new JSONObject()
                        .put("courseId", "12345")
                        .put("snils", "12345")
                        .put("status", EducationCourseStatuses.FINISHED)
                ).toString();

        //проставляем статус "Завершен"
        Response response = EducationRequests.setCourseStatus(statusRequestBody, HttpStatus.SC_UNPROCESSABLE_ENTITY);

        Assert.assertTrue(response.jsonPath().getList("snils").contains("12345"));

        Thread.sleep(40000);

        course = EducationUtils.getUserCourse(courseId, user);

        Assert.assertNotNull(course);
        Assert.assertEquals(course.get("status").toString(), EducationCourseStatuses.FINISHED.getValue());
    }
}
