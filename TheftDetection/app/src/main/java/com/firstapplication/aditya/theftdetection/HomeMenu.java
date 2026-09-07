package com.firstapplication.aditya.theftdetection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.TextUtils;
import java.util.Calendar;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firstapplication.aditya.theftdetection.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Created by Aditya on 11/29/2016.
 */
public class HomeMenu extends AppCompatActivity {

    String str = "",username = "", armMagnet = "", viewVideo = "",  text="", faceDetection="";
    static String user;
    double latitude, longitude;

    static BluetoothAdapter adapter;

    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (Bluetooth.connectedThread != null) {
            Bluetooth.connectedThread.write("Q");
        }
        super.onBackPressed();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {

                //If bluetooth connected successfully
                case Bluetooth.SUCCESS_CONNECT:
                    Bluetooth.connectedThread = new Bluetooth.ConnectedThread((BluetoothSocket) msg.obj);
                    Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_LONG).show();
                    String s = "successfully connected";
                    Bluetooth.connectedThread.start();
                    break;

                //Read the bluetooth message
                case Bluetooth.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String incomingString = new String(readBuf, 0, 1);
                    Log.i("incomingString", incomingString);

                    if ("h".equals(incomingString)) {
                        Toast.makeText(HomeMenu.this, "Intrusion Detected", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(HomeMenu.this, FaceTrackerActivity.class);
                        startActivity(intent);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        setContentView(R.layout.homemenu);

        Bluetooth.gethandler(mHandler);

        Intent intent = getIntent();
        user = intent.getStringExtra("Username");
        //Log.i("Username", user);
        faceDetection = intent.getStringExtra("FaceDetection");

        //Get device date and time
        TimeZone timeZone = TimeZone.getTimeZone("GMT+11");
        final Calendar calendar = Calendar.getInstance(timeZone);

        Button connect = (Button)findViewById(R.id.connect);
        Button menu = (Button)findViewById(R.id.menu);
        Button homemenu = (Button)findViewById(R.id.homemenu);

        //Button video = (Button)findViewById(R.id.video);
        final TextView txt = (TextView) findViewById(R.id.txtView);

        final View vOne = findViewById(R.id.linearLayoutThree);
        final View vTwo = findViewById(R.id.linearLayoutFour);
        final View vThree = findViewById(R.id.linearLayoutFive);
        final View vFour = findViewById(R.id.linearLayoutSix);
        final TextView arming = (TextView) findViewById(R.id.arming);

        vOne.setVisibility(View.GONE);
        vTwo.setVisibility(View.GONE);
        vThree.setVisibility(View.GONE);
        vFour.setVisibility(View.GONE);

        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent("android.intent.action.BT1"));
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeMenu.this, Menu.class);
                intent.putExtra("Username",user);
                startActivity(intent);
            }
        });
        homemenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        /*
        video.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeMenu.this, VideoCapture.class);
                intent.putExtra("Username", username);
                startActivity(intent);
            }
        });
        */


        Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Theft/" + user);

        mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                    List<Object> values = new ArrayList(td.values());

                    text = txt.getText().toString();
                    text = "";

                    if(values.size() > 6) {
                        if ("Yes".equals(values.get(5).toString())) {
                            vFour.setVisibility(View.VISIBLE);
                            //txt.setText(text);
                            //txt.invalidate();
                        }

                        if ("No".equals(values.get(5).toString())) {
                            //Disarm magnet
                            arming.setText("Disarming magnet requested");
                            vFour.setVisibility(View.VISIBLE);
                            //txt.setText(text);
                            //txt.invalidate();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        });

        //Face detection

        if("Face detected".equals(faceDetection))
        {
            //Face detected
            vTwo.setVisibility(View.VISIBLE);

            Firebase mRefTwo = new Firebase("https://theftdetection-973a2.firebaseio.com/");
            Firebase tableOne = mRefTwo.child("Theft");
            Firebase username = tableOne.child("abcdef");
            Firebase facedetected = username.child("Face Detection");
            facedetected.setValue("Yes");
            Firebase armMagnet = username.child("Arm Magnet");
            armMagnet.setValue("");
            Firebase issueStatus = username.child("IssueStatus");
            issueStatus.setValue("Unattended");
            Firebase lt = username.child("Latitude");
            lt.setValue(latitude);
            Firebase lng = username.child("Longitude");
            lng.setValue(longitude);
            Firebase videoID = username.child("Video ID");
            videoID.setValue("");
            Firebase viewVideo = username.child("View Video");
            viewVideo.setValue("");

            vThree.setVisibility(View.VISIBLE);


        }
    }
}
