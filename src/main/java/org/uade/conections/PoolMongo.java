package org.uade.conections;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.uade.exceptions.ErrorConectionMongoException;

public class PoolMongo {

    private static PoolMongo instancia;
    private String url ;
    private MongoClient mongoClient;

    private PoolMongo() {
        url = "mongodb://127.0.01:27017";
        mongoClient = MongoClients.create(url);
    }

    public static PoolMongo getInstancia(){
        if(instancia == null)
            instancia = new PoolMongo();
        return instancia;
    }

    public MongoDatabase getConection(String database) throws ErrorConectionMongoException {
        try {
            MongoDatabase db = mongoClient.getDatabase(database);
            return db;
        }
        catch (Exception e) {
            throw new ErrorConectionMongoException("Error ene la coneccxion a MongoDB");
        }
    }

}

