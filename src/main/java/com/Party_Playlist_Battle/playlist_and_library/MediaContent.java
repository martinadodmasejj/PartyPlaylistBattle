package com.Party_Playlist_Battle.playlist_and_library;
import com.Party_Playlist_Battle.enums.genres;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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



}
