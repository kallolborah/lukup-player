package com.port.apps.epg;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.port.Channel;
import com.port.Port;
import com.port.api.apps.webservices.TVGuide;
import com.port.api.db.service.BouquetGateway;
import com.port.api.db.service.BouquetInfo;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ChannelInfo;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.util.CacheData;
import com.port.api.db.util.CommonUtil;
import com.port.api.epg.service.FeaturedInfo;
import com.port.api.epg.service.PackageInfos;
import com.port.api.network.Listener;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.api.webservices.Subscription;
//import com.port.api.db.util.CacheData;

public class Catalogue extends IntentService {
	
	private String TAG = "Catalogue";
	private static long preTimeStamp = 0;	
	
	public static List<ProgramInfo> EventList = new ArrayList<ProgramInfo>();
	public static List<ChannelInfo> ServiceList = new ArrayList<ChannelInfo>();
	public static List<BouquetInfo> BouquetList = new ArrayList<BouquetInfo>();
	
	public static HashMap<String,String> channelImageList = new HashMap<String,String>();
	public static HashMap<String,String> programImageList = new HashMap<String,String>();
	
	public static List<FeaturedInfo> featuredList = new ArrayList<FeaturedInfo>();
	public static List<PackageInfos> packageList = new ArrayList<PackageInfos>();
	public static ArrayList<HashMap<String,String>> featuredImageList = new ArrayList<HashMap<String,String>>();
	
	public static ArrayList<HashMap<String,String>> recommendedList = new ArrayList<HashMap<String,String>>();
	public static ArrayList<HashMap<String,String>> recommendedImageList = new ArrayList<HashMap<String,String>>();
	
	String functionData = "";
	String distributorId = "";
	String distributorPwd = "";
	String subscriberid = "";
	String dockID ="";
	String url = "";
	String type = "";
	String id = "";
	String pnetwork = "";

//	public static List<AdInfo> AdsList = new ArrayList<AdInfo>();
//	
//	private static final String DEVICETYPE = "TV";
//	public static String CurrentDate;
//	private AdInfo adInfo = null;
	
