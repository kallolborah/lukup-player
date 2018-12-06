package com.port.apps.epg;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.util.Log;

import com.port.Channel;
import com.port.Port;
import com.port.api.db.service.BouquetGateway;
import com.port.api.db.service.BouquetInfo;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ChannelInfo;
import com.port.api.db.service.ContentProviderTest;
import com.port.api.db.service.ProfileGateway;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.service.StatusInfo;
import com.port.api.db.service.Store;
import com.port.api.db.util.CacheData;
import com.port.api.epg.service.FeaturedInfo;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.api.webservices.Subscription;
import com.port.apps.epg.util.CommonUtil;

public class Guide extends IntentService {

	private static String TAG = "Guide";
	String method = "com.port.apps.epg.Guide.";

	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String caller;
	Channel returner;	
	String dockID;
	List<FeaturedInfo> homeFeaturedList = new ArrayList<FeaturedInfo>();

	Activity activity;
	public Guide() {
		super("Guide");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		int start = 0;
		int limit = 0;
		int id = 0;
		String mode = "";
		String type = "";
		String id_DVB= "";

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
					if(jsonObj.has("start")){
						start = Integer.parseInt(jsonObj.getString("start"));
					}if(jsonObj.has("limit")){
						limit = Integer.parseInt(jsonObj.getString("limit"));
					}if(jsonObj.has("id")){
						if(Constant.DVB){
							id_DVB =jsonObj.getString("id");
							id = Integer.parseInt(jsonObj.getString("id"));
						}else
							id = Integer.parseInt(jsonObj.getString("id"));
					}if(jsonObj.has("mode")){
						mode = jsonObj.getString("mode");
					}if(jsonObj.has("type")){
						type = jsonObj.getString("type");
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
					if(func.equalsIgnoreCase("sendFavouriteList")){
						sendFavouriteList(start, limit);
					}else if(func.equalsIgnoreCase("sendBouquetList")){
						if(Constant.DVB){
							sendDVBBouquetList(start, limit);
						} else {
							sendBouquetList(start, limit);
						}
					}else if(func.equalsIgnoreCase("sendServiceList")){
						if(Constant.DVB){
							sendDVBServiceList(id_DVB, start, limit);
						} else {
							sendServiceList(id, start, limit);
						}
					}else if(func.equalsIgnoreCase("sendEventList")){
						sendEventList(id, start, limit);
					}else if(func.equalsIgnoreCase("sendFeaturedList")){
						sendFeaturedList(start,limit,mode);
					}else if(func.equalsIgnoreCase("sendEventInfo")){
						sendEventInfo(id);
					}else if(func.equalsIgnoreCase("freeContent")){
						freeContent(id,mode,type);
					}else if(func.equalsIgnoreCase("checkSubscribedData")){
						checkSubscribedData(id);
					}
				} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}	    		
			}
		}
	}


	private void sendFeaturedList(int start, int resultset, String mode){
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		try{
			for (int i = start; i < (start+resultset); i++) {
				if(Catalogue.featuredImageList.size() > i){
					jsonObject = new JSONObject();
					jsonObject.put("id", Catalogue.featuredImageList.get(i).get("imageId"));
					jsonObject.put("name", Catalogue.featuredImageList.get(i).get("title"));
					jsonObject.put("desc", Catalogue.featuredImageList.get(i).get("desc"));
					jsonArray.put(jsonObject);
				}
			}
			data.put("result", "success");
			data.put("featuredList", jsonArray);
			resp.put("params",data);
			//			appendLog(resp.toString());
			returner.add("com.port.apps.epg.Guide.sendFeaturedList", resp,"messageActivity");
			returner.send();
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}	



	// send Bouquets Service List like Bollywood Hits, consumer : ? 
	private void sendServiceList(int id,int start, int resultset) throws JSONException, InterruptedException {
		List<ChannelInfo> tempList = new ArrayList<ChannelInfo>();
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;

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
		
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
		CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));


		int bouquetId = id;
		if(Constant.DEBUG)  Log.d(TAG,"From Cache :Catalogue.ServiceList: "+Catalogue.ServiceList.size());
		if(Catalogue.ServiceList.size() > 0 && Catalogue.BouquetList.size() > 0){
			try{
				if(Constant.DEBUG)  Log.d(TAG,"From Cache");
				for (int i = 0; i < Catalogue.ServiceList.size(); i++) {
					if(Catalogue.ServiceList.get(i).getBouquetId() == id){
						tempList.add(Catalogue.ServiceList.get(i));
					}
				}
				if(Constant.DEBUG)  Log.d(TAG,"From Cache :tempList: "+tempList.size());

				if(start <= tempList.size()){
					int size = 0;
 
					if((tempList.size() - start) > resultset){
						size = resultset;
					}else{
						size = tempList.size() - start;
					}
					
					if(Constant.DEBUG)  Log.d(TAG,"From Cache :start: "+start+",size "+size);
					for (int j = start; j < size; j++) {
					
						if(bouquetId == tempList.get(j).getBouquetId()){
							jsonObject = new JSONObject();
							int serviceId = tempList.get(j).getServiceId();
							jsonObject.put("id", serviceId+"");
							jsonObject.put("name", tempList.get(j).getChannelName());
							//							jsonObject.put("image", tempList.get(j).getChannelLogo());
							jsonObject.put("category", tempList.get(j).getServiceCategory());
							jsonObject.put("servicetype", tempList.get(j).getType());
							boolean subscribe = false;

//							ProfileInfo selectedProfileInfo = CacheData.getSelectedProfileInfo();
//							if(selectedProfileInfo != null){
//								userId = selectedProfileInfo.getUserId();
//							}

							String pricingmodel = tempList.get(j).getPriceModel();
							jsonObject.put("pricingmodel", pricingmodel);
							jsonObject.put("price", tempList.get(j).getPrice()+"");
							if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPC")){
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
							}
							jsonObject.put("subscribe", subscribe);

							boolean lock = false;
							StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", serviceId, 2,"service");
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

							boolean like = false;
							StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", serviceId, 1,"service");
							if(likeInfo == null){
								like = false;
							} else {
								if(likeInfo.getStatus() == 1){
									like = true;
								} else {
									like = false;
								}
							}
							jsonObject.put("like", like);
							jsonObject.put("source", tempList.get(j).getChannelurl());
							
							jsonArray.put(jsonObject);
						}
					}
				}

				if(jsonArray.length()>0){
					data.put("serviceList", jsonArray);
					data.put("result", "success");
					data.put("start", start+"");
					data.put("limit", resultset+"");
					resp.put("params",data);
					returner.add(method+"sendServiceList", resp,"messageActivity");
					returner.send();
				}else{
					data.put("result", "failure");
					data.put("start", start+"");
					data.put("limit", resultset+"");
					resp.put("params",data);
					returner.add(method+"sendServiceList", resp,"messageActivity");
					returner.send();
				}
			}catch(Exception e){
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}
		}else {
			if(Constant.DEBUG)  Log.d(TAG,"From DB :");
			
			ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;

			List<ChannelInfo> channelIdList = channelGateway.getAllServiceInfoByBouquetId(id);
			if(Constant.DEBUG)  Log.d(TAG,"From DB :channelIdList: "+channelIdList.size());
			if(channelIdList != null && channelIdList.size() > 0){
				try{
					if(start <= channelIdList.size()){
						int size = 0;
						if((channelIdList.size() - start) > resultset){
							size = resultset;
						}else{
							size = channelIdList.size() - start;
						}
						if(Constant.DEBUG)  Log.d(TAG,"From DB :start: "+start+",size "+size);
						for (int i = start; i < size; i++) {
							if(channelIdList != null){
								jsonObject = new JSONObject();
								int serviceId = channelIdList.get(i).getServiceId();
								jsonObject.put("id", serviceId+"");
								jsonObject.put("name", channelIdList.get(i).getChannelName());
								jsonObject.put("image", channelIdList.get(i).getChannelLogo());
								jsonObject.put("category", channelIdList.get(i).getServiceCategory());
								jsonObject.put("servicetype", channelIdList.get(i).getType());
								boolean subscribe = false;

//								ProfileInfo selectedProfileInfo = CacheData.getSelectedProfileInfo();
//								if(selectedProfileInfo != null){
//									userId = selectedProfileInfo.getUserId();
//								}

								String pricingmodel = channelIdList.get(i).getPriceModel();
								jsonObject.put("pricingmodel", pricingmodel);
								jsonObject.put("price", channelIdList.get(i).getPrice()+"");
								if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPC")){
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
								}
								jsonObject.put("subscribe", subscribe);

								boolean lock = false;
								StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", serviceId, 2,"service");
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

								boolean like = false;
								StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", serviceId, 1,"service");
								if(likeInfo == null){
									like = false;
								} else {
									if(likeInfo.getStatus() == 1){
										like = true;
									} else {
										like = false;
									}
								}
								jsonObject.put("like", like);
								jsonArray.put(jsonObject);
							}
						}
						data.put("serviceList", jsonArray);
						data.put("result", "success");
						data.put("start", start+"");
						data.put("limit", resultset+"");
						resp.put("params",data);
						returner.add(method+"sendServiceList", resp,"messageActivity");
						returner.send();
					}else{
						data.put("result", "failure");
						data.put("start", start+"");
						data.put("limit", resultset+"");
						resp.put("params",data);
						returner.add(method+"sendServiceList", resp,"messageActivity");
						returner.send();
					}
				}catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}
			}else {
				data.put("result", "failure");
				data.put("start", start+"");
				data.put("limit", resultset+"");
				resp.put("params",data);
				returner.add(method+"sendServiceList", resp,"messageActivity");
				returner.send();
			}
		} 
	}

	private void sendDVBServiceList(String id,int start, int resultset) throws JSONException, InterruptedException {
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		int service_Id = 0;
		String category="";
		String [] bouquetID = id.split(":");
		
		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
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
		
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
		CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));

		
		if(Constant.DEBUG)  Log.d(TAG,"From Cache :Catalogue.ServiceList: "+Catalogue.ServiceList.size());
		if(Constant.DVB){
			if(Constant.DEBUG)  Log.d(TAG,"From DB :");
			//Added By @Tomesh 0n 26 AUG 2015
			BouquetGateway bouquetInfoGateway =new BouquetGateway(Port.c.getApplicationContext()) ;
			List<BouquetInfo> bouquetInfo =new ArrayList<BouquetInfo>();
			if(Constant.DEBUG)  Log.d(TAG,"bouquetID split the clubbed :"+bouquetID +" Size : "+ bouquetID.length);
			
			for(int i = 0;i<bouquetID.length;i++){
				if(Constant.DEBUG)  Log.d(TAG,"bouquetID :"+Integer.parseInt(bouquetID[i]));
				bouquetInfo.addAll(bouquetInfoGateway.getDVBBouquetInfoById(Integer.parseInt(bouquetID[i])));
			}
			
			if(Constant.DEBUG)  Log.d(TAG,"BouquetInfo size :" + bouquetInfo.size());
			if(Constant.DEBUG)  Log.d(TAG,"BouquetInfo  :" + bouquetInfo);
			
			List<ChannelInfo> channelIdList = new ArrayList<ChannelInfo>();
			
			if(bouquetInfo!=null){
				for(int i =0; i< bouquetInfo.size();i++){
					service_Id = bouquetInfo.get(i).getTSId();
					if(Constant.DEBUG)  Log.d(TAG,"service_Id  :" +service_Id);
					if(service_Id <= 0){
						channelIdList.addAll(channelGateway.getAllServiceInfoByBouquetId(bouquetInfo.get(i).getBouquetId()));
						if(Constant.DEBUG)  Log.d(TAG,"service_Id less than 0:"+channelIdList);
					}else {
						if(Constant.DEBUG)  Log.d(TAG,"service_Id  : Inside DVB" );
						channelIdList.add(channelGateway.getDVBServiceInfoByServiceId(service_Id));
						
					}
				}
			}//Added Till Here By @Tomesh 0n 26 AUG 2015	
			
			if(Constant.DEBUG)  Log.d(TAG,"From DB :channelIdList: "+channelIdList.size());
			if(channelIdList != null && channelIdList.size() > 0){
				try{
					if(start <= channelIdList.size()){
						int size = 0;
						if((channelIdList.size() - start) > resultset){
							size = resultset;
						}else{
							size = channelIdList.size() - start;
						}
						if(Constant.DEBUG)  Log.d(TAG,"From DB :start: "+start+",size "+size);
						for (int i = start; i < size; i++) {
							if(channelIdList != null){
								jsonObject = new JSONObject();
								int serviceId = channelIdList.get(i).getServiceId();
								jsonObject.put("id", serviceId+"");
								if (channelIdList.get(i).getLCN() > 0) {
									jsonObject.put("name", channelIdList.get(i).getLCN()+" - "+channelIdList.get(i).getChannelName());
								}else{
									jsonObject.put("name", channelIdList.get(i).getChannelName());
								}
								jsonObject.put("image", channelIdList.get(i).getChannelLogo());
								jsonObject.put("category", channelIdList.get(i).getServiceCategory());
								jsonObject.put("servicetype", channelIdList.get(i).getType());
								boolean subscribe = false;

//								ProfileInfo selectedProfileInfo = CacheData.getSelectedProfileInfo();
//								if(selectedProfileInfo != null){
//									userId = selectedProfileInfo.getUserId();
//								}

								String pricingmodel = channelIdList.get(i).getPriceModel();
								jsonObject.put("pricingmodel", pricingmodel);
								jsonObject.put("price", channelIdList.get(i).getPrice()+"");
								if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPC")){
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
								}
								jsonObject.put("subscribe", subscribe);

								boolean lock = false;
								StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", serviceId, 2,"service");
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

								boolean like = false;
								StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", serviceId, 1,"service");
								if(likeInfo == null){
									like = false;
								} else {
									if(likeInfo.getStatus() == 1){
										like = true;
									} else {
										like = false;
									}
								}
								jsonObject.put("like", like);
								jsonArray.put(jsonObject);
							}
						}
						data.put("serviceList", jsonArray);
						data.put("result", "success");
						data.put("start", start+"");
						data.put("limit", resultset+"");
						resp.put("params",data);
						returner.add(method+"sendServiceList", resp,"messageActivity");
						returner.send();
					}else{
						data.put("result", "failure");
						data.put("start", start+"");
						data.put("limit", resultset+"");
						resp.put("params",data);
						returner.add(method+"sendServiceList", resp,"messageActivity");
						returner.send();
					}
				}catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				}
			}else {
				data.put("result", "failure");
				data.put("start", start+"");
				data.put("limit", resultset+"");
				resp.put("params",data);
				returner.add(method+"sendServiceList", resp,"messageActivity");
				returner.send();
			}
		} 
	}


	private void sendEventList(int id,int start, int resultset) throws JSONException {
		long currentTime = com.port.api.util.CommonUtil.getDateTime();
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = null;
		String pricingmodel="";
		String serviceType = "";

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
		
		ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
		CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));


		int serviceId = id;
		if(Constant.DEBUG)  Log.d(TAG,"serviceID "+serviceId);
		if(Constant.DEBUG)  Log.d(TAG,"sendEventList().ServiceList.size: "+Catalogue.ServiceList.size());
		if(Constant.DEBUG)  Log.d(TAG,"sendEventList().EventList.size: "+Catalogue.EventList.size());
		//		if(Constant.DEBUG)  Log.d(TAG,"sendEventList().serviceId: "+serviceId+ ", now: "+Port.now);
		if(Constant.DEBUG)  Log.d(TAG,"sendEventList().currentTime: "+currentTime+", Date: "+com.port.api.util.CommonUtil.getDate());

		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
		StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
		
		try{	
			ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(serviceId);
			
			data.put("serviceid", serviceId+"");
			data.put("category", serviceInfo.getServiceCategory());
			data.put("servicetype", serviceInfo.getType());

			pricingmodel = serviceInfo.getPriceModel();
			data.put("pricingmodel", pricingmodel);
			serviceType = serviceInfo.getType();
			boolean subscribe = false;
			if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPC")){
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
				data.put("subscribe", subscribe);
				data.put("price", serviceInfo.getPrice()+"");
			}

			boolean lock = false;
			StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", serviceId, 2,"service");
			if(lockInfo == null){
				lock = false;
			} else {
				if(lockInfo.getStatus() == 2){
					lock = true;
				} else {
					lock = false;
				}
			}
			data.put("lock", lock);
			if(serviceType.equalsIgnoreCase("live")){
				List<ProgramInfo> programIdList = programGateway.getAllExternalEventByServiceId(id);
				if(programIdList != null && programIdList.size() > 0){
					if(Constant.DEBUG)  Log.d(TAG,"DB sendEventList()size: "+programIdList.size());
					if(Constant.DEBUG)  Log.d(TAG,"sendEventList()Cache.serviceType: "+serviceType);
					for (int i = 0; i < programIdList.size(); i++) {
					//	long starttime = com.port.api.util.CommonUtil.CurrentTimeProgram(programIdList.get(i).getStartTime());
						String dur = programIdList.get(i).getDuration();
						StringTokenizer tokens = new StringTokenizer(dur, ":");
						int hours = 0;
						int min = 0;
							//if(Constant.DEBUG)  Log.d(TAG,"sendEventList()DB. starttime "+ (starttime) +" > "+ currentTime);
							jsonObject = new JSONObject();
							int eventId = programIdList.get(i).getEventId();
							jsonObject.put("id", eventId+"");
							jsonObject.put("name", programIdList.get(i).getEventName());
							jsonObject.put("image", programIdList.get(i).getImage());
							jsonObject.put("subcategory", programIdList.get(i).getEventCategory());
							jsonObject.put("starttime", com.port.api.util.CommonUtil.timeFormat(programIdList.get(i).getStartTime()));
							jsonObject.put("url", programIdList.get(i).getEventSrc());
							jsonObject.put("collectionid", programIdList.get(i).getCollectionId()+"");
							jsonObject.put("collectionname", programIdList.get(i).getCollectionName());

							if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPV")){
								StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(programIdList.get(i).getProgramId(), 9,"event");
								if(info != null) {
									if(info.getStatus() == 9) {
										subscribe = true;
									} else {
										subscribe = false;
									}
								} else {
									subscribe = false;
								}
								jsonObject.put("subscribe", subscribe);
								jsonObject.put("price", programIdList.get(i).getPrice()+"");
							}

							boolean like = false;
							StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", eventId, 1,"event");
							if(likeInfo == null){
								like = false;
							} else {
								like = true;
							}
							jsonObject.put("like", like);

							boolean record = false;
							StatusInfo recordInfo = statusGateway.getStatusInfoByServiceId(userId+"", eventId, 4,"event");
							if(recordInfo == null){
								record = false;
							} else {
								record = true;
							}
							jsonObject.put("record", record);
							jsonArray.put(jsonObject);
					}
					data.put("eventList", jsonArray);
					data.put("result", "success");
					data.put("start", start+"");
					data.put("limit", resultset+"");
					resp.put("params",data);
					returner.add(method+"sendEventList", resp,"messageActivity");
					returner.send();
				}else{
					data.put("result", "failure");
					data.put("start", start+"");
					data.put("limit", resultset+"");
					resp.put("params",data);
					returner.add(method+"sendEventList", resp,"messageActivity");
					returner.send();
				}

			}else{
				JSONObject collectionItem = null;
				JSONArray jsonCollectionArray = new JSONArray();
				List<ProgramInfo> UniqueCollectionIdList = programGateway.getCollectionIdByServiceId(id);
				if(Constant.DEBUG)  Log.d(TAG,"DB UniqueCollectionIdList()size: "+UniqueCollectionIdList.size());
				if(UniqueCollectionIdList.size() >= 1 && UniqueCollectionIdList.get(0).getCollectionId()>0 ){
					for (int j = 0; j < UniqueCollectionIdList.size(); j++) {
						collectionItem = new JSONObject();
						jsonArray = new JSONArray();
						if(Constant.DEBUG)  Log.d(TAG,"DB CollectionId: "+UniqueCollectionIdList.get(j).getCollectionId());
						List<ProgramInfo> CollectionIdList = programGateway.getEventByCollectionId(UniqueCollectionIdList.get(j).getCollectionId(),id);
						collectionItem.put("collectionname", UniqueCollectionIdList.get(j).getCollectionName());
						collectionItem.put("collectionid", UniqueCollectionIdList.get(j).getCollectionId());
						if(Constant.DEBUG)  Log.d(TAG,"DB CollectionIdList()size: "+CollectionIdList.size());
						for (int k = 0; k < CollectionIdList.size(); k++) {
							jsonObject = new JSONObject();
							jsonObject.put("id", CollectionIdList.get(k).getEventId()+"");
							jsonObject.put("name", CollectionIdList.get(k).getEventName());
							jsonObject.put("image", CollectionIdList.get(k).getImage());
							jsonObject.put("subcategory", CollectionIdList.get(k).getEventCategory());
							jsonObject.put("starttime", com.port.api.util.CommonUtil.timeFormat(CollectionIdList.get(k).getStartTime()));
							jsonObject.put("url", CollectionIdList.get(k).getEventSrc());
							jsonObject.put("collectionid", CollectionIdList.get(k).getCollectionId()+"");
							jsonObject.put("collectionname", CollectionIdList.get(k).getCollectionName());

							if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPV")){
								StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(CollectionIdList.get(k).getProgramId(), 9,"event");
								if(info != null) {
									if(info.getStatus() == 9) {
										subscribe = true;
									} else {
										subscribe = false;
									}
								} else {
									subscribe = false;
								}
								jsonObject.put("subscribe", subscribe);
								jsonObject.put("price", CollectionIdList.get(k).getPrice()+"");
							}

							boolean like = false;
							StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", CollectionIdList.get(k).getEventId(), 1,"event");
							if(likeInfo == null){
								like = false;
							} else {
								like = true;
							}
							jsonObject.put("like", like);
							boolean record = false;
							jsonObject.put("record", record);

							jsonArray.put(jsonObject);
						}
						collectionItem.put("collectionEventList", jsonArray);
						jsonCollectionArray.put(collectionItem);
					}
					data.put("collection", jsonCollectionArray);

					List<ProgramInfo> eventIdList = programGateway.getEventByCollectionId(0,id);
					if(eventIdList.size()>0){
						for (int j = 0; j < eventIdList.size(); j++) {
							jsonObject = new JSONObject();
							jsonObject.put("id", eventIdList.get(j).getEventId()+"");
							jsonObject.put("name", eventIdList.get(j).getEventName());
							jsonObject.put("image", eventIdList.get(j).getImage());
							jsonObject.put("subcategory", eventIdList.get(j).getEventCategory());
							jsonObject.put("starttime", com.port.api.util.CommonUtil.timeFormat(eventIdList.get(j).getStartTime()));
							jsonObject.put("url", eventIdList.get(j).getEventSrc());
							jsonObject.put("collectionid", eventIdList.get(j).getCollectionId()+"");
							jsonObject.put("collectionname", eventIdList.get(j).getCollectionName());

							if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPV")){
								StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(eventIdList.get(j).getProgramId(), 9,"event");
								if(info != null) {
									if(info.getStatus() == 9) {
										subscribe = true;
									} else {
										subscribe = false;
									}
								} else {
									subscribe = false;
								}
								jsonObject.put("subscribe", subscribe);
								jsonObject.put("price", eventIdList.get(j).getPrice()+"");
							}

							boolean like = false;
							StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", eventIdList.get(j).getEventId(), 1,"event");
							if(likeInfo == null){
								like = false;
							} else {
								like = true;
							}
							jsonObject.put("like", like);
							boolean record = false;
							jsonObject.put("record", record);

							jsonArray.put(jsonObject);
						}
						data.put("eventList", jsonArray);
					}
					data.put("result", "success");
					data.put("start", start+"");
					data.put("limit", resultset+"");
					resp.put("params",data);
					returner.add(method+"sendEventList", resp,"messageActivity");
					returner.send();

				}else{
					List<ProgramInfo> pgmIdList = programGateway.getAllExternalEventByServiceId(id);
					if(pgmIdList != null && pgmIdList.size() > 0){
						for (int j = 0; j < pgmIdList.size(); j++) {
							jsonObject = new JSONObject();
							jsonObject.put("id", pgmIdList.get(j).getEventId()+"");
							jsonObject.put("name", pgmIdList.get(j).getEventName());
							jsonObject.put("image", pgmIdList.get(j).getImage());
							jsonObject.put("subcategory", pgmIdList.get(j).getEventCategory());
							jsonObject.put("starttime", com.port.api.util.CommonUtil.timeFormat(pgmIdList.get(j).getStartTime()));
							jsonObject.put("url", pgmIdList.get(j).getEventSrc());
							jsonObject.put("collectionid", pgmIdList.get(j).getCollectionId()+"");
							jsonObject.put("collectionname", pgmIdList.get(j).getCollectionName());

							if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPV")){
								StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(pgmIdList.get(j).getProgramId(), 9,"event");
								if(info != null) {
									if(info.getStatus() == 9) {
										subscribe = true;
									} else {
										subscribe = false;
									}
								} else {
									subscribe = false;
								}
								jsonObject.put("subscribe", subscribe);
								jsonObject.put("price", pgmIdList.get(j).getPrice()+"");
							}

							boolean like = false;
							StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", pgmIdList.get(j).getEventId(), 1,"event");
							if(likeInfo == null){
								like = false;
							} else {
								like = true;
							}
							jsonObject.put("like", like);
							boolean record = false;
							jsonObject.put("record", record);

							jsonArray.put(jsonObject);
						}
						data.put("eventList", jsonArray);
						data.put("result", "success");
						data.put("start", start+"");
						data.put("limit", resultset+"");
						resp.put("params",data);
						returner.add(method+"sendEventList", resp,"messageActivity");
						returner.send();
					}else {
						data.put("result", "failure");
						data.put("start", start+"");
						data.put("limit", resultset+"");
						resp.put("params",data);
						returner.add(method+"sendEventList", resp,"messageActivity");
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

	//for Getting Favourite Services and Events, consumer : ?
	private void sendFavouriteList(int start, int resultset){
		try{
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			JSONArray jsonServiceArray = new JSONArray();
			JSONArray jsonEventArray = new JSONArray();
			JSONObject jsonObject = null;

			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
			
			int userId = 0;
//			int userId=CacheData.getUserId();
//			if(Constant.DEBUG)  Log.d(TAG, "User id from CacheData " + userId);

			if(userId==0){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					userId = Integer.valueOf(info.getProfile());
					CacheData.setUserId(userId);
				}
			}
			if(Constant.DEBUG)  Log.d(TAG, "User id used in " +TAG + userId);
			
			ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
			CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));


			List<ProgramInfo> favouritePrograms = null;
			List<ChannelInfo> favouriteServices = null;

			//check if cache is there, if not do the following
			favouritePrograms = getFavouritesEvents(start, resultset);
			favouriteServices = getFavouritesServices(start, resultset);
			if(Constant.DEBUG)  Log.d(TAG , "favouritePrograms Size: "+favouritePrograms.size());
			if(favouriteServices != null && favouriteServices.size() > 0){
				for(ChannelInfo serviceInfo : favouriteServices){
					if(serviceInfo != null){
						jsonObject = new JSONObject();
						int serviceId = serviceInfo.getServiceId();
						jsonObject.put("id", serviceId+"");
						jsonObject.put("name", serviceInfo.getChannelName());
						//						jsonObject.put("image", serviceInfo.getChannelLogo());
						jsonObject.put("category", serviceInfo.getServiceCategory());
						jsonObject.put("servicetype", serviceInfo.getType());
						jsonObject.put("event", "false");
						jsonObject.put("serviceid", serviceId+"");
						boolean subscribe = false;
						
//						int userId=CacheData.getUserId();
//						if(userId==0){
//							CacheGateway cache  = new CacheGateway(Port.c);
//							CacheInfo info = cache.getCacheInfo(1000);
//							if (info != null) {
//								userId = Integer.valueOf(info.getProfile());
//								CacheData.setUserId(userId);
//							}
//						}
						
						String pricingmodel = serviceInfo.getPriceModel();
						jsonObject.put("pricingmodel", pricingmodel);
						jsonObject.put("price", serviceInfo.getPrice()+"");
						if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPC")){
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
						}
						jsonObject.put("subscribe", subscribe);
						boolean lock = false;
						StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", serviceId, 2,"service");
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
						jsonObject.put("source", serviceInfo.getChannelurl());
						jsonServiceArray.put(jsonObject);
					}
				}
				data.put("serviceList", jsonServiceArray);
			} else {
				data.put("serviceList", new JSONArray());
			}
			jsonEventArray = new JSONArray(); //add
			if(favouritePrograms != null && favouritePrograms.size() > 0){
				
				ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
				for(ProgramInfo externalEventInfo : favouritePrograms){
					if(externalEventInfo != null){
						jsonObject = new JSONObject();
						int serviceId = externalEventInfo.getChannelServiceId();
						ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(serviceId);
						jsonObject.put("serviceid", serviceId+"");
						jsonObject.put("category", serviceInfo.getServiceCategory());
						jsonObject.put("servicetype", serviceInfo.getType());
						jsonObject.put("event", "true");

						boolean subscribe = false;
						
//						int userId=CacheData.getUserId();
//						if(userId==0){
//							CacheGateway cache  = new CacheGateway(Port.c);
//							CacheInfo info = cache.getCacheInfo(1000);
//							if (info != null) {
//								userId = Integer.valueOf(info.getProfile());
//								CacheData.setUserId(userId);
//							}
//						}

						int eventId = externalEventInfo.getEventId();
						jsonObject.put("id", eventId+"");
						jsonObject.put("name", externalEventInfo.getEventName());
						//						jsonObject.put("image", externalEventInfo.getImage());
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
						}
						jsonObject.put("subscribe", subscribe);

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
					jsonEventArray.put(jsonObject);
				}
			}
			//cache jsonArray as favouritePrograms
			//put resultset from start in data 
			if (jsonEventArray.length() > 0  || jsonServiceArray.length() > 0) {
				data.put("eventList", jsonEventArray);
				data.put("result", "success");
				resp.put("params",data);
				returner.add(method+"sendFavouriteList", resp,"messageActivity");
				returner.send();
			}else{
				data.put("eventList", new JSONArray());
				data.put("result", "failure");
				resp.put("params",data);
				returner.add(method+"sendFavouriteList", resp,"messageActivity");
				returner.send();
			}

		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}

	// send Event Full Info, consumer : ?
	private void sendEventInfo(int eventId) throws JSONException {
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		try{
			ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;

			String pricingmodel="";
			int serviceId;

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
			
			ProfileGateway profileInfoGateway = new ProfileGateway(getApplicationContext());
			CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo(Integer.toString(userId)));

			ChannelInfo chlInfo = null;
			ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventId);
			if(selectedEventInfo != null){
				pricingmodel = selectedEventInfo.getPriceModel();
				serviceId = selectedEventInfo.getChannelServiceId();
				chlInfo = channelGateway.getServiceInfoByServiceId(serviceId);
				boolean subscribe = false;
				if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPC")){
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
					data.put("subscribe", subscribe);
				}else if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPV")){
					StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(selectedEventInfo.getProgramId(), 9,"event");
					if(info != null) {
						if(info.getStatus() == 9) {
							subscribe = true;
						} else {
							subscribe = false;
						}
					} else {
						subscribe = false;
					}
					data.put("subscribe", subscribe);
				}

				boolean lock = false;
				StatusInfo lockInfo = statusGateway.getStatusInfoByServiceId(userId+"", serviceId, 2,"service");

				if(lockInfo == null){
					lock = false;
				} else {
					if(lockInfo.getStatus() == 2){
						lock = true;
					} else {
						lock = false;
					}
				}
				data.put("lock", lock);
				boolean like = false;
				StatusInfo likeInfo = statusGateway.getStatusInfoByServiceId(userId+"", eventId, 1,"event");
				if(likeInfo == null){
					like = false;
				} else {
					like = true;
				}
				data.put("like", like);

				boolean record = false;
				StatusInfo recordInfo = statusGateway.getStatusInfoByServiceId(userId+"", eventId, 4,"event");
				if(recordInfo == null){
					record = false;
				} else {
					record = true;
				}
				data.put("record", record);
			}

			if(Catalogue.EventList.size() > 0){
				for (int i = 0; i < Catalogue.EventList.size(); i++) {
					if(Catalogue.EventList.get(i).getEventId() == eventId){
						if(Constant.DEBUG)  Log.d(TAG , "eventId is Present in Catalogue.EventList.");
						data.put("id", Catalogue.EventList.get(i).getEventId());
						data.put("servicetype", Catalogue.EventList.get(i).getChannelType());
						data.put("serviceid", Catalogue.EventList.get(i).getChannelServiceId());
						data.put("channelPrice", chlInfo.getPrice());
						data.put("url", Catalogue.EventList.get(i).getEventSrc());
						data.put("name", Catalogue.EventList.get(i).getEventName());
						data.put("releasedate", Catalogue.EventList.get(i).getDateAdded());
						data.put("actors", Catalogue.EventList.get(i).getActors());
						data.put("rating", Catalogue.EventList.get(i).getRating());
						data.put("genre", Catalogue.EventList.get(i).getGenre());
						data.put("pricingmodel", Catalogue.EventList.get(i).getPriceModel());
						//						data.put("image", Catalogue.EventList.get(i).getImage());
						data.put("description", Catalogue.EventList.get(i).getDescription());
						data.put("director", Catalogue.EventList.get(i).getDirector());
						data.put("production", Catalogue.EventList.get(i).getProductionHouse());
						data.put("musicdirector", Catalogue.EventList.get(i).getMusicDirector());
						data.put("price", Catalogue.EventList.get(i).getPrice());
						sendResponse.put("params", data);
						returner.add(method+"sendEventInfo", sendResponse,"messageActivity");
						returner.send();
					}
				}
			}else{
				if(selectedEventInfo != null){
					if(!Attributes.isEventLocked(selectedEventInfo.getEventId())){
						if(Constant.DEBUG)  Log.d(TAG , "eventId is Present in Catalogue.EventList.");
						data.put("id", selectedEventInfo.getEventId());
						data.put("servicetype", selectedEventInfo.getChannelType());
						data.put("serviceid", selectedEventInfo.getChannelServiceId());
						data.put("channelPrice", chlInfo.getPrice());
						data.put("url", selectedEventInfo.getEventSrc());
						data.put("name", selectedEventInfo.getEventName());
						data.put("releasedate", selectedEventInfo.getDateAdded());
						data.put("actors", selectedEventInfo.getActors());
						data.put("rating", selectedEventInfo.getRating());
						data.put("genre", selectedEventInfo.getGenre());
						data.put("pricingmodel",selectedEventInfo.getPriceModel());
						//						data.put("image", selectedEventInfo.getImage());
						data.put("description", selectedEventInfo.getDescription());
						data.put("director", selectedEventInfo.getDirector());
						data.put("production", selectedEventInfo.getProductionHouse());
						data.put("musicdirector", selectedEventInfo.getMusicDirector());
						data.put("price", selectedEventInfo.getPrice());
						sendResponse.put("params", data);
						returner.add(method+"sendEventInfo", sendResponse,"messageActivity");
						returner.send();
					}else{
						String msg = this.getResources().getString(R.string.LOCK_MESSAGE);
						data.put("msg", msg);
						sendResponse.put("params",data);
						returner.add(method+"sendEventInfo", sendResponse,"messageActivity");
						returner.send();
					}
				} else {
					if(Constant.DEBUG)  Log.d(TAG , "selectedEventInfo is null.");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}	

	/**********************************************************/

	public static List<ChannelInfo> getFavouritesServices(int start, int resultset) {
		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		
		ChannelGateway sgate = new ChannelGateway(Port.c.getApplicationContext()) ;
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
		if(Constant.DEBUG)  Log.d(TAG , "Getting favourite channels for " + userId);

		try {
			//			list = CacheData.getServiceInfo("favourite");
			if(Catalogue.ServiceList.size() > 0){				
				if(Constant.DEBUG)  Log.d(TAG , "Getting channel info from cache");

				List<StatusInfo> likeInfos = statusGateway.getAllStatusInfoByUserId(userId+"",1,"Service");
				if (likeInfos != null && likeInfos.size() > 0) {
					if(start <= likeInfos.size()){
						int size = 0;
						if((likeInfos.size() - start) > resultset){
							size = resultset;
						}else{
							size = likeInfos.size() - start;
						}
						for (int j = 0; j < Catalogue.ServiceList.size(); j++) {
							for(int i = start; i < size; i++) {
								if (likeInfos.get(i) != null) {
									if (Catalogue.ServiceList.get(j).getServiceId() == likeInfos.get(i).getServiceId()) {
										list.add(Catalogue.ServiceList.get(j));
									}
								}
							}
						}
					}
				}
				//				CacheData.setServiceInfo(list, "favourite");
				return list;
			}else{
				if(Constant.DEBUG)  Log.d(TAG , "Getting channel info from table");

				List<StatusInfo> likeInfos = statusGateway.getAllStatusInfoByUserId(userId+"",1,"Service");
				if (likeInfos != null && likeInfos.size() > 0) {
					if(start <= likeInfos.size()){
						int size = 0;
						if((likeInfos.size() - start) > resultset){
							size = resultset;
						}else{
							size = likeInfos.size() - start;
						}
						for(int i = start; i < size; i++) {
							if (likeInfos.get(i) != null) {
								ChannelInfo Info = sgate.getServiceInfoByServiceId(likeInfos.get(i).getServiceId());
								if (Info != null) {
									list.add(Info);
								}
							}
						}
					}
				}
				return list;
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			return list;
		}
	}


	public static List<ProgramInfo> getFavouritesEvents(int start, int resultset) {
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		
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
		if(Constant.DEBUG)  Log.d(TAG , "Getting favourite events for " + userId);

		if(Catalogue.EventList.size() > 0){
			List<StatusInfo> likeInfos = statusGateway.getAllStatusInfoByUserId(userId+"",1,"event");
			if (likeInfos != null && likeInfos.size() > 0) {
				if(Constant.DEBUG)  Log.d(TAG , "Getting program info from cache");

				if(start <= likeInfos.size()){
					int size = 0;
					if((likeInfos.size() - start) > resultset){
						size = resultset;
					}else{
						size = likeInfos.size() - start;
					}
					for (int j = 0; j < Catalogue.EventList.size(); j++) {
						for (int i = start; i < size; i++) {
							if (likeInfos.get(i) != null) {
								if (Catalogue.EventList.get(j).getEventId() == likeInfos.get(i).getEventId()) {
									list.add(Catalogue.EventList.get(j));
								}
							}
						}
					}
				}
			}
			return list;
		}else{
			List<StatusInfo> likeInfos = statusGateway.getAllStatusInfoByUserId(userId+"",1,"event");
			if (likeInfos != null && likeInfos.size() > 0) {
				if(Constant.DEBUG)  Log.d(TAG , "Getting program info from program table");

				if(start <= likeInfos.size()){
					int size = 0;
					if((likeInfos.size() - start) > resultset){
						size = resultset;
					}else{
						size = likeInfos.size() - start;
					}
					for (int i = start; i < size; i++) {
						if (likeInfos.get(i) != null) {
							ProgramInfo programInfo = programGateway.getProgramInfoByEventId(likeInfos.get(i).getEventId());
							if (programInfo != null) {
								list.add(programInfo);
							}
						}
					}
				}
			}
			return list;
		}
	}

	public String BitMapToString(Bitmap bitmap){
		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		byte [] arr=baos.toByteArray();
		String result=Base64.encodeToString(arr, Base64.DEFAULT);
		return result;
	}

	private LruCache<String, Bitmap> mMemoryCache;
	public Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}


	private void sendBouquetList(int start, int resultset) throws JSONException {
		try{
			List<BouquetInfo> tempList = new ArrayList<BouquetInfo>();
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = null;
			//check if available in cache, if not do the following
			if(Catalogue.BouquetList.size() > 0){
				for (int i = 0; i < Catalogue.BouquetList.size(); i++) {
					tempList.add(Catalogue.BouquetList.get(i));
				}
				if(Constant.DEBUG)  Log.d(TAG , "from cache");
				if(start <= tempList.size()){
					int size = 0;
					if((tempList.size() - start) > resultset){
						size = resultset;
					}else{
						size = tempList.size() - start;
					}
					if(Constant.DEBUG)  Log.d(TAG , "start "+start+", size "+size);
					for (int j = start; j < size; j++) {
					//for (int j = start; j < start+20; j++) {
						//if(tempList != null && tempList.size()>j){
						if(tempList != null){
							jsonObject = new JSONObject();
							jsonObject.put("id", tempList.get(j).getBouquetId()+"");
							jsonObject.put("name", tempList.get(j).getBouquetName());
							jsonObject.put("image", "");
							jsonArray.put(jsonObject);
						}
					}
					data.put("bouquetList", jsonArray);
					data.put("result", "success");
					data.put("start", start+"");
					data.put("limit", resultset+"");
					resp.put("params",data);
					returner.add(method+"sendBouquetList", resp,"messageActivity");
					returner.send();
				}else{
					data.put("result", "failure");
					data.put("start", start+"");
					data.put("limit", resultset+"");
					resp.put("params",data);
					returner.add(method+"sendBouquetList", resp,"messageActivity");
					returner.send();
				}

			}else{
				if(Constant.DEBUG)  Log.d(TAG , "from database");
				BouquetGateway bouquetInfoGateway =new BouquetGateway(Port.c.getApplicationContext()) ;
				
				List<BouquetInfo> bouquetInfos = bouquetInfoGateway.getAllBouquetInfo();

				if(start <= bouquetInfos.size()){
					int size = 0;
					if((bouquetInfos.size() - start) > resultset){
						size = resultset;
					}else{
						size = bouquetInfos.size() - start;
					}
					if(Constant.DEBUG)  Log.d(TAG , "start "+start+", size "+size);
					for (int i = start; i < size; i++) {
						if(bouquetInfos != null){
							jsonObject = new JSONObject();
							jsonObject.put("id", bouquetInfos.get(i).getBouquetId()+"");
							jsonObject.put("name", bouquetInfos.get(i).getBouquetName());
							jsonObject.put("image", "");
							jsonArray.put(jsonObject);
						}
					}
					data.put("bouquetList", jsonArray);
					data.put("result", "success");
				} else {
					data.put("result", "failure");
				}			
				resp.put("params",data);
				returner.add(method+"sendBouquetList", resp,"messageActivity");  //messageService
				returner.send();
				//put data in cache as bouquetList
			}

		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}

	private void sendDVBBouquetList(int start, int resultset) throws JSONException {
		try{
			List<BouquetInfo> tempList = new ArrayList<BouquetInfo>();
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = null;
			//check if available in cache, if not do the following
			if(Constant.DVB){
				
				BouquetGateway bouquetInfoGateway =new BouquetGateway(Port.c.getApplicationContext()) ;
				
				List<BouquetInfo> bouquetInfos = bouquetInfoGateway.getAllBouquetInfo();
				List<String>BList =  new ArrayList<String>();
				boolean flag= false;
				if(start <= bouquetInfos.size()){
					int size = 0;
					if((bouquetInfos.size() - start) > resultset){
						size = resultset;
					}else{
						size = bouquetInfos.size() - start;
					}
					if(Constant.DEBUG)  Log.d(TAG , "start "+start+", size "+size);
					for (int i = start; i < size; i++) {
						if(bouquetInfos != null){
					/*for (int i = start; i < start+10; i++) {
						if(bouquetInfos != null&& bouquetInfos.size()> i){*/
					//Edited By Tomesh for Removal for dublication of Bouquet Names 		
							String clubbedID = bouquetInfos.get(i).getBouquetId()+"";
							for(int j = i+1 ; j < size ; j++)
								if(bouquetInfos.get(i).getBouquetName().equalsIgnoreCase(bouquetInfos.get(j).getBouquetName())){
									BList.add(bouquetInfos.get(i).getBouquetName());
									clubbedID += ":" + bouquetInfos.get(j).getBouquetId() ;
									flag=true;
									if(Constant.DEBUG)  Log.d(TAG + " BouquetID : clubbedID :" , clubbedID);
								} 
							if(!BList.contains(bouquetInfos.get(i).getBouquetName())){
									jsonObject = new JSONObject();
									jsonObject.put("id", bouquetInfos.get(i).getBouquetId()+"");
									jsonObject.put("name", bouquetInfos.get(i).getBouquetName());
									jsonObject.put("image", "");
									jsonArray.put(jsonObject);
							}else if(flag){
									flag=false;
									jsonObject = new JSONObject();
									jsonObject.put("id", clubbedID);
									jsonObject.put("name", bouquetInfos.get(i).getBouquetName());
									jsonObject.put("image", "");
									jsonArray.put(jsonObject);
							}
						}
					}
					data.put("bouquetList", jsonArray);
					data.put("result", "success");
				} else {
					data.put("result", "failure");
				}			
				resp.put("params",data);
				returner.add(method+"sendBouquetList", resp,"messageActivity");  //messageService
				returner.send();
				//put data in cache as bouquetList
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}

	private void freeContent(int id, String pricingModel, String type){
		String stringURL = Constant.SUBSCRIBE_UNSUBSCRIBE_SERVICE;
		
		ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;

		if (type.equalsIgnoreCase("event")) {
			ProgramInfo eventInfo = programGateway.getProgramInfoByEventId(id);
			stringURL += "?chid="+eventInfo.getProgramId();
		}else{
			ChannelInfo serviceInfo = channelGateway.getServiceInfoByServiceId(id);
			stringURL += "?chid="+serviceInfo.getServiceId();
		}
		stringURL += "&type=subscribe";
		String subscriberid = CacheData.getSubscriberId();
		if(subscriberid!= null && !subscriberid.equalsIgnoreCase("")) {
			stringURL += "&subscriberid="+subscriberid;
		}

		if(pricingModel != null && !(pricingModel.trim().equals(""))) {
			stringURL += "&pricingmodel="+pricingModel;
		}
		stringURL += "&adminPassword=FREE";
		Subscription.freeSubscription(stringURL);
	}

	private void checkSubscribedData(int eventId){
		try{
			ArrayList<String> ProgramSubscribeList = new ArrayList<String>();
			ArrayList<String> channelSubscribeList = new ArrayList<String>();
			JSONObject sendResponse = new JSONObject();
			JSONObject data = new JSONObject();

			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;

			String subscriberId = CacheData.getSubscriberId();
			if(Constant.DEBUG) Log.d(TAG,"subscriberId : "+subscriberId);
			if(!subscriberId.equalsIgnoreCase("") && subscriberId != null){
				JSONObject obj = Subscription.SubscribedData(subscriberId);
				JSONObject objData = obj.getJSONObject("data");
				JSONArray subProData = objData.getJSONArray("eventIdList");
				for(int i = 0; i < subProData.length(); i++){
					ProgramSubscribeList.add(subProData.getString(i));
					Log.i(TAG, i+" --- "+ProgramSubscribeList.get(i));
				}
				JSONArray subChnData = objData.getJSONArray("channelIdList");
				for(int i = 0; i < subChnData.length(); i++){
					channelSubscribeList.add(subChnData.getString(i));
					Log.i(TAG, i+" --- "+channelSubscribeList.get(i));
				}
			}

			ProgramInfo pgmInfo = programGateway.getProgramInfoByEventId(eventId);
			String pricingModel = pgmInfo.getPriceModel();

			if(pricingModel.equalsIgnoreCase("PPC")){
				for (int i = 0; i < channelSubscribeList.size(); i++) {
					if(channelSubscribeList.get(i).equalsIgnoreCase(pgmInfo.getChannelServiceId()+"")){
						data.put("subscribe", "true");
						data.put("type", "channel");
						data.put("id", eventId+"");
						sendResponse.put("params", data);
						returner.add(method+"checkSubscribedData", sendResponse,"messageActivity");
						returner.send();
					}else{
						data.put("subscribe", "false");
						data.put("type", "channel");
						data.put("id", eventId+"");
						sendResponse.put("params", data);
						returner.add(method+"checkSubscribedData", sendResponse,"messageActivity");
						returner.send();
					}
				}
			}else if(pricingModel.equalsIgnoreCase("PPV")){
				for (int i = 0; i < ProgramSubscribeList.size(); i++) {
					if(ProgramSubscribeList.get(i).equalsIgnoreCase(eventId+"")){
						data.put("subscribe", "true");
						data.put("type", "event");
						data.put("id", eventId+"");
						sendResponse.put("params", data);
						returner.add(method+"checkSubscribedData", sendResponse,"messageActivity");
						returner.send();
					}else{
						data.put("subscribe", "false");
						data.put("type", "event");
						data.put("id", eventId+"");
						sendResponse.put("params", data);
						returner.add(method+"checkSubscribedData", sendResponse,"messageActivity");
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

}
