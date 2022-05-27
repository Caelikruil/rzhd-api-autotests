package ru.digital.services.sp.SQL;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.digital.services.sp.Props;

import java.sql.*;

public class PostgreSQLConnection {

    //Возвращает результат селекта в виде JSON массива объектов строк
    public static JSONArray selectFromDatabase(db db, String SQLRequest) {

        if (!SQLRequest.toLowerCase().startsWith("select")) {
            System.out.println("Метод только для селектов");
            return null;
        }

        Connection connection;
        try {
            connection = DriverManager.getConnection(
                    Props.getOrEnv("sql." + db),
                    Props.getOrEnv("sql.login"),
                    Props.getOrEnv("sql.password"));

            Statement sqlConnection = connection.createStatement();
            ResultSet resultSet = sqlConnection.executeQuery(SQLRequest);

            JSONArray json = new JSONArray();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int numColumns = resultSetMetaData.getColumnCount();
            while (resultSet.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= numColumns; i++) {
                    obj.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
                }
                json.put(obj);
            }
            return json;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Возвращает количество задетых строк в процессе выполнения запроса
    //-1 если запрос не выполнился
    //0 - если задето 0 строк или у запроса нет результата
    public static int changeDataBySQL(db db, String SQLRequest) {

        if (SQLRequest.toLowerCase().startsWith("select")) {
            System.out.println("Для селектов используйте другой метод");
            return -1;
        }

        Connection connection;
        try {
            connection = DriverManager.getConnection(
                    Props.getOrEnv("sql." + db),
                    Props.getOrEnv("sql.login"),
                    Props.getOrEnv("sql.password"));

            Statement sqlConnection = connection.createStatement();

            return sqlConnection.executeUpdate(SQLRequest);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}