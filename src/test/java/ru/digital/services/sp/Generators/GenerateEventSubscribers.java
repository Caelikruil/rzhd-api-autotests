package ru.digital.services.sp.Generators;

import org.testng.annotations.Test;
import ru.digital.services.sp.Utils.EventsUtils;

public class GenerateEventSubscribers {

    //Создает для указанного в eventId события userCount участников путем выборки данных из таблицы event_employee
    @Test
    public void generateEventSubscriber()
    {
        String eventId = "07015cb3-8d4c-41d6-a9c5-1cfaf3d30c04";
        int userCount = 10;
        EventsUtils.insertRandomEventParticipant(eventId, userCount);
    }
}
