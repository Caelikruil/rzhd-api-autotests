package ru.digital.services.sp.Generators;

import org.testng.annotations.Test;
import ru.digital.services.sp.API.HousingPolicyRequests.NewHousingPolicyRequests;
import ru.digital.services.sp.Dictionaries.housingpolicy.HousingPolicyDictionaryCodes;
import ru.digital.services.sp.SQL.PostgreSQLConnection;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.Users.User;
import ru.digital.services.sp.Users.Users;
import ru.digital.services.sp.Utils.HousingPolicyUtils;

public class HousingGenerators {

    private static NewHousingPolicyRequests httpRequestSender = new NewHousingPolicyRequests();

    private User user = Users.getUser("02022035663");

    @Test
    public void generateSomeApplications() {
        HousingPolicyUtils.createBasicApplication();
    }

    @Test
    public void clearAllDictionaries() {
        PostgreSQLConnection.changeDataBySQL(db.housingPolicy, "delete from housing_dict_ls_norms;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicy, "delete from housing_approved_profession;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicy, "delete from housing_dict_direction_orgs;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicy, "delete from housing_dict_directions;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicy, "delete from housing_square_price;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicy, "delete from housing_dict_employee_categories;");

        PostgreSQLConnection.changeDataBySQL(db.housingPolicyInt, "delete from hpi_square_meter_norm;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicyInt, "delete from hpi_org_profession;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicyInt, "delete from hpi_direction_orgs;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicyInt, "delete from hpi_directions;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicyInt, "delete from hpi_cost_meter;");
        PostgreSQLConnection.changeDataBySQL(db.housingPolicyInt, "delete from hpi_employee_categories;");
    }
}
