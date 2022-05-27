package ru.digital.services.sp.Dictionaries;

//Справочник статусов курсов интеграции с новым СДО сервиса education
public enum EducationCourseStatuses {

    //курс отменен для пользователя
    REVOKED("0"),
    //пользователь завершил курс
    FINISHED("34"),
    //дефолтный статус, курс в процессе
    IN_PROGRESS("33");

    private String value;

    private EducationCourseStatuses(String value) {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}

