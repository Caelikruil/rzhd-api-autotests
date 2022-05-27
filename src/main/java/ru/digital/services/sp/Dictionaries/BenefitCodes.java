package ru.digital.services.sp.Dictionaries;

// Номер льготы
public enum BenefitCodes {

    //Санаторно-курортное лечение
    SanKur(1),
    //Детский отдых
    ChildRest(2),
    //Бытовое топливо
    Drovishki(3),
    //Спортивные абонементы
    Sport(4),
    //Негосударственное пенсионное обеспечение
    Pension(5),
    //Льготный проезд
    Travel(6),
    //Жилищная политика
    HousingPolicy(7),
    //Компенсируемый социальный пакет
    CSP(8);

    private int value;

    private BenefitCodes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

