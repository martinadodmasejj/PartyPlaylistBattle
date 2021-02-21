package com.Party_Playlist_Battle.user;

import com.Party_Playlist_Battle.server.DatabaseHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class StatsManager {
    public void updateUserStats(int winnerID,int loserID,DatabaseHandler dbHandler) throws SQLException {
        String winSql="\tUPDATE \"PartyPlaylistBattle\".\"stats\" " +
                "set \"wins\"=\"wins\"+1 " +
                "where \"userid\"=?";
        String loseSql="UPDATE \"PartyPlaylistBattle\".\"stats\" " +
                "set \"losses\"=\"losses\"+1 " +
                "where \"userid\"=?";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(winSql);
        preparedStatement.setInt(1,winnerID);
        preparedStatement.executeUpdate();
        preparedStatement=dbHandler.getConnection().prepareStatement(loseSql);
        preparedStatement.setInt(1,loserID);
        preparedStatement.executeUpdate();
    }

    public void showUserStats(String username,DatabaseHandler dbHandler) throws SQLException {
        String sql="SELECT \"username\",\"wins\",\"losses\" FROM " +
                "\"PartyPlaylistBattle\".\"stats\" as s " +
                "join \"PartyPlaylistBattle\".\"user\" as u " +
                "on s.\"userid\"=u.\"userid\" " +
                "where u.\"username\"=?";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(sql);
        preparedStatement.setString(1,username);
        ResultSet resultSet=preparedStatement.executeQuery();
        while (resultSet.next()){
            System.out.println("User: "+username);
            System.out.println("Wins: "+resultSet.getString("wins"));
            System.out.println("Losses: "+resultSet.getString("losses"));
        }
    }

    public void insertBattleLog(int winnerID,int loserID,DatabaseHandler dbHandler)throws SQLException{
        String insertIntoBattleLog="INSERT INTO \"PartyPlaylistBattle\".battlelog( " +
                "winnerid, loserid) VALUES (?, ?)";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(insertIntoBattleLog);
        preparedStatement.setInt(1,winnerID);
        preparedStatement.setInt(2,loserID);
        preparedStatement.executeUpdate();
    }

    public void showScoreboard(DatabaseHandler dbHandler, User user)throws  SQLException{
        String selectSql="select * from \"PartyPlaylistBattle\".\"battlelog\" " +
                "where \"winnerid\"=? OR \"loserid\"=?";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(selectSql);
        preparedStatement.setInt(1,user.getUserID(dbHandler));
        preparedStatement.setInt(2,user.getUserID(dbHandler));
        ResultSet resultSet=preparedStatement.executeQuery();
        while (resultSet.next()){
            System.out.println("Username: "+user.getUsername());
            if(resultSet.getInt("winnerid")==user.getUserID(dbHandler)){
                System.out.println("Won a Battle");
            }
            else {
                System.out.println("Lost a Battle");
            }
            System.out.println("Battle time: "+resultSet.getString("battletime"));
        }
    }


}
