package com.firstapplication.aditya.theftdetection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextUtils;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firstapplication.aditya.theftdetection.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aditya on 11/29/2016.
 */
public class Login extends AppCompatActivity {

    String str = "",username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        setContentView(R.layout.login);

        Button login = (Button)findViewById(R.id.login);
        Button register = (Button) findViewById(R.id.signup);
        Button forgotPassword = (Button) findViewById(R.id.forgotpassword);
        Button fbLogin = (Button)findViewById(R.id.fbLogin);



        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            EditText user = (EditText)findViewById(R.id.enterUsername);
            EditText password = (EditText)findViewById(R.id.enterPassword);

                username = user.getText().toString();

                boolean invalidOne = checkBlanks(username);
                boolean invalidTwo = checkBlanks(password.getText().toString());
                boolean invalidThree = checkString(username);
                boolean invalidFour = checkString(password.getText().toString());

                if(invalidOne == true)
                {
                    user.setError("Username cannot be blank");
                }
                if(invalidTwo == true)
                {
                    password.setError("Password cannot be blank");
                }
                if(invalidThree == true)
                {
                    user.setError("Username cannot be a number");
                }
                if(invalidFour == true)
                {
                    password.setError("Password cannot be a number");
                }

                if(invalidOne == false && invalidTwo == false && invalidThree == false && invalidFour == false)
                {
                    Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Login/" + username);
                    str = password.getText().toString();
                    mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
                        @Override
                        public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                                List<Object> values = new ArrayList(td.values());
                                String password = null;
                                try {
                                    password = new String(Base64.decode(values.get(0).toString(), Base64.DEFAULT),"UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                if(str.equals(password))
                                {
                                        Toast.makeText(Login.this, "User authenticated", Toast.LENGTH_LONG).show();
                                        if("HomeOwner".equals(values.get(1).toString()))
                                        {
                                            Intent intent = new Intent(Login.this, Menu.class);
                                            intent.putExtra("Username",username);
                                            startActivity(intent);
                                        }
                                        if("Police".equals(values.get(1).toString()))
                                        {
                                            Intent intent = new Intent(Login.this, PoliceMenu.class);
                                            intent.putExtra("Username",username);
                                            startActivity(intent);
                                        }
                                        if("SurveillancePhone".equals(values.get(1).toString()))
                                        {
                                            Intent intent = new Intent(Login.this, HomeMenu.class);
                                            intent.putExtra("Username",username);
                                            startActivity(intent);
                                        }
                                }
                                else
                                {
                                    Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(Login.this, "Username does not exist", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }


                    });
                 }
            }});

        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent register = new Intent(Login.this, com.firstapplication.aditya.theftdetection.Register.class);
                startActivity(register);

            }});
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent forgotPassword = new Intent(Login.this, com.firstapplication.aditya.theftdetection.ForgotPassword.class);
                startActivity(forgotPassword);

            }});
        //fbLogin.setOnClickListener();
        //twitterLogin.setOnClickListener();

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

}
