package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.port.Channel;
import com.port.api.network.Listener;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class A2DPConnector extends BroadcastReceiver{
	
	private String TAG = "A2DPConnector";
	String address;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(Constant.DEBUG)  Log.d(TAG, "BT Device found");
		if (BluetoothDevice.ACTION_FOUND.equals(action)) { //after discovery has found some devices
			// Get the BluetoothDevice object from the Intent
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			// If it's already paired, skip it, because it's been listed already
			if(Constant.DEBUG)  Log.d(TAG, "Bluetooth Device Found "+device.getName());
			if(device.getName() != null && !device.getName().equalsIgnoreCase("Lukup") && !device.getName().equalsIgnoreCase("Lukup Player")){
				address = device.getAddress();
				connectDevice(address);
			}
		}
	}
	
	//Bind newly found BT devices
		private void connectDevice(String address) {
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			JSONArray outputDevices = new JSONArray();
			JSONObject jsonObject = null;
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			
			if(Constant.DEBUG)  Log.d(TAG, "connectDevice() address: "+address);
			if(address!=null && address!=""){
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
				if(device != null){
					if(device.getName()!=null && !device.getName().equalsIgnoreCase("Lukup") && !device.getName().equalsIgnoreCase("Lukup Player")){
						if (device.getBondState() != BluetoothDevice.BOND_BONDED){
							try{
								if(Constant.DEBUG)  Log.d(TAG, "NOT BOND_BONDED" + address);
								Method createBondMethod = device.getClass().getMethod("createBond");
								if((Boolean)createBondMethod.invoke(device)){
									jsonObject = new JSONObject();
									if(Constant.DEBUG)  Log.d(TAG, "Bound Devices "+device.getName());
									address = device.getAddress();
									//create JSON array of device.getName:device.getAddress
									jsonObject.put("name", device.getName());
									jsonObject.put("address", address);
									outputDevices.put(jsonObject);
									
									final Channel returner = new Channel("Dock", ""); //only to be used to send back responses from Dock to Requestor, eg, Player
									returner.set(Listener.pname, Listener.pnetwork, "com.player.UpdateService");
									
							    	data.put("connectedList", outputDevices);
									data.put("result", "success");
									resp.put("params",data);
									returner.add("com.port.apps.epg.Devices.getOutputDeviceList", resp,"startService");
									returner.send();
								}		
								
							}catch (Exception e){
								if(Constant.DEBUG)  Log.d(TAG, "setPiN failed!");
								e.printStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
							} 
						} 
					}
				}
			} 	
		}
	
	

}
