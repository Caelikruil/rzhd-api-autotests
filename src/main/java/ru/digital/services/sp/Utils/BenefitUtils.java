package ru.digital.services.sp.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.digital.services.sp.API.BenefitRequests.BenefitRequests;
import ru.digital.services.sp.Dictionaries.BeneficiaryTypes;
import ru.digital.services.sp.Dictionaries.BenefitCodes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BenefitUtils {

    public static JSONObject GenerateResponseBenefits(String requestId) {

        JSONArray benefitBody = new JSONArray();
        for (BenefitCodes val : BenefitCodes.values()) {

            JSONObject entry = new JSONObject()
                    .put("code", val.getValue())
                    .put("year", LocalDate.now().getYear())
                    .put("sumComp", Math.round(Math.random() * 10000))
                    .put("sumPart", Math.round(Math.random() * 1000))
                    .put("benefType", BeneficiaryTypes.employee.getValue());

            if (val == BenefitCodes.Travel) {
                //Добавляем информацию о типе ВТТ и станциях + дата
                entry.put("formTicket", "Ф.6В");
                entry.put("fromStation", "Зуево");
                entry.put("toStation", "Кукуево");
                entry.put("benefDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                //копируем компенсацию для того чтобы создать ВТТ на иждивенца
                JSONObject dependentEntry = new JSONObject(entry, JSONObject.getNames(entry));
                //указываем компенсации что она на иждивенца
                dependentEntry.put("benefType", BeneficiaryTypes.dependent.getValue());
                //добавляем компенсацию в общий массив компенсаций
                benefitBody.put(dependentEntry);
            }

            if (val == BenefitCodes.SanKur) {
                JSONObject dependentEntry = new JSONObject(entry, JSONObject.getNames(entry));
                dependentEntry.put("benefType", BeneficiaryTypes.dependent.getValue());
                benefitBody.put(dependentEntry);
            }

            benefitBody.put(entry);
        }

        return new JSONObject()
                .put("uuid", requestId)
                .put("benefit", benefitBody);
    }
}
