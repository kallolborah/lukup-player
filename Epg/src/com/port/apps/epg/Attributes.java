package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.port.Channel;
import com.port.Port;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ChannelInfo;
import com.port.api.db.service.ProfileGateway;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.service.StatusInfo;
import com.port.api.db.util.CacheData;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.api.webservices.Subscription;
import com.port.apps.epg.util.CommonUtil;

public class Attributes extends IntentService {

	private String TAG = "Attributes";
	String method = "com.port.apps.epg.Attributes.";
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String caller;
	Channel returner;
	String dockID;
	
	public Attributes() {
		super("Attributes");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		int id = 0;
		String pwd = "";
		String type = "";
		String state = "";
		String password = "";
		String userid = "";
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
		    		}if(jsonObj.has("type")){
		    			type = jsonObj.getString("type");
		    		}if(jsonObj.has("imageid")){
		    			pwd = jsonObj.getString("imageid");
		    		}if(jsonObj.has("state")){
		    			state = jsonObj.getString("state");
		    		}if(jsonObj.has("password")){
		    			password = jsonObj.getString("password");
		    		}if(jsonObj.has("userid")){
		    			userid = jsonObj.getString("userid");
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
	    			if(func.equalsIgnoreCase("Lock")){
	    				Lock(id, pwd, type, state);
	    			}else if(func.equalsIgnoreCase("Like")){
	    				Like(id, type, state);
	    			}else if(func.equalsIgnoreCase("Subscriptions")){
	    				Subscriptions(id, type, state, password);
	    			}else if(func.equalsIgnoreCase("sendPlaylistData")){
	    				sendPlaylistData(userid);
	    			}else if(func.equalsIgnoreCase("PlayList")){
	    				PlayList(id, state);
	    			}else if(func.equalsIgnoreCase("getTVHomeStatus")){
	    				getTVHomeStatus();
	    			}
	    		} catch (JSONException e) {
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}		    		
	    	}
	    	
	    }
	}
	
	private void getTVHomeStatus() {
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			
			String status = com.port.api.db.util.CacheData.getHomeVisibility();
			if(status.equalsIgnoreCase("visible")){
				data.put("status", status);
			}else{
				data.put("status", "invisible");
			}
			sendResponse.put("params", data);
			returner.add(method+"getTVHomeStatus", sendResponse, "messageActivity");
			returner.send();
		} catch (JSONException e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//id could be event or service id, type will be either event or service, state could be lock or unlock
	private void Lock(int id,String pwd,String type, String state) throws JSONException {
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		try{
			ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
			
//			int userId=CacheData.getUserId();
//			if(Constant.DEBUG)  Log.d(TAG, "User id from CacheData in Lock " + userId);
			int userId = 0;
			if(userId==0){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					userId = Integer.valueOf(info.getProfile());
					CacheData.setUserId(userId);
				}
			}
			if(Constant.DEBUG)  Log.d(TAG, "User id used in Lock " + userId);
			
			ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
			CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));

			if(id > -1){
				if(userId != Constant.GuestUserId){
					if((pwd != null && !pwd.trim().equalsIgnoreCase("")) && isUserPasswordValidate(pwd)) {
						if(type.equalsIgnoreCase("event")){
							ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(id);
							String eventName = selectedEventInfo.getEventName();
							StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", id, 2,"event");
							if(state.trim().equalsIgnoreCase("Lock")) {
								if(lockInfo == null) {
									if(Constant.DEBUG)  Log.d(TAG, "You locked "+eventName+" event.");
									statusGateway.insertStatusInfo(userId,0, id, 0, 2, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
									cleanList(type);
									data.put("id", id);
									data.put("result", "success");
									data.put("msg", this.getResources().getString(R.string.LOCK)+" "+eventName);
									sendResponse.put("params", data);
									returner.add(method+"Lock", sendResponse,"messageActivity");
									returner.send();
								} else {
									if(Constant.DEBUG)  Log.d(TAG, "Again You have re-activated lock functionality for "+eventName+" Channel.");
									data.put("result", "failure");
									data.put("msg", this.getResources().getString(R.string.LOCK_FAILURE));
									sendResponse.put("params", data);
									returner.add(method+"Lock", sendResponse, "messageActivity");
									returner.send();
									
								}
							} else if(state.trim().equalsIgnoreCase("Unlock")) {
								if(lockInfo != null) {
									if(Constant.DEBUG)  Log.d(TAG, "You unlocked "+eventName+" event");
									statusGateway.toggleStatusInfo(userId+"", id, "event", 11);
									cleanList(type);
									data.put("id", id);
									data.put("result", "success");
									data.put("msg", this.getResources().getString(R.string.UNLOCK)+" "+eventName);
									sendResponse.put("params", data);
									returner.add(method+"Lock", sendResponse, "messageActivity");
									returner.send();
								} else {
									data.put("result", "failure");
									data.put("msg", this.getResources().getString(R.string.UNLOCK_FAILURE));
									sendResponse.put("params", data);
									returner.add(method+"Lock", sendResponse, "messageActivity");
									returner.send();
								}
							
							} else {
								if(Constant.DEBUG)  Log.d(TAG, "Enterd password is not matched to unlock the "+eventName+" channel.");
								data.put("result", "failure");
								data.put("msg", this.getResources().getString(R.string.UNKNOWN_ERROR));
								sendResponse.put("params", data);
								returner.add(method+"Lock", sendResponse, "messageActivity");
								returner.send();
							}
								
						}else{
							ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(id);
							String channelName = serviceInfo.getChannelName();
							StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", id, 2,"service");
							if(state.trim().equalsIgnoreCase("Lock")) {
								if(lockInfo == null) {
									if(Constant.DEBUG)  Log.d(TAG, "You locked "+channelName+" Channel.");
									statusGateway.insertStatusInfo(userId,id, 0, 0, 2, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
									cleanList(type);
									data.put("id", id);
									data.put("result", "success");
									data.put("msg", this.getResources().getString(R.string.LOCK)+" "+channelName);
									sendResponse.put("params", data);
									returner.add(method+"Lock", sendResponse, "messageActivity");
									returner.send();
									
								} else {
									if(Constant.DEBUG)  Log.d(TAG, "Again You have re-activated lock functionality for "+channelName+" Channel.");
									data.put("result", "failure");
									data.put("msg", this.getResources().getString(R.string.LOCK_FAILURE));
									sendResponse.put("params", data);
									returner.add(method+"Lock", sendResponse, "messageActivity");
									returner.send();
									
								}
							}else if(state.trim().equalsIgnoreCase("Unlock")) {
//								StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", id, 2,"service");
								if(lockInfo != null) {
									if(Constant.DEBUG)  Log.d(TAG, "You unlocked "+channelName+" channel");
									statusGateway.toggleStatusInfo(userId+"", id, "service", 11);
									cleanList(type);
									data.put("id", id);
									data.put("result", "success");
									data.put("msg", this.getResources().getString(R.string.UNLOCK)+" "+channelName);
									sendResponse.put("params", data);
									returner.add(method+"Lock", sendResponse, "messageActivity");
									returner.send();
								} else {
									if(Constant.DEBUG)  Log.d(TAG, "Again You have re-activated lock functionality for "+channelName+" Channel.");
									data.put("result", "failure");
									data.put("msg", this.getResources().getString(R.string.UNLOCK_FAILURE));
									sendResponse.put("params", data);
									returner.add(method+"Lock", sendResponse, "messageActivity");
									returner.send();
								}
							} 							
						}
					} else {
						data.put("msg", this.getResources().getString(R.string.WRONG_PASSWORD));
						
						String mProfileName = "Guest";
						ProfileInfo selectedProfileInfos = CacheData.getSelectedProfileInfo();
						if(selectedProfileInfos != null){
							mProfileName = selectedProfileInfos.getUserName();
						}
						
						data.put("name", mProfileName);
						sendResponse.put("params", data);
						returner.add(method+"Lock", sendResponse, "messageActivity");
						returner.send();
					}	
				} else {
					data.put("result", "failure");
					data.put("msg", this.getResources().getString(R.string.LOCK_FROM_GUEST));
					sendResponse.put("params", data);
					returner.add(method+"Lock", sendResponse, "messageActivity");
					returner.send();
					
				}
	
			} else {
				if(Constant.DEBUG)  Log.d(TAG , "Invalid channel id is selected.");
				data.put("result", "failure");
				data.put("msg", this.getResources().getString(R.string.UNKNOWN_ERROR));
				sendResponse.put("params", data);
				returner.add(method+"Lock", sendResponse, "messageActivity");
				returner.send();
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    }
	}
	
	//id could be event or service id, type will be either event or service, state could be like or unlike
	private void Like(int id,String type, String state) throws JSONException {
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		String fbDesc = "";
		String fbName = "";
		String fbLink = "";
		String fbLogo = "";
		
		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		try{
			int userId=CacheData.getUserId();
			if(userId==0){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					userId = Integer.valueOf(info.getProfile());
					CacheData.setUserId(userId);
				}
			}
			
			ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
			CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));
			
			if(Constant.DEBUG)  Log.d(TAG, "User id liking content is "+ userId);
			if(id > -1){
				if(type.equalsIgnoreCase("event")){
					ProgramInfo programInfo = programGateway.getProgramInfoByEventId(id);
					String eventName = programInfo.getEventName();
					if(state.equalsIgnoreCase("like")){
						StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", id, 1,"event");
						if(likeInfo == null) {
							if(Constant.DEBUG)  Log.d(TAG, "You liked "+eventName);
							statusGateway.insertStatusInfo(userId, 0, id,0, 1, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
							cleanList(type);
							String description = programInfo.getDescription();
							if(Constant.DEBUG)  Log.d(TAG, "description : "+description);
							if(!description.equalsIgnoreCase("") && description != null){
								fbDesc =  description;
							}
							fbName = eventName;
							String logo = null;
							if(Constant.DEBUG)  Log.d(TAG, "logo : "+logo);
							if(!programInfo.getImage().equalsIgnoreCase("") && programInfo.getImage() != null){
								logo = programInfo.getImage().replaceAll("%20", " ");
								try {
									fbLogo = logo;
									if(Constant.DEBUG)  Log.d(TAG, "final logo : "+logo);
								}catch(Exception e){
									e.printStackTrace();
									StringWriter errors = new StringWriter();
									e.printStackTrace(new PrintWriter(errors));
									SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
							    }
							} else {
								if(Constant.DEBUG)  Log.d(TAG, "image is null or empty");
							}
							fbLink = Constant.FB_LIKE_URL+programInfo.getProgramId();
							data.put("id", id);
							data.put("result", "success");
							data.put("msg", this.getResources().getString(R.string.LIKE)+" "+eventName+"!");
							sendResponse.put("params", data);
							returner.add(method+"Like", sendResponse, "messageActivity");
							returner.send();
							
							//FB like code blocked till navigation on TV is fixed
							
//							Intent intent = new Intent(this, Social.class);
//							intent.putExtra("Title", fbName);
//							intent.putExtra("Link", fbLink);
//							intent.putExtra("Desc", fbDesc);
//							intent.putExtra("Image", fbLogo);
//							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//							startActivity(intent);
							
						}else{
							if(Constant.DEBUG)  Log.d(TAG, "selected "+eventName+" program is already liked by current user.");
							data.put("result", "failure");
							sendResponse.put("params", data);
							returner.add(method+"Like", sendResponse, "messageActivity");
							returner.send();
						}
					}else if(state.equalsIgnoreCase("unlike")){
						StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", id, 1,"event");
						if(likeInfo != null) {
							statusGateway.toggleStatusInfo(userId+"", id, "event", 10);
							
							cleanList(type);
							data.put("id", id);
							data.put("result", "success");
							data.put("msg", this.getResources().getString(R.string.UNLIKE)+eventName+"!");
							sendResponse.put("params", data);
							returner.add(method+"Like", sendResponse, "messageActivity");
							returner.send();
						}
					}
				}else{
					ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(id);
					String channelName = serviceInfo.getChannelName();
					
					if(state.equalsIgnoreCase("like")){
						StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", id, 1,"service");
						if(likeInfo == null) {
							if(Constant.DEBUG)  Log.d(TAG, "You liked "+channelName+" Channel.");
							statusGateway.insertStatusInfo(userId, id, 0, 0, 1, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
							cleanList(type);
							String description = serviceInfo.getDesc();
							if(Constant.DEBUG)  Log.d(TAG, "description : "+description);
							if(!description.equalsIgnoreCase("") && description != null){
								fbDesc =  description;
							}
							
							fbName = channelName;
							fbLink = "";
							String logo = null;
							if(Constant.DEBUG)  Log.d(TAG, "logo : "+logo);
							if(!serviceInfo.getChannelLogo().equalsIgnoreCase("") && serviceInfo.getChannelLogo() != null){
								logo = serviceInfo.getChannelLogo().replaceAll("%20", " ");
								try {
									fbLogo = logo;
									if(Constant.DEBUG)  Log.d(TAG, "final logo : "+logo);
								}catch(Exception e){
									e.printStackTrace();
									StringWriter errors = new StringWriter();
									e.printStackTrace(new PrintWriter(errors));
									SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
							    }
							} else {
								if(Constant.DEBUG)  Log.d(TAG, "image is null or empty");
							}
							fbLink = Constant.FB_LIKE_URL+"/c"+id;
							data.put("id", id);
							data.put("result", "success");
							data.put("msg", "You liked "+channelName+"!");
							sendResponse.put("params", data);
							returner.add(method+"Like", sendResponse, "messageActivity");
							returner.send();
							
							//FB like code blocked till navigation on TV is fixed
							
//							Intent intent = new Intent(this, Social.class);
//							intent.putExtra("Title", fbName);
//							intent.putExtra("Link", fbLink);
//							intent.putExtra("Desc", fbDesc);
//							intent.putExtra("Image", fbLogo);
//							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//							startActivity(intent);
							
						}else{
							if(Constant.DEBUG)  Log.d(TAG, "selected "+channelName+" channel is already liked by current user.");
							data.put("result", "failure");
							sendResponse.put("params", data);
							returner.add(method+"Like", sendResponse, "messageActivity");
							returner.send();
						}
					}else if(state.equalsIgnoreCase("unlike")){
						if(Constant.DEBUG)  Log.d(TAG, "You unliked "+channelName+" Channel.");
						StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", id, 1,"service");
						if(likeInfo != null) {
							statusGateway.toggleStatusInfo(userId+"", id, "service", 10);
							cleanList(type);
							data.put("id", id);
							data.put("result", "success");
							data.put("msg", this.getResources().getString(R.string.UNLIKE)+""+channelName+" Channel.");
							sendResponse.put("params", data);
							returner.add(method+"Like", sendResponse, "messageActivity");
							returner.send();
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    }
		
	}
	
	//id indicates event, state could be add or remove
	private void PlayList(int id, String state){
		try {
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			
			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
			
			int userId=CacheData.getUserId();
			if(userId==0){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					userId = Integer.valueOf(info.getProfile());
					CacheData.setUserId(userId);
				}
			}
			
			ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
			CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));
			
			if(state.equalsIgnoreCase("remove")){
				List<StatusInfo> playListInfos = statusGateway.getAllStatusInfoByUserId(userId+"",5,"event");
				if (playListInfos != null && playListInfos.size() > 0) {
					int row =	statusGateway.deleteStatusInfo(userId,id,5);
					if(Constant.DEBUG)Log.i("PlayList=== Events", "row remove : " + row);
						data.put("result", "success");
						data.put("state", "removed");
						data.put("msg", this.getResources().getString(R.string.REMOVED_FROM_PLAYLIST));
						resp.put("params", data);
						returner.add(method+"PlayList", resp, "messageActivity");
						returner.send();
				}
			}else if(state.equalsIgnoreCase("add")){
				StatusInfo playListInfos = statusGateway.getStatusInfoById(userId+"", id, 5,"event");
				if (playListInfos == null) {
					statusGateway.insertStatusInfo(userId,0,id, 0,5, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
					data.put("result", "success");
					data.put("state", "added");
					data.put("msg", this.getResources().getString(R.string.ADDED_TO_PLAYLIST));
					resp.put("params", data);
					returner.add(method+"PlayList", resp, "messageActivity");
					returner.send();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    }
	}
	
	//id could be event or service id, type will be either event or service, state could be subscribe or unsubscribe
	private void Subscriptions(int id,String type, String state,String pwd) throws JSONException {
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		if(Constant.DEBUG)  Log.d(TAG,"Subscription id:"+id+", type:"+type+", state:"+state+", pwd:"+pwd);
		
		String subscriberid = CacheData.getSubscriberId();	
		if(subscriberid.equalsIgnoreCase("")){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				subscriberid = info.getSubscriber();
				CacheData.setSubscriberId(subscriberid);
			}
		}
		
		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		try{
			int userId=CacheData.getUserId();
			if(userId==0){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					userId = Integer.valueOf(info.getProfile());
					CacheData.setUserId(userId);
				}
			}
			
			ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
			CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));
			
			if(id > 0){
				if(type.equalsIgnoreCase("event")){
					ProgramInfo programInfo = programGateway.getProgramInfoByEventId(id);
					int programId = programInfo.getProgramId();
					String pricingModel = programInfo.getPriceModel();
					if(Constant.DEBUG)  Log.d(TAG,"Subscription event pricingModel:"+pricingModel);
					if(pricingModel.trim().equalsIgnoreCase("PPC") && programInfo.getPrice() == 0){
						data.put("result", "success");
						resp.put("params", data);
						returner.add(method+"Subscriptions", resp, "messageActivity");
						returner.send();
					}else if(pricingModel.trim().equalsIgnoreCase("PPC")){
						int uniqueId = programInfo.getChannelServiceId();
						
						String mProfileName = "Guest";
						ProfileInfo selectedProfileInfos = CacheData.getSelectedProfileInfo();
						if(selectedProfileInfos != null){
							mProfileName = selectedProfileInfos.getUserName();
						}
						
						JSONObject jsonResponse = Subscription.subscribeForService(subscriberid,mProfileName,uniqueId+"", state, "PPC", pwd);
						
						if(jsonResponse != null){
							JSONObject jsonResponseData = jsonResponse.getJSONObject("data");
							String result = jsonResponseData.getString("result");
							String msg = jsonResponseData.getString("msg");
	
							boolean isSubscribeSuccess = (result.trim().equalsIgnoreCase("success")) ? true : false;
							if(isSubscribeSuccess){
								if(state.equalsIgnoreCase("subscribe")){
									statusGateway.insertStatusInfo(userId, uniqueId, 0, 0, 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
								}else{
									statusGateway.deleteStatusInfoById(uniqueId, "service",9);
								}
								if(Constant.DEBUG)  Log.d(TAG, msg);
								cleanList(type);
								data.put("id", id);
								data.put("result", "success");
								data.put("msg", msg);
								resp.put("params", data);
								returner.add(method+"Subscriptions", resp, "messageActivity");
								returner.send();
							}else{
								data.put("id", id);
								data.put("result", "failure");
								if (msg != null && !msg.equalsIgnoreCase("")) {
									data.put("msg", msg);
								}
								resp.put("params", data);
								returner.add(method+"Subscriptions", resp, "messageActivity");
								returner.send();
							}
							
						}else{
							data.put("id", id);
							data.put("result", "failure");
							resp.put("params", data);
							returner.add(method+"Subscriptions", resp, "messageActivity");
							returner.send();
						}
						
					}else if(pricingModel.trim().equalsIgnoreCase("PPV")){

						String mProfileName = "Guest";
						ProfileInfo selectedProfileInfo1 = CacheData.getSelectedProfileInfo();
						if(selectedProfileInfo1 != null){
							mProfileName = selectedProfileInfo1.getUserName();
						}
						
						JSONObject jsonResponse = Subscription.subscribeForService(subscriberid,mProfileName,programInfo.getProgramId()+"", state, "PPV", pwd);
						if(jsonResponse != null){
							JSONObject jsonResponseData = jsonResponse.getJSONObject("data");
							String result = jsonResponseData.getString("result");
							String msg = jsonResponseData.getString("msg");
							boolean isSubscribeSuccess = (result.trim().equalsIgnoreCase("success")) ? true : false;
							
							if(isSubscribeSuccess){
								if(state.equalsIgnoreCase("subscribe")){
	//								statusGateway.insertStatusInfo(userId, 0, id, 9, 0, 0,CommonUtil.getDate(),CommonUtil.getDateTime());
									statusGateway.insertStatusInfo(userId, 0, 0, programId, 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
								}else{
									statusGateway.deleteStatusInfoById(programId, "event",9);
								}
								cleanList(type);
								if(Constant.DEBUG)  Log.d(TAG, msg);
								data.put("id", id);
								data.put("result", "success");
								data.put("msg", msg);
								resp.put("params", data);
								returner.add(method+"Subscriptions", resp, "messageActivity");
								returner.send();
							}else{
								data.put("id", id);
								data.put("result", "failure");
								if (msg != null && !msg.equalsIgnoreCase("")) {
									data.put("msg", msg);
								}
								resp.put("params", data);
								returner.add(method+"Subscriptions", resp, "messageActivity");
								returner.send();
							}
						}else{
							data.put("id", id);
							data.put("result", "failure");
							resp.put("params", data);
							returner.add(method+"Subscriptions", resp, "messageActivity");
							returner.send();
						}
					}
				}else if(type.equalsIgnoreCase("service")){
					ChannelInfo channelInfo = channelGateway.getServiceInfoByServiceId(id);
					String pricingModel = channelInfo.getPriceModel();
					if(Constant.DEBUG)  Log.d(TAG,"Subscription service pricingModel:"+pricingModel);
					if(pricingModel.trim().equalsIgnoreCase("PPC") && channelInfo.getPrice() == 0){
						cleanList(type);
						data.put("result", "success");
						resp.put("params", data);
						returner.add(method+"Subscriptions", resp, "messageActivity");
						returner.send();
					}else if(pricingModel.trim().equalsIgnoreCase("PPC")){
						int uniqueId = channelInfo.getServiceId();

						String mProfileName = "Guest";
						ProfileInfo selectedProfileInfo3 = CacheData.getSelectedProfileInfo();
						if(selectedProfileInfo3 != null){
							mProfileName = selectedProfileInfo3.getUserName();
						}
						
						JSONObject jsonResponse = Subscription.subscribeForService(subscriberid,mProfileName,uniqueId+"", state, "PPC", pwd);
//						JSONObject jsonResponse = Subscription.subscribeForService(uniqueId+"", state, "PPC", pwd);
						if(jsonResponse != null){
							JSONObject jsonResponseData = jsonResponse.getJSONObject("data");
							String result = jsonResponseData.getString("result");
							String msg = jsonResponseData.getString("msg");
	
							boolean isSubscribeSuccess = (result.trim().equalsIgnoreCase("success")) ? true : false;
							
							if(isSubscribeSuccess){
								if(state.equalsIgnoreCase("subscribe")){
									statusGateway.insertStatusInfo(userId, id, 0, 0, 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
								}else{
									statusGateway.deleteStatusInfoById(id, "service",9);
								}
								if(Constant.DEBUG)  Log.d(TAG, msg);
								cleanList(type);
								data.put("id", id);
								data.put("result", "success");
								data.put("msg", msg);
								resp.put("params", data);
								returner.add(method+"Subscriptions", resp, "messageActivity");
								returner.send();
							}else{
								data.put("id", id);
								data.put("result", "failure");
								if (msg != null && !msg.equalsIgnoreCase("")) {
									data.put("msg", msg);
								}
								resp.put("params", data);
								returner.add(method+"Subscriptions", resp, "messageActivity");
								returner.send();
							}
						}else{
							data.put("id", id);
							data.put("result", "failure");
							resp.put("params", data);
							returner.add(method+"Subscriptions", resp, "messageActivity");
							returner.send();
						}
					}
				}else if(type.equalsIgnoreCase("package")){
					
					String mProfileName = "Guest";
					ProfileInfo selectedProfileInfo4 = CacheData.getSelectedProfileInfo();
					if(selectedProfileInfo4 != null){
						mProfileName = selectedProfileInfo4.getUserName();
					}
					
					JSONObject jsonResponse = Subscription.subscribeForService(subscriberid,mProfileName,id+"", state, "package", pwd);
					
//					JSONObject jsonResponse = Subscription.subscribeForService(id+"", state, "package", pwd);
					if(jsonResponse != null){
						JSONObject jsonResponseData = jsonResponse.getJSONObject("data");
						String result = jsonResponseData.getString("result");
						String msg = jsonResponseData.getString("msg");
	
						boolean isSubscribeSuccess = (result.trim().equalsIgnoreCase("success")) ? true : false;
						
						if(isSubscribeSuccess){
							ArrayList<String> chllist = new ArrayList<String>();
							ArrayList<String> pgmlist = new ArrayList<String>();
							String programs = "";
							String channel = "";
							for (int i = 0; i < Catalogue.packageList.size(); i++) {
								if(Catalogue.packageList.get(i).getPackageId().equalsIgnoreCase(id+"")){
									programs = Catalogue.packageList.get(i).getPackagePrograms();
									channel = Catalogue.packageList.get(i).getPackageChannels();
									if(Constant.DEBUG)  Log.d(TAG , "pgmList "+programs);
									if(Constant.DEBUG)  Log.d(TAG , "chlList "+channel);
								}
							}
							if(programs.length()>0){
								pgmlist = com.port.api.util.CommonUtil.Tokenizer(programs, ",");
								for (int j = 0; j < pgmlist.size(); j++) {
									statusGateway.insertStatusInfo(userId, 0, 0, Integer.parseInt(pgmlist.get(j)), 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
								}
							}
							
							
							if(channel.length()>0){
								chllist = com.port.api.util.CommonUtil.Tokenizer(channel, ",");
								for (int j = 0; j < chllist.size(); j++) {
									statusGateway.insertStatusInfo(userId, Integer.parseInt(chllist.get(j)), 0, 0, 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
								}
							}
							
							if(Constant.DEBUG)  Log.d(TAG, msg);
							cleanList(type);
							data.put("id", id);
							data.put("result", "success");
							data.put("msg", msg);
							resp.put("params", data);
							returner.add(method+"Subscriptions", resp, "messageActivity");
							returner.send();
						}else{
							data.put("id", id);
							data.put("result", "failure");
							if (msg != null && !msg.equalsIgnoreCase("")) {
								data.put("msg", msg);
							}
							resp.put("params", data);
							returner.add(method+"Subscriptions", resp, "messageActivity");
							returner.send();
						}
					}else{
						data.put("id", id);
						data.put("result", "failure");
						resp.put("params", data);
						returner.add(method+"Subscriptions", resp, "messageActivity");
						returner.send();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    }
	}	
	
	//send playlist
	private void sendPlaylistData(String userid){
		try{
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();

			JSONArray jsonArray = null;
			JSONObject jsonObject = null;
			
			ChannelGateway serviceInfoGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
			ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
			
			CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(userid));
			
			List<ProgramInfo> externalEventInfos = null;
			externalEventInfos = getPlayList();
			if(Constant.DEBUG)  Log.d(TAG,"sendPlaylistData() externalEventInfos: "+externalEventInfos.size());
			
			if(externalEventInfos != null && externalEventInfos.size() > 0){
				jsonArray = new JSONArray();
				
				for(ProgramInfo externalEventInfo : externalEventInfos){
					if(externalEventInfo != null){
						jsonObject = new JSONObject();
						ProgramInfo eventInfo = programGateway.getProgramInfoByEventId(externalEventInfo.getEventId());
						int serviceId = eventInfo.getChannelServiceId();
						ChannelInfo serviceInfo = serviceInfoGateway.getServiceInfoByServiceId(serviceId);
						jsonObject.put("serviceid", serviceId+"");
						jsonObject.put("category", serviceInfo.getServiceCategory());
						jsonObject.put("servicetype", serviceInfo.getType());
						jsonObject.put("channelprice", serviceInfo.getPrice());
						jsonObject.put("channelname", serviceInfo.getChannelName());
						jsonObject.put("event", "true");
						
						boolean subscribe = false;
						
						int userId=CacheData.getUserId();
						if(userId==0){
							CacheGateway cache  = new CacheGateway(Port.c);
							CacheInfo info = cache.getCacheInfo(1000);
							if (info != null) {
								userId = Integer.valueOf(info.getProfile());
								CacheData.setUserId(userId);
							}
						}
						
						int eventId = externalEventInfo.getEventId();
						jsonObject.put("id", eventId+"");
						jsonObject.put("name", externalEventInfo.getEventName());
						jsonObject.put("image", externalEventInfo.getImage());
						String pricingmodel = serviceInfo.getPriceModel();
						jsonObject.put("pricingmodel", pricingmodel);
						jsonObject.put("price", externalEventInfo.getPrice()+"");
						jsonObject.put("url", externalEventInfo.getEventSrc());
						
						if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPV")){
							StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(externalEventInfo.getProgramId(), 9,"event");
							if(info != null) {
								if(info.getStatus() == 9) {
									subscribe = true;
								} else {
									subscribe = false;
								}
							} else {
								subscribe = false;
							}
							jsonObject.put("price", externalEventInfo.getPrice()+"");
							jsonObject.put("subscribe", subscribe);
						}else{
							StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(serviceId, 9,"service");
							if(info != null) {
								if(info.getStatus() == 9) {
									subscribe = true;
								} else {
									subscribe = false;
								}
							} else {
								subscribe = false;
							}
							jsonObject.put("price", serviceInfo.getPrice()+"");
							jsonObject.put("subscribe", subscribe);
						}
						
						boolean lock = false;
						StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", eventId, 2,"event");
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
					}
					jsonArray.put(jsonObject);
				}
				data.put("eventList", jsonArray);
				data.put("result", "success");
				resp.put("params", data);
				returner.add(method+"sendPlaylistData", resp, "messageActivity");
				returner.send();
			}else{
				data.put("result", "success");
				resp.put("params", data);
				returner.add(method+"sendPlaylistData", resp, "messageActivity");
				returner.send();
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    }
	}
	
	
	/*****************************************************************/
	
	private boolean isUserPasswordValidate(String imageId) {
		try{
			if(imageId != null && !(imageId.trim().equalsIgnoreCase(""))) {
				
				ProfileInfo profileInfo = CacheData.getSelectedProfileInfo();
				if(profileInfo != null) {
					String imageIdPassword = profileInfo.getImagePwd();
					if(imageIdPassword != null && !(imageIdPassword.trim().equalsIgnoreCase(""))){
						if(imageIdPassword.trim().equalsIgnoreCase(imageId.trim())){
							if(Constant.DEBUG)  Log.d(TAG, "Authentication success.");
							return true;
						} else {
							if(Constant.DEBUG)  Log.d(TAG, "Authentication failure.");
							return false;
						}
					} else {
						if(Constant.DEBUG)  Log.d(TAG, "imageIdPassword null or empty.");
						return false;
					}
				} else {
					if(Constant.DEBUG)  Log.d(TAG, "profile info null.");
					return false;
				}
			} else {
				if(Constant.DEBUG)  Log.d(TAG, "imageId null or empty.");
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    }
		return false;
	}
	
	public static List<ProgramInfo> getPlayList() {

		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		
		try {
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
			
			int userId=CacheData.getUserId();
			if(userId==0){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					userId = Integer.valueOf(info.getProfile());
					CacheData.setUserId(userId);
				}
			}
			
			List<StatusInfo> likeInfos = statusGateway.getAllStatusInfoByUserId(userId+"",5,"event"); // "event"
			if (likeInfos != null && likeInfos.size() > 0) {
				
				for (int i = 0; i < likeInfos.size(); i++) {
					if (likeInfos.get(i) != null) {
						ProgramInfo programInfo = programGateway.getProgramInfoByEventId(likeInfos.get(i).getEventId());
						if (programInfo != null) {
							list.add(programInfo);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return list;
	}
	
	private void cleanList(String type){
		if(type.equalsIgnoreCase("service")){
			Catalogue.ServiceList.clear();
		}else if(type.equalsIgnoreCase("event")){
			Catalogue.EventList.clear();
		}
	}
	
	public static boolean isServiceLocked(int channelId){
		boolean isLocked = false;
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		int userId=CacheData.getUserId();
		if(userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}
		
		StatusInfo lockInfo = statusGateway.getStatusInfoById(userId+"", channelId,2,"service");
		if(lockInfo == null){
			isLocked = false;
		} else if(lockInfo.getStatus() == 2){
			isLocked = true;
		} else {
			isLocked = false;
		}
		return isLocked;
	}
	
	public static boolean isEventLocked(int eventId){
		boolean isLocked = false;
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		int userId=CacheData.getUserId();
		if(userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}
		
		StatusInfo lockInfo = statusGateway.getStatusInfoById(userId+"", eventId,2,"event");
		if(lockInfo == null){
			isLocked = false;
		} else if(lockInfo.getStatus() == 2){
			isLocked = true;
		} else {
			isLocked = false;
		}

		return isLocked;
	}
	
}
