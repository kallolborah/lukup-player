package com.port.api.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.port.api.network.bonjour.ConnectedDevice;
import android.net.wifi.p2p.WifiP2pDevice;

public class Peers {
	public static List peerList = new ArrayList();
	private static List<ConnectedDevice> mConnectedDeviceList = null;
	
	public static void resetData(){
    	//remove list of peers from cache
		peerList.clear();
    }
	
	public static void onPeersAvailable(Collection<WifiP2pDevice> deviceList) {
		peerList.addAll(deviceList);
	}
	
	public static List<ConnectedDevice> getConnectedDeviceList() {
		return mConnectedDeviceList;
	}
	
	public static void setConnectedDeviceList(List<ConnectedDevice> mConnectedDeviceList) {
		Peers.mConnectedDeviceList = mConnectedDeviceList;
	}
}
