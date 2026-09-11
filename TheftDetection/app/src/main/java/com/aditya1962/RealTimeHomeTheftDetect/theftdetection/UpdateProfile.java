package com.aditya1962.RealTimeHomeTheftDetect.theftdetection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SYSTEM on 1/16/2017.
 */
public class UpdateProfile extends AppCompatActivity {
    
    String username = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        setContentView(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.layout.updateprofile);

        Intent intent = getIntent();

        username = intent.getStringExtra("Username");

        final EditText etOne = (EditText)findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.fullname);
        final  EditText etTwo = (EditText)findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.age);
        final  EditText etThree = (EditText)findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.address);
        final  EditText etFour = (EditText)findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.email);
        Button btnUpdate = (Button)findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.btnUpdate);
        
        //On load get the values from database

        Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/User/" + username);
        mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                    List<Object> values = new ArrayList(td.values());

                    for(int i = 0; i < values.size(); i++)
                    {
                        Log.i("Value", values.get(i).toString());
                    }

                    etOne.setText(values.get(1).toString());
                    etTwo.setText(values.get(3).toString());
                    etThree.setText(values.get(4).toString());
                    etFour.setText(values.get(2).toString());

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        });

        //On button click update the values

        btnUpdate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                     final String name = etOne.getText().toString();
                     final String age = etTwo.getText().toString();
                     final String address = etThree.getText().toString();
                     final String email = etFour.getText().toString();

                     boolean invalidOne = checkBlanks(name);
                     boolean invalidTwo = checkBlanks(address);
                     boolean invalidThree = checkBlanks(age);
                     boolean invalidFour = checkBlanks(email);

                     boolean invalidFive = checkString(name);
                     boolean invalidSix = checkString(age);
                     boolean invalidSeven = checkString(address);
                     boolean invalidEight = checkString(email);


                 if(invalidOne == true)
                 {
                     etOne.setError("Full name cannot be blank");
                 }
                 if(invalidTwo == true)
                 {
                     etTwo.setError("Address cannot be blank");
                 }
                 if(invalidThree == true)
                 {
                     etThree.setError("Age cannot be blank");
                 }
                 if(invalidFour == true)
                 {
                     etFour.setError("Email cannot be blank");
                 }

                 if(invalidFive == true)
                 {
                     etOne.setError("Full name cannot be a number");
                 }
                 if(invalidSix == false)
                 {
                     etTwo.setError("Age cannot be text");
                 }
                 if(invalidSeven == true)
                 {
                     etThree.setError("Address cannot be a number");
                 }
                 if(invalidEight == true)
                 {
                     etFour.setError("Email cannot be a number");
                 }

                 if(invalidOne == false && invalidTwo == false && invalidThree == false && invalidFour == false
                         && invalidFive == false && invalidSix == true && invalidSeven == false && invalidEight == false)
                 {
                     Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/User/" + username);

                     mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
                         @Override
                         public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                             if (dataSnapshot.hasChildren()) {
                                 Firebase mRefTwo = new Firebase("https://theftdetection-973a2.firebaseio.com/");
                                 Firebase tableName = mRefTwo.child("User");
                                 Firebase user = tableName.child(username);
                                 Firebase fullname = user.child("Full Name");
                                 fullname.setValue(name);
                                 Firebase add = user.child("Address");
                                 add.setValue(address);
                                 Firebase ag = user.child("Age");
                                 ag.setValue(age);
                                 Firebase em = user.child("Email");
                                 em.setValue(email);
                                 Toast.makeText(UpdateProfile.this, "User profile update successful", Toast.LENGTH_LONG).show();
                                 Intent intent = new Intent(UpdateProfile.this, Login.class);
                                 startActivity(intent);
                             }

                         }


                         @Override
                         public void onCancelled(FirebaseError firebaseError) {

                         }

                     });

                 }
              }
           }
        );
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


