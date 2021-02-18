package com.Party_Playlist_Battle.server;


import com.Party_Playlist_Battle.playlist_and_library.MediaContent;
import com.Party_Playlist_Battle.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonSerializer {
    /*
    public Card convertCardToObject(String jsonCard) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        Card card;
        if(jsonCard.contains("Spell")){
             card = objectMapper.readValue(jsonCard,SpellCard.class);
        }
        else{
             card = objectMapper.readValue(jsonCard,MonsterCard.class);
        }
        return card;
    }*/

    public MediaContent convertMediaToObject(String jsonContent)throws JsonProcessingException{
        ObjectMapper objectMapper=new ObjectMapper();
        MediaContent content=objectMapper.readValue(jsonContent,MediaContent.class);
        return content;
    }

    public User convertUserToObject(String jsonUser)throws JsonProcessingException{
        ObjectMapper objectMapper=new ObjectMapper();
        User user=objectMapper.readValue(jsonUser,User.class);
        return user;
    }
}
