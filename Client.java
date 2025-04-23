/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author babafemi.sorinolu
 */
public class Client {

    public static void main(String[] args) {

        try {
            //Establish socket connection to server
            Socket socket = new Socket("10.53.6.246", 6000); //my ip not Noah's
            
             //instantiates the input and output stream 
           
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
           
            //keyboard input
            Scanner in = new Scanner(System.in);

            System.out.println("Enter your username to start chatting:");
            String username = in.nextLine();

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            System.out.println("You are now connected!");
            
            listenForIncomingMessages(socket, bufferedReader);
            
            while (!socket.isClosed()) {
                String messageToSend = in.nextLine();
                bufferedWriter.write(messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                if (messageToSend.equalsIgnoreCase("exit")) {
                    closeConnection(bufferedReader, bufferedWriter, socket);
                    break;
                }
            }

//            exit the program
            System.exit(0);
            System.out.println("program ended");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void closeConnection(BufferedReader bufferedReader, BufferedWriter bufferedWriter, Socket socket) throws IOException {
        //close input and output streams
        bufferedReader.close();
        bufferedWriter.close();
        
        //close socket connection
        if (!socket.isClosed()) {
            socket.close();
        }
        System.out.println("you have left the chat.");
    }

    private static void listenForIncomingMessages(Socket socket, BufferedReader bufferedReader) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (!socket.isClosed()) {
                    try {
                        String messageFromServer = bufferedReader.readLine();
                        if(messageFromServer!=null){
                        System.out.println(messageFromServer);
                        }
                    } catch (IOException ex) {
                    }
                }
            }
        };
        
        Thread thread= new Thread(runnable);
        thread.start();
    }

}
