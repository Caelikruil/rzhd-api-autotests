package ru.digital.services.sp.Education;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.EducationRequests.EducationRequests;
import ru.digital.services.sp.Utils.EducationUtils;
import ru.digital.services.sp.Dictionaries.EducationCourseStatuses;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import java.util.LinkedHashMap;

public class FullCourseFlowWithoutDateTests {

    private String courseId = Long.toString(Math.round(Math.random() * 100000));
    private User user = Users.getUser("02022035663");

    @Test(priority = 1)
    //Добавление курса СДО пользователю
    public void
    добавление_курса_СДО_пользователю() throws InterruptedException {
        //Формируем тело запроса
        JSONArray requestBody = new JSONArray().put(new JSONObject()
                .put("courseId", courseId)                      //уникальный ИД курса
                .put("snils", user.snils)                       //снилс пользователя
                .put("title", "Автотестовый курс " + courseId));  //название курса

        //Назначаем новый курс пользователю
        EducationRequests.sendCourseFromSDOtoSP(requestBody.toString(), HttpStatus.SC_OK);

        //Подождать, есть временной лаг на обработку данных
        Thread.sleep(40000);

        LinkedHashMap course = EducationUtils.getUserCourse(courseId, user);

        //курс присутствует в списке назначенных
        Assert.assertNotNull(course);
        Assert.assertEquals(course.get("status"), EducationCourseStatuses.IN_PROGRESS.getValue());
    }

    @Test(priority = 2, dependsOnMethods = {"добавление_курса_СДО_пользователю"})
    public void получение_события_об_успешном_завершении_курса_для_пользователя() throws InterruptedException {
        String requestBody = new JSONArray()
                .put(new JSONObject()
                        .put("courseId", courseId)
                        .put("snils", user.snils)
                        .put("status", EducationCourseStatuses.FINISHED)
                ).toString();

        //проставляем статус "Завершен"
        EducationRequests.setCourseStatus(requestBody, HttpStatus.SC_OK);

        //Подождать, есть временной лаг на обработку данных
        Thread.sleep(40000);

        LinkedHashMap course = EducationUtils.getUserCourse(courseId, user);

        Assert.assertNotNull(course);
        Assert.assertEquals(course.get("status").toString(), (EducationCourseStatuses.FINISHED.getValue()));
    }

    @Test(priority = 3, dependsOnMethods = {"получение_события_об_успешном_завершении_курса_для_пользователя"})
    public void получение_события_об_отмене_курса_для_пользователя() throws InterruptedException {
        String requestBody = new JSONArray()
                .put(new JSONObject()
                        .put("courseId", courseId)
                        .put("snils", user.snils)
                        .put("status", EducationCourseStatuses.REVOKED)
                ).toString();

        //проставляем статус "Отменен"
        EducationRequests.setCourseStatus(requestBody, HttpStatus.SC_OK);

        //Подождать, есть временной лаг на обработку данных
        Thread.sleep(40000);

        LinkedHashMap course = EducationUtils.getUserCourse(courseId, user);

        Assert.assertNull(course);
    }
}
