package com.port.api.network;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.port.Port;
import com.port.api.network.bt.BluetoothConnectionService;
import com.port.api.network.wifidirect.WifiP2PConnectionService;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class Transmitter extends IntentService{

	private static String TAG = "Transmitter" ;
	private static String jsonMsg = "";
	private String network="";
	
	public Transmitter() {
		super("Transmitter");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
	    	if(extras.containsKey("JSON")){
	    		jsonMsg = extras.getString("JSON"); 
	    		network = extras.getString("network");
	    		sendMessage(jsonMsg, network);
	    	}
		}
	}	
	
	
	public void sendMessage(String message, String network) {
		if(Constant.DEBUG) Log.d(TAG, "sending message");
		
		if(network.equalsIgnoreCase("Wifi")){
			Intent intent = new Intent();					
			intent.setAction("send.data.wifi");
			intent.putExtra("Value", message);
			getApplicationContext().sendBroadcast(intent);
		}else if(network.equalsIgnoreCase("BT")){
			Intent intent = new Intent();					
			intent.setAction("send.data.bt");
			intent.putExtra("Value", message);
			getApplicationContext().sendBroadcast(intent);
		}
	}	

}
