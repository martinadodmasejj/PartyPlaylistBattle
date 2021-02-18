package com.Party_Playlist_Battle.server;

import com.Party_Playlist_Battle.stack.PackageHandler;
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

    public ClientThread(Socket clientSocket,UserManager userManager) throws SQLException, IOException {
        dbHandler = new DatabaseHandler();
        handler = new RequestContext();
        jsonSerializer = new JsonSerializer();
        tokenGenerator = new TokenGenerator();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.userManager = userManager;
        handler = new RequestContext();
    }

    @Override
    public void run() {
            try {
                String header = handler.readHeader(in);
                String payload = handler.readPayload(in);
                String request = handler.readRequest();
                System.out.println(payload);
                if (request.compareTo("users") == 0) {
                    User user = jsonSerializer.convertUserToObject(payload);
                    dbHandler.createUser(user.getUsername(), user.getPassword());
                    user.initialiseStats(dbHandler);
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
                } else if (request.compareTo("packages") == 0) {
                    String user=tokenGenerator.returnUserFromToken(header);
                    if(userManager.at(user)==null){
                        System.out.println("Not logged in");
                    }
                    else if (user.compareTo("admin")!=0) {
                        System.out.println("Admin privilege is required!");
                    }else {
                        PackageHandler packageHandler = new PackageHandler();
                        packageHandler.generatePackage(payload, dbHandler);
                    }
                    out.println(handler.ServerResponse);
                    out.flush();
                } else if (request.compareTo("transactions/packages") == 0) {
                    String username = tokenGenerator.returnUserFromToken(header);
                    if (userManager.at(username) == null) {
                        System.out.println("Not logged in");
                    }
                    if (userManager.at(username).acquirePackage(dbHandler)) {
                        System.out.println(username + " acquired a package");
                    } else {
                        System.out.println("Unsuccessful transaction");
                    }
                    out.println(handler.ServerResponse);
                    out.flush();
                } else if (request.compareTo("cards") == 0) {
                    String username = tokenGenerator.returnUserFromToken(header);
                    if (userManager.at(username) == null) {
                        System.out.println("Not logged in");
                    }
                    else {
                        System.out.println("User: " + username + " cards:");
                        userManager.at(username).showAcquiredCards(dbHandler);
                    }
                    out.println(handler.ServerResponse);
                    out.flush();
                } else if (request.compareTo("deck") == 0 || request.compareTo("deck?format=plain")==0) {
                    if (handler.readHTTPVerb().compareTo("GET") == 0) {
                        String username = tokenGenerator.returnUserFromToken(header);
                        if (userManager.at(username) == null) {
                            System.out.println("Not logged in");
                        }
                        else {
                            userManager.at(username).printDeck();
                        }
                        out.println(handler.ServerResponse);
                        out.flush();
                    } else {
                        String username = tokenGenerator.returnUserFromToken(header);
                        if (userManager.at(username) == null) {
                            System.out.println("Not logged in");
                        }
                        else {
                            if (payload.isEmpty()){
                                userManager.at(username).autoGenerateDeck(dbHandler);
                            }
                            else {
                                userManager.at(username).createDeck(payload, dbHandler);
                            }
                        }
                        out.println(handler.ServerResponse);
                        out.flush();
                    }
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
                else if (request.compareTo("battles")==0){
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
            } catch (IOException | SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                System.out.println(e);
                out.println(handler.ServerResponse);
                out.flush();
            }
    }
}

