package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;

import com.port.Channel;
import com.port.Port;
import com.port.api.apps.webservices.CloudStorage;
import com.port.api.db.service.BouquetGateway;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ChannelInfo;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.service.StatusInfo;
import com.port.api.db.util.CacheData;
import com.port.api.jni.NativeHdmicvbs;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.apps.epg.util.CommonUtil;

public class Plan extends IntentService {
	
	private String TAG = "Plan";
	Activity activity;
	String method = "com.port.apps.epg.Plan.";
	private Handler handler;
	
	protected static AlarmManager alarms;
	protected static PendingIntent alarmIntent;
	
	//Receiver
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	Channel returner;
	String caller;
	String dockID;
	
	public Plan() {
		super("Plan");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.handler = new Handler();
		
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		int id = 0;
		String userid = "";
		String objectid = "";
		String type = "";
		if (extras != null) {
			
			if(extras.containsKey("ProducerNetwork")){
	    		pnetwork = extras.getString("ProducerNetwork"); //to be used to return back response
	    	}
	    	if(extras.containsKey("ConsumerNetwork")){
	    		cnetwork = extras.getString("ConsumerNetwork"); //to be used to send request onward 
	    	}
	    	if(extras.containsKey("Producer")){
	    		producer = extras.getString("Producer");
	    	}
	    	if(extras.containsKey("Caller")){
	    		caller = extras.getString("Caller");
	    	}
	    	if(extras.containsKey("macid")){
	    		dockID = extras.getString("macid");
	    	}
	    	if(returner==null){ //to ensure that there is only one returner instance for one activity
		    	returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
	    		returner.set(producer, pnetwork, caller); //setting consumer = producer, network
	    	}
			
	    	if(extras.containsKey("Params")){
	    		try{
		    		functionData = extras.getString("Params");
		    		JSONObject jsonObj = new JSONObject(functionData);
		    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
		    		if(jsonObj.has("id")){
		    			id = Integer.parseInt(jsonObj.getString("id"));
		    		}if(jsonObj.has("userid")){
		    			userid = jsonObj.getString("userid");
		    		}if(jsonObj.has("objectid")){
		    			objectid = jsonObj.getString("objectid");
		    		}if(jsonObj.has("type")){
		    			type = jsonObj.getString("type");
		    		}
	    		} catch (Exception e) {
	    			e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}
	    	}	
	    	if(extras.containsKey("Method")){
	    		try{
	    			func = extras.getString("Method");
	    			if(func.equalsIgnoreCase("getReminder")){
	    				getReminder(userid);
	    			}else if(func.equalsIgnoreCase("setReminder")){
	    				setReminder(userid, id);
	    			}else if(func.equalsIgnoreCase("cancelReminder")){
	    				cancelReminder(userid, id);
	    			}if(func.equalsIgnoreCase("getRecord")){
	    				getRecord();
	    			}else if(func.equalsIgnoreCase("setRecord")){
	    				setRecord(id);
	    			}else if(func.equalsIgnoreCase("cancelRecord")){
	    				cancelRecord(id);
	    			}else if(func.equalsIgnoreCase("stopRecord")){
//	    				stopRecord(id);
	    			}else if (func.equalsIgnoreCase("deleteFavourite")){
	    				deleteFavourite(userid, id, type);
	    			}else if (func.equalsIgnoreCase("deleteRecording")){
	    				deleteRecording(objectid, id);
	    			}else if (func.equalsIgnoreCase("showRecording")){
	    				showRecording();
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
	
	    
    //get list of reminders, consumer : Player
	private void getReminder(String userID){
		int userId=CacheData.getUserId();
		if(Constant.DEBUG)  Log.d(TAG, "User id from CacheData " + userId);

		if(userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}
		if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
		userID = String.valueOf(userId);
		
		try{
			JSONObject sendResponse = new JSONObject();
			JSONObject data = new JSONObject();
			JSONArray jsonArray = getJsonRecordAndReminderList(3, userID);
			data.put("reminderList", jsonArray);
			if((jsonArray == null || jsonArray.length() < 1)){
				data.put("result", "failure");
				data.put("msg", this.getResources().getString(R.string.NO_REMINDER));
			} else {
				data.put("result", "success");
			}
			sendResponse.put("params", data);
			returner.add(method+"getReminder", sendResponse,"messageActivity");
			returner.send();
			
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	//set reminder and update list of reminders, consumer : Player
	private void setReminder(String userID, int eventID){
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		
		int userId=CacheData.getUserId();
		if(Constant.DEBUG)  Log.d(TAG, "User id from CacheData " + userId);

		if(userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}
		if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
		userID = String.valueOf(userId);
		
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		try {
			ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventID);
			String eventName = selectedEventInfo.getEventName();
			String eventStartTime = selectedEventInfo.getStartTime();
			StatusInfo reminderInfo = statusGateway.getStatusInfoByServiceId(userID, eventID, 3,"event");
			if(reminderInfo == null) {
//				2014-10-06 16:00:00.000			
				int hours = Integer.parseInt(getHoursAndMin(eventStartTime, "hours"));
				int minute = Integer.parseInt(getHoursAndMin(eventStartTime, "min"));
				
				if(Constant.DEBUG)  Log.d(TAG, "Setting reminder for " + eventName + " Hours: "+hours+", Minute: "+minute);
				alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
				Intent intent = new Intent(this, Class.forName("com.port.apps.epg.Reminders"));
				intent.putExtra("userId", userID);
				intent.putExtra("eventId", eventID);
				intent.putExtra("ProducerNetwork", pnetwork);
				intent.putExtra("Producer", producer);
				alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
				
				Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta")); 
				Time t = new Time();
				t.set(0, minute, hours, c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH),c.get(Calendar.YEAR));
				
				if(Constant.DEBUG)  Log.d(TAG, "Current System.currentTimeMillis : " + System.currentTimeMillis() + " Current time from commonUtil " + com.port.api.util.CommonUtil.getDateTime() + " Alarm set for " + t.toMillis(true));
				alarms.set(AlarmManager.RTC_WAKEUP, t.toMillis(true), alarmIntent);				
				
				if(Constant.DEBUG)  Log.d(TAG, "You set Reminder on "+eventName);
				statusGateway.insertStatusInfo(Integer.valueOf(userID),0,eventID, 0, 3, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
				JSONArray jsonArray = getJsonRecordAndReminderList(3, userID);
				data.put("reminderList", jsonArray);
				data.put("result", "success");
				data.put("msg", this.getResources().getString(R.string.SET_REMINDER)+eventName);
				sendResponse.put("params", data);
				returner.add(method+"setReminder", sendResponse,"messageActivity");
				returner.send();
				
			} else {
				if(Constant.DEBUG)  Log.d(TAG, "Reminder already set on "+eventName+" event.");
				JSONArray jsonArray = getJsonRecordAndReminderList(3, userID);
				data.put("reminderList", jsonArray);
				data.put("result", "failure");
				sendResponse.put("params", data);
				returner.add(method+"setReminder", sendResponse,"messageActivity");
				returner.send();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
			
	}
	
	//cancel reminder and update list of reminders, consumer : Player 
	private void cancelReminder(String userID, int eventID){
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		
		int userId=CacheData.getUserId();
		if(Constant.DEBUG)  Log.d(TAG, "User id from CacheData " + userId);

		if(userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}
		if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
		userID = String.valueOf(userId);
		
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		try {
			JSONArray jsonArray = new JSONArray();
			ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventID);
			String eventName = selectedEventInfo.getEventName();
			StatusInfo reminderInfo = statusGateway.getStatusInfoByServiceId(userID, eventID, 3,"event");
			if(reminderInfo != null) {
//				if(alarms!=null){
//					  alarms.cancel(alarmIntent);
//				}
				
				if(Constant.DEBUG)  Log.d(TAG, "Reminder cancel on "+eventName + " user id " + userID + " and event ID " + eventID);
				statusGateway.deleteStatusInfo(Integer.valueOf(userID),eventID, 3);
				jsonArray = getJsonRecordAndReminderList(3, userID);
				data.put("reminderList", jsonArray);
				data.put("result", "success");
				data.put("msg", this.getResources().getString(R.string.CANCEL_REMINDER)+eventName);
				sendResponse.put("params", data);
				returner.add(method+"cancelReminder", sendResponse,"messageActivity");
				returner.send();
				
			} else {
				if(Constant.DEBUG)  Log.d(TAG, "No Reminder on "+eventName+" event.");
				jsonArray = getJsonRecordAndReminderList(3, userID);
				data.put("reminderList", jsonArray);
				data.put("result", "failure");
				sendResponse.put("params", data);
				returner.add(method+"cancelReminder", sendResponse,"messageActivity");
				returner.send();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	//get list of recordings, consumer : Player
	private void getRecord(){
		try{
			JSONObject sendResponse = new JSONObject();
			JSONObject data = new JSONObject();
			
			int userId=CacheData.getUserId();
			if(Constant.DEBUG)  Log.d(TAG, "User id from CacheData " + userId);

			if(userId==0){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					userId = Integer.valueOf(info.getProfile());
					CacheData.setUserId(userId);
				}
			}
			if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
			String userID = String.valueOf(userId);

			JSONArray jsonArray = getJsonRecordAndReminderList(4, userID);
			data.put("recordList", jsonArray);
			if((jsonArray == null || jsonArray.length() < 1)){
				data.put("result", "failure");
				data.put("msg", this.getResources().getString(R.string.NO_RECORDER));
			} else {
				data.put("result", "success");
			}
			sendResponse.put("params", data);
			returner.add(method+"getRecord", sendResponse,"messageActivity");
			returner.send();
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	//set recording and update list of recordings, consumer : Player
	private void setRecord(int eventID){
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		
		int userId=CacheData.getUserId();
		String subscriberid = CacheData.getSubscriberId();	
		if(subscriberid.equalsIgnoreCase("") || userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				subscriberid = info.getSubscriber();
				CacheData.setSubscriberId(subscriberid);
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}		
		if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
		String userID = String.valueOf(userId);
		
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;

		try {
			ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventID);
			String eventName = selectedEventInfo.getEventName().replaceAll("\\s", "");
			String eventTime = selectedEventInfo.getStartTime();
			String eventdur = selectedEventInfo.getDuration();
			String serviceId = selectedEventInfo.getChannelServiceId()+"";
			
			StatusInfo recordInfo = statusGateway.getStatusInfoByServiceId(userID, eventID, 4,"event");
			
			if(recordInfo == null) { //if event has not been scheduled already
				StringTokenizer tokens = new StringTokenizer(eventdur, ":");
				int h = 0;
				int min = 0;
				int sec = 0;
				if (tokens.hasMoreTokens()) {
					h = Integer.parseInt(tokens.nextToken());
				}
				if(tokens.hasMoreTokens()){
					min = Integer.parseInt(tokens.nextToken());
				}
				if(Constant.DEBUG)  Log.d(TAG, "Duration Hours: "+h+", Minute: "+min);
				
				tokens = new StringTokenizer(eventTime, ":");
				int sh = 0;
				int smin = 0;
				if (tokens.hasMoreTokens()) {
					sh = Integer.parseInt(tokens.nextToken());
				}
				if(tokens.hasMoreTokens()){
					smin = Integer.parseInt(tokens.nextToken());
				}
				if(Constant.DEBUG)  Log.d(TAG, "Start Hours: "+h+", Minute: "+min);
				int eh = 0;
				int emin = 0;
				if((sh+h)>24){
					eh = (sh+h)-24;
				}else{
					eh = sh+h;
				}
				if((smin+min)>60){
					emin = (smin+min)-60;
				}else{
					emin = smin+min;
				}
				String endTime = eh+":"+emin+":00";
				
				if(Constant.DEBUG)  Log.d(TAG, "end Time "+ endTime + " start Time " + eventTime);
//				String request = Constant.SCHEDULE_RECORD + "serviceid=" + serviceId + "&eventname=" + eventName + "&starttime=" + eventTime + "&endtime=" + endTime + "&subscriberid=" + subscriberid + "&userid=" + userID; 
				if(Constant.DEBUG)  Log.d(TAG, "You set Record on "+eventName);
				
				JSONObject jsonObject = CloudStorage.ScheduleRecording(String.valueOf(eventID), serviceId, eventName, eventTime, endTime, subscriberid, userID);
				
				if(jsonObject != null){
					try {
						String result = jsonObject.getJSONObject("data").getString("result");
						if(result.equalsIgnoreCase("success")){
							if(Constant.DEBUG)  Log.d(TAG, "Scheduled recording successfully");
							statusGateway.insertStatusInfo(Integer.parseInt(userID),0,eventID, 0, 4, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
							JSONArray jsonArray = getJsonRecordAndReminderList(4, userID);
							data.put("recordList", jsonArray);
							data.put("id", eventID);
							data.put("result", "success");
							data.put("msg", this.getResources().getString(R.string.SET_RECORD)+" "+eventName);
							sendResponse.put("params", data);
							returner.add(method+"setRecord", sendResponse,"messageActivity");
							returner.send();
						}else{
							if(Constant.DEBUG)  Log.d(TAG, "Scheduled recording failed");
							data.put("result", "failure");
							data.put("msg", getApplicationContext().getResources().getString(R.string.RECORDING_FAILURE));
							sendResponse.put("params", data);
							returner.add(method+"setRecord", sendResponse,"messageActivity");
							returner.send();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}else{
					//send failure msg
					data.put("result", "failure");
					data.put("msg", getApplicationContext().getResources().getString(R.string.RECORDING_FAILURE));
					sendResponse.put("params", data);
					returner.add(method+"setRecord", sendResponse,"messageActivity");
					returner.send();
				}

			} else {
				if(Constant.DEBUG)  Log.d(TAG, "Recording already set on "+eventName+" event.");
				JSONArray jsonArray = getJsonRecordAndReminderList(4, userID);
				data.put("recordList", jsonArray);
				data.put("result", "failure");
				sendResponse.put("params", data);
				returner.add(method+"setRecord", sendResponse,"messageActivity");
				returner.send();
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	//cancel recording and update list of recordingss, consumer : Player 
	private void cancelRecord(int eventID){
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		
		int userId=CacheData.getUserId();
		String subscriberid = CacheData.getSubscriberId();	
		if(subscriberid.equalsIgnoreCase("") || userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				subscriberid = info.getSubscriber();
				CacheData.setSubscriberId(subscriberid);
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}		
		if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
		String userID = String.valueOf(userId);
		
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		try {
			JSONArray jsonArray = new JSONArray();
			ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventID);
			String eventName = selectedEventInfo.getEventName().replaceAll("\\s", "");
			String eventTime = selectedEventInfo.getStartTime();
			String eventdur = selectedEventInfo.getDuration();
			String serviceId = selectedEventInfo.getChannelServiceId()+"";
			
			StatusInfo recordInfo = statusGateway.getStatusInfoByServiceId(userID, eventID, 4,"event");
			
			if(recordInfo != null) {
				StringTokenizer tokens = new StringTokenizer(eventdur, ":");
				int h = 0;
				int min = 0;
				int sec = 0;
				if (tokens.hasMoreTokens()) {
					h = Integer.parseInt(tokens.nextToken());
				}
				if(tokens.hasMoreTokens()){
					min = Integer.parseInt(tokens.nextToken());
				}
				if(Constant.DEBUG)  Log.d(TAG, "Duration Hours: "+h+", Minute: "+min);
				
				tokens = new StringTokenizer(eventTime, ":");
				int sh = 0;
				int smin = 0;
				if (tokens.hasMoreTokens()) {
					sh = Integer.parseInt(tokens.nextToken());
				}
				if(tokens.hasMoreTokens()){
					smin = Integer.parseInt(tokens.nextToken());
				}
				if(Constant.DEBUG)  Log.d(TAG, "Start Hours: "+h+", Minute: "+min);
				int eh = 0;
				int emin = 0;
				if((sh+h)>24){
					eh = (sh+h)-24;
				}else{
					eh = sh+h;
				}
				if((smin+min)>60){
					emin = (smin+min)-60;
				}else{
					emin = smin+min;
				}
				String endTime = eh+":"+emin+":00";
				
				//send cancellation request to server
//				String request = Constant.SCHEDULE_RECORD + "method=delete&serviceid=" + serviceId + "&eventname=" + eventName + "&starttime=" + eventTime + "&endtime=" + endTime + "&subscriberid=" + subscriberid; 
								
				JSONObject jsonObject = CloudStorage.cancelRecording(serviceId, eventName, eventTime, endTime, subscriberid, String.valueOf(eventID));
				if(jsonObject != null){
					try {
						String result = jsonObject.getString("result");
						if(result.equalsIgnoreCase("success")){
							if(Constant.DEBUG)  Log.d(TAG, "You cancelled Record on "+eventName);
							statusGateway.deleteStatusInfo(Integer.parseInt(userID),eventID, 4);
							jsonArray = getJsonRecordAndReminderList(4, userID);
							data.put("recordList", jsonArray);
							data.put("id", eventID);
							data.put("result", "success");
							data.put("msg", this.getResources().getString(R.string.CANCEL_RECORD) +eventName);
							sendResponse.put("params", data);
							returner.add(method+"stopRecord", sendResponse,"messageActivity");
							returner.send();
						}else{
							if(Constant.DEBUG)  Log.d(TAG, "Failed to cancel recording on "+eventName+" event.");
							jsonArray = getJsonRecordAndReminderList(4, userID);
							data.put("recordList", jsonArray);
							data.put("result", "failure");
							sendResponse.put("params", data);
							returner.add(method+"stopRecord", sendResponse,"messageActivity");
							returner.send();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					if(Constant.DEBUG)  Log.d(TAG, "No Recording on "+eventName+" event.");
					jsonArray = getJsonRecordAndReminderList(4, userID);
					data.put("recordList", jsonArray);
					data.put("result", "failure");
					sendResponse.put("params", data);
					returner.add(method+"stopRecord", sendResponse,"messageActivity");
					returner.send();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
//	private void stopRecord(int eventId){
//		try{
//			JSONObject sendResponse = new JSONObject();
//			JSONObject data = new JSONObject();
//			
//			int userId=CacheData.getUserId();
//			String subscriberid = CacheData.getSubscriberId();	
//			if(subscriberid.equalsIgnoreCase("") || userId==0){
//				CacheGateway cache  = new CacheGateway(Port.c);
//				CacheInfo info = cache.getCacheInfo(1000);
//				if (info != null) {
//					subscriberid = info.getSubscriber();
//					CacheData.setSubscriberId(subscriberid);
//					userId = Integer.valueOf(info.getProfile());
//					CacheData.setUserId(userId);
//				}
//			}		
//			if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
//			String userID = String.valueOf(userId);
//			
//			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
//			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
//			ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventId);
//			String eventName = selectedEventInfo.getEventName().replaceAll("\\s", "");
//			String eventTime = selectedEventInfo.getStartTime();
//			String eventdur = selectedEventInfo.getDuration();
//			String serviceId = selectedEventInfo.getChannelServiceId()+"";
//			
//			StatusInfo recordInfo = statusGateway.getStatusInfoByServiceId(userID, eventId, 4,"event");
//			if(recordInfo!=null){
//				StringTokenizer tokens = new StringTokenizer(eventdur, ":");
//				int h = 0;
//				int min = 0;
//				int sec = 0;
//				if (tokens.hasMoreTokens()) {
//					h = Integer.parseInt(tokens.nextToken());
//				}
//				if(tokens.hasMoreTokens()){
//					min = Integer.parseInt(tokens.nextToken());
//				}
//				if(Constant.DEBUG)  Log.d(TAG, "Duration Hours: "+h+", Minute: "+min);
//				
//				tokens = new StringTokenizer(eventTime, ":");
//				int sh = 0;
//				int smin = 0;
//				if (tokens.hasMoreTokens()) {
//					sh = Integer.parseInt(tokens.nextToken());
//				}
//				if(tokens.hasMoreTokens()){
//					smin = Integer.parseInt(tokens.nextToken());
//				}
//				if(Constant.DEBUG)  Log.d(TAG, "Start Hours: "+h+", Minute: "+min);
//				int eh = 0;
//				int emin = 0;
//				if((sh+h)>24){
//					eh = (sh+h)-24;
//				}else{
//					eh = sh+h;
//				}
//				if((smin+min)>60){
//					emin = (smin+min)-60;
//				}else{
//					emin = smin+min;
//				}
//				String endTime = eh+":"+emin+":00";
//				
//				//send cancellation request to server
////				String request = Constant.SCHEDULE_RECORD + "method=delete&serviceid=" + serviceId + "&eventname=" + eventName + "&starttime=" + eventTime + "&endtime=" + endTime + "&subscriberid=" + subscriberid; 
//				if(Constant.DEBUG)  Log.d(TAG, "You stopped recording on "+eventName);
//				
//				JSONObject jsonObject = CloudStorage.ScheduleRecording(serviceId, eventName, eventTime, endTime, subscriberid, userID);
//				if(jsonObject != null){
//					try {
//						JSONArray jsonArray = new JSONArray();
//						String result = jsonObject.getJSONObject("data").getString("result");
//						if(result.equalsIgnoreCase("success")){
//							//send cancellation request to server
//							statusGateway.deleteStatusInfo(Integer.parseInt(userID),eventId, 4);
//							jsonArray = getJsonRecordAndReminderList(4, userID);
//							data.put("recordList", jsonArray);
//							data.put("result", "success");
//							data.put("id", eventId);
//							data.put("msg", "data is deleted successfully");
//							sendResponse.put("params", data);
//							returner.add(method+"stopRecord", sendResponse,"messageActivity");
//							returner.send();
//						}else{
//							data.put("result", "failure");
//							data.put("id", eventId);
//							data.put("msg", "Record is deleted.");
//							sendResponse.put("params", data);
//							returner.add(method+"stopRecord", sendResponse,"messageActivity");
//							returner.send();
//						}
//					} catch (Exception e) {
//		    			e.printStackTrace();
//						StringWriter errors = new StringWriter();
//						e.printStackTrace(new PrintWriter(errors));
//						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//		    		}
//				}else{
//					data.put("result", "failure");
//					data.put("id", eventId);
//					data.put("msg", "Record is deleted.");
//					sendResponse.put("params", data);
//					returner.add(method+"stopRecord", sendResponse,"messageActivity");
//					returner.send();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//		}
//	}
	
	private void deleteRecording(String objId, int eventId){
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();

		int userId=CacheData.getUserId();
		String subscriberid = CacheData.getSubscriberId();	
		if(subscriberid.equalsIgnoreCase("") || userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				subscriberid = info.getSubscriber();
				CacheData.setSubscriberId(subscriberid);
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}		
		if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
		String userID = String.valueOf(userId);
		
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
//		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
//		ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventId);
//		String eventName = selectedEventInfo.getEventName().replaceAll("\\s", "");
//		String eventTime = selectedEventInfo.getStartTime();
//		String eventdur = selectedEventInfo.getDuration();
//		String serviceId = selectedEventInfo.getChannelServiceId()+"";
//		
//		StatusInfo recordInfo = statusGateway.getStatusInfoByServiceId(userID, eventId, 4,"event");
//		
		try{
//			if(recordInfo!=null){
//				StringTokenizer tokens = new StringTokenizer(eventdur, ":");
//				int h = 0;
//				int min = 0;
//				int sec = 0;
//				if (tokens.hasMoreTokens()) {
//					h = Integer.parseInt(tokens.nextToken());
//				}
//				if(tokens.hasMoreTokens()){
//					min = Integer.parseInt(tokens.nextToken());
//				}
//				if(Constant.DEBUG)  Log.d(TAG, "Duration Hours: "+h+", Minute: "+min);
//				
//				tokens = new StringTokenizer(eventTime, ":");
//				int sh = 0;
//				int smin = 0;
//				if (tokens.hasMoreTokens()) {
//					sh = Integer.parseInt(tokens.nextToken());
//				}
//				if(tokens.hasMoreTokens()){
//					smin = Integer.parseInt(tokens.nextToken());
//				}
//				if(Constant.DEBUG)  Log.d(TAG, "Start Hours: "+h+", Minute: "+min);
//				int eh = 0;
//				int emin = 0;
//				if((sh+h)>24){
//					eh = (sh+h)-24;
//				}else{
//					eh = sh+h;
//				}
//				if((smin+min)>60){
//					emin = (smin+min)-60;
//				}else{
//					emin = smin+min;
//				}
//				String endTime = eh+":"+emin+":00";
				
				//send cancellation request to server
//				String request = Constant.SCHEDULE_RECORD + "method=remove&serviceid=" + serviceId + "&eventname=" + eventName + "&starttime=" + eventTime + "&endtime=" + endTime + "&subscriberid=" + subscriberid; 
				if(Constant.DEBUG)  Log.d(TAG, "You deleted Record on "+eventId);
				
				JSONObject jsonObject = CloudStorage.DeleteRecording(objId, String.valueOf(eventId));
				if(jsonObject != null){
					String result = jsonObject.getJSONObject("data").getString("result");
					if(result.equalsIgnoreCase("success")){
						if(Constant.DEBUG)  Log.d(TAG, "Deleting recording successfully");
						data.put("result", "success");
						statusGateway.deleteStatusInfo(Integer.parseInt(userID),eventId, 4);
					}
					else{
						if(Constant.DEBUG)  Log.d(TAG, "Deleting recording failed");
						data.put("result", "failure");
					}
					data.put("msg", jsonObject.getJSONObject("data").getString("message"));
					sendResponse.put("params", data);
					returner.add(method+"deleteRecording", sendResponse,"messageActivity");
					returner.send();
				}else{
					data.put("result", "failure");
					data.put("msg", "Failed to delete recording");
					sendResponse.put("params", data);
					returner.add(method+"deleteRecording", sendResponse,"messageActivity");
					returner.send();
				}
//			}else{
//				data.put("result", "failure");
//				data.put("msg", "Failed to delete recording");
//				sendResponse.put("params", data);
//				returner.add(method+"deleteRecording", sendResponse,"messageActivity");
//				returner.send();
//			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
		}
	}
	
	
	private void showRecording(){
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject child = null;
		JSONObject jsonObject = null;
		
		int userId=CacheData.getUserId();
		if(Constant.DEBUG)  Log.d(TAG, "User id from CacheData " + userId);

		if(userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}
		if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);

		try{
			jsonObject = CloudStorage.GetRecords();
			
			if(jsonObject.getString("result").equalsIgnoreCase("success")){
				String Userid="";
				String Size="";
				String Objectid="";
				String name="";
				String eventId="";
				JSONArray Element = jsonObject.getJSONObject("data").getJSONArray("List");
				Log.i("Element=========>", Element.length()+ ""+Element.toString());
				for(int i = 0; i < Element.length(); i++){
					child = new JSONObject();
					JSONObject rec = Element.getJSONObject(i);
					if(rec.has("userid")){
						Userid = rec.getString("userid");
					}
					if(rec.has("objectid")){
						Objectid = rec.getString("objectid");
					}
					if(rec.has("size")){
						Size = rec.getString("size");
					}
					if(rec.has("name")){
						name = rec.getString("name");
					}
					if(rec.has("eventid")){
						eventId = rec.getString("eventid");
					}
					if(String.valueOf(userId).equalsIgnoreCase(Userid)){
						child.put("path", Objectid);
						child.put("size", Size);
						child.put("name", name);
						child.put("eventid", eventId);
					}
					jsonArray.put(child);
				}
				data.put("result", "success");
				data.put("recordList", jsonArray);
				resp.put("params",data);
				returner.add(method+"showRecording", resp,"messageActivity");
				returner.send();
			}else{
				data.put("result", "failure");
				resp.put("params",data);
				returner.add(method+"showRecording", resp,"messageActivity");
				returner.send();
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
		}
		
	}
	
	private void deleteFavourite(String userID, int eventID, String type){
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		
		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		try {
			JSONArray jsonArray = new JSONArray();
			StatusInfo info = null;
			String Name = "";
			if(type.equalsIgnoreCase("true")){
				ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventID);
				Name = selectedEventInfo.getEventName();
				info = statusGateway.getStatusInfoByServiceId(userID, eventID, 1,"event");
			}else if(type.equalsIgnoreCase("false")){
				ChannelInfo selectedInfo = channelGateway.getServiceInfoByServiceId(eventID);
				Name = selectedInfo.getChannelName();
				info = statusGateway.getStatusInfoByServiceId(userID, eventID, 1,"service");
			}
			if(info != null){
				if(Constant.DEBUG)  Log.d(TAG, "Removed "+Name+ " from Favourite List");
				if(type.equalsIgnoreCase("true")){
					statusGateway.deleteStatusInfo(Integer.parseInt(userID),eventID, 1);
				}else if(type.equalsIgnoreCase("false")){
					statusGateway.deleteServiceStatusInfo(Integer.parseInt(userID),eventID, 1);
				}
				jsonArray = getFavouriteServiceList(1, userID,"event");
				data.put("eventList", jsonArray);

				jsonArray = getFavouriteServiceList(1, userID,"service");
				data.put("serviceList", jsonArray);

				data.put("result", "success");
				data.put("msg", this.getResources().getString(R.string.REMOVE_FAV));
				sendResponse.put("params", data);
				returner.add(method+"deleteFavourite", sendResponse,"messageActivity");
				returner.send();

			}else{
				jsonArray = getFavouriteServiceList(1, userID,"event");
				data.put("eventList", jsonArray);
				
				jsonArray = getFavouriteServiceList(1, userID,"service");
				data.put("serviceList", jsonArray);

				data.put("result", "failure");
				sendResponse.put("params", data);
				returner.add(method+"deleteFavourite", sendResponse,"messageActivity");
				returner.send();
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	/*******************************************************************/
	
	private JSONArray getFavouriteServiceList(int value, String userID, String type) throws JSONException {
		List<ChannelInfo> favList = new ArrayList<ChannelInfo>();
		List<ProgramInfo> favEventList = new ArrayList<ProgramInfo>();

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;

		favList = getFavServiceList(value, userID,type);
		favEventList = getFavEventList(value, userID,type);

		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;

		if(type.equalsIgnoreCase("service")){
			if(favList != null && favList.size() > 0){
				for(ChannelInfo chlInfo : favList){
					if(chlInfo != null){
						jsonObject = new JSONObject();
						jsonObject.put("id", chlInfo.getServiceId());
						jsonObject.put("name", chlInfo.getChannelName());
						jsonObject.put("servicetype", chlInfo.getType());
						jsonObject.put("category", chlInfo.getServiceCategory());
						jsonObject.put("event", "false");
						boolean subscribe = false;

						String pricingmodel = chlInfo.getPriceModel();
						jsonObject.put("pricingmodel", pricingmodel);
						jsonObject.put("price", chlInfo.getPrice()+"");

						if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPC")){
							StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(chlInfo.getServiceId(), 9,"service");
							if(info != null) {
								if(info.getStatus() == 9) {
									subscribe = true;
								} else {
									subscribe = false;
								}
							} else {
								subscribe = false;
							}
						}

						jsonObject.put("subscribe", subscribe);
						boolean lock = false;
						StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userID+"", chlInfo.getServiceId(), 2,"service");
						if(lockInfo == null){
							lock = false;
						} else {
							if(lockInfo.getStatus() == 2){
								lock = true;
							} else {
								lock = false;
							}
						}
						jsonObject.put("lock", lock);
						jsonArray.put(jsonObject);
					}
				}
			} else {
				if(Constant.DEBUG)  Log.d(TAG , "ServiceInfo is null");
			}

		}else if(type.equalsIgnoreCase("event")){
			if(favEventList != null && favEventList.size() > 0){
				for(ProgramInfo proInfo : favEventList){
					if(proInfo != null){
						jsonObject = new JSONObject();
						int serviceId = proInfo.getChannelServiceId();
						ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(serviceId);
						jsonObject.put("serviceid", serviceId+"");
						jsonObject.put("category", serviceInfo.getServiceCategory());
						jsonObject.put("servicetype", serviceInfo.getType());
						jsonObject.put("event", "true");
						boolean subscribe = false;

						int eventId = proInfo.getEventId();
						jsonObject.put("id", eventId+"");
						jsonObject.put("name", proInfo.getEventName());
						String pricingmodel = proInfo.getPriceModel();
						jsonObject.put("pricingmodel", pricingmodel);
						jsonObject.put("price", proInfo.getPrice()+"");
						if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPV")){
							StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(proInfo.getProgramId(), 9,"event");
							if(info != null) {
								if(info.getStatus() == 9) {
									subscribe = true;
								} else {
									subscribe = false;
								}
							} else {
								subscribe = false;
							}
						}
						jsonObject.put("subscribe", subscribe);
						boolean lock = false;
						StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userID+"", eventId, 2,"event");
						if(lockInfo == null){
							lock = false;
						} else {
							if(lockInfo.getStatus() == 2){
								lock = true;
							} else {
								lock = false;
							}
						}
						jsonObject.put("lock", lock);
						jsonArray.put(jsonObject);
					}
				}
			} else {
				if(Constant.DEBUG)  Log.d(TAG , "EventInfo is null");
			}
		}
		return jsonArray;
	}



	public List<ChannelInfo> getFavServiceList(int value, String userID, String type) {

		List<ChannelInfo> list = new ArrayList<ChannelInfo>();

		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;

		try {
			if(type.equalsIgnoreCase("service")){
				List<StatusInfo> Infos = statusGateway.getAllStatusInfoByUserId(userID,value,"service"); // "service"
				if(Constant.DEBUG)  Log.d(TAG , "getfavServiceList Size "+Infos.size());
				if (Infos != null && Infos.size() > 0) {
					for (int i = 0; i < Infos.size(); i++) {
						if (Infos.get(i) != null) {
							ChannelInfo chlInfo = channelGateway.getServiceInfoByServiceId(Infos.get(i).getServiceId());
							if (chlInfo != null) {
								list.add(chlInfo);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return list;
	}

	public List<ProgramInfo> getFavEventList(int value, String userID, String type) {

		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;

		try {
			if(type.equalsIgnoreCase("event")){
				List<StatusInfo> Infos = statusGateway.getAllStatusInfoByUserId(userID,value,"event"); // "event"
				if(Constant.DEBUG)  Log.d(TAG , "getfavEventList Size "+Infos.size());
				if (Infos != null && Infos.size() > 0) {
					for (int i = 0; i < Infos.size(); i++) {
						if (Infos.get(i) != null) {
							ProgramInfo programInfo = programGateway.getProgramInfoByEventId(Infos.get(i).getEventId());
							if (programInfo != null) {
								list.add(programInfo);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return list;
	}
	
	
	private JSONArray getJsonRecordAndReminderList(int value, String userID) throws JSONException {
		List<ProgramInfo> recordReminderList = new ArrayList<ProgramInfo>();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		recordReminderList = getRecordAndReminderList(value, userID);
		if(Constant.DEBUG)  Log.d(TAG , "recordReminderList Size "+recordReminderList.size());
		
		if (value == 4) {
			if(recordReminderList != null && recordReminderList.size() > 0){
				for(ProgramInfo programInfo : recordReminderList){
					if(programInfo != null){
						jsonObject = new JSONObject();
						String status = getstatusOfRecording(programInfo.getStartTime(), programInfo.getDuration()); 
						jsonObject.put("id", programInfo.getEventId());
						jsonObject.put("name", programInfo.getEventName()+"- ("+status+")");
						jsonObject.put("date", programInfo.getDate());
						jsonObject.put("startTime", programInfo.getStartTime());
						jsonObject.put("duration", programInfo.getDuration());
						jsonArray.put(jsonObject);
					}
				}
			} else {
				if(Constant.DEBUG)  Log.d(TAG , "EventInfo is null");
			}
		}else{
			if(recordReminderList != null && recordReminderList.size() > 0){
				for(ProgramInfo programInfo : recordReminderList){
					if(programInfo != null){
						jsonObject = new JSONObject();
						jsonObject.put("id", programInfo.getEventId());
						jsonObject.put("name", programInfo.getEventName());
						jsonObject.put("date", programInfo.getDate());
						jsonObject.put("startTime", programInfo.getStartTime());
						jsonObject.put("duration", programInfo.getDuration());
						
						jsonArray.put(jsonObject);
					}
				}
			} else {
				if(Constant.DEBUG)  Log.d(TAG , "EventInfo is null");
			}
		}
		return jsonArray;
	}
	
	
	private String getstatusOfRecording(String stime,String duration){
		String status = "";
		try{
			long currentTime = com.port.api.util.CommonUtil.getDateTime();
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
			formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			Date date = formatter.parse(stime);
			long starttime = date.getTime();

			StringTokenizer tokens = new StringTokenizer(duration, ":");
			int hours = 0;
			int min = 0;
			if (tokens.hasMoreTokens()) {
				hours = Integer.parseInt(tokens.nextToken());
			}
			if(tokens.hasMoreTokens()){
				min = Integer.parseInt(tokens.nextToken());
			}
			if(Constant.DEBUG)  Log.d(TAG, "Hours: "+hours+", Minute: "+min);
			long endtime = starttime + hours*60*60*1000 + min*60*1000;
			
			if (currentTime > starttime && currentTime < endtime) {
				status  = "progressing";
			}else if(currentTime > starttime && currentTime > endtime){
				status  = "done";
			}else if(currentTime < starttime){
				status  = "scheduled";
			}
			return status;
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return status;
	}
	
	public List<ProgramInfo> getRecordAndReminderList(int value, String userID) {
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		try {
			List<StatusInfo> Infos = statusGateway.getAllStatusInfoByUserId(userID,value,"event"); // "event"
			if(Constant.DEBUG)  Log.d(TAG , "getRecordAndReminderList Size "+Infos.size());
			if (Infos != null && Infos.size() > 0) {
				for (int i = 0; i < Infos.size(); i++) {
					if (Infos.get(i) != null) {
						ProgramInfo programInfo = programGateway.getProgramInfoByEventId(Infos.get(i).getEventId());
						if (programInfo != null) {
							list.add(programInfo);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return list;
	}
	
	public static String getHoursAndMin(String time,String tag) {
		Date parsed;
		if(time != null && !time.equalsIgnoreCase("")){
			try {
				Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
//				SimpleDateFormat timeFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				SimpleDateFormat timeFormat1 = new SimpleDateFormat("HH:mm:ss");
				SimpleDateFormat timeFormat2 = new SimpleDateFormat("HH");
				SimpleDateFormat timeFormat3 = new SimpleDateFormat("mm");
				if(tag.equalsIgnoreCase("hours")){
					parsed = timeFormat1.parse(time);
					String result = timeFormat2.format(parsed);
					return result;
				}else{
					parsed = timeFormat1.parse(time);
					String result = timeFormat3.format(parsed);
					return result;
				}
			}catch (Exception e) {
    			e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
    		}
		}
		return time;
	}
	
}
