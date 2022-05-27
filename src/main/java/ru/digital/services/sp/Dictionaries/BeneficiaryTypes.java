package ru.digital.services.sp.Dictionaries;

//Тип льготника
public enum BeneficiaryTypes {
    //Работник
    employee(1),

    //Иждивенец
    dependent(2);

    private int value;

    private BeneficiaryTypes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
