package ru.digital.services.sp.API;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CheckAuthTestAnnotation {
    //Флаг того, что метод можно проверять в тестах на авторизацию
    //используется просто, навешиваете на метод, который хотите тестировать
    //важно, сигнатура вызова у метода должна быть (RequestPayload payload)
}
