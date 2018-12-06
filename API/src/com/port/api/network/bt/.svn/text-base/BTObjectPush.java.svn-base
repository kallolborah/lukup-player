package com.port.api.network.bt;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.port.Channel;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class BTObjectPush extends IntentService{
	
	private String TAG = "BT OPP";
	
	public BTObjectPush() {
		super("BTObjectPush");
	}	

	@Override
	protected void onHandleIntent(Intent intent) {
		//extract from bundle extras
		Bundle extras = intent.getExtras();

        if(extras != null){
    		if (extras.containsKey("FileName")) {
    			if(Constant.DEBUG)  Log.d(TAG , "Pushing file to Player ");
    			pushFile(extras.getString("FileName"), extras.getString("UpdateType"));
    		}
        }    			
	}
	
	public void pushFile(String file_to_transfer, String type){
		try{
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(getFilesDir(),file_to_transfer).getAbsoluteFile()));
			
			PackageManager pm = getPackageManager();
			List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);
			if(appsList.size() > 0) {
				String packageName = null;
				String className = null;
				boolean found = false;
				 
				for(ResolveInfo info: appsList){
					packageName = info.activityInfo.packageName;
					if(Constant.DEBUG)  Log.d(TAG , "Finding Network package to push " + packageName);
					if( packageName.equals("com.android.bluetooth")){
						className = info.activityInfo.name;
						found = true;
						break;// found
					}
				}
				if(Constant.DEBUG)  Log.d(TAG , "Sending file to Player finally using Package " + packageName + " Class " + className);
				intent.setClassName(packageName, className);
				intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				
				try{
					if(Constant.DEBUG)  Log.d(TAG , "Send Message to Player.....");
					String dockID = Build.ID; //is this correct ?
					Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
					
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					
	        		returner.set("PLAYER", "BT", "com.player.NotificationsService");
	        		data.put("message", "Software update. Please restart your Player."); //put this in Strings.xml
    				sendResponse.put("params", data);
    				returner.add(type, sendResponse,"startService");
    				returner.send();
    	        } catch (JSONException e) {
    	        	e.printStackTrace();
    				StringWriter errors = new StringWriter();
    				e.printStackTrace(new PrintWriter(errors));
    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
				}     
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
	    }
	}
	
}
