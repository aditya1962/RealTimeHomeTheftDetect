package com.firstapplication.aditya.theftdetection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firstapplication.aditya.theftdetection.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aditya on 11/29/2016.
 */
public class ForgotPassword extends AppCompatActivity {

    String strOne = "", strTwo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        setContentView(R.layout.resetpassword);

        Button reset = (Button) findViewById(R.id.reset);


        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText username = (EditText) findViewById(R.id.enterUsername);
                EditText passwordOne = (EditText) findViewById(R.id.enterPasswordOne);
                EditText passwordTwo = (EditText) findViewById(R.id.enterPasswordTwo);

                boolean invalidOne = checkBlanks(username.getText().toString());
                boolean invalidTwo = checkBlanks(passwordOne.getText().toString());
                boolean invalidThree = checkBlanks(passwordTwo.getText().toString());
                boolean invalidFour = checkString(username.getText().toString());
                boolean invalidFive = checkString(passwordOne.getText().toString());
                boolean invalidSix = checkString(passwordTwo.getText().toString());

                if (invalidOne == true) {
                    username.setError("Username cannot be blank");
                }
                if (invalidTwo == true) {
                    passwordOne.setError("New Password cannot be blank");
                }
                if (invalidThree == true) {
                    passwordTwo.setError("Confirm Password cannot be blank");
                }
                if (invalidFour == true) {
                    username.setError("Username cannot be a number");
                }
                if (invalidFive == true) {
                    passwordOne.setError("New Password cannot be a number");
                }
                if (invalidSix == true) {
                    passwordTwo.setError("Confirm Password cannot be a number");
                }
                if (!(passwordTwo.getText().toString().equals(passwordOne.getText().toString()))) {
                    passwordTwo.setError("Confirm Password does not match new password");
                }

                if (invalidOne == false && invalidTwo == false && invalidThree == false && invalidFour == false && invalidFive == false && invalidSix == false) {

                    strOne = username.getText().toString();
                    strTwo = passwordOne.getText().toString();

                    Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Login/" + strOne);

                    mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
                        @Override
                        public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                Firebase mRefTwo = new Firebase("https://theftdetection-973a2.firebaseio.com/");
                                Firebase tableName = mRefTwo.child("Login");
                                Firebase username = tableName.child(strOne);
                                Firebase password = username.child("Password");
                                password.setValue(strTwo);
                                Toast.makeText(ForgotPassword.this, "Password reset successful", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ForgotPassword.this, Login.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(ForgotPassword.this, "Username does not exist", Toast.LENGTH_LONG).show();
                            }
                        }


                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }

                    });

                }
            }
        });
    }



    public boolean checkBlanks(String text) {
        boolean invalid;
        if (text == null || text.isEmpty()) {
            invalid = true;
        } else {
            invalid = false;
        }
        return invalid;
    }

    public boolean checkString(String text) {
        boolean invalid;
        try {
            double number = Double.parseDouble(text);
            invalid = true;
        } catch (NumberFormatException e) {
            invalid = false;
        }

        return invalid;
    }

}
