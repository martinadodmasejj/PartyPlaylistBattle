package com.Party_Playlist_Battle.user;

import com.Party_Playlist_Battle.server.DatabaseHandler;
import com.Party_Playlist_Battle.stack.Deck;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class User {
    private int vcoins;
    private String username;
    private String password;
    private int userID;
    private String bio;
    private String image;
    private ReentrantLock mutex=new ReentrantLock();
    private StatsManager statsManager=new StatsManager();

    @JsonCreator
    User(@JsonProperty("Username")String username,@JsonProperty("Password")String password)  {
        this.username=username;
        this.password=password;
        userID=-1; // userID is non existent
        bio="";
        image="";
    }

    User(String username) throws SQLException {
        this.username=username;
        userID=-1;
        bio="";
        image="";
    }
    public int getVcoins() {
        return vcoins;
    }

    public void setVcoins(int vcoins) {
        this.vcoins = vcoins;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public void resetPassword(){ password=""; }

    public String getBio() {
        return bio;
    }

    public int getUserID() { return  userID; }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Deck returnDeck(){ return deck;  }

    public void getUserIdDatabase(DatabaseHandler dbHandler) throws SQLException {
        if(userID==-1) {
            String getUserID = "Select \"userid\" from \"MonsterCardGame\".\"user\"\n" +
                    "WHERE \"username\" = ?";
            PreparedStatement preparedStatement = dbHandler.getConnection().prepareStatement(getUserID);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                userID=resultSet.getInt("userid");
            }
        }
    }

    private String deserialiseUserInfo(String payload){
        int nameIndex=payload.indexOf("Name");
        nameIndex+=8;
        String firstName="";
        for (int i=nameIndex;i<payload.length();i++){
            if(payload.charAt(i)=='\"'){
                break;
            }
            firstName+=payload.charAt(i);
        }
        System.out.println(firstName);
        int bioIndex=payload.indexOf("Bio");
        bioIndex+=7;
        for (int i=bioIndex;i<payload.length();i++){
            if(payload.charAt(i)=='\"'){
                break;
            }
            bio+=payload.charAt(i);
        }
        System.out.println(bio);
        int imageIndex=payload.indexOf("Image");
        imageIndex+=9;
        for (int i=imageIndex;i<payload.length();i++){
            if(payload.charAt(i)=='\"'){
                break;
            }
            image+=payload.charAt(i);
        }
        System.out.println(image);
        return firstName;
    }



    public void showAcquiredCards(DatabaseHandler dbHandler) throws SQLException {
        getUserIdDatabase(dbHandler);
        String selectCards="select * " +
                "from \"MonsterCardGame\".\"card\" as c" +
                " join \"MonsterCardGame\".\"package\" as p" +
                " on c.\"packageid\"=p.\"packageid\"" +
                " where p.\"userid\"="+userID;
        Statement stmt=dbHandler.getConnection().createStatement();
        System.out.println(selectCards);
        ResultSet resultSet=stmt.executeQuery(selectCards);
        int i=0;
        while (resultSet.next()){
            i++;
            System.out.println("Card "+resultSet.getString("cardserialid")+": "
                    +resultSet.getString("cardname")+" "+resultSet.getString("carddamage")+
                    " "+resultSet.getString("cardattribute")+" "+resultSet.getString("cardmonster"));
        }
    }


    public void updateUserData(DatabaseHandler dbHandler,String payload) throws IOException, SQLException {
        String firstName=deserialiseUserInfo(payload);
        String sqlUpdate="UPDATE \"MonsterCardGame\".\"user\" " +
                "SET  \"firstname\"= ? , \"bio\"= ? , \"image\"= ?"
                +" WHERE \"username\"=?";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(sqlUpdate);
        preparedStatement.setString(1,firstName);
        preparedStatement.setString(2,bio);
        preparedStatement.setString(3,image);
        preparedStatement.setString(4,username);
        preparedStatement.executeUpdate();
    }

    public void showUserData(DatabaseHandler dbHandler)throws SQLException{
        String sqlSelect="select * from \"MonsterCardGame\".\"user\" where \"username\" = ? ";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(sqlSelect);
        preparedStatement.setString(1,username);
        ResultSet resultSet=preparedStatement.executeQuery();
        while (resultSet.next()){
            System.out.println("Username: "+username);
            System.out.println("Firstname: "+resultSet.getString("firstname"));
            System.out.println("Bio: "+resultSet.getString("bio"));
            System.out.println("Image: "+resultSet.getString("image"));
        }
    }

    public void initialiseStats(DatabaseHandler dbHandler)throws SQLException{
        getUserIdDatabase(dbHandler);
        String sqlInsert="INSERT INTO \"MonsterCardGame\".stats( " +
                " userid, elo, wins, losses, draws) " +
                " VALUES (?, 100, 0, 0, 0) ";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(sqlInsert);
        preparedStatement.setInt(1,userID);
        preparedStatement.executeUpdate();
    }

    public void showUserStats(DatabaseHandler dbHandler) throws SQLException {
        statsManager.showUserStats(username,dbHandler);
    }

    public void showUserScoreboard(DatabaseHandler dbHandler)throws SQLException{
        getUserIdDatabase(dbHandler);
        statsManager.showScoreboard(dbHandler,this);
    }

}
