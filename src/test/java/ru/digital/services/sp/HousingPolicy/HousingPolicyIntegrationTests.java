package ru.digital.services.sp.HousingPolicy;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.digital.services.sp.API.HousingPolicyRequests.HousingPolicyIntRequests;
import ru.digital.services.sp.API.HousingPolicyRequests.NewHousingPolicyRequests;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.HousingPolicyUtils;
import ru.digital.services.sp.Dictionaries.housingpolicy.HousingPolicyDictionaryCodes;
import ru.digital.services.sp.Utils.Utils;

import java.time.LocalDateTime;
import java.time.temporal.IsoFields;
import java.util.UUID;

public class HousingPolicyIntegrationTests {
    private NewHousingPolicyRequests HPHttpSender = new NewHousingPolicyRequests();
    private User user = Users.getUser("02022035663");

    //Список параметров для проверки метода добавления нормы м2 жилья на человека
    @DataProvider(name = "SquareMetersPerPersonParams")
    public Object[][] squareMetersPerPersonParams() {
        return new Object[][]{
                //позитивный сценарий
                {
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().get(IsoFields.QUARTER_OF_YEAR),
                        Utils.randomAlphaNumericRU(10)
                },
                //сценарий добавления скрытой от пользователя нормы по кварталу
                {
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().get(IsoFields.QUARTER_OF_YEAR) == 4
                                ? 2 : LocalDateTime.now().get(IsoFields.QUARTER_OF_YEAR) + 1,
                        Utils.randomAlphaNumericRU(10)
                },
                //сценарий добавления скрытой от пользователя нормы по году
                {
                        LocalDateTime.now().getYear() + 1,
                        LocalDateTime.now().get(IsoFields.QUARTER_OF_YEAR),
                        Utils.randomAlphaNumericRU(10)
                },
                //сценарий добавления нормы по области, скрытой от пользователя
                {
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().get(IsoFields.QUARTER_OF_YEAR),
                        ""
                }
        };
    }

    //Список параметров для проверки метода добавления нормы стоимости м2 жилья в регионе
    @DataProvider(name = "SquareMetersPricesParams")
    public Object[][] squareMetersPricesParams() {
        return new Object[][]{
                //позитивный сценарий
                {
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().get(IsoFields.QUARTER_OF_YEAR)
                },
                {
                        LocalDateTime.now().getYear(),
                        LocalDateTime.now().get(IsoFields.QUARTER_OF_YEAR) == 4
                                ? 2 : LocalDateTime.now().get(IsoFields.QUARTER_OF_YEAR) + 1,
                },
                {
                        LocalDateTime.now().getYear() + 1,
                        LocalDateTime.now().get(IsoFields.QUARTER_OF_YEAR)
                }
        };
    }

    @Test(dataProvider = "SquareMetersPerPersonParams")
    public void
    добавление_нормы_квадратных_метров_жилья_на_человека(int year, int quarter,
                                                         String settlementName) {
        String uniqueId = UUID.randomUUID().toString();

        //Формируем тело запроса
        JSONArray requestBody = new JSONArray().put(new JSONObject()
                .put("id", uniqueId)                    //уникальный ИД записи
                .put("year", String.valueOf(year))      //год действия записи
                .put("quarter", String.valueOf(quarter))//квартал действия записи
                .put("regionCode", "56")                //Код региона РФ
                .put("nameSettlement", settlementName)  //наименование поселения
                .put("norm", 100.99));                  //Норма площади

        //Добавляем новую норму
        HousingPolicyIntRequests.addSquareMetersNorm(requestBody.toString());

        //Проверим наличие добавленной нормы в словаре
        //ToDo: расширить проверку на то, что данные записались корректно
        assert HousingPolicyUtils.isPresentInDictionary(user,
                HousingPolicyDictionaryCodes.NORMS_SQUARE_METERS_LIVING_SPACE, uniqueId);

        //Удалим переданное значение
        HousingPolicyIntRequests.deleteSquareMetersNorm(uniqueId);

        //Проверим наличие добавленной нормы в словаре
        assert HousingPolicyUtils.isPresentInDictionary(user,
                HousingPolicyDictionaryCodes.NORMS_SQUARE_METERS_LIVING_SPACE, uniqueId) == false;
    }

