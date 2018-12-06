package com.port.apps.settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;

import com.parse.ParseInstallation;
import com.player.microtik.MicrotikConnection;
import com.port.Channel;
import com.port.Port;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.service.ProfileGateway;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.util.CacheData;
import com.port.api.db.util.CommonUtil;
import com.port.api.network.wifidirect.WiFiDirectBroadcastReceiver;
import com.port.api.network.wifidirect.WifiP2PConnectionService;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.api.webservices.Subscription;
import com.port.apps.settings.webservices.TVOperator;
import com.port.util.ApplicationConstant;

//consumer : player, network : bluetooth
public class Settings extends IntentService {

	String liveTvNetwork = "";
	String liveTvNetworkid = "";
	int networkid;
	static String deviceType;
	String type = null;
	private static String TAG = "Settings";
	private boolean flag=false;
	public static Context context;
	private SharedPreferences settingData;
	private SharedPreferences.Editor edit;
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String dockID;
	Channel returner;
	String caller;
	WifiManager tmpManager;
	BroadcastReceiver receiver;
	
	CacheGateway cache;
	CacheInfo info;
	
    public Settings() {
		super("Settings");
	}
    
    @Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String position = "";
		String ip = "";
		String mode = "";
		String subnet = "";
		String gateway = "";
		String dns = "";
		String distributorid = "";
		String distributorpwd ="";
		String tvOperator = "";
		String tvOperatorId = "";
		String ssid = "";
		String bssid ="";
		String password = "";
		String username = "";
		String pppoe_status = "";
		String type = "";
		String playerapp ="";
		String playerfirmware ="";
		String id = "";
		String wifiName = "";
		String btaddress = "";
		boolean enable = false;
		String PlayerID = "";
		
		settingData = getApplicationContext().getSharedPreferences("Port-Setup",MODE_WORLD_READABLE);
		edit = settingData.edit();
		
		IntentFilter setup = new IntentFilter("com.port.apps.settings.Settings.setpppoe");
		registerReceiver(mSetupReceiver,setup); 
		
		cache  = new CacheGateway(getApplicationContext());
		info = cache.getCacheInfo(1000);
		
	    if (extras != null) {
	    	if(extras.containsKey("ProducerNetwork")){
	    		pnetwork = extras.getString("ProducerNetwork"); //to be used to return back response
	    	}
	    	if(extras.containsKey("ConsumerNetwork")){
	    		cnetwork = extras.getString("ConsumerNetwork"); //to be used to send request onward 
	    	}
	    	if(extras.containsKey("Producer")){
	    		producer = extras.getString("Producer");
	    	}
	    	if(extras.containsKey("Caller")){
	    		caller = extras.getString("Caller");
	    	}
	    	if(extras.containsKey("macid")){
	    		dockID = extras.getString("macid");
	    	}
	    	
	    	if(Constant.DEBUG) Log.d(TAG,"onHandleIntent().pnetwork: "+pnetwork+", producer: "+producer);
	    	if(Constant.DEBUG) Log.d(TAG,"onHandleIntent().caller: "+caller+", cnetwork: "+cnetwork);
	    	if(returner==null){ //to ensure that there is only one returner instance for one activity
		    	returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
	    	}
	    	
	    	if(extras.containsKey("Params")){
	    		try { 
		    		functionData = extras.getString("Params");
		    		if(Constant.DEBUG) Log.d(TAG,"onHandleIntent().functionData: "+functionData);
		    		JSONObject jsonObj = new JSONObject(functionData);
		    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
		    		
		    		if(jsonObj.has("position")){
						position = jsonObj.getString("position");
		    		}if(jsonObj.has("mode")){
		    			mode = jsonObj.getString("mode");
		    		}if(jsonObj.has("manualip")){
						ip = jsonObj.getString("manualip");
		    		}if(jsonObj.has("subnet")){
		    			subnet = jsonObj.getString("subnet");
		    		}if(jsonObj.has("gateway")){
		    			gateway = jsonObj.getString("gateway");
		    		}if(jsonObj.has("dns")){
		    			dns = jsonObj.getString("dns");
		    		}if(jsonObj.has("distributorid")){
		    			distributorid = jsonObj.getString("distributorid");
		    		}if(jsonObj.has("liveTVOperator")){
		    			tvOperator = jsonObj.getString("liveTVOperator");
		    		}if(jsonObj.has("liveTVOperatorid")){
		    			tvOperatorId = jsonObj.getString("liveTVOperatorid");
		    		}if(jsonObj.has("password")){
		    			password = jsonObj.getString("password");Log.e("Password", password);
		    		}if(jsonObj.has("username")){
		    			username = jsonObj.getString("username");Log.e("username", username);
		    		}if(jsonObj.has("pppoestatus")){
		    			pppoe_status = jsonObj.getString("pppoestatus");Log.e("pppoe_status", pppoe_status);
		    		}if(jsonObj.has("ssid")){
		    			ssid = jsonObj.getString("ssid");
		    		}if(jsonObj.has("bssid")){
		    			bssid = jsonObj.getString("bssid");
		    		}if(jsonObj.has("networktype")){
						type = jsonObj.getString("networktype");
		    		}if(jsonObj.has("hotspotenable")){
		    			String val = jsonObj.getString("hotspotenable");
		    			if (val.equalsIgnoreCase("true")) {
							enable = true;
						}else{
							enable = false;
						}
		    		}if(jsonObj.has("playerapp")){
		    			playerapp = jsonObj.getString("playerapp");
		    		}if(jsonObj.has("playerfirmware")){
		    			playerfirmware = jsonObj.getString("playerfirmware");
		    		}if(jsonObj.has("id")){
						id = jsonObj.getString("id");
		    		}if(jsonObj.has("wifiName")){
		    			wifiName = jsonObj.getString("wifiName");
		    		}if(jsonObj.has("distributorpwd")){
		    			distributorpwd = jsonObj.getString("distributorpwd");
		    		}if(jsonObj.has("BTAddress")){
		    			btaddress = jsonObj.getString("BTAddress");
		    		}if(jsonObj.has("PlayerID")){
		    			PlayerID = jsonObj.getString("PlayerID");
		    		}
	    		} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}
	    	}		    	
	    	
