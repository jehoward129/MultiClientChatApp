/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 *
 * @author jehow
 */
public class Config {

    public static final String db_url = "mongodb+srv://jehoward129:Ihateyou2024!@cluster0.xqognpg.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

    public static MongoClient getConnection() {
        MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(db_url))
                        .build());
        return mongoClient;
    }

}
