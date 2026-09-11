package com.aditya1962.RealTimeHomeTheftDetect.theftdetection;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.os.Vibrator;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aditya on 11/29/2016.
 */

public class Menu extends Activity {
    ListView list;
    String[] menuList = { "Arm Magnet", "Notify Police", "Notify a Neighbor", "Update Status", "Settings", "Logout" };
    Integer[] imageId = { com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.drawable.magnet, com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.drawable.police, com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.drawable.house, com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.drawable.update, com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.drawable.settings, com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.drawable.logout, };

    String username = "", phoneNumber = "", message = "", name = "", password = "";
    boolean smsSent = true, valid = false;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.layout.menu);

        Context mContext = getApplicationContext();

        Intent intent = getIntent();

        username = intent.getStringExtra("Username");

        Button menu = (Button)findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.menu);
        Button homemenu = (Button)findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.homemenu);

        CustomList adapter = new CustomList(Menu.this, menuList, imageId);

        list = (ListView) findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.list);

        Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/");
        Firebase tableOne = mRef.child("Theft");
        final Firebase user = tableOne.child(username);

        menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        homemenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, HomeMenu.class);
                intent.putExtra("Username",username);
                startActivity(intent);
            }
        });

        list.setAdapter(adapter);

        getNotification(username);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                if (menuList[+position] == "Arm Magnet") {

                    //sending command to database

                    TextView txtView = (TextView) findViewById(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.id.txt);
                    if (txtView.getText() == "Arm Magnet") {
                        boolean valid = getVerification(username);

                        if(valid==true) {
                            Firebase arm = user.child("Arm Magnet");
                            arm.setValue("Yes");

                            Toast.makeText(Menu.this, "Magnet Armed", Toast.LENGTH_LONG).show();

                            txtView.setText("Disarm Magnet");
                        }
                    } else {
                        boolean valid = getVerification(username);

                        if(valid==true) {
                            Firebase arm = user.child("Arm Magnet");
                            arm.setValue("No");

                            Toast.makeText(Menu.this, "Magnet Disarmed", Toast.LENGTH_LONG).show();

                            txtView.setText("Arm Magnet");
                        }
                    }

                }


                if (menuList[+position] == "Notify Police") {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:119"));
                    startActivity(intent);
                }
                if (menuList[+position] == "Notify a Neighbor") {
                    //Get an array of available neighbors

                    Firebase mRefTwo = new Firebase("https://theftdetection-973a2.firebaseio.com/User/" + username + "/Neighbor");

                    mRefTwo.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                                List<Object> values = new ArrayList(td.values());

                                String name = getName(username);

                                message = "Theft detected at " + name + "'s place";

                                //Iterate through each neighbor

                                Toast.makeText(Menu.this, "Attempting to send sms", Toast.LENGTH_LONG).show();

                                for (int i = 0; i < values.size(); i++) {
                                    phoneNumber = values.get(i).toString();

                                    sendSMSMessage(phoneNumber, message);

                                    if (smsSent == true) {
                                        Toast.makeText(Menu.this, "SMS sent to " + phoneNumber, Toast.LENGTH_LONG).show();
                                    }

                                    if (smsSent == false) {
                                        Toast.makeText(Menu.this, "Error sending sms to " + phoneNumber, Toast.LENGTH_LONG).show();
                                    }

                                }

                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }


                    });
                }


                if (menuList[+position] == "Update Status") {
                    Firebase mRefThree = new Firebase("https://theftdetection-973a2.firebaseio.com/Theft/");
                    mRefThree.child(username).removeValue();
                    Toast.makeText(Menu.this, "Theft details updated" + phoneNumber, Toast.LENGTH_LONG).show();
                }

                if (menuList[+position] == "Settings") {
                    Intent intentOne = new Intent(Menu.this, UserSettings.class);
                    intentOne.putExtra("Username", username);
                    startActivity(intentOne);
                }


                if (menuList[+position] == "Logout") {
                    Intent intentOne = new Intent(Menu.this, Login.class);
                    startActivity(intentOne);
                }

                Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Theft/" + username);

                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                            List<Object> values = new ArrayList(td.values());

                            for (int i = 0; i < values.size(); i++) {
                                Log.i("Value", values.get(i).toString());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }

                });

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void getNotification(String user) {

        final String str = user;

        Firebase mRefFour = new Firebase("https://theftdetection-973a2.firebaseio.com/Theft/" + str);

        ValueEventListener valueEventListener = mRefFour.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    armedStatus(str);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public boolean getVerification(String username)
    {
        final String user = username;
        final String password = getPassword(username);

        //Create alert dialog to prompt user for password


        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(Menu.this);
        alertDialog.setTitle("Security Code");
        alertDialog.setMessage("Enter password to continue");

        final EditText input = new EditText(Menu.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.drawable.password);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = input.getText().toString();
                        if (pass.compareTo("") == 0) {
                            if (pass.equals(password)) {
                                Toast.makeText(getApplicationContext(), "Password Matched", Toast.LENGTH_SHORT).show();
                                valid = true;
                                dialog.cancel();
                            } else {
                                Toast.makeText(getApplicationContext(), "Wrong Password!", Toast.LENGTH_SHORT).show();
                                getVerification(user);
                            }
                        }
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Arming/ Disarming not performed", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        valid = false;
                    }
                });

        return valid;

    }

    public String getPassword(String username)
    {

        Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Login/" + username);

        mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                    List<Object> values = new ArrayList(td.values());
                    try {
                        password = new String(Base64.decode(values.get(0).toString(), Base64.DEFAULT),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        });
        return password;
    }

    public void armedStatus(String user)
    {
        Firebase mRef = new Firebase("https://theftdetection-973a2.firebaseio.com/Theft/" + user);

        mRef.addValueEventListener(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                    List<Object> values = new ArrayList(td.values());

                    //Check the status of armed value. If it is null, create notification
                    if("".equals(values.get(0).toString()))
                    {
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Menu.this)
                                .setSmallIcon(com.aditya1962.RealTimeHomeTheftDetect.theftdetection.R.drawable.logo)
                                .setContentTitle("Theft Detected")
                                .setContentText("Theft detected at your house")
                                .setAutoCancel(true);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
                        mBuilder.setContentIntent(pi);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(0, mBuilder.build());

                        //Get phone time

                        int hours = new Time(System.currentTimeMillis()).getHours();

                        //If time is between 7am-10pm, create vibration

                        if(hours > 7 && hours < 22) {
                            Vibrator v = (Vibrator) Menu.this.getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(5000);
                        }

                        //If time is between 10pm-7am, create ringing tone
                        else {
                            playTone(1000, 10);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public String getName(String username) {

        Firebase mRefThree = new Firebase("https://theftdetection-973a2.firebaseio.com/User/" + username);

        mRefThree.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                    List<Object> values = new ArrayList(td.values());
                    Log.i("User's name", values.get(3).toString());
                    name = values.get(3).toString();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        return name;
    }

    protected void sendSMSMessage(String phoneNumber, String message) {
        Log.i("Phone number", phoneNumber);
        Log.i("Message", message);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            smsSent = true;
        } catch (Exception e) {
            smsSent = false;
            e.printStackTrace();
        }


    }

    public void playTone(double freqOfTone, double duration) {

        int sampleRate = 8000;

        double dnumSamples = duration * sampleRate;
        dnumSamples = Math.ceil(dnumSamples);
        int numSamples = (int) dnumSamples;
        double sample[] = new double[numSamples];
        byte generatedSnd[] = new byte[2 * numSamples];


        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(freqOfTone * 2 * Math.PI * i / (sampleRate));
        }

        int idx = 0;
        int i = 0;

        int ramp = numSamples / 20;


        for (i = 0; i < ramp; ++i) {
            double dVal = sample[i];

            final short val = (short) ((dVal * 32767 * i / ramp));

            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }


        for (i = i; i < numSamples - ramp; ++i) {
            double dVal = sample[i];

            final short val = (short) ((dVal * 32767));

            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        for (i = i; i < numSamples; ++i) {
            double dVal = sample[i];

            final short val = (short) ((dVal * 32767 * (numSamples - i) / ramp));

            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        AudioTrack audioTrack = null;
        try {
            int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
            audioTrack.write(generatedSnd, 0, generatedSnd.length);
        } catch (Exception e) {
        }
        if (audioTrack != null) audioTrack.release();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Menu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.firstapplication.aditya.theftdetection/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Menu Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.firstapplication.aditya.theftdetection/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}