package com.example.bookitapp;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
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

    public class User {
        String username;
        String Password;
    }

    public class file {
        UserInfo user;
        String message;

        public file(UserInfo user, String message) {
            this.user = user;
            this.message = message;
        }
    }


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
                    User thisUser = new User();
                    thisUser.username = username;
                    thisUser.Password = password;
                    new LogInTask().execute(thisUser);
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

    public class LogInTask extends AsyncTask<User, Void, file> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Logging In");
            dialog.show();
        }

        @Override
        protected file doInBackground(User... params) {
            file result = new file(null,null);
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet accrs = st.executeQuery("select * from accounts");
                boolean userExists = false;
                while (accrs.next()) {
                    if (accrs.getString("Username").equals(params[0].username)) {
                        userExists = true;
                        if (accrs.getString("Password").equals(params[0].Password)) {
                            result.message="Logged in Successfully";
                            result = new file(new UserInfo(accrs.getInt("UserID"),accrs.getString("Username"),accrs.getString("Password"),accrs.getString("FirstName"),accrs.getString("LastName")),"Logged in Successfully");
                        } else result.message="Wrong Password";
                    }
                }
                if (!userExists) result.message = "User doesn't exist";
                con.close();
            } catch (Exception e) {
                result.message = "Error";
            }
            return result;
        }


        @Override
        protected void onPostExecute(file s) {
            dialog.dismiss();
            if (s.message.equals("Logged in Successfully")) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("user", s.user);
                startActivity(intent);
                //finish();
            } else {
                editTextUsername.setText("");
                editTextPassword.setText("");
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage(s.message);
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
