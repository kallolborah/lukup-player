package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.port.Channel;
import com.port.Consumer;
import com.port.MessageQueue;
import com.port.Port;
import com.port.api.db.util.CacheData;
import com.port.api.network.Listener;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class Displays extends BroadcastReceiver{
	
	private String TAG = "Displays";
	private ArrayList<HashMap<String,String>> wifiList = new ArrayList<HashMap<String,String>>();
	Channel returner;
	String dockID;

	@Override
	public void onReceive(Context arg0, Intent intent) {
		try{
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = null;
			
			String action = intent.getAction();
			Bundle extras = intent.getExtras();
			if(Constant.DEBUG)  Log.d(TAG, "Wifi displays found");
			if (action.equalsIgnoreCase("LUKUP_WIFI_DISPLAYS")) {
				if (extras != null) {
					if(extras.containsKey("Devices")){
						wifiList = (ArrayList<HashMap<String, String>>) intent.getSerializableExtra("Devices");
						if(Constant.DEBUG)  Log.d(TAG  , "wifiList: "+wifiList.size());
						
						if (wifiList.size() > 0) {
							for(int i = 0;i < wifiList.size();i++) {
								jsonObject = new JSONObject();
								HashMap<String, String> deviceValue = wifiList.get(i);
								jsonObject.put("name", deviceValue.get("name"));
								jsonObject.put("id", deviceValue.get("address"));
								if(Constant.DEBUG)  Log.d(TAG,"Name: "+deviceValue.get("name")+", id: "+deviceValue.get("address"));
								jsonArray.put(jsonObject);
							}
							data.put("connectedList", jsonArray);
							data.put("result", "success");
							resp.put("params",data);
							
							returner = new Channel("Dock", ""); 
							returner.set(Listener.pname, Listener.pnetwork, CacheData.getWifiRemoteCaller()); //setting consumer = producer, network
							
							returner.add("com.port.apps.epg.Devices.getRemoteDisplays", resp,"messageActivity");
							returner.send();
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WIFIDISPLAY, errors.toString(), e.getMessage());
		}
	}
	
}
