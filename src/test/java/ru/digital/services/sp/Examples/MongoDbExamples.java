package ru.digital.services.sp.Examples;

import com.mongodb.BasicDBObject;
import org.bson.BsonValue;
import org.bson.Document;
import org.json.JSONObject;
import org.testng.annotations.Test;
import ru.digital.services.sp.Mongo.MongoConnection;
import ru.digital.services.sp.Mongo.MongoDBs;

import java.util.ArrayList;
import java.util.Map;

public class MongoDbExamples {
    @Test
    public void findTest() {
        /*
        Для работы тестов связанных с доступом к базе данных, необходимо чтобы был проброшен
        порт доступа к БД. Нужные баш скрипты приложены в проекте в папке connection
        После запуска, нужно убедиться что порт прокинут и прослушивается - далее можно запускать тест
        
        Запросы к монге состоят из 3х параметров
        database - это имя базы в монге
        collection - это имя коллекции в базе выше, в которой мы хотим искать документы
        query - это запрос на фильтр документов, вы его могли использовать в студии подключения к монге
        у квери есть требования, это должен быть валидный json
        пример:
        db.getCollection('direction').find({code : "COMMUNICATIONS"})
        database - то что тут db
        collection - direction
        query - {code : "COMMUNICATIONS"}

        Результат:
        результатом запроса является массив BSON документов, грубо говоря, чтото похожее на json
        */
        ArrayList<Document> result = MongoConnection.getDocuments(MongoDBs.achievementservice,
                "direction",
                "{ code : \"COMMUNICATIONS\"}");

        /*
        Доступ к каждому документом можно получить путем итерации массива через цикл
         */
        for (Document d : result) {
            System.out.println(d);
        }

        /*
        Если мы хотим получить полный список документов, то нужно передать пустой квери запрос
        в данном случае это пустой JSON объект -> {}
         */
        result = MongoConnection.getDocuments(MongoDBs.achievementservice,
                "direction",
                "{}");

        ArrayList<Document> docs = new ArrayList<>();

        /*
        В цикле можно творить разные штуки с документами, например переложить в другой массив по какому-то условию
         */
        for (Document d : result) {
            docs.add(d);
            System.out.println(d);
        }

        /*
        Обратиться к конкретному полю в документе можно таким образом
        get(индекс документа в массиве) - чтобы выбрать документ из списка
        второй гет - это получение поля из документа по его имени
         */
        System.out.println(docs.get(0).get("code"));
    }

    @Test
    public void updateTest()
    {
        String insertObject = new JSONObject()
                .put("code", "123").toString();

        ArrayList<String> insertList = new ArrayList<>();
        insertList.add(insertObject);

        //Добавление записей в коллекцию через массив json-строк
        //результатом является список идентификаторов и индексов записи
        Map<Integer, BsonValue> insertResult =
                MongoConnection.insertDocuments(MongoDBs.achievementservice,"direction", insertList);

        for (Map.Entry<Integer, BsonValue> entry : insertResult.entrySet())
        {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        ArrayList<Document> result = MongoConnection.getDocuments(MongoDBs.achievementservice,
                "direction",
                "{code : \"123\"}");

        for (Document d : result) {
            System.out.println(d);
        }

        //Редактирование идет по простому принципу, создается документ ключ:значение и мерджится
        //с тем, что нашлось по ключу
        BasicDBObject updateQuery = new BasicDBObject();
        updateQuery.append("$set", new BasicDBObject().append("value1", 13));
        updateQuery.append("$set", new BasicDBObject().append("value2", 4));
        updateQuery.append("$set", new BasicDBObject().append("value3", 455));

        MongoConnection.updateDocument(MongoDBs.achievementservice, "direction",
                "{code : \"123\"}", updateQuery);

        System.out.println("-------------------------------------------");

        result = MongoConnection.getDocuments(MongoDBs.achievementservice,
                "direction",
                "{code : \"123\"}");

        for (Document d : result) {
            System.out.println(d);
        }

        //Удаление работает подобно выборке - удаляется первый документ удовлетворяющий query
        MongoConnection.deleteDocument(MongoDBs.achievementservice, "direction",
                "{code : \"123\"}");
    }
}
