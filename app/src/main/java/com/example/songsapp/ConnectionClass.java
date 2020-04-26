package com.example.songsapp;
import android.os.StrictMode;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionClass {
    String classs = "com.mysql.jdbc.Driver";
    String url = "jdbc:mysql://10.0.0.25:3306/bjwdatabase";
    String un = "root";
    String password = "root";
    public Connection CONN() throws Exception{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn;
        Class.forName(classs);
        conn = DriverManager.getConnection(url, un, password);
        return conn;
    }
}

