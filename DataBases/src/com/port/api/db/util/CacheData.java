package com.port.api.db.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

import com.facebook.android.Facebook;
import com.port.Port;
import com.port.api.db.service.BouquetGateway;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ContentProviderTest;
import com.port.api.db.service.ProfileGateway;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.service.Store;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;


public class CacheData {
	private static String TAG = "CacheData";
	
	private static Activity mainActivity = null;
	private static String subscriberId = "";
	private static int userId;
	private static String operaterName = "";
	private static String distributorId = "";
	private static String distributorPwd = "";
	
//	private static ProfileGateway profileGateway;
//	private static BouquetGateway bouquetGateway;
//	private static StatusGateway statusGateway;
//	private static ChannelGateway channelGateway;
//	private static ProgramGateway programGateway;
	
	private static ProfileInfo selectedProfileInfo;
	
	private static boolean playing;
	
	private static String homeVisibility = "";
	
	private static Facebook facebook;
	
	private static String wifiRemoteCaller="";
	private static String wifiDisplayName="";
	private static String connecteda2dpdevice="";
	private static String connectedA2DPDeviceName="";
	
	private static String externalIp = "";
	private static String ServiceID="";
	private static String EventID="";
	private static String pricingmodel="";
	
//	private static String portVersion = "";
//	public static String getPortVersion() {
//		return portVersion;
//	}
//
//	public static void setPortVersion(String portVersion) {
//		CacheData.portVersion = portVersion;
//	}

	public static String getExternalIp() {
		//return externalIp;
		//return from content provider
		return "" ;
	}

	public static void setExternalIp(String externalIp) {
		//CacheData.externalIp = externalIp;
		//set in content provider
	}

	public static String getWifiDisplayName() {
		return wifiDisplayName;
	}

	public static void setWifiDisplayName(String wifiDisplayName) {
		CacheData.wifiDisplayName = wifiDisplayName;
	}

	public static String getWifiRemoteCaller() {
		return wifiRemoteCaller;
	}

	public static void setWifiRemoteCaller(String wifiRemoteCaller) {
		CacheData.wifiRemoteCaller = wifiRemoteCaller;
	}

	public static String getConnectedA2dpDevice(){
		return connecteda2dpdevice;
	}
	
	public static void setConnectedA2dpDevice(String address){
		connecteda2dpdevice = address;
	}
	
	public static String getConnectedA2DPDeviceName() {
		return connectedA2DPDeviceName;
	}

	public static void setConnectedA2DPDeviceName(String connectedA2DPDeviceName) {
		CacheData.connectedA2DPDeviceName = connectedA2DPDeviceName;
	}
	
	public static void Play(boolean status){
		playing = true;
	}
	
	public static boolean isPlaying(){
		return playing;
	}
	
	/**
	 * @return the homeVisibility
	 */
	public static String getHomeVisibility() {
		return homeVisibility;
	}
	/**
	 * @param homeVisibility the homeVisibility to set
	 */
	public static void setHomeVisibility(String homeVisibility) {
		CacheData.homeVisibility = homeVisibility;
	}
	
//	public static SQLiteDatabase getDatabase() {
//		return database;
//	}
//	public static void setDatabase(SQLiteDatabase database) {
//		CacheData.database = database;
//	}
//	
//	public static Store getDbHelper() {
//		return dbHelper;
//	}
//	
//	public static void setDbHelper(Store dbHelper) {
//		CacheData.dbHelper = dbHelper;
//	}
	
	public static Activity getActivity() {
		return mainActivity;
	}
	
	public static void setActivity(Activity activity) {
		mainActivity = activity;
	}
	
	public static String getSubscriberId() {
		/*if(subscriberId == null || subscriberId.trim().equals("")){
			ProfileGateway profileInfoGateway = CacheData.getProfileGateway();
			if(profileInfoGateway == null){
				if(CommonUtil.checkConnectionForLocaldb()){
					profileInfoGateway = new ProfileGateway(CacheData.getDatabase());
					CacheData.setProfileInfoGateway(profileInfoGateway);
				}
			}

			List<ProfileInfo> profileInfos = profileInfoGateway.getAllProfileInfo();

			if(profileInfos == null || profileInfos.size() < 1) {
				if(Constant.DEBUG)  Log.d(TAG , "no profile");
			} else {
				for(ProfileInfo profileInfo : profileInfos){
					if(profileInfo != null){
						if(Constant.DEBUG)  Log.d(TAG,"profileInfo name : "+profileInfo.getUserName());
						if(profileInfo.getUserName().trim().equalsIgnoreCase("Guest")){
							CacheData.setSubscriberId(profileInfo.getSubscriberId());
							subscriberId = CacheData.getSubscriberId();
						}
					}
				}
			}
		}*/
		return subscriberId;
	}

	public static void setSubscriberId(String subscriberId) {
		CacheData.subscriberId = subscriberId;
	}
	