	    	if(extras.containsKey("Method")){
	    		if(Constant.DEBUG) Log.d(TAG,"Method");
	    		try{
	    			func = extras.getString("Method");
	    			if(Constant.DEBUG) Log.d(TAG,"Method()."+func);
	    			if(func.equalsIgnoreCase("getNetworkType")){
	    				getNetworkType();
	    			}else if(func.equalsIgnoreCase("setpppoe")){
	    				setPppoe(username,password,pppoe_status);
	    			}else if(func.equalsIgnoreCase("VolumeControl")){
	    				VolumeControl(position);
	    			}else if(func.equalsIgnoreCase("setRJ45")){
	    				setRJ45(ip, subnet, gateway ,dns, mode);		
	    			}else if(func.equalsIgnoreCase("getTVOperators")){
	    				getTVOperators();
	    			}else if(func.equalsIgnoreCase("setTVOperator")){
	    				if (Constant.DVB) {	//DVB middleware
	    					if (Constant.DEBUG) Log.d(TAG, "Not in DVB Module");
						} else {
							setTVOperator(tvOperator, tvOperatorId);
						}
	    			}else if(func.equalsIgnoreCase("getAccountInfo")){
	    				getAccountInfo();
	    			}else if(func.equalsIgnoreCase("getDeviceInfo")){
	    				getDeviceInfo(wifiName,PlayerID);
	    			}else if(func.equalsIgnoreCase("getSubscriberID")){
	    				getSubscriberID(tvOperator, distributorid,distributorpwd,playerapp,playerfirmware);
	    			}else if(func.equalsIgnoreCase("getInitialize")){
	    				getInitialize();
	    			}else if(func.equalsIgnoreCase("getWifiNetworks")){
	    				getWifiNetworks();
	    			}else if(func.equalsIgnoreCase("connectToNetwork")){
	    				connectToNetwork(bssid, ssid, password, type);
	    			}else if(func.equalsIgnoreCase("createWifiHotspot")){
	    				if (Constant.DEBUG) Log.d(TAG, "onHandleIntent().enable: "+enable);
	    				createWifiHotspot(enable, password,username);
	    			}
//	    			else if(func.equalsIgnoreCase("powerOff")){
//	    				powerOff();
//	    			}
	    			else if(func.equalsIgnoreCase("disconnect")){
	    				disconnect();
	    			}else if(func.equalsIgnoreCase("doBTUnbind")){
	    				doBTUnbind(btaddress);
	    			}
//	    			else if(func.equalsIgnoreCase("stopWifi")){
//	    				stopWifi();
//	    			}
	    		}catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}		    		
	    	}	    	
	    	
	    }
	}
    
