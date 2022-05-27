package ru.digital.services.sp.Dictionaries.Events;

//Часовой пояс проведения события
public enum EventTimeZones {
    //Время события указано по МСК
    //00:00Z в такой записи подразумевает 03:00+3, время передается только в UTC
    MSK,
    //Время указано с реальным часовым поясом события
    LOCAL
}
