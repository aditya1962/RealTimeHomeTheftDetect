package com.firstapplication.aditya.theftdetection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SYSTEM on 1/16/2017.
 */
public class UpdateNeighbor extends AppCompatActivity {

    String username = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        setContentView(R.layout.updateneighbor);

        Intent intent = getIntent();

        username = intent.getStringExtra("Username");

        final EditText etOne = (EditText) findViewById(R.id.tpOne);
        final EditText etTwo = (EditText) findViewById(R.id.tpTwo);
        final EditText etThree = (EditText) findViewById(R.id.tpThree);
        final EditText etFour = (EditText) findViewById(R.id.tpFour);
        Button btnUpdate = (Button) findViewById(R.id.btnUpdate);

        //On load get the values from database

        Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/User/" + username + "/Neighbor");
        mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                    List<Object> values = new ArrayList(td.values());

                    int length = values.size();

                    if (length == 1) {
                        etOne.setText(values.get(0).toString());
                    }
                    if (length == 2) {
                        etOne.setText(values.get(0).toString());
                        etTwo.setText(values.get(1).toString());
                    }
                    if (length == 3) {
                        etOne.setText(values.get(0).toString());
                        etTwo.setText(values.get(1).toString());
                        etThree.setText(values.get(2).toString());
                    }
                    if (length == 4) {
                        etOne.setText(values.get(0).toString());
                        etTwo.setText(values.get(1).toString());
                        etThree.setText(values.get(2).toString());
                        etFour.setText(values.get(3).toString());
                    }


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
                final String tpOne = etOne.getText().toString();
                final String tpTwo = etTwo.getText().toString();
                final String tpThree = etThree.getText().toString();
                final String tpFour = etFour.getText().toString();


                Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/User/" + username);

                mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
                    @Override
                    public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            Firebase mRefTwo = new Firebase("https://theftdetection-973a2.firebaseio.com/");
                            Firebase tableName = mRefTwo.child("User");
                            Firebase user = tableName.child(username);
                            Firebase neighbor = user.child("Neighbor");
                            Firebase nOne = neighbor.child("Neighbor 1");
                            nOne.setValue(tpOne);
                            Firebase nTwo = neighbor.child("Neighbor 2");
                            nTwo.setValue(tpTwo);
                            Firebase nThree = neighbor.child("Neighbor 3");
                            nThree.setValue(tpThree);
                            Firebase nFour = neighbor.child("Neighbor 4");
                            nFour.setValue(tpFour);
                            Toast.makeText(UpdateNeighbor.this, "Neighbors update successful", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(UpdateNeighbor.this, Login.class);
                            startActivity(intent);
                        }

                    }


                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }

                });

            }


        });
    }


}


