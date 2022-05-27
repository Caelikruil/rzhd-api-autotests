package ru.digital.services.sp.Utils;

import ru.digital.services.sp.API.EducationRequests.EducationRequests;
import ru.digital.services.sp.Users.User;

import java.util.LinkedHashMap;
import java.util.List;

public class EducationUtils {
    public static LinkedHashMap getUserCourse(String courseId, User user) {
        //Запросить список доступных курсов
        List<LinkedHashMap> courses = EducationRequests.getUserAssignedCourseList(user)
                .jsonPath().getList("list");

        return courses.stream().filter(linkedHashMap -> linkedHashMap.get("courseId")
                .equals(courseId)).findFirst().orElse(null);
    }
}
