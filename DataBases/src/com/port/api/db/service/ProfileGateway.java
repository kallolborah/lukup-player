package com.port.api.db.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.port.api.db.util.CacheData;
import com.port.api.db.util.CommonUtil;
//import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;


public class ProfileGateway {
	private static final String TAG = "ProfileGateway";

	private static final String AUTHORITY = "com.port.apps.epg.contentprovider" ;
	public static final Uri CONTENT_URI_PROFILE = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.PROFILE_TABLE);

	private Context mContext ;
	
	/**
	 * Field declaration.
	 */
//	private SQLiteDatabase database;

	// 11 Field
	private String[] allColumns = { Store.PROFILE_USER_ID,
			Store.PROFILE_USER_NAME, Store.PROFILE_IMAGE_PWD,
			Store.PROFILE_FB_ID ,Store.PROFILE_FB_TOKEN,
			Store.PROFILE_FB_SYNC,Store.PROFILE_FB_PHOTO,Store.PROFILE_SUBSCRIBER_ID,
			Store.PROFILE_SUB_STATUS, Store.PROFILE_LAST_VIEWED_SERVICE,
			Store.PROFILE_LAST_VIEWED,Store.PROFILE_NETWORK_ID};
	
//	public ProfileGateway(SQLiteDatabase database,Context argContext){
//		this.database=database;
//		this.mContext=argContext ;
//	}
	
	public ProfileGateway(Context argContext){
		this.mContext=argContext ;
	}
	
	public long insertProfileInfo(int userId, String userName,String imagePwd,String FBId,String FBToken, int FBSync,String FBPhoto,String subscriberId, int subStatus, int lastViewService, int lastView,String network) {
		ContentValues values = new ContentValues();
		values.put(Store.PROFILE_USER_ID, userId);
		values.put(Store.PROFILE_USER_NAME, userName);
		values.put(Store.PROFILE_IMAGE_PWD, imagePwd);
		values.put(Store.PROFILE_FB_ID, FBId);
		values.put(Store.PROFILE_FB_TOKEN, FBToken);
		values.put(Store.PROFILE_FB_SYNC, FBSync);
		values.put(Store.PROFILE_FB_PHOTO, FBPhoto);
		values.put(Store.PROFILE_SUBSCRIBER_ID, subscriberId);
		values.put(Store.PROFILE_SUB_STATUS, subStatus);
		values.put(Store.PROFILE_LAST_VIEWED_SERVICE, lastViewService);
		values.put(Store.PROFILE_LAST_VIEWED, lastView);
		values.put(Store.PROFILE_NETWORK_ID, network);

		mContext.getContentResolver().insert(CONTENT_URI_PROFILE, values) ;
		return 0;
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
//			//LogUtil.log(TAG, "database is not null and opened.");
//		}
//
//		try{
//			return database.insert(Store.PROFILE_TABLE, null, values);
//		}catch(Exception e){
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//			return 0;
//		}
	}
	
	
	public int updateProfileInfo (int userId, String userName,String imagePwd,String FBId,String FBToken, int FBSync,String FBPhoto,String subscriberId, int subStatus, int lastViewService, int lastView,String network) {
		ContentValues values = new ContentValues();
		values.put(Store.PROFILE_USER_ID, userId);
		values.put(Store.PROFILE_USER_NAME, userName);
		values.put(Store.PROFILE_IMAGE_PWD, imagePwd);
		values.put(Store.PROFILE_FB_ID, FBId);
		values.put(Store.PROFILE_FB_TOKEN, FBToken);
		values.put(Store.PROFILE_FB_SYNC, FBSync);
		values.put(Store.PROFILE_FB_PHOTO, FBPhoto);
		values.put(Store.PROFILE_SUBSCRIBER_ID, subscriberId);
		values.put(Store.PROFILE_SUB_STATUS, subStatus);
		values.put(Store.PROFILE_LAST_VIEWED_SERVICE, lastViewService);
		values.put(Store.PROFILE_LAST_VIEWED, lastView);
		values.put(Store.PROFILE_NETWORK_ID, network);

//		return database.update(Store.PROFILE_TABLE, values, Store.PROFILE_USER_ID + " = " +userId, null);
		
		int row = mContext.getContentResolver().update(CONTENT_URI_PROFILE, values,Store.PROFILE_USER_ID + " = " +userId, null);
		return row;
	}
	
	public List<ProfileInfo> getAllProfileInfo() {
		List<ProfileInfo> profileInfos = new ArrayList<ProfileInfo>();
		Cursor cursor;
		try {
			if(mContext!=null){
				Log.i("ContentProvider", "mContext is not null");
			}
			if(mContext.getContentResolver()!=null){
				Log.i("ContentProvider", "mContext.getContentResolver() is not null");
			}
			cursor=mContext.getContentResolver().query(CONTENT_URI_PROFILE, 
					allColumns, null, null, null);
			Log.i("ContentProvider", ""+cursor.getCount());
			if (cursor != null){
				if (cursor.moveToFirst()) {
					do {
						ProfileInfo profileInfo = cursorToProfileInfo(cursor);
						profileInfos.add(profileInfo); 
					} while (cursor.moveToNext());
				}
			}

//			if(database == null || !database.isOpen()){
//				if(CommonUtil.checkConnectionForLocaldb()){
//					database = CacheData.getDatabase();
//				} else {
//					if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//				}
//			} else {
//				//LogUtil.log(TAG, "database is not null and opened.");
//			}
//
//			cursor = this.database.query(Store.PROFILE_TABLE, allColumns, null, null, null, null, null);
//			if (cursor.moveToFirst()) {
//				do {
//					ProfileInfo profileInfo = cursorToProfileInfo(cursor);
//					profileInfos.add(profileInfo); 
//				} while (cursor.moveToNext());
//			}
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return profileInfos;
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
		return null;
	}
	
	private ProfileInfo cursorToProfileInfo(Cursor cursor) {
		ProfileInfo profileInfo = new ProfileInfo();
		profileInfo.setUserId(cursor.getInt(0));
		profileInfo.setUserName(cursor.getString(1));
		profileInfo.setImagePwd(cursor.getString(2));
		profileInfo.setfBId(cursor.getString(3));
		profileInfo.setFbToken(cursor.getString(4));
		profileInfo.setFbSync(cursor.getInt(5));
		profileInfo.setfBPhoto(cursor.getString(6));
		profileInfo.setSubscriberId(cursor.getString(7));
		profileInfo.setSubStatus(cursor.getInt(8));
		profileInfo.setLastViewService(cursor.getInt(9));
		profileInfo.setLastView(cursor.getInt(10));
		profileInfo.setNetwork(cursor.getString(11));

		return profileInfo;
	}
	
	public void updateProfileInfo (String network) {
		ContentValues values = new ContentValues();
		values.put(Store.PROFILE_NETWORK_ID, network);
		
		mContext.getContentResolver().update(CONTENT_URI_PROFILE, values, Store.PROFILE_USER_ID + " = 1000", null);

//		database.update(Store.PROFILE_TABLE, values, Store.PROFILE_USER_ID + " = 1000", null);
	}
	
	public void updataLastViewedInfo (int userId,int eventId,int serviceId) {
		ContentValues values = new ContentValues();
		values.put(Store.PROFILE_LAST_VIEWED_SERVICE, serviceId);
		values.put(Store.PROFILE_LAST_VIEWED, eventId);

		mContext.getContentResolver().update(CONTENT_URI_PROFILE, values, Store.PROFILE_USER_ID + " = "+userId, null);
//		database.update(Store.PROFILE_TABLE, values, Store.PROFILE_USER_ID + " = "+userId, null);
	}
	
	public ProfileInfo getProfileInfo(String userId) {
		
		Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_PROFILE, 
				allColumns,Store.PROFILE_USER_ID + " = ? ", new String[] {userId}, null);
		
		cursor.moveToFirst();
		ProfileInfo profileInfo = null;
		if (!cursor.isAfterLast()) {
			profileInfo = cursorToProfileInfo(cursor);
		}
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
//			//LogUtil.log(TAG, "database is not null and opened.");
//		}
//
//		Cursor cursor = database.query(Store.PROFILE_TABLE, allColumns, Store.PROFILE_USER_ID + " = ? ", new String[] {userId}, null, null, null);
//		if(Constant.DEBUG)  Log.d(TAG,"profile user id : "+userId);
//		cursor.moveToFirst();
//		ProfileInfo profileInfo = null;
//		if (!cursor.isAfterLast()) {
//			profileInfo = cursorToProfileInfo(cursor);
//		}
//		// Make sure to close the cursor
//		if (cursor != null && !cursor.isClosed()) {
//			cursor.deactivate();
//
//		}
//		cursor.close();
		return profileInfo;
	}
	
	
	
	public int deleteProfileInfo(int userId) {
//		return database.delete(Store.PROFILE_TABLE, Store.PROFILE_USER_ID+" = "+userId ,null);
		
		int row=mContext.getContentResolver().delete(CONTENT_URI_PROFILE, 
				Store.PROFILE_USER_ID+" = "+userId ,null);
		return row;
	}
	
	public int setFbStatus(String status, int userId){
		ContentValues values = new ContentValues();
		if (status.equalsIgnoreCase("connect")) {
			values.put(Store.PROFILE_FB_SYNC, 1);
		}else{
			values.put(Store.PROFILE_FB_SYNC, 0);
		}
//		return database.update(Store.PROFILE_TABLE, values, Store.PROFILE_USER_ID + " = "+userId ,null);
		
		int row = mContext.getContentResolver().update(CONTENT_URI_PROFILE, values, Store.PROFILE_USER_ID + " = "+userId ,null);
		return row;
	}
	
	public int renameProfileInfo(int userId,String name) {
		ContentValues values = new ContentValues();
		values.put(Store.PROFILE_USER_NAME, name);
		if(Constant.DEBUG)  Log.d(TAG , "renameProfileInfo() id: "+userId+", name: "+name);
//		return database.update(Store.PROFILE_TABLE, values, Store.PROFILE_USER_ID + " = "+userId ,null);
		
		int row = mContext.getContentResolver().update(CONTENT_URI_PROFILE, values, Store.PROFILE_USER_ID + " = "+userId ,null);
		return row;
	}
	
	public int setImageIdPassword(int userId, String imageIdPassword) {
		ContentValues values = new ContentValues();
		values.put(Store.PROFILE_IMAGE_PWD, imageIdPassword);
//		return database.update(Store.PROFILE_TABLE, values, Store.PROFILE_USER_ID + " = "+userId ,null);
		
		int row = mContext.getContentResolver().update(CONTENT_URI_PROFILE, values, Store.PROFILE_USER_ID + " = "+userId ,null);
		return row;
	}
	
}
