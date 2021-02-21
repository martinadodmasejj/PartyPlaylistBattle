package com.Party_Playlist_Battle.user;

import com.Party_Playlist_Battle.playlist_and_library.Library;
import com.Party_Playlist_Battle.server.DatabaseHandler;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.postgresql.util.JdbcBlackHole;
import sun.security.krb5.internal.APRep;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class User {
    private String username;
    private String password;
    private int userID;
    private String bio;
    private String image;
    private ReentrantLock mutex=new ReentrantLock();
    private StatsManager statsManager=new StatsManager();
    private boolean isAdmin;
    public Library lib=new Library();
    public List<Character> actions=new ArrayList();

    @JsonCreator
    User(@JsonProperty("Username")String username,@JsonProperty("Password")String password)  {
        this.username=username;
        this.password=password;
        userID=-1; // userID is non existent
        bio="";
        image="";
        isAdmin=false;
    }

    User(String username) throws SQLException {
        this.username=username;
        userID=-1;
        bio="";
        image="";
        isAdmin=false;
    }

    public String getUsername() {
        return username;
    }

    public void promoteToAdmin(){
        isAdmin=true;
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

    public int getUserID(DatabaseHandler dbHandler) throws SQLException {
        getUserIdDatabase(dbHandler);
        return  userID;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public void getUserIdDatabase(DatabaseHandler dbHandler) throws SQLException {
        if(userID==-1) {
            String getUserID = "Select \"userid\" from \"PartyPlaylistBattle\".\"user\"\n" +
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

    public void updateUserData(DatabaseHandler dbHandler,String payload) throws IOException, SQLException {
        String firstName=deserialiseUserInfo(payload);
        String sqlUpdate="UPDATE \"PartyPlaylistBattle\".\"user\" " +
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
        String sqlSelect="select * from \"PartyPlaylistBattle\".\"user\" where \"username\" = ? ";
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
        String sqlInsert="INSERT INTO \"PartyPlaylistBattle\".stats( " +
                " userid, wins, losses) " +
                " VALUES (?,  0, 0) ";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(sqlInsert);
        preparedStatement.setInt(1,userID);
        preparedStatement.executeUpdate();
    }

    public void initialiseLibrary(DatabaseHandler dbHandler)throws SQLException{
        getUserIdDatabase(dbHandler);
        String sqlInsert="INSERT INTO \"PartyPlaylistBattle\".library( " +
                "userid) VALUES (?)";
        PreparedStatement preparedStatement= dbHandler.getConnection().prepareStatement(sqlInsert);
        preparedStatement.setInt(1,userID);
        ResultSet resultSet=preparedStatement.executeQuery();
    }

    public void getLibraryID(DatabaseHandler dbHandler)throws SQLException{
        getUserIdDatabase(dbHandler);
        String sqlInsert="select libraryid from \"PartyPlaylistBattle\".library " +
                "where userid=?";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(sqlInsert);
        preparedStatement.setInt(1,userID);
        ResultSet resultSet=preparedStatement.executeQuery();
        while (resultSet.next()){
            lib.setLibraryID(resultSet.getInt("libraryid"));
        }
    }

    public void showUserStats(DatabaseHandler dbHandler) throws SQLException {
        statsManager.showUserStats(username,dbHandler);
    }

    public void showUserScoreboard(DatabaseHandler dbHandler)throws SQLException{
        getUserIdDatabase(dbHandler);
        statsManager.showScoreboard(dbHandler,this);
    }

    public void fillActionsList(String payload){
        if (!actions.isEmpty()){
            actions.clear();
        }
        for (int i=0;i<payload.length();i++){
            actions.add(payload.charAt(i));
        }
    }

    public void fillActionsRandomly(){
        int max=3;
        int min=1;
        int temp;
        if (!actions.isEmpty()){
            actions.clear();
        }
        for (int i=0;i<5;i++){
            temp = (int)(Math.random() * (max - min + 1) + min);
            if (temp==1) {
                actions.add('R');
            }
            else if(temp==2) {
                actions.add('S');
            }
            else if(temp==3){
                actions.add('P');
            }
        }
    }

    public void printActions(){
        if (actions.isEmpty()){
            System.out.println("No actions configured");
            return;
        }
        String values= Arrays.toString(actions.toArray());
        System.out.println(values);
    }


}
