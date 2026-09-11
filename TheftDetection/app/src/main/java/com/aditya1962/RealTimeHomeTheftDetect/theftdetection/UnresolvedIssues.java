package com.aditya1962.RealTimeHomeTheftDetect.theftdetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.Activity;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.firebase.client.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UnresolvedIssues extends Activity {
    ListView list;
    
    Integer[] imageId = { com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.drawable.unresolved };
    private Firebase mFirebase = new Firebase("https://theftdetection-973a2.firebaseio.com");
    private FirebaseListener mListener;

    String username = "", address = "", issueStatus = "";

    ArrayList<String> items = new ArrayList<String>();
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.layout.menu);

        Intent intent = getIntent();

        username = intent.getStringExtra("Username");

        mListener = new FirebaseListener() {
            @Override
            public void onUserValueListner(List<String> userList) {
                for(String username : userList)
                {
                    Log.i("User list item at " , username);
                    getAddress(username);
                }
            }

            @Override
            public void onAddressListener(String address) {
                Log.i("User address: " , address);
            }
        };

        getItems();

        //CustomList adapter = new CustomList(UnresolvedIssues.this, menuList, imageId);
        /*
        CList adapter = new CList(UnresolvedIssues.this, menuList, imageId);
        list=(ListView)findViewById(R.id.list);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
 
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                          //Get username for a selected position and send to new activity

                          String user = userList[position];

                          Intent intent = new Intent(UnresolvedIssues.this, TheftProfile.class);
                          intent.putExtra("Username", username);
                          intent.putExtra("User", user);
                          startActivity(intent);

                        }
                });
 */
    }

    protected void
    getItems()
    {


       //Get items from database to arraylist

       mFirebase.child("Theft").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                            for (com.firebase.client.DataSnapshot child : dataSnapshot.getChildren()) {
                                String username = child.getKey();

                                Log.i("Username", username);
                                //Check if issueStatus is unattended and if so add to arraylist
                                getStatus(username);
                                Log.i("Issue Status", issueStatus);
                                if ("Unattended".equals(issueStatus)) {
                                    items.add(username);
                                    Log.i("Item added", "Item added");
                                    mListener.onUserValueListner(items);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }


                    });
    }

    protected void getStatus(String username)
    {
        Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Theft/" + username);
        mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {

            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {

                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                    List<Object> values = new ArrayList(td.values());
                    issueStatus = values.get(2).toString();
                }
                Log.i("Status", issueStatus);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        });

    }


    protected String getAddress(String username)
    {


       //Get items from database to arraylist
       mFirebase.child(username).child("Address").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
               mListener.onAddressListener((String)dataSnapshot.getValue());
           }

           @Override
           public void onCancelled(FirebaseError firebaseError) {

           }
       });
        return address;
    }

    public interface FirebaseListener{
        void onUserValueListner(List<String> users);

        void onAddressListener(String address);
    }


    
 
}