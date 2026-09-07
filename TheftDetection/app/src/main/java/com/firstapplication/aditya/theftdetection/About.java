package com.firstapplication.aditya.theftdetection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by SYSTEM on 1/16/2017.
 */
public class About extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        TextView txt = (TextView)findViewById(R.id.txtView);
        txt.setText("Abc");
    }
}
