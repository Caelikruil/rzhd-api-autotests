package ru.digital.services.sp.API.VTTRequests;

import io.restassured.response.Response;
import ru.digital.services.sp.API.CheckAuthTestAnnotation;
import ru.digital.services.sp.API.RequestPayload;
import ru.digital.services.sp.Dictionaries.WebPlatforms;
import ru.digital.services.sp.Users.User;

import java.util.Map;

import static ru.digital.services.sp.API.BaseApi.*;

public class VTTIntRequests {

    //Базовый путь сервиса vtt-int
    private static final String vttIntPath = "api/vttint/v1/";

    @CheckAuthTestAnnotation
    public static void changeResidenceToWorkVttStatus(RequestPayload payload) {
        postIntRequest(vttIntPath + "orderFromResidenceToWorkStatus", payload);
    }

    @CheckAuthTestAnnotation
    public static void changeVttStatus(RequestPayload payload) {
        postIntRequest(vttIntPath, payload);
    }

    @CheckAuthTestAnnotation
    public static void addExternalVtt(RequestPayload payload) {
        postIntRequest(vttIntPath + "vttExt", payload);
    }
}
