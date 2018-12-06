package com.port.apps.settings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.port.Channel;
import com.port.api.network.Listener;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class WifiScanner extends BroadcastReceiver{
	
	Channel returner;
	private String TAG = "WifiScanner";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (Constant.DEBUG) Log.d(TAG, "WifiScanner onReceive");
		String method = "com.port.apps.settings.Settings.getWifiNetworks";
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		JSONObject jsonObject;
		JSONArray jsonArray = new JSONArray();
		
		if(returner==null){ 
	    	returner = new Channel("Dock", ""); //only to be used to send back responses from Dock to Requestor, eg, Player
    	}
		try {
			WifiManager wifi = (WifiManager)context.getSystemService(context.WIFI_SERVICE);
			ScanResult tmpScan = null;
			List<ScanResult> tmpliste = wifi.getScanResults();
			if (tmpliste.size() > 0) {
				if (Constant.DEBUG) Log.d(TAG, "Scan results available");
				for (int i = 0; i < tmpliste.size(); i++) {
					jsonObject = new JSONObject();
					tmpScan = tmpliste.get(i);
					String.valueOf(tmpScan.BSSID);
					String.valueOf(tmpScan.SSID);
					String.valueOf(tmpScan.capabilities);
					String.valueOf(tmpScan.frequency);
					String.valueOf(tmpScan.level);

					jsonObject.put("BSSID", String.valueOf(tmpScan.BSSID));
					jsonObject.put("SSID", String.valueOf(tmpScan.SSID));
					jsonObject.put("capabilities",String.valueOf(tmpScan.capabilities));
					jsonObject.put("frequency",String.valueOf(tmpScan.frequency));
					jsonObject.put("level", String.valueOf(tmpScan.level));
					jsonArray.put(jsonObject);
				}
				if (jsonArray.length() > 0) {
					if (Constant.DEBUG) Log.d(TAG, "wifiList: " + jsonArray.toString());
					data.put("wifiList", jsonArray);
					data.put("result", "success");
					sendResponse.put("params", data);
					returner.set(Listener.pname, Listener.pnetwork, "com.player.apps.Setup"); //setting consumer = producer, network
					returner.add(method, sendResponse, "messageActivity");
					returner.send();
				} else {
					data.put("result", "failure");
					sendResponse.put("params", data);
					returner.set(Listener.pname, Listener.pnetwork, "com.player.apps.Setup"); //setting consumer = producer, network
					returner.add(method, sendResponse, "messageActivity");
					returner.send();
				}
			}else{
				if (Constant.DEBUG) Log.d(TAG, "Scan results not available, failed !");
				data.put("result", "failure");
				sendResponse.put("params", data);
				returner.set(Listener.pname, Listener.pnetwork, "com.player.apps.Setup"); //setting consumer = producer, network
				returner.add(method, sendResponse, "messageActivity");
				returner.send();
			}
			
		
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,
					SystemLog.LOG_WIFI, errors.toString(),e.getMessage());
		}
	}	

}
