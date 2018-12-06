package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.port.Channel;
import com.port.Port;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.util.CacheData;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.apps.epg.util.CommonUtil;

public class Reminders extends BroadcastReceiver{
	
	//Receiver
	String pnetwork;
	String producer;
	Channel returner;
	String userID;
	String dockID;
	
	private String TAG = "Reminders";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if(extras.containsKey("ProducerNetwork")){
	    		pnetwork = extras.getString("ProducerNetwork"); //to be used to return back response
	    	}
	    	if(extras.containsKey("Producer")){
	    		producer = extras.getString("Producer");
	    	}
	    	if(extras.containsKey("userId")){
	    		userID = extras.getString("userId");
	    	}
	    	if(extras.containsKey("macid")){
	    		dockID = extras.getString("macid");
	    	}
	    	
		    returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
	    	returner.set(producer, pnetwork, "com.player.NotificationsService"); //setting consumer = producer, network  	
	    	
	    	if(extras.containsKey("eventId")){
	    		sendReminderDetail(extras.getInt("eventId"));
	    	}	    	
	    	
	    }
	}

	private void sendReminderDetail(int eventID){
		if(Constant.DEBUG)  Log.d(TAG, "sendReminderDetail() eventID "+eventID+", userID "+userID);
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();

		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		try {
			ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventID);
			if(selectedEventInfo != null){
				data.put("id", selectedEventInfo.getEventId());
				data.put("name", selectedEventInfo.getEventName());
				data.put("releasedate", selectedEventInfo.getDateAdded());
				data.put("actors", selectedEventInfo.getActors());
				data.put("rating", selectedEventInfo.getRating());
				data.put("genre", selectedEventInfo.getGenre());
				data.put("image", selectedEventInfo.getImage());
				data.put("description", selectedEventInfo.getDescription());
				data.put("director", selectedEventInfo.getDirector());
				data.put("production", selectedEventInfo.getProductionHouse());
				data.put("musicdirector", selectedEventInfo.getMusicDirector());
				data.put("price", selectedEventInfo.getPrice());
				sendResponse.put("params", data);
				returner.add("com.port.apps.epg.Reminders.sendReminderDetail", sendResponse,"startService");
				returner.send();
				if(Constant.DEBUG)  Log.d(TAG, "Fired reminder for " + selectedEventInfo.getEventName());
				statusGateway.deleteStatusInfo(Integer.parseInt(userID),eventID, 3);
				
			}
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}	
	
}
