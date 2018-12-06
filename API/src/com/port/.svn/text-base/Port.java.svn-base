package com.port;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Application;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.util.LruCache;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;
import com.port.api.jni.GuiMwDvb;
import com.port.api.jni.NativeHdmicvbs;
import com.port.api.network.bt.BluetoothConnectionService;
import com.port.api.network.bt.BluetoothConnectionService.BTBinder;
import com.port.api.network.wifidirect.WiFiDirectBroadcastReceiver;
import com.port.api.network.wifidirect.WifiP2PConnectionService;
import com.port.api.network.wifidirect.WifiP2PConnectionService.WifiBinder;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class Port extends Application {

	private static String TAG = "Port" ;

	//BT related
	public static BluetoothConnectionService mBluetoothService;
	public static BluetoothAdapter mBluetoothAdapter;
	public static boolean mBTBound;
	public static String BTAddress;
	Messenger mService = null;

	//HDMI
	public static int hdmi = 0;
	public static NativeHdmicvbs nativeHdmi;
	public static GuiMwDvb guiMwDvb;

	//Persistence related
	public static LruCache<String, Bitmap> mMemoryCache;
	public static int last_viewed_service;
	public static int last_viewed_event;
	public static Context c;

	private static int BTCount = 0;

	//Timers for jobs
	private static Timer t;
	private static Timer adtimer;
	private static Timer d;

	//Request and Response handlers
	private static MessageQueue sendq = null;
	private static MessageQueue recvq = null;
	public static ArrayList<HashMap<String,String>> pairs = new ArrayList<HashMap<String,String>>();

	//WifiP2P handlers
	public static WifiP2pManager.Channel mChannel;
	public static WifiP2pManager mManager;
	public static List peers = new ArrayList();
	public static WifiP2PConnectionService mWifiP2PService;
	private BroadcastReceiver receiver = null;
	public static boolean mWifiBound;

	@Override
	public void onCreate() {
		super.onCreate();	
		if(Constant.DEBUG) Log.d(TAG, "onCreate()");
		c = getApplicationContext();
		try{
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if(mBluetoothAdapter==null){
				if(Constant.DEBUG) Log.d(TAG, "BluetoothAdapter has a problem on the Port");
				mBTBound = false;
			} 
			BTAddress = mBluetoothAdapter.getAddress();
			if(Constant.DEBUG) Log.d(TAG, "BluetoothAdapter Address: "+BTAddress);
			if (!mBluetoothAdapter.isEnabled()) {
				mBluetoothAdapter.enable();
				startBluetoothCommunication();
			} else {
				startBluetoothCommunication();
			}

		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
		}
		
		if(!Constant.model.equalsIgnoreCase("S")){
			try{
				WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
				if(!wifi.isWifiEnabled()){
					wifi.setWifiEnabled(true);
					if(Constant.DEBUG) Log.d(TAG, "Wifi was not enabled, enabled it !");
				}else{
					if(Constant.DEBUG) Log.d(TAG, "Wifi is already enabled");
				}
			}catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WIFI, errors.toString(), e.getMessage());
			}
			
			try{
				mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
				mChannel = mManager.initialize(this, getMainLooper(), null);
				
				receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
			} catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WIFIDISPLAY, errors.toString(), e.getMessage());
			}
			
			startWifiP2PCommunication();
		}
		
		if(!Constant.model.equalsIgnoreCase("X1")){
			nativeHdmi = new NativeHdmicvbs();
			hdmi = new NativeHdmicvbs().getHdmiOutStatus();
		}

		imageMemoryInitialize(); 

		Producer p = new Producer(Port.this);

		adtimer = new Timer("Update Ads");
		adtimer.schedule(AdsScheduling, 60000L, 3600000*6L); //every 3 hrs

		d = new Timer("Discoverable");
		d.schedule(pairPlayer, 60000L, 60000L);//1 min      	

		//Initialize the Parse SDK.
		if(Constant.DEBUG) Log.d(TAG, "Model Name: "+Build.MODEL.replaceAll("\\s+",""));

		Parse.initialize(this, "rjzvMqPaEhZwcNb96exJzieYbh7Iu1UHKpZ7NDOj", "zqrBTyKzoKLjZDRWvFGd54gyAkfsnfuYr4eNW4nI");
		PushService.setDefaultPushCallback(this.getApplicationContext(), com.port.Main.class);
		PushService.subscribe(getApplicationContext(), Build.MODEL.replaceAll("\\s+",""), Main.class);
		ParseInstallation.getCurrentInstallation().saveEventually();

		if (Constant.DVB) {	//DVB middleware
			guiMwDvb = new GuiMwDvb(); //DVB middleware
			guiMwDvb.EnableMWDvbInit(true);
		}
	}
	
	public void startWifiP2PCommunication(){
		Intent intent = new Intent(this, WifiP2PConnectionService.class);
		bindService(intent, wConnection, Context.BIND_AUTO_CREATE); 
		
		mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(Constant.DEBUG)  Log.d(TAG, "Set the Lukup Player as group owner..");
			}
			
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				if(Constant.DEBUG)  Log.d(TAG, "Failure in setting as group owner..");
			}
		});
		
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if(Constant.DEBUG)  Log.d(TAG, "Discovering Wifi direct peers..");
			}
			
			@Override
			public void onFailure(int reason) {
				// TODO Auto-generated method stub
				if(Constant.DEBUG)  Log.d(TAG, "Failed discovering Wifi direct peers" + reason);
			}
		});
	}

	private void startBluetoothCommunication(){	
		//start bluetooth listener
		Intent intent = new Intent(this, BluetoothConnectionService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE); 
		
		if(Constant.DEBUG) Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			getApplicationContext().startActivity(discoverableIntent);
		}
	}
	
	public static ServiceConnection wConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance	
			WifiBinder binder = (WifiBinder) service;
			mWifiP2PService = binder.getService();
			try {
				mWifiP2PService.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Constant.DEBUG)  Log.d(TAG , "Wifi P2P initialized");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mWifiBound = false;
			if(Constant.DEBUG)  Log.d(TAG , "Wifi P2P Disconnected");
		}
	};	

	public static ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance	
			BTBinder binder = (BTBinder) service;
			mBluetoothService = binder.getService();
			mBluetoothService.start();
			if(Constant.DEBUG)  Log.d(TAG , "BT initialized");
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBTBound = false;
			if(Constant.DEBUG)  Log.d(TAG , "BT Disconnected");
		}
	};	

	private TimerTask pairPlayer = new TimerTask(){
		@Override
		public void run(){
			try{
				if(Constant.DEBUG) Log.d(TAG, "TimerTask is Running");
				if(!mBTBound){
					if(Constant.DEBUG) Log.d(TAG, "ensure discoverable");
					if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
						Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
						discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
						discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
						getApplicationContext().startActivity(discoverableIntent);
					}
				}
				if(!mWifiBound){
					mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
						
						@Override
						public void onSuccess() {
							// TODO Auto-generated method stub
							if(Constant.DEBUG)  Log.d(TAG, "Discovering Wifi direct peers..");
						}
						
						@Override
						public void onFailure(int reason) {
							// TODO Auto-generated method stub
							if(Constant.DEBUG)  Log.d(TAG, "Failed discovering Wifi direct peers" + reason);
						}
					});
				}
			} catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
			}
		}		
	};


	private TimerTask AdsScheduling = new TimerTask() {
		@Override
		public void run() {
			try {
				if(Constant.DEBUG)  Log.d(TAG, "inside AdsScheduling");
				Intent mServiceIntent = new Intent(Port.this, Schedule.class);
				mServiceIntent.putExtra("Title", "ads");
				startService(mServiceIntent);
			} catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}
		}
	};

	public MessageQueue pair(String pnetwork, String producer, String macID, String consumer, String network){
		if(Constant.DEBUG)  Log.d(TAG , "Pairing: Producer- "+ producer + " PID- " + macID + " Consumer- " + consumer + " Network- " + network);
		recvq = MessageQueue.getInstance("receiver");
		recvq.receive(pnetwork, Port.this);

		HashMap<String, String> list = new HashMap<String, String>();
		list.put("Producer",producer);
		list.put("macID", macID);
		list.put("Consumer", consumer);
		list.put("Network", network);
		pairs.add(list);

		return recvq;
	}

	public void processMessage(String PackageClass, String MethodName, MessageQueueElement data, String pnetwork){
		if(Constant.DEBUG)  Log.d(TAG, "PackageClass : "+PackageClass+", MethodName : "+MethodName+", pnetwork : "+pnetwork);

		try{
			if(data.getCalled().equalsIgnoreCase("startActivity")){
				if(Constant.DEBUG)  Log.d(TAG, "PackageClass : "+PackageClass);
				Intent intent = new Intent(this, Class.forName(PackageClass));					
				intent.putExtra("Producer", data.getProducer());
				intent.putExtra("ProducerNetwork", pnetwork);
				intent.putExtra("ConsumerNetwork", data.getNetwork());
				intent.putExtra("Method", MethodName);
				intent.putExtra("Caller", data.getCaller());
				intent.putExtra("Params", data.getJsonParams());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				getApplicationContext().startActivity(intent);
			}else if (data.getCalled().equalsIgnoreCase("messageActivity")){
				Intent intent = new Intent();					
				intent.putExtra("Producer", data.getProducer());
				intent.putExtra("ProducerNetwork", pnetwork);
				intent.putExtra("ConsumerNetwork", data.getNetwork());
				intent.putExtra("Method", MethodName);
				intent.putExtra("Caller", data.getCaller());
				intent.putExtra("Params", data.getJsonParams());
				intent.setAction(PackageClass);
				sendBroadcast(intent);
			}else if (data.getCalled().equalsIgnoreCase("startService")){
				int index = PackageClass.lastIndexOf('.');
				String packagename = PackageClass.substring(0, index);
				if(Constant.DEBUG)  Log.d(TAG, "PackageClass : "+PackageClass+", packagename : "+packagename);
				Intent intent = new Intent();
				intent.setClassName(packagename,PackageClass);
				intent.putExtra("Producer", data.getProducer());
				intent.putExtra("ProducerNetwork", pnetwork);
				intent.putExtra("ConsumerNetwork", data.getNetwork());
				intent.putExtra("Method", MethodName);
				intent.putExtra("Caller", data.getCaller());
				intent.putExtra("Params", data.getJsonParams());
				startService(intent);
			}else if (data.getCalled().equalsIgnoreCase("messageService")){
				Intent intent = new Intent();					
				intent.putExtra("Producer", data.getProducer());
				intent.putExtra("ProducerNetwork", pnetwork);
				intent.putExtra("ConsumerNetwork", data.getNetwork());
				intent.putExtra("Method", MethodName);
				intent.putExtra("Caller", data.getCaller());
				intent.putExtra("Params", data.getJsonParams());
				intent.setAction(PackageClass);
				sendBroadcast(intent);
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}

	private void imageMemoryInitialize(){
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/4th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 4;

		if(mMemoryCache == null){
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					// The cache size will be measured in kilobytes rather than
					// number of items.
					return bitmap.getByteCount() / 1024;
				}
			};
		}
	}

	public static PeerListListener peerListListener = new PeerListListener() {
		@Override
		public void onPeersAvailable(WifiP2pDeviceList peerList) {
			try{
				// Out with the old, in with the new.
				ArrayList<HashMap<String,String>> wifiList = new ArrayList<HashMap<String,String>>();

				peers.clear();
				peers.addAll(peerList.getDeviceList());

				for(int i=0; i<peers.size(); i++){
					WifiP2pDevice device = (WifiP2pDevice) peers.get(i);
					HashMap<String,String> map = new HashMap<String, String>();
					String name = device.deviceName;
					String address = device.deviceAddress;
					map.put("name", name);
					map.put("address", address);
					wifiList.add(map);
					if(Constant.DEBUG)  Log.d(TAG,"Name: "+name+", address: "+address);
				}

				if (wifiList.size() == 0) {
					Log.d(TAG, "No wifi devices found");
					return;
				}else{
					if(Constant.DEBUG) Log.d(TAG, "wifiList.size(): "+wifiList.size());
					Intent peerBroadcast = new Intent();
					peerBroadcast.setAction("LUKUP_WIFI_DISPLAYS");
					peerBroadcast.putExtra("Devices", wifiList);
					c.sendBroadcast(peerBroadcast);
				}
			} catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WIFIDISPLAY, errors.toString(), e.getMessage());
			}
		}
	};
}
