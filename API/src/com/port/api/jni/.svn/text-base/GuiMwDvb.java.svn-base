package com.port.api.jni;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Intent;
import android.util.Log;

import com.port.Port;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class GuiMwDvb {
	private static String TAG = "GuiMwDvb";
	static {
		if(Constant.DEBUG)  Log.d(TAG  , "loadLibrary");
		System.loadLibrary("nexusmwdvbv00");
		if(Constant.DEBUG)  Log.d(TAG , "loadLibrary done");
    }
	
	public native int EnableMWDvbInit(boolean enable); //called from Port
	 
	public native int SelectAvService(int channel_no); //called from Play
	
//	public native int SendErrorMessage(int err_no, String error_msg);
//	
//	public native int SendStatusMessage(int status_no, String status_msg);
	
	public static void ErrorMessage(int err_no, String error_msg){
		if(Constant.DEBUG)  Log.d(TAG , "ErrorMessage error_msg: "+error_msg);
		try{
			if(Constant.DEBUG)  Log.d(TAG  , "StatusMessage() status_no: "+err_no);
			Intent intent = new Intent();
			intent.putExtra("msg", error_msg);
			intent.putExtra("no", err_no);
			intent.putExtra("type", "error");
			intent.setAction("DVBMSG");
			Port.c.sendBroadcast(intent);
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_DVBMIDDLE, errors.toString(), e.getMessage());
		}
	}
	
	public static void StatusMessage(int status_no, String status_msg){
			if(Constant.DEBUG)  Log.d(TAG , "status_msg: "+status_msg);
			try{
				if(Constant.DEBUG)  Log.d(TAG  , "StatusMessage() status_no: "+status_no);
				Intent intent = new Intent();
				intent.putExtra("msg", status_msg);
				intent.putExtra("no", status_no);
				intent.putExtra("type", "status");
				intent.setAction("DVBMSG");
				Port.c.sendBroadcast(intent);
			} catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_DVBMIDDLE, errors.toString(), e.getMessage());
			}
	}
	
	public native int StartAvService(int channel_no);
	
	public native int StopAvService();
}
