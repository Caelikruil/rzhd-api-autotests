package ru.digital.services.sp.API.HousingPolicyRequests;

import io.restassured.response.Response;
import ru.digital.services.sp.API.RequestPayload;

import static ru.digital.services.sp.API.BaseApi.*;

public class HousingPolicyRequests {

    //Базовый путь сервиса housing-policy
    private static final String housingPath = "api/housingpolicy/v1/";

    //Метод получения словарей по его типу
    //dictonaryCode - тип словаря из справочника словарей
    public static Response getHousingPolicyDictionary(RequestPayload payload) {
        return getUserRequest(housingPath + "getDictElements",
                payload);
    }

    //Получение заявки на учет в Льготной ипотеке
    public static Response getApplicationRequest(RequestPayload payload) {
        return getUserRequest(housingPath + "appRegistration",
                payload);
    }


    //Подать заявление на учет в Льготной ипотеке
    public static void createApplicationRequest(RequestPayload payload) {
        postUserRequest(housingPath + "applicationRegistration",
                payload);
    }
}
