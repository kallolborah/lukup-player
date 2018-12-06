package com.port.apps.settings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.android.Facebook;
import com.facebook.android.SessionStore;
import com.port.Channel;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.service.ProfileGateway;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.util.CacheData;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.apps.settings.webservices.Subscriber;

public class Profile extends IntentService {
	
	private static String TAG = "Profile";
	String method = "com.port.apps.settings.Profile.";
	
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	Channel returner;
	String caller;
	String dockID;
	private SharedPreferences settingData;
	private SharedPreferences.Editor edit;
	
	CacheGateway cache;
	CacheInfo info;
	
	public Profile() {
		super("Profile");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String userid = "";
		String pwd = "";
		String tag = "";
		String name = "";
		String status = "";
		
		settingData = getApplicationContext().getSharedPreferences("Port-Setup",MODE_WORLD_READABLE);
		edit = settingData.edit();
		
		cache  = new CacheGateway(getApplicationContext());
		info = cache.getCacheInfo(1000);
		
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
		    			userid = jsonObj.getString("id");
		    		}if(jsonObj.has("imageid")){
		    			pwd = jsonObj.getString("imageid");
		    		}if(jsonObj.has("tag")){
		    			tag = jsonObj.getString("tag");
		    		}if(jsonObj.has("name")){
		    			name = jsonObj.getString("name");
		    		}if(jsonObj.has("status")){
		    			status = jsonObj.getString("status");
		    		}
	    		} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}
	    	}	
	    	if(extras.containsKey("Method")){
	    		try{
	    			func = extras.getString("Method");
	    			if(func.equalsIgnoreCase("authenticate")){
	    				authenticate(userid, pwd, tag);
	    			}else if(func.equalsIgnoreCase("createProfile")){
	    				createProfile(name, pwd);
	    			}else if(func.equalsIgnoreCase("getAllProfiles")){
	    				getAllProfiles(); //Id is SubscriberId or else
	    			}
	    			else if(func.equalsIgnoreCase("switchProfile")){
	    				switchProfile(userid);
	    			}
	    			else if(func.equalsIgnoreCase("editProfile")){
	    				editProfile(userid, name, pwd, tag);
	    			}else if(func.equalsIgnoreCase("fbConnectDisconnect")){
	    				fbConnectDisconnect(status,Integer.parseInt(userid));
	    			}
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
	    	
	    }
	}
 
	private void fbConnectDisconnect(String status,int userId) {
//		Intent intent = new Intent(this, Social.class);
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setComponent(new ComponentName("com.port.apps.epg","com.port.apps.epg.Social"));
		
		intent.putExtra("Status", status);
		intent.putExtra("UserId", userId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivity(intent);
	}

	//authenticates profile with password
	public void authenticate(String userid, String pwd, String tag) throws JSONException, InterruptedException{
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		
		if(!userid.equalsIgnoreCase("") && userid != null){

			ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());

			ProfileInfo profileInfo = profileInfoGateway.getProfileInfo(userid);
			if(profileInfo != null) {
				String imageIdPassword = profileInfo.getImagePwd();
				if(imageIdPassword != null && !(imageIdPassword.trim().equalsIgnoreCase(""))){
					if(imageIdPassword.trim().equalsIgnoreCase(pwd)){
						if(tag.equalsIgnoreCase("switchProfile")){
							try{
//								int prevUserId = 1000;
//								ProfileInfo selectedProfileInfo = CacheData.getSelectedProfileInfo();
//								if(selectedProfileInfo != null){
//									prevUserId = selectedProfileInfo.getUserId();
//								}
								
								int prevUserId = 0;
								if(info!=null){
									prevUserId = Integer.valueOf(info.getProfile());
								}
								if(prevUserId==0 && CacheData.getSelectedProfileInfo()!=null){
									prevUserId = CacheData.getSelectedProfileInfo().getUserId();
								}
								
								CacheData.setUserId(Integer.valueOf(userid));
								CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(userid));
								
								data.put("id", userid);
								data.put("result", "success");
								data.put("msg", "Welcome "+profileInfo.getUserName()+"!");
								data.put("tag", tag);
								resp.put("params",data);
								returner.add(method+"authenticate", resp,"messageActivity");
								returner.send();
								
								if(Integer.parseInt(userid)!=prevUserId){
									if(info!=null){
										cache.updateCacheInfo(1000, info.getSsid(), info.getBssid(), info.getPwd(), info.getSecurity(), info.getSubscriber(), info.getDistributor(), userid, info.getHotspotName(), info.getHotspotPwd());
									}else{
										cache.insertCacheInfo(1000, "", "", "", "", settingData.getString("subscriberid", ""), settingData.getString("distributorId", ""), userid, "", "");
									}
									
//									Intent intent = new Intent(this, Social.class);
									Intent intent = new Intent(Intent.ACTION_MAIN);
									intent.setComponent(new ComponentName("com.port.apps.epg","com.port.apps.epg.Social"));
									intent.putExtra("Status", "disconnect");
									intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
									startActivity(intent);
								}
								
							}catch(Exception e){
								e.printStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
							}
						}else if(tag.equalsIgnoreCase("deleteProfile")){
							JSONArray jsonArray = new JSONArray();
							JSONObject jsonObject = null;
							if(Integer.parseInt(userid) == 1000){ //for guest id
								data.put("result", "failure");
								data.put("msg", this.getResources().getString(R.string.DELETE_PRIMARY_SUBSCRIBER));
							}else{
								if(profileInfo.getUserId() == Integer.parseInt(userid) && profileInfo.getImagePwd().trim().equalsIgnoreCase(pwd) ){
									profileInfoGateway.deleteProfileInfo(Integer.parseInt(userid));
									jsonObject = Subscriber.deleteProfileFromSMS(userid+"");	//webService call
								}
								data.put("result", "success");
								data.put("msg", this.getResources().getString(R.string.DELETED_PROFILE));
							}
							
//							int currentUserId = CacheData.getSelectedProfileInfo().getUserId();
							List<ProfileInfo> profileInfos = profileInfoGateway.getAllProfileInfo();
							for(ProfileInfo proInfo : profileInfos){
								try{
									if(profileInfo != null && !(proInfo.getUserId() == Integer.parseInt(userid))){
										jsonObject = new JSONObject();
										jsonObject.put("id", proInfo.getUserId()+"");
										jsonObject.put("name", proInfo.getUserName());
										jsonObject.put("image", proInfo.getfBPhoto());
										jsonArray.put(jsonObject);
									}
								}catch(Exception e){
									e.printStackTrace();
									StringWriter errors = new StringWriter();
									e.printStackTrace(new PrintWriter(errors));
									SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
								}
							}
							data.put("profileList", jsonArray);
							data.put("tag", "deleteProfile");
							resp.put("params",data);
							returner.add(method+"authenticate", resp,"messageActivity");
							returner.send();
						}
						
					}else {
						try{
							data.put("name", profileInfo.getUserName());
							data.put("result", "failure");
							data.put("tag", tag);
							data.put("msg", this.getResources().getString(R.string.WRONG_PASSWORD));
							data.put("id", profileInfo.getUserId()+"");
							data.put("imageList", new JSONArray(getImageIdsList(profileInfo.getImagePwd())));
							resp.put("params",data);
							returner.add(method+"authenticate", resp,"messageActivity");
							returner.send();
						}catch(Exception e){
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
						}
					}
				} else if(tag.equalsIgnoreCase("switchProfile") && Integer.parseInt(userid) == Constant.GuestUserId){
					try{
						if(Constant.DEBUG)  Log.d(TAG, "switchProfile userid "+userid);
						CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(userid));
						CacheData.setUserId(Integer.valueOf(userid));
						
						if(info!=null){
							cache.updateCacheInfo(1000, info.getSsid(), info.getBssid(), info.getPwd(), info.getSecurity(), info.getSubscriber(), info.getDistributor(), userid, info.getHotspotName(), info.getHotspotPwd());
						}else{
							cache.insertCacheInfo(1000, "", "", "", "", settingData.getString("subscriberid", ""), settingData.getString("distributorId", ""), userid, "", "");
						}
						
						data.put("id", userid);
						data.put("result", "success");
						data.put("msg", "Welcome "+profileInfo.getUserName()+"!");
						data.put("tag", tag);
						resp.put("params",data);
						returner.add(method+"authenticate", resp,"messageActivity");
						returner.send();
					}catch(Exception e){
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
					}
				} else {
					try{
						data.put("result", "failure");
						data.put("tag", tag);
						resp.put("params",data);
						returner.add(method+"authenticate", resp,"messageActivity");
						returner.send();
					}catch(Exception e){
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
					}
				}
			}else {
				if(Constant.DEBUG)  Log.d(TAG, "imageIdPassword is null or empty for this user.");
				try{
					data.put("result", "failure");
					data.put("tag", tag);
					resp.put("params",data);
					returner.add(method+"authenticate", resp,"messageActivity");
					returner.send();
					
				}catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}
			}
		}else {
			try{
				data.put("result", "failure");
				data.put("tag", tag);
				resp.put("params",data);
				returner.add(method+"authenticate", resp,"messageActivity");
				returner.send();
			}catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}
		}
	}
	
	//creates profile
	private void createProfile(String name, String pwd) throws JSONException, SQLException, InterruptedException {

		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();

		ProfileGateway profileGateway = new ProfileGateway(getApplicationContext());
		
		boolean exist = false;
		int userId = 0;
		String imageId = "";
		List<ProfileInfo> profileInfos = profileGateway.getAllProfileInfo();
		
		String profileName = name;
		if(!profileName.equalsIgnoreCase("") && profileName != null){
			if(profileInfos != null && profileInfos.size() > 0){
				for(ProfileInfo profileInfo : profileInfos){
					if(profileInfo != null && profileInfo.getUserName().trim().equals(profileName.trim())){
						userId = profileInfo.getUserId();
						imageId = profileInfo.getImagePwd();
						exist = true;
						break;
					}
				}
			}

			if(exist){
				if(!imageId.equalsIgnoreCase("") && imageId != null){
					data.put("result", "failure");
					data.put("tag", "createProfile");
					data.put("msg", this.getResources().getString(R.string.ALREADY_PROFILE_NAME_EXIST));
					sendResponse.put("params", data);
					returner.add(method+"createProfile", sendResponse,"messageActivity");
					returner.send();
					
				}else {
					profileGateway.setImageIdPassword(userId, pwd);
					data.put("result", "success");
					data.put("tag", "createProfile");
					data.put("name", name);
					data.put("id", userId);
					data.put("msg", "Welcome "+ name +"!");
					sendResponse.put("params",data);
					returner.add(method+"createProfile", sendResponse,"messageActivity");
					returner.send();
				}
			} else {
				JSONObject jsonLoginResponse = Subscriber.registerNewProfileName(profileName, "registerName");
				if(jsonLoginResponse != null){
					JSONObject jsonLoginResponseData = jsonLoginResponse.getJSONObject("json");
					String resultLogin = jsonLoginResponseData.getString("result");
					boolean isLoginSuccess = (resultLogin.trim().equalsIgnoreCase("success")) ? true : false;
					
					if(Constant.DEBUG)  Log.d(TAG, "Result is success "+isLoginSuccess);
					if(isLoginSuccess){
						userId = jsonLoginResponseData.getInt("userid");
						if(profileGateway != null) {
							profileGateway.insertProfileInfo(userId, name, pwd, "", "", 0, "", "", 0, 0, 0, "");
						}
						
						edit.putString("userid", userId+"");
						edit.commit();
						
						if(Constant.DEBUG)  Log.d(TAG, "register user result is success");
						try{
							data.put("result", "success");
							data.put("tag", "createProfile");
							data.put("name", name);
							data.put("id", userId);
							data.put("msg", "Welcome "+ name +"!");
							sendResponse.put("params",data);
							returner.add(method+"createProfile", sendResponse,"messageActivity");
							returner.send();
						}catch(Exception e){
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
						}
					} else {
						String registerMsg = jsonLoginResponseData.getString("msg");
						if(Constant.DEBUG)  Log.d(TAG, registerMsg);
						data.put("result", "failure");
						data.put("tag", "createProfile");
						data.put("msg", registerMsg);
						sendResponse.put("params", data);
						returner.add(method+"createProfile", sendResponse,"messageActivity");
						returner.send();
					}
				}
			}
		} else {
			if(Constant.DEBUG)  Log.d(TAG, "name is null or empty");
			data.put("result", "failure");
			data.put("tag", "createProfile");
			sendResponse.put("params", data);
			returner.add(method+"createProfile", sendResponse,"messageActivity");
			returner.send();
		}
	}
	
	//requests syncing with FB for userid
