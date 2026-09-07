package com.firstapplication.aditya.bluetoothapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.firstapplication.aditya.bluetoothapplication.Bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity implements View.OnClickListener {

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (Bluetooth.connectedThread != null) {
			Bluetooth.connectedThread.write("Q");
		}//Stop streaming
		super.onBackPressed();
	}

	TextView txtView;

	Button bConnect, bDisconnect, bSend;

	EditText editSend;

	static BluetoothAdapter adapter;

	public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

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
					String incomingString = new String(readBuf, 0, 1);                 // create string from bytes array

					Log.i("incomingString", incomingString);

					txtView.setText(incomingString);

					if ("h".equals(txtView.getText().toString())) {
						Toast.makeText(MainActivity.this, "Theft Detected", Toast.LENGTH_LONG).show();
					}
					break;
			}
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//Hide title
		this.getWindow().setFlags(WindowManager.LayoutParams.
				FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//Hide Status bar
		setContentView(R.layout.activity_main);


		init();
		ButtonInit();

		bSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bluetooth.ConnectedThread connectedThread = new Bluetooth.ConnectedThread(getSocket());
				connectedThread.write("Arm");
			}
		});
	}

	void init() {
		Bluetooth.gethandler(mHandler);
	}

	void ButtonInit() {
		bConnect = (Button) findViewById(R.id.bConnect);
		bConnect.setOnClickListener(this);
		bDisconnect = (Button) findViewById(R.id.bDisconnect);
		bDisconnect.setOnClickListener(this);

		bSend = (Button) findViewById(R.id.btnSend);
		editSend = (EditText) findViewById(R.id.etSend);
		txtView = (TextView) findViewById(R.id.txtGet);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.bConnect:
				startActivity(new Intent("android.intent.action.BT1"));
				break;
			case R.id.bDisconnect:
				Bluetooth.disconnect();
				break;
		}
	}

	public BluetoothSocket getSocket() {
		BluetoothSocket mSocket = null;



		String deviceName = "THEFT-DETECTION";

		BluetoothDevice result = null;

		Set<BluetoothDevice> devices = adapter.getBondedDevices();
		if (devices != null) {
			for (BluetoothDevice device : devices) {
				if (deviceName.equals(device.getName())) {
					result = device;
					break;
				}
			}
		}

		if (result.getBondState() == result.BOND_BONDED) {

			//BluetoothSocket mSocket=null;
			try {
				mSocket = result.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e1) {
				// TODO Auto-generated catch block

				e1.printStackTrace();
			}
			try {
				mSocket.connect();
				Toast.makeText(MainActivity.this, "Connected to socket", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				try {
					mSocket.close();

				} catch (IOException e1) {

					e1.printStackTrace();
				}
			}
		}

		return mSocket;
	}
}
