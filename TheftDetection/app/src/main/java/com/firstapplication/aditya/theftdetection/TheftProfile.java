package com.firstapplication.aditya.theftdetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheftProfile extends Activity {
    
    ArrayList<String> values = new ArrayList<String>();
    String username = "", user="";
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.theftprofile);

        Intent intent = getIntent();

        username = intent.getStringExtra("Username");
        user = intent.getStringExtra("User");


        TextView txtName = (TextView) findViewById(R.id.txtName);
        TextView txtEmail = (TextView) findViewById (R.id.txtEmail);
        TextView txtAddress = (TextView) findViewById(R.id.txtAddress);

        Button resolve = (Button) findViewById (R.id.resolve);

        ArrayList<String> val = setValues(user);

        String name = val.get(0).toString();
        String email =  val.get(1).toString();
        String address =  val.get(2).toString();

        txtName.setText(name);
        txtEmail.setText(email);
        txtAddress.setText(address);

        resolve.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Theft/" + user);

                    mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
                        @Override
                        public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                Firebase mRefTwo = new Firebase("https://theftdetection-973a2.firebaseio.com/");
                                Firebase tableName = mRefTwo.child("User");
                                Firebase username = tableName.child(user);
                                Firebase issueStatus = username.child("IssueStatus");
                                issueStatus.setValue("Resolved");
                                Toast.makeText(TheftProfile.this, "Issue status changed successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(TheftProfile.this, UnresolvedIssues.class);
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

    protected ArrayList<String> setValues(String user)
    {
        Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/User/" + user);
        mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                    List<Object> values = new ArrayList(td.values());
                    String name = values.get(3).toString();
                    String email = values.get(2).toString();
                    String address = values.get(0).toString();

                    values.add(name);
                    values.add(email);
                    values.add(address);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        return values;
    }
 
}