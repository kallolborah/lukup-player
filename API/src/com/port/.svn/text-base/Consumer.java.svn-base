package com.port;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.port.api.network.Transmitter;
import com.port.api.network.bonjour.SocketTransmitter;
import com.port.api.util.CacheData;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class Consumer {
	private static Consumer c=null;
	String message;
	String producer = null;
	String pid = null;
	String consumer = null;
	String network = null;
	String method = null;
	String called = null;
	String caller = null;
	String params = null;
	private String TAG = "Consumer";
	boolean res = false;
	
	//Profile related
	public static MediaPlayer mp;
	
	public static void getInstance(MessageQueue mq){
		if (c==null){
			new Consumer(mq);
		}		
	}	
	
	protected Consumer(final MessageQueue sendq){
		
		new Thread() {
			@Override
			public void run(){
				
				while(true) { //keep popping from queue
					MessageQueueElement data = null;
					try {
						data = sendq.pop();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(data != null){
						producer = data.getProducer();
						pid = data.getMacId();
						consumer = data.getConsumer();
						network = data.getNetwork();
						method = data.getMethod();
						params = data.getJsonParams();
						caller = data.getCaller();
						called = data.getCalled();
						if(Constant.DEBUG)  Log.d(TAG , "producer: "+producer+", consumer: "+consumer + ", pid " + pid);	
						if(Constant.DEBUG)  Log.d(TAG , " network: "+network+", method: "+method+" caller: "+caller+", called: "+called);	
						
						if(producer!=null && consumer!=null && (network.equalsIgnoreCase("BT")||network.equalsIgnoreCase("Wifi")) && method!=null && called!=null && caller!=null){
							//construct json string here only if producer, pid, consumer, network and method are not null
							JSONObject resp = new JSONObject();
							JSONObject jsonObject = new JSONObject();
							JSONObject paramObject = new JSONObject();
							String result= "";
							if(Constant.DEBUG)  Log.d(TAG , "Before Transmitting ");
							try {
								paramObject = data.getJson();
								if(paramObject.has("params")){
									result = paramObject.getString("params");
								}
								JSONObject paramsObject = new JSONObject(result.toString());
								jsonObject.put("Producer", producer);						
								jsonObject.put("macID", pid);
								jsonObject.put("Consumer", consumer);
								jsonObject.put("Network", network);
								jsonObject.put("Handler", method);
								jsonObject.put("Caller", caller);
								jsonObject.put("Called", called);
								jsonObject.put("params", paramsObject);
								resp.put("data", jsonObject);
								message = resp.toString();
									
							} catch(Exception e){
								e.printStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
							}
							
							Intent intent = new Intent(Port.c, Transmitter.class);
							intent.putExtra("JSON", message);
							intent.putExtra("network", network);
							Port.c.startService(intent);
								
						} else if (network.equalsIgnoreCase("a2dp") && consumer!=null){	
							if(Constant.DEBUG)  Log.d(TAG , " Streaming by a2dp to : "+ consumer);	
							
							try{
								if(method.equalsIgnoreCase("disconnect")){
										new DisconnectBt(consumer).execute();
								}else if(method.equalsIgnoreCase("connect")){
									
									if(Constant.DEBUG)  Log.d(TAG, "Going for Streaming");
									JSONObject paramObject = new JSONObject();
									String result= "";
									paramObject = data.getJson();
									if(paramObject.has("params")){
										result = paramObject.getString("params");
									}
									JSONObject paramsObject = new JSONObject(result.toString());
									String path = "";
									if(paramsObject.has("url")){
										path = paramsObject.getString("url");
									}
									if(Constant.DEBUG)  Log.d(TAG, "path: "+path);
									if(path != null && !path.equalsIgnoreCase("")){
										if(!CacheData.getConnectedA2dpDevice().equalsIgnoreCase("")){
											if(mp!=null){
												if(mp.isPlaying()){
													mp.stop();
													mp.release();
													mp = null;
													CacheData.setConnectedA2dpDevice("");
													CacheData.setConnectedA2DPDeviceName("");
													if(Constant.DEBUG)  Log.d(TAG, "Stopping already running audio");
												}
											}
										}
//										new DisconnectBt(CacheData.getConnectedA2dpDevice()).execute();
										new ConnectBt(consumer, path).execute(); //consumer is BT device address
									}
								}
								
							}catch(Exception e){
								e.printStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
							}
						} 
					}
				}
			}
		}.start();
	}
	
	private class DisconnectBt extends AsyncTask<Void, Void, Void>{
		String consumer;
		BluetoothDevice device = null;
		
		public DisconnectBt(String Consumer) {
			this.consumer = Consumer;
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			if(Constant.DEBUG)  Log.d(TAG, "disconnectBt doInBackground: consumer: "+consumer);
			BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();

			Set<BluetoothDevice> pairedDevices = mBTA.getBondedDevices();
			
			for (BluetoothDevice dev : pairedDevices) {
				if (dev.getAddress().equalsIgnoreCase(consumer))
					device = dev;
			}
			if (device == null){
				if(Constant.DEBUG)  Log.d(TAG, "doInBackground: device null");

			}else{
				if(Constant.DEBUG)  Log.d(TAG, "Found device " + device.getName());
				try {
					Class<?> c2 = Class.forName("android.os.ServiceManager");
					Method m2 = c2.getDeclaredMethod("getService", String.class);
					IBinder b = (IBinder) m2.invoke(c2.newInstance(), "bluetooth_a2dp");
					if (b == null) {
						// For Android 4.2 Above Devices
						BluetoothAdapter.getDefaultAdapter().getProfileProxy(Port.c.getApplicationContext(),new ServiceListener() {
							@Override
							public void onServiceDisconnected(int profile) {
								if(Constant.DEBUG)  Log.d(TAG, "Service disconnected ");
							}
	
							@Override
							public void onServiceConnected(int profile,BluetoothProfile proxy) {
								BluetoothA2dp a2dp = (BluetoothA2dp) proxy;
								try {								
									CacheData.setConnectedA2dpDevice("");
									CacheData.setConnectedA2DPDeviceName("");
									if(mp!=null){
										if(mp.isPlaying()){
											mp.stop();
											mp.release();
											mp = null;
											if(Constant.DEBUG)  Log.d(TAG, "Media Player Stop");
										}
										JSONObject resp = new JSONObject();
										JSONObject jsonObject = new JSONObject();
										JSONObject data1 = new JSONObject();
										data1.put("result", "success");
										data1.put("name", CacheData.getConnectedA2DPDeviceName());
										data1.put("state", "stop");
										jsonObject.put("Producer", "Dock");						
										jsonObject.put("macID", Build.ID);
										jsonObject.put("Consumer", consumer);
										jsonObject.put("Network", "BT");
										jsonObject.put("Handler", "com.port.apps.epg.Devices.playA2dp");
										jsonObject.put("Caller", "com.player.UpdateService");
										jsonObject.put("Called", "startService");
										jsonObject.put("params", data1);
										resp.put("data", jsonObject);
										message = resp.toString();

										Intent intent = new Intent(Port.c, Transmitter.class);
										intent.putExtra("JSON", message);
										intent.putExtra("network", network);
										Port.c.startService(intent);
									}
									a2dp.getClass().getMethod("disconnect",BluetoothDevice.class).invoke(a2dp, device);
									
								} catch(Exception e){
									e.printStackTrace();
									StringWriter errors = new StringWriter();
									e.printStackTrace(new PrintWriter(errors));
									SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
								}
							}
						}, BluetoothProfile.A2DP);
						
					} 
				} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
				}
			}
			return null;
		}
	}
		
	private class ConnectBt extends AsyncTask<Void, Void, Void> {
		String path;
		String consumer;
		BluetoothDevice device = null;
		
		public ConnectBt(String Consumer, String Path) {
			this.consumer = Consumer;
			this.path = Path;
		}

		@Override
		protected Void doInBackground(Void ... arg) {
			if(Constant.DEBUG)  Log.d(TAG, "ConnectBt doInBackground: consumer: "+consumer);
			BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();
			if (mBTA == null || !mBTA.isEnabled())
				confirm(false);

			Set<BluetoothDevice> pairedDevices = mBTA.getBondedDevices();
			
			for (BluetoothDevice dev : pairedDevices) {
				if (dev.getAddress().equalsIgnoreCase(consumer))
					device = dev;
			}
			
			if (device == null){
				if(Constant.DEBUG)  Log.d(TAG, "doInBackground: device null");
				confirm(false);

			}else{
				if(Constant.DEBUG)  Log.d(TAG, "doInBackground: device found "+ device.getName());
				try {

					BluetoothAdapter.getDefaultAdapter().getProfileProxy(Port.c.getApplicationContext(),new ServiceListener() {
						
						@Override
						public void onServiceDisconnected(int profile) {
							if(Constant.DEBUG)  Log.d(TAG, "Service disconnected ");							
						}

						@Override
						public void onServiceConnected(int profile,BluetoothProfile proxy) {
							
							if(mp==null){
								if(Constant.DEBUG)  Log.d(TAG, "MediaPlayer null ");	
								mp = new MediaPlayer();
							}else{
								if(Constant.DEBUG)  Log.d(TAG, "MediaPlayer not null");	
								if(Constant.DEBUG)  Log.d(TAG, "MediaPlayer: "+mp);	
							}
							
							if(profile==BluetoothProfile.A2DP){ //profile of device connected is A2DP
								try{
									Class<?> c2 = Class.forName("android.os.ServiceManager");
									Method m2 = c2.getDeclaredMethod("getService", String.class);
									IBinder b = (IBinder) m2.invoke(c2.newInstance(), "bluetooth_a2dp");
									if(b==null){
										BluetoothA2dp a2dp = (BluetoothA2dp) proxy;
										 try {
											if(Constant.DEBUG)  Log.d(TAG, "Service connected " + device.getAddress());
											a2dp.getClass().getMethod("connect",BluetoothDevice.class).invoke(a2dp, device);
											
											if(a2dp.getConnectionState(device)==BluetoothA2dp.STATE_CONNECTED || a2dp.getConnectionState(device)==BluetoothA2dp.STATE_CONNECTING){
												//play
												if(Constant.DEBUG)  Log.d(TAG, "now Playing  " + device.getAddress());
												mp.setDataSource(path);
												mp.prepareAsync();
												mp.setOnPreparedListener(new OnPreparedListener() {
													@Override
													public void onPrepared(MediaPlayer mp) {
														mp.start();
														CacheData.setConnectedA2dpDevice(device.getAddress());
														CacheData.setConnectedA2DPDeviceName(device.getName());
														confirm(true);
													}
												});
											}else{
												if(Constant.DEBUG)  Log.d(TAG, "Failed to connect ");
												confirm(false);
											}
										 } catch(Exception e){
											e.printStackTrace();
											StringWriter errors = new StringWriter();
											e.printStackTrace(new PrintWriter(errors));
											SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
											confirm(false);
										 }
									}else {
										// For Android below 4.2 devices
										Class<?> c3 = Class.forName("android.bluetooth.IBluetoothA2dp");
										Class<?>[] s2 = c3.getDeclaredClasses();
										Class<?> c = s2[0];
										Method m = c.getDeclaredMethod("asInterface", IBinder.class);
										m.setAccessible(true);
	//										IBluetoothA2dp a2dp = (IBluetoothA2dp) m.invoke(null, b);
	//										a2dp.connect(device);
									}
								}catch(Exception e){
									e.printStackTrace();
									StringWriter errors = new StringWriter();
									e.printStackTrace(new PrintWriter(errors));
									SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
									confirm(false);
								}
							}else if(profile==BluetoothProfile.HEADSET){
								if(Constant.DEBUG)  Log.d(TAG, "Headset profile connected  ");
								
							}
						}
					},BluetoothProfile.STATE_CONNECTED);
				} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
					confirm(false);
				}
			}
			return null;
		}
		
		protected void confirm(boolean result) {
			if(!result){
				try{
					JSONObject resp = new JSONObject();
					JSONObject jsonObject = new JSONObject();
					JSONObject data1 = new JSONObject();
					data1.put("result", "failure");
					data1.put("name", CacheData.getConnectedA2DPDeviceName());
					jsonObject.put("Producer", "Dock");						
					jsonObject.put("macID", Build.ID);
					jsonObject.put("Consumer", consumer);
					jsonObject.put("Network", "BT");
					jsonObject.put("Handler", "com.port.apps.epg.Devices.playA2dp");
					jsonObject.put("Caller", "com.player.UpdateService");
					jsonObject.put("Called", "startService");
					jsonObject.put("params", data1);
					resp.put("data", jsonObject);
					message = resp.toString();
					
					Intent intent = new Intent(Port.c, Transmitter.class);
					intent.putExtra("JSON", message);
					intent.putExtra("network", network);
					Port.c.startService(intent);
				} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
				}
			}else{
				try {
					JSONObject resp = new JSONObject();
					JSONObject jsonObject = new JSONObject();
					JSONObject data1 = new JSONObject();
					data1.put("result", "success");
					data1.put("name", CacheData.getConnectedA2DPDeviceName());
					data1.put("state", "start");
					data1.put("address", consumer);
					jsonObject.put("Producer", "Dock");
					jsonObject.put("macID", Build.ID);
					jsonObject.put("Consumer", consumer);
					jsonObject.put("Network", "BT");
					jsonObject.put("Handler","com.port.apps.epg.Devices.playA2dp");
					jsonObject.put("Caller", "com.player.UpdateService");
					jsonObject.put("Called", "startService");
					jsonObject.put("params", data1);
					resp.put("data", jsonObject);
					message = resp.toString();
					
					Intent intent = new Intent(Port.c, Transmitter.class);
					intent.putExtra("JSON", message);
					intent.putExtra("network", network);
					Port.c.startService(intent);
				} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_A2DP, errors.toString(), e.getMessage());
				}				
				
			}
		}
	}

}