//	private void syncWithFb(int userid,String pwd) throws JSONException {
//		JSONObject resp = new JSONObject();
//		JSONObject data = new JSONObject();
//		ProfileGateway profileInfoGateway = CacheData.getProfileGateway();
//		if(profileInfoGateway == null){
//			profileInfoGateway = new ProfileGateway(CacheData.getDatabase());
//			CacheData.setProfileInfoGateway(profileInfoGateway);
//		}
//
//		if(profileInfoGateway != null) {
//			profileInfoGateway.setImageIdPassword(userid, pwd);
//			ProfileInfo profileInfo = profileInfoGateway.getProfileInfo(userid+"");
//
////			if(DataStorage.isSetupComplete()){
////				try {
//					switchedProfileSetup(profileInfo);
////				} catch (Exception e) {
////					e.printStackTrace();
////				}
////				data.put("screenId", ScreenIds.SHOW_DIALOG_FOR_ASSOCIATE+"");
////			} 
//			data.put("result", "success");
//			data.put("msg", "Welcome "+profileInfo.getUserName()+"!");
//			resp.put("params",data);
//			returner.add(method+"syncWithFb", resp,"messageActivity");
//			returner.send();
//			
//		} else {
//			data.put("result", "success");
//			resp.put("params",data);
//			returner.add(method+"syncWithFb", resp,"messageActivity");
//			returner.send();
//		}
//	}
	
	//get list of all profiles to present for choice during switching
	private void getAllProfiles() throws JSONException, InterruptedException {
		int fbStatus = 0;
		
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());

		List<ProfileInfo> profileInfos = profileInfoGateway.getAllProfileInfo();
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;

		for(ProfileInfo profileInfo : profileInfos){
			try{
				if(profileInfo != null){
					jsonObject = new JSONObject();
					jsonObject.put("id", profileInfo.getUserId()+"");
					jsonObject.put("name", profileInfo.getUserName());
					jsonObject.put("image", profileInfo.getfBPhoto());
					jsonObject.put("status", profileInfo.getFbSync()+"");
					jsonArray.put(jsonObject);
				}
			}catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}
		}
		data.put("profileList", jsonArray);
		data.put("tag", "getAllProfiles");
		resp.put("params",data);
		returner.add(method+"getAllProfiles", resp,"messageActivity");
		returner.send();
	}
	
	
	// for Delete Profile