    @Test(dataProvider = "SquareMetersPricesParams")
    public void добавление_нормы_стоимости_квадратного_метра_в_регионе(int year, int quarter) {
        String uniqueId = UUID.randomUUID().toString();

        //Формируем тело запроса
        JSONArray requestBody = new JSONArray().put(new JSONObject()
                .put("id", uniqueId)                                    //уникальный ИД записи
                .put("year", String.valueOf(year))                      //год действия записи
                .put("quarter", String.valueOf(quarter))                //квартал действия записи
                .put("regionCode", "56")                                //Код региона РФ
                .put("costMeter", Math.round(Math.random() * 10000)));    //Норма площади

        //Добавляем новую цену
        HousingPolicyIntRequests.addSquareMetersCost(requestBody.toString());

        //Проверяем ее наличие в базе
        //select count(*) from housing_square_price where id = uniqueId;  == 1

        //Удалим переданное значение
        HousingPolicyIntRequests.deleteSquareMetersNorm(uniqueId);

        //Проверим что данные удалились из БД
        //select count(*) from housing_square_price where id = uniqueId;  == 0
    }

    @Test
    public void добавление_профессии_доступной_для_использования_льготной_ипотеки() {
        String direction = Utils.randomAlphaNumericEN(15);
        String professionCode = Utils.randomAlphaNumericEN(15);
        String professionName = Utils.randomRussianWords(2);
        String searchedValue = "[" + professionCode + ", " + professionName + ", " + direction + "]";

        String requestBody = new JSONArray().put(new JSONObject().put("code", professionCode)
                .put("name", professionName)).toString();

        //добавим профессию в словарь
        HousingPolicyIntRequests.addApprovedProfessions(direction, requestBody);

        //проверим ее наличие
        Assert.assertTrue(
                HousingPolicyUtils.isPresentInDictionaryByString(user,
                        HousingPolicyDictionaryCodes.APPROVED_PROFESSIONS, searchedValue));

        //удалим ее
        HousingPolicyIntRequests.deleteApprovedProfession(direction, requestBody);

        //проверим ее отсутствие
        Assert.assertFalse(
                HousingPolicyUtils.isPresentInDictionaryByString(user,
                        HousingPolicyDictionaryCodes.APPROVED_PROFESSIONS, searchedValue));
    }

    @Test
    public void удаление_связанных_профессий_и_связей_по_удалению_дирекции() {
        String directionCode = Utils.randomAlphaNumericEN(15);
        String directionName = Utils.randomRussianWords(2);

        String professionCode = Utils.randomAlphaNumericEN(15);
        String professionName = Utils.randomRussianWords(2);

        String orgCode = Long.toString(Math.round(Math.random() * 10000000));

        //добавим профессию, дирекцию и связь в словарь
        String directionBody = new JSONArray()
                .put(
                        new JSONObject()
                                .put("code", directionCode)
                                .put("name", directionName)
                ).toString();
        HousingPolicyIntRequests.addDirections(directionBody);

        String directionOrgRelationBody = new JSONArray()
                .put(
                        new JSONObject().put("code", orgCode)
                ).toString();
        HousingPolicyIntRequests.addDirectionOrgRelations(directionCode, directionOrgRelationBody);

        String professionBody = new JSONArray()
                .put(
                        new JSONObject().put("code", professionCode)
                                .put("name", professionName)
                ).toString();
        HousingPolicyIntRequests.addApprovedProfessions(directionCode, professionBody);

        //проверим их наличие
        String directionValue = "[" + directionCode + ", " + directionName + "]";
        String directionOrgRelationValue = "[" + directionCode + ", " + orgCode + "]";
        String approvedProfessionValue = "[" + professionCode + ", " + professionName + ", " + directionCode + "]";

        Assert.assertTrue(
                HousingPolicyUtils.isPresentInDictionaryByString(user,
                        HousingPolicyDictionaryCodes.DIRECTIONS, directionValue));
        Assert.assertTrue(
                HousingPolicyUtils.isPresentInDictionaryByString(user,
                        HousingPolicyDictionaryCodes.DIRECTION_ORGS, directionOrgRelationValue));
        Assert.assertTrue(
                HousingPolicyUtils.isPresentInDictionaryByString(user,
                        HousingPolicyDictionaryCodes.APPROVED_PROFESSIONS, approvedProfessionValue));

        //удалим дирекцию
        HousingPolicyIntRequests.deleteDirection(directionCode);

        //проверим отсутствие дирекции, профессии и связи
        Assert.assertFalse(
                HousingPolicyUtils.isPresentInDictionaryByString(user,
                        HousingPolicyDictionaryCodes.DIRECTIONS, directionValue));
        Assert.assertFalse(
                HousingPolicyUtils.isPresentInDictionaryByString(user,
                        HousingPolicyDictionaryCodes.DIRECTION_ORGS, directionOrgRelationValue));
        Assert.assertFalse(
                HousingPolicyUtils.isPresentInDictionaryByString(user,
                        HousingPolicyDictionaryCodes.APPROVED_PROFESSIONS, approvedProfessionValue));
    }

