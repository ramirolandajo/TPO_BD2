package org.uade.connections;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.uade.exceptions.MongoConnectionException;

import java.util.ArrayList;

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
            PojoCodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();

            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(pojoCodecProvider));


            MongoDatabase db = mongoClient.getDatabase("tpo-bd2").withCodecRegistry(pojoCodecRegistry);
                if (!db.listCollectionNames().into(new ArrayList<>()).contains("Productos")) {
                    db.createCollection("Productos");
                    db.createCollection("Usuarios");
                    db.createCollection("Pedidos");
                    db.createCollection("Facturas");
                }
                return db;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new MongoConnectionException("Error de conexion con MongoDB.");
        }
    }

    public void close() {
        mongoClient.close();
    }
}

