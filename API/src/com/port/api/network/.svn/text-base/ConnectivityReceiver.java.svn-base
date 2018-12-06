package com.port.api.network;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.port.Channel;
import com.port.Consumer;
import com.port.MessageQueue;
import com.port.Port;
import com.port.api.R;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class ConnectivityReceiver extends BroadcastReceiver {
	  
	  private String TAG = "ConnectivityReceiver";
	  @Override
	  public void onReceive(Context context, Intent intent) {
		  ConnectivityManager connectivityManager= (ConnectivityManager) Port.c.getSystemService(Context.CONNECTIVITY_SERVICE);
		  NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
		  boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		  if(Constant.DEBUG)  Log.d(TAG , " Network connected "+isConnected);
		  try{
			  JSONObject sendResponse = new JSONObject();
			  JSONObject data = new JSONObject();
			  String dockID = Build.ID; //is this correct ?
			  Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
			  returner.set("DOCK", "BT", ""); 	
			  if(isConnected){
		    		if(Constant.DEBUG)  Log.d(TAG , " Recd message in Home, Network connected  ");
		    		Toast.makeText(Port.c, "Network is back on !", Toast.LENGTH_LONG).show();
					data.put("netvalue",100);
					sendResponse.put("params", data);
					returner.add("com.port.apps.epg.Home.networkStatus", sendResponse,"");					
					returner.send();
					
					Intent home = new Intent();
				    home.setAction("NETWORKSTATUS");
					home.putExtra("Connection","true");
					Port.c.sendBroadcast(home);
			  } else {
		    		if(Constant.DEBUG)  Log.d(TAG , " Recd message in Home, Network disconnected  ");
		    		Toast.makeText(Port.c, R.string.NETWORKERROR, Toast.LENGTH_LONG).show();
					data.put("netvalue",0);
					sendResponse.put("params", data);
					returner.add("com.port.apps.epg.Home.networkStatus", sendResponse,"");					
					returner.send();
					
					Intent home = new Intent();
				    home.setAction("NETWORKSTATUS");
					home.putExtra("Connection","false");
					Port.c.sendBroadcast(home);
			  }
		  }catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
		  }
	  }
	  
}
