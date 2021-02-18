package com.Monster_Card_Game.tests;

import com.Monster_Card_Game.cards.Card;
import com.Monster_Card_Game.cards.MonsterCard;
import com.Monster_Card_Game.cards.SpellCard;
import com.Monster_Card_Game.server.JsonSerializer;
import com.Monster_Card_Game.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.Monster_Card_Game.enums.monsters;
import com.Monster_Card_Game.enums.elements;
import java.io.BufferedReader;
import java.io.IOException;

public class JsonSerializerTest {
    @Test
    public void testCardSerialization() throws IOException {
        //Arrange
        JsonSerializer jsonSerializer=new JsonSerializer();
        BufferedReader in= Mockito.mock(BufferedReader.class);
        Mockito.when(in.readLine()).thenReturn("{\"Id\":\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"Name\":\"WaterGoblin\", \"Damage\": 10.0}",
                "{\"Id\":\"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"Name\":\"WaterSpell\", \"Damage\": 20.0}");
        //Act
        Card monsterCard=jsonSerializer.convertCardToObject(in.readLine());
        Card spellCard=jsonSerializer.convertCardToObject(in.readLine());
        //Assert
        Assert.assertEquals(monsters.Goblin,monsterCard.getMonsterType());
        Assert.assertEquals(elements.Water,monsterCard.getAttribute());
        Assert.assertEquals(10,monsterCard.getDamage());
        Assert.assertEquals(elements.Water,spellCard.getAttribute());
        Assert.assertEquals(20,spellCard.getDamage());
    }

    @Test
    public  void testUserSerialization() throws IOException {
        //Arrange
        JsonSerializer jsonSerializer=new JsonSerializer();
        BufferedReader in= Mockito.mock(BufferedReader.class);
        Mockito.when(in.readLine()).thenReturn("{\"Username\":\"admin\",\"Password\":\"istrator\"}");
        //Act
        User testUser=jsonSerializer.convertUserToObject(in.readLine());
        //Assert
        Assert.assertEquals("admin",testUser.getUsername());
        Assert.assertEquals("istrator",testUser.getPassword());
    }

}
