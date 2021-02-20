package com.Party_Playlist_Battle;

import com.Party_Playlist_Battle.playlist_and_library.Playlist;
import com.Party_Playlist_Battle.server.*;
import com.Party_Playlist_Battle.user.UserManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        int portNumber=10001;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            UserManager userManager=new UserManager();
            Playlist playlist=new Playlist();
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clientSocket != null) {
                        //System.out.println("Connected");
                    }
                    ClientThread clientThread=new ClientThread(clientSocket,userManager,playlist);
                    clientThread.run();
                } catch (IOException | SQLException e){
                    System.out.println(e);
                }
            }
        }catch (IOException  e){
            System.out.println(e);
        }
    }
}
