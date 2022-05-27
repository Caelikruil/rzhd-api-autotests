package ru.digital.services.sp.VTTService;

import org.testng.Assert;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.CheckAuthTestAnnotation;
import ru.digital.services.sp.API.RequestPayload;
import ru.digital.services.sp.API.VTTRequests.VTTIntRequests;
import ru.digital.services.sp.API.VTTRequests.VTTRequests;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ContractTests {
    private static RequestPayload payloadWithoutAuth = RequestPayload.getPayloadWithoutAuth();

    @Test(testName = "Проверка работы авторизации методов сервиса VTT")
    public void checkVttMethodsAuth() {

        List<String> failedMethods = new ArrayList<>();

        Method[] methods = VTTRequests.class.getMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(CheckAuthTestAnnotation.class)) {
                try {
                    System.out.println(m.getName());
                    m.invoke(m, payloadWithoutAuth);
                } catch (Exception e) {
                    failedMethods.add(m.getName() + " " + e.getLocalizedMessage());
                }
            }
        }

        Assert.assertTrue(failedMethods.isEmpty(),
                "Не все методы прошли проверку на авторизацию, а именно: \n" + String.join(";\n", failedMethods));
    }

    @Test(testName = "Проверка работы авторизации методов сервиса VTT-Int")
    public void checkVttIntMethodsAuth() {

        List<String> failedMethods = new ArrayList<>();

        Method[] methods = VTTIntRequests.class.getMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(CheckAuthTestAnnotation.class)) {
                try {
                    System.out.println(m.getName());
                    m.invoke(m, payloadWithoutAuth);
                } catch (Throwable e) {
                    failedMethods.add(m.getName() + " " + e.getMessage());
                }
            }
        }
        Assert.assertTrue(failedMethods.isEmpty(),
                "Не все методы прошли проверку на авторизацию, а именно: \n" + String.join(";\n", failedMethods));
    }
}
