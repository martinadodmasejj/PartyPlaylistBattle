package com.Party_Playlist_Battle.playlist_and_library;

import com.Party_Playlist_Battle.server.DatabaseHandler;
import com.Party_Playlist_Battle.user.User;

import javax.management.relation.RelationSupport;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Playlist {
    List<MediaContent> playlist=new ArrayList<>();

    public void addContentPlaylist(String fileName, DatabaseHandler dbHandler, User user) throws SQLException {
        MediaContent temp=null;
        String selectSql="select * from \"PartyPlaylistBattle\".mediacontent as m " +
                "join \"PartyPlaylistBattle\".library as l " +
                "on l.libraryid=m.libraryid " +
                "join \"PartyPlaylistBattle\".user as u " +
                "on l.userid=u.userid " +
                "where filename = ? AND u.userid=? ";
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(selectSql);
        preparedStatement.setString(1,fileName);
        preparedStatement.setInt(2,user.getUserID(dbHandler));
        ResultSet resultSet=preparedStatement.executeQuery();
        while (resultSet.next()){
            String tempFileName=resultSet.getString("filename");
            String source=resultSet.getString("contentsource");
            int rating=resultSet.getInt("rating");
            String genre=resultSet.getString("genre");
            String title=resultSet.getString("title");
            String album=resultSet.getString("album");
            String length=resultSet.getString("length");
            temp=new MediaContent(tempFileName,source,rating,genre,title,album,length);
        }
        if(temp!=null) {
            playlist.add(temp);
        }
        else {
            System.out.println("Song not available");
        }
       // MediaContent temp=new MediaContent(fileName)
    }

    public void printPlaylist(){
        if (playlist.isEmpty()){
            System.out.println("Empty playlist");
        }
        else {
            for (int i=0;i<playlist.size();i++){
                MediaContent temp=playlist.get(i);
                System.out.println("Filename: "+temp.getFileName()+" Title: "+temp.getTitle()+" Rating: "+temp.getRating()
                +" Length: "+temp.getLength());
            }
        }
    }
}
