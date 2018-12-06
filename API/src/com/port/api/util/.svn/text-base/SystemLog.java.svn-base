package com.port.api.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import com.port.Port;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class SystemLog {
	
	private static final String TAG = "SystemLog";
	
	public static final String TYPE_DOCK	= "Dock";
	public static final String TYPE_PLAYER	= "Player";
	
	
	/*
	1. Bluetooth       - All the errors related to bluetooth
	2. MediaPlayer   - All the errors related to playing content
	3. WiFi              - All the errors related to WiFi and  HotSpot
	4. A2dp             - All the errors related to a2dp
	5. Ethernet        - All the errors related to Ethernet
	6. HDMI            - All the errors related to HDMI
	7. CVBS            - All the errors related to CVBS
	8. Transcoder     - All the errors related to recording,uploading
	9. Updates         - Errors related to parse initialization and push notifications, App/Firmware updates
	10. Application   - All the common exceptions like Null pointer exception,Filenotfound exception etc.
	11. Webservices  - Errors related to web services calls
	12. IR                 - Errors related to IR
	13. USB             - Errors related to USB, External storage
	14. WiFiDisplay   - Errors related to WiFiDisplay
	15. DRM
	*/
	
	public static final String LOG_BT	= "Bluetooth";
	public static final String LOG_WIFI	= "Wifi";
	public static final String LOG_ETHERNET	= "Ethernet";
	public static final String LOG_IR	= "IR";
	public static final String LOG_PLAYBACK = "MediaPlayer";
	public static final String LOG_STORAGE = "USB";
	public static final String LOG_WEBSERVICE = "Webservices";
	public static final String LOG_A2DP = "A2dp";
	public static final String LOG_HDMI = "HDMI";
	public static final String LOG_CVBS = "CVBS";
	public static final String LOG_TRANSCODER = "Transcoder";
	public static final String LOG_UPDATES = "Updates";
	public static final String LOG_APPLICATION = "Application";
	public static final String LOG_WIFIDISPLAY = "WiFiDisplay";
	public static final String LOG_DRM = "DRM";
	public static final String LOG_DVBMIDDLE = "DVBMiddleError";
	
	private static InetAddress	 address;
	private static final int PORT = 514;
	private static DatagramPacket	packet;
	private static DatagramSocket	socket;

	
	public static void createErrorLogXml(String type, String category,String errorname,String msg){
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("<ErrorLog>");
			sb.append("<MacID>"+getMACAddress("eth0")+"</MacID>");
			sb.append("<Model.No>"+Build.MODEL+" - "+type+"</Model.No>");
			sb.append("<Version>"+Build.VERSION.RELEASE +"</Version>");
			String portVersion = "";
			try {
				if(Port.c!=null){
					portVersion = Port.c.getApplicationContext().getPackageManager().getPackageInfo(Port.c.getApplicationContext().getPackageName(), 0).versionName;
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			if (portVersion != null && !portVersion.equalsIgnoreCase("")) {
				sb.append("<PortVersion>"+portVersion +"</PortVersion>");
			}else{
				sb.append("<PortVersion>"+Constant.APP_VERSION +"</PortVersion>");
			}
			
//			String subscriberId = CacheData.getSubscriberId();
			String subscriberId ="";
			if (subscriberId != null && !subscriberId.equalsIgnoreCase("")) {
				sb.append("<SubscriberID>"+ subscriberId +"</SubscriberID>");
			}
			sb.append("<report>");
			
			sb.append("<category>"+category.toString()+"</category>");
			sb.append("<name>"+errorname.toString() +"</name>");
			sb.append("<message>"+msg.toString() +"</message>");
			sb.append("<date-time>"+CommonUtil.getCurrentDateAndTime() +"</date-time>");
			
			sb.append("</report>");
			sb.append("</ErrorLog>");
			
			if(Constant.DEBUG) Log.d(TAG,"Error xml : "+sb.toString());
			
			String data = sb.toString();
			int length = data.length();
			sendLogs(data, length);
		}catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	public static void getXmlData(){
		try {
			String result = "";
			String filePath = "/error_log.xml".trim();
			File file = new File(filePath); 
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileInputStream newStream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(newStream));
			if(newStream != null){
				String inputLine;
				while ((inputLine = br.readLine()) != null) {
					result += inputLine;
				}

				if (Constant.DEBUG)Log.d(TAG,"error_log.xml : "+ result);
				sendLogs(result, result.length());
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
	}
	
	
	private static void sendLogs(final String data, final int length){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(address == null)
					address = InetAddress.getByName(Constant.ERROR_LOG_REPORT);
				} catch (UnknownHostException e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
					address = null;
				}
				try{
					socket = new DatagramSocket();
				}catch (SocketException e1 ){
					try {
						throw new SyslogException("error creating syslog udp socket: " + e1.getMessage() );
					} catch (SyslogException e) {
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
					}
				}
	
				packet = new DatagramPacket( data.getBytes(), length, address, PORT );
	
				try{
					if(socket != null)
					socket.send(packet);
				}catch ( Exception e1 ){
					try {
						throw new SyslogException("error sending message: '" + e1.getMessage() + "'" );
					} catch (Exception e) {
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
					}
				}
			}
		}).start();
	}
	
	public static String DVBMiddleError(int errorCode){
		try{
//			if (errorCode <= DVBError.length) {
//				return DVBError[errorCode];
//			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return ""; 
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
	
//	public static class sendLogs extends AsyncTask<String, String, Boolean> {
//		String Data;
//		int length;
//		String Tag;
//		public sendLogs(String data,int len,String tag){
//			Data = data;
//			length = len;
//			Tag = tag;
//		}
//		 
//		@Override
//		protected Boolean doInBackground(String... params) {
//			try {
//				if(address == null)
//				address = InetAddress.getByName(Constant.ERROR_LOG_REPORT);
//			} catch (UnknownHostException e) {
//				e.printStackTrace();
//				address = null;
//			}
//			
//			try{
//				socket = new DatagramSocket();
//			}catch (SocketException e ){
//				try {
//					throw new SyslogException("error creating syslog udp socket: " + e.getMessage() );
//				} catch (SyslogException e1) {
//					e1.printStackTrace();
//				}
//			}
//			catch ( IOException e ){
//				try {
//					throw new SyslogException( "error creating syslog udp socket: " + e.getMessage() );
//				} catch (SyslogException e1) {
//					e1.printStackTrace();
//				}
//			}
//
//			packet = new DatagramPacket( Data.getBytes(), length, address, PORT );
//
//			try{
//				if(socket != null)
//				socket.send(packet);
//			}catch ( IOException e ){
//				try {
//					throw new SyslogException("error sending message: '" + e.getMessage() + "'" );
//				} catch (SyslogException e1) {
//					e1.printStackTrace();
//				}
//			}
//			return true;
//		}
//		
//		@Override
//		protected void onPostExecute(Boolean result) {
//			super.onPostExecute(result);
//			if(Tag.equalsIgnoreCase("applog")){
//				if(Constant.DEBUG)  Log.d(TAG, "Error Log Dispatched "+result);
//				getXmlData();
//				
//			}else{
//				File file = new File("/error_log.xml"); 
//				if (file.exists()) {
//					file.delete();
//				}
//			}
//		}
//	}
	
}
