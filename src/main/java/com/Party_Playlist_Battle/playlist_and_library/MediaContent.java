package com.Party_Playlist_Battle.playlist_and_library;
import com.Party_Playlist_Battle.enums.genres;
import com.Party_Playlist_Battle.server.DatabaseHandler;
import com.Party_Playlist_Battle.user.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MediaContent {

    String fileType;
    String source;
    String fileName;
    String title;
    String artist;
    String album;
    int rating;
    genres genre;
    String length;


    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public genres getGenre() {
        return genre;
    }

    public void setGenre(genres genre) {
        this.genre = genre;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }


    @JsonCreator
    MediaContent(@JsonProperty("Name")String fileName, @JsonProperty("Url")String source, @JsonProperty("Rating")int rating
    ,@JsonProperty("Genre")String genre, @JsonProperty("Title")String title,@JsonProperty("Album")String album,@JsonProperty("Length")String length){
        this.fileName=fileName;
        this.source=source;
        this.rating=rating;
        if(genre.compareTo("Pop")==0){
            this.genre=genres.Pop;
        }
        else if(genre.compareTo("Game Music")==0){
            this.genre=genres.GameMusic;
        }
        this.title=title;
        if(album!=null){
            this.album=album;
        }
        if(length!=null){
            this.length=length;
        }
    }

    public void uploadMediaContent(DatabaseHandler dbHandler, User user) throws SQLException {
        String insertSql="INSERT INTO \"PartyPlaylistBattle\".mediacontent(  " +
                "filename, contentsource, rating, genre, title, libraryid, length, album)  " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        user.getLibraryID(dbHandler);
        PreparedStatement preparedStatement=dbHandler.getConnection().prepareStatement(insertSql);
        preparedStatement.setString(1,fileName);
        preparedStatement.setString(2,source);
        preparedStatement.setInt(3,rating);
        preparedStatement.setString(4,genre.toString());
        preparedStatement.setString(5,title);
        preparedStatement.setInt(6,user.lib.getLibraryID());
        preparedStatement.setString(7,length);
        preparedStatement.setString(8,album);
        preparedStatement.executeUpdate();
    }




}