    @Test
    public void добавление_связи_для_дирекции_и_оргструктуры() {
        String directionCode = Utils.randomAlphaNumericEN(15);
        String orgCode = Long.toString(Math.round(Math.random() * 10000000));
        String directionName = Utils.randomRussianWords(2);

        String directionBody = new JSONArray()
                .put(
                        new JSONObject().put("code", directionCode)
                                .put("name", directionName)
                ).toString();
        HousingPolicyIntRequests.addDirections(directionBody);

        //добавим связь в словарь
        String directionOrgRelationBody = new JSONArray()
                .put(
                        new JSONObject().put("code", orgCode)
                ).toString();
        HousingPolicyIntRequests.addDirectionOrgRelations(directionCode, directionOrgRelationBody);

        //проверим ее наличие
        String directionOrgRelationValue = "[" + directionCode + ", " + orgCode + "]";
        Assert.assertTrue(HousingPolicyUtils.isPresentInDictionaryByString(user,
                HousingPolicyDictionaryCodes.DIRECTION_ORGS,
                directionOrgRelationValue));

        //удалим дирекцию
        HousingPolicyIntRequests.deleteDirectionAndOrgRelation(directionCode, orgCode);

        //проверим отсутствие дирекции, профессии и связи
        Assert.assertFalse(HousingPolicyUtils.isPresentInDictionaryByString(user,
                HousingPolicyDictionaryCodes.DIRECTION_ORGS,
                directionOrgRelationValue));
    }

    @Test(testName = "проверка работы справочника категорий работников")
    public void checkEmployeeCategoryDictionaryTest() {
        String uniqueId = UUID.randomUUID().toString();

        //Формируем тело запроса
        JSONArray requestBody = new JSONArray().put(new JSONObject()
                .put("id", uniqueId)
                .put("name", Utils.randomRussianWords(5)));

        //Добавляем новую норму
        Response response = HPHttpSender.getBasicRequest()
                .body(requestBody.toString())
                .post(NewHousingPolicyRequests.employeeCategoryDictUrl);

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);

        //Проверим наличие добавленной нормы в словаре
        Assert.assertTrue(HousingPolicyUtils.isPresentInDictionary(user,
                HousingPolicyDictionaryCodes.EMPLOYEE_CATEGORY, uniqueId));

        //Удалим переданное значение
        response = HPHttpSender.getBasicRequest()
                .body(requestBody.toString())
                .delete(NewHousingPolicyRequests.employeeCategoryDictUrl);

        Assert.assertEquals(response.statusCode(), HttpStatus.SC_OK);

        //Проверим наличие добавленной нормы в словаре
        Assert.assertFalse(HousingPolicyUtils.isPresentInDictionary(user,
                HousingPolicyDictionaryCodes.EMPLOYEE_CATEGORY, uniqueId));
    }

    @AfterClass
    public void cleanUp()
    {

    }
}
