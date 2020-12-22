package com.github.server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnector extends BaseDBConnector {
    public static final String USERNAME_FIELD = "username";
    public static final String PASSWORD_FIELD = "password";
    private final MongoClient client;
    private final MongoDatabase db;
    private final MongoCollection<Document> collection;

    public MongoDBConnector(String mongoURI, String dbName, String dbUsername,
                            String dbPassword, String collectionName) {
        super(mongoURI);
        client = new MongoClient(new MongoClientURI(uri));
        MongoCredential.createCredential(dbUsername, dbName, dbPassword.toCharArray());
        db = client.getDatabase(dbName);
        collection = db.getCollection(collectionName);
    }

    private Document getUser(String username) {
        FindIterable<Document> documents = collection.find();
        for (Document doc : documents) {
            if (doc.get(username) != null) {
                return doc;
            }
        }
        return null;
    }

    public boolean passwordMatches(String username, String password) {
        Document document = getUser(username);
        if (document != null) {
            return document.get(username).equals(password);
        }
        return false;
    }

    public boolean containsUser(String username) {
        return getUser(username) != null;
    }

    public void addUser(String username, String password) {
        Document document = new Document(USERNAME_FIELD, username).append(PASSWORD_FIELD, password);
        collection.insertOne(document);
        System.out.println(document.toString());
    }
}
