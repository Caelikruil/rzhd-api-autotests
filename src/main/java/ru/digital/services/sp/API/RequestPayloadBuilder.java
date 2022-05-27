package ru.digital.services.sp.API;

import org.apache.http.HttpStatus;
import ru.digital.services.sp.Users.User;

import java.util.HashMap;
import java.util.Map;

public class RequestPayloadBuilder {
    private boolean withAuth = false;
    private User user;
    private String body = "";
    private Map<String, String> queryParams = new HashMap<>();
    private String routeParam;
    private int expectedCode = HttpStatus.SC_OK;

    public RequestPayloadBuilder setWithAuth() {
        this.withAuth = true;
        return this;
    }

    public RequestPayloadBuilder setUser(User user) {
        this.user = user;
        return this;
    }

    public RequestPayloadBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public RequestPayloadBuilder setQueryParams(HashMap<String, String> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    public RequestPayloadBuilder setRouteParam(String routeParam) {
        this.routeParam = routeParam;
        return this;
    }

    public RequestPayloadBuilder setExpectedCode(int expectedCode) {
        this.expectedCode = expectedCode;
        return this;
    }

    public RequestPayload build() {
        return new RequestPayload(
                withAuth, user, body, queryParams, routeParam, expectedCode);
    }

    public static RequestPayloadBuilder builder() {
        return new RequestPayloadBuilder();
    }
}
