package com.Monster_Card_Game.server;

import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

public class DatabaseHandler {
    String jdbcURL;
    String username;
    String password;
    private Connection connection;
    PasswordHasher pwHasher=new PasswordHasher();
    Statement stmt;
    public DatabaseHandler() throws SQLException {
        jdbcURL="jdbc:postgresql://localhost:5432/MonsterCardGame";
        username="postgres";
        password="root";
        connection = DriverManager.getConnection(jdbcURL,username,password);
        //System.out.println("Database Connected");
    }
    public Connection getConnection(){
        return connection;
    }
    public void createUser(String username,String password) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        password=pwHasher.generateStrongPasswordHash(password);
        String insertStatement="INSERT INTO \"MonsterCardGame\".\"user\" (\"username\",\"password\") " +
                "VALUES (\'"+username+"\',\'"+password+"\')";
        stmt=connection.createStatement();
        stmt.executeUpdate(insertStatement);
    }
    public boolean validateUser(String username,String password) throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        String db_pass="";
        String selectStatement="Select \"password\" from \"MonsterCardGame\".\"user\" "
        +"where \"username\"=?";
        PreparedStatement preparedStatement=connection.prepareStatement(selectStatement);
        preparedStatement.setString(1,username);
        ResultSet resultSet=preparedStatement.executeQuery();
        while (resultSet.next()) {
            db_pass=resultSet.getString("password");
        }
        return pwHasher.validatePassword(password,db_pass);
    }
}
