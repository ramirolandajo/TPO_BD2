package org.uade.connections;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.uade.exceptions.MongoConnectionException;

public class MongoDB {

    private static MongoDB instancia;
    private final String url;
    private MongoClient mongoClient;

    private MongoDB() {
        url = "mongodb://127.0.01:27017";
        mongoClient = MongoClients.create(url);
    }

    public static MongoDB getInstancia(){
        if(instancia == null)
            instancia = new MongoDB();
        return instancia;
    }

    public MongoDatabase getConnection() throws MongoConnectionException {
        try {
            MongoDatabase db = mongoClient.getDatabase("tpo-bd2");
            return db;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new MongoConnectionException("Error de conexion con MongoDB.");
        }
    }

}

