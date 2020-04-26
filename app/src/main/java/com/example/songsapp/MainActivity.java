package com.example.songsapp;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;

import java.sql.*;
public class MainActivity extends AppCompatActivity {
    Button buttonLogIn, buttonCreateAccount;
    EditText editTextUsername, editTextPassword;

    private class LogInInfo {
        String username;
        String password;
    }

    private enum LogInResult{UserDoesntExist, WrongPassword, Successful, Fail}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        buttonLogIn = (Button) findViewById(R.id.buttonLogIn);
        buttonCreateAccount = (Button) findViewById(R.id.buttonCreateAccount);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                if(username.isEmpty() || password.isEmpty()){
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("ATTENTION");
                    alertDialog.setMessage("Please Enter All Fields!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else {
                    LogInInfo logInInfo = new LogInInfo();
                    logInInfo.username = username;
                    logInInfo.password = password;
                    new LogInTask().execute(logInInfo);
                }
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    public class LogInTask extends AsyncTask<LogInInfo, Void, LogInResult> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Logging In");
            dialog.show();
        }

        @Override
        protected LogInResult doInBackground(LogInInfo... params) {
            LogInResult result = LogInResult.Fail;
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet users = st.executeQuery("select * from users where username = '" + params[0].username + "';");
                boolean userExists = false;
                while (users.next()) {
                    userExists = true;
                    if (users.getString("password").equals(params[0].password)) {
                        result = LogInResult.Successful;
                        UserInfo.shared.username = users.getString("username");
                        UserInfo.shared.firstname = users.getString("firstname");
                        UserInfo.shared.lastname = users.getString("lastname");
                    }
                    else result= LogInResult.WrongPassword;
                }
                if (!userExists) result = LogInResult.UserDoesntExist;
                con.close();
            } catch (Exception e) {
                result = LogInResult.Fail;
                Log.e("log in error", e.getMessage());
            }
            return result;
        }


        @Override
        protected void onPostExecute(LogInResult result) {
            dialog.dismiss();
            String message = "Error!";
            if (result == LogInResult.Successful) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (result == LogInResult.UserDoesntExist) message = "User does not exist. Create an account";
                if (result == LogInResult.WrongPassword) message = "Wrong Password! Please try again";
                editTextUsername.setText("");
                editTextPassword.setText("");
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage(message);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }
}
