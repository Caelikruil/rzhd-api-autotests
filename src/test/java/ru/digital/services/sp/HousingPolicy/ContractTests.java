package ru.digital.services.sp.HousingPolicy;

import org.testng.annotations.Test;
import ru.digital.services.sp.API.HousingPolicyRequests.HousingPolicyRequests;
import ru.digital.services.sp.API.RequestPayload;

//ToDo экспериментальный пример, обдумать
public class ContractTests {
    @Test
    public void checkDictUnauthorized() {
        //потому что у нас херовое апи, вместо 401 ждем 403)
        HousingPolicyRequests.getHousingPolicyDictionary(RequestPayload.getPayloadWithoutAuth());
    }
}
