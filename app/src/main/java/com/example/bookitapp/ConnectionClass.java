package com.example.bookitapp;
import android.os.StrictMode;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionClass {
    String classs = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://192.168.43.112:3306/bookit";
    String un = "root";
    String password = "root";
    public Connection CONN() throws Exception{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        Class.forName(classs);
        conn = DriverManager.getConnection(url, un, password);
        return conn;
    }
}

