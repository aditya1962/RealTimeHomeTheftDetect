package com.firstapplication.aditya.theftdetection;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * Created by Aditya on 11/29/2016.
 */
public class HomeMenuInfo extends AppCompatActivity {

    String str = "",username = "", armMagnet = "", viewVideo = "", user="", text="", latitude = "", longitude = "",theftDetected = "", faceDetection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);
        setContentView(R.layout.homemenuinfo);

        Intent intent = getIntent();

        username = intent.getStringExtra("Username");
        user = intent.getStringExtra("User");
        theftDetected = intent.getStringExtra("TheftDetection");
        latitude = intent.getStringExtra("Latitude");
        longitude = intent.getStringExtra("Longitude");
        faceDetection = intent.getStringExtra("FaceDetection");
        

        final TextView txt = (TextView) findViewById(R.id.txtView);

        //Get device date and time
        TimeZone timeZone = TimeZone.getTimeZone("GMT+11");
        Calendar calendar = Calendar.getInstance(timeZone);

        Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Theft/" + user);

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

                    text = txt.getText().toString();
                    text = "";

                    //Get device date and time
                    TimeZone timeZone = TimeZone.getTimeZone("GMT+11");
                    Calendar calendar = Calendar.getInstance(timeZone);


                    if("Yes".equals(values.get(5).toString()))
                    {

                        //Arm magnet
                        Toast.makeText(HomeMenuInfo.this, "Arming magnet requested", Toast.LENGTH_LONG).show();
                        text+= "Arming magnet requested on " + calendar.get(Calendar.DATE) +
                                "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) +
                                " at" + calendar.get(Calendar.HOUR) +
                                ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "\n";
                        txt.setText(text);
                        txt.invalidate();
                    }

                    if("No".equals(values.get(5).toString()))
                    {

                        //Arm magnet
                        Toast.makeText(HomeMenuInfo.this, "Disarming magnet requested", Toast.LENGTH_LONG).show();
                        text+= "Disarming magnet requested on " + calendar.get(Calendar.DATE) +
                                "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) +
                                " at" + calendar.get(Calendar.HOUR) +
                                ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "\n";
                        txt.setText(text);
                        txt.invalidate();
                    }

                    if("Yes".equals(values.get(6).toString()))
                    {
                        //View Video
                        Toast.makeText(HomeMenuInfo.this, "Video viewing requested", Toast.LENGTH_LONG).show();
                        text+= "Video viewing requested on " + calendar.get(Calendar.DATE) +
                                "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) +
                                " at" + calendar.get(Calendar.HOUR) +
                                ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "\n";
                        txt.setText(text);
                        txt.invalidate();
                    }

                    

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        });

        Log.i("FaceDetected", faceDetection);

        if("Yes".equals(theftDetected))
        {

            Intent intentTwo = new Intent(HomeMenuInfo.this, FaceTrackerActivity.class);
            intentTwo.putExtra("Username", username);
            startActivity(intentTwo);
        }

        Firebase mRefTwo = new Firebase("https://theftdetection-973a2.firebaseio.com/");

                    if("Face detected".equals(faceDetection))
                    {
                        //Face detected
                        Toast.makeText(HomeMenuInfo.this, "Video viewing requested", Toast.LENGTH_LONG).show();
                        text+= "Face detected on " + calendar.get(Calendar.DATE) +
                                "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR) +
                                " at" + calendar.get(Calendar.HOUR) +
                                ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "\n";
                        txt.setText(text);
                        txt.invalidate();

                        //Add update to table

                        //adding data to theft table

                        Firebase tableOne = mRefTwo.child("Theft");
                        Firebase username = tableOne.child(user);
                        Firebase facedetected = username.child("Face Detection");
                        facedetected.setValue("Yes");
                        Firebase armMagnet = username.child("Arm Magnet");
                        armMagnet.setValue("No");
                        Firebase issueStatus = username.child("IssueStatus");
                        facedetected.setValue("Unattended");
                        Firebase lt = username.child("Latitude");
                        lt.setValue(latitude);
                        Firebase lng = username.child("Longitude");
                        lng.setValue(longitude);
                        Firebase videoID = username.child("Video ID");
                        videoID.setValue("");
                        Firebase viewVideo = username.child("View Video");
                        viewVideo.setValue("");
                            
                    }


    }
}
