package com.company;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>();
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username=bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: "+username+" has entered the chat!");
        }
        catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;
        while (socket.isConnected()){
            try{
                message=bufferedReader.readLine();
                broadcastMessage(username+": "+message);
            }catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                e.printStackTrace();
            }
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClient();
        try {
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if (bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void removeClient(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: "+username+ " has left from server!!!");
    }

    private void broadcastMessage(String message) {
        for(ClientHandler clientHandler:clientHandlers){
            try {
                if(!clientHandler.username.equals(username)){
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }
            catch (IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                e.printStackTrace();
            }

        }
    }
}
