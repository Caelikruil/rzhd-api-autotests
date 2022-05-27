package ru.digital.services.sp.HousingPolicy.Applications;

import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.SQL.PostgreSQLConnection;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.HousingPolicyUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApplicationDeleteJobTests {

    private static User user = Users.getUser("02022035663");

    @DataProvider(name = "ChangedDbs")
    public Object[][] changedDbs() {
        return new Object[][]{
                {
                        db.housingPolicyInt,
                        "hpi_applications"
                },
                {
                        db.housingPolicy,
                        "housing_applications"
                }
        };
    }


    @Test(testName = "Проверка джобы удаления заявления в сервисе housingpolicy", dataProvider = "ChangedDbs")
    public void deleteJobIntTest(db database, String applicationTable) throws InterruptedException {
        String applicationId = HousingPolicyUtils.sendApplication(user, false);

        JSONArray applicationInDb = PostgreSQLConnection.selectFromDatabase(database,
                "SELECT id FROM " + applicationTable + " WHERE id = '" + applicationId + "';");

        Assert.assertTrue(applicationInDb.length() == 1);

        //Удалим через джобу, путем изменения даты создания заявления
        String createDateFiveYearsAgo = LocalDateTime.now().minusYears(5).minusHours(6)
                .format(DateTimeFormatter.ISO_LOCAL_DATE);

        PostgreSQLConnection.changeDataBySQL(database,
                "UPDATE " + applicationTable + " SET created_date = '" + createDateFiveYearsAgo
                        + "' WHERE id = '" + applicationId + "';");

        Thread.sleep(10000);

        applicationInDb = PostgreSQLConnection.selectFromDatabase(database,
                "SELECT id FROM " + applicationTable + " WHERE id = '" + applicationId + "';");

        Assert.assertTrue(applicationInDb.length() == 0);
    }
}
