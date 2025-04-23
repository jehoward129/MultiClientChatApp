/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import static com.mycompany.chatapp.MainServer.clientsList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

/**
 *
 * @author babafemi.sorinolu
 */
class ChatClientHandler implements Runnable {

    Socket socket;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    String username;
    String Language;
    String toClient;

    public ChatClientHandler(Socket s) {
        try {
            this.socket = s;
            //instantiates the input and output stream 
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //store the first input from the client socket as username
            username = bufferedReader.readLine();

//            broadcast notification to clients - new user joining 
            String notification = username + " has joined the chat";
            System.out.println(notification);
            sendMessageReceivedToOtherClients(username);
        } catch (IOException ex) {
            Logger.getLogger(ChatClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {

//        as long as the socket connection is active, keep getting message from client
//        broadcast to the other clients
        while (!socket.isClosed()) {

            try {
                //retrieve message from client
                String messageFromClient = bufferedReader.readLine();

                //disconnect if the clients sends the exit keyword
                if (messageFromClient.equalsIgnoreCase("exit")) {
                    closeConnection();
                    break;
                }

                if (messageFromClient.contains("@")) {
                    toClient = extractName(messageFromClient);
                    sendMessageReceivedToSpecificClients(toClient, messageFromClient);
                } else {
                    //broadcast the incoming client message to the other clients
                    sendMessageReceivedToOtherClients(username + ":" + messageFromClient);
                }

            } catch (IOException ex) {
//                in the advent of an error, close socket connection
                try {
                    closeConnection();
                    break;
                } catch (IOException ex1) {
                    Logger.getLogger(ChatClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }

        }

    }

    private void sendMessageReceivedToOtherClients(String messageFromClient) {
//        loop through the clientsLists and send message to the clients except the sender
        for (ChatClientHandler client : clientsList) {
            try {
//                skip the sender
                if (client.username.equals(username)) {
                    continue;
                }
//                send the message to the client
                client.bufferedWriter.write(messageFromClient);
                client.bufferedWriter.newLine();
                client.bufferedWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(ChatClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void sendMessageReceivedToSpecificClients(String target, String messageFromClient) {
        boolean found = false;
//        ArrayList<ChatClientHandler> toClientsList= new ArrayList<>();

        for (ChatClientHandler client : clientsList) {
            if (client.username.equals(target)) {
                found = true;

            }
        }
        if (!found) {
            try {
                //            Tell sender there is no client
                this.bufferedWriter.write("Client not found");
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(ChatClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
//        loop through the clientsLists and send message to the clients except the sender
        for (ChatClientHandler client : clientsList) {
            try {
//                skip the sender
                if (client.username.equals(username)) {
                    continue;
                }
//                send the message to the client
                if (client.username.equals(target)) {
                    client.bufferedWriter.write(messageFromClient);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(ChatClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void closeConnection() throws IOException {
        //broadcast notification to clients - user leaving the chat 
        String notification = username + " has left the chat";
        System.out.println(notification);
        sendMessageReceivedToOtherClients(notification);

        //remove the client socket from the list
        clientsList.remove(this);

        //close input and output streams
        bufferedReader.close();
        bufferedWriter.close();

        //close socket connection
        if (!socket.isClosed()) {
            socket.close();
        }

    }

    public String extractName(String receiver) {
        int atIndex = receiver.indexOf('@');
        if (atIndex == -1) {
            return ""; 
        }

        int spaceIndex = receiver.indexOf(' ', atIndex);
        if (spaceIndex == -1) {
            
            return receiver.substring(atIndex + 1);
        }

        return receiver.substring(atIndex + 1, spaceIndex);
    }

}
