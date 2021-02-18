package com.Monster_Card_Game.stack;

import com.Monster_Card_Game.cards.Card;
import com.Monster_Card_Game.server.DatabaseHandler;
import com.Monster_Card_Game.server.JsonSerializer;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class PackageHandler {
    Card[] cardPackage=new Card[5];
    JsonSerializer jsonSerializer=new JsonSerializer();

    public void generatePackage(String payload,DatabaseHandler dbHandler) throws JsonProcessingException, SQLException {
        String cards[]=payload.split("(?<=\\}), (?=\\{)");
        cards[0]=cards[0].replace("[","");
        cards[cards.length-1]=cards[cards.length-1].replace("]","");
        for (int i=0;i<cards.length;i++){
            //System.out.println(cards[i]);
            cardPackage[i]=jsonSerializer.convertCardToObject(cards[i]);
        }
        uploadPackage(cardPackage,dbHandler);
    }

    private void uploadPackage(Card[] cardPackage,DatabaseHandler dbHandler) throws SQLException {
        int packageID = 0;
        String insertPackage="INSERT INTO \"MonsterCardGame\".\"package\"( \"packagename\", \"packagecost\")" +
                " VALUES ('package',5) RETURNING \"packageid\"";
        String insertCard="INSERT INTO \"MonsterCardGame\".\"card\"(\"cardserialid\", \"carddamage\", \"cardname\"," +
                "\"cardattribute\", \"cardmonster\", \"packageid\")" +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Statement stmt=dbHandler.getConnection().createStatement();
        ResultSet resultSet=stmt.executeQuery(insertPackage);
        while (resultSet.next()){
            packageID=Integer.parseInt(resultSet.getString("packageid"));
        }
        for (int i=0;i<cardPackage.length;i++){
            PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(insertCard);
            preparedStatement.setString(1,cardPackage[i].getCardID());
            preparedStatement.setInt(2,cardPackage[i].getDamage());
            preparedStatement.setString(3,cardPackage[i].getName());
            preparedStatement.setString(4,cardPackage[i].getAttribute().toString());
            preparedStatement.setString(5,cardPackage[i].getMonsterType().toString());
            preparedStatement.setInt(6,packageID);
            preparedStatement.executeUpdate();
        }
    }
}