//	private void deleteProfile(int userId, String pwd) throws JSONException {
//		JSONObject resp = new JSONObject();
//		JSONObject data = new JSONObject();
//		JSONArray jsonArray = new JSONArray();
//		JSONObject jsonChildObject = null;
//		if(userId != 0){
//			ProfileGateway profileInfoGateway = CacheData.getProfileGateway();
//			if(profileInfoGateway == null){
//				if(CommonUtil.checkConnectionForLocaldb()){
//					profileInfoGateway = new ProfileGateway(CacheData.getDatabase());
//					CacheData.setProfileInfoGateway(profileInfoGateway);
//				}
//			}
//			ProfileInfo profileInfo = profileInfoGateway.getProfileInfo(userId+"");
//			if(profileInfo != null) {
//				if(profileInfo.getUserId() == userId && profileInfo.getImagePwd().trim().equalsIgnoreCase(pwd)){
//					JSONObject jsonObject = Subscriber.deleteProfileFromSMS(userId+"");	//webService call
//					if(jsonObject != null){
//						JSONObject jsonresultData = jsonObject.getJSONObject("json");
//						if(jsonresultData != null){
//							String result = jsonresultData.getString("result");
//							if(result != null && result.trim().equalsIgnoreCase("success")){
//								profileInfoGateway.deleteProfileInfo(userId);
////								updateUserDetails();
//								int userid = CacheData.getUserId();//	setSelectedProfileInfo
//								int currentUserId = CacheData.getSelectedProfileInfo().getUserId();
//								List<ProfileInfo> profileInfos = profileInfoGateway.getAllProfileInfo();
//								for(ProfileInfo proInfo : profileInfos){
//									try{
//										if(proInfo != null && !(proInfo.getUserId() == userid)){
//											jsonChildObject = new JSONObject();
//											jsonChildObject.put("id", proInfo.getUserId()+"");
//											jsonChildObject.put("name", proInfo.getUserName());
//											jsonChildObject.put("image", proInfo.getfBPhoto());
//											String currentprofile = "false";
//											if(currentUserId == proInfo.getUserId()){
//												currentprofile = "true";
//											}
//											jsonChildObject.put("currentprofile", currentprofile);
//											jsonArray.put(jsonChildObject);
//										}
//									}catch(Exception e){
//										e.printStackTrace();
//										StringWriter errors = new StringWriter();
//										e.printStackTrace(new PrintWriter(errors));
//										SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//									}
//								}
//								data.put("profileList", jsonArray);
//								data.put("result", "success");
//								
//							}else{
//								data.put("result", "failure");
//								data.put("msg", jsonresultData.getString("msg"));
//								
//							}
//						}else{
//							data.put("result", "failure");
//						}						
//					}else{
//						data.put("result", "failure");
//					}
//				}else{
//					data.put("result", "failure");
//				}
//			}else{
//				data.put("result", "failure");
//			}
//			data.put("tag", "deleteProfile");
//			resp.put("params",data);
//			returner.add(method+"deleteProfile", resp,"messageActivity");
//			returner.send();
//		}
//	}
	
	// for Switch profile from Setting
	private void switchProfile(String userId) throws JSONException, InterruptedException {
		
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());

		List<ProfileInfo> profileInfos = profileInfoGateway.getAllProfileInfo();

		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		ProfileInfo selectedProfileInfo = CacheData.getSelectedProfileInfo();
		
		if(Constant.DEBUG)  Log.d(TAG, "User id in switch profile " + userId);

		int currentUserId=0;
		if(!userId.equalsIgnoreCase("")){
			currentUserId = Integer.parseInt(userId);
		}else{
			if(selectedProfileInfo != null){
				currentUserId = selectedProfileInfo.getUserId();
			}
		}
		
		if(!userId.equalsIgnoreCase("")){
			edit.putString("userid", userId);
			edit.commit();
			
			CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(userId));
			CacheData.setUserId(Integer.valueOf(userId));
			
			if(info!=null){
				cache.updateCacheInfo(1000, info.getSsid(), info.getBssid(), info.getPwd(), info.getSecurity(), info.getSubscriber(), info.getDistributor(), userId, info.getHotspotName(), info.getHotspotPwd());
				if(Constant.DEBUG)  Log.d(TAG, "Updating User id in Cache DB " + userId);
			}else{
				cache.insertCacheInfo(1000, "", "", "", "", settingData.getString("subscriberid", ""), settingData.getString("distributorId", ""), userId, "", "");
				if(Constant.DEBUG)  Log.d(TAG, "Inserting User id in Cache DB " + userId);
			}
