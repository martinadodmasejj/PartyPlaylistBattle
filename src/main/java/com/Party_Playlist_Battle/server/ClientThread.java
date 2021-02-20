package com.Party_Playlist_Battle.server;

import com.Party_Playlist_Battle.playlist_and_library.Library;
import com.Party_Playlist_Battle.playlist_and_library.MediaContent;
import com.Party_Playlist_Battle.playlist_and_library.Playlist;
import com.Party_Playlist_Battle.user.User;
import com.Party_Playlist_Battle.user.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

public class ClientThread extends Thread {
    DatabaseHandler dbHandler;
    RequestContext handler;
    JsonSerializer jsonSerializer;
    TokenGenerator tokenGenerator;
    PrintWriter out;
    BufferedReader in;
    UserManager userManager;
    private ReentrantLock mutex=new ReentrantLock();
    Playlist playlist;

    public ClientThread(Socket clientSocket, UserManager userManager, Playlist playlist) throws SQLException, IOException {
        dbHandler = new DatabaseHandler();
        handler = new RequestContext();
        jsonSerializer = new JsonSerializer();
        tokenGenerator = new TokenGenerator();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.userManager = userManager;
        handler = new RequestContext();
        this.playlist=playlist;
    }

    @Override
    public void run() {
            try {
                String header = handler.readHeader(in);
                String payload = handler.readPayload(in);
                String request = handler.readRequest();
                String verb=handler.readHTTPVerb();
                System.out.println(payload);
                if (request.compareTo("users") == 0) {
                    User user = jsonSerializer.convertUserToObject(payload);
                    dbHandler.createUser(user.getUsername(), user.getPassword());
                    user.initialiseStats(dbHandler);
                    user.initialiseLibrary(dbHandler);
                    user.resetPassword();
                    out.println(handler.ServerResponse);
                    out.flush();
                } else if (request.compareTo("sessions") == 0) {
                    User tempUser=jsonSerializer.convertUserToObject(payload);
                    int position;
                    if (dbHandler.validateUser(tempUser.getUsername(),tempUser.getPassword())) {
                        position=userManager.addUser(tempUser);
                        tokenGenerator.generateToken(tempUser.getUsername());
                        userManager.at(position).resetPassword();
                    } else {
                        System.out.println("Wrong username or password");
                    }
                    out.println(handler.ServerResponse);
                    out.flush();
                }
                else if(request.contains("users/")){
                    String user = tokenGenerator.returnUserFromToken(header);
                    if (request.contains(user)) {
                        if(handler.readHTTPVerb().compareTo("PUT")==0) {
                            userManager.at(user).updateUserData(dbHandler, payload);
                        }
                        else if(handler.readHTTPVerb().compareTo("GET")==0){
                            userManager.at(user).showUserData(dbHandler);
                        }
                    } else {
                        System.out.println("No permission to edit the user bad authentication");
                    }
                    out.println(handler.ServerResponse);
                    out.flush();
                }
                /*else if (request.compareTo("battles")==0){
                    String username=tokenGenerator.returnUserFromToken(header);
                    if (userManager.at(username)==null){
                        System.out.println("Not logged in");
                    }
                    else {
                        userManager.queueUp(userManager.at(username));
                        userManager.startBattle(dbHandler);
                    }
                    out.println(handler.ServerResponse);
                    out.flush();
                }*/
                else if(request.compareTo("lib")==0 && verb.compareTo("POST")==0){
                    String username=tokenGenerator.returnUserFromToken(header);
                    if (userManager.at(username)==null) {
                        System.out.println("Not logged in");
                    }
                    MediaContent tempMedia=jsonSerializer.convertMediaToObject(payload);
                    tempMedia.uploadMediaContent(dbHandler,userManager.at(username));
                    out.println(handler.ServerResponse);
                    out.flush();
                }
                else if(request.compareTo("lib/.") > 0 && verb.compareTo("DELETE")==0){
                    String username=tokenGenerator.returnUserFromToken(header);
                    if (userManager.at(username)==null) {
                        System.out.println("Not logged in");
                    }
                    String fileName=request.replace("lib/","");
                    userManager.at(username).lib.deleteMediaContent(fileName,dbHandler,userManager.at(username));
                    out.println(handler.ServerResponse);
                    out.flush();
                }
                else if(request.compareTo("lib")==0){
                    String username=tokenGenerator.returnUserFromToken(header);
                    userManager.at(username).lib.printLibrary(dbHandler,username);
                    if (userManager.at(username)==null) {
                        System.out.println("Not logged in");
                    }
                    out.println(handler.ServerResponse);
                    out.flush();
                }
                else if (request.compareTo("stats")==0){
                    String username=tokenGenerator.returnUserFromToken(header);
                    if (userManager.at(username)==null) {
                        System.out.println("Not logged in");
                    }else {
                        userManager.at(username).showUserStats(dbHandler);
                    }
                    out.println(handler.ServerResponse);
                    out.flush();
                }
                else if(request.compareTo("score")==0){
                    String username=tokenGenerator.returnUserFromToken(header);
                    if (userManager.at(username)==null) {
                        System.out.println("Not logged in");
                    }else {
                        userManager.at(username).showUserScoreboard(dbHandler);
                    }
                    out.println(handler.ServerResponse);
                    out.flush();
                }
                else if(request.compareTo("playlist")==0 && verb.compareTo("POST")==0){
                    String username=tokenGenerator.returnUserFromToken(header);
                    if (userManager.at(username)==null) {
                        System.out.println("Not logged in");
                    }
                    String fileName=handler.getFileName(payload);
                    playlist.addContentPlaylist(fileName,dbHandler,userManager.at(username));
                    out.println(handler.ServerResponse);
                    out.flush();
                }
                else if (request.compareTo("playlist")==0 && verb.compareTo("GET")==0){
                    playlist.printPlaylist();
                    out.println(handler.ServerResponse);
                    out.flush();
                }
            } catch (IOException | SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                System.out.println(e);
                out.println(handler.ServerResponse);
                out.flush();
            }
    }
}

