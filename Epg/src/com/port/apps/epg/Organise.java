package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.port.Channel;
import com.port.Port;
import com.port.api.apps.webservices.RSS;
import com.port.api.db.service.BouquetGateway;
import com.port.api.db.service.BouquetInfo;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ChannelInfo;
import com.port.api.db.service.ProfileGateway;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.util.CacheData;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.apps.epg.util.CommonUtil;

public class Organise extends IntentService {
	
	private String TAG = "Organise";
	Activity activity;
	String method = "com.port.apps.epg.Organise.";
	
	//Receiver
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String caller;
	Channel returner;
	String dockID;
		
	public Organise() {
		super("Organise");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String id = "";
		String name = "";
		String url = "";
		String category = "";
		int showid = 0;
		
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
		    			id = jsonObj.getString("id");
		    		}if(jsonObj.has("name")){
		    			name = jsonObj.getString("name");
		    		}if(jsonObj.has("showid")){
		    			showid = Integer.parseInt(jsonObj.getString("showid"));
		    		}if(jsonObj.has("url")){
		    			url = jsonObj.getString("url");
		    		}if(jsonObj.has("type")){
		    			category = jsonObj.getString("type");
		    		}
	    		} catch (JSONException e) {
	    			e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}
	    	}
	    	if(extras.containsKey("Method")){
	    		try{
	    			func = extras.getString("Method");
	    			if(func.equalsIgnoreCase("createService")){
	    				createService(name, showid);
	    			}else if(func.equalsIgnoreCase("RenameService")){
	    				RenameService(showid, name);
	    			}else if(func.equalsIgnoreCase("deleteChannel")){
	    				deleteChannel(showid);
	    			}else if(func.equalsIgnoreCase("fetchData")){
	    				fetchData(showid, url, category);
	    			}else if(func.equalsIgnoreCase("addTo")){
	    				addTo(showid, name);
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


	//create personal channel, consumer : Player + TV
	private void createService(String name,int bouquetId){
		if(Constant.DEBUG)  Log.d(TAG, "createService().name: "+name+", bouquetId: "+bouquetId);
		try{
			if(name != null && !name.trim().equalsIgnoreCase("")) {
				if(Constant.DEBUG)  Log.d(TAG, "createService().name: "+name);
				
				BouquetGateway bouquetInfoGateway =new BouquetGateway(Port.c.getApplicationContext()) ;
				ProfileGateway profileGateway = new ProfileGateway(Port.c.getApplicationContext()) ;
				
				int userId=CacheData.getUserId();
				if(userId==0){
					CacheGateway cache  = new CacheGateway(Port.c);
					CacheInfo info = cache.getCacheInfo(1000);
					if (info != null) {
						userId = Integer.valueOf(info.getProfile());
						CacheData.setUserId(userId);
					}
				}
				
				if(Constant.DEBUG)  Log.d(TAG, "createService().userId: "+userId);
				BouquetInfo bouquetInfo = bouquetInfoGateway.getBouquetInfoById(bouquetId);
				if(Constant.DEBUG)  Log.d(TAG, "createService().bouquetInfo: "+bouquetInfo);
				if(bouquetInfo == null){
					if(Constant.DEBUG)  Log.d(TAG, "Bouquet is missing");
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("result", "failure");
					data.put("msg", "Please select any bouquet.");
					sendResponse.put("params", data);
					returner.add(method+"createService", sendResponse,"messageActivity");
					returner.send();
					
				} else if(bouquetInfo != null && bouquetInfo.getBouquetId() == bouquetId) {
					if(Constant.DEBUG)  Log.d(TAG, "create bouquet");
					try{
						String category = bouquetInfo.getCategory();
						ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
						
						if(Constant.DEBUG)  Log.d(TAG, "category: "+category);
						int size = channelGateway.getAllServiceInfoByType("personal").size();
						int serviceid = 10000 + size;
						if (Constant.DVB) {
							channelGateway.insertDvbChannelInfo(serviceid,0, 0, 0, 0, "personal", name, bouquetId, userId, "", "", 0F, "", "", 0, 0, category, "",0,0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime(),"");
						} else {
							channelGateway.insertChannelInfo(serviceid, 0, "", "personal", name, bouquetId, userId, "", "", 0F, "", "", 0, 0, category, "",com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime(),"");
						}
						Catalogue.ServiceList.clear();
						JSONObject sendResponse = new JSONObject();
						JSONObject data = new JSONObject();
						data.put("result", "success");
						data.put("serviceid", serviceid+"");
						sendResponse.put("params", data);
						returner.add(method+"createService", sendResponse,"messageActivity");
						returner.send();
					}catch (Exception e) {
		    			e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		    		}
	
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	//rename personal channel, consumer : Player + TV
	private void RenameService(int channelId, String newname){
		if(Constant.DEBUG)  Log.d(TAG, "RenameService().newname: "+newname+", channelId: "+channelId);
		try{
			if(newname != null && !newname.trim().equalsIgnoreCase("")) {
				ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
				ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(channelId);
				if(serviceInfo != null && serviceInfo.getType().equalsIgnoreCase("personal")){
					channelGateway.renameServiceInfoByChannelId(channelId, newname);
					Catalogue.ServiceList.clear();
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("name", newname);
					data.put("result", "success");
					sendResponse.put("params", data);
					returner.add(method+"RenameService", sendResponse,"messageActivity");
					returner.send();
					
				}else{
					if(Constant.DEBUG)  Log.d(TAG, "rename service result is failure");
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("result", "failure");
					sendResponse.put("params", data);
					returner.add(method+"RenameService", sendResponse,"messageActivity");
					returner.send();
					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	//delete personal channel, consumer : Player + TV
	private void deleteChannel(int id){
		if(Constant.DEBUG)  Log.d(TAG, "deleteChannel()");
		try{
			if(id > 0) {
				ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
				ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
				
				ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(id);
				if(serviceInfo != null && serviceInfo.getType().equalsIgnoreCase("personal")){
					int channelId = serviceInfo.getServiceId();
					channelGateway.deleteServiceInfoByChannelId(channelId);
					programGateway.deleteEventInfoByServiceId(serviceInfo.getServiceId());
					if(Constant.DEBUG)  Log.d(TAG, "custom service and it's events is deleted successfully.");
					
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("result", "success");
					data.put("msg", "Done! Deleted.");
					sendResponse.put("params", data);
					returner.add(method+"deleteChannel", sendResponse,"messageActivity");
					returner.send();
					
				}else{
					if(Constant.DEBUG)  Log.d(TAG, "delete service result is failure");
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("result", "failure");
					sendResponse.put("params", data);
					returner.add(method+"deleteChannel", sendResponse,"messageActivity");
					returner.send();
					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	//fetch content feed into personal channel, consumer : Player + TV
	private void fetchData(int id,String url, String category){
		if(Constant.DEBUG)  Log.d(TAG, "fetchData() id: "+id+", url: "+url+", category: "+category);
		try{
			int channelId = id;
			int BouquetId = 0;
			String ChannelName = "";
			
			ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			
			ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(channelId);
			if(serviceInfo != null){
				BouquetId = serviceInfo.getBouquetId();
				ChannelName = serviceInfo.getChannelName();
			}
			
			if(serviceInfo.getType().equalsIgnoreCase("personal")){
				if(category != null && category.trim().equalsIgnoreCase("videos")) {
					String type = serviceInfo.getType();
					String subCategory = "Videos";
					JSONObject externalEventJson = RSS.getEventJsonForRSS("RSS", url, "English");
					JSONArray jsonArray = null;
					if(externalEventJson != null) {
						if(Constant.DEBUG)  Log.d(TAG,"External event json response is : "+externalEventJson.toString());
						jsonArray = externalEventJson.getJSONArray("data");
					}
					if(jsonArray != null && jsonArray.length() > 0) {
						String serviceName = null;
	
						if(externalEventJson.has("name")){
							serviceName = externalEventJson.getString("name");
						}
						if(serviceName != null && !(serviceName.trim().equalsIgnoreCase(""))) {
							insertEventDataForRSS(channelId, jsonArray,"videos",BouquetId,ChannelName);
							
							JSONObject sendResponse = new JSONObject();
							JSONObject data = new JSONObject();
							data.put("result", "success");
							sendResponse.put("params", data);
							returner.add(method+"fetchData", sendResponse,"messageActivity");
							returner.send();
							
						}else{
							JSONObject sendResponse = new JSONObject();
							JSONObject data = new JSONObject();
							data.put("result", "failure");
							data.put("msg", this.getResources().getString(R.string.FETCH_CONTENT_FAILED));
							sendResponse.put("params", data);
							returner.add(method+"fetchData", sendResponse,"messageActivity");
							returner.send();
							
						}
					} else {
						JSONObject sendResponse = new JSONObject();
						JSONObject data = new JSONObject();
						data.put("result", "failure");
						data.put("msg", this.getResources().getString(R.string.FETCH_CONTENT_FAILED));
						sendResponse.put("params", data);
						returner.add(method+"fetchData", sendResponse,"messageActivity");
						returner.send();
					}
				} else if(category != null && category.trim().equalsIgnoreCase("photos")) {
					
				} else {
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("result", "failure");
					data.put("msg", this.getResources().getString(R.string.FETCH_CONTENT_FAILED));
					sendResponse.put("params", data);
					returner.add(method+"fetchData", sendResponse,"messageActivity");
					returner.send();
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		
	}
	
	//move event into different channel, consumer : Player + TV
	private void addTo(int child, String parent){	//eventid & Channelname
		if(Constant.DEBUG)  Log.d(TAG, "addTo child: "+child+", parent: "+parent);
		try{
			if(parent != null && !(parent.trim().equalsIgnoreCase(""))) {
				ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
				ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
				
				ChannelInfo serviceInfo = channelGateway.getPersonalServiceInfo(parent);
				if(Constant.DEBUG)  Log.d(TAG, "addTo serviceInfo: "+serviceInfo.getServiceId());
				if(serviceInfo != null && serviceInfo.getType().equalsIgnoreCase("personal") && serviceInfo.getChannelName().equalsIgnoreCase(parent)){
					int channelId = serviceInfo.getServiceId();
					String channelName = serviceInfo.getChannelName();
					programGateway.updatePersonalInfoById(child, channelId,channelName);
					if(Constant.DEBUG)  Log.d(TAG, "selected Event is added in requested channel");
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("result", "success");
					sendResponse.put("params", data);
					returner.add(method+"addTo", sendResponse,"messageActivity");
					returner.send();
				}else{
					if(Constant.DEBUG)  Log.d(TAG, "selected Event is added in requested channel is failure");
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("result", "failure");
					sendResponse.put("params", data);
					returner.add(method+"addTo", sendResponse,"messageActivity");
					returner.send();
				}
			}else{
				if(Constant.DEBUG)  Log.d(TAG, "Requested channel is NULL");
				JSONObject sendResponse = new JSONObject();
				JSONObject data = new JSONObject();
				data.put("result", "failure");
				sendResponse.put("params", data);
				returner.add(method+"addTo", sendResponse,"messageActivity");
				returner.send();
			}
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	
	
	/***********************************************************************/
	private void insertEventDataForRSS(int channelId, JSONArray jsonArray,String category,int BouquetId,String ChannelName)  throws JSONException {

		String eventName = "";
		String description = "";
		String language = "";
		String startDate = "";
		String endDate = "";
		String startTime = "";
		String eventId = "";
		String image = "";
		String duration = "";
		float price = 0;
		String sourceUrl = "";
		String releaseDate = "";
		String rating = "";
		

		for(int i=0; i<jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if(jsonObject.has("title")){
				eventName = jsonObject.getString("title");
			}
			if(jsonObject.has("name")){
				eventName = jsonObject.getString("name");
			}
			if(jsonObject.has("description")){
				description = jsonObject.getString("description");
			}
			if(jsonObject.has("id")){
				eventId = jsonObject.getString("id");
			}
			if(jsonObject.has("image")){
				image = jsonObject.getString("image");
			}
			if(jsonObject.has("duration")){
				duration = jsonObject.getString("duration");
			}
			if(jsonObject.has("price")){
				price = Float.parseFloat(jsonObject.getString("price"));
			}
			if(jsonObject.has("url")){
				sourceUrl = jsonObject.getString("url");
			}
			if(jsonObject.has("releasedate")){
				releaseDate = jsonObject.getString("releasedate");
			}
			if(jsonObject.has("category")){
				category = jsonObject.getString("category");
			}
			if(jsonObject.has("rating")){
				rating = jsonObject.getString("rating");
			}

			if(Constant.DEBUG)  Log.d(TAG,"channelId : "+channelId+", eventId : "+eventId+", eventName : "+eventName+", description : "+description+", language : "+language+", image : "+image+", duration : "+duration);

			if(channelId > 0) {
				if(eventId != null && !(eventId.trim().equalsIgnoreCase(""))) {
					ProgramGateway eventInfoGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
					if (Constant.DVB) {
						int size = Catalogue.EventList.size() + 1;
						Catalogue.EventList.add(new ProgramInfo(size, sourceUrl, "personal",channelId, "", price, "", "", releaseDate, description, "", image, 0, "", "", "", "", startTime, duration, rating, language, eventName, category, 0,"",BouquetId,ChannelName,0,"",0,0,""));
						ProgramInfo externalEventInfo = eventInfoGateway.getExternalEventInfoByBoth(channelId, size);
						if(externalEventInfo == null) {
							eventInfoGateway.insertDvbProgramInfo( sourceUrl, "personal", channelId, "", price, "", "", releaseDate, description, "", image, 0, "", "", "", "", "",startTime, duration, rating, language, eventName, category, 0,"",BouquetId,ChannelName,0,"",0,0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
						} else if(externalEventInfo != null && externalEventInfo.getChannelServiceId() == channelId && externalEventInfo.getEventId() == Integer.parseInt(eventId)) {
							eventInfoGateway.updateDvbProgramInfo(size,sourceUrl, "personal", channelId, "", price, "", "", releaseDate, description, "", image, 0, "", "", "", "", "",startTime, duration, rating, language, eventName, category, 0,"",BouquetId,ChannelName,0,"",0,0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
						}
					} else {
						int size = Catalogue.EventList.size() + 1;
						Catalogue.EventList.add(new ProgramInfo(size, sourceUrl, "personal",channelId, "", price, "", "", releaseDate, description, "", image, 0, "", "", "", "", startTime, duration, rating, language, eventName, category, 0,"",BouquetId,ChannelName,0,""));
						ProgramInfo externalEventInfo = eventInfoGateway.getExternalEventInfoByBoth(channelId, size);
						if(externalEventInfo == null) {
							eventInfoGateway.insertProgramInfo( sourceUrl, "personal", channelId, "", price, "", "", releaseDate, description, "", image, 0, "", "", "", "", startTime, duration, rating, language, eventName, category, 0,"",BouquetId,ChannelName,0,"",com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
						} else if(externalEventInfo != null && externalEventInfo.getChannelServiceId() == channelId && externalEventInfo.getEventId() == Integer.parseInt(eventId)) {
							eventInfoGateway.updateProgramInfo(size,sourceUrl, "personal", channelId, "", price, "", "", releaseDate, description, "", image, 0, "", "", "", "", startTime, duration, rating, language, eventName, category, 0,"",BouquetId,ChannelName,0,"",com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
						}
					}
				}
			}
		}

	}
}
