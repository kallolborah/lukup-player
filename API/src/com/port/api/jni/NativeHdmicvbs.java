package com.port.api.jni;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Intent;
import android.util.Log;

import com.port.Port;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class NativeHdmicvbs {
	private static String TAG = "NativeHdmicvbs";
	static {
		if(Constant.DEBUG)  Log.d(TAG  , "loadLibrary");
		System.loadLibrary("jni_hdmicvbs");
		if(Constant.DEBUG)  Log.d(TAG , "loadLibrary done");
		
    }

	/* set graphics setting according to video offset settings */
	public native int setScreenOffset(int xoff, int yoff, int width, int height); 
	public native int getScreenOffset(int xoff, int yoff, int width, int height);     
	
	public native int getHdmiInStatus();    
	public native int getHdmiOutStatus();   
	public native int CVBSSwitch(int pin,int out,int val);   
	
	// Recording API
	public native int stopTranscode();
//	public native int startTranscode(String subscriberID, String Date, String Time, String serviceId, String programId, String eventname);
	
//	public native int startTranscode();
	public native int startTranscode(String objectid, String duration);
	
	public native void wifidisplayuri(String uri);
	
	public static void callbackfail(){
		try{
			if(Constant.DEBUG)  Log.d(TAG  , "callbackfail() Recording finish.");
			Intent intent = new Intent(Port.c, Class.forName("com.port.apps.epg.Recordings"));
			intent.putExtra("transcode", "failure");
			intent.setAction("com.port.apps.epg.Recordings");
			Port.c.sendBroadcast(intent);
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
		}
	}	
	
	
	public static void upload(String location,String objectid, boolean lastchunk){
		if(Constant.DEBUG)  Log.d(TAG  , "upload() Callback location: "+location+", objectid: "+objectid+", lastchunk: "+lastchunk);
//		MultiPartUpload mu = MultiPartUpload.getInstance(objectid);
//		mu.upload(objectid,location,lastchunk);
		
		try{
			if(Constant.DEBUG)  Log.d(TAG  , "upload() Record");
			Intent intent = new Intent(Port.c, Class.forName("com.port.apps.epg.Recordings"));
			intent.putExtra("objectid", objectid);
			intent.putExtra("location", location);
			intent.putExtra("lastchunk", lastchunk);
			intent.setAction("com.port.apps.epg.Recordings");
			Port.c.sendBroadcast(intent);
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
		}
	}
}
