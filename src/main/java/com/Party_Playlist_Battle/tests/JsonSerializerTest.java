package com.Party_Playlist_Battle.tests;

import com.Party_Playlist_Battle.server.JsonSerializer;
import com.Party_Playlist_Battle.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;

public class JsonSerializerTest {

    @Test
    public  void testUserSerialization() throws IOException {
        //Arrange
        JsonSerializer jsonSerializer=new JsonSerializer();
        BufferedReader in= Mockito.mock(BufferedReader.class);
        Mockito.when(in.readLine()).thenReturn("{\"Username\":\"admin\",\"Password\":\"istrator\"}");
        //Act
        User testUser=jsonSerializer.convertUserToObject(in.readLine());
        //Assert
        Assert.assertEquals("admin",testUser.getUsername());
        Assert.assertEquals("istrator",testUser.getPassword());
    }

}
