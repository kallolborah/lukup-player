package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Set;

import org.apache.http.conn.util.InetAddressUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.port.Channel;
import com.port.Port;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class Devices extends IntentService {
	
	private String TAG = "Devices";
	String method = "com.port.apps.epg.Devices.";
	
	//Receiver
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String caller;
	Channel returner;
	String dockID;
	String eventId = "";
	BluetoothAdapter mBluetoothAdapter = null;
	String address;

	native int native_dlna_url(String url, String iface);
	native int native_dlna_url_uploaded();
	native int native_dlna_stop();
	
	//Display
	private DisplayManager mDisplayManager;
	
	public Devices() {
		super("Device");
		System.loadLibrary("dlna_url");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String type = "";
		String target = "";	
		String displayID = "";
		String url = "";
		
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
	    	if(Constant.DEBUG)  Log.d(TAG, "Pnetwork : "+ pnetwork + " CNetwork :" + cnetwork + " Producer :" + producer + " Caller: "+ caller + " macID : "+dockID);

	    	if(returner==null){
		    	returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
				returner.set(producer, pnetwork, caller);
			}
			
	    	if(extras.containsKey("Params")){
	    		try{
		    		functionData = extras.getString("Params");
		    		JSONObject jsonObj = new JSONObject(functionData);
		    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
		    		if(jsonObj.has("id")){
		    			eventId = jsonObj.getString("id");
		    		}
		    		if(jsonObj.has("type")){
		    			type = jsonObj.getString("type");
		    		}
		    		//check selected device to stream content to
		    		if(jsonObj.has("address")){
		    			target = jsonObj.getString("address");
		    		}
		    		if(jsonObj.has("displayID")){
		    			displayID = jsonObj.getString("displayID");
		    		}
		    		if(jsonObj.has("sourceUrl")){
		    			url = jsonObj.getString("sourceUrl");
		    		}
	    		} catch (JSONException e) {
	    			e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}
	    	}
	    	if(extras.containsKey("Method")){
	    		try{
	    			func = extras.getString("Method");
	    			if(func.equalsIgnoreCase("getOutputDeviceList")){
		    			getOutputDeviceList(eventId,type);
		    		}
	    			if(func.equalsIgnoreCase("stream")){
	    				sendOutputToDevice(target, eventId);
	    			}
	    			if(func.equalsIgnoreCase("stopCommandToDevice")){
	    				stopCommandToDevice(target);
	    			}
	    			if(func.equalsIgnoreCase("playOnDLNA")){
	    				if(Constant.DEBUG)Log.d(TAG, "playOnDLNA is called");
	    				playOnDLNA(url);
	    			}
	    			if(func.equalsIgnoreCase("StopDLNA")){
	    				if(Constant.DEBUG)Log.d(TAG, "StopDLNA is called");
	    				StopDLNA();
	    			}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}		    		
	    	}
	    	
	    }
	}
	
	private void playOnDLNA(final String URL) {
		if(Constant.DEBUG)Log.d(TAG, "playOnDLNA is called");
		try{
			new Thread (new Runnable() {
				
				@Override
				public void run() {
					native_dlna_stop();
					String ip = getIPInterface();
					if(Constant.DEBUG)Log.d(TAG, "native_dlna_url is called with IP " + ip + " and URL " + URL);
					int r = 0;
					native_dlna_url(URL, ip); //start service 
					r = native_dlna_url_uploaded();//load url
					if(Constant.DEBUG)Log.d(TAG, "DLNA url load result = " + r);
					try{
						if(r==1){
							if(Constant.DEBUG)Log.d(TAG, "Sending success ");
							JSONObject sendResponse = new JSONObject();
							JSONObject data = new JSONObject();
							data.put("result", "success");
							sendResponse.put("params", data);
							returner.add(method+"playOnDLNA", sendResponse,"messageActivity");
							returner.send();
						}else{
							if(Constant.DEBUG)Log.d(TAG, "Sending failure ");
							JSONObject sendResponse = new JSONObject();
							JSONObject data = new JSONObject();
							data.put("result", "failure");
							sendResponse.put("params", data);
							returner.add(method+"playOnDLNA", sendResponse,"messageActivity");
							returner.send();
						}
					}catch(Exception e){}
				}
			}).start();
			
			
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}	
	}
	
	
	private void StopDLNA() {
		if(Constant.DEBUG)Log.d(TAG, "StopDLNA is called");
		try{
			new Thread (new Runnable() {
				
				@Override
				public void run() {
					if(Constant.DEBUG)Log.d(TAG, "native_dlna_Stop");
					int r = 0;
					r = native_dlna_stop();
					
					try{
						if(r==1){
							JSONObject sendResponse = new JSONObject();
							JSONObject data = new JSONObject();
							data.put("result", "success");
							sendResponse.put("params", data);
							returner.add(method+"StopDLNA", sendResponse,"messageActivity");
							returner.send();
						}else{
							JSONObject sendResponse = new JSONObject();
							JSONObject data = new JSONObject();
							data.put("result", "failure");
							sendResponse.put("params", data);
							returner.add(method+"StopDLNA", sendResponse,"messageActivity");
							returner.send();
						}
					}catch(Exception e){}
				}
			}).start();
			
			
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}	
	}
	
	
	public String getIPInterface() {
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
							return intf.getName();
						}
					}
				}
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
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	
	private void getOutputDeviceList(String Id,String extension) {
		if(Constant.DEBUG)  Log.d(TAG,"getOutputDeviceList().extension: "+extension);
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray outputDevices = new JSONArray();
		JSONObject jsonObject = null;

    	returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
		returner.set(producer, pnetwork, caller); //setting consumer = producer, network
		
		if(mBluetoothAdapter == null){
			if(Constant.DEBUG)  Log.d(TAG, "mBluetoothAdapter");
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}	
		
		mBluetoothAdapter.startDiscovery();		
		
		if(Constant.DEBUG)  Log.d(TAG, "getOutputDeviceList() "+ mBluetoothAdapter.getScanMode());
			
		try{
			Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();            
			if(pairedDevice.size()>0) {
				for(BluetoothDevice device : pairedDevice) {
					if(device.getName()!=null && !device.getName().equalsIgnoreCase("Lukup") && !device.getName().equalsIgnoreCase("Lukup Player")){
						jsonObject = new JSONObject();
						if(Constant.DEBUG)  Log.d(TAG, "Searched Devices "+device.getName());
						address = device.getAddress();
						//create JSON array of device.getName:device.getAddress
						jsonObject.put("name", device.getName());
						jsonObject.put("address", address);
						outputDevices.put(jsonObject);
					}
				}
				data.put("connectedList", outputDevices);
				data.put("id", Id);
				data.put("result", "success");
				resp.put("params",data);
				returner.add(method+"getOutputDeviceList", resp,"messageActivity");
				returner.send();
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
		}
		
	}
	
	//send to A2DP
	private void sendOutputToDevice(String target, String eventid){
		try{
			returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
			returner.set(target, cnetwork, caller); //setting consumer = producer, network
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			ProgramInfo programInfo = programGateway.getProgramInfoByEventId(Integer.parseInt(eventid));
			String src = programInfo.getEventSrc();
			if(Constant.DEBUG)  Log.d(TAG, "Sending to output "+ target+", src "+ src);
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("url", src);
			resp.put("params",data);
			
			returner.add("connect", resp,"");
			returner.send();
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
		}
	}
	
	private void stopCommandToDevice(String target){
		if(Constant.DEBUG)  Log.d(TAG, "stopCommandToDevice");
		try{
			returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
			returner.set(target, cnetwork, caller); //setting consumer = producer, network
			
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			resp.put("params",data);
			
			returner.add("disconnect", resp,"");
			returner.send();
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
		}
	}
	
}