package ru.digital.services.sp.API.HousingPolicyRequests;

import ru.digital.services.sp.API.NewBaseApi;

public class NewHousingPolicyRequests extends NewBaseApi {

    public static String createApplicationUrl = "applicationRegistration";
    public static String getDictUrl = "getDictElements";
    public static String getApplicationRequestsUrl = "appRegistrationList";
    public static String getApplicationUrl = "appRegistration";
    public static String employeeCategoryDictUrl = "dictEmployeeCategory";
    public static String NormSquareMetersDictUrl = "dictNormsSquareMetersLivingSpace";
    public static String queuePositionUrl = "queuePosition";

    //Базовый путь сервиса housing-policy
    private final String basePath = "api/housingpolicy/v1/";
    private final String baseIntPath = "api/housingpolicy-int/v1/";

    public String getBasePath() {
        return this.basePath;
    }

    public String getBaseIntPath() {
        return this.baseIntPath;
    }
}

