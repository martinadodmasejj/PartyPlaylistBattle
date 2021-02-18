package com.Monster_Card_Game.tests;

import com.Monster_Card_Game.server.RequestContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class RequestContextTest {
    @Test
    public void testHTTPVerb() throws IOException {
        //Arrange
        RequestContext handler=new RequestContext();
        //Act
        handler.setHTTPHeader("GET /messages HTTP/1.1\n" +
                "Host: localhost:1111\n" +
                "User-Agent: curl/7.71.1\n" +
                "Accept: */*\n" +
                "Content-Type: application/json\n" +
                "Content-Length: 59\n");
        String actual=handler.readHTTPVerb();
        //Assert
        Assert.assertEquals("GET", actual);
    }
    @Test
    public void testRequest() throws IOException {
        //Arrange
        RequestContext handler=new RequestContext();
        //Act
        handler.setHTTPHeader("GET /messages HTTP/1.1\n" +
                "Host: localhost:1111\n" +
                "User-Agent: curl/7.71.1\n" +
                "Accept: */*\n" +
                "Content-Type: application/json\n" +
                "Content-Length: 59\n");
        String actual=handler.readRequest();
        //Assert
        Assert.assertEquals("messages ", actual);
    }


    /*
    @Test

    public void testReadHeader() throws IOException{
        //Arrange
        BufferedReader in= Mockito.mock(BufferedReader.class);
        RequestContext handler=new RequestContext();
        Mockito.when(in.readLine()).thenReturn("POST http://localhost:10001/sessions --header",
                "\"Content-Type: application/json\""
                ,"-d \"{\\\"Username\\\":\\\"kienboec\\\", \\\"Password\\\":\\\"different\\\"}\"","");
        //Act
        String header=handler.readHeader(in);
        //Assert
     */
        //Assert.assertEquals("POST /sessions HTTP/1.1\n" +
          //      "Host: localhost:10001\n" +
            //    "User-Agent: curl/7.71.1\n" +
               //  "Accept: */*\n" +
    //             "Content-Type: application/json\n" +
    //             "Content-Length: 47",header);

   // }

    /*
    @Test
    public void testReadPayload() throws IOException{
        //Arrange
        BufferedReader in= Mockito.mock(BufferedReader.class);
        RequestContext handler=new RequestContext();
        Mockito.when(in.readLine()).thenReturn("POST http://localhost:10001/sessions --header",
                "\"Content-Type: application/json\""
                ,"-d \"{\\\"Username\\\":\\\"kienboec\\\", \\\"Password\\\":\\\"different\\\"}\"","");
        //Act
        handler.readHeader(in);
        String payload=handler.readPayload(in);
        //Assert
        Assert.assertEquals("{\\\"Username\\\":\\\"kienboec\\\", \\\"Password\\\":\\\"different\\\"}\"",payload);
    }*/

    @Test
    public void testReadHeader() throws IOException{
        //Arrange
        ServerSocket test=new ServerSocket(999);
        RequestContext handler=new RequestContext();
        Runtime.getRuntime().exec("curl -X POST http://localhost:999/sessions " +
                "--header \"Content-Type: application/json\" " +
                "-d \"{\\\"Username\\\":\\\"kienboec\\\", \\\"Password\\\":\\\"different\\\"}\"");
        Socket clientSocket=test.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Accept");
        //Act
        String header=handler.readHeader(in);
        test.close();
        clientSocket.close();
        //Assert
        Assert.assertEquals("POST /sessions HTTP/1.1\n" +
                "Host: localhost:999\n" +
                "User-Agent: curl/7.55.1\n" +
                "Accept: */*\n" +
                "Content-Type: application/json\n" +
                "Content-Length: 47\n",header);
    }

    @Test
    public void testReadPayload() throws IOException{
        //Arrange
        ServerSocket test=new ServerSocket(999);
        RequestContext handler=new RequestContext();
        Runtime.getRuntime().exec("curl -X POST http://localhost:999/sessions " +
                "--header \"Content-Type: application/json\" " +
                "-d \"{\\\"Username\\\":\\\"kienboec\\\", \\\"Password\\\":\\\"different\\\"}\"");
        Socket clientSocket=test.accept();
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Accept");
        //Act
        handler.readHeader(in);
        String payload=handler.readPayload(in);
        test.close();
        clientSocket.close();
        //Assert
        Assert.assertEquals("{\"Username\":\"kienboec\", \"Password\":\"different\"}",payload);
    }
}
