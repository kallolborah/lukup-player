package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONObject;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.port.Consumer;
import com.port.api.network.Transmitter;
import com.port.api.util.CacheData;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class A2DPDisconnector extends BroadcastReceiver{

	private String TAG = "A2DPDisconnector";
	String address;
	BluetoothDevice device;

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if(Constant.DEBUG)  Log.d(TAG, "BT Device disconnected");
		if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) { 
			// Get the BluetoothDevice object from the Intent
			device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if(device.getAddress().equalsIgnoreCase(CacheData.getConnectedA2dpDevice())){
				new Thread() {
					@Override
					public void run(){
						try{
							if(Consumer.mp.isPlaying()){
								Consumer.mp.stop();
								CacheData.setConnectedA2dpDevice("");
								CacheData.setConnectedA2DPDeviceName("");
								if(Constant.DEBUG)  Log.d(TAG, "Stopping Media Player in A2DPDisconnector");
								JSONObject resp = new JSONObject();
								JSONObject jsonObject = new JSONObject();
								JSONObject data1 = new JSONObject();
								data1.put("result", "success");
								data1.put("state", "start");
								jsonObject.put("Producer", "Dock");						
								jsonObject.put("macID", Build.ID);
								jsonObject.put("Consumer", device.getAddress());
								jsonObject.put("Network", "BT");
								jsonObject.put("Handler", "com.port.apps.epg.A2DPDisconnector.Stop");
								jsonObject.put("Caller", "com.player.UpdateService");
								jsonObject.put("Called", "startService");
								jsonObject.put("params", data1);
								resp.put("data", jsonObject);
								String message = resp.toString();
//								BluetoothTransmitter.sendMessage(message);
								Intent intent = new Intent(context,Transmitter.class);
								intent.putExtra("JSON", message);
								context.startService(intent);
							}
						}catch(Exception e){
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
						}
					}
				}.start();				
			}
		}
	}	

}
