package ru.digital.services.sp.API.VTTRequests;

import io.restassured.response.Response;
import ru.digital.services.sp.API.CheckAuthTestAnnotation;
import ru.digital.services.sp.API.RequestPayload;

import static ru.digital.services.sp.API.BaseApi.*;

public class VTTRequests {

    //Базовый путь сервиса vtt
    private static final String vttPath = "api/vtt/v1/";

    //?platform=
    @CheckAuthTestAnnotation
    public static Response isAccess(RequestPayload payload) {
        return getUserRequest(vttPath + "isAccess", payload);
    }

    @CheckAuthTestAnnotation
    public static Response createVtt(RequestPayload payload) {
        return postUserRequest(vttPath, payload);
    }

    @CheckAuthTestAnnotation
    public static void createVttFromResidenceToWork(RequestPayload payload) {
        postUserRequest(vttPath + "orderFromResidenceToWork", payload);
    }

    @CheckAuthTestAnnotation
    public static Response getVttList(RequestPayload payload) {
        return getUserRequest(vttPath, payload);
    }

    @CheckAuthTestAnnotation
    public static Response getVtt(RequestPayload payload) {
        return getUserRequest(vttPath + payload.routeParam, payload);
    }

    @CheckAuthTestAnnotation
    public static Response getFromResidenceToWorkVtt(RequestPayload payload) {
        return getUserRequest(vttPath + "orderFromResidenceToWork/" + payload.routeParam,
                payload);
    }

    @CheckAuthTestAnnotation
    public static Response deleteVtt(RequestPayload payload) {
        return deleteUserRequest(vttPath + payload.routeParam, payload);
    }

    @CheckAuthTestAnnotation
    public static void deleteFromResidenceToWorkVtt(RequestPayload payload) {
        deleteUserRequest(vttPath + "orderFromResidenceToWork/" + payload.routeParam, payload);
    }

    @CheckAuthTestAnnotation
    public static Response updateVtt(RequestPayload payload) {
        return putUserRequest(vttPath, payload);
    }

    @CheckAuthTestAnnotation
    public static Response getFile(RequestPayload payload) {
        return getUserRequest(vttPath + "file/" + payload.routeParam, payload);
    }

    @CheckAuthTestAnnotation
    public static Response changeVTTStatus(RequestPayload payload) {
        return putUserRequest(vttPath + "status/", payload);
    }

    @CheckAuthTestAnnotation
    public static Response getStations(RequestPayload payload) {
        return getUserRequest(vttPath + "stations", payload);
    }
}
