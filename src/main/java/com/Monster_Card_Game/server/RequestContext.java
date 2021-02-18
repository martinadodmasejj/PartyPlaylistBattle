package com.Monster_Card_Game.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class RequestContext {
    private StringBuilder payload;
    private String headerInfo;
    public String ServerResponse="HTTP/1.1 200 OK.\r\n"+
            "Server: Denis\r\n"+
            "Content-Type: text/html\r\n"+
            "Accept-Ranges: bytes\r\n"+
            "Content-Length:1\r\n\r\n";

    public String readHeader(BufferedReader in) throws IOException {
        System.out.println();
        headerInfo = "";
        String line = in.readLine();

        while (true) {
            if(line==null){
                line=in.readLine();
                continue;
            }
            if (line != null) {
                System.out.println(line);
                if (line.compareTo("null") == 0) {
                    line = in.readLine();
                    continue;
                }
                if (line.length() == 0) {
                    break;
                }
                headerInfo = headerInfo + line + "\n";
                line = in.readLine();
            }
//        headerInfo=headerInfo.replace("null","");
        }
        return headerInfo;
    }

    public void setHTTPHeader(String input){
        headerInfo=input;
    }
    public int saveHTTPHeader(List<String> list,BufferedReader in) throws IOException {
        list.add(headerInfo);
        return list.size();
    }

    public void updateHTTPHeader(int position,List<String> list){
        if(list.isEmpty()){
            System.out.println("Messages are Empty!!");
            return;
        }
        list.set(position,headerInfo);
    }

    public void deleteHTTPHeader(int position,List<String> list){
        if(list.isEmpty()){
            System.out.println("Messages are Empty!!");
            return;
        }
        list.remove(position);
    }

    public String readHTTPVerb() throws IOException {
        String verb="";
        for (int i=0;i<headerInfo.length();i++){
            if(headerInfo.charAt(i)==' '){
                break;
            }
            verb+=headerInfo.charAt(i);
        }
        return verb;
    }

    public String readRequest()throws IOException{
        String request="";
        for (int i=0;i<headerInfo.length();i++){
            if(headerInfo.charAt(i)=='/'){
                for (int j=i+1;j<headerInfo.length();j++){
                    if(headerInfo.charAt(j)==' '){
                        return request;
                    }
                    request+=headerInfo.charAt(j);
                }
            }
        }
        return request;
    }

    public String readPayload(BufferedReader in)throws IOException{
        payload = new StringBuilder();
        while(in.ready()){
            payload.append((char) in.read());
        }
        // System.out.println("Payload data is: "+payload.toString());
        return payload.toString();
    }

    public int savePayload(List<String> list,BufferedReader in) throws IOException {
        readPayload(in);
        list.add(payload.toString());
        System.out.println("Payload: "+payload.toString());
        System.out.println("\n");
        payload=null; // empty payload
        return list.size();
    }

}
