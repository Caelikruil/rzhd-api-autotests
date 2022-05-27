package ru.digital.services.sp.Examples;

import org.json.JSONArray;
import org.testng.annotations.Test;
import ru.digital.services.sp.SQL.db;
import ru.digital.services.sp.SQL.PostgreSQLConnection;
import ru.digital.services.sp.Utils.Utils;

public class SqlTestsExamples {

    @Test
    public void selectFromBaseExample(){
        /*Для работы тестов связанных с доступом к базе данных, необходимо чтобы был проброшен
        порт доступа к БД. Нужные баш скрипты приложены в проекте в папке connection
        После запуска, нужно убедиться что порт прокинут и прослушивается - далее можно запускать тест

        В классе PostgreSQLConnection есть 2 метода - на чтение и редактирование
        схема вызова метода на чтение простая - БД для подключения, запрос

        Результатом выполнения будет JSON массив с записями из базы, например
        select 1 as asd; вернет массив вида
        [{"a":1}];
        */
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.vtt,
                "select * from vtt_settings where snils = '02022035663';");
        System.out.println(result);
    }

    @Test
    public void test2()
    {
        /*
        Вызов метода редактирования не отличается от вызова метода чтения
        Отличается только результат, для методов редактирования возвращается количество
        строк, которые были задеты запросом

        ВАЖНО! Помните, ваши запросы небезопасны, всегда используйте WHERE чтобы не удалить все данные
        иначе Игорек удалит вас
        */
        JSONArray result = PostgreSQLConnection.selectFromDatabase(db.vtt,
                "select * from vtt_settings where snils = '02022035663';");
        System.out.println(result);

        int result1 = PostgreSQLConnection.changeDataBySQL(db.vtt,
                "delete from vtt_settings where snils = '02022035663';");
        System.out.println(result1);

        result = PostgreSQLConnection.selectFromDatabase(db.vtt,
                "select * from vtt_settings where snils = '02022035663';");
        System.out.println(result);

        result1 = PostgreSQLConnection.changeDataBySQL(db.vtt,
                "insert into vtt_settings values ('02022035663', true, false, false);");
        System.out.println(result1);

        result = PostgreSQLConnection.selectFromDatabase(db.vtt,
                "select * from vtt_settings where snils = '02022035663';");
        System.out.println(result);

        result1 = PostgreSQLConnection.changeDataBySQL(db.vtt,
                "update vtt_settings set send_notif_email = false where snils = '02022035663';");
        System.out.println(result1);

        result = PostgreSQLConnection.selectFromDatabase(db.vtt,
                "select * from vtt_settings where snils = '02022035663';");
        System.out.println(result);
    }

    @Test
    public void exa()
    {
        System.out.println(Utils.randomFloat(1));
        System.out.println(Utils.randomFloat(2));
        System.out.println(Utils.randomFloat(5));
        System.out.println(Utils.randomInt(1));
        System.out.println(Utils.randomInt(2));
        System.out.println(Utils.randomInt(5));
    }
}