//			CacheGateway cache  = new CacheGateway(getApplicationContext());
//			CacheInfo info = cache.getCacheInfo(1000);
//			if (info != null) {
//				cache.updateCacheInfo(1000, info.getSsid(), info.getBssid(), info.getPwd(), info.getSecurity(), settingData.getString("subscriberid", ""), settingData.getString("distributorId", ""), userId+"");	
//			}
			
		}
		
		for(ProfileInfo profileInfo : profileInfos){
			try{
				if(profileInfo != null && !(profileInfo.getUserId() == currentUserId)){
					jsonObject = new JSONObject();
					jsonObject.put("id", profileInfo.getUserId()+"");
					jsonObject.put("name", profileInfo.getUserName());
					jsonObject.put("image", profileInfo.getfBId());
					jsonArray.put(jsonObject);
				}
			}catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			
			}
		}

		data.put("profileList", jsonArray);
		data.put("tag", "switchProfile");
		resp.put("params",data);
		returner.add(method+"switchProfile", resp,"messageActivity");
		returner.send();
	}	
	
	
	// for Edit Profile
	private void editProfile(String id,String name,String pass,String tag) throws JSONException, InterruptedException{
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		
		if(Constant.DEBUG)  Log.d(TAG , "editProfile() id: "+id+",name: "+name+",pass: "+pass+",tag: "+tag);
		
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
		
		if((!id.equalsIgnoreCase("") && id!=null) && (tag.equalsIgnoreCase("editname"))){
			//check id with pass, if ok - send request for change pwd, else send - failure
			ProfileInfo profileInfos = profileInfoGateway.getProfileInfo(id);
			if(profileInfos != null){
				if(profileInfos.getUserId() == Integer.parseInt(id) && profileInfos.getImagePwd().equalsIgnoreCase(pass)){
					if(Constant.DEBUG)  Log.d(TAG , "editProfile() UserId: "+profileInfos.getUserId()+", pwd: "+profileInfos.getImagePwd());
					profileInfoGateway.renameProfileInfo(Integer.parseInt(id), name);
					
					JSONArray jsonArray = new JSONArray();
					JSONObject jsonObject = null;
					
					List<ProfileInfo> profiles = profileInfoGateway.getAllProfileInfo();
					for(ProfileInfo proInfo : profiles){
						try{
							if(proInfo!=null){
								jsonObject = new JSONObject();
								jsonObject.put("id", proInfo.getUserId()+"");
								jsonObject.put("name", proInfo.getUserName());
								jsonObject.put("image", proInfo.getfBPhoto());
								jsonArray.put(jsonObject);
							}
						}catch(Exception e){
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
						}
					}
					data.put("profileList", jsonArray);
					data.put("result", "success");
					data.put("tag", "editname");
					resp.put("params",data);
					returner.add(method+"editProfile", resp,"messageActivity");
					returner.send();
				}else{
					data.put("result", "failure");
					data.put("tag", "editname");
					data.put("msg", this.getResources().getString(R.string.WRONG_PASSWORD));
					resp.put("params",data);
					returner.add(method+"editProfile", resp,"messageActivity");
					returner.send();
				}
			}else{
				data.put("result", "failure");
				data.put("tag", "editname");
				resp.put("params",data);
				returner.add(method+"editProfile", resp,"messageActivity");
				returner.send();
			}
			
			
		}else if((!id.equalsIgnoreCase("") && id!=null) &&(tag.equalsIgnoreCase("editpwd"))){
			//update pwd with id
			ProfileInfo profileInfos = profileInfoGateway.getProfileInfo(id);
			if(profileInfos != null){
				if(profileInfos.getUserId() == Integer.parseInt(id)){
					profileInfoGateway.setImageIdPassword(Integer.parseInt(id), pass);
				}
				
				JSONArray jsonArray = new JSONArray();
				JSONObject jsonObject = null;
				
				List<ProfileInfo> profiles = profileInfoGateway.getAllProfileInfo();
				for(ProfileInfo proInfo : profiles){
					try{
						if(proInfo!=null){
							jsonObject = new JSONObject();
							jsonObject.put("id", proInfo.getUserId()+"");
							jsonObject.put("name", proInfo.getUserName());
							jsonObject.put("image", proInfo.getfBPhoto());
							jsonArray.put(jsonObject);
						}
					}catch(Exception e){
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
					}
				}
				data.put("profileList", jsonArray);
				data.put("result", "success");
				data.put("tag", "editpwd");
				resp.put("params",data);
				returner.add(method+"editProfile", resp,"messageActivity");
				returner.send();
			}else{
				data.put("result", "failure");
				data.put("tag", "editpwd");
				resp.put("params",data);
				returner.add(method+"editProfile", resp,"messageActivity");
				returner.send();
			}
			
		}	
			
							
	}
	
	
	public static List<String> getImageIdsList(String imageIdPassword) {
		List<String> finalList = new ArrayList<String>();
		imageIdPassword = imageIdPassword.trim();
		finalList.add(imageIdPassword);

		String[] imageIdArray = Constant.IMAGE_IDS.split(",");

		List<String> imageIds = new ArrayList<String>();

		for(String imageId : imageIdArray){
			imageId = imageId.trim();
			imageIds.add(imageId);
		}

		imageIds.remove(imageIdPassword);
		for(int i=0; i<4; i++) {
			finalList.add(imageIds.get(i));
		}

		for(String imageId : finalList) {
			if(imageIds.contains(imageId)) {
				imageIds.remove(imageId);
			}
		}

		Collections.shuffle(imageIds);

		for(int i=0; i<7; i++) {
			finalList.add(imageIds.get(i));
		}

		Collections.shuffle(finalList);
		return finalList;
	}
	
	public void facebookLogout() {
		try {
			CacheData.setFacebook(new Facebook(Constant.APP_ID));
			SessionStore.restore(CacheData.getFacebook(), getApplicationContext());
			
			Facebook facebook = CacheData.getFacebook();
			if (Constant.DEBUG)	Log.d(TAG, "facebookLogout()  isSessionValid :" + CacheData.getFacebook().isSessionValid());
			if (facebook.isSessionValid()) {
				new Thread() {
					@Override
					public void run() {
						SessionStore.clear(getApplicationContext());
						int what = 1;
				        try {
				        	CacheData.getFacebook().logout(getApplicationContext());
				        	what = 0;
				        } catch (Exception ex) {
				        	ex.printStackTrace();
				        }
				        mHandler.sendMessage(mHandler.obtainMessage(what));
					}
				}.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if(Constant.DEBUG)  Log.d(TAG,"  Disconnected");
				if(Constant.DEBUG)  Log.d("mHandler()", "Disconnected logout failed");
			} else {
				if(Constant.DEBUG)  Log.d("mHandler()", "Disconnected from Facebook");
			}
		}
	};
}
