package com.port;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.port.api.ads.AdInfo;
import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.api.webservices.Ads;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class Schedule  extends IntentService {
	
	private String TAG = "Catalogue";
	private static long preTimeStamp = 0;	
	
	public static List<AdInfo> AdsList = new ArrayList<AdInfo>();
	private static final String DEVICETYPE = "TV";
	public static String CurrentDate;
	private AdInfo adInfo = null;
	
	public Schedule(){
		super("Schedule");		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		//extract from bundle extras
		Bundle extras = intent.getExtras();
        String msg = "";
        
    	String dockID = Build.ID; //is this correct ?
	    Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
    	
        preTimeStamp = CommonUtil.getDateTime();
        
        if(extras != null){
    		if (extras.containsKey("Title")) {
    			msg = extras.getString("Title");
    	        if (msg.equalsIgnoreCase("ads")){
    	        	AdsData();
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
//    	        }
    	        	
    		}
    	}       
	}
	
	
	private void AdsData(){
		Date dt = new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String currDate = dateFormat.format(dt.getTime());
    	String urlAdsData = Constant.AD_MP_FULL_SCREEN_URL;
    	urlAdsData = urlAdsData + currDate + "&resolution=1080p";
    	AdsList.clear();
		JSONObject obj = Ads.getJSONData(urlAdsData);
		JSONArray adsElement = null;
		long startTime = 0;
		long endTime = 0;
		long currtime = 0;
		int appId = 0;
		String deviceType = null;
    	String channelid = null;
    	String adType = "";
    	String stime = "";
    	String etime = "";
		try {
			if (obj != null && obj.toString().contains("starttime") && obj.toString().contains("endtime")) {
				JSONObject data = obj.getJSONObject("data");
				if (data.has("ads")) {
					adsElement = data.getJSONArray("ads");
				}
				if (data.has("starttime")) {
					stime = data.getString("starttime");
				}
				if (data.has("endtime")) {
					etime = data.getString("endtime");
				}
				
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
				String timeVal = timeFormat.format(cal.getTime());
	
				Calendar c1 = Calendar.getInstance();
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				currDate = dateformat.format(c1.getTime());
	
				startTime = CommonUtil.TimeConverter(stime + ":" + currDate);
				endTime = CommonUtil.TimeConverter(etime + ":" + currDate);
				currtime = CommonUtil.TimeConverter(timeVal + ":" + currDate);
				if (endTime >= currtime && currtime > startTime) {
					for (int i = 0; i < adsElement.length(); i++) {
						JSONObject c = adsElement.getJSONObject(i);
						deviceType = c.getString("deviceType");
						adType = c.getString("type");
						String widgeturl = c.getString("widget-url");
						if (deviceType.equalsIgnoreCase(DEVICETYPE)) {
							String extention = widgeturl.trim().substring(widgeturl.trim().lastIndexOf("."));
	//			    		if (extention != null && extention.equalsIgnoreCase(".mp4")) {
							if (extention != null && (extention.equalsIgnoreCase(".mp4") || extention.equalsIgnoreCase(".html"))) {	
								int spots = c.getInt("spots");
								int campId = c.getInt("campId");
								appId = c.getInt("appId");
								String adname = c.getString("name");
								channelid = c.getString("serviceid");
								String pubid = c.getString("pub_id");
								String pubcampid = c.getString("pub_camp_id");
								if(Constant.DEBUG) Log.i(TAG+":"+i, "spots: "+spots+", and campId: "+campId+", and appId: "+appId+", and channelid: "+channelid);
								AdsList.add(new AdInfo(i, spots, campId, appId, adname,
										adType, widgeturl, channelid, stime, etime, pubid,
										pubcampid,0));
							}
						}
					}
				}
			}
			
//			added on EPG
//			String subscriberid = CacheData.getSubscriberId();
//			if (subscriberid != null && !subscriberid.equalsIgnoreCase("")) {
//				getSubscribedItems(subscriberid);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}

	}
	
//	private boolean DownloadFile(String Url,String name){
//		int count;
//		try {
//			URL url = new URL(Url);
//			URLConnection conexion = url.openConnection();
//			conexion.connect();
//
//			InputStream input = new BufferedInputStream(url.openStream());
//			FileOutputStream filetowrite = getApplicationContext().openFileOutput(name, getApplicationContext().MODE_WORLD_READABLE);
//						
//			byte data[] = new byte[1024];
//			long total = 0;
//			while ((count = input.read(data)) != -1) {
//				total += count;
//				filetowrite.write(data, 0, count);
//			}
//
//			filetowrite.flush();
//			filetowrite.close();
//			input.close();
//			
//		} catch (Exception e) {
//    		e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
//			return false;
//		}
//		return true;
//	}
//	
//	private boolean unpackZip(String path, String zipname){      
////		if(Constant.DEBUG)  Log.d(TAG,"Start unpackZip().");
//	     InputStream is;
//	     ZipInputStream zis;
//	     try {
//	         String filename;
//	         
//	         is = new FileInputStream(new File(getFilesDir(),zipname).getAbsoluteFile());
//	         zis = new ZipInputStream(new BufferedInputStream(is));          
//	         
//	         ZipEntry ze;
//	         byte[] buffer = new byte[1024];
//	         int count;
//
//	         while ((ze = zis.getNextEntry()) != null){
//	             filename = ze.getName();
//	             if (ze.isDirectory()) {
//	                File fmd = new File(path + filename);
//	                fmd.mkdirs();
//	                continue;
//	             }
//
//	             FileOutputStream fout = new FileOutputStream(path + filename);
//	             while ((count = zis.read(buffer)) != -1){
//	                 fout.write(buffer, 0, count);             
//	             }
//	             fout.close();               
//	             zis.closeEntry();
//	         }
//	         zis.close();
//	     } 
//	     catch(IOException e) {
//	    	e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_UPDATES, errors.toString(), e.getMessage());
//			return false;
//	     } 
//	    return true;
//	}
	
}
