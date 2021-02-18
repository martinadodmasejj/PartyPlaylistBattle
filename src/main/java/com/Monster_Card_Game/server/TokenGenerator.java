package com.Monster_Card_Game.server;

import java.util.ArrayList;
import java.util.List;

public class TokenGenerator {
    List<String> tokens = new ArrayList<>();
    public String generateToken(String input){
        String token="Basic "+input+"-mtcgToken";
        tokens.add(token);
        return token;
    }


    public String returnUserFromToken(String input){
        int starting_pos=input.indexOf("Authorization");
        String user="";
        int i=starting_pos+21;
        while (input.charAt(i)!='-'){
            user+=input.charAt(i);
            i++;
        }
        if (authenticate(user,input)) {
            return user;
        }
        return "-_-";
    }

    private boolean authenticate(String user,String header) {
        if(header.contains("Authorization: Basic "+user+"-mtcgToken")){
            return true;
        }
        return false;
    }
}