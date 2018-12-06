package com.port.api.network.wifidirect;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import com.port.Port;
import com.port.api.network.Peers;
import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

/**
* A BroadcastReceiver that notifies of important wifi p2p events.
*/
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
	
	private String TAG = "WifiDirectBroadcastReceiver";
	private WifiP2pManager manager;
	private Channel channel;
	private Port port;
	
	public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel, Application port){
		super();
		this.manager = manager;
		this.channel = channel;
		this.port = (Port) port;		
	}	

	@Override
    public void onReceive(Context context, Intent intent) {
		try{
	        String action = intent.getAction();
	        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

	        	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
	            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
	            	if(Constant.DEBUG)  Log.d(TAG, "Wifi P2P is enabled");
	            	port.startWifiP2PCommunication();
	            } else {
	            	if(Constant.DEBUG)  Log.d(TAG, "Wifi P2P is not yet enabled");
	            }
	        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
	
	        	if(Constant.DEBUG)  Log.d(TAG, "Wifi displays found");
	        	if (manager != null) {
	                manager.requestPeers(channel, Port.peerListListener);
	            }
	
	        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
	
	        	NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
	        	if(networkInfo.isConnected()){
	        		Port.mWifiBound = true;
	        	}else{
	        		Port.mWifiBound = false;
	        	}
	
	        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
	
	        }
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WIFIDISPLAY, errors.toString(), e.getMessage());
		}
    }
}