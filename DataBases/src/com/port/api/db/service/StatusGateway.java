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

public class StatusGateway {
	private static final String TAG = "StatusGateway";

	private static final String AUTHORITY = "com.port.apps.epg.contentprovider" ;
	public static final Uri CONTENT_URI_STATUS = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.STATUS_TABLE);

	private Context mContext ;
	
	/**
	 * Field declaration.
	 */
//	private SQLiteDatabase database;
    
	// 6 Fields
	private String[] allColumns = { Store.STATUS_USER_ID,
			Store.STATUS_SERVICE_ID, Store.STATUS_EVENT_ID,
			Store.STATUS_UNIQUE_ID,Store.STATUS_STATUS ,
			Store.STATUS_FREQUENCY,Store.STATUS_LAST_VIEWED ,
			Store.STATUS_DATE,Store.STATUS_TIMESTAMP};
	
//	public StatusGateway(SQLiteDatabase database,Context argContext){
//		this.database=database;
//		this.mContext=argContext ;
//	}
	
	public StatusGateway(Context argContext){
		this.mContext=argContext ;
	}
    
	public long insertStatusInfo(int userId,int serviceId, int eventId, int uniqueId,int status, int statusFreq, int lastViewed,String date,long timestamp) {
		ContentValues values = new ContentValues();
		values.put(Store.STATUS_USER_ID, userId);
		values.put(Store.STATUS_SERVICE_ID, serviceId);
		values.put(Store.STATUS_EVENT_ID, eventId);
		values.put(Store.STATUS_UNIQUE_ID, uniqueId);
		values.put(Store.STATUS_STATUS, status);
		values.put(Store.STATUS_FREQUENCY, statusFreq);
		values.put(Store.STATUS_LAST_VIEWED, lastViewed);
		values.put(Store.STATUS_DATE, date);
		values.put(Store.STATUS_TIMESTAMP, timestamp);

		mContext.getContentResolver().insert(CONTENT_URI_STATUS, values) ;
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
//			return database.insert(Store.STATUS_TABLE, null, values);
//		}catch(Exception e){
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//			return 0;
//		}
	}
	
	public int updateStatusInfo (int userId,int serviceId, int eventId, int uniqueId,int status, int statusFreq, int lastViewed,String date,long timestamp) {
		ContentValues values = new ContentValues();
		values.put(Store.STATUS_USER_ID, userId);
		values.put(Store.STATUS_SERVICE_ID, serviceId);
		values.put(Store.STATUS_EVENT_ID, eventId);
		values.put(Store.STATUS_UNIQUE_ID, uniqueId);
		values.put(Store.STATUS_STATUS, status);
		values.put(Store.STATUS_FREQUENCY, statusFreq);
		values.put(Store.STATUS_LAST_VIEWED, lastViewed);
		values.put(Store.STATUS_DATE, date);
		values.put(Store.STATUS_TIMESTAMP, timestamp);

//		return database.update(Store.STATUS_TABLE, values, Store.STATUS_USER_ID + " = " +userId, null);
		
		int row = mContext.getContentResolver().update(CONTENT_URI_STATUS, values,Store.STATUS_USER_ID + " = " +userId, null);
		return row;
		
	}
	
