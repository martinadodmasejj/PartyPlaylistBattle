package com.Monster_Card_Game;

import com.Monster_Card_Game.cards.Card;
import com.Monster_Card_Game.cards.MonsterCard;
import com.Monster_Card_Game.cards.SpellCard;
import com.Monster_Card_Game.enums.elements;
import com.Monster_Card_Game.enums.monsters;
import com.Monster_Card_Game.server.*;
import com.Monster_Card_Game.stack.PackageHandler;
import com.Monster_Card_Game.user.User;
import com.Monster_Card_Game.user.UserManager;

import javax.jws.soap.SOAPBinding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        int portNumber=10001;
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            UserManager userManager=new UserManager();
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    if (clientSocket != null) {
                        //System.out.println("Connected");
                    }
                    ClientThread clientThread=new ClientThread(clientSocket,userManager);
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
