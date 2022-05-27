package ru.digital.services.sp.API;

import org.apache.http.HttpStatus;
import ru.digital.services.sp.Users.User;

import java.util.HashMap;
import java.util.Map;

public class RequestPayload {

    public boolean withAuth;
    public User user;
    public String body;
    public Map<String, String> queryParams;
    public String routeParam;
    public int expectedCode;

    public RequestPayload(boolean withAuth,
                          User user,
                          String body,
                          Map<String, String> queryParams,
                          String routeParam,
                          int expectedCode) {
        this.withAuth = withAuth;
        this.user = user;
        this.body = body;
        this.queryParams = queryParams;
        this.routeParam = routeParam;
        this.expectedCode = expectedCode;
    }

    public static RequestPayload getPayloadWithoutAuth() {
        return new RequestPayload(
                false, null, "", new HashMap<>(), null, HttpStatus.SC_FORBIDDEN);
    }

}

