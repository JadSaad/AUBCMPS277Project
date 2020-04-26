package com.example.songsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Intent;
import android.app.ProgressDialog;


import com.example.songsapp.R;

import java.sql.*;

public class CreateAccountActivity extends AppCompatActivity {
    EditText Firstname, Lastname, Username, Password, Retype;
    Button Create;
    TextView result;

    private class User{
        String firstname;
        String lastname;
        String username;
        String password;
    }

    private enum CreateAccountResult{Successful, UserAlreadyExists, Fail };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide();
        Firstname = findViewById(R.id.editTextFirstName);
        Lastname = findViewById(R.id.editTextLastName);
        Username =  findViewById(R.id.editTextCreateAccountUsername);
        Password =  findViewById(R.id.editTextCreateAccountPassword);
        Create =  findViewById(R.id.buttonCreateInCreate);
        result =  findViewById(R.id.textViewCreateAccount);
        Retype =  findViewById(R.id.editTextRetypePass);

        Create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(Firstname.getText().toString().isEmpty() || Lastname.getText().toString().isEmpty() || Username.getText().toString().isEmpty()
                || Password.getText().toString().isEmpty()){
                   AlertDialog alertDialog = new AlertDialog.Builder(CreateAccountActivity.this).create();
                    alertDialog.setTitle("ATTENTION");
                    alertDialog.setMessage("All Fields Are Required!");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else if(!(Retype.getText().toString().equals(Password.getText().toString()))){
                    AlertDialog alertDialog = new AlertDialog.Builder(CreateAccountActivity.this).create();
                    alertDialog.setTitle("ATTENTION");
                    alertDialog.setMessage("Passwords Not Matching");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                else{
                    User thisUser = new User();
                    thisUser.lastname = Lastname.getText().toString();
                    thisUser.firstname = Firstname.getText().toString();
                    thisUser.username = Username.getText().toString();
                    thisUser.password = Password.getText().toString();
                    new CreateAccountTask().execute(thisUser);

                }
            }
        });
    }
    public class CreateAccountTask extends AsyncTask<User, Void, CreateAccountResult> {
        ProgressDialog dialog = new ProgressDialog(CreateAccountActivity.this);
        @Override
        protected void onPreExecute() {
            dialog.setMessage("Creating Account");
            dialog.show();
        }

        @Override
        protected CreateAccountResult doInBackground(User... params) {
            try {
                ConnectionClass c = new ConnectionClass();
                Connection con = c.CONN();
                Statement st = con.createStatement();
                ResultSet users = st.executeQuery("select * from users where username = '" + params[0].username + "';");
                while (users.next()) {
                    return CreateAccountResult.UserAlreadyExists;
                }
                PreparedStatement psst = con.prepareStatement("INSERT INTO users (username, password, firstname, lastname) " +
                        "VALUES (?, ?, ?, ?" +
                        ");");
                psst.setString(1,params[0].username);
                psst.setString(2,params[0].password);
                psst.setString(3,params[0].firstname);
                psst.setString(4,params[0].lastname);
                psst.execute();
                con.close();
                return CreateAccountResult.Successful;

            } catch (Exception e) {
                return CreateAccountResult.Fail;
            }
        }


        @Override
        protected void onPostExecute(CreateAccountResult result) {
            dialog.dismiss();
            String message = "Unknown Error Occurred :(";
            if(result == CreateAccountResult.Successful){
                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                if (result == CreateAccountResult.UserAlreadyExists) message = "User ALready Exusts";
                AlertDialog alertDialog = new AlertDialog.Builder(CreateAccountActivity.this).create();
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
