package com.example.songsapp;
import android.os.StrictMode;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionClass {
    String classs = "com.mysql.jdbc.Driver";// dont change
    String ip = "10.0.0.25";// confugure this
    String database = "bjwdatabase";//configure this
    String url = "jdbc:mysql://" + ip + ":3306/" + database;//dont change
    String un = "root";//configure this
    String password = "root";//configure this
    public Connection CONN() throws Exception{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn;
        Class.forName(classs);
        conn = DriverManager.getConnection(url, un, password);
        return conn;
    }
}

