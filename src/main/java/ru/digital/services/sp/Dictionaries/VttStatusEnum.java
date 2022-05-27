package ru.digital.services.sp.Dictionaries;

public enum VttStatusEnum {
    /**
     * Черновик.
     */
    _01("01", "Черновик"),
    /**
     * Новая заявка.
     */
    _02("02", "Новая заявка"),
    /**
     * В работе.
     */
    _03("03", "В работе"),
    /**
     * Выполнено.
     */
    _20("20", "Выполнено"),
    /**
     * Отклонено.
     */
    _30("30", "Отклонено"),
    /**
     * Автоматическая обработка.
     */
    _50("50", "Автоматическая обработка"),
    /**
     * Отмена.
     */
    _100("100", "Отмена");

    /**
     * Идентификатор статуса заявки.
     */
    private String id;
    /**
     * Наименование статуса.
     */
    private String name;

    /**
     * @param idd   Идентификатор статуса заявки
     * @param namee Наименование статуса
     */
    VttStatusEnum(final String idd, final String namee) {
        this.id = idd;
        this.name = namee;
    }

    public String getId() {
        return this.id;
    }

    /**
     * @param id Идентификатор статуса заявки
     * @return статус
     */
    public static VttStatusEnum getById(final String id) {
        for (VttStatusEnum e : VttStatusEnum.values()) {
            if (e.getId().equals(id)) {
                return e;
            }
        }
        return null;
    }

}
