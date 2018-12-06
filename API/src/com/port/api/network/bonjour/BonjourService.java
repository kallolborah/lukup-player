package com.port.api.network.bonjour;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import org.apache.http.conn.util.InetAddressUtils;

import com.port.api.network.NetworkInfo;
import com.port.api.network.Peers;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class BonjourService extends Service {

	private static final String TAG = "BonjourService";
	//	android.net.wifi.WifiManager.MulticastLock lock;
	android.os.Handler handler = new android.os.Handler();
	private String type = "_data._tcp.local.";
	private JmDNS jmdns = null;
	private ServiceListener listener = null;
	private ServiceInfo serviceInfo;
	private static List<ConnectedDevice> mConnectedDeviceList = null;

	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if(Constant.DEBUG) Log.d(TAG, "Bonjour on create "+System.currentTimeMillis());
		mConnectedDeviceList = new ArrayList<ConnectedDevice>();
		new AsyncBounjourTask(this).execute();
		/*handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				setUpForBonjour();
			}
		}, 1000);*/
	}

	private void checkWifiState() {
		Log.d(TAG, "Bonjour on Started "+System.currentTimeMillis());
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		int intaddr = wifi.getConnectionInfo().getIpAddress();
		if (wifi.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
			if(Constant.DEBUG) Log.d(TAG, "Wifi is disabled");

		} else if (intaddr == 0) {
			if(Constant.DEBUG) Log.d(TAG, "wifi is enabled but no network connection");

		} else {
			if(Constant.DEBUG) Log.d(TAG, "wifi is enabled and there is a network");

		}
	}

	private void setUpForBonjour() {
		try {
			Log.d(TAG, "In set up for bonjour "+System.currentTimeMillis());
			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);
			WifiInfo wifiinfo = wifi.getConnectionInfo();
			int intaddr = wifiinfo.getIpAddress();
			if(NetworkInfo.getMediaPlayerIP() != null){
				try {
					if(Constant.DEBUG) Log.d(TAG, "addr value : "+NetworkInfo.getMediaPlayerIP());
					InetAddress iAddr= InetAddress.getByName(NetworkInfo.getMediaPlayerIP());
					String hostname = InetAddress.getByName(iAddr.getHostName()).toString();
					// start multicast lock
					/*lock = wifi.createMulticastLock("mylockthereturn");
					lock.setReferenceCounted(true);
					//
					lock.acquire();
					 Log.i(TAG, "multicastLock.isHeld() = " + lock.isHeld());
			         Log.i(TAG, "multicastLock.toString() = " + lock.toString());*/
					//				InetAddress iAddr= CommonUtil.getLocalInetAddress();
					jmdns = JmDNS.create(iAddr,hostname);
					//					jmdns = JmDNS.create();

					//					ServiceInfo[] infos = jmdns.list(type);
					jmdns.addServiceListener(type, listener = new ServiceListener() {
						@Override
						public void serviceResolved(final ServiceEvent ev) {
							mConnectedDeviceList = Peers.getConnectedDeviceList();
							final ServiceInfo info = ev.getInfo();
							if(Constant.DEBUG) Log.d(TAG, "Service resolved: getQualifiedName : " + info.getQualifiedName() + " port : " + info.getPort()+" IP Address : "+info.getHostAddress());

							//							if(!(ev.getName().equalsIgnoreCase("MediaPlayerDock")))
							if(ev.getName().trim().indexOf("Lukup Player") == -1){
								String ip = info.getHostAddress();
								ConnectedDevice device = new ConnectedDevice();
								device.setmName(ev.getName().trim());
								device.setmIp(ip);
								device.setmDeviceInfo("device Info not available.");
								device.setmDeviceState("paired");
								device.setmPort(9898);
								device.setmDeviceType("wifi");
								if(mConnectedDeviceList == null){
									mConnectedDeviceList =  new ArrayList<ConnectedDevice>();
								}
								if(Constant.DEBUG)  Log.d(TAG, "Bonjour service Connected Device >>>>>>>>> "+device.getmName());
								mConnectedDeviceList.add(device);
								if(Constant.DEBUG)  Log.d(TAG, "Bonjour service Resolved Flow 3");
							}
							Peers.setConnectedDeviceList(mConnectedDeviceList);
						}


						@Override
						synchronized public void serviceRemoved(ServiceEvent ev) {
							final String name = ev.getName();
							if(Constant.DEBUG)  Log.d(TAG, "Bonjour service Removed = "+name);
							mConnectedDeviceList = Peers.getConnectedDeviceList();
							if(mConnectedDeviceList != null && mConnectedDeviceList.size() >0){
								for(int i=0;i<mConnectedDeviceList.size();i++){
									ConnectedDevice device = mConnectedDeviceList.get(i);
									if(device != null && device.getmName() != null && device.getmName().trim().equalsIgnoreCase(name)){
										mConnectedDeviceList.remove(i);
										break;
									}
								}
							}
							Peers.setConnectedDeviceList(mConnectedDeviceList);
							//							final Activity activity = DataStorage.getActivity();
							/*	String ip = ev.getInfo().getHostAddress();
							if(ip != null && !(ip.trim().equals(""))) {
								List<String> remoteIPs = DataStorage.getRemoteIPs();
								if(remoteIPs.contains(ip)) {
									remoteIPs.remove(ip);
								}
							}*/
						}


						@Override
						public void serviceAdded(ServiceEvent event) {
							final String name = event.getName();
							if(Constant.DEBUG)  Log.d(TAG, "Bonjour service Added = "+name);
							if(!name.equalsIgnoreCase("MediaPlayer")){
								jmdns.requestServiceInfo(event.getType(), name, 1);
							}
							/*final Activity activity = DataStorage.getActivity();
							if(activity != null){
								activity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										//									Toast.makeText(DataStorage.getActivity(), "Name serviceAdded : "+name, Toast.LENGTH_SHORT).show();
									}
								});
							}*/
						}
					});
					serviceInfo = ServiceInfo.create("_data._tcp.local.", "Lukup Player", Constant.MEDIA_PLAYER_PORT, "Lukup Player");
					jmdns.registerService(serviceInfo);
				} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
					return;
				}
			}else{
				try {
					if (intaddr != 0) { // Only worth doing if there's an actual wifi
						// connection

						byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff),
								(byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff) };
						InetAddress addr = null;
						try {
							addr = InetAddress.getByAddress(byteaddr);
							String ip = getLocalIpAddress();
//							if(ip!= null && !ip.equalsIgnoreCase("")){
//								NetworkInfo.setMediaPlayerIP(null, ip);
//							}else{
//								NetworkInfo.setMediaPlayerIP(null, addr.getHostName());
//							}
							
							if(ip!= null && !ip.equalsIgnoreCase("")){
								NetworkInfo.setMediaPlayerIP(ip);
							}else{
								NetworkInfo.setMediaPlayerIP(addr.getHostName());
							}
							
							/*if(DataStorage.getActivity() != null){
								DataStorage.getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Toast.makeText(DataStorage.getActivity(), "IP Address  : "+DataStorage.getMediaPlayerIP(), Toast.LENGTH_SHORT).show();
									}
								});
							}*/
						} catch (Exception e1) {
							e1.printStackTrace();
						}

						if(Constant.DEBUG) Log.d(TAG, String.format("found intaddr=%d, addr=%s", intaddr, addr.toString()));
						// start multicast lock
						/*lock = wifi.createMulticastLock("mylockthereturn");
						lock.setReferenceCounted(true);
						lock.acquire();*/
						try {
							if(Constant.DEBUG) Log.d(TAG, "addr value : "+addr.getAddress());
							jmdns = JmDNS.create(addr.getHostAddress());
							jmdns.addServiceListener(type, listener = new ServiceListener() {
								@Override
								public void serviceResolved(final ServiceEvent ev) {
									final ServiceInfo info = ev.getInfo();
									if(Constant.DEBUG) Log.d(TAG, "Service resolved: getQualifiedName : " + info.getQualifiedName() + " port : " + info.getPort()+" IP Address : "+info.getHostAddress());
									if(!(ev.getName().equalsIgnoreCase("Lukup Player")) && ev.getName().contains("Remote")){
										String ip = info.getHostAddress();
									}
								}

								@Override
								public void serviceRemoved(ServiceEvent ev) {
									final String name = ev.getName();
									if(Constant.DEBUG) Log.d(TAG, "Service removed: " + name);
								}

								@Override
								public void serviceAdded(ServiceEvent event) {
									final String name = event.getName();
									jmdns.requestServiceInfo(event.getType(), name, 1);
									/*final Activity activity = DataStorage.getActivity();
									if(activity != null){
										activity.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												//									Toast.makeText(DataStorage.getActivity(), "Name serviceAdded : "+name, Toast.LENGTH_SHORT).show();
											}
										});
									}*/
								}
							});
							serviceInfo = ServiceInfo.create("_data._tcp.local.", "Lukup Player", Constant.MEDIA_PLAYER_PORT, "Lukup Player");
							jmdns.registerService(serviceInfo);
						} catch(Exception e){
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
							return;
						}
					} else{
						checkWifiState();
					}
				} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		if(Constant.DEBUG)  Log.d(TAG, "On Destroy of Bonjour service");
		if (jmdns != null) {
			if (listener != null) {
				jmdns.removeServiceListener(type, listener);
				listener = null;
			}
			jmdns.unregisterAllServices();
			try {
				jmdns.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			jmdns = null;
		}
		try{
			/*if(lock != null){
				lock.release();	
				lock = null;
			}*/
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
		}
		super.onDestroy();
	}
	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
			return null;
		}
		return null;
	}

	public class AsyncBounjourTask extends AsyncTask<Void, Void, Void>{
		Context context;

		public AsyncBounjourTask(Context context) {
			this.context = context;
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				setUpForBonjour();
			}catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}
			return null;
		}

	}
}