//	CREATE TABLE Status( User_id integer, Channel_id integer, Event_id integer, Status integer,
//			Frequency integer, Last_viewed integer);
	
	private StatusInfo cursorToStatusInfo(Cursor cursor) {
		StatusInfo statusInfo = new StatusInfo();
		statusInfo.setUserId(cursor.getInt(0));
		statusInfo.setServiceId(cursor.getInt(1));
		statusInfo.setEventId(cursor.getInt(2));
		statusInfo.setUniqueId(cursor.getInt(3));
		statusInfo.setStatus(cursor.getInt(4));
		statusInfo.setStatusFreq(cursor.getInt(5));
		statusInfo.setLastViewed(cursor.getInt(6));
		statusInfo.setDate(cursor.getString(7));
		statusInfo.setTimeStamp(cursor.getLong(8));

		return statusInfo;
	}
	
	public List<StatusInfo> getAllStatusInfo() {
		List<StatusInfo> statusInfos = new ArrayList<StatusInfo>();
		Cursor cursor;
		try {

			cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, 
					allColumns, null, null, null);
			
			if (cursor.moveToFirst()) {
				do {
					StatusInfo statusInfo = cursorToStatusInfo(cursor);
					statusInfos.add((StatusInfo) statusInfos); 
				} while (cursor.moveToNext());
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
//			cursor = this.database.query(Store.STATUS_TABLE, allColumns, null, null, null, null, null);
//			if (cursor.moveToFirst()) {
//				do {
//					StatusInfo statusInfo = cursorToStatusInfo(cursor);
//					statusInfos.add((StatusInfo) statusInfos); 
//				} while (cursor.moveToNext());
//			}
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return statusInfos;
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
		return null;
	}
	
	
	public List<StatusInfo> getAllStatusInfoByUserId(String userId,int status,String type) {
		List<StatusInfo> list = new ArrayList<StatusInfo>();
		Cursor cursor;
		try {
			
			if(type.equalsIgnoreCase("event")){
				cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_USER_ID +" = ? AND "+Store.STATUS_EVENT_ID +" > 0 AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null);
			}else if(type.equalsIgnoreCase("Service")){
				cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_USER_ID +" = ? AND "+Store.STATUS_SERVICE_ID +" > 0 AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null);
			}else{
				cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_USER_ID +" = ? AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null);
			}
			
			if (cursor.moveToFirst()) {
				do {
					StatusInfo Info = cursorToStatusInfo(cursor);
					list.add(Info); 
				} while (cursor.moveToNext());
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
//			if(type.equalsIgnoreCase("event")){
//				cursor = this.database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_USER_ID +" = ? AND "+Store.STATUS_EVENT_ID +" > 0 AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null,null);
//			}else if(type.equalsIgnoreCase("Service")){
//				cursor = this.database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_USER_ID +" = ? AND "+Store.STATUS_SERVICE_ID +" > 0 AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null,null);
//			}else{
//				cursor = this.database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_USER_ID +" = ? AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null,null);
//			}
//			if (cursor.moveToFirst()) {
//				do {
//					StatusInfo Info = cursorToStatusInfo(cursor);
//					list.add(Info); 
//				} while (cursor.moveToNext());
//			}
//
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return list;
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
		return null;
	}
	
	
	public List<StatusInfo> getAllStatusInfoByType(int status,String type) {
		List<StatusInfo> list = new ArrayList<StatusInfo>();
		Cursor cursor = null;
		try {

			if(type.equalsIgnoreCase("event")){
				cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_EVENT_ID +" > 0 AND "+Store.STATUS_STATUS+" = "+status, null, null,null);
			}else if(type.equalsIgnoreCase("service")){
				cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_SERVICE_ID +" > 0 AND "+Store.STATUS_STATUS+" = "+status, null, null,null);
			}
			
			if (cursor.moveToFirst()) {
				do {
					StatusInfo Info = cursorToStatusInfo(cursor);
					list.add(Info); 
				} while (cursor.moveToNext());
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
//			if(type.equalsIgnoreCase("event")){
//				cursor = this.database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_EVENT_ID +" > 0 AND "+Store.STATUS_STATUS+" = "+status, null, null, null,null);
//			}else if(type.equalsIgnoreCase("service")){
//				cursor = this.database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_SERVICE_ID +" > 0 AND "+Store.STATUS_STATUS+" = "+status, null, null, null,null);
//			}
//			if (cursor.moveToFirst()) {
//				do {
//					StatusInfo Info = cursorToStatusInfo(cursor);
//					list.add(Info); 
//				} while (cursor.moveToNext());
//			}
//
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return list;
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
		return null;
	}
	
	public StatusInfo getStatusInfoById(String userId, int eventId,int status,String type) {
		Cursor cursor;
		
		if(type.equalsIgnoreCase("event")){
			cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_EVENT_ID +" = "+eventId+" AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null);
		}else{
			cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_SERVICE_ID +" = "+eventId+" AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null);
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
//		if(type.equalsIgnoreCase("event")){
//			cursor = database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_EVENT_ID +" = "+eventId+" AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null, null);
//		}else{
//			cursor = database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_SERVICE_ID +" = "+eventId+" AND "+Store.STATUS_STATUS+" = "+status, new String[] {userId}, null, null, null);
//		}
		cursor.moveToFirst();

		StatusInfo lockInfo = null;

		if (!cursor.isAfterLast()) {
			lockInfo = cursorToStatusInfo(cursor);
		}

		// Make sure to close the cursor
//		cursor.close();

		return lockInfo;
	}
	
	
	public StatusInfo getStatusInfoByServiceId(String userid, int channelId, int status,String type) {
		Cursor cursor = null;
		
		if(type.equalsIgnoreCase("event")){
			cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_EVENT_ID +" = "+channelId +" AND "+Store.STATUS_STATUS +" = "+status, new String[]{userid}, null, null);
			cursor.moveToFirst();
		}else{
			cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns,Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_SERVICE_ID +" = "+channelId +" AND "+Store.STATUS_STATUS +" = "+status, new String[]{userid}, null, null);
			cursor.moveToFirst();
		}
		
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
////			//LogUtil.log(TAG, "database is not null and opened.");
//		}
//		
//		if(type.equalsIgnoreCase("event")){
//			cursor = database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_EVENT_ID +" = "+channelId +" AND "+Store.STATUS_STATUS +" = "+status, new String[]{userid}, null, null, null);
//			cursor.moveToFirst();
//		}else{
//			cursor = database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_SERVICE_ID +" = "+channelId +" AND "+Store.STATUS_STATUS +" = "+status, new String[]{userid}, null, null, null);
//			cursor.moveToFirst();
//		}
		
		StatusInfo subscribedServiceInfo = null;
		if (!cursor.isAfterLast()) {
			subscribedServiceInfo = cursorToStatusInfo(cursor);
		}

		// Make sure to close the cursor
		cursor.deactivate();
		cursor.close();
		return subscribedServiceInfo;
	}
	
	public StatusInfo getInfoByType(int Id, int status,String type) {
		Cursor cursor = null;
		if(type.equalsIgnoreCase("event")){
			cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_EVENT_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status, null, null, null);
			cursor.moveToFirst();
		}else{
			cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_SERVICE_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status, null, null, null);
			cursor.moveToFirst();
		}
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
////			//LogUtil.log(TAG, "database is not null and opened.");
//		}
//		
//		if(type.equalsIgnoreCase("event")){
//			cursor = database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_EVENT_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status, null, null, null, null);
//			cursor.moveToFirst();
//		}else{
//			cursor = database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_SERVICE_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status, null, null, null, null);
//			cursor.moveToFirst();
//		}
		
		StatusInfo serviceInfo = null;

		if (!cursor.isAfterLast()) {
			serviceInfo = cursorToStatusInfo(cursor);
		}

		// Make sure to close the cursor
		cursor.deactivate();
		cursor.close();

		return serviceInfo;
	}
	
	public StatusInfo getSubscribeInfoByUniqueId(int Id, int status,String type) {
		Cursor cursor = null;
		if(type.equalsIgnoreCase("event")){
			cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS, allColumns, Store.STATUS_UNIQUE_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status, null, null, null);
			cursor.moveToFirst();
		}else{
			cursor=mContext.getContentResolver().query(CONTENT_URI_STATUS,allColumns, Store.STATUS_SERVICE_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status, null, null, null);
			cursor.moveToFirst();
		}
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
////			//LogUtil.log(TAG, "database is not null and opened.");
//		}
//		
//		if(type.equalsIgnoreCase("event")){
//			cursor = database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_UNIQUE_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status, null, null, null, null);
//			cursor.moveToFirst();
//		}else{
//			cursor = database.query(Store.STATUS_TABLE, allColumns, Store.STATUS_SERVICE_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status, null, null, null, null);
//			cursor.moveToFirst();
//		}
		
		StatusInfo subscribedServiceInfo = null;

		if (!cursor.isAfterLast()) {
			subscribedServiceInfo = cursorToStatusInfo(cursor);
		}

		// Make sure to close the cursor
		cursor.deactivate();
		cursor.close();

		return subscribedServiceInfo;
	}

	public int toggleStatusInfo(String userid,int id,String type,int value) {
		ContentValues values = new ContentValues();
		values.put(Store.STATUS_STATUS, value);
//		if(type.equalsIgnoreCase("event")){
//			return database.update(Store.STATUS_TABLE, values, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_EVENT_ID+" = "+id, new String[] {userid});
//		}else{
//			return database.update(Store.STATUS_TABLE, values, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_SERVICE_ID+" = "+id, new String[] {userid});
//		}
		
		if(type.equalsIgnoreCase("event")){
			int row = mContext.getContentResolver().update(CONTENT_URI_STATUS, values, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_EVENT_ID+" = "+id, new String[] {userid});
			if(Constant.DEBUG)Log.i("toggleStatusInfo=== Events", "row : " + row);
			return row;
		}else{
			int row = mContext.getContentResolver().update(CONTENT_URI_STATUS, values, Store.STATUS_USER_ID + " = ? AND "+Store.STATUS_SERVICE_ID+" = "+id, new String[] {userid});
			if(Constant.DEBUG)Log.i("toggleStatusInfo=== noevents", "row : " + row);
			return row;
		}
	}
	
	
	public int activateLockInfo(int lockId,String type) {
		ContentValues values = new ContentValues();
		values.put(Store.STATUS_STATUS, 2);
//		if(type.equalsIgnoreCase("event")){
//			return database.update(Store.STATUS_TABLE, values, Store.STATUS_EVENT_ID+" = "+lockId, null);
//		}else{
//			return database.update(Store.STATUS_TABLE, values, Store.STATUS_SERVICE_ID+" = "+lockId, null);
//		}
		
		if(type.equalsIgnoreCase("event")){
			int row = mContext.getContentResolver().update(CONTENT_URI_STATUS, values, Store.STATUS_EVENT_ID+" = "+lockId, null);
			return row;
		}else{
			int row = mContext.getContentResolver().update(CONTENT_URI_STATUS, values, Store.STATUS_SERVICE_ID+" = "+lockId, null);
			return row;
		}
	}
	
	public int deleteStatusInfo(int userId,int eventId,int status) {
//		return database.delete(Store.STATUS_TABLE, Store.STATUS_USER_ID+" = "+userId+" AND "+Store.STATUS_EVENT_ID +" = "+eventId +" AND "+Store.STATUS_STATUS +" = "+status,null);
		int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_USER_ID+" = "+userId+" AND "+Store.STATUS_EVENT_ID +" = "+eventId +" AND "+Store.STATUS_STATUS +" = "+status,null);
		return row;
	}
	
	public int deleteStatusInfoById(int Id,String type) {
//		if(type.equalsIgnoreCase("event")){
//			return database.delete(Store.STATUS_TABLE, Store.STATUS_EVENT_ID+" = "+Id,null);
//		}else{
//			return database.delete(Store.STATUS_TABLE, Store.STATUS_SERVICE_ID+" = "+Id,null);
//		}
		
		if(type.equalsIgnoreCase("event")){
			int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_EVENT_ID+" = "+Id,null);
			return row;
		}else{
			int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_SERVICE_ID+" = "+Id,null);
			return row;
		}
		
	}
	
	public int deleteStatusInfoByUniqueId(int Id,String type,int status) {
//		if(type.equalsIgnoreCase("event")){
//			return database.delete(Store.STATUS_TABLE, Store.STATUS_UNIQUE_ID+" = "+Id+" AND "+Store.STATUS_STATUS +" = "+status,null);
//		}else{
//			return database.delete(Store.STATUS_TABLE, Store.STATUS_SERVICE_ID+" = "+Id+" AND "+Store.STATUS_STATUS +" = "+status,null);
//		}
		
		if(type.equalsIgnoreCase("event")){
			int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_UNIQUE_ID+" = "+Id+" AND "+Store.STATUS_STATUS +" = "+status,null);
			return row;
		}else{
			int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_SERVICE_ID+" = "+Id+" AND "+Store.STATUS_STATUS +" = "+status,null);
			return row;
		}
		
	}
	
	public int deleteStatusInfoById(int Id,String type,int status) {
//		if(type.equalsIgnoreCase("event")){
//			return database.delete(Store.STATUS_TABLE, Store.STATUS_EVENT_ID+" = "+Id+" AND "+Store.STATUS_STATUS +" = "+status,null);
//		}else{
//			return database.delete(Store.STATUS_TABLE, Store.STATUS_SERVICE_ID+" = "+Id+" AND "+Store.STATUS_STATUS +" = "+status,null);
//		}
		
		if(type.equalsIgnoreCase("event")){
			int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_EVENT_ID+" = "+Id+" AND "+Store.STATUS_STATUS +" = "+status,null);
			return row;
		}else{
			int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_SERVICE_ID+" = "+Id+" AND "+Store.STATUS_STATUS +" = "+status,null);
			return row;
		}
	}
	
	public int deleteServiceStatusInfo(int userId,int Id,int status) {
//        return database.delete(Store.STATUS_TABLE, Store.STATUS_USER_ID+" = "+userId+" AND "+Store.STATUS_SERVICE_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status,null);
		int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_USER_ID+" = "+userId+" AND "+Store.STATUS_SERVICE_ID +" = "+Id +" AND "+Store.STATUS_STATUS +" = "+status,null);
		return row;
	}
	
	public int deleteStatusInfoByStatus(int status, String type) {
//		if(type.equalsIgnoreCase("event")){
//			return database.delete(Store.STATUS_TABLE, Store.STATUS_STATUS +" = "+status,null);
//		}else{
//			return database.delete(Store.STATUS_TABLE, Store.STATUS_STATUS +" = "+status,null);
//		}
		
		if(type.equalsIgnoreCase("event")){
			int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_STATUS +" = "+status,null);
			return row;
		}else{
			int row = mContext.getContentResolver().delete(CONTENT_URI_STATUS, Store.STATUS_STATUS +" = "+status,null);
			return row;
		}
	}
}
