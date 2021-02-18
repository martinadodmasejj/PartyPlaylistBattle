package com.Monster_Card_Game.user;

import com.Monster_Card_Game.server.DatabaseHandler;

import javax.jws.soap.SOAPBinding;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class StatsManager {
    public void updateUserStats(int winnerID,int loserID,boolean draw,DatabaseHandler dbHandler) throws SQLException {
        String winSql="\tUPDATE \"MonsterCardGame\".\"stats\" " +
                "set \"wins\"=\"wins\"+1, \"elo\"=\"elo\"+3 " +
                "where \"userid\"=?";
        String loseSql="UPDATE \"MonsterCardGame\".\"stats\" " +
                "set \"losses\"=\"losses\"+1, \"elo\"=\"elo\"-5 " +
                "where \"userid\"=?";
        String drawSql="UPDATE \"MonsterCardGame\".\"stats\" " +
                "set \"draws\"=\"draws\"+1 " +
                "where \"userid\"=?";
        if (draw){
            PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(drawSql);
            preparedStatement.setInt(1,winnerID);
            preparedStatement.executeUpdate();
            preparedStatement=dbHandler.getConnection().prepareStatement(drawSql);
            preparedStatement.setInt(1,loserID);
            preparedStatement.executeUpdate();
        }
        else{
            PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(winSql);
            preparedStatement.setInt(1,winnerID);
            preparedStatement.executeUpdate();
            preparedStatement=dbHandler.getConnection().prepareStatement(loseSql);
            preparedStatement.setInt(1,loserID);
            preparedStatement.executeUpdate();
        }

    }

    public void showUserStats(String username,DatabaseHandler dbHandler) throws SQLException {
        String sql="SELECT \"username\",\"wins\",\"losses\",\"elo\",\"draws\" FROM " +
                "\"MonsterCardGame\".\"stats\" as s " +
                "join \"MonsterCardGame\".\"user\" as u " +
                "on s.\"userid\"=u.\"userid\" " +
                "where u.\"username\"=?";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(sql);
        preparedStatement.setString(1,username);
        ResultSet resultSet=preparedStatement.executeQuery();
        while (resultSet.next()){
            System.out.println("User: "+username);
            System.out.println("ELO: "+resultSet.getString("elo"));
            System.out.println("Wins: "+resultSet.getString("wins"));
            System.out.println("Losses: "+resultSet.getString("losses"));
            System.out.println("Draws: "+resultSet.getString("draws"));
        }
    }

    public void insertBattleLog(DatabaseHandler dbHandler,int winnerID,int loserID,boolean draw)throws SQLException{
        String insertIntoBattleLog="INSERT INTO \"MonsterCardGame\".battlelog( " +
                "winnerid, loserid,  draw) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(insertIntoBattleLog);
        preparedStatement.setInt(1,winnerID);
        preparedStatement.setInt(2,loserID);
        preparedStatement.setBoolean(3,draw);
        preparedStatement.executeUpdate();
    }

    public void showScoreboard(DatabaseHandler dbHandler, User user)throws  SQLException{
        String selectSql="select * from \"MonsterCardGame\".\"battlelog\" " +
                "where \"winnerid\"=? OR \"loserid\"=?";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(selectSql);
        preparedStatement.setInt(1,user.getUserID());
        preparedStatement.setInt(2,user.getUserID());
        ResultSet resultSet=preparedStatement.executeQuery();
        while (resultSet.next()){
            System.out.println("Username: "+user.getUsername());
            if(resultSet.getBoolean("draw")==true) {
                System.out.println("Draw");
            }
            else if(resultSet.getInt("winnerid")==user.getUserID()){
                System.out.println("Won a Battle");
            }
            else {
                System.out.println("Lost a Battle");
            }
            System.out.println("Battle time: "+resultSet.getString("battletime"));
        }
    }


}
