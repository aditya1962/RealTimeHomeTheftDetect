package com.firstapplication.aditya.theftdetection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothData extends AppCompatActivity {

  private static final int REQUEST_ENABLE_BT = 1;

  BluetoothAdapter bluetoothAdapter;

  ArrayList<BluetoothDevice> pairedDeviceArrayList;

  ListView listViewPairedDevice;
  LinearLayout inputPane;
  Button btnOn, btnOff;

  ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
  private UUID myUUID;
  private final String UUID_STRING_WELL_KNOWN_SPP =
          "00001101-0000-1000-8000-00805F9B34FB";

  ThreadConnectBTdevice myThreadConnectBTdevice;
  ThreadConnected myThreadConnected;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    btnOn = (Button)findViewById(R.id.btnOn);
    btnOff = (Button)findViewById(R.id.btnOff);
    btnOn.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
        if(myThreadConnected!=null){
          String val = "1";
          byte[] bytesToSend = val.getBytes();
          myThreadConnected.write(bytesToSend);
          byte[] NewLine = "\n".getBytes();
          myThreadConnected.write(NewLine);
        }
      }});
    btnOff.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v) {
        if(myThreadConnected!=null){
          String val = "1";
          byte[] bytesToSend = val.getBytes();
          myThreadConnected.write(bytesToSend);
          byte[] NewLine = "\n".getBytes();
          myThreadConnected.write(NewLine);
        }
      }});





    //using the well-known SPP UUID
    myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);




  }

  @Override
  protected void onStart() {
    super.onStart();


    setup();
  }

  private void setup() {
    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
    if (pairedDevices.size() > 0) {
      pairedDeviceArrayList = new ArrayList<BluetoothDevice>();

      for (BluetoothDevice device : pairedDevices) {
        pairedDeviceArrayList.add(device);
      }

      pairedDeviceAdapter = new ArrayAdapter<BluetoothDevice>(this,
              android.R.layout.simple_list_item_1, pairedDeviceArrayList);
      listViewPairedDevice.setAdapter(pairedDeviceAdapter);

      listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
          BluetoothDevice device =
                  (BluetoothDevice) parent.getItemAtPosition(position);
          Toast.makeText(BluetoothData.this,
                  "Name: " + device.getName() + "\n"
                          + "Address: " + device.getAddress() + "\n"
                          + "BondState: " + device.getBondState() + "\n"
                          + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                          + "Class: " + device.getClass(),
                  Toast.LENGTH_LONG).show();


          myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
          myThreadConnectBTdevice.start();
        }
      });
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if(myThreadConnectBTdevice!=null){
      myThreadConnectBTdevice.cancel();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode==REQUEST_ENABLE_BT){
      if(resultCode == Activity.RESULT_OK){
        setup();
      }else{
        Toast.makeText(this,
                "BlueTooth NOT enabled",
                Toast.LENGTH_SHORT).show();
        finish();
      }
    }
  }

  //Called in ThreadConnectBTdevice once connect successed
  //to start ThreadConnected
  private void startThreadConnected(BluetoothSocket socket){

    myThreadConnected = new ThreadConnected(socket);
    myThreadConnected.start();
  }

  /*
  ThreadConnectBTdevice:
  Background Thread to handle BlueTooth connecting
  */
  private class ThreadConnectBTdevice extends Thread {

    private BluetoothSocket bluetoothSocket = null;
    private final BluetoothDevice bluetoothDevice;


    private ThreadConnectBTdevice(BluetoothDevice device) {
      bluetoothDevice = device;

      try {
        bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);

      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      boolean success = false;
      try {
        bluetoothSocket.connect();
        success = true;
      } catch (IOException e) {
        e.printStackTrace();

        final String eMessage = e.getMessage();
        runOnUiThread(new Runnable() {

          @Override
          public void run() {

          }
        });

        try {
          bluetoothSocket.close();
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }

      if(success){
        //connect successful
        final String msgconnected = "connect successful:\n"
                + "BluetoothSocket: " + bluetoothSocket + "\n"
                + "BluetoothDevice: " + bluetoothDevice;

        runOnUiThread(new Runnable() {

          @Override
          public void run() {

            Toast.makeText(BluetoothData.this, msgconnected, Toast.LENGTH_LONG).show();

            listViewPairedDevice.setVisibility(View.GONE);
            inputPane.setVisibility(View.VISIBLE);
          }
        });

        startThreadConnected(bluetoothSocket);

      }else{
        //fail
      }
    }

    public void cancel() {

      Toast.makeText(getApplicationContext(),
              "close bluetoothSocket",
              Toast.LENGTH_LONG).show();

      try {
        bluetoothSocket.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

  }

  /*
  ThreadConnected:
  Background Thread to handle Bluetooth data communication
  after connected
   */
  private class ThreadConnected extends Thread {
    private final BluetoothSocket connectedBluetoothSocket;
    private final InputStream connectedInputStream;
    private final OutputStream connectedOutputStream;

    public ThreadConnected(BluetoothSocket socket) {
      connectedBluetoothSocket = socket;
      InputStream in = null;
      OutputStream out = null;

      try {
        in = socket.getInputStream();
        out = socket.getOutputStream();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      connectedInputStream = in;
      connectedOutputStream = out;
    }

    @Override
    public void run() {
      byte[] buffer = new byte[1024];
      int bytes;

      String strRx = "";

      while (true) {
        try {
          bytes = connectedInputStream.read(buffer);
          final String strReceived = new String(buffer, 0, bytes);
          final String strByteCnt = String.valueOf(bytes) + " bytes received.\n";

          runOnUiThread(new Runnable(){

            @Override
            public void run() {

            }});

        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();

          final String msgConnectionLost = "Connection lost:\n"
                  + e.getMessage();
          runOnUiThread(new Runnable(){

            @Override
            public void run() {

            }});
        }
      }
    }

    public void write(byte[] buffer) {
      try {
        connectedOutputStream.write(buffer);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    public void cancel() {
      try {
        connectedBluetoothSocket.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}