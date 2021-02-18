package com.Party_Playlist_Battle.server;

import com.Party_Playlist_Battle.cards.Card;
import com.Party_Playlist_Battle.cards.MonsterCard;
import com.Party_Playlist_Battle.cards.SpellCard;
import com.Party_Playlist_Battle.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonSerializer {
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
    }

    public User convertUserToObject(String jsonUser)throws JsonProcessingException{
        ObjectMapper objectMapper=new ObjectMapper();
        User user=objectMapper.readValue(jsonUser,User.class);
        return user;
    }
}
