package com.Party_Playlist_Battle.playlist_and_library;

import com.Party_Playlist_Battle.server.DatabaseHandler;
import com.Party_Playlist_Battle.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Library {

    private int libraryID;

    public int getLibraryID() {
        return libraryID;
    }

    public void setLibraryID(int libraryID) {
        this.libraryID = libraryID;
    }


    public void printLibrary(DatabaseHandler dbHandler,String username) throws SQLException {
        String sqlSelect="select * from \"PartyPlaylistBattle\".\"mediacontent\" as m " +
                "join \"PartyPlaylistBattle\".\"library\" as l " +
                "on m.\"libraryid\"=l.\"libraryid\" " +
                "join \"PartyPlaylistBattle\".\"user\" as u " +
                "on l.\"userid\"=u.\"userid\" " +
                "where u.\"username\"=? ";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(sqlSelect);
        preparedStatement.setString(1,username);
        ResultSet resultSet=preparedStatement.executeQuery();
        if(resultSet.next()){
            System.out.println("Title: "+resultSet.getString("title")+", Length: "+
                    resultSet.getString("length")+", Source: "+resultSet.getString("contentsource"));
        }
        else {
            System.out.println("Library Empty!!");
        }
        while (resultSet.next()){
            System.out.println("Title: "+resultSet.getString("title")+", Length: "+
                    resultSet.getString("length")+", Source: "+resultSet.getString("contentsource"));
        }
    }

    public void deleteMediaContent(String fileName, DatabaseHandler dbHandler, User user)throws SQLException {
        user.getUserIdDatabase(dbHandler);
        String deleteSql="delete from \"PartyPlaylistBattle\".mediacontent " +
                " where filename = ? AND libraryid = (select libraryid from \"PartyPlaylistBattle\".library " +
                " where userid = ?)";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(deleteSql);
        preparedStatement.setString(1,fileName);
        preparedStatement.setInt(2,user.getUserID());
        preparedStatement.executeUpdate();
    }
}
