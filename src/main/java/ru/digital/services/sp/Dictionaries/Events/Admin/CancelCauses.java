package ru.digital.services.sp.Dictionaries.Events.Admin;

//Причина снятия события с публикации
public enum CancelCauses {
    //подобное событие уже опубликовано
    SIMILAR_ALREADY_PUBLISHED,
    //событие содержит ошибку
    ERROR,
    //событие отменено организатором
    CANCELED,
    //другое
    OTHER
}
