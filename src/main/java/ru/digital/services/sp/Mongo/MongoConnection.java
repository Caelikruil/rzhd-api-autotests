package ru.digital.services.sp.Mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import ru.digital.services.sp.Props;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MongoConnection {

    public static MongoClientSettings SETTINGS = MongoClientSettings.builder()
            .applyConnectionString(
                    new ConnectionString(Props.getOrEnv("mongo.connectionString")))
            .credential(MongoCredential.createCredential(
                    Props.getOrEnv("mongo.username"),
                    Props.getOrEnv("mongo.database"),
                    Props.getOrEnv("mongo.password").toCharArray()))
            .build();

    public static ArrayList<Document> getDocuments(MongoDBs database, String collection, String query) {
        MongoClient client = MongoClients.create(SETTINGS);
        MongoDatabase db = client.getDatabase(database.toString());
        Bson filter = Document.parse(query);
        ArrayList<Document> result = new ArrayList<>();
        db.getCollection(collection).find(filter).forEach(join -> result.add(join));
        return result;
    }

    public static Document updateDocument(MongoDBs database, String collection, String query, BasicDBObject updateQuery)
    {
        MongoClient client = MongoClients.create(SETTINGS);
        MongoDatabase db = client.getDatabase(database.toString());
        BasicDBObject filter = BasicDBObject.parse(query);
        return db.getCollection(collection).findOneAndUpdate(filter, updateQuery);
    }

    public static long deleteDocument(MongoDBs database, String collection, String query)
    {
        MongoClient client = MongoClients.create(SETTINGS);
        MongoDatabase db = client.getDatabase(database.toString());
        BasicDBObject filter = BasicDBObject.parse(query);
        return db.getCollection(collection).deleteOne(filter).getDeletedCount();
    }

    public static Map<Integer, BsonValue> insertDocuments(MongoDBs database, String collection, List<String> insertList)
    {
        MongoClient client = MongoClients.create(SETTINGS);
        MongoDatabase db = client.getDatabase(database.toString());
        List<Document> documents = new ArrayList<>();
        for (String s: insertList)
        {
            documents.add(Document.parse(s));
        }
        return db.getCollection(collection).insertMany(documents).getInsertedIds();
    }
}