	public Catalogue(){
		super("Catalogue");		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();

		Bundle extras = intent.getExtras();
        String msg = "";
        
	    preTimeStamp = System.currentTimeMillis();
        
        if(extras != null){

	    	if(extras.containsKey("Params")){
	    		
	    		functionData = extras.getString("Params");
	    		if(Constant.DEBUG) Log.d(TAG,"onHandleIntent().functionData: "+functionData);
	    		JSONObject jsonObj;
				try {
					jsonObj = new JSONObject(functionData);
					if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
					
					if(jsonObj.has("subscriberid")){
						subscriberid = jsonObj.getString("subscriberid");
						CacheData.setSubscriberId(subscriberid);
		    		}
					if(jsonObj.has("distributorId")){
		    			distributorId = jsonObj.getString("distributorId");
		    			CacheData.setDistributorId(distributorId);
		    		}
					if(jsonObj.has("distributorPwd")){
						distributorPwd = jsonObj.getString("distributorPwd");
						CacheData.setDistributorPwd(distributorPwd);
		    		}
					if(jsonObj.has("url")){
						url = jsonObj.getString("url");
		    		}
					if(jsonObj.has("type")){
						type = jsonObj.getString("type");
		    		}
					if(jsonObj.has("Id")){
						id = jsonObj.getString("Id");
		    		}
					if(jsonObj.has("macid")){
						dockID = jsonObj.getString("macid");
					}
					if(jsonObj.has("network")){
						pnetwork = jsonObj.getString("network");
					}
					if(Constant.DEBUG) Log.d(TAG,"Network " + pnetwork);
				} catch (JSONException e) {
					e.printStackTrace();
				}
	    		
	    	}
	    	
	    	Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
	    	
	    	if(extras.containsKey("Method")){
	    		if(Constant.DEBUG) Log.d(TAG,"Method");
	    		
	    		String func = extras.getString("Method");
	    		
    			if(Constant.DEBUG) Log.d(TAG,"Method()."+func);
    			
    			if(func.equalsIgnoreCase("getFeatured")){
    				try {
						FeaturedDataDetails();
						String subscriberid = CacheData.getSubscriberId();
						if (subscriberid != null && !subscriberid.equalsIgnoreCase("")) {
								getSubscribedItems(subscriberid);
						}	
					} catch (JSONException e) {
						e.printStackTrace();
					}
    			}else if(func.equalsIgnoreCase("getVODUpdates")){
    				try {
						getVODServerData();
					} catch (JSONException e) {
						e.printStackTrace();
	    				StringWriter errors = new StringWriter();
	    				e.printStackTrace(new PrintWriter(errors));
	    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} 
    			}else if(func.equalsIgnoreCase("getEPG")){
    				if (Constant.DVB) {	//DVB middleware
						if (Constant.DEBUG) Log.d(TAG, "Not in DVB Module");
					} else {
//						try {
//							getEPGServerData();
//						} catch (JSONException e) {
//							e.printStackTrace();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					}
    			}else if(func.equalsIgnoreCase("portAppUpdate")){
					if(DownloadFile(url,"portapp.zip")){
    	        		if(Constant.model.equalsIgnoreCase("X1")){
    	        			unpackZip("\\system\\", "portapp.zip"); 
    	        		}else{
		    	        	//unzip downloaded app into path
		    	        	unpackZip("/", "portapp.zip"); //put correct path to which unzipped files should be put and downloaded zip file name
    	        		}
	    	        	//send message to Player
	    	        	try{
	    	        		returner.set(Listener.pname, pnetwork, "com.player.NotificationsService");
	    	        		data.put("message", "Software update. Reboot the Lukup Player."); //put this in Strings.xml
	        				sendResponse.put("params", data);
	        				returner.add("com.port.service.Catalogue.port-app-updates", sendResponse,"startService");
	        				returner.send();
		    	        } catch (JSONException e) {
		    	        	e.printStackTrace();
		    				StringWriter errors = new StringWriter();
		    				e.printStackTrace(new PrintWriter(errors));
		    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}     	        	
    	        	}
    			}else if(func.equalsIgnoreCase("portFirmwareUpdate")){
    				if(DownloadFile(url,"portfirmware.zip")){
    	        		if(Constant.model.equalsIgnoreCase("X1")){
    	        			unpackZip("\\system\\", "portfirmware.zip"); 
    	        		}else{
		    	        	//unzip downloaded firmware into path
		    	        	unpackZip("/", "portfirmware.zip"); //put correct path to which unzipped files should be put and downloaded zip file name
    	        		}
	    	        	//send message to Player    	        	
	    	        	try{
	    	        		returner.set(Listener.pname, pnetwork, "com.player.NotificationsService");
	    	        		data.put("message", "Software update. Reboot the Lukup Player."); //put this in Strings.xml
	        				sendResponse.put("params", data);
	        				returner.add("com.port.service.Catalogue.port-firmware-updates", sendResponse,"startService");
	        				returner.send();
		    	        } catch (JSONException e) {
		    	        	e.printStackTrace();
		    				StringWriter errors = new StringWriter();
		    				e.printStackTrace(new PrintWriter(errors));
		    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
    	        	}
    			}else if(func.equalsIgnoreCase("subscription")){
    				try {
    					if(!id.equalsIgnoreCase("")){
		    	        	String name = webSubscription(func, Integer.parseInt(id), type);
		    	        	
		    	        	//send message to Player
		    	        	try{
		    	        		returner.set(Listener.pname, pnetwork, "com.player.apps.GuideService");
		    	        		data.put("result", "success"); 
		    	        		data.put("id", id);
		    	        		data.put("type", type);
		        				sendResponse.put("params", data);
		        				returner.add("com.port.service.Catalogue.subscription", sendResponse,"startService");
		        				returner.send();
			    	        } catch (JSONException e) {
			    	        	e.printStackTrace();
			    				StringWriter errors = new StringWriter();
			    				e.printStackTrace(new PrintWriter(errors));
			    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
							}  
    					}
    	        	} catch (Exception e) {
    	        		e.printStackTrace();
	    				StringWriter errors = new StringWriter();
	    				e.printStackTrace(new PrintWriter(errors));
	    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
					} 
    			}else if(func.equalsIgnoreCase("unsubscription")){
    				try {
    					if(!id.equalsIgnoreCase("")){
		    	        	String name = webSubscription(func, Integer.parseInt(id), type);
		    	        	
		    	        	//send message to Player
		    	        	try{
		    	        		returner.set(Listener.pname, pnetwork, "com.player.apps.GuideService");
		    	        		data.put("result", "success"); 
		    	        		data.put("id", id);
		    	        		data.put("type", type);
		        				sendResponse.put("params", data);
		        				returner.add("com.port.service.Catalogue.unsubscription", sendResponse,"startService");
		        				returner.send();
			    	        } catch (JSONException e) {
			    	        	e.printStackTrace();
			    				StringWriter errors = new StringWriter();
			    				e.printStackTrace(new PrintWriter(errors));
			    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
							}  
    					}
    	        	} catch (Exception e) {
    	        		e.printStackTrace();
	    				StringWriter errors = new StringWriter();
	    				e.printStackTrace(new PrintWriter(errors));
	    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
					} 
    			}
	    		
	    	}
	    	
	    	
    		if (extras.containsKey("Title")) {
    			msg = extras.getString("Title");
//    			igetVODServerDataf (msg.equalsIgnoreCase("recommended")){
//    	        	try {
//    	        		if(Constant.DEBUG) Log.d(TAG, "Network Status: "+CommonUtil.isNetworkAvailable());
//						RecommendedDataDetails();
//						if(featuredList.size()<=0){
//							FeaturedDataDetails();
//						}
//						while(recommendedList.size()>0){
//							Intent home = new Intent();
//						    home.setAction("CATALOGUE");
//							home.putExtra("ShowHome","ok");
//							sendBroadcast(home);
//							break;
//						}
//    				} catch (Exception e) {
//    					e.printStackTrace();
//    					StringWriter errors = new StringWriter();
//    					e.printStackTrace(new PrintWriter(errors));
//    					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//    				}
//    	        } else 
//    	        	if (msg.equalsIgnoreCase("ads")){
//    	        	AdsData();
//    	        } else 
    	        	if (msg.equalsIgnoreCase("featured")){
    	        		
    	        		if (extras.containsKey("distributorId")) {
    	        			if(Constant.DEBUG)  Log.d(TAG+" : distributorId:" , extras.getString("distributorId"));
    	        			CacheData.setDistributorId(extras.getString("distributorId")); 
    	        		}
    	        		if (extras.containsKey("distributorPwd")) {
    	        			if(Constant.DEBUG)  Log.d(TAG+" : distributorPwd:" , extras.getString("distributorPwd"));
    	        			CacheData.setDistributorPwd(extras.getString("distributorPwd")); 
    	        		}
    	        		if (extras.containsKey("subscriberid")) {
    	        			if(Constant.DEBUG)  Log.d(TAG+" : subscriberid:" , extras.getString("subscriberid"));
    	        			CacheData.setSubscriberId(extras.getString("subscriberid")); 
    	        		}
    	        		
    	        	try {
						FeaturedDataDetails();
						
						String subscriberid = CacheData.getSubscriberId();
						if (subscriberid != null && !subscriberid.equalsIgnoreCase("")) {
							getSubscribedItems(subscriberid);
						}
					} catch (Exception e) {
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
					}
    	        } 
//    	        else if (msg.equalsIgnoreCase("port-app-updates")){
//    	        	//download port app
//    	        	if(DownloadFile(extras.getString("Url"),"portapp.zip")){
//    	        		if(Constant.model.equalsIgnoreCase("X1")){
//    	        			unpackZip("/system/", "portapp.zip"); 
//    	        		}else{
//		    	        	//unzip downloaded app into path
//		    	        	unpackZip("/", "portapp.zip"); //put correct path to which unzipped files should be put and downloaded zip file name
//    	        		}
//	    	        	//send message to Player
//	    	        	try{
//	    	        		returner.set("PLAYER", "BT", "com.player.NotificationsService");
//	    	        		data.put("message", "Software update. Dock reboot required."); //put this in Strings.xml
//	        				sendResponse.put("params", data);
//	        				returner.add("com.port.service.Catalogue.port-app-updates", sendResponse,"startService");
//	        				returner.send();
//		    	        } catch (JSONException e) {
//		    	        	e.printStackTrace();
//		    				StringWriter errors = new StringWriter();
//		    				e.printStackTrace(new PrintWriter(errors));
//		    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}     	        	
//    	        	}
//    	        } else if (msg.equalsIgnoreCase("port-firmware-updates")){
//    	        	//download port firmware
//    	        	if(DownloadFile(extras.getString("Url"),"portfirmware.zip")){
//    	        		if(Constant.model.equalsIgnoreCase("X1")){
//    	        			unpackZip("/system/", "portfirmware.zip"); 
//    	        		}else{
//		    	        	//unzip downloaded firmware into path
//		    	        	unpackZip("/", "portfirmware.zip"); //put correct path to which unzipped files should be put and downloaded zip file name
//    	        		}
//	    	        	//send message to Player    	        	
//	    	        	try{
//	    	        		returner.set("PLAYER", "BT", "com.player.NotificationsService");
//	    	        		data.put("message", "Software update. Dock reboot required."); //put this in Strings.xml
//	        				sendResponse.put("params", data);
//	        				returner.add("com.port.service.Catalogue.port-firmware-updates", sendResponse,"startService");
//	        				returner.send();
//		    	        } catch (JSONException e) {
//		    	        	e.printStackTrace();
//		    				StringWriter errors = new StringWriter();
//		    				e.printStackTrace(new PrintWriter(errors));
//		    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} 
//    	        	}
//    	        	
//    	        } else if (msg.equalsIgnoreCase("player-app-updates")){
//    	        	if(Constant.DEBUG)  Log.d(TAG , "Received Player app update ");
//    	        	//download player app update
//    	        	if(DownloadFile(extras.getString("Url"),"playerapp.zip")){
//    	        	
//	    	        	//send app update to Player    	        	
//	    	        	Intent appPush = new Intent(this, com.port.api.network.bt.BTObjectPush.class);					
//	    	        	appPush.putExtra("FileName", "playerapp.zip");
//	    	        	appPush.putExtra("UpdateType","com.port.service.Catalogue.player-app-updates");
//	    				startService(appPush);
//    	        	}
//    	        	
//    	        } else if (msg.equalsIgnoreCase("player-firmware-updates")){
//    	        	if(Constant.DEBUG)  Log.d(TAG , "Received Player firmware update ");
//    	        	//download player firmware update
//    	        	if(DownloadFile(extras.getString("Url"),"playerfirmware.zip")){
//    	        	
//	    	        	//send firmware update to Player
//	    	        	Intent appPush = new Intent(this, com.port.api.network.bt.BTObjectPush.class);					
//	    	        	appPush.putExtra("FileName", "playerfirmware.zip");
//	    	        	appPush.putExtra("UpdateType","com.port.service.Catalogue.player-firmware-updates");
//	    				startService(appPush);
//    	        	}
//    	        	
//    	        } else //Uncommented by tomesh
    	        if (msg.equalsIgnoreCase("epg-updates")){
    	        	try {
    	        		if (Constant.DVB) {	//DVB middleware
							if (Constant.DEBUG) Log.d(TAG, "In DVB Module");
						} else {
//							getEPGServerData();
						}
    	        		if(Constant.DEBUG)  Log.d("epg-updates" , "Inside Live Tv Download ");
					} catch (Exception e) {
						e.printStackTrace();
	    				StringWriter errors = new StringWriter();
	    				e.printStackTrace(new PrintWriter(errors));
	    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
					}
    	        } else if (msg.equalsIgnoreCase("vod-updates")){
    	        	try {
						getVODServerData();
					} catch (JSONException e) {
						e.printStackTrace();
	    				StringWriter errors = new StringWriter();
	    				e.printStackTrace(new PrintWriter(errors));
	    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	        } 
//    	        else if (msg.equalsIgnoreCase("subscription")){
//    	        	//send message to Player
//    	        	try {
//	    	        	String type = extras.getString("type");
//	    	        	String id = extras.getString("Id");
//	    	        	String name = webSubscription(msg, Integer.parseInt(id), type);
//	    	        	
//	    	        	//send message to Player
//	    	        	try{
//	    	        		returner.set("PLAYER", "BT", "com.player.apps.GuideService");
//	    	        		data.put("result", "success"); 
//	    	        		data.put("id", id);
//	    	        		data.put("type", type);
//	        				sendResponse.put("params", data);
//	        				returner.add("com.port.service.Catalogue.subscription", sendResponse,"startService");
//	        				returner.send();
//		    	        } catch (JSONException e) {
//		    	        	e.printStackTrace();
//		    				StringWriter errors = new StringWriter();
//		    				e.printStackTrace(new PrintWriter(errors));
//		    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
//						}  
//	    	        	
//    	        	} catch (Exception e) {
//    	        		e.printStackTrace();
//	    				StringWriter errors = new StringWriter();
//	    				e.printStackTrace(new PrintWriter(errors));
//	    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
//					} 
//    	        } else if (msg.equalsIgnoreCase("unsubscription")){
//    	        	//send message to Player
//    	        	try {
//	    	        	String type = extras.getString("type");
//	    	        	String id = extras.getString("Id");
//	    	        	String name = webSubscription(msg, Integer.parseInt(id), type);
//	    	        	
//	    	        	//send message to Player
//	    	        	try{
//	    	        		returner.set("PLAYER", "BT", "com.player.apps.GuideService");
//	    	        		data.put("result", "success"); 
//	    	        		data.put("id", id);
//	    	        		data.put("type", type);
//	        				sendResponse.put("params", data);
//	        				returner.add("com.port.service.Catalogue.unsubscription", sendResponse,"startService");
//	        				returner.send();
//		    	        } catch (JSONException e) {
//		    	        	e.printStackTrace();
//		    				StringWriter errors = new StringWriter();
//		    				e.printStackTrace(new PrintWriter(errors));
//		    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
//						}  
//	    	        	
//    	        	} catch (Exception e) {
//    	        		e.printStackTrace();
//	    				StringWriter errors = new StringWriter();
//	    				e.printStackTrace(new PrintWriter(errors));
//	    				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
//					} 
//    	        } 
    		}
    	}       
	}    
	
	private String webSubscription(String tag,int id,String type){
		String title = "";

		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		try{
			if(id > 0){
				if(tag.equalsIgnoreCase("subscription")){
					if(type.equalsIgnoreCase("event")){
						statusGateway.insertStatusInfo(Constant.GuestUserId,0, 0, id, 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
//						CacheData.setStatusGateway(statusGateway);
						ProgramInfo info = programGateway.getProgramInfoByUniqueId(id+"");
						title = info.getEventName();
					}else if(type.equalsIgnoreCase("service")){
						statusGateway.insertStatusInfo(Constant.GuestUserId, id, 0, 0, 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
//						CacheData.setStatusGateway(statusGateway);
						ChannelInfo info = channelGateway.getServiceInfoByServiceId(id);
						title = info.getChannelName();
					}else{
						title = getSubscribedItems(tag, id,type);
					}
				}else{
					if(type.equalsIgnoreCase("event")){
						statusGateway.deleteStatusInfoById(id, "event",9);
						ProgramInfo info = programGateway.getProgramInfoByUniqueId(id+"");
						title = info.getEventName();
					}else if(type.equalsIgnoreCase("service")){
						statusGateway.deleteStatusInfoById(id, "service",9);
						ChannelInfo info = channelGateway.getServiceInfoByServiceId(id);
						title = info.getChannelName();
					}else{
						title = getSubscribedItems(tag, id,type);
					}
				}
			}
			return title;
		}catch (Exception e) {
    		e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
		return title;
	}
	
	private String getSubscribedItems(String tag,int id,String type){
		String title = "";
		try{	
			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
			
			String pgmList = "";
			String chlList = "";
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < Catalogue.packageList.size(); i++) {
				if(Catalogue.packageList.get(i).getPackageId().equalsIgnoreCase(id+"")){
					title = Catalogue.packageList.get(i).getPackageName();
					pgmList = Catalogue.packageList.get(i).getPackagePrograms();
					chlList = Catalogue.packageList.get(i).getPackageChannels();
				}
			}
			
			if(tag.equalsIgnoreCase("subscription")){
				if(pgmList.length()>0){
					list = com.port.api.util.CommonUtil.Tokenizer(pgmList, ",");
					for (int j = 0; j < list.size(); j++) {
						if(Constant.DEBUG) Log.i(TAG, j+" Event --- "+list.get(j));
						statusGateway.insertStatusInfo(Constant.GuestUserId, 0, 0, Integer.parseInt(list.get(j)), 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
//						CacheData.setStatusGateway(statusGateway);
					}
				}
				
				if(chlList.length()>0){
					list = com.port.api.util.CommonUtil.Tokenizer(chlList, ",");
					for (int j = 0; j < list.size(); j++) {
						if(Constant.DEBUG) Log.i(TAG, j+" Channel --- "+list.get(j));
						statusGateway.insertStatusInfo(Constant.GuestUserId,Integer.parseInt(list.get(j)), 0, 0, 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
//						CacheData.setStatusGateway(statusGateway);
					}
				}
			}else{
				if(pgmList.length()>0){
					list = com.port.api.util.CommonUtil.Tokenizer(pgmList, ",");
					for (int j = 0; j < list.size(); j++) {
						if(Constant.DEBUG) Log.i(TAG, j+" Event --- "+list.get(j));
						statusGateway.deleteStatusInfoByUniqueId(Integer.parseInt(list.get(j)), "event",9);
					}
				}
				
				if(chlList.length()>0){
					list = com.port.api.util.CommonUtil.Tokenizer(chlList, ",");
					for (int j = 0; j < list.size(); j++) {
						if(Constant.DEBUG) Log.i(TAG, j+" Channel --- "+list.get(j));
						statusGateway.deleteStatusInfoById(Integer.parseInt(list.get(j)), "service",9);
					}
				}
			}
			return title;
		}catch (Exception e) {
        	e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
        }
		return title;
	}
	
	
	private boolean DownloadFile(String Url,String name){
		int count;
		if(CommonUtil.isNetworkAvailable()){
			try {
				URL url = new URL(Url);
				URLConnection conexion = url.openConnection();
				conexion.connect();
	
				InputStream input = new BufferedInputStream(url.openStream());
				FileOutputStream filetowrite = getApplicationContext().openFileOutput(name, getApplicationContext().MODE_WORLD_READABLE);
							
				byte data[] = new byte[1024];
				long total = 0;
				while ((count = input.read(data)) != -1) {
					total += count;
					filetowrite.write(data, 0, count);
				}
	
				filetowrite.flush();
				filetowrite.close();
				input.close();
				
			} catch (Exception e) {
	    		e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
				return false;
			}
			return true;
		}else{
			return false;
		}
	}
	
	private boolean unpackZip(String path, String zipname){      
//		if(Constant.DEBUG)  Log.d(TAG,"Start unpackZip().");
	     InputStream is;
	     ZipInputStream zis;
	     try {
	         String filename;
	         
	         is = new FileInputStream(new File(getFilesDir(),zipname).getAbsoluteFile());
	         zis = new ZipInputStream(new BufferedInputStream(is));          
	         
	         ZipEntry ze;
	         byte[] buffer = new byte[1024];
	         int count;

	         while ((ze = zis.getNextEntry()) != null){
	             filename = ze.getName();
	             if (ze.isDirectory()) {
	                File fmd = new File(path + filename);
	                fmd.mkdirs();
	                continue;
	             }

	             FileOutputStream fout = new FileOutputStream(path + filename);
	             while ((count = zis.read(buffer)) != -1){
	                 fout.write(buffer, 0, count);             
	             }
	             fout.close();               
	             zis.closeEntry();
	         }
	         zis.close();
	         
	         new File(getFilesDir(),zipname).getAbsoluteFile().delete();
	     } 
	     catch(IOException e) {
	    	e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
			return false;
	     } 
	    return true;
	}

	
    private void getVODServerData()  throws JSONException, InterruptedException {
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		BouquetGateway bouquetGateway =new BouquetGateway(Port.c.getApplicationContext()) ;

		JSONObject jsonBouquetData = TVGuide.getExternalBouquetAndServiceJson();
		if(jsonBouquetData != null){
			JSONArray jsonArray = jsonBouquetData.getJSONArray("data");
			if(jsonArray != null && jsonArray.length() > 0) {
				int Bouquet_id = 0;
				String bouquet_Id = "";
				String Bouquet_name = "";
				String bouquetCategory;
				
//				programGateway.deleteEventInfoByType("live");

				for(int i=0; i<jsonArray.length(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					if(jsonObject != null) {
						Bouquet_name = jsonObject.getString("bouquet");
						bouquetCategory =  jsonObject.getString("bouquet");
						bouquet_Id = jsonObject.getString("bouquetid");
						if(Bouquet_name != null && !(Bouquet_name.trim().equalsIgnoreCase(""))) {
//							BouquetGateway bouquetGateway =new BouquetGateway(Port.c.getApplicationContext()) ;
							BouquetInfo bouquetInfo = bouquetGateway.getBouquetInfoByName(Bouquet_name.trim());
							
							if (Constant.DVB) {	//DVB middleware
								if (Constant.DEBUG) Log.d(TAG, "DVB Module");
								if(bouquetInfo == null) {
									Bouquet_id = bouquetGateway.insertDvbBouquetInfo(bouquet_Id,Bouquet_name, bouquetCategory, 0, 0, 0,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
								} else if(bouquetInfo != null && bouquetInfo.getBouquetName().trim().equalsIgnoreCase(Bouquet_name.trim())) {
									Bouquet_id = bouquetGateway.updateDvbBouquetInfo(bouquetInfo.getBouquetId(), Bouquet_name, bouquetCategory, 0, 0, 0,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
								}
							} else {
								if(bouquetInfo == null) {
									Bouquet_id = bouquetGateway.insertBouquetInfo(Bouquet_name, bouquetCategory,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
									if (Constant.DEBUG) Log.d(TAG, "Bouquet_id: "+Bouquet_id+", bouquetName: "+Bouquet_name);
								} else if(bouquetInfo != null && bouquetInfo.getBouquetName().trim().equalsIgnoreCase(Bouquet_name.trim())) {
									Bouquet_id = bouquetGateway.updateBouquetInfo(bouquetInfo.getBouquetId(), Bouquet_name, bouquetCategory,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
								}
							}

							if(Constant.DEBUG)  Log.d(TAG,"Bouquets : Bouquet_id: "+Bouquet_id+", Bouquet_name: "+Bouquet_name+", bouquetCategory: "+bouquetCategory);
						}
					}
					JSONArray jsonArrayForChannel = jsonObject.getJSONArray("list");
					if(Constant.DEBUG)  Log.d(TAG,"EPG service jsonArray size is :"+jsonArrayForChannel.length());
					if(jsonArrayForChannel != null && jsonArrayForChannel.length() > 0) {
						int Service_id;
						String Channel_name;
						float Price;
						String Expiry_date;
						String Maturity;
						String Description;
						String Pricing_model;
						String Language;
						String Channel_logo = "";
						String Category = "";
						String Type;
						for(int j=0; j<jsonArrayForChannel.length(); j++) {
							JSONObject jsonObjectForChannel = jsonArrayForChannel.getJSONObject(j);
							if(jsonObjectForChannel != null) {
								Channel_name = jsonObjectForChannel.getString("name");		
								Type = jsonObjectForChannel.getString("type");
								Expiry_date = jsonObjectForChannel.getString("expirydate");
								Service_id = Integer.parseInt(jsonObjectForChannel.getString("serviceid"));	
								Price = Float.parseFloat(jsonObjectForChannel.getString("price"));
								Language = jsonObjectForChannel.getString("language");
								Maturity = jsonObjectForChannel.getString("maturity");
								Description = jsonObjectForChannel.getString("description");
								Pricing_model = jsonObjectForChannel.getString("pricingmodel");
								if(Pricing_model == null || Pricing_model.trim().equals("")){
									Pricing_model = "PPC";
								}
								Channel_logo = jsonObjectForChannel.getString("logo");
								if(jsonObjectForChannel.has("channelgenre")){
									Category = jsonObjectForChannel.getString("channelgenre");
								}
								if(Service_id >-1) {
									ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(Service_id);//,"vod");
									
									if (Constant.DVB) {	//DVB middleware
										if (Constant.DEBUG) Log.d(TAG, "DVB Module");
										if(serviceInfo == null) {//Chnages don for ChannelURL Column by tomesh
											channelGateway.insertDvbChannelInfo(Service_id, 0, 0, 0, 0, Type, Channel_name, Bouquet_id, 0, Description, Maturity, Price, Pricing_model, Expiry_date, 0, 0, Category, Channel_logo, 0, 0, com.port.api.util.CommonUtil.getDate(),preTimeStamp,"");
										} else if(serviceInfo != null && serviceInfo.getServiceId() == Service_id) {
											channelGateway.updateDvbChannelInfo(Service_id, 0, 0, 0, 0, Type, Channel_name, Bouquet_id, 0, Description, Maturity, Price, Pricing_model, Expiry_date, 0, 0, Category, Channel_logo, 0, 0, com.port.api.util.CommonUtil.getDate(),preTimeStamp,"");
										}
									} else {
										if(serviceInfo == null) {//Chnages don for ChannelURL Column by tomesh
											channelGateway.insertChannelInfo(Service_id, 0, "", Type, Channel_name, Bouquet_id, 0, Description, Maturity, Price, Pricing_model, Expiry_date, 0, 0, Category, Channel_logo,com.port.api.util.CommonUtil.getDate(),preTimeStamp,"");
										} else if(serviceInfo != null && serviceInfo.getServiceId() == Service_id) {
											channelGateway.updateChannelInfo(Service_id, 0, "", Type, Channel_name, Bouquet_id, 0, Description, Maturity, Price, Pricing_model, Expiry_date, 0, 0, Category, Channel_logo,com.port.api.util.CommonUtil.getDate(),preTimeStamp,"");
										}
									}

									if(Constant.DEBUG)  Log.d(TAG,"Service :Service_id: "+Service_id+", type: "+Type+", bouquetCount: "+Bouquet_id+", Price: "+Price+", Pricing_model: "+Pricing_model+", Category: "+Category);
								}
								try{
									insertProgramData(Service_id,Pricing_model,Maturity,Language,Type,Bouquet_id,Channel_name);
								}
								catch(Exception e){
									e.printStackTrace();
									StringWriter errors = new StringWriter();
									e.printStackTrace(new PrintWriter(errors));
									SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
								}
							}
						}
					}
				}
				List<ProgramInfo> programIdList = null;
				List<ChannelInfo> channelIdList = channelGateway.getAllServiceInfoByTimeStamp(preTimeStamp,"live");
				for(ChannelInfo serviceInfo : channelIdList){
					programIdList = programGateway.getAllExternalEventByServiceId(serviceInfo.getServiceId());
					if(programIdList != null){
						for(ProgramInfo proInfo : programIdList){
							statusGateway.deleteStatusInfoById(proInfo.getEventId(),"event");								
//							Home.mMemoryCache.remove("P_"+proInfo.getProgramId());								
						}
						if(Constant.DEBUG)Log.d(TAG, "Deleting programs before time : " + preTimeStamp);
						programGateway.deleteEventInfoByDate(serviceInfo.getServiceId(),preTimeStamp); 
					}
					statusGateway.deleteStatusInfoById(serviceInfo.getServiceId(),"service");
//					Port.mMemoryCache.remove("C_"+serviceInfo.getServiceId());
				}
				if(Constant.DEBUG)Log.d(TAG, "Deleting all live channels before time : " + preTimeStamp);
				channelGateway.deleteServiceInfoByDate(preTimeStamp,"live");
			
			
				// Remove from Status, DB and Cache
				programIdList = null;
				channelIdList = channelGateway.getAllServiceInfoByTimeStamp(preTimeStamp,"vod");
				for(ChannelInfo serviceInfo : channelIdList){
					programIdList = programGateway.getAllExternalEventByServiceId(serviceInfo.getServiceId());
					if(programIdList != null){
						for(ProgramInfo proInfo : programIdList){
							statusGateway.deleteStatusInfoById(proInfo.getEventId(),"event");							
    						Port.mMemoryCache.remove("P_"+proInfo.getProgramId());	    						
						}
						programGateway.deleteEventInfoByDate(serviceInfo.getServiceId(),preTimeStamp);
					}
					statusGateway.deleteStatusInfoById(serviceInfo.getServiceId(),"service");
					Port.mMemoryCache.remove("C_"+serviceInfo.getServiceId());
				}
				channelGateway.deleteServiceInfoByDate(preTimeStamp,"vod");
				
				clearBouquet();
				storeInCache();
				
				getAllServiceAndEventImages("vod");				
				
			    Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
		    	returner.set(Listener.pname, pnetwork, "com.player.apps.GuideService");	
				JSONObject resp = new JSONObject();
				JSONObject data = new JSONObject();
				data.put("update", "VOD");
				resp.put("params", data);
				returner.add("com.port.service.Catalogue.epg-update", resp,"startService");
				returner.send();
				
			}
		}
		
	}
    
    
    private void insertProgramData(int Service_id,String Pricing_model,String Maturity,String Language,String type,int bouquetid,String chlName)throws JSONException {
		JSONObject externalEventJson = null;
		externalEventJson = TVGuide.getExternalEventJson(Service_id);
		if(externalEventJson != null) {
			JSONObject json = externalEventJson.getJSONObject("json");
			if(json != null){
//				if(Constant.DEBUG)  Log.d(TAG,"External event json response is : "+externalEventJson.toString());
				String result = json.getString("result");
				if(result != null && result.trim().equalsIgnoreCase("success")){
					JSONArray jsonArray = json.getJSONArray("data");
					if(jsonArray != null && jsonArray.length() > 0) {
//						if(Constant.DEBUG)  Log.d(TAG,"external event jsonArray size is :"+jsonArray.length());

						String Event_name = "";
						String Description = "";
						String language = "";
						String Date_added = "";
						String Expiry_date = "";
						String Start_time = "";
						int programid = 0;
						String Image = "";
						String Duration = "";
						String Rating = "";
						int Ranking = 0;
						float Price = 0;
						String Event_src  = "";
						String Genre = "";
						String Actors = "";
						String Director = "";
						String Production = "";
						String Musicdirector = "";
						String eventCategory = "";
						String summary = "";
						int collectionid = 0;
						String collectionName = "";
						
						for(int i=0; i<jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							Event_name = jsonObject.getString("name");
							if(jsonObject.has("description")){
								Description = jsonObject.getString("description");
							}
							
							if(jsonObject.has("summary")){
								summary = jsonObject.getString("summary");
							}
							
							if(jsonObject.has("language")){
								language = jsonObject.getString("language");
								if(language == null || language.equalsIgnoreCase("")){
									language = Language;
								}
							}

							if(jsonObject.has("rating")){
								if(jsonObject.getString("rating") != null && !(jsonObject.getString("rating").trim().equals(""))){
									Rating = jsonObject.getString("rating");
								}
							}

							if(jsonObject.has("ranking")){
								String Rankings = jsonObject.getString("ranking");
								if(Rankings != null && !(Rankings.trim().equals(""))){
									Ranking = Integer.parseInt(Rankings);	
								}
							}
							
							if(jsonObject.has("uniqueid")){
								programid = Integer.parseInt(jsonObject.getString("uniqueid"));
							}
							
							if(jsonObject.has("releasedate")){
								Date_added = jsonObject.getString("releasedate");
							}
							if(jsonObject.has("expirydate")){
								Expiry_date = jsonObject.getString("expirydate");
							}

							Image = jsonObject.getString("image");
							Duration = jsonObject.getString("duration");

							if(jsonObject.has("price")){
								String string = jsonObject.getString("price");
								if(string != null && !(string.trim().equals(""))){
									Price = Float.parseFloat(string);	
								}
							}

							if(jsonObject.has("source")){
								Event_src  = jsonObject.getString("source");
							}

							if(jsonObject.has("genre")){
								Genre = jsonObject.getString("genre");
							}

							if(jsonObject.has("actors")){
								Actors = jsonObject.getString("actors");
							}
							
							if(jsonObject.has("director")){
								Director = jsonObject.getString("director");
							}

							if(jsonObject.has("production")){
								Production = jsonObject.getString("production");
							}

							if(jsonObject.has("musicdirector")){
								Musicdirector = jsonObject.getString("musicdirector");
							}
							if(jsonObject.has("category")){
								eventCategory = jsonObject.getString("category");
							}
							
							if(jsonObject.has("collectionId")){
								String ids = jsonObject.getString("collectionId");
								if(ids != null && !(ids.trim().equals(""))){
									collectionid = Integer.parseInt(ids);	
								}
							}
							
							if(jsonObject.has("collectionName")){
								collectionName = jsonObject.getString("collectionName");
							}
							if(jsonObject.has("starttime")){
								Start_time = jsonObject.getString("starttime");
							}

							int eventId = 0;
							if(Service_id > -1) {
								ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
								if(programid > -1 ) {
									ProgramInfo eventIfo = programGateway.getExternalEventInfoByBoth(Service_id, programid);
									if (Constant.DVB) {	//DVB middleware
										if (Constant.DEBUG) Log.d(TAG, "DVB Module");
										if(eventIfo == null) {
											eventId = (int) programGateway.insertDvbProgramInfo(Event_src , type, Service_id, Genre, Price, Pricing_model, Expiry_date, Date_added, Description, Maturity, Image, Ranking, Actors, Director, Musicdirector, Production,"", Start_time, Duration, Rating, language, Event_name, eventCategory, programid,summary,bouquetid,chlName,collectionid,collectionName, 0, 0,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
										} else {
											if(eventIfo.getChannelServiceId() == Service_id && eventIfo.getProgramId() == programid) {
												eventId = programGateway.updateDvbProgramInfo(eventIfo.getEventId(),Event_src , type, Service_id, Genre, Price, Pricing_model, Expiry_date, Date_added, Description, Maturity, Image, Ranking, Actors, Director, Musicdirector, Production,"", Start_time, Duration, Rating, language, Event_name, eventCategory, programid,summary,bouquetid,chlName,collectionid,collectionName, 0, 0,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
											}
										}
									} else {
									
										if(eventIfo == null) {
											eventId = (int) programGateway.insertProgramInfo(Event_src , type, Service_id, Genre, Price, Pricing_model, Expiry_date, Date_added, Description, Maturity, Image, Ranking, Actors, Director, Musicdirector, Production, Start_time, Duration, Rating, language, Event_name, eventCategory, programid,summary,bouquetid,chlName,collectionid,collectionName,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
										} else {
											if(eventIfo.getChannelServiceId() == Service_id && eventIfo.getProgramId() == programid) {
												eventId = programGateway.updateProgramInfo(eventIfo.getEventId(),Event_src , type, Service_id, Genre, Price, Pricing_model, Expiry_date, Date_added, Description, Maturity, Image, Ranking, Actors, Director, Musicdirector, Production, Start_time, Duration, Rating, language, Event_name, eventCategory, programid,summary,bouquetid,chlName,collectionid,collectionName,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
											}
										}
									}
									if(Constant.DEBUG)  Log.d(TAG,"Event : eventId: "+eventId+", Type: "+type+", Service_id: "+ Service_id+", Price: "+Price+", Pricing_model: "+Pricing_model+", Expiry_date: "+Expiry_date+", Date_added: "+Date_added+", Image: "+Image+", Start_time: "+Start_time+", eventCategory: "+eventCategory+", Unique: "+programid);
								}
							}
						}
					}
				}
			}
		}
	}
    
    
    private void getEPGServerData()  throws JSONException, InterruptedException {
//		StatusGateway statusGateway = CacheData.getStatusGateway();
//		if(statusGateway == null){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				statusGateway = new StatusGateway(CacheData.getDatabase());
//				CacheData.setStatusGateway(statusGateway);
//			}
//		}
    	Log.e(TAG, "getEPGServerData============");
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		BouquetGateway bouquetGateway =new BouquetGateway(Port.c.getApplicationContext()) ;
		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		
		JSONObject jsonObjectForLiveTvEPGData = TVGuide.getLiveEPGJsonData();
		if(jsonObjectForLiveTvEPGData != null){
			JSONObject jsonObjectForLiveTvSchedule = jsonObjectForLiveTvEPGData.getJSONObject("Schedule");
			String Network = jsonObjectForLiveTvEPGData.getString("operatorname");
			if(jsonObjectForLiveTvSchedule != null){			
				
				JSONArray jsonArrayForLiveTvChannel = jsonObjectForLiveTvSchedule.getJSONArray("channel");
				if(jsonArrayForLiveTvChannel != null && jsonArrayForLiveTvChannel.length() > 0){
					String Bouquet_name = "";
					int Service_id=-1;
					String Channel_name = "";
					String Event_name = "";
					String Description = "";
					String eventStartTime = "";
					String endTimeAndEndDate = "";
					String Duration = "";
					String source="";
					float price=0;
					int Bouquet_id = 0;
					int Event_id = 0;
					
					int collectionid = 0;
					String collectionName = "";
					
					//remove all programs for all live channels
					programGateway.deleteEventInfoByType("live");
					
					for(int i=0; i<jsonArrayForLiveTvChannel.length(); i++) {
						JSONObject jsonObjectForLiveChannel = jsonArrayForLiveTvChannel.getJSONObject(i);
						if(jsonObjectForLiveChannel != null){

							Bouquet_name = jsonObjectForLiveChannel.getString("bouquet");
							if (Constant.DEBUG) Log.d(TAG, "getEPGServerData() "+ Bouquet_name);
							if(Bouquet_name != null && !(Bouquet_name.trim().equalsIgnoreCase(""))) {
								BouquetInfo bouquetInfo = bouquetGateway.getBouquetInfoByName(Bouquet_name.trim());
								if(bouquetInfo == null) {
									Bouquet_id = bouquetGateway.insertBouquetInfo(Bouquet_name, Bouquet_name,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
								} else if(bouquetInfo != null && bouquetInfo.getBouquetName().trim().equalsIgnoreCase(Bouquet_name.trim())) {
									Bouquet_id = bouquetGateway.updateBouquetInfo(bouquetInfo.getBouquetId(), Bouquet_name, Bouquet_name,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
								}

//								if(Constant.DEBUG)  Log.d(TAG,"Live Bouquets : Bouquet_id: "+Bouquet_id+", Bouquet_name: "+Bouquet_name);
							}
							
							if(jsonObjectForLiveChannel.has("lcn")){
								Service_id = Integer.parseInt(jsonObjectForLiveChannel.getString("lcn"));
							}
							Channel_name = jsonObjectForLiveChannel.getString("name");	
							String channelgenre = jsonObjectForLiveChannel.getString("channelgenre");
							String Channel_logo = "";
							
							if(jsonObjectForLiveChannel.has("logo")){
								Channel_logo = jsonObjectForLiveChannel.getString("logo");
							}
							//Added by  Tomesh
							if(jsonObjectForLiveChannel.has("source")){
								source = jsonObjectForLiveChannel.getString("source");
							}
							if(jsonObjectForLiveChannel.has("prepaid_price")){
								price = Float.parseFloat(jsonObjectForLiveChannel.getString("prepaid_price"));
							}
							if(Service_id >-1) {
								ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(Service_id,"live");
								if(serviceInfo == null) {
									if(Constant.DEBUG)Log.d(TAG, "Inserting channel with service id : " + Service_id);
									channelGateway.insertChannelInfo(Service_id, 0, Network, "live", Channel_name, Bouquet_id, 0, "", "", price, "", "", 0, 0, channelgenre, Channel_logo,com.port.api.util.CommonUtil.getDate(),preTimeStamp,source);
								} else if(serviceInfo != null && serviceInfo.getServiceId() == Service_id) {
									if(Constant.DEBUG)Log.d(TAG, "Updating channel with service id : " + Service_id);
									channelGateway.updateChannelInfo(Service_id, 0, Network, "live", Channel_name, Bouquet_id, 0, "", "", price, "", "", 0, 0, channelgenre, Channel_logo,com.port.api.util.CommonUtil.getDate(),preTimeStamp,source);
								}
							
								if(jsonObjectForLiveChannel.has("programme")){
									JSONArray jsonArrayForLiveTvEvent = jsonObjectForLiveChannel.getJSONArray("programme");
									
									if(jsonArrayForLiveTvEvent != null && jsonArrayForLiveTvEvent.length() > 0){
										for(int j=0; j<jsonArrayForLiveTvEvent.length(); j++) {
											JSONObject jsonObjectForLiveEvent = jsonArrayForLiveTvEvent.getJSONObject(j);
											if(jsonObjectForLiveEvent != null){
												Event_name = com.port.api.util.CommonUtil.getDataFromJSON(jsonObjectForLiveEvent,"name");
	//											Description = CommonUtil.getDataFromJSON(jsonObjectForLiveEvent,"episodesynopsis");
												eventStartTime = com.port.api.util.CommonUtil.getDataFromJSON(jsonObjectForLiveEvent,"start");
												endTimeAndEndDate = com.port.api.util.CommonUtil.getDataFromJSON(jsonObjectForLiveEvent,"stop");
	//											Duration = getEventDuration(eventStartTime,endTimeAndEndDate);
												String Date_added = com.port.api.util.CommonUtil.getDataFromJSON(jsonObjectForLiveEvent, "startdate");
												String time = com.port.api.util.CommonUtil.getDataFromJSON(jsonObjectForLiveEvent,"start");
												String Expiry_date = com.port.api.util.CommonUtil.getDataFromJSON(jsonObjectForLiveEvent,"stopdate");
	//											String Image = CommonUtil.getDataFromJSON(jsonObjectForLiveEvent,"image");
												String Genre = com.port.api.util.CommonUtil.getDataFromJSON(jsonObjectForLiveEvent,"genre");
												String year = "";
												String month = "";
												String day = "";
	//											String Start_time = time+"-"+Date_added;		//20:30-20140927
												String Start_time = com.port.api.util.CommonUtil.dbTimeFormat(time+"-"+Date_added);	// YYYY-MM-DD HH:MM:SS.SSS
												if(!Date_added.equalsIgnoreCase("") && Date_added != null){
													try {
														year = Date_added.substring(0, 4);
														month = Date_added.substring(4, 6);
														day = Date_added.substring(6, 8);
														Date_added = year+"-"+month+"-"+day;
													} catch (Exception e) {
														e.printStackTrace();
														StringWriter errors = new StringWriter();
														e.printStackTrace(new PrintWriter(errors));
														SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
													}
												}
												if(!Expiry_date.equalsIgnoreCase("") && Expiry_date != null){
													try {
														year = Expiry_date.substring(0, 4);
														month = Expiry_date.substring(4, 6);
														day = Expiry_date.substring(6, 8);
														Expiry_date = year+"-"+month+"-"+day;
													} catch (Exception e) {
														e.printStackTrace();
														StringWriter errors = new StringWriter();
														e.printStackTrace(new PrintWriter(errors));
														SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
													}
												}
												
												Duration = getEventDuration(Date_added+" "+eventStartTime,Expiry_date+" "+endTimeAndEndDate);
												
	//											String Language = jsonObjectForLiveEvent.getString("language");
												try{
														Event_id = (int) programGateway.insertProgramInfo( "","live" ,Service_id, Genre, 0, "", Expiry_date, Date_added, Description, "", "", 0, "", "", "", "", Start_time, Duration, "", "", Event_name, "", 0,"",Bouquet_id,Channel_name,collectionid,collectionName,com.port.api.util.CommonUtil.getDate(),preTimeStamp);
	//													if(Constant.DEBUG)  Log.d(TAG,"Live Event : Event_id: "+Event_id+", Type: "+"live"+", Service_id: "+Service_id+", Expiry_date: "+Expiry_date+", Date_added: "+Date_added+", Image: "+Image+", Duration: "+Duration+", Start_time: "+Start_time);
														
												}
												catch(Exception e){
													e.printStackTrace();
													StringWriter errors = new StringWriter();
													e.printStackTrace(new PrintWriter(errors));
													SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
													
												}
											}
										}
									}
								}else{
									try{//Added by @Tomesh for adding program detail in live channels
										Log.e(TAG, "getEPGServerData======Calling======insertProgramData");
										
										insertProgramData(Service_id,"","","","live",Bouquet_id,Channel_name);
									}
									catch(Exception e){
										e.printStackTrace();
										StringWriter errors = new StringWriter();
										e.printStackTrace(new PrintWriter(errors));
										SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
									}
								}
							}
						
						}
					}
					
					
					// Remove from Status
					List<ProgramInfo> programIdList = null;
					List<ChannelInfo> channelIdList = channelGateway.getAllServiceInfoByTimeStamp(preTimeStamp,"live");
					for(ChannelInfo serviceInfo : channelIdList){
						programIdList = programGateway.getAllExternalEventByServiceId(serviceInfo.getServiceId());
						if(programIdList != null){
							for(ProgramInfo proInfo : programIdList){
								statusGateway.deleteStatusInfoById(proInfo.getEventId(),"event");								
//								Home.mMemoryCache.remove("P_"+proInfo.getProgramId());								
							}
							if(Constant.DEBUG)Log.d(TAG, "Deleting programs before time : " + preTimeStamp);
							programGateway.deleteEventInfoByDate(serviceInfo.getServiceId(),preTimeStamp); 
						}
						statusGateway.deleteStatusInfoById(serviceInfo.getServiceId(),"service");
//						Port.mMemoryCache.remove("C_"+serviceInfo.getServiceId());
					}
					if(Constant.DEBUG)Log.d(TAG, "Deleting all live channels before time : " + preTimeStamp);
					channelGateway.deleteServiceInfoByDate(preTimeStamp,"live");
					
					clearBouquet();
					
					//Data Store in Cache
					storeInCache();
					
//					getAllServiceAndEventImages("live");
					
				    Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
			    	returner.set("PLAYER", pnetwork, "com.player.apps.GuideService");	
					JSONObject resp = new JSONObject();
					JSONObject data = new JSONObject();
					data.put("update", "EPG");
					resp.put("params", data);
					returner.add("com.port.service.Catalogue.epg-update", resp,"startService");
					returner.send();
				}
			}
		}
	}
    
    private void clearBouquet(){
    	try{
//	    	BouquetGateway bouquetGateway = null;
//			if(CommonUtil.checkConnectionForLocaldb()){
//				bouquetGateway = new BouquetGateway(CacheData.getDatabase());
//				CacheData.setBouquetGateway(bouquetGateway);
//			}
//			
//			ChannelGateway channelGateway = null;
//			if(CommonUtil.checkConnectionForLocaldb()){
//				channelGateway = new ChannelGateway(CacheData.getDatabase());	
//				CacheData.setChannelGateway(channelGateway);
//			}
    		if (Constant.DEBUG) Log.d(TAG, "clearBouquet()");
    		BouquetGateway bouquetGateway = new BouquetGateway(Port.c.getApplicationContext()) ;
    		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		
			List<BouquetInfo> bouquetInfos = bouquetGateway.getAllBouquetInfo();
	    	for(BouquetInfo bInfo : bouquetInfos){
	    		int size = channelGateway.getAllServiceInfoByBouquetId(bInfo.getBouquetId()).size();
	    		if(size <= 0){
	    			bouquetGateway.deleteBouquetInfoByBouquetId(bInfo.getBouquetId());
	    		}
	    	}
    	}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
    }
    
    private void storeInCache()  throws JSONException {
//		if(Constant.DEBUG)  Log.d(TAG,"storeInCache() ");
		BouquetList.clear();
		ServiceList.clear();
		EventList.clear();
		BouquetGateway bouquetGateway =new BouquetGateway(Port.c.getApplicationContext()) ;
		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		List<BouquetInfo> bouquetList = bouquetGateway.getAllBouquetInfo();
		if(bouquetList != null && bouquetList.size() > 0){
			for (BouquetInfo B_info : bouquetList) {
				if (B_info != null) {
//					if(Constant.DEBUG)  Log.d(TAG,"storeInCache() Bouquet Id: "+B_info.getBouquetId()+", Bouquet Name: "+B_info.getBouquetName());
					if (Constant.DVB) {	//DVB middleware
						if (Constant.DEBUG) Log.d(TAG, "Not in DVB Module");
						BouquetList.add(new BouquetInfo(B_info.getBouquetId(), B_info.getBouquetName(), B_info.getCategory(), B_info.getServiceId(),B_info.getTSId(),B_info.getLCN()));
					} else {
						BouquetList.add(new BouquetInfo(B_info.getBouquetId(), B_info.getBouquetName(), B_info.getCategory()));
					}
				}
			}
		}
		
		List<ChannelInfo> channelList = channelGateway.getAllServiceList();
		if(channelList != null && channelList.size() > 0){
			for (int i = 0; i < channelList.size(); i++) {
//				if(Constant.DEBUG)  Log.d(TAG,"storeInCache() ChannelName: "+channelList.get(i).getChannelName()+", Service_id: "+channelList.get(i).getServiceId()+", Type: "+channelList.get(i).getType()+", Name: "+channelList.get(i).getChannelName()+", Bouquet_id: "+channelList.get(i).getBouquetId()+", Channel_logo: "+channelList.get(i).getChannelLogo());
				if (Constant.DVB) {	//DVB middleware
					if (Constant.DEBUG) Log.d(TAG, "Not in DVB Module");
					ServiceList.add(new ChannelInfo(channelList.get(i).getServiceId(), 0, 0, 0, 0, channelList.get(i).getType(), channelList.get(i).getChannelName(), channelList.get(i).getBouquetId(), 0, channelList.get(i).getDesc(), channelList.get(i).getMaturity(), channelList.get(i).getPrice(), channelList.get(i).getPriceModel(), channelList.get(i).getExpiryDate(), 0, 0, channelList.get(i).getServiceCategory(), channelList.get(i).getChannelLogo(),channelList.get(i).getEitSchedule(),channelList.get(i).getEitPresent()));
				} else {
					ServiceList.add(new ChannelInfo(channelList.get(i).getServiceId(), 0, "", channelList.get(i).getType(), channelList.get(i).getChannelName(), channelList.get(i).getBouquetId(), 0, channelList.get(i).getDesc(), channelList.get(i).getMaturity(), channelList.get(i).getPrice(), channelList.get(i).getPriceModel(), channelList.get(i).getExpiryDate(), 0, 0, channelList.get(i).getServiceCategory(), channelList.get(i).getChannelLogo(), channelList.get(i).getChannelurl()));
					//Added by tomesh
				}
			}
		}
		
		List<ProgramInfo> programList = programGateway.getAllProgramInfo();
		if(programList != null && programList.size() > 0){
			for (ProgramInfo P_info : programList) {
				if (P_info != null) {
//					if(Constant.DEBUG)  Log.d(TAG,"storeInCache() Event_id: "+P_info.getEventId()+", ChannelType: "+P_info.getChannelType()+", eventUniqueId: "+P_info.getProgramId()+", Name: "+P_info.getEventName()+", chlUniqueId: "+P_info.getChannelServiceId()+", Price: "+P_info.getPrice()+", Pricing_model: "+P_info.getPriceModel()+", Image: "+P_info.getImage());
					if (Constant.DVB) {	//DVB middleware
						if (Constant.DEBUG) Log.d(TAG, "Not in DVB Module");
						EventList.add(new ProgramInfo(P_info.getEventId(), P_info.getEventSrc(), P_info.getChannelType(),P_info.getChannelServiceId(), P_info.getGenre(), P_info.getPrice(), P_info.getPriceModel(), P_info.getExpiryDate(), P_info.getDateAdded(), P_info.getDescription(), P_info.getMaturity(), P_info.getImage(), P_info.getRanking(), P_info.getActors(), P_info.getDirector(), P_info.getMusicDirector(), P_info.getProductionHouse(), P_info.getStartTime(), P_info.getDuration(), P_info.getRating(), P_info.getLanguage(), P_info.getEventName(), P_info.getEventCategory(), P_info.getProgramId(),P_info.getSummary(),P_info.getBouquetId(),P_info.getChannelName(),P_info.getCollectionId(),P_info.getCollectionName(),P_info.getRunningStatus(),P_info.getFreeCAmode(), P_info.getStartDate()));
					} else {
						EventList.add(new ProgramInfo(P_info.getEventId(), P_info.getEventSrc(), P_info.getChannelType(),P_info.getChannelServiceId(), P_info.getGenre(), P_info.getPrice(), P_info.getPriceModel(), P_info.getExpiryDate(), P_info.getDateAdded(), P_info.getDescription(), P_info.getMaturity(), P_info.getImage(), P_info.getRanking(), P_info.getActors(), P_info.getDirector(), P_info.getMusicDirector(), P_info.getProductionHouse(), P_info.getStartTime(), P_info.getDuration(), P_info.getRating(), P_info.getLanguage(), P_info.getEventName(), P_info.getEventCategory(), P_info.getProgramId(),P_info.getSummary(),P_info.getBouquetId(),P_info.getChannelName(),P_info.getCollectionId(),P_info.getCollectionName()));
					}
				}
			}
		}		
	
//		if(Constant.DEBUG)  Log.d(TAG,"storeInCache() BouquetList.size()"+BouquetList.size());
//		if(Constant.DEBUG)  Log.d(TAG,"storeInCache() ServiceList.size()"+ServiceList.size());
//		if(Constant.DEBUG)  Log.d(TAG,"storeInCache() EventList.size()"+EventList.size());
	}
    
    
    private void getAllServiceAndEventImages(String type){
    	channelImageList.clear();
    	programImageList.clear();
		if(Constant.DEBUG)  Log.d(TAG, "getAllServiceAndEventImages()");
		ArrayList<Integer> Id = new ArrayList<Integer>();
		try {
			for (int i = 0; i < ServiceList.size(); i++) {
				if(ServiceList.get(i).getChannelLogo() != null && !ServiceList.get(i).getChannelLogo().equalsIgnoreCase("") && ServiceList.get(i).getType().equalsIgnoreCase(type)){
					Id.add(ServiceList.get(i).getServiceId());
					if(getBitmapFromMemCache("C_"+ServiceList.get(i).getServiceId()) == null){
//						if(Constant.DEBUG)  Log.d(TAG, "getAllServiceAndEventImages().Service Image: "+ServiceList.get(i).getChannelLogo());
						
						Bitmap tvBitmap = DownloadBitmap(ServiceList.get(i).getChannelLogo());
							//for TV Guide
							if (tvBitmap!=null){
								if(Constant.DEBUG)  Log.d(TAG, "service logo not null");
								ByteArrayOutputStream compressedImageStream = new ByteArrayOutputStream();
								tvBitmap.compress(Bitmap.CompressFormat.PNG, 100, compressedImageStream);
								byte[] compressedTVImage = compressedImageStream.toByteArray();
								String channelImage = Base64.encodeToString(compressedTVImage, Base64.DEFAULT);
								channelImageList.put(ServiceList.get(i).getServiceId()+"", channelImage);
//								addBitmapToMemoryCache("C_"+ServiceList.get(i).getServiceId(), tvBitmap);
							}else{
//								if(Constant.DEBUG)  Log.d(TAG, "Service decode not working");
							}
	
					}
				}
			}
			
//			if(Constant.DEBUG)  Log.d(TAG,"getAllServiceAndEventImages() Id.size(): "+Id.size());
//			if(Constant.DEBUG)  Log.d(TAG,"getAllServiceAndEventImages() EventList.size(): "+EventList.size());
//			if(Constant.DEBUG)  Log.d(TAG,"getAllServiceAndEventImages() ServiceList.size(): "+ServiceList.size());
			
			for (int i = 0; i < EventList.size(); i++) {
				for (int j = 0; j < Id.size(); j++) {
					if(EventList.get(i).getImage() != null && !EventList.get(i).getImage().equalsIgnoreCase("") && EventList.get(i).getChannelServiceId() == Id.get(j)
							&& EventList.get(i).getChannelType().equalsIgnoreCase(type)){
						if(getBitmapFromMemCache("P_"+EventList.get(i).getProgramId()) == null){
//							if(Constant.DEBUG)  Log.d(TAG, "i: "+i+", # j: "+j+", ServiceId: "+Id.get(j)+", getAllServiceAndEventImages().Event Image: "+EventList.get(i).getImage());
							Bitmap tvBitmap = DownloadBitmap(EventList.get(i).getImage());

								//for TV Guide
								if (tvBitmap!=null){
//									if(Constant.DEBUG)  Log.d(TAG, "event logo not null");
									ByteArrayOutputStream compressedImageStream = new ByteArrayOutputStream();
									tvBitmap.compress(Bitmap.CompressFormat.PNG, 100, compressedImageStream);
									byte[] compressedTVImage = compressedImageStream.toByteArray();
									String programImage = Base64.encodeToString(compressedTVImage, Base64.DEFAULT);
									programImageList.put(EventList.get(i).getProgramId()+"", programImage);
//									addBitmapToMemoryCache("P_"+EventList.get(i).getProgramId(), tvBitmap);
								}else{
//									if(Constant.DEBUG)  Log.d(TAG, "Event decode not working");
								}
							
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}
	}   
 
    
    public void FeaturedDataDetails() throws JSONException {
    	for (int i = 0; i < featuredList.size(); i++) {
    		if (featuredList.get(i).getExternalEventId() > 0) {    			
				String fImage = "F_"+featuredList.get(i).getExternalEventId();
				if(Constant.DEBUG) Log.d(TAG,"Removing featured image : "+ fImage);
				Port.mMemoryCache.remove(fImage);
    		}
		}
    	for ( int i = 0; i < packageList.size(); i++){
    		if (packageList.get(i).getPackageId() !=""){
    			String pImage = "F_" + packageList.get(i).getPackageId();
    			if(Constant.DEBUG) Log.d(TAG,"Removing package image : "+ pImage);
    			Port.mMemoryCache.remove(pImage);
    		}    		
    	}
    	
    	featuredList.clear();
    	featuredImageList.clear();
    	packageList.clear();
    	
		JSONObject jsonObject = TVGuide.getFeaturedDetails();
		if(jsonObject != null){
			try {
//				if(Constant.DEBUG) Log.d(TAG,"Update Feature Data: "+jsonObject.toString());
				JSONObject jsonObjectForFeatured = jsonObject.getJSONObject("json");
				if(jsonObjectForFeatured != null){
					String result = jsonObjectForFeatured.getString("result");
					if(result != null && result.trim().equalsIgnoreCase("success")){
						String summary = "";
						String name = "";
						// For Package
						JSONArray featured = jsonObjectForFeatured.getJSONArray("packages");
						if(featured != null && featured.length() > 0){
							for(int i=0; i<featured.length(); i++){
								JSONObject jsonObjectForPackage = featured.getJSONObject(i);
								if(jsonObjectForPackage != null){
									String packageid = jsonObjectForPackage.getString("packageid");

									JSONArray jsonArrayForPrograms = null;
									if(jsonObjectForPackage.has("programs")){
										jsonArrayForPrograms = jsonObjectForPackage.getJSONArray("programs");
									}

									JSONArray jsonArrayForChannels = null;
									if(jsonObjectForPackage.has("channels")){
										jsonArrayForChannels = jsonObjectForPackage.getJSONArray("channels");
									}

									String subscriptionperiod = jsonObjectForPackage.getString("subscriptionperiod");
									if(jsonObjectForPackage.has("summary")){
										summary = jsonObjectForPackage.getString("summary");
									}
									name = jsonObjectForPackage.getString("name");
									String image = "";
									String thumbnail = "";

									if(jsonObjectForPackage.has("image")){
										image = jsonObjectForPackage.getString("image");
									}
									
									if(jsonObjectForPackage.has("thumbnail")){
										thumbnail = jsonObjectForPackage.getString("thumbnail");
									}

									String subscriptionprice = jsonObjectForPackage.getString("subscriptionprice");
									String periodunit = jsonObjectForPackage.getString("periodunit");
									String currency = jsonObjectForPackage.getString("currency");
									
									String packagePrograms = "";
									if(jsonArrayForPrograms != null && jsonArrayForPrograms.length() > 0){
										for(int j=0; j<jsonArrayForPrograms.length(); j++){
											int programId = jsonArrayForPrograms.getInt(j);
											if(j==0){
												packagePrograms += programId;
											} else {
												packagePrograms += ","+programId;
											}
										}
									}

									String packageChannels = "";
									if(jsonArrayForChannels != null && jsonArrayForChannels.length() > 0){
										for(int j=0; j<jsonArrayForChannels.length(); j++){
											int Channel_id = jsonArrayForChannels.getInt(j);
											if(j==0){
												packageChannels += Channel_id;
											} else {
												packageChannels += ","+Channel_id;
											}
										}
									}
									packageList.add(new PackageInfos(packageid, name, summary, image, subscriptionprice, currency, packagePrograms, packageChannels, periodunit, subscriptionperiod,thumbnail));
									if(Constant.DEBUG)  Log.d(TAG,"featured List(Package) : packageid: "+packageid+", packagePrograms: "+packagePrograms+", packageChannels: "+packageChannels+", subscriptionperiod: "+subscriptionperiod+", periodunit: "+periodunit+", image: "+image);
								}
							}
						}
						
						// For Program
						featured = jsonObjectForFeatured.getJSONArray("featuredPrograms");
			            for (int i=0; i<featured.length(); i++) {
			                JSONObject item = featured.getJSONObject(i);
			                String id = item.getString("id");
			                String image = item.getString("image");
			                String thumbnail = item.getString("thumbnail");
			                summary = item.getString("summary");
			                name = item.getString("name");
//			                if(Constant.DEBUG) Log.d(TAG ,"event id "+ id +  " with image " + image);
			                featuredList.add(new FeaturedInfo(0, 0, Integer.parseInt(id), "event",image,summary,name,thumbnail));
			                if(Constant.DEBUG)  Log.d(TAG,"featured List(Event) : EventId: "+Integer.parseInt(id)+", name: "+name+", event, image: "+image);
							
			            }
			            
			         // For Channel
			            featured = jsonObjectForFeatured.getJSONArray("featuredChannels");
			            for (int i=0; i<featured.length(); i++) {
			                JSONObject item = featured.getJSONObject(i);
			                String id = item.getString("id");
			                String image = item.getString("image");
			                String thumbnail = item.getString("thumbnail");
			                summary = item.getString("summary");
			                name = item.getString("name");
//			                if(Constant.DEBUG) Log.d(TAG ,"service id "+ id +  " with image " + image);
			                featuredList.add(new FeaturedInfo(0, 0, Integer.parseInt(id), "service",image,summary,name,thumbnail));
			                if(Constant.DEBUG)  Log.d(TAG,"featured List(Service) : Service_id: "+Integer.parseInt(id)+", type: service, image: "+image);
							
			            }
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
			}
		}
		
		getAllFeaturedImages();
		
	}
    
    private void getAllFeaturedImages(){
//		if(Constant.DEBUG)  Log.d(TAG, "getAllFeaturedImages()");
		try {
			if(Constant.DEBUG)  Log.d(TAG, "getAllFeaturedImages().featuredList.size() "+featuredList.size());
			
			for (int i = 0; i < packageList.size(); i++) {
				HashMap<String, String> imagelist = new HashMap<String, String>();
				if(packageList.get(i).getPackageImage()!=null && !packageList.get(i).getPackageImage().equalsIgnoreCase("") && 
					packageList.get(i).getPackageThumbnail()!=null && !packageList.get(i).getPackageThumbnail().equalsIgnoreCase("")){
					if(Constant.DEBUG)  Log.d(TAG, "getAllFeaturedImages().featured Package Image: "+packageList.get(i).getPackageImage());
					Bitmap bigBitmap = DownloadBitmap(packageList.get(i).getPackageImage());
					if(bigBitmap!=null){
						addBitmapToMemoryCache("F_"+packageList.get(i).getPackageId(), bigBitmap);	
						// added jan 8 2015
						Bitmap tvBitmap = DownloadBitmap(packageList.get(i).getPackageThumbnail());
						String encodedImage = "";
						if(tvBitmap!=null){
							ByteArrayOutputStream compressedImageStream = new ByteArrayOutputStream();
							tvBitmap.compress(Bitmap.CompressFormat.JPEG, 100, compressedImageStream);
							byte[] compressedImage = compressedImageStream.toByteArray();
							encodedImage = Base64.encodeToString(compressedImage, Base64.DEFAULT);
						}
						
						imagelist.put("type", "package");
//						imagelist.put("title", packageList.get(i).getPackageName());
						imagelist.put("name", packageList.get(i).getPackageName());
						imagelist.put("desc", packageList.get(i).getPackageDescription());
						imagelist.put("imageId",packageList.get(i).getPackageId()+"");
						imagelist.put("image", encodedImage);		// added jan 8 2015
						
						featuredImageList.add(imagelist); //to send to Player
						
						// added 24 Feb 2015
						if (featuredImageList.size()==1) {
							if(Constant.DEBUG)  Log.d(TAG, "Package Featured call");
							Intent home = new Intent();
							home.setAction("CATALOGUE");
							home.putExtra("ShowHome","ok");
							sendBroadcast(home);	
						}
					}else{
						if(Constant.DEBUG)  Log.d(TAG, "Featured Package decode not working");
					}
				}
			}
			
			for (int i = 0; i < featuredList.size(); i++) {
				HashMap<String, String> imagelist = new HashMap<String, String>();
				if(featuredList.get(i).getFeaturedImage()!=null && !featuredList.get(i).getFeaturedImage().equalsIgnoreCase("") && 
						featuredList.get(i).getFeaturedThumbnail()!=null && !featuredList.get(i).getFeaturedThumbnail().equalsIgnoreCase("")){

					Bitmap bigBitmap = DownloadBitmap(featuredList.get(i).getFeaturedImage()); 
					if(bigBitmap!=null){
						addBitmapToMemoryCache("F_"+featuredList.get(i).getExternalEventId(), bigBitmap);
						// added jan 8 2015
						Bitmap tvBitmap = DownloadBitmap(featuredList.get(i).getFeaturedThumbnail());
						String encodedImage = "";
						if(tvBitmap!=null){
							ByteArrayOutputStream compressedImageStream = new ByteArrayOutputStream();
							tvBitmap.compress(Bitmap.CompressFormat.JPEG, 100, compressedImageStream);
							byte[] compressedImage = compressedImageStream.toByteArray();
							encodedImage = Base64.encodeToString(compressedImage, Base64.DEFAULT);
						}
						
						imagelist.put("type", featuredList.get(i).getFeaturedType());
//						imagelist.put("title", featuredList.get(i).getFeaturedTitle());
						imagelist.put("name", featuredList.get(i).getFeaturedTitle());
						imagelist.put("desc", featuredList.get(i).getFeaturedDesc());
						imagelist.put("imageId",featuredList.get(i).getExternalEventId()+"");
						imagelist.put("image", encodedImage);		// added jan 8 2015
						
						featuredImageList.add(imagelist); //to send to Player 
						// added 24 Feb 2015
						if (featuredImageList.size()==1) {
							if(Constant.DEBUG)  Log.d(TAG, "Event Featured call");
							Intent home = new Intent();
							home.setAction("CATALOGUE");
							home.putExtra("ShowHome","ok");
							sendBroadcast(home);	
						}
					}else{
						if(Constant.DEBUG)  Log.d(TAG, "Featured Event decode not working");
					}

				}
			}
			if(Constant.DEBUG)  Log.d(TAG, "featuredImageList: "+featuredImageList.size());
			// added 24 Feb 2015
			if (featuredImageList.size()>1) {
				if(Constant.DEBUG)  Log.d(TAG, "Package Featured call");
				Intent home = new Intent();
				home.setAction("CATALOGUE");
				home.putExtra("ShowFeatured","ok");
				sendBroadcast(home);	
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}
	}   
    
    private void RecommendedDataDetails() throws Exception {
//    	if(Constant.DEBUG)  Log.d(TAG, "RecommendedDataDetails() ");
		recommendedList.clear();
		
		HashMap<String, String> hashMap = null;
		JSONObject jsonData = TVGuide.getRecommendedData(); //webservice to get recommendations
		if(jsonData != null){
			try {
//				if(Constant.DEBUG) Log.d(TAG ,"Update Recommended Data: "+jsonData.toString());
				JSONObject jsonObject = jsonData.getJSONObject("data");
				if(jsonObject != null){
					String result = jsonObject.getString("result");
					if(result != null && result.trim().equalsIgnoreCase("success")){
						JSONArray jsonArray = jsonObject.getJSONArray("eventdata");
						if(jsonArray != null && jsonArray.length() >0){
							for(int i= 0;i<jsonArray.length();i++){
								JSONObject obj = jsonArray.getJSONObject(i);
								String event_id = "";
								String name = "";
								String language = "";
								String category = "";
								String genre = "";
								String source = "";
								String channelPrice= "";
								String channelName = "";
								String desc = "";
								String actor = "";
								String pricingModel = "";
								String director = "";
								String price = "";
								String musicdirector = "";
								String producer = "";
								String duration = "";
								String channelType = "";
								String channelId = "";
								String image = "";
								
								if(obj.has("programid")){
									event_id = obj.getString("programid");
								}
								if(obj.has("genre")){
									genre = obj.getString("genre");
								}
								if(obj.has("channelname")){
									channelName = obj.getString("channelname");
								}
								if(obj.has("channelprice")){
									channelPrice = obj.getString("channelprice");
								}
								if(obj.has("serviceid")){
									channelId = obj.getString("serviceid");
								}
								if(obj.has("channeltype")){
									channelType = obj.getString("channeltype");
								}
								if(obj.has("description")){
									desc = obj.getString("description");
								}
								
								if(obj.has("name")){
									name = obj.getString("name");
								}
								if(obj.has("actors")){
									actor = obj.getString("actors");
								}
								if(obj.has("pricingmodel")){
									pricingModel = obj.getString("pricingmodel");
								}
								if(obj.has("image")){
									image = obj.getString("image");
								}
								if(obj.has("category")){
									category = obj.getString("category");
								}
								if(obj.has("duration")){
									duration = obj.getString("duration");
								}
								if(obj.has("price")){
									price = obj.getString("price");
								}
								if(obj.has("source")){
									source = obj.getString("source");
								}
								if(obj.has("language")){
									language = obj.getString("language");
								}
								if(obj.has("producer")){
									producer = obj.getString("producer");
								}
								if(obj.has("musicdirector")){
									musicdirector = obj.getString("musicdirector");
								}
								if(obj.has("director")){
									director = obj.getString("director");
								}
								
								hashMap = new HashMap<String, String>();
								hashMap.put("id", event_id);
								hashMap.put("name",name);
								hashMap.put("image",image);
								hashMap.put("language", language);
								hashMap.put("urllink", source);
								hashMap.put("category", category);
								hashMap.put("channelprice", channelPrice);
								hashMap.put("channelname", channelName);
								hashMap.put("description", desc);
								hashMap.put("pricingmodel", pricingModel);
								hashMap.put("director", director);
								hashMap.put("price", price);
								hashMap.put("actors", actor);
								hashMap.put("musicdirector", musicdirector);
								hashMap.put("producer", producer);
								hashMap.put("duration", duration);
								hashMap.put("channeltype", channelType);
								hashMap.put("channelid", channelId);
								hashMap.put("genre", genre);
								hashMap.put("duration", duration);
								recommendedList.add(hashMap);
							}
						}
					}
				}
//				if(Constant.DEBUG) System.out.println("getRecommendedList().list "+recommendedList.size());	
				getAllRecommendedImages();
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
			}
		}
	}
	
	private void getAllRecommendedImages(){
		recommendedImageList.clear();
		try {
			for (int i = 0; i < recommendedList.size(); i++) {
				HashMap<String, String> imagelist = recommendedList.get(i);
				if (imagelist.containsKey("image")){
					String ImageUrl = imagelist.get("image");
					String UniqueId = imagelist.get("id");	//UniqueId
					if(ImageUrl != null && !ImageUrl.equalsIgnoreCase("")){
						Bitmap tvBitmap = DownloadBitmap(ImageUrl);
						if(tvBitmap!=null){
							ByteArrayOutputStream compressedImageStream = new ByteArrayOutputStream();
							tvBitmap.compress(Bitmap.CompressFormat.JPEG, 100, compressedImageStream);
							byte[] compressedImage = compressedImageStream.toByteArray();
							String encodedImage = Base64.encodeToString(compressedImage, Base64.DEFAULT);
//							if(Constant.DEBUG)  Log.d(TAG, "Recommended Event Image"+encodedImage);
							imagelist.put("image", encodedImage);
							imagelist.put("type", "event");
							imagelist.put("imageId",UniqueId);
							recommendedImageList.add(imagelist); //to send to Player 
						}else{
//							if(Constant.DEBUG)  Log.d(TAG, "Featured Event decode not working");
						}
					}
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}
	}
	
//	private void AdsData(){
//		Date dt = new java.util.Date();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		String currDate = dateFormat.format(dt.getTime());
//    	String urlAdsData = Constant.AD_MP_FULL_SCREEN_URL;
//    	urlAdsData = urlAdsData + currDate + "&resolution=1080p";
//    	AdsList.clear();
//		JSONObject obj = Ads.getJSONData(urlAdsData);
//		JSONArray adsElement = null;
//		long startTime = 0;
//		long endTime = 0;
//		long currtime = 0;
//		int appId = 0;
//		String deviceType = null;
//    	String channelid = null;
//    	String adType = "";
//    	String stime = "";
//    	String etime = "";
//		try {
//			if (obj != null && obj.toString().contains("starttime") && obj.toString().contains("endtime")) {
//				JSONObject data = obj.getJSONObject("data");
//				if (data.has("ads")) {
//					adsElement = data.getJSONArray("ads");
//				}
//				if (data.has("starttime")) {
//					stime = data.getString("starttime");
//				}
//				if (data.has("endtime")) {
//					etime = data.getString("endtime");
//				}
//				
//				Calendar cal = Calendar.getInstance();
//				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//				String timeVal = timeFormat.format(cal.getTime());
//	
//				Calendar c1 = Calendar.getInstance();
//				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
//				currDate = dateformat.format(c1.getTime());
//	
//				startTime = CommonUtil.TimeConverter(stime + ":" + currDate);
//				endTime = CommonUtil.TimeConverter(etime + ":" + currDate);
//				currtime = CommonUtil.TimeConverter(timeVal + ":" + currDate);
//				if (endTime >= currtime && currtime > startTime) {
//					for (int i = 0; i < adsElement.length(); i++) {
//						JSONObject c = adsElement.getJSONObject(i);
//						deviceType = c.getString("deviceType");
//						adType = c.getString("type");
//						String widgeturl = c.getString("widget-url");
//						if (deviceType.equalsIgnoreCase(DEVICETYPE)) {
//							String extention = widgeturl.trim().substring(widgeturl.trim().lastIndexOf("."));
//	//			    		if (extention != null && extention.equalsIgnoreCase(".mp4")) {
//							if (extention != null && (extention.equalsIgnoreCase(".mp4") || extention.equalsIgnoreCase(".html"))) {	
//								int spots = c.getInt("spots");
//								int campId = c.getInt("campId");
//								appId = c.getInt("appId");
//								String adname = c.getString("name");
//								channelid = c.getString("serviceid");
//								String pubid = c.getString("pub_id");
//								String pubcampid = c.getString("pub_camp_id");
//								if(Constant.DEBUG) Log.i(TAG+":"+i, "spots: "+spots+", and campId: "+campId+", and appId: "+appId+", and channelid: "+channelid);
//								AdsList.add(new AdInfo(i, spots, campId, appId, adname,
//										adType, widgeturl, channelid, stime, etime, pubid,
//										pubcampid,0));
//							}
//						}
//					}
//				}
//			}
//			
//			String subscriberid = CacheData.getSubscriberId();
//			if (subscriberid != null && !subscriberid.equalsIgnoreCase("")) {
//				getSubscribedItems(subscriberid);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
//		}
//
//	}
	
	public void getSubscribedItems(String subscriberId){
		try{	
			ArrayList<String> PmgList = new ArrayList<String>();
			ArrayList<String> subscribedChlList = new ArrayList<String>();
			
			ArrayList<Integer> notSubscribedEvent = new ArrayList<Integer>();
			ArrayList<Integer> notSubscribedChannel = new ArrayList<Integer>();
			
			int userId = 1000;
//			StatusGateway statusGateway = CacheData.getStatusGateway();
//			if(statusGateway == null){
//				if(CommonUtil.checkConnectionForLocaldb()){
//					statusGateway = new StatusGateway(CacheData.getDatabase());
//				}
//			}
			
			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
						
			if(Constant.DEBUG) Log.d(TAG,"getSubscribedItems subscriberId : "+subscriberId);
			if(!subscriberId.equalsIgnoreCase("") && subscriberId != null){
				JSONObject obj = Subscription.SubscribedData(subscriberId);
				JSONObject objData = obj.getJSONObject("data");
				//Subscribed Program(ProgramId)
				JSONArray subProData = objData.getJSONArray("eventIdList");
				for(int i = 0; i < subProData.length(); i++){
					PmgList.add(subProData.getString(i));
					if(Constant.DEBUG) Log.i(TAG, i+" --- "+PmgList.get(i));
				}
				//Subscribed Channel(ServiceId)
				JSONArray subChnData = objData.getJSONArray("channelIdList");
				for(int i = 0; i < subChnData.length(); i++){
			        subscribedChlList.add(subChnData.getString(i));
			        if(Constant.DEBUG) Log.i(TAG, i+" --- "+subscribedChlList.get(i));
				}
				
//				List<StatusInfo> programInfos = statusGateway.getAllStatusInfoByType( 9,"event");
//				List<StatusInfo> channelInfos = statusGateway.getAllStatusInfoByType( 9,"service");
				
				statusGateway.deleteStatusInfoByStatus(9,"event");
				statusGateway.deleteStatusInfoByStatus(9,"service");
				
				for (int i = 0; i < PmgList.size(); i++) {	//1000|0|0|9930|9|0|0|14-01-2015|1421240207000
					statusGateway.insertStatusInfo(userId, 0, 0, Integer.parseInt(PmgList.get(i)), 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
//					CacheData.setStatusGateway(statusGateway);
				}
				
				for (int i = 0; i < subscribedChlList.size(); i++) {
					statusGateway.insertStatusInfo(userId,Integer.parseInt(subscribedChlList.get(i)), 0, 0, 9, 0, 0,com.port.api.util.CommonUtil.getDate(),com.port.api.util.CommonUtil.getDateTime());
//					CacheData.setStatusGateway(statusGateway);
				}
				
				
//				boolean present = false;
//				for (int i = 0; i < programInfos.size(); i++) {
//					for (int j = 0; j < PmgList.size(); j++) {
//						if (programInfos.get(i).getEventId() == Integer.parseInt(PmgList.get(j))) {
//							present = true;
//						}
//					}
//					if(Constant.DEBUG) Log.i(TAG, "getSubscribedItems() present --- "+present);
//					if (!present) {
//						if(Constant.DEBUG) Log.i(TAG, "getSubscribedItems() Event --- "+programInfos.get(i).getEventId());
//						statusGateway.deleteStatusInfoByUniqueId(programInfos.get(i).getEventId(), "event", 9);
//						CacheData.setStatusGateway(statusGateway);
//						present = false;
//					}
//				}
//				
//				if(PmgList.size() == 0){
//					for (int j = 0; j < programInfos.size(); j++) {
//						if(Constant.DEBUG) Log.i(TAG, "getSubscribedItems() Event --- "+programInfos.get(j).getEventId());
//						statusGateway.deleteStatusInfoByUniqueId(programInfos.get(j).getEventId(), "event", 9);
//						CacheData.setStatusGateway(statusGateway);
//					}
//				}
//					
//				
//				present = false;
//				
//				for (int i = 0; i < channelInfos.size(); i++) {
//					for (int j = 0; j < subscribedChlList.size(); j++) {
//						if (channelInfos.get(i).getServiceId() == Integer.parseInt(subscribedChlList.get(j))) {
//							present = true;
//						}
//					}
//					if(Constant.DEBUG) Log.i(TAG, "getSubscribedItems() present --- "+present);
//					if (!present) {
//						if(Constant.DEBUG) Log.i(TAG, "getSubscribedItems() Channel --- "+channelInfos.get(i).getServiceId());
//						statusGateway.deleteStatusInfoByUniqueId(channelInfos.get(i).getServiceId(), "service", 9);
//						CacheData.setStatusGateway(statusGateway);
//						present = false;
//					}
//				}
//				
//				if(subscribedChlList.size() == 0){
//					for (int j = 0; j < channelInfos.size(); j++) {
//						if(Constant.DEBUG) Log.i(TAG, "getSubscribedItems() Channel --- "+channelInfos.get(j).getServiceId());
//						statusGateway.deleteStatusInfoByUniqueId(channelInfos.get(j).getServiceId(), "service", 9);
//						CacheData.setStatusGateway(statusGateway);
//					}
//				}
				
//				for (int i = 0; i < programInfos.size(); i++) {
//					for (int j = 0; j < PmgList.size(); j++) {
//						if (programInfos.get(i).getEventId() != Integer.parseInt(PmgList.get(j))) {
//							notSubscribedEvent.add(programInfos.get(i).getEventId());
//							if(Constant.DEBUG) Log.i(TAG, "getSubscribedItems() unsubscribeEvent --- "+programInfos.get(i).getEventId());
//						}
//					}
//				}
				
//				for (int i = 0; i < channelInfos.size(); i++) {
//					for (int j = 0; j < subscribedChlList.size(); j++) {
//						if (channelInfos.get(i).getServiceId() != Integer.parseInt(subscribedChlList.get(j))) {
//							notSubscribedChannel.add(channelInfos.get(i).getServiceId());
//							if(Constant.DEBUG) Log.i(TAG, "getSubscribedItems() unsubscribeChannel --- "+channelInfos.get(i).getServiceId());
//						}
//					}
//				}
				
//				if (notSubscribedEvent.size()>0) {
//					for (int i = 0; i < notSubscribedEvent.size(); i++) {
//						statusGateway.deleteStatusInfoByUniqueId(notSubscribedEvent.get(i), "event", 9);
//						CacheData.setStatusGateway(statusGateway);
//					}
//				}
//				
//				if (notSubscribedChannel.size()>0) {
//					for (int i = 0; i < notSubscribedChannel.size(); i++) {
//						statusGateway.deleteStatusInfoByUniqueId(notSubscribedChannel.get(i), "service", 9);
//						CacheData.setStatusGateway(statusGateway);
//					}
//				}
			}
		}catch (Exception e) {
        	e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
        }
	}

    private synchronized Bitmap DownloadBitmap(String URL) {
    	
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(URL);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { 
//            	if (Constant.DEBUG) Log.d("Error " + statusCode + " while retrieving bitmap from ", URL); 
                return null;
            }
//            if (Constant.DEBUG) Log.d("Going to download image from ", URL); 
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent(); 
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();  
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
//            if (Constant.DEBUG) Log.d("Error while retrieving bitmap from ", e.toString());
            e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }	        

    	return null;
	}
    	
      
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
//		if(Constant.DEBUG)  Log.d(TAG, "addBitmapToMemoryCache key: "+key);
	    if (getBitmapFromMemCache(key) == null) {
//	    	if(Constant.DEBUG)  Log.d(TAG, "addBitmapToMemoryCache is null");
	    	Port.mMemoryCache.put(key, bitmap);
	    }
	}
	
	public static Bitmap getBitmapFromMemCache(String key) {
	    return Port.mMemoryCache.get(key);
	}
	
    private String getEventDurations(String eventStartTime,String endTimeAndEndDate) {
		String duration = "";
		try {
			if(eventStartTime != null && endTimeAndEndDate != null){
				int endTime = 0;
				int endMin = 0;
				int startTime = 0;
				int startMin = 0;
				String[] split = endTimeAndEndDate.split(":");
				if(split.length >0){
					endTime =Integer.parseInt(split[0].toString());
					endMin = Integer.parseInt(split[1].trim().toString());
				}
				split = eventStartTime.split(":");
				if(split.length >0){
					startTime =Integer.parseInt(split[0].trim().toString());
					startMin = Integer.parseInt(split[1].trim().toString());
				}
				duration = Math.abs(endTime-startTime)+":"+Math.abs(endMin-startMin);
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return duration;
	}
    
    private static String getEventDuration(String eventStartTime,String endTimeAndEndDate) {
//		System.out.println("eventStartTime: "+eventStartTime+", endTimeAndEndDate: "+endTimeAndEndDate);
		String dur = "";
		//HH converts hour in 24 hours format (0-23), day calculation
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
 
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(eventStartTime);
			d2 = format.parse(endTimeAndEndDate);
 
			//in milliseconds
			long duration = d2.getTime() - d1.getTime();
//			System.out.println("Duration: "+duration);
			long seconds = duration/1000;
		    long val = (seconds / 60) % 60;
		    String m = val+"";
		    
		    val = (seconds / (60 * 60)) % 24;
		    String h = val+"";
		    
		    dur = h+":"+m;
			
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return dur;
	}
    

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                      int bytes = read();
                      if (bytes < 0) {
                          break;  // we reached EOF
                      } else {
                          bytesSkipped = 1; // we read one byte
                      }
               }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
