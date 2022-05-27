package ru.digital.services.sp.Education;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.EducationRequests.EducationRequests;
import ru.digital.services.sp.Utils.EducationUtils;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;

import java.time.LocalDate;
import java.util.LinkedHashMap;

public class ChangeCourseDateTests {

    private String courseWithDateId = Long.toString(Math.round(Math.random() * 100000));
    private String courseWithDate2Id = Long.toString(Math.round(Math.random() * 100000));
    private String courseWithoutDateId = Long.toString(Math.round(Math.random() * 100000));
    private User user = Users.getUser("02022035663");
    private LocalDate courseDate = LocalDate.now().plusDays(14);
    private LocalDate newDate = courseDate.plusDays(7);

    @BeforeClass
    public void generateCourses() throws InterruptedException {
        //Формируем тело запроса
        String requestBody = new JSONArray()
                .put(new JSONObject()
                        .put("courseId", courseWithDateId)              //уникальный ИД курса
                        .put("snils", user.snils)                       //снилс пользователя
                        .put("endDate", courseDate)                     //Дата завершения курса
                        .put("title", "Автотестовый курс d1 " + courseWithDateId))  //название курса
                .put(new JSONObject()
                        .put("courseId", courseWithDate2Id)              //уникальный ИД курса
                        .put("snils", user.snils)                       //снилс пользователя
                        .put("endDate", courseDate)                     //Дата завершения курса
                        .put("title", "Автотестовый курс d2 " + courseWithDate2Id))  //название курса
                .put(new JSONObject()
                        .put("courseId", courseWithoutDateId)              //уникальный ИД курса
                        .put("snils", user.snils)                       //снилс пользователя
                        .put("title", "Автотестовый курс wd " + courseWithoutDateId))  //название курса
                        .toString();

        //Назначаем новый курс пользователю
        EducationRequests.sendCourseFromSDOtoSP(requestBody, HttpStatus.SC_OK);

        //дождемся применения изменений
        Thread.sleep(30000);

        //отредактируем даты заранее, чтобы сократить время выполнения тестов
        String updateBody = new JSONArray().put(new JSONObject()
                        .put("courseId", courseWithoutDateId)                      //уникальный ИД курса
                        .put("snils", user.snils)                       //снилс пользователя
                        .put("title", "Автотестовый курс " + courseWithoutDateId)    //название курса
                        .put("endDate", courseDate))                         //Дата завершения курса
                .put(new JSONObject()
                        .put("courseId", courseWithDateId)                      //уникальный ИД курса
                        .put("snils", user.snils)                       //снилс пользователя
                        .put("title", "Автотестовый курс " + courseWithDateId))   //название курса
                .put(new JSONObject()
                        .put("courseId", courseWithDate2Id)                      //уникальный ИД курса
                        .put("snils", user.snils)                               //снилс пользователя
                        .put("title", "Автотестовый курс " + courseWithDate2Id)    //название курса
                        .put("endDate", newDate))                       //Дата завершения курса
                        .toString();

        //Назначаем новый курс пользователю
        EducationRequests.sendCourseFromSDOtoSP(updateBody, HttpStatus.SC_OK);

        //Подождать, есть временной лаг на обработку данных
        Thread.sleep(30000);
    }

    @Test(testName = "перевод бессрочного курса в курс с датой окончания")
    public void changeFromNullToValue() {
        LinkedHashMap course = EducationUtils.getUserCourse(courseWithoutDateId, user);

        //Проверим, что у курса изменилась дата окончания
        Assert.assertEquals(course.get("endDate").toString(), courseDate.toString());
    }

    @Test(testName = "перевод курса в бессрочный курс")
    public void changeFromValueToNull() {
        LinkedHashMap course = EducationUtils.getUserCourse(courseWithDateId, user);

        //Проверим, что у курса изменилась дата окончания
        Assert.assertNull(course.get("endDate"));
    }

    @Test(testName = "измение даты у события с датой")
    public void changeFromValueToValue() {
        LinkedHashMap course = EducationUtils.getUserCourse(courseWithDate2Id, user);

        //Проверим, что у курса изменилась дата окончания
        Assert.assertEquals(course.get("endDate").toString(), newDate.toString());
    }
}