	public static String getOperaterName() {
		if(operaterName == null || operaterName.trim().equals("")){
//			ProfileGateway profileInfoGateway = CacheData.getProfileGateway();
//			if(profileInfoGateway == null){
//				if(CommonUtil.checkConnectionForLocaldb()){
//					//profileInfoGateway = new ProfileGateway(CacheData.getDatabase());
//					CacheData.setProfileInfoGateway(profileInfoGateway);
//				}
//			}
			
			if(Port.c.getApplicationContext()==null){
				Log.i("CacheData", "Port.c.getApplicationContext() is null");
			}
			
			ProfileGateway profileInfoGateway = new ProfileGateway(Port.c.getApplicationContext());
			try{
				List<ProfileInfo> profileInfos = profileInfoGateway.getAllProfileInfo();
	
				if(profileInfos == null || profileInfos.size() < 1) {
					if(Constant.DEBUG)  Log.d(TAG , "no profile");
				} else {
					for(ProfileInfo profileInfo : profileInfos){
						if(profileInfo != null){
	//						if(Constant.DEBUG)  Log.d(TAG,"profileInfo name : "+profileInfo.getUserName());
							if(profileInfo.getUserName().trim().equalsIgnoreCase("Guest")){
								if(profileInfo.getNetwork() != null && !profileInfo.getNetwork().equalsIgnoreCase("")){
									CacheData.setOperaterName(profileInfo.getNetwork());
									operaterName = CacheData.getOperaterName();
								}
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
		return operaterName;
	}
	
	public static void setOperaterName(String operaterName) {
		CacheData.operaterName = operaterName;
	}
	
	public static int getUserId() {
//		if(userId == 0){
//			ProfileGateway profileInfoGateway = new ProfileGateway(Port.c.getApplicationContext());
//			
//			try{
//				List<ProfileInfo> profileInfos = profileInfoGateway.getAllProfileInfo();
//	
//				if(profileInfos == null || profileInfos.size() < 1) {
//					if(Constant.DEBUG)  Log.d(TAG , "no profile");
//				} else {
//					for(ProfileInfo profileInfo : profileInfos){
//						if(profileInfo != null){
//							if(Constant.DEBUG)  Log.d(TAG,"profileInfo name : "+profileInfo.getUserName());
//							if(profileInfo.getUserName().trim().equalsIgnoreCase("Guest")){
//								CacheData.setUserId(profileInfo.getUserId());
//								userId = CacheData.getUserId();
//								CacheData.setSelectedProfileInfo(profileInfoGateway.getProfileInfo("1000"));
//							}
//						}
//					}
//				}
//			}catch(Exception e){
//				e.printStackTrace();
//				StringWriter errors = new StringWriter();
//				e.printStackTrace(new PrintWriter(errors));
//				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//		    }
//		}
		return userId;
	}
	public static void setUserId(int userId) {
		CacheData.userId = userId;
	}
	
//	public static ProfileGateway getProfileGateway() {
//		return profileGateway;
//	}
//	/**
//	 * @param profileInfoGateway the profileInfoGateway to set
//	 */
//	public static void setProfileInfoGateway(ProfileGateway profileGateway) {
//		CacheData.profileGateway = profileGateway;
//	}
//	
//	/**
//	 * @return the bouquetGateway
//	 */
//	public static BouquetGateway getBouquetGateway() {
//		return bouquetGateway;
//	}
//	/**
//	 * @param bouquetGateway the bouquetGateway to set
//	 */
//	public static void setBouquetGateway(BouquetGateway bouquetGateway) {
//		CacheData.bouquetGateway = bouquetGateway;
//	}
//	/**
//	 * @return the statusGateway
//	 */
//	public static StatusGateway getStatusGateway() {
//		return statusGateway;
//	}
//	/**
//	 * @param statusGateway the statusGateway to set
//	 */
//	public static void setStatusGateway(StatusGateway statusGateway) {
//		CacheData.statusGateway = statusGateway;
//	}
//	/**
//	 * @return the channelGateway
//	 */
//	public static ChannelGateway getChannelGateway() {
//		return channelGateway;
//	}
//	/**
//	 * @param channelGateway the channelGateway to set
//	 */
//	public static void setChannelGateway(ChannelGateway channelGateway) {
//		CacheData.channelGateway = channelGateway;
//	}
//	/**
//	 * @return the programGateway
//	 */
//	public static ProgramGateway getProgramGateway() {
//		return programGateway;
//	}
//	/**
//	 * @param programGateway the programGateway to set
//	 */
//	public static void setProgramGateway(ProgramGateway programGateway) {
//		CacheData.programGateway = programGateway;
//	}
	
	/**
	 * @return the selectedProfileInfo
	 */
	public static ProfileInfo getSelectedProfileInfo() {
		return selectedProfileInfo;
	}

	/**
	 * @param selectedProfileInfo the selectedProfileInfo to set
	 */
	public static void setSelectedProfileInfo(ProfileInfo selectedProfileInfo) {
		CacheData.selectedProfileInfo = selectedProfileInfo;
	}
	
	public static void expireCache(String tag){
		
	}
	
	public static Facebook getFacebook() {
		return facebook;
	}

	/**
	 * @param facebook the facebook to set
	 */
	public static void setFacebook(Facebook facebook) {
		CacheData.facebook = facebook;
	}

	public static String getDistributorId() {
		return distributorId;
	}

	public static void setDistributorId(String distributorId) {
		CacheData.distributorId = distributorId;
	}

	public static String getDistributorPwd() {
		return distributorPwd;
	}

	public static void setDistributorPwd(String distributorPwd) {
		CacheData.distributorPwd = distributorPwd;
	}

	public static String getCurrentServiceID() {
		return ServiceID;
	}

	public static void setCurrentServiceID(String serviceID) {
		ServiceID = serviceID;
	}

	public static String getCurrentEventID() {
		return EventID;
	}

	public static void setCurrentEventID(String eventID) {
		EventID = eventID;
	}

	public static String getCurrentPricingmodel() {
		return pricingmodel;
	}

	public static void setCurrentPricingmodel(String pricingmodel) {
		CacheData.pricingmodel = pricingmodel;
	}
	
}
