package com.firstapplication.aditya.theftdetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.Activity;

import com.firebase.client.Firebase;

public class PoliceMenu extends Activity {
    ListView list;
    String[] menuList = { "Unresolved Issues","View Map"  } ;
    Integer[] imageId = { R.drawable.unresolved, R.drawable.map };

    String username = "";
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.menu);

        Intent intent = getIntent();

        username = intent.getStringExtra("Username");

        CustomList adapter = new
                CustomList(PoliceMenu.this, menuList, imageId);
        list=(ListView)findViewById(R.id.list);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
 
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        if(menuList[+position] == "Unresolved Issues")
                        {
                            Intent intent = new Intent(PoliceMenu.this, com.firstapplication.aditya.theftdetection.UnresolvedIssues.class);
                            intent.putExtra("Username", username);
                            startActivity(intent);
                        }

                         if(menuList[+position] == "View Map")
                        {
                            /*
                            Intent intent = new Intent(PoliceMenu.this, com.firstapplication.aditya.theftdetection.BluetoothData.class);
                            intent.putExtra("Username",username);
                            startActivity(intent);
                            */
                        }

                        }
                });
    }
}