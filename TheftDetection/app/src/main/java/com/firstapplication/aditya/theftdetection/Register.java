package com.firstapplication.aditya.theftdetection;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import javax.crypto.*;


import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Aditya on 11/29/2016.
 */
public class Register extends AppCompatActivity {

    Cipher encryptedCipher;

    boolean imagePicked = false,invalidImg = false, loadInstance = false;
    String strOne = "", strTwo = "", strThree="", strFour = "", strFive="", strSix = "", imgString, imgFile ,encryptedPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        Firebase.setAndroidContext(this);
        setContentView(R.layout.signup);

        Button register = (Button) findViewById(R.id.signup);


       

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                EditText username = (EditText) findViewById(R.id.enterUsername);
                EditText password = (EditText) findViewById(R.id.enterPassword);
                EditText fullname = (EditText) findViewById(R.id.enterFullName);
                EditText age = (EditText) findViewById(R.id.enterAge);
                EditText address = (EditText) findViewById(R.id.enterAddress);
                EditText email = (EditText) findViewById(R.id.enterEmail);


                boolean invalidOne = checkBlanks(username.getText().toString());
                boolean invalidTwo = checkBlanks(password.getText().toString());
                boolean invalidThree = checkBlanks(fullname.getText().toString());
                boolean invalidFour = checkBlanks(age.getText().toString());
                boolean invalidFive = checkBlanks(address.getText().toString());
                boolean invalidSix = checkBlanks(email.getText().toString());
                boolean invalidSeven = checkString(username.getText().toString());
                boolean invalidEight = checkString(password.getText().toString());
                boolean invalidNine = checkString(fullname.getText().toString());
                boolean invalidTen = checkString(age.getText().toString());
                boolean invalidEleven = checkString(address.getText().toString());
                boolean invalidTwelve = validate(email.getText().toString());


                if (invalidOne == true) {
                    username.setError("Username cannot be blank");
                }
                if (invalidTwo == true) {
                    password.setError("Password cannot be blank");
                }
                if (invalidThree == true) {
                    fullname.setError("Fullname cannot be blank");
                }
                if (invalidFour == true) {
                    age.setError("Age cannot be blank");
                }
                if (invalidFive == true) {
                    address.setError("Address cannot be blank");
                }
                if (invalidSix == true) {
                    email.setError("Email cannot be blank");
                }
                if (invalidSeven == true) {
                    username.setError("Username cannot be a number");
                }
                if (invalidEight == true) {
                    password.setError("Password cannot be a number");
                }
                if (invalidNine == true) {
                    fullname.setError("Fullname cannot be a number");
                }
                if (invalidTen == false) {
                    age.setError("Age cannot be text");
                }
                if (invalidEleven == true) {
                    address.setError("Address cannot be a number");
                }
                if (invalidTwelve == false) {
                    email.setError("Invalid Email address");
                }
                

                if (invalidOne == false && invalidTwo == false && invalidThree == false && invalidFour == false
                        && invalidFive == false && invalidSix == false && invalidSeven == false && invalidEight == false
                        && invalidNine == false && invalidTen == true && invalidEleven == false && invalidTwelve == true) {

                    strOne = username.getText().toString();
                    strTwo = password.getText().toString();

                    try {
                        encryptedPassword = Base64.encodeToString(strTwo.getBytes("UTF-8"), Base64.DEFAULT);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    strThree = fullname.getText().toString();
                    strFour = age.getText().toString();
                    strFive = address.getText().toString();
                    strSix = email.getText().toString();


                    Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Login/" + strOne);

                    mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
                        @Override
                        public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                            if (loadInstance == false && dataSnapshot.hasChildren()) {
                                Toast.makeText(Register.this, "Username exists", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                                Toast.makeText(Register.this, "Click Forgot Password if you have forgotten your password", Toast.LENGTH_LONG).show();

                            } else {
                                Firebase mRefTwo = new Firebase("https://theftdetection-973a2.firebaseio.com/");

                                //adding data to user table

                                Firebase tableOne = mRefTwo.child("User");
                                Firebase username = tableOne.child(strOne);
                                Firebase fullname = username.child("Fullname");
                                fullname.setValue(strThree);
                                Firebase age = username.child("Age");
                                age.setValue(strFour);
                                Firebase address = username.child("Address");
                                address.setValue(strFive);
                                Firebase email = username.child("Email");
                                email.setValue(strSix);

                                //Adding data to login table

                                Firebase tableTwo = mRefTwo.child("Login");
                                Firebase user = tableTwo.child(strOne);
                                Firebase password = user.child("Password");
                                password.setValue(encryptedPassword);

                                loadInstance = true;
                                Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();

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




    public boolean checkBlanks(String text)
    {
        boolean invalid;
        if(text == null || text.isEmpty()){
            invalid = true;
        }
        else
        {
            invalid = false;
        }
        return invalid;
    }

    public boolean checkString(String text)
    {
        boolean invalid;
        try
        {
            double number = Double.parseDouble(text);
            invalid = true;
        }
        catch(NumberFormatException e)
        {
            invalid = false;
        }

        return invalid;
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);

    public static boolean validate (String email)
    {
        Matcher m = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return m.find();
    }
}