//    private void stopWifi(){
//    	if(Constant.DEBUG){Log.e(TAG ,"Going to reset Wifi connection");}	
//    	Port.mWifiP2PService.connectionLost();
//    }
    
    private void doBTUnbind(String BTaddress){
    	JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
    	BluetoothDevice device = Port.mBluetoothAdapter.getRemoteDevice(BTaddress);
    	boolean returnValue = false;
    	if(device != null){
    		try{
    			if (device.getBondState() == BluetoothDevice.BOND_BONDED){
    				Method removeBondMethod = device.getClass().getMethod("removeBond");
    				returnValue = (Boolean) removeBondMethod.invoke(device);
    			}
    			
    			if(returnValue){
    				data.put("result", "success");
					data.put("BTUnbind","true");
					sendResponse.put("params", data);
					returner.set(producer, pnetwork, caller); //setting consumer = producer, network
					returner.add("com.port.apps.settings.Settings.doBTUnbind", sendResponse, "messageActivity");
					returner.send();
					
    			}
    			else{
    				data.put("result", "failure");
					data.put("BTUnbind","false");
					sendResponse.put("params", data);
					returner.set(producer, pnetwork, caller); //setting consumer = producer, network
					returner.add("com.port.apps.settings.Settings.doBTUnbind", sendResponse, "messageActivity");
					returner.send();
    			}
    			
    		}catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}	
		}
    }
    
    
    //Added on 23 June 2015 for PPPoE  by  @Tomesh 
    private void setPppoe(String username, String password,String pppoe_status) {
    	if(Constant.DEBUG){Log.e(TAG ,"  SetPPPoE Called ");}	
    	String method = "com.port.apps.settings.Settings.setpppoe";
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
    	if(pppoe_status.equalsIgnoreCase("connect"))
		{
    		if(Constant.model.equalsIgnoreCase("X") || Constant.model.equalsIgnoreCase("S")){
    			MicrotikConnection api = new MicrotikConnection(ApplicationConstant.microtik_url, 8728);
    			if(!api.isConnected()){
    				api.start();
    				try{
    					api.join();
    					if(api.isConnected()){
    						if(api.login(username, password.toCharArray())){
    							data.put("result", "success");
    							data.put("pppoestatus","connected");
    							sendResponse.put("params", data);
    							returner.set(producer, pnetwork, caller); //setting consumer = producer, network
    							returner.add(method, sendResponse, "messageActivity");
    							returner.send();
    						}else{
    							data.put("result", "failure");
    							data.put("pppoestatus","WrongPassword");
    							sendResponse.put("params", data);
    							returner.set(producer, pnetwork, caller); //setting consumer = producer, network
    							returner.add(method, sendResponse, "messageActivity");
    							returner.send();
    						}
    					}
    				}catch(Exception e){
    					e.printStackTrace();
    				}
    			}
    		}else{
				try {
					Intent intent = new Intent("com.port.pppoe.LoginPPPoE.START_SERVICE");
					intent.putExtra("connect",pppoe_status);
					intent.putExtra("username", username);
					intent.putExtra("password", password);
					startService(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
		}else if(pppoe_status.equalsIgnoreCase("forget"))
		{
			if(Constant.model.equalsIgnoreCase("X") || Constant.model.equalsIgnoreCase("S")){
				MicrotikConnection api = new MicrotikConnection(ApplicationConstant.microtik_url, 8728);
				if(api.isConnected()){
					try{
						if(api.disconnect()){
							data.put("result", "success");
							data.put("pppoestatus","disconnected");
							sendResponse.put("params", data);
							returner.set(producer, pnetwork, caller); //setting consumer = producer, network
							returner.add(method, sendResponse, "messageActivity");
							returner.send();
						}else{
							data.put("result", "failure");
							data.put("pppoestatus","disconnected");
							sendResponse.put("params", data);
							returner.set(producer, pnetwork, caller); //setting consumer = producer, network
							returner.add(method, sendResponse, "messageActivity");
							returner.send();
						}
					}catch(Exception e){
						
					}
				}
    		}else{
				try {
					Intent intent = new Intent("com.port.pppoe.LoginPPPoE.START_SERVICE");
					intent.putExtra("connect",pppoe_status);
					startService(intent);
	
				} catch (Exception e) {
					e.printStackTrace();
				} 
    		}
		}
//		else
//		{
//			try {
//				data.put("result", "failure");
//				sendResponse.put("params", data);
//				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
//				returner.add(method, sendResponse, "messageActivity");
//				returner.send();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//		}
 		
	}

	@Override
    public void onDestroy(){
    	super.onDestroy();
    	if(receiver != null){
    		unregisterReceiver(receiver);
    	}
    	if(mSetupReceiver != null){
			unregisterReceiver(mSetupReceiver);
    	}
    }
    
    //switch off Port
//    private void powerOff(){
//		Port.mBluetoothService.stop();
//		Port.mWifiP2PService.stop();
//    }
 	
	// returns type of bb network port is connected to
	public void getNetworkType(){
		String method = "com.port.apps.settings.Settings.getNetworkType";
		boolean haveConnectedWifi = false;
		boolean haveConnectedEthernet = false;	
		
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo[] ni = cm.getAllNetworkInfo();
		if(Constant.DEBUG) Log.d(TAG,"Networks connected are  "+ ni);
		
		NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(wifiInfo.isConnected()){
			haveConnectedWifi = true;
			type = "wifi";
		}
		
		NetworkInfo ethernetInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if(ethernetInfo.isConnected()){
			haveConnectedEthernet = true;
			type = "ethernet";
		}
		
		if(Constant.DEBUG) Log.d(TAG,"Checking Wifi  "+haveConnectedWifi+", Ethernet "+haveConnectedEthernet);
		try{
			if(haveConnectedWifi || haveConnectedEthernet){
				JSONObject resp = new JSONObject();
				JSONObject data = new JSONObject();
				data.put("netvalue", 100+"");
				data.put("type", type);
				resp.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method,resp,"messageActivity");
				returner.send();
			}else{
				JSONObject resp = new JSONObject();
				JSONObject data = new JSONObject();
				data.put("netvalue", 0+"");
				data.put("networkError", getResources().getString(R.string.NETWORKERROR));
				resp.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method,resp,"messageActivity");
				returner.send();
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
		}
		
	}
	
	//for volume Control
	public void VolumeControl(String position) throws JSONException, InterruptedException {
		String method = "com.port.apps.settings.Settings.VolumeControl";
		if(Constant.DEBUG)  Log.d(TAG , "VolumeControl method- " + position);
		
		double currentVolume = Double.parseDouble(position);
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (Math.ceil((maxVolume*currentVolume) / 100)), 0);
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("result", "success");
		resp.put("params",data);
		returner.set(producer, pnetwork, caller); //setting consumer = producer, network
		returner.add(method,resp,"messageActivity");
		returner.send();
	}
	
	
	//for set Auto/Manual IP address
	public void setRJ45(String ip, String subnet, String gateway, String dns, String mode) throws JSONException, InterruptedException {
		String method = "com.port.apps.settings.Settings.setRJ45";
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		
		if(mode.equalsIgnoreCase("Auto")){			
			if(setDHCPNetwork()){
				data.put("result", "success");
				data.put("msg", getResources().getString(R.string.AUTO_NETWORK_SETUP_SUCCESS));
				data.put("mode", "Auto");
				resp.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method,resp,"messageActivity");				
				returner.send();
			}else{
				data.put("result", "failure");
				data.put("mode", "Auto");
				resp.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method,resp,"messageActivity");
				returner.send();
			}
		}else{
			if(ip != null && !ip.equalsIgnoreCase("") || gateway != null && !gateway.equalsIgnoreCase("") || subnet != null && !subnet.equalsIgnoreCase("") ){
				setManualNetwork(ip, gateway, subnet, dns);
			}else{
				data.put("msg", getResources().getString(R.string.MANUAL_NETWORK_SETUPINFO_FAILURE));
				data.put("result", "failure");
				data.put("mode", "Manual");
				resp.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method,resp,"messageActivity");
				returner.send();
			}
		}
	}
	
	//for getting Tv Operator list
	public void getTVOperators() throws JSONException, InterruptedException {
		String method = "com.port.apps.settings.Settings.getTVOperators";
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray jsonArray = TVOperator.getLiveTvOperatorsList(); //web service call to get list of operators
		if (jsonArray != null) {
			data.put("tvOperator", jsonArray);
			sendResponse.put("params", data);
			returner.set(producer, pnetwork, caller); //setting consumer = producer, network
			returner.add(method,sendResponse,"messageActivity");
			returner.send();
		}
		
	}	
	
	// for setting Tv Operator
	public void setTVOperator(String TVOperator, String TVOperatorid) throws JSONException, InterruptedException {
		String method = "com.port.apps.settings.Settings.setTVOperator";
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
		
		liveTvNetwork = TVOperator.replaceAll("\\s+",""); 
		liveTvNetworkid = TVOperatorid;
		if(CacheData.getSubscriberId() != null && !CacheData.getSubscriberId().equalsIgnoreCase("")){
			profileInfoGateway.updateProfileInfo(liveTvNetwork.toLowerCase());
		}
		
		if(liveTvNetwork != null && !liveTvNetwork.equalsIgnoreCase("")){
			CacheData.setOperaterName(liveTvNetwork);
		}
		
		data.put("result", "success");
		data.put("tvOperator", TVOperator);
		resp.put("params",data);
		returner.set(producer, pnetwork, caller); //setting consumer = producer, network
		returner.add(method,resp,"messageActivity");
		returner.send();
		
		//run live TV EPG updates
		try {
//			Intent mServiceIntent = new Intent(Settings.this, Catalogue.class);
			Intent mServiceIntent = new Intent(Intent.ACTION_MAIN);
			mServiceIntent.setComponent(new ComponentName("com.port.apps.epg","com.port.apps.epg.Catalogue"));
			mServiceIntent.putExtra("Title", "epg-updates");
			mServiceIntent.putExtra("network", pnetwork);
			getApplicationContext().startService(mServiceIntent);
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}		
		
	}	
	
	
	//Get Wifi network list
	public void getWifiNetworks() {
		String method = "com.port.apps.settings.Settings.getWifiNetworks";
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		if (Constant.DEBUG) Log.d("Settings", "getWifiNetworks().isWiFiActive: "+ isWiFiActive());
		
		try{
			WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(getApplicationContext().WIFI_SERVICE);
			
			receiver = new WifiScanner();
			IntentFilter scanner = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
			registerReceiver(receiver, scanner);
			
			if(!wifi.isWifiEnabled()){
				if (Constant.DEBUG) Log.d("Settings", "Wifi not enabled, going to enable it now");
				wifi.setWifiEnabled(true);
				data.put("result", "pending");
				sendResponse.put("params", data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method, sendResponse, "messageActivity");
				returner.send();
			}else{
				ScanResult tmpScan = null;
				List<ScanResult> tmpliste = wifi.getScanResults();
				if (tmpliste.size() > 0) {
					if (Constant.DEBUG) Log.d("Settings", "Scan results available");
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
						jsonObject.put("capabilities",
								String.valueOf(tmpScan.capabilities));
						jsonObject.put("frequency",
								String.valueOf(tmpScan.frequency));
						jsonObject.put("level", String.valueOf(tmpScan.level));
						jsonArray.put(jsonObject);
					}
					if (jsonArray.length() > 0) {
						if (Constant.DEBUG)
							Log.d(TAG, "wifiList: " + jsonArray.toString());
						data.put("wifiList", jsonArray);
						data.put("result", "success");
						sendResponse.put("params", data);
						returner.set(producer, pnetwork, caller); //setting consumer = producer, network
						returner.add(method, sendResponse, "messageActivity");
						returner.send();
					} else {
						data.put("result", "failure");
						sendResponse.put("params", data);
						returner.set(producer, pnetwork, caller); //setting consumer = producer, network
						returner.add(method, sendResponse, "messageActivity");
						returner.send();
					}
				}else{	
					if (Constant.DEBUG) Log.d("Settings", "Scan results not available, going to scan");
					wifi.startScan();
				}			
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,
					SystemLog.LOG_WIFI, errors.toString(),
					e.getMessage());
		}
	}


	//Connect to selected Wifi network
	public void connectToNetwork(String bssid, String ssid, String pass, String security){
		
		String method = "com.port.apps.settings.Settings.connectToNetwork";
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		if(Constant.DEBUG)  Log.d("Settings","is Wifi active ? : "+ isWiFiActive());
		try{
			if(isWiFiActive()){				
				boolean status = false;
				Context context = getApplicationContext();
				
				WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
								
				List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
				for( WifiConfiguration i : list ) {
					if(i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
						 wifiManager.disconnect();
						 if(Constant.DEBUG)  Log.d("Settings","Going to set network : "+ i.SSID);
						 wifiManager.enableNetwork(i.networkId, true);
						 wifiManager.reconnect(); 
						 status = true;
						 break;
					}           
				}
				
				if(!status){
					WifiConfiguration conf = new WifiConfiguration();
					if(Constant.DEBUG)  Log.d("Settings","Configuring wifi with : SSID "+ ssid + " BSSID " + bssid + " password " + pass + " Security " + security);
					conf.SSID = "\"" + ssid + "\"";
					conf.priority = 1;
					
					if(security.equalsIgnoreCase("WEP")){
						conf.wepKeys[0] = "\"" + pass + "\""; 
						conf.wepTxKeyIndex = 0;
					} else if (security.equalsIgnoreCase("WPA")){
						conf.preSharedKey = "\""+ pass +"\"";
					} else if (security.equalsIgnoreCase("OPEN")){
//						conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
					}
					conf.status = WifiConfiguration.Status.ENABLED;
					networkid = wifiManager.addNetwork(conf);
					if(Constant.DEBUG)  Log.d("Settings","Enabling network id : "+ networkid);
					if(networkid!=-1){
						wifiManager.enableNetwork(networkid, true);
						status = true;
					}
				}
				if(status){
					if(info!=null){
						cache.updateCacheInfo(1000, ssid, bssid, pass, security, settingData.getString("subscriberId", ""), settingData.getString("distributorId", ""), settingData.getString("userid", ""), info.getHotspotName(), info.getHotspotPwd());
					}else{
						cache.updateCacheInfo(1000, ssid, bssid, pass, security, settingData.getString("subscriberId", ""), settingData.getString("distributorId", ""), settingData.getString("userid", ""), "", "");
					}
					data.put("result", "success");
					data.put("tag", "connect");
					data.put("ssid", ssid);
					data.put("msg", getResources().getString(R.string.WIFI_NETWORK_SETUP_SUCCESS));
					sendResponse.put("params",data);
					returner.set(producer, pnetwork, caller); //setting consumer = producer, network
					returner.add(method, sendResponse,"messageActivity");
					returner.send();
				}else{
					if(info!=null){
						cache.updateCacheInfo(1000, "", "", "", "", settingData.getString("subscriberId", ""), settingData.getString("distributorId", ""), settingData.getString("userid", ""), info.getHotspotName(), info.getHotspotPwd());
					}else{
						cache.updateCacheInfo(1000, "", "", "", "", settingData.getString("subscriberId", ""), settingData.getString("distributorId", ""), settingData.getString("userid", ""), "", "");
					}
					data.put("result", "failure");
					data.put("tag", "connect");
					sendResponse.put("params",data);
					returner.set(producer, pnetwork, caller); //setting consumer = producer, network
					returner.add(method, sendResponse,"messageActivity");
					returner.send();
				}
			}else{
				data.put("result", "failure");
				data.put("tag", "connect");
				sendResponse.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method, sendResponse,"messageActivity");
				returner.send();
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WIFI, errors.toString(), e.getMessage());
		}

	}
	
	public void disconnect(){
		String method = "com.port.apps.settings.Settings.connectToNetwork";
		if(Constant.DEBUG)  Log.d("Settings","disconnect networkid ");
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		Context context = getApplicationContext();
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		
		try{
			if(wifiManager.disconnect()){
				if(info!=null){
					cache.updateCacheInfo(1000, "", "", "", "", settingData.getString("subscriberid", ""), settingData.getString("distributorId", ""), settingData.getString("userid", ""), info.getHotspotName(), info.getHotspotPwd());
				}else{
					cache.updateCacheInfo(1000, "", "", "", "", settingData.getString("subscriberid", ""), settingData.getString("distributorId", ""), settingData.getString("userid", ""), "", "");
				}
				data.put("result", "success");
				data.put("tag", "disconnect");
				sendResponse.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method, sendResponse,"messageActivity");
				returner.send();
			}else{
				data.put("result", "failure");
				data.put("tag", "disconnect");
				sendResponse.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method, sendResponse,"messageActivity");
				returner.send();
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WIFI, errors.toString(), e.getMessage());
		}
	}
	
	//Make the dock a wifi hotspot
	public void createWifiHotspot(boolean enable, String pwd , String name ){
		String method = "com.port.apps.settings.Settings.createWifiHotspot";
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		
		Context context = getApplicationContext();
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        if(Constant.DEBUG)  Log.d("Settings","if createWifiHotspot().enable: "+enable);
//        if(enable){
	        try {
	        	if(wifi.isWifiEnabled()){
	        		wifi.setWifiEnabled(false); // disable WiFi before creating hotspot
	        	}
	    		if(Constant.DEBUG)  Log.d(TAG,"Renaming hotspot with name : "+ name + " and pwd " + pwd);
	    		Method methods = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
				methods.invoke(wifi, null, false);

	    		methods = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
	    		WifiConfiguration conf = new WifiConfiguration();
	    		conf.SSID = name;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//                conf.preSharedKey = "\"" + pwd + "\"";    
//	            conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.preSharedKey = pwd;  
	    		methods.invoke(wifi, conf, true);
	    		
	    		edit.putString("hotspotName", name);
	    		edit.putString("hotspotPwd", pwd);
	    		edit.commit();
	    		
	    		if(info!=null){
	    			cache.updateCacheInfo(1000, info.getSsid(), info.getBssid(), info.getPwd(), info.getSecurity(), settingData.getString("subscriberid", ""), settingData.getString("distributorId", ""), settingData.getString("userid", ""), name, pwd);
	    		}else{
	    			cache.updateCacheInfo(1000, "", "", "", "", settingData.getString("subscriberid", ""), settingData.getString("distributorId", ""), settingData.getString("userid", ""), name, pwd);
	    		}
	    		
	    		data.put("result", "enabled");
	            data.put("msg", getResources().getString(R.string.HOTSPOT_ENABLE));
				sendResponse.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method, sendResponse,"messageActivity");
				returner.send();
	    		} catch (Exception ex) { 
	    			try {
						data.put("result", "failure");
						sendResponse.put("params",data);
						returner.set(producer, pnetwork, caller); //setting consumer = producer, network
						returner.add(method, sendResponse,"messageActivity");
						returner.send();
					} catch(Exception e){
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
					}			
	    		}    
