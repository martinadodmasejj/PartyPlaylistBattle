package com.Monster_Card_Game.user;

import com.Monster_Card_Game.cards.Card;
import com.Monster_Card_Game.server.DatabaseHandler;
import com.Monster_Card_Game.stack.Deck;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.omg.PortableInterceptor.USER_EXCEPTION;

import javax.xml.crypto.Data;
import java.awt.dnd.DropTarget;
import java.io.IOException;
import java.net.Socket;
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
    private Deck deck=null;
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

    public boolean acquirePackage(DatabaseHandler dbHandler) throws SQLException {
        boolean confirmation=true;
        getUserIdDatabase(dbHandler);
        mutex.lock();
        if (checkCoins(dbHandler)) {
            String sqlAcquire = "UPDATE \"MonsterCardGame\".\"package\" SET \"userid\" = " + userID + " WHERE \"packageid\" = " +
                    "(SELECT \"packageid\" from \"MonsterCardGame\".\"package\"" +
                    "WHERE \"userid\" IS NULL LIMIT 1) RETURNING (SELECT \"packageid\" from \"MonsterCardGame\".\"package\" " +
                    "                WHERE \"userid\" IS NULL LIMIT 1);";
            String sqlReduceCoins = "UPDATE \"MonsterCardGame\".user set \"vcoins\"=\"vcoins\"-5 " +
                    "where \"userid\"=" + userID + " AND \"vcoins\" > 0 RETURNING \"vcoins\"";
            Statement stmt2 = dbHandler.getConnection().createStatement();
            Statement stmt = dbHandler.getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(sqlAcquire);
            if (!resultSet.next()){
                confirmation=false;
                System.out.println("No more packages left to buy!!");
            }
            else {
                ResultSet resultSet2 = stmt2.executeQuery(sqlReduceCoins);
                while (resultSet2.next()){
                    vcoins=resultSet2.getInt("vcoins");
                }
            }
        }
        else {
            System.out.println("Not enough coins to buy package");
            confirmation=false;
        }
        mutex.unlock();
        return confirmation;
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

    public void createDeck(String payload,DatabaseHandler dbHandler) throws SQLException {
        getUserIdDatabase(dbHandler);
        mutex.lock();
        if(deck==null) {
            deck = new Deck();
        }
        deck.createCards(userID,payload,dbHandler);
        mutex.unlock();
    }

    private boolean checkCoins(DatabaseHandler dbHandler) throws SQLException {
        String sql="select \"vcoins\" from \"MonsterCardGame\".\"user\""+
                " where \"userid\"="+userID;
        Statement stmt=dbHandler.getConnection().createStatement();
        ResultSet resultSet=stmt.executeQuery(sql);
        while (resultSet.next()){
            vcoins=resultSet.getInt("vcoins");
        }
        if (vcoins > 0){
            return true;
        }
        return false;
    }

    public void autoGenerateDeck(DatabaseHandler dbHandler) throws SQLException {
        getUserIdDatabase(dbHandler);
        mutex.lock();
        if(deck==null) {
            deck = new Deck();
        }
        deck.autoGenerateCards(dbHandler,userID);
        mutex.unlock();
    }

    public void printDeck(){
        if (deck==null){
            System.out.println("Empty Deck!!!");
            return;
        }
        mutex.lock();
        deck.printDeck();
        mutex.unlock();
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


    public void battle(User other,DatabaseHandler dbHandler) throws SQLException {
        Random random=new Random();
        int user1Rand=0;
        int user2Rand=0;
        Card cardPlayer1;
        Card cardPlayer2;
        int user1DeckCounter=4;
        int user2DeckCounter=4;
        Deck opposingDeck=other.returnDeck();
        if (opposingDeck==null){
            System.out.println("Opponent hasn't configured his Deck yet");
            return;
        }
        // Starting Battle
        for (int i=0;i<100;i++){
            if(deck.isEmpty()){
                statsManager.insertBattleLog(dbHandler,other.getUserID(),userID,false);
                statsManager.updateUserStats(other.getUserID(),userID,false,dbHandler);
                System.out.println("Player: "+other.getUsername()+" won");
                return;
            }
            else if(opposingDeck.isEmpty()){
                statsManager.insertBattleLog(dbHandler,userID,other.getUserID(),false);
                statsManager.updateUserStats(userID,other.getUserID(),false,dbHandler);
                System.out.println("Player: "+username+" won");
                return;
            }
            System.out.println("\n\nRound "+i);
            System.out.println("Deck "+username+" Cards left:"+deck.getLength());
            System.out.println("Deck "+other.getUsername()+" Cards left:"+opposingDeck.getLength());
            user1Rand=random.nextInt(user1DeckCounter);
            user2Rand=random.nextInt(user2DeckCounter);
            cardPlayer1=deck.getCard(user1Rand);
            cardPlayer2=opposingDeck.getCard(user2Rand);
            System.out.println(cardPlayer1.getName()+" vs "+cardPlayer2.getName());
            if(cardPlayer1.battleCard(cardPlayer2)){
                deck.addCard(cardPlayer2);
                opposingDeck.removeCard(cardPlayer2);
                user2DeckCounter--;
                user1DeckCounter++;
                System.out.println(cardPlayer1.getBattleLog());
                cardPlayer1.resetBattleLog();
                cardPlayer2.resetBattleLog();
            }
            else if(cardPlayer2.battleCard(cardPlayer1)){
                deck.removeCard(cardPlayer1);
                opposingDeck.addCard(cardPlayer1);
                user1DeckCounter--;
                user2DeckCounter++;
                System.out.println(cardPlayer2.getBattleLog());
                cardPlayer2.resetBattleLog();
                cardPlayer1.resetBattleLog();
            }
            else {
                System.out.println(cardPlayer1.getBattleLog());
                cardPlayer1.resetBattleLog();
                cardPlayer2.resetBattleLog();
                System.out.println("Draw between Cards!!");
            }
        }
        statsManager.insertBattleLog(dbHandler,other.getUserID(),userID,true);
        statsManager.updateUserStats(userID,other.getUserID(),true,dbHandler);
        System.out.println("Maximum number of Rounds exceeded. Draw !");
    }

    public void showUserStats(DatabaseHandler dbHandler) throws SQLException {
        statsManager.showUserStats(username,dbHandler);
    }

    public void showUserScoreboard(DatabaseHandler dbHandler)throws SQLException{
        getUserIdDatabase(dbHandler);
        statsManager.showScoreboard(dbHandler,this);
    }

}
