package com.port.api.webservices;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.port.api.util.Constant;
import com.port.api.util.SyslogException;
import com.port.api.util.SystemLog;

public class Logs extends IntentService {

	private static String TAG = "Logs";
	
	private static InetAddress	 address;
	private static final int PORT = 514;
	private static DatagramPacket	packet;
	private static DatagramSocket	socket;
	
	String func;
	private String functionData;
	
	public Logs() {
		super("Logs");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		int length = 0;
		String data = "";
		
		if (extras != null) {
			if(extras.containsKey("Params")){
	    		try{
		    		functionData = extras.getString("Params");
		    		JSONObject jsonObj = new JSONObject(functionData);
		    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
		    		if(jsonObj.has("length")){
		    			length = Integer.parseInt(jsonObj.getString("length"));
		    		}if(jsonObj.has("data")){
		    			data = jsonObj.getString("data");
		    		}
	    		}catch (JSONException e) {
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}	
	    	}
			
			if(extras.containsKey("Method")){
	    		try{
	    			func = extras.getString("Method");
	    			if(func.equalsIgnoreCase("sendSystemLog")){
	    				sendSystemLog(data, length);
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
	
	
	private void sendSystemLog(final String data, final int length){
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

}