//        } else {        	
//        	Method methods;
//        	
//			try {
//				methods = wifi.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
//				methods.invoke(wifi, null, false);
//				
//				wifi.setWifiEnabled(true);
//	        	
//	        	edit.putString("hotspotName", "");
//	    		edit.putString("hotspotPwd", "");
//	    		edit.commit();
//
//				data.put("result", "disabled");
//	            sendResponse.put("params",data);
//	            returner.set(producer, pnetwork, caller); //setting consumer = producer, network
//				returner.add(method, sendResponse,"messageActivity");
//				returner.send();
//	        	
//			} catch (Exception e1) {
//				e1.printStackTrace();
//				try {
//					data.put("result", "failure");
//					sendResponse.put("params",data);
//					returner.set(producer, pnetwork, caller); //setting consumer = producer, network
//					returner.add(method, sendResponse,"messageActivity");
//					returner.send();
//				} catch(Exception e){
//					e.printStackTrace();
//					StringWriter errors = new StringWriter();
//					e.printStackTrace(new PrintWriter(errors));
//					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
//				}		
//			} 
//        	
//        }
        
	}
	
	
	// for getting Account Info
	public void getAccountInfo() throws JSONException {
		String method = "com.port.apps.settings.Settings.getAccountInfo";
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		try{
			data.put("device", Build.DEVICE);
			String subscriberId = CacheData.getSubscriberId();
			if(Constant.DEBUG) Log.d(TAG,"subscriberId : "+subscriberId);
			if(Constant.DEBUG) Log.d(TAG,"Model : "+Build.MODEL);
			
			if(!subscriberId.equalsIgnoreCase("") && subscriberId != null){
				JSONObject jsonObject = Subscription.Accountinfo(subscriberId);
				if(jsonObject != null){
					String balance = jsonObject.getJSONObject("data").getString("balance");
					data.put("Subscriber ID", subscriberId);
					data.put("balance", balance);
					resp.put("params",data);
					returner.set(producer, pnetwork, caller); //setting consumer = producer, network
					returner.add(method,resp,"messageActivity");
					returner.send();
				}
			}else{
				data.put("msg", getResources().getString(R.string.UNKNOWN_ERROR));
				resp.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method,resp,"messageActivity");
				returner.send();
			}
		}catch (Exception e) {
        	e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
        }
	}
	
	// for getting Device Info
	public void getDeviceInfo(String wifiName , String PlayerID) throws JSONException, InterruptedException {
		String method = "com.port.apps.settings.Settings.getDeviceInfo";
		
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
        discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
        getApplicationContext().startActivity(discoverableIntent);
        
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("Model", Build.MODEL);	// Lukup Player X or S
		data.put("Device", Build.DEVICE);	
		data.put("Firmware Version", Build.VERSION.RELEASE);	
		data.put("Description", Build.PRODUCT);
		if(Constant.DEBUG) Log.d(TAG,"Model : "+Build.MODEL+", Device : "+Build.DEVICE);
		String subscriberId = "";
		String operatorName = "";
		String ssid = "";
		String distributorId = "";
		
		int userId = 0;
		if(userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null && info.getProfile()!=null && !("".equalsIgnoreCase(info.getProfile()))) {
				userId = Integer.valueOf(info.getProfile());
			}else{
				userId = 1000;
				cache.insertCacheInfo(1000, "", "", "", "", settingData.getString("subscriberid", ""), settingData.getString("distributorId", ""), Integer.toString(userId), "", "");
			}
		}
		CacheData.setUserId(userId);
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
		CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));
		
		if (info != null) {
			subscriberId = info.getSubscriber();
			distributorId = info.getDistributor();
		}
		if(subscriberId.equalsIgnoreCase("")){
			subscriberId = settingData.getString("subscriberId", "");
		}
		if(distributorId.equalsIgnoreCase("")){
			distributorId = settingData.getString("distributorId", "");
		}
		if(!distributorId.equalsIgnoreCase("")){
			CacheData.setDistributorId(distributorId);
		}
		if(!subscriberId.equalsIgnoreCase("")){
			CacheData.setSubscriberId(subscriberId);
		}
		if(Constant.DEBUG) Log.d(TAG,"distributorId : "+ distributorId);
		
		if(subscriberId !=null && !subscriberId.equalsIgnoreCase("")){
			data.put("subscriberid", subscriberId);
			CacheData.setSubscriberId(subscriberId);
		}else if(CacheData.getSubscriberId()!=null){
			subscriberId = CacheData.getSubscriberId();
			data.put("subscriberid", subscriberId);
		}else{
			data.put("subscriberid", "");
		}
		if(Constant.DEBUG) Log.d(TAG,"subscriberId : "+subscriberId);
		
		
		if(operatorName !=null && !operatorName.equalsIgnoreCase("")){
			data.put("operatorname", operatorName);
			CacheData.setOperaterName(operatorName);
		} else if (CacheData.getOperaterName()!=null){
			operatorName = CacheData.getOperaterName();
			data.put("operatorname", operatorName);
		}else {
			data.put("operatorname", "");
		}
		
		String	MAC = getDockMacAddress("eth0");
		if(Constant.DEBUG) Log.d(TAG,"ETH0 MAC ADDRESS "+MAC);
		if (MAC != null && !MAC.equalsIgnoreCase("")) {
			data.put("MAC Address", MAC);
		} else {
			data.put("MAC Address", "");
		}
		
		String isHomeVisible = com.port.api.db.util.CacheData.getHomeVisibility();
		if(isHomeVisible != null && isHomeVisible.equalsIgnoreCase("visible")){
			data.put("videostatus", "false");
		}else if(isHomeVisible != null && isHomeVisible.equalsIgnoreCase("invisible")){
			data.put("videostatus", "true");
		}
		
		String ip = getLocalIpAddress();
		if(Constant.DEBUG) Log.d(TAG,"IP Address : "+ ip);
		if(ip!=null){
			data.put("IP", ip);
		}
		
		com.port.api.db.util.CacheData.setExternalIp(CommonUtil.getExternalIp());
		
		if(Port.BTAddress!="" && Port.BTAddress!=null){
			data.put("PortMac", Port.BTAddress);
		}else{
			if(BluetoothAdapter.getDefaultAdapter()!=null)
				data.put("PortMac", BluetoothAdapter.getDefaultAdapter().getAddress());
			else
				data.put("PortMac", "");
		}
		
		String versionName="";
		try {
			versionName = getApplicationContext().getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(versionName!=null){
			data.put("Port Version", versionName);
		}
		
		
		if (info != null) {
			ssid = info.getSsid();
		}
		if(Constant.DEBUG) Log.d(TAG,"Any Connected WiFi: "+ ssid);
		if (ssid != null && !ssid.equalsIgnoreCase("")) {
			String bssid = info.getBssid();
			String password = info.getPwd();
			String security = info.getSecurity();
			
			connectToNetwork(bssid, ssid, password, security);
		}
		
		resp.put("params",data);	
		
		if(Constant.DEBUG) Log.d(TAG,"caller : "+caller);
		if (caller.equalsIgnoreCase("com.player.UpdateService")) {
			returner.set(producer, pnetwork, caller); //setting consumer = producer, network
			returner.add(method,resp,"startService");
			returner.send();
		} else {
			returner.set(producer, pnetwork, caller); //setting consumer = producer, network
			returner.add(method,resp,"messageActivity");
			returner.send();
		}
	}
	
	
	public String getLocalIpAddress() {
		try {
			if(CommonUtil.isNetworkAvailable()){
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if(Constant.DEBUG) Log.d(TAG, "Check all address : "+inetAddress.getHostAddress()+", host name : "+inetAddress.getHostName()+", caconical host name : "+inetAddress.getCanonicalHostName()+", address : "+inetAddress.getAddress()+" , Network Interface Name:"+intf.getName());
						//if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
						if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())&& (intf.getName().equalsIgnoreCase("eth0")||intf.getName().equalsIgnoreCase("ppp0"))) {
							if(Constant.DEBUG) Log.d(TAG, "IPV4 host address : "+inetAddress.getHostAddress()+", host name : "+inetAddress.getHostName()+", caconical host name : "+inetAddress.getCanonicalHostName()+", address : "+inetAddress.getAddress()+" , Network Interface Name:"+intf.getName());
							return inetAddress.getHostAddress().toString();
						}
					}
				}
			}else{
				if(Constant.DEBUG) Log.d(TAG, "Network not available, so not host IP returned ");
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
		}
		return "";
	}
	
	
	// getting Subscribe Id after full setup
	public void getSubscriberID(String liveTvOperator, String distributorId,String distributorPwd,String papp,String pfirmware) throws JSONException, InterruptedException {
		String method = "com.port.apps.settings.Settings.getSubscriberID";
		if(Constant.DEBUG) Log.d(TAG,"liveTvOperator : "+liveTvOperator+ ", distributorId : "+distributorId);
		if(Constant.DEBUG) Log.d(TAG,"playerfirmware : "+pfirmware+ ", playerapp : "+papp);
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		
		CacheData.setDistributorId(distributorId);
		edit.putString("distributorId", distributorId);
		edit.putString("distributorPwd", distributorPwd);
		edit.commit();
		if(Constant.DEBUG){Log.d(TAG, "Distributor ID " + settingData.getString("distributorId", "null"));
						   Log.d(TAG, "Distributor Pwd " + settingData.getString("distributorPwd", "null"));}
		
		String subscriberId="";
		if(info!=null){
			subscriberId = info.getSubscriber();
		}
		if(subscriberId.equalsIgnoreCase("")){
			subscriberId = settingData.getString("subscriberId", "");
		}
		
		if(subscriberId != null && !subscriberId.equalsIgnoreCase("")){ //if subscriber id is not null
			//Added to store SubcriberId to share Different application	
			if(settingData.getString("subscriberId", "").equalsIgnoreCase("")){
				edit.putString("subscriberId", subscriberId);
				edit.commit();
				if(Constant.DEBUG) Log.d(TAG, settingData.getString("subscriberid", ""));
			}
			CacheData.setSubscriberId(subscriberId);
			if (info != null) {
				cache.updateCacheInfo(1000, info.getSsid(), info.getBssid(), info.getPwd(), info.getSecurity(), subscriberId, distributorId, info.getProfile(), info.getHotspotName(), info.getHotspotPwd());	
			}else{
				cache.insertCacheInfo(1000, "", "", "", "", subscriberId, distributorId, "", "", "");
			}
			if(Constant.DEBUG)Log.d(TAG,"Subscriber ID is already present "+ subscriberId);
			if(Constant.DEBUG)  Log.d(TAG,"Downloading catalog from Settings");
//			Intent mServiceIntent = new Intent("com.port.api.epg.service.Catalogue.Start");
			Intent mServiceIntent = new Intent("com.port.apps.epg.Catalogue.Start");
			mServiceIntent.putExtra("Title", "featured");
			mServiceIntent.putExtra("distributorId",distributorId );
			mServiceIntent.putExtra("distributorPwd",distributorPwd );
			mServiceIntent.putExtra("subscriberid",subscriberId);
			mServiceIntent.putExtra("macid", dockID);
			mServiceIntent.putExtra("network", pnetwork);
			getApplicationContext().startService(mServiceIntent);
			
//			Intent mServiceIntentvod = new Intent("com.port.api.epg.service.Catalogue.Start");
			Intent mServiceIntentvod = new Intent("com.port.apps.epg.Catalogue.Start");
			mServiceIntentvod.putExtra("Title", "vod-updates");
			mServiceIntentvod.putExtra("macid", dockID);
			mServiceIntentvod.putExtra("network", pnetwork);
			getApplicationContext().startService(mServiceIntentvod);
			
			data.put("result", "registered");
			data.put("distributorId", distributorId);
			data.put("distributorPwd", distributorPwd);
			data.put("subscriberid", subscriberId);
			data.put("Model", Build.MODEL);
			resp.put("params",data);
			returner.set(producer, pnetwork, caller); //setting consumer = producer, network
			returner.add(method,resp,"messageActivity");
			returner.send();
			return;
		}else{ //if subscriber ID is null, then generate it
			String mANSIDateVal = "";
			String MAC = getDockMacAddress("eth0");
			String ProductID = MAC;
			if(Constant.DEBUG) Log.d(TAG,"ETH0 MAC ADDRESS "+MAC);
			if(!MAC.equalsIgnoreCase("") && MAC != null){
				if(MAC.indexOf(":") != -1){
					MAC = MAC.replaceAll(":", "");
					int length = MAC.length();
					MAC = MAC.substring(length-6, length);
				}
			}else{
				data.put("result", "failure");
				data.put("msg", "MAC address Not found !");
				resp.put("params",data);
				returner.set(producer, pnetwork, caller); //setting consumer = producer, network
				returner.add(method,resp,"messageActivity");
				returner.send();
				return;
			}
									
			mANSIDateVal = CommonUtil.getANSIDATEValue(CommonUtil.getCurrentDateAndTime());
			if(Constant.DEBUG) Log.d(TAG ,"Final  ANSI DATE "+mANSIDateVal);
			subscriberId = MAC+mANSIDateVal;
			if(Constant.DEBUG) Log.d(TAG ,"Final  subscriberId "+subscriberId);
			
			try{
			//	JSONObject jsonObject = Subscription.registerSubscriberId(subscriberId,distributorId,distributorPwd,android.os.Build.ID,Build.MODEL.replaceAll("\\s+",""), papp, pfirmware, Constant.APP_VERSION,java.lang.System.getProperty("ro.build.version.firmware"));
			//Added MAC-ID(ProductID in place of BUILD-ID to get Product the product details for the subscriber.
				if(Constant.DEBUG)Log.d(TAG,"ProductID =========> "+ ProductID);
				JSONObject jsonObject = Subscription.registerSubscriberId(subscriberId,distributorId,distributorPwd,ProductID,Build.MODEL.replaceAll("\\s+",""), papp, pfirmware, Constant.APP_VERSION,java.lang.System.getProperty("ro.build.version.firmware"));
				if(jsonObject != null){
					JSONObject jsonObjectForSubscriberId = jsonObject.getJSONObject("json");
					if(jsonObjectForSubscriberId != null){
						String result = jsonObjectForSubscriberId.getString("result");
						if(result != null && result.trim().equalsIgnoreCase("success")){ //registration with SMS is ok
							
							ProfileGateway profileGateway = new ProfileGateway(getApplicationContext());
							setandGetProfileInformation(subscriberId,liveTvOperator); //store profile info in DB

							if(!subscriberId.equalsIgnoreCase("") && subscriberId != null){
								data.put("subscriberid", subscriberId );	
								edit.putString("subscriberId", subscriberId);
								edit.commit();
							}
							CacheData.setSubscriberId(subscriberId);
							if (info != null) {
								cache.updateCacheInfo(1000, info.getSsid(), info.getBssid(), info.getPwd(), info.getSecurity(), subscriberId, distributorId, info.getProfile(), info.getHotspotName(), info.getHotspotPwd());	
							}else{
								cache.insertCacheInfo(1000, "", "", "", "", subscriberId, distributorId, "", "", "");
							}
							if(Constant.DEBUG)Log.d(TAG,"Generated Subscriber ID "+ subscriberId);
//							Intent mServiceIntent = new Intent("com.port.api.epg.service.Catalogue.Start");
							Intent mServiceIntent = new Intent("com.port.apps.epg.Catalogue.Start");
							mServiceIntent.putExtra("Title", "featured");
							mServiceIntent.putExtra("distributorId",distributorId );
							mServiceIntent.putExtra("distributorPwd",distributorPwd );
							mServiceIntent.putExtra("subscriberid",subscriberId);
							mServiceIntent.putExtra("macid", dockID);
							mServiceIntent.putExtra("network", pnetwork);
							getApplicationContext().startService(mServiceIntent);
							
//							Intent mServiceIntentvod = new Intent("com.port.api.epg.service.Catalogue.Start");
							Intent mServiceIntentvod = new Intent("com.port.apps.epg.Catalogue.Start");
							mServiceIntentvod.putExtra("Title", "vod-updates");
							mServiceIntentvod.putExtra("macid", dockID);
							mServiceIntentvod.putExtra("network", pnetwork);
							getApplicationContext().startService(mServiceIntentvod);
							
							data.put("result", "created");
							data.put("subscriberid", subscriberId);
							data.put("distributorId", distributorId);
							data.put("distributorPwd", distributorPwd);
							data.put("Model", Build.MODEL);
							resp.put("params",data);
							returner.set(producer, pnetwork, caller); //setting consumer = producer, network
							returner.add(method,resp,"messageActivity");
							returner.send();
							return;
							
						}else{ //registration with SMS fails
							
							ProfileGateway profileGateway = new ProfileGateway(getApplicationContext());
							subscriberId = CommonUtil.getDataFromJSON(jsonObjectForSubscriberId, "subscriberid");
							
							if(subscriberId!=null && !subscriberId.equalsIgnoreCase("")){
								setandGetProfileInformation(subscriberId,liveTvOperator); //store profile info in DB 
								
								//Added to store SubcriberId to share Different application	
								if(settingData.getString("subscriberId", "").equalsIgnoreCase("")){
									edit.putString("subscriberId", subscriberId);
									edit.commit();
									if(Constant.DEBUG) Log.d("getSubsciberID() :subscriberid", settingData.getString("subscriberid", ""));
								} 
								CacheData.setSubscriberId(subscriberId);
								if (info != null) {
									cache.updateCacheInfo(1000, info.getSsid(), info.getBssid(), info.getPwd(), info.getSecurity(), subscriberId, distributorId, info.getProfile(), info.getHotspotName(), info.getHotspotPwd());	
								}else{
									cache.insertCacheInfo(1000, "", "", "", "", subscriberId, distributorId, "", "", "");
								}
								if(Constant.DEBUG)Log.d(TAG,"Subscriber ID is already present "+ subscriberId);
								if(Constant.DEBUG)  Log.d(TAG,"Downloading featured from Settings()");
//								Intent mServiceIntent = new Intent("com.port.api.epg.service.Catalogue.Start");
								Intent mServiceIntent = new Intent("com.port.apps.epg.Catalogue.Start");
								mServiceIntent.putExtra("Title", "featured");
								mServiceIntent.putExtra("distributorId",distributorId );
								mServiceIntent.putExtra("distributorPwd",distributorPwd );
								mServiceIntent.putExtra("subscriberid",subscriberId);
								mServiceIntent.putExtra("macid", dockID);
								mServiceIntent.putExtra("network", pnetwork);
								getApplicationContext().startService(mServiceIntent);
								
//								Intent mServiceIntentvod = new Intent("com.port.api.epg.service.Catalogue.Start");
								Intent mServiceIntentvod = new Intent("com.port.apps.epg.Catalogue.Start");
								mServiceIntentvod.putExtra("Title", "vod-updates");
								mServiceIntentvod.putExtra("macid", dockID);
								mServiceIntentvod.putExtra("network", pnetwork);
								getApplicationContext().startService(mServiceIntentvod);
								
								data.put("result", "registered");
								data.put("subscriberid", subscriberId);
								data.put("distributorId", distributorId);
								data.put("distributorPwd", distributorPwd);
								data.put("Model", Build.MODEL);
								resp.put("params",data);
								returner.set(producer, pnetwork, caller); //setting consumer = producer, network
								returner.add(method,resp,"messageActivity");
								returner.send();
								return;
							}else{
								String msg = CommonUtil.getDataFromJSON(jsonObjectForSubscriberId, "msg");
								if(msg != null){
									String message = jsonObjectForSubscriberId.getString("msg");
									data.put("result", "failure");
									data.put("msg", message);
								}else{
									data.put("result", "failure");
									data.put("msg", getResources().getString(R.string.SubscriberId_is_Not_Generated));
								}
							}
						}
					}
					
				} else {
					data.put("result", "failure");
					data.put("msg", getResources().getString(R.string.SubscriberId_is_Not_Generated));
				}
							
			}catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
			}
			
		}
		
		if(Constant.DEBUG) Log.d(TAG,"Set Notwork Done");
	}
	
	//Get logged in profile
	private void getInitialize(){
		
		int userId=CacheData.getUserId();
		if(Constant.DEBUG)  Log.d(TAG, "User id from CacheData " + userId);

		if(userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}
		if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
		
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
		CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));
		
		try{
			String method = "com.port.apps.settings.Settings.getInitialize";
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("init", "yes");
			ProfileInfo selectedProfileInfo = CacheData.getSelectedProfileInfo();
			if(selectedProfileInfo != null){
				data.put("id", selectedProfileInfo.getUserId());
				data.put("name", selectedProfileInfo.getUserName());
			}
			resp.put("params",data);
			returner.set(producer, pnetwork, caller); //setting consumer = producer, network
			returner.add(method,resp,"messageActivity");
			returner.send();
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	/***********************************************************/
	
	public final BroadcastReceiver mSetupReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(Constant.DEBUG)  Log.d(TAG , "Receiving pppoe status in receiver ");
			Bundle extras = intent.getExtras();
			String method = "com.port.apps.settings.Settings.setpppoe";
			JSONObject sendResponse = new JSONObject();
			JSONObject data = new JSONObject();
			
			if(extras.containsKey("status")){
				if(extras.getString("status").equalsIgnoreCase("connected")){
					try {
						if(android.os.SystemProperties.get("pppoe.error").equalsIgnoreCase("ConnectSuccess")){
							data.put("result", "success");
							data.put("pppoestatus","connected");
							sendResponse.put("params", data);
							returner.set(producer, pnetwork, caller); //setting consumer = producer, network
							returner.add(method, sendResponse, "messageActivity");
							returner.send();
						}else if(android.os.SystemProperties.get("pppoe.error").equalsIgnoreCase("WrongPassword")){
							data.put("result", "failure");
							data.put("pppoestatus","WrongPassword");
							sendResponse.put("params", data);
							returner.set(producer, pnetwork, caller); //setting consumer = producer, network
							returner.add(method, sendResponse, "messageActivity");
							returner.send();
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else if(extras.getString("status").equalsIgnoreCase("disconnected")){
					try {
	//					while(true){
	//						Thread.sleep(4000);
	//						if(android.os.SystemProperties.get("pppoe.status1").equalsIgnoreCase("0")){
	//							break;
	//						}
	//					}
						data.put("result", "success");
						data.put("pppoestatus","disconnected");
						sendResponse.put("params", data);
						returner.set(producer, pnetwork, caller); //setting consumer = producer, network
						returner.add(method, sendResponse, "messageActivity");
						returner.send();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	
	//See if Wifi is active
		public boolean isWiFiActive() { 
			// Get context Variable 
			Context tmpContext = getApplicationContext();
			//And WIFI manager object 
			WifiManager tmpManager = (WifiManager)tmpContext.getSystemService(android.content.Context.WIFI_SERVICE); 
			return tmpManager.isWifiEnabled(); 
		}

	
	public  boolean setDHCPNetwork() {
		try {
			if(Constant.model.equalsIgnoreCase("X1")){  
				//added by Tomesh for DHCP KITKAT		
				Intent intent = new Intent("com.port.pppoe.LoginPPPoE.START_SERVICE");
				intent.putExtra("connect","dhcp");
				startService(intent);
				Thread.sleep(3000);
				if(SystemProperties.get("dhcp.eth0.result").equalsIgnoreCase("OK")){
					return true;
				}else
					return false;
			} else{
				EthernetManager mEthManager;
				EthernetDevInfo mEthInfo;
				android.net.ethernet.EthernetEnabler mEthEnabler = null;
				
				mEthEnabler = new android.net.ethernet.EthernetEnabler((EthernetManager) Port.c.getSystemService("ethernet"), null);
				mEthManager = mEthEnabler.getManager();
		
				if(mEthManager != null){
					EthernetDevInfo info = new EthernetDevInfo();
					mEthInfo = mEthManager.getSavedConfig();
					String ifName = "eth0";
					if(mEthInfo != null){
						ifName = mEthInfo.getIfName();
					}
					if(Constant.DEBUG)  Log.d("Settings","setDHCPNetwork().Connection Mode : "+ifName);
					if(Constant.DEBUG)  Log.d("Settings","Already set ethernet info : "+ mEthInfo.toString());
					info.setIfName(ifName);
					info.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
					info.setIpAddress(null);
					info.setRouteAddr(null);
					info.setDnsAddr(null);
					info.setNetMask(null);
					mEthManager.updateDevInfo(info);
					return true;
			}else{
				return false;
			}
		  }
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
		}
		return false;
	}
	
	
	public void setManualNetwork(final String mIp, final String mGateway,final String mSubnet,final String mDns) {
		try {
			if(Constant.DEBUG)  Log.d("Settings", "Manual Network Called");	//mEnablePending
	
			if(Constant.model.equalsIgnoreCase("X1")) // Added by Tomesh for Kitkat Static IP
			{
				
				Intent intent = new Intent("com.port.pppoe.LoginPPPoE.START_SERVICE");
				intent.putExtra("connect","staticIP");
				intent.putExtra("ip", mIp);
				intent.putExtra("gateway", mGateway);
				intent.putExtra("mask", mSubnet);
				intent.putExtra("dns", mDns);
				startService(intent);
				Thread.sleep(4000);
				
				
			}else{
				EthernetManager mEthManager;
				EthernetDevInfo mEthInfo;
				android.net.ethernet.EthernetEnabler mEthEnabler = null;
				
			//	mEthEnabler = new android.net.ethernet.EthernetEnabler((EthernetManager) CacheData.getActivity().getSystemService("ethernet"), null);
				mEthEnabler = new android.net.ethernet.EthernetEnabler((EthernetManager) Port.c.getSystemService("ethernet"), null);
				mEthManager = mEthEnabler.getManager();
				if(mEthManager != null){
					EthernetDevInfo info = new EthernetDevInfo();
					mEthInfo = mEthManager.getSavedConfig();
					String ifName = "eth0";
					if(mEthInfo != null){
						ifName = mEthInfo.getIfName();
					}
					if(Constant.DEBUG)  Log.d("Settings","setManualNetwork().Connection Mode : "+ifName);
					info.setIfName(ifName);
					info.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);
					info.setIpAddress(mIp);
					info.setRouteAddr(mGateway);
					if (!mDns.equalsIgnoreCase("")) {
						info.setDnsAddr(mDns);
					} 
//					else {
//						info.setDnsAddr("8.8.8.8");
//					}
					info.setNetMask(mSubnet);
					mEthManager.updateDevInfo(info);
				}
				
				
			}		
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("result", "success");
			data.put("msg", getResources().getString(R.string.MANUAL_NETWORK_SETUP_SUCCESS));
			data.put("mode", "Manual");
			resp.put("params",data);
			returner.set(producer, pnetwork, caller); //setting consumer = producer, network
			returner.add("com.port.apps.settings.Settings.setRJ45",resp,"messageActivity");
			returner.send();

		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
		}
	}
	
	/*
	 * Get the STB MacAddress
	 */
	public static String getDockMacAddress(String tag){
	    try {
	    	String path = "/sys/class/net/"+tag+"/address";
	        return loadFileAsString(path).toUpperCase().substring(0, 17);
	    } catch (IOException e) {
	        return "";
	    }
	}
	
	public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));       
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception e) {
        	e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
        } // for now eat exceptions
        return "";
    }
	
	
	/*
	 * Load file content to String
	 */
	public static String loadFileAsString(String filePath) throws java.io.IOException{
	    StringBuffer fileData = new StringBuffer(1000);
	    BufferedReader reader = new BufferedReader(new FileReader(filePath));
	    char[] buf = new char[1024];
	    int numRead=0;
	    while((numRead=reader.read(buf)) != -1){
	        String readData = String.valueOf(buf, 0, numRead);
	        fileData.append(readData);
	    }
	    reader.close();
	    return fileData.toString();
	}


	private void setandGetProfileInformation(String subscriberId,String tvOperator) {
		
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
		
		if(tvOperator == null || tvOperator.equalsIgnoreCase("")){
			tvOperator = "";
		}

		ProfileInfo profileInfoByUserId = profileInfoGateway.getProfileInfo(subscriberId);
		int GuestUserId = 1000;
		if(profileInfoByUserId == null) {
			if(Constant.DEBUG)  Log.d(TAG, "userId is not exist, going to insert the data...");
			profileInfoGateway.insertProfileInfo(GuestUserId, "Guest", "", "", "", 0, "", subscriberId, 1, 0, 0, tvOperator);
		} else {
			profileInfoGateway.updateProfileInfo(GuestUserId, "Guest", "", "", "", 0, "", subscriberId, 1, 0, 0, tvOperator);
			if(Constant.DEBUG)  Log.d(TAG, "userId is exist, new data is updated.");
		}

		CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(subscriberId));
		CacheData.setSubscriberId(subscriberId);
	}

}
