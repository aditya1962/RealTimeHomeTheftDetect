package com.firstapplication.aditya.theftdetection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by SYSTEM on 1/16/2017.
 */
public class UserSettings extends Activity {

    String username="";

    ListView list;
    String[] menuList = {
            "Account",
            "Update Neighbors"
    } ;
    Integer[] imageId = {
            R.drawable.user,
            R.drawable.house
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.usersettings);

        Intent intent = getIntent();

        username = intent.getStringExtra("Username");

        CustomList adapter = new CustomList(UserSettings.this, menuList, imageId);

        list = (ListView) findViewById(R.id.list);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (menuList[+position] == "Account") {
                    Intent intentOne = new Intent(UserSettings.this,UpdateProfile.class);
                    intentOne.putExtra("Username", username);
                    startActivity(intentOne);
                }

                if (menuList[+position] == "Update Neighbors") {
                    Intent intentTwo = new Intent(UserSettings.this,UpdateNeighbor.class);
                    intentTwo.putExtra("Username", username);
                    startActivity(intentTwo);
                }


              }
           }
        );

    }
}
