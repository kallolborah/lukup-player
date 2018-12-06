package com.port.api.db.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

public class ProgramGateway {
	private static final String TAG = "ProgramGateway";
	
	private static final String AUTHORITY = "com.port.apps.epg.contentprovider" ;
	public static final Uri CONTENT_URI_PROGRAM = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.PROGRAM_TABLE);

	private Context mContext ;

	/**
	 * Field declaration.
	 * 
	 */	

//	private SQLiteDatabase database;
	
	// 24 Fields
	private String[] allColumns = { Store.PROGRAM_EVENT_ID,
			Store.PROGRAM_EVENT_SRC, Store.PROGRAM_TYPE,
			Store.PROGRAM_CHANNEL_SERVICE_ID,
			Store.PROGRAM_GENRE ,
			Store.PROGRAM_PRICE,Store.PROGRAM_PRICING_MODEL,
			Store.PROGRAM_EXPIRY_DATE,
			Store.PROGRAM_DATE_ADDED,Store.PROGRAM_DESCRIPTION,
			Store.PROGRAM_MATURITY, Store.PROGRAM_IMAGE,
			Store.PROGRAM_RANKING ,Store.PROGRAM_ACTORS,
			Store.PROGRAM_DIRECTOR,Store.PROGRAM_MUSIC_DIRECTOR,
			Store.PROGRAM_PRODUCTIONHOUSE, Store.PROGRAM_START_TIME,
			Store.PROGRAM_DURATION ,Store.PROGRAM_RATING,
			Store.PROGRAM_LANGUAGE,Store.PROGRAM_EVENT_NAME,Store.PROGRAM_EVENT_CATEGORY,
			Store.PROGRAM_UNIQUE_ID,Store.PROGRAM_SUMMARY,
			Store.PROGRAM_BOUQUET_ID,Store.PROGRAM_CHANNELNAME,
			Store.PROGRAM_COLLECTIONID,Store.PROGRAM_COLLECTIONNAME,
			Store.PROGRAM_DATE,Store.PROGRAM_TIMESTAMP};
	
	//DVB middleware
	private String[] allDvbColumns = { Store.PROGRAM_EVENT_ID,
			Store.PROGRAM_EVENT_SRC, Store.PROGRAM_TYPE,
			Store.PROGRAM_CHANNEL_SERVICE_ID,
			Store.PROGRAM_GENRE ,
			Store.PROGRAM_PRICE,Store.PROGRAM_PRICING_MODEL,
			Store.PROGRAM_EXPIRY_DATE,
			Store.PROGRAM_DATE_ADDED,Store.PROGRAM_DESCRIPTION,
			Store.PROGRAM_MATURITY, Store.PROGRAM_IMAGE,
			Store.PROGRAM_RANKING ,Store.PROGRAM_ACTORS,
			Store.PROGRAM_DIRECTOR,Store.PROGRAM_MUSIC_DIRECTOR,
			Store.PROGRAM_PRODUCTIONHOUSE,Store.PROGRAM_START_DATE,
			Store.PROGRAM_START_TIME,Store.PROGRAM_DURATION ,
			Store.PROGRAM_RUNNING_STATUS,Store.PROGRAM_FREE_CA_MODE,
			Store.PROGRAM_RATING,
			Store.PROGRAM_LANGUAGE,Store.PROGRAM_EVENT_NAME,Store.PROGRAM_EVENT_CATEGORY,
			Store.PROGRAM_UNIQUE_ID,Store.PROGRAM_SUMMARY,
			Store.PROGRAM_BOUQUET_ID,Store.PROGRAM_CHANNELNAME,
			Store.PROGRAM_COLLECTIONID,Store.PROGRAM_COLLECTIONNAME,
			Store.PROGRAM_DATE,Store.PROGRAM_TIMESTAMP};
	
//	public ProgramGateway(SQLiteDatabase database,Context argContext){
//		this.database=database;
//		this.mContext=argContext ;
//	}
	
	public ProgramGateway(Context argContext){
		this.mContext=argContext ;
	}
	
	public int insertProgramInfo(/*int eventId,*/String eventSrc,String type,int chlUniqueId,String genre, float price,String priceModel,String expiryDate,
		String dateAdded,String Description, String maturity, String image, int ranking, String actors,String director,String musicDirector,
			String productionHouse, String startTime, String duration, String rating, String language, String name,String category,int unique_id,
			String summary,int bouquet_id,String channelName,int collectionid,String collectionName,String date,long timestamp) {
		ContentValues values = new ContentValues();
		values.put(Store.PROGRAM_EVENT_SRC, eventSrc);
		values.put(Store.PROGRAM_TYPE, type);
		values.put(Store.PROGRAM_CHANNEL_SERVICE_ID, chlUniqueId);
		values.put(Store.PROGRAM_GENRE, genre);
		values.put(Store.PROGRAM_EVENT_CATEGORY, category);
		values.put(Store.PROGRAM_PRICE, price);
		values.put(Store.PROGRAM_PRICING_MODEL, priceModel);
		values.put(Store.PROGRAM_EXPIRY_DATE, expiryDate);
		values.put(Store.PROGRAM_DATE_ADDED, dateAdded);
		values.put(Store.PROGRAM_DESCRIPTION, Description);
		values.put(Store.PROGRAM_MATURITY, maturity);
		values.put(Store.PROGRAM_IMAGE, image);
		values.put(Store.PROGRAM_RANKING, ranking);
		values.put(Store.PROGRAM_ACTORS, actors);
		values.put(Store.PROGRAM_DIRECTOR, director);
		values.put(Store.PROGRAM_MUSIC_DIRECTOR, musicDirector);
		values.put(Store.PROGRAM_PRODUCTIONHOUSE, productionHouse);
		values.put(Store.PROGRAM_START_TIME, startTime);
		values.put(Store.PROGRAM_DURATION, duration);
		values.put(Store.PROGRAM_RATING, rating);
		values.put(Store.PROGRAM_LANGUAGE, language);
		values.put(Store.PROGRAM_EVENT_NAME, name);
		values.put(Store.PROGRAM_UNIQUE_ID, unique_id);
		values.put(Store.PROGRAM_SUMMARY, summary);
		values.put(Store.PROGRAM_BOUQUET_ID, bouquet_id);
		values.put(Store.PROGRAM_CHANNELNAME, channelName);
		values.put(Store.PROGRAM_COLLECTIONID, collectionid);
		values.put(Store.PROGRAM_COLLECTIONNAME, collectionName);
		values.put(Store.PROGRAM_DATE, date);
		values.put(Store.PROGRAM_TIMESTAMP, timestamp);
		if (Constant.DEBUG) Log.d(TAG, "name: "+name);
		mContext.getContentResolver().insert(CONTENT_URI_PROGRAM, values) ;
		return maxRow();
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
////			if(Constant.DEBUG)  Log.d(TAG, "database is not null and opened.");
//		}
//		if(Constant.DEBUG)  Log.d(TAG, "Event values "+values);
//		try{
//			database.insert(Store.PROGRAM_TABLE, null, values);
//			return maxRow();
//		}catch(Exception e){
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//			return 0;
//		}
	}
	
	//DVB middleware
	public int insertDvbProgramInfo(/*int eventId,*/String eventSrc,String type,int chlUniqueId,String genre, float price,String priceModel,String expiryDate,
		String dateAdded,String Description, String maturity, String image, int ranking, String actors,String director,String musicDirector,
			String productionHouse, String startDate, String startTime, String duration, String rating, String language, String name,String category,int unique_id,
			String summary,int bouquet_id,String channelName,int collectionid,String collectionName,int runningStatus,int freeCAmode,String date,long timestamp) {
		ContentValues values = new ContentValues();
		values.put(Store.PROGRAM_EVENT_SRC, eventSrc);
		values.put(Store.PROGRAM_TYPE, type);
		values.put(Store.PROGRAM_CHANNEL_SERVICE_ID, chlUniqueId);
		values.put(Store.PROGRAM_GENRE, genre);
		values.put(Store.PROGRAM_EVENT_CATEGORY, category);
		values.put(Store.PROGRAM_PRICE, price);
		values.put(Store.PROGRAM_PRICING_MODEL, priceModel);
		values.put(Store.PROGRAM_EXPIRY_DATE, expiryDate);
		values.put(Store.PROGRAM_DATE_ADDED, dateAdded);
		values.put(Store.PROGRAM_DESCRIPTION, Description);
		values.put(Store.PROGRAM_MATURITY, maturity);
		values.put(Store.PROGRAM_IMAGE, image);
		values.put(Store.PROGRAM_RANKING, ranking);
		values.put(Store.PROGRAM_ACTORS, actors);
		values.put(Store.PROGRAM_DIRECTOR, director);
		values.put(Store.PROGRAM_MUSIC_DIRECTOR, musicDirector);
		values.put(Store.PROGRAM_PRODUCTIONHOUSE, productionHouse);
		values.put(Store.PROGRAM_START_DATE, startDate);
		values.put(Store.PROGRAM_START_TIME, startTime);
		values.put(Store.PROGRAM_DURATION, duration);
		values.put(Store.PROGRAM_RATING, rating);
		values.put(Store.PROGRAM_LANGUAGE, language);
		values.put(Store.PROGRAM_EVENT_NAME, name);
		values.put(Store.PROGRAM_UNIQUE_ID, unique_id);
		values.put(Store.PROGRAM_SUMMARY, summary);
		values.put(Store.PROGRAM_BOUQUET_ID, bouquet_id);
		values.put(Store.PROGRAM_CHANNELNAME, channelName);
		values.put(Store.PROGRAM_COLLECTIONID, collectionid);
		values.put(Store.PROGRAM_COLLECTIONNAME, collectionName);
		values.put(Store.PROGRAM_RUNNING_STATUS, runningStatus);
		values.put(Store.PROGRAM_FREE_CA_MODE, freeCAmode);
		values.put(Store.PROGRAM_DATE, date);
		values.put(Store.PROGRAM_TIMESTAMP, timestamp);
		
		mContext.getContentResolver().insert(CONTENT_URI_PROGRAM, values) ;
		return maxRow();

//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
////				if(Constant.DEBUG)  Log.d(TAG, "database is not null and opened.");
//		}
//		if(Constant.DEBUG)  Log.d(TAG, "Event values "+values);
//		try{
//			database.insert(Store.PROGRAM_TABLE, null, values);
//			return maxRow();
//		}catch(Exception e){
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//			return 0;
//		}
	}
	
	private int maxRow() {
		
		Cursor c=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
				allColumns, null, null, null) ;
		if (Constant.DEBUG) Log.d(TAG, "maxRow(): "+c.getPosition());
		return c.getPosition();
//		String query = "SELECT MAX("+Store.PROGRAM_EVENT_ID+") AS max_id FROM "+Store.PROGRAM_TABLE;
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
////			//LogUtil.log(TAG, "database is not null and opened.");
//		}
//		Cursor cursor = database.rawQuery(query, null);
//		int id = 0;     
//		if (cursor.moveToFirst()){
//			do{           
//				id = cursor.getInt(0);                  
//			} while(cursor.moveToNext());           
//		}
//		return id;
	}
	
		
	public int updateProgramInfo (int eventId, String eventSrc,String type,int chlUniqueId,String genre,float price,String priceModel,String expiryDate,
		String dateAdded,String Description, String maturity, String image, int ranking, String actors,String director,String musicDirector,
			String productionHouse, String startTime, String duration, String rating, String language, String name,String category,int unique_id,
			String summary,int bouquet_id,String channelName,int collectionid,String collectionName,String date,long timestamp) {
		ContentValues values = new ContentValues();
		values.put(Store.PROGRAM_EVENT_ID, eventId);
		values.put(Store.PROGRAM_EVENT_SRC, eventSrc);
		values.put(Store.PROGRAM_TYPE, type);
		values.put(Store.PROGRAM_CHANNEL_SERVICE_ID, chlUniqueId);
		values.put(Store.PROGRAM_GENRE, genre);
		values.put(Store.PROGRAM_EVENT_CATEGORY, category);
		values.put(Store.PROGRAM_PRICE, price);
		values.put(Store.PROGRAM_PRICING_MODEL, priceModel);
		values.put(Store.PROGRAM_EXPIRY_DATE, expiryDate);
		values.put(Store.PROGRAM_DATE_ADDED, dateAdded);
		values.put(Store.PROGRAM_DESCRIPTION, Description);
		values.put(Store.PROGRAM_MATURITY, maturity);
		values.put(Store.PROGRAM_IMAGE, image);
		values.put(Store.PROGRAM_RANKING, ranking);
		values.put(Store.PROGRAM_ACTORS, actors);
		values.put(Store.PROGRAM_DIRECTOR, director);
		values.put(Store.PROGRAM_MUSIC_DIRECTOR, musicDirector);
		values.put(Store.PROGRAM_PRODUCTIONHOUSE, productionHouse);
		values.put(Store.PROGRAM_START_TIME, startTime);
		values.put(Store.PROGRAM_DURATION, duration);
		values.put(Store.PROGRAM_RATING, rating);
		values.put(Store.PROGRAM_LANGUAGE, language);
		values.put(Store.PROGRAM_EVENT_NAME, name);
		values.put(Store.PROGRAM_UNIQUE_ID, unique_id);
		values.put(Store.PROGRAM_SUMMARY, summary);
		values.put(Store.PROGRAM_BOUQUET_ID, bouquet_id);
		values.put(Store.PROGRAM_CHANNELNAME, channelName);
		values.put(Store.PROGRAM_COLLECTIONID, collectionid);
		values.put(Store.PROGRAM_COLLECTIONNAME, collectionName);
		values.put(Store.PROGRAM_DATE, date);
		values.put(Store.PROGRAM_TIMESTAMP, timestamp);

//		database.update(Store.PROGRAM_TABLE, values, Store.PROGRAM_EVENT_ID + " = " +eventId, null);
		
		mContext.getContentResolver().update(CONTENT_URI_PROGRAM, values, Store.PROGRAM_EVENT_ID + " = " +eventId, null);
		return eventId;
	}
	
	//DVB middleware
	public int updateDvbProgramInfo (int eventId, String eventSrc,String type,int chlUniqueId,String genre,float price,String priceModel,String expiryDate,
			String dateAdded,String Description, String maturity, String image, int ranking, String actors,String director,String musicDirector,
				String productionHouse, String startDate, String startTime, String duration, String rating, String language, String name,String category,int unique_id,
				String summary,int bouquet_id,String channelName,int collectionid,String collectionName,int runningStatus,int freeCAmode,String date,long timestamp) {
			ContentValues values = new ContentValues();
			values.put(Store.PROGRAM_EVENT_ID, eventId);
			values.put(Store.PROGRAM_EVENT_SRC, eventSrc);
			values.put(Store.PROGRAM_TYPE, type);
			values.put(Store.PROGRAM_CHANNEL_SERVICE_ID, chlUniqueId);
			values.put(Store.PROGRAM_GENRE, genre);
			values.put(Store.PROGRAM_EVENT_CATEGORY, category);
			values.put(Store.PROGRAM_PRICE, price);
			values.put(Store.PROGRAM_PRICING_MODEL, priceModel);
			values.put(Store.PROGRAM_EXPIRY_DATE, expiryDate);
			values.put(Store.PROGRAM_DATE_ADDED, dateAdded);
			values.put(Store.PROGRAM_DESCRIPTION, Description);
			values.put(Store.PROGRAM_MATURITY, maturity);
			values.put(Store.PROGRAM_IMAGE, image);
			values.put(Store.PROGRAM_RANKING, ranking);
			values.put(Store.PROGRAM_ACTORS, actors);
			values.put(Store.PROGRAM_DIRECTOR, director);
			values.put(Store.PROGRAM_MUSIC_DIRECTOR, musicDirector);
			values.put(Store.PROGRAM_PRODUCTIONHOUSE, productionHouse);
			values.put(Store.PROGRAM_START_DATE, startDate);
			values.put(Store.PROGRAM_START_TIME, startTime);
			values.put(Store.PROGRAM_DURATION, duration);
			values.put(Store.PROGRAM_RATING, rating);
			values.put(Store.PROGRAM_LANGUAGE, language);
			values.put(Store.PROGRAM_EVENT_NAME, name);
			values.put(Store.PROGRAM_UNIQUE_ID, unique_id);
			values.put(Store.PROGRAM_SUMMARY, summary);
			values.put(Store.PROGRAM_BOUQUET_ID, bouquet_id);
			values.put(Store.PROGRAM_CHANNELNAME, channelName);
			values.put(Store.PROGRAM_COLLECTIONID, collectionid);
			values.put(Store.PROGRAM_COLLECTIONNAME, collectionName);
			values.put(Store.PROGRAM_RUNNING_STATUS, runningStatus);
			values.put(Store.PROGRAM_FREE_CA_MODE, freeCAmode);
			values.put(Store.PROGRAM_DATE, date);
			values.put(Store.PROGRAM_TIMESTAMP, timestamp);

//			database.update(Store.PROGRAM_TABLE, values, Store.PROGRAM_EVENT_ID + " = " +eventId, null);
			
			mContext.getContentResolver().update(CONTENT_URI_PROGRAM, values, Store.PROGRAM_EVENT_ID + " = " +eventId, null);
			return eventId;
		}

	private ProgramInfo cursorToProgramInfo(Cursor cursor) {
		ProgramInfo programInfo = new ProgramInfo();
		programInfo.setEventId(cursor.getInt(0));
		programInfo.setEventSrc(cursor.getString(1));
		programInfo.setChannelType(cursor.getString(2));
		programInfo.setChannelServiceId(cursor.getInt(3));
		programInfo.setGenre(cursor.getString(4));
		programInfo.setPrice(cursor.getFloat(5));
		programInfo.setPriceModel(cursor.getString(6));
		programInfo.setExpiryDate(cursor.getString(7));
		programInfo.setDateAdded(cursor.getString(8));
		programInfo.setDescription(cursor.getString(9));
		programInfo.setMaturity(cursor.getString(10));
		programInfo.setImage(cursor.getString(11));
		programInfo.setRanking(cursor.getInt(12));
		programInfo.setActors(cursor.getString(13));
		programInfo.setDirector(cursor.getString(14));
		programInfo.setMusicDirector(cursor.getString(15));
		programInfo.setProductionHouse(cursor.getString(16));
		programInfo.setStartTime(cursor.getString(17));
		programInfo.setDuration(cursor.getString(18));
		programInfo.setRating(cursor.getString(19));
		programInfo.setLanguage(cursor.getString(20));
		programInfo.setEventName(cursor.getString(21));
		programInfo.setEventCategory(cursor.getString(22));
		programInfo.setProgramId(cursor.getInt(23));
		programInfo.setSummary(cursor.getString(24));
		programInfo.setBouquetId(cursor.getInt(25));
		programInfo.setChannelName(cursor.getString(26));
		programInfo.setCollectionId(cursor.getInt(27));
		programInfo.setCollectionName(cursor.getString(28));
		programInfo.setDate(cursor.getString(29));
		programInfo.setTimeStamp(cursor.getLong(30));
		
		return programInfo;
	}
	
	//DVB middleware
	private ProgramInfo cursorToDvbProgramInfo(Cursor cursor) {
		ProgramInfo programInfo = new ProgramInfo();
		programInfo.setEventId(cursor.getInt(0));
		programInfo.setEventSrc(cursor.getString(1));
		programInfo.setChannelType(cursor.getString(2));
		programInfo.setChannelServiceId(cursor.getInt(3));
		programInfo.setGenre(cursor.getString(4));
		programInfo.setPrice(cursor.getFloat(5));
		programInfo.setPriceModel(cursor.getString(6));
		programInfo.setExpiryDate(cursor.getString(7));
		programInfo.setDateAdded(cursor.getString(8));
		programInfo.setDescription(cursor.getString(9));
		programInfo.setMaturity(cursor.getString(10));
		programInfo.setImage(cursor.getString(11));
		programInfo.setRanking(cursor.getInt(12));
		programInfo.setActors(cursor.getString(13));
		programInfo.setDirector(cursor.getString(14));
		programInfo.setMusicDirector(cursor.getString(15));
		programInfo.setProductionHouse(cursor.getString(16));
		programInfo.setStartDate(cursor.getString(17));
		programInfo.setStartTime(cursor.getString(18));
		programInfo.setDuration(cursor.getString(19));
		
		programInfo.setRunningStatus(cursor.getInt(20));
		programInfo.setFreeCAmode(cursor.getInt(21));
		
		programInfo.setRating(cursor.getString(22));
		programInfo.setLanguage(cursor.getString(23));
		programInfo.setEventName(cursor.getString(24));
		programInfo.setEventCategory(cursor.getString(25));
		programInfo.setProgramId(cursor.getInt(26));
		programInfo.setSummary(cursor.getString(27));
		programInfo.setBouquetId(cursor.getInt(28));
		programInfo.setChannelName(cursor.getString(29));
		programInfo.setCollectionId(cursor.getInt(30));
		programInfo.setCollectionName(cursor.getString(31));
		
		programInfo.setDate(cursor.getString(32));
		programInfo.setTimeStamp(cursor.getLong(33));
		
		return programInfo;
	}
	
	public List<ProgramInfo> getAllProgramInfo() {
		List<ProgramInfo> programInfos = new ArrayList<ProgramInfo>();
		Cursor cursor;
		try {
			
			cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
					allColumns, null, null, Store.PROGRAM_BOUQUET_ID+" AND "+Store.PROGRAM_CHANNEL_SERVICE_ID);
			
			if (cursor.moveToFirst()) {
				do {
					ProgramInfo programInfo = cursorToProgramInfo(cursor);
					programInfos.add(programInfo); 
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
//			if (Constant.DVB) {		//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = this.database.query(Store.PROGRAM_TABLE, allDvbColumns, null, null, null, null, Store.PROGRAM_BOUQUET_ID+" AND "+Store.PROGRAM_CHANNEL_SERVICE_ID);
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToDvbProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			} else {
//				cursor = this.database.query(Store.PROGRAM_TABLE, allColumns, null, null, null, null, Store.PROGRAM_BOUQUET_ID+" AND "+Store.PROGRAM_CHANNEL_SERVICE_ID);
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			}
//			
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return programInfos;
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
		return null;
	}
	
	public ProgramInfo getExternalEventInfoByBoth(int serviceId, int externalEventId) {
		Cursor cursor = null;
		ProgramInfo externalEventInfo = null;
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}

		try {
			cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
					allColumns,Store.PROGRAM_CHANNEL_SERVICE_ID + " = "+serviceId+" AND "+Store.PROGRAM_UNIQUE_ID + " = "+ externalEventId, null, null, null) ;
			if(cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					externalEventInfo = cursorToProgramInfo(cursor);
				}
			}
			
//			if (Constant.DVB) {		//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_CHANNEL_SERVICE_ID + " = "+serviceId+" AND "+Store.PROGRAM_UNIQUE_ID + " = "+ externalEventId, null, null, null, null);
//				if(cursor != null) {
//					cursor.moveToFirst();
//					if (!cursor.isAfterLast()) {
//						externalEventInfo = cursorToDvbProgramInfo(cursor);
//					}
//				}
//			} else {
//				cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_CHANNEL_SERVICE_ID + " = "+serviceId+" AND "+Store.PROGRAM_UNIQUE_ID + " = "+ externalEventId, null, null, null, null);
//				if(cursor != null) {
//					cursor.moveToFirst();
//					if (!cursor.isAfterLast()) {
//						externalEventInfo = cursorToProgramInfo(cursor);
//					}
//				}
//			}
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
//		if(cursor != null && !(cursor.isClosed())){
//			cursor.deactivate();
//			cursor.close();
//		}
		return externalEventInfo;
	}
	
	public ProgramInfo getProgramInfoByUniqueId(String programInfoId) {
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}

		Cursor cursor = null;
		ProgramInfo externalEventInfo = null;
		try{
			
			cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
					allColumns,Store.PROGRAM_UNIQUE_ID+" = ? ", new String[] {programInfoId}, null, null) ;
			if(cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					externalEventInfo = cursorToProgramInfo(cursor);
				}
			}
			
//			if (Constant.DVB) {		//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_UNIQUE_ID+" = ? ", new String[] {programInfoId}, null, null, null);
//				if(cursor != null) {
//					cursor.moveToFirst();
//		
//					if (!cursor.isAfterLast()) {
//						externalEventInfo = cursorToDvbProgramInfo(cursor);
//					}
//					// Make sure to close the cursor
//					cursor.close();
//					cursor.deactivate();
//				}
//			} else {
//				cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_UNIQUE_ID+" = ? ", new String[] {programInfoId}, null, null, null);
//				if(cursor != null) {
//					cursor.moveToFirst();
//		
//					if (!cursor.isAfterLast()) {
//						externalEventInfo = cursorToProgramInfo(cursor);
//					}
//					// Make sure to close the cursor
//					cursor.close();
//					cursor.deactivate();
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			return externalEventInfo;
		}
		return externalEventInfo;
	}
	
	public ProgramInfo getProgramInfoByEventId(int eventId) {
		Cursor cursor;
		ProgramInfo externalEventInfo = null;
		
		if(Constant.DVB){
			
			cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
					allDvbColumns,Store.PROGRAM_EVENT_ID+" = "+eventId, null, null, null) ;
			if(cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					externalEventInfo = cursorToDvbProgramInfo(cursor);
				}
			}
			
		}else{
		
		cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
				allColumns,Store.PROGRAM_EVENT_ID+" = "+eventId, null, null, null) ;
		if(cursor != null) {
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				externalEventInfo = cursorToProgramInfo(cursor);
			}
		}
	}
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
//		if (Constant.DVB) {		//DVB middleware
//			if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//			cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_EVENT_ID+" = "+eventId, null, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				externalEventInfo = cursorToDvbProgramInfo(cursor);
//			}
//		} else {
//			cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_EVENT_ID+" = "+eventId, null, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				externalEventInfo = cursorToProgramInfo(cursor);
//			}
//		}
//
//		// Make sure to close the cursor
//		cursor.close();
//		cursor.deactivate();
		return externalEventInfo;
	}
	
	
	public List<ProgramInfo> getAllEventByServiceId(int ServiceId,String currentTime) {
		List<ProgramInfo> programInfos = new ArrayList<ProgramInfo>();
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}

		Cursor cursor = null;
		try {
			
			cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
					allColumns,Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId + " AND " + Store.PROGRAM_START_TIME + " >= '"+currentTime +"'", null,Store.PROGRAM_START_TIME  + " ASC");
			if (cursor.moveToFirst()) {
				do {
					ProgramInfo programInfo = cursorToProgramInfo(cursor);
					programInfos.add(programInfo); 
				} while (cursor.moveToNext());
			}
			
//			if (Constant.DVB) {		//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId + " AND " + Store.PROGRAM_START_TIME + " >= '"+currentTime +"'", null, null, null, Store.PROGRAM_START_TIME  + " ASC");
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToDvbProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			} else {
//				cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId + " AND " + Store.PROGRAM_START_TIME + " >= '"+currentTime +"'", null, null, null, Store.PROGRAM_START_TIME  + " ASC");
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			}
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return programInfos;
			
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 

//		if(cursor != null){
//			cursor.close();
//			cursor.deactivate();
//		}

		return programInfos;
	}
	
	public List<ProgramInfo> getAllExternalEventByServiceId(int ServiceId) {
		List<ProgramInfo> programInfos = new ArrayList<ProgramInfo>();
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}

		Cursor cursor = null;
		try {
			if(Constant.DVB){//Added by tomesh for DVB
				cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
						allColumns,Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null,null);
				if (cursor.moveToFirst()) {
					do {
						ProgramInfo programInfo = cursorToProgramInfo(cursor);
						programInfos.add(programInfo); 
					} while (cursor.moveToNext());
				}//Added till here
			}else{
				cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
						allColumns,Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null,null);
				if (cursor.moveToFirst()) {
					do {
						ProgramInfo programInfo = cursorToProgramInfo(cursor);
						programInfos.add(programInfo); 
					} while (cursor.moveToNext());
				}
			}
			
			
//			if (Constant.DVB) {		//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null, null, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToDvbProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			} else {
//				cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null, null, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			}
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return programInfos;
			
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
//
//		if(cursor != null){
//			cursor.close();
//			cursor.deactivate();
//		}

		return programInfos;
	}
	
	public List<ProgramInfo> getEventByCollectionId(int collectionId,int ServiceId) {
		List<ProgramInfo> programInfos = new ArrayList<ProgramInfo>();
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
		Cursor cursor = null;
		try {
			
			cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
					allColumns,Store.PROGRAM_COLLECTIONID+" = "+collectionId+" AND "+Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null,null);
			if (cursor.moveToFirst()) {
				do {
					ProgramInfo programInfo = cursorToProgramInfo(cursor);
					programInfos.add(programInfo); 
				} while (cursor.moveToNext());
			}
			
//			if (Constant.DVB) {		//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_COLLECTIONID+" = "+collectionId+" AND "+Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null, null, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToDvbProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			} else {
//				cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_COLLECTIONID+" = "+collectionId+" AND "+Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null, null, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			}
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return programInfos;
			
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
		if(cursor != null){
			cursor.close();
			cursor.deactivate();
		}
		return programInfos;
	}
	
	public List<ProgramInfo> getCollectionIdByServiceId(int ServiceId) {
		List<ProgramInfo> programInfos = new ArrayList<ProgramInfo>();
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}

		Cursor cursor = null;
		try {
			cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
					allColumns,Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null,null);
			if (cursor.moveToFirst()) {
				do {
					ProgramInfo programInfo = cursorToProgramInfo(cursor);
					programInfos.add(programInfo); 
				} while (cursor.moveToNext());
			}
			
//			if (Constant.DVB) {		//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null, Store.PROGRAM_COLLECTIONID, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToDvbProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			} else {
//				cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+ServiceId, null, Store.PROGRAM_COLLECTIONID, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			}
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return programInfos;
			
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 

		if(cursor != null){
			cursor.close();
			cursor.deactivate();
		}

		return programInfos;
	}
	
	public List<ProgramInfo> getAllEventByBouquetId(int Bouquetid, String currentTime) {
		if(Constant.DEBUG) Log.i(TAG , "currentTime "+currentTime);
		List<ProgramInfo> programInfos = new ArrayList<ProgramInfo>();
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}

		Cursor cursor = null;
		try {
			
			cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
					allColumns,Store.PROGRAM_BOUQUET_ID+" = "+Bouquetid, null,Store.PROGRAM_CHANNEL_SERVICE_ID  + " DESC");
			if (cursor.moveToFirst()) {
				do {
					ProgramInfo programInfo = cursorToProgramInfo(cursor);
					programInfos.add(programInfo); 
				} while (cursor.moveToNext());
			}
			
//			cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_BOUQUET_ID+" = "+Bouquetid + " AND " + Store.PROGRAM_START_TIME + " >= '"+currentTime +"'", null, null, null, Store.PROGRAM_CHANNEL_SERVICE_ID + " AND " + Store.PROGRAM_START_TIME  + " ASC");
//			if (Constant.DVB) {		//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_BOUQUET_ID+" = "+Bouquetid, null, null, null, Store.PROGRAM_CHANNEL_SERVICE_ID  + " DESC");
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToDvbProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			} else {
//				cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_BOUQUET_ID+" = "+Bouquetid, null, null, null, Store.PROGRAM_CHANNEL_SERVICE_ID  + " DESC");
//				if (cursor.moveToFirst()) {
//					do {
//						ProgramInfo programInfo = cursorToProgramInfo(cursor);
//						programInfos.add(programInfo); 
//					} while (cursor.moveToNext());
//				}
//			}
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
			return programInfos;
			
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 

		if(cursor != null){
			cursor.close();
			cursor.deactivate();
		}

		return programInfos;
	}
	
	public List<ProgramInfo> getAllExternalEventByServiceId(String uniquID) {
		if(Constant.DEBUG)  Log.d(TAG, "getAllExternalEventByServiceId Called");
		Cursor cursor;
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
		List<ProgramInfo> list = new ArrayList<ProgramInfo>();
		if(!uniquID.equalsIgnoreCase("") && uniquID != null){
			return list;
		}
		
		cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
				allColumns,Store.PROGRAM_CHANNEL_SERVICE_ID+" = ? ", new String[] {uniquID}, null);
		if (cursor.moveToFirst()) {
			do {
				ProgramInfo programInfo = cursorToProgramInfo(cursor);
				list.add(programInfo); 
			} while (cursor.moveToNext());
		}
		
//		if (Constant.DVB) {		//DVB middleware
//			if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//			cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = ? ", new String[] {uniquID}, null, null, null);
//			cursor.moveToFirst();
//			while (!cursor.isAfterLast()) {
//				ProgramInfo ProgramInfo = cursorToDvbProgramInfo(cursor);
//				if(!ProgramInfo.getEventCategory().equalsIgnoreCase("") && ProgramInfo.getEventCategory() != null && ProgramInfo.getEventCategory().equalsIgnoreCase("live") && ProgramInfo != null && ProgramInfo.getStartTime() != null && ProgramInfo.getDateAdded() != null){
//
//					String eventTime_mm = null;
//					String sysTime_mm = null;
//
//					String start_Date = null;
//					start_Date = ProgramInfo.getDateAdded();
//
//					
//					if(!start_Date.equalsIgnoreCase("") && start_Date != null && CommonUtil.compareDates(CommonUtil.getCurrentDate(), start_Date)){
//						if(ProgramInfo.getStartTime().indexOf(":") != -1){
//							eventTime_mm = ProgramInfo.getStartTime().substring(0, ProgramInfo.getStartTime().indexOf(":"));
//						}
//						if(CommonUtil.getCurrentHourValue() != null && CommonUtil.getCurrentHourValue().indexOf(":") != -1){
//							sysTime_mm = CommonUtil.getCurrentHourValue().substring(0, ProgramInfo.getStartTime().indexOf(":")-1);
//						}
//						try {
//							if(Integer.parseInt(eventTime_mm.trim()) >= Integer.parseInt(sysTime_mm.trim())){
//								list.add(ProgramInfo); 
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}else{
//					//	if(Constant.DEBUG)  Log.d(TAG, ProgramInfo.getEventName()+" Live Event Start Date "+start_Date+ "==== Current Date "+CommonUtil.getCurrentDate());
//						if(Constant.DEBUG)  Log.d(TAG, "Date not Matched");
//					}
//
//				}else{
//					list.add(ProgramInfo); 
//				}
//				try {
//					Thread.sleep(2);
//				} catch (Exception e) {
//					e.printStackTrace();
//					StringWriter errors = new StringWriter();
//					e.printStackTrace(new PrintWriter(errors));
//					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//					continue;
//				}
//				cursor.moveToNext();
//			}
//		} else {
//			cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = ? ", new String[] {uniquID}, null, null, null);
//			cursor.moveToFirst();
//			while (!cursor.isAfterLast()) {
//				ProgramInfo ProgramInfo = cursorToProgramInfo(cursor);
//				if(!ProgramInfo.getEventCategory().equalsIgnoreCase("") && ProgramInfo.getEventCategory() != null && ProgramInfo.getEventCategory().equalsIgnoreCase("live") && ProgramInfo != null && ProgramInfo.getStartTime() != null && ProgramInfo.getDateAdded() != null){
//	
//					String eventTime_mm = null;
//					String sysTime_mm = null;
//	
//					String start_Date = null;
//					start_Date = ProgramInfo.getDateAdded();
//	
//					
//					if(!start_Date.equalsIgnoreCase("") && start_Date != null && CommonUtil.compareDates(CommonUtil.getCurrentDate(), start_Date)){
//						if(ProgramInfo.getStartTime().indexOf(":") != -1){
//							eventTime_mm = ProgramInfo.getStartTime().substring(0, ProgramInfo.getStartTime().indexOf(":"));
//						}
//						if(CommonUtil.getCurrentHourValue() != null && CommonUtil.getCurrentHourValue().indexOf(":") != -1){
//							sysTime_mm = CommonUtil.getCurrentHourValue().substring(0, ProgramInfo.getStartTime().indexOf(":")-1);
//						}
//						try {
//							if(Integer.parseInt(eventTime_mm.trim()) >= Integer.parseInt(sysTime_mm.trim())){
//								list.add(ProgramInfo); 
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}else{
//					//	if(Constant.DEBUG)  Log.d(TAG, ProgramInfo.getEventName()+" Live Event Start Date "+start_Date+ "==== Current Date "+CommonUtil.getCurrentDate());
//						if(Constant.DEBUG)  Log.d(TAG, "Date not Matched");
//					}
//	
//				}else{
//					list.add(ProgramInfo); 
//				}
//				try {
//					Thread.sleep(2);
//				} catch (Exception e) {
//					e.printStackTrace();
//					StringWriter errors = new StringWriter();
//					e.printStackTrace(new PrintWriter(errors));
//					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//					continue;
//				}
//				cursor.moveToNext();
//			}
//		}
//
//		if(cursor != null && !(cursor.isClosed())){
//			cursor.deactivate();
//			cursor.close();
//		}
		return list;
	}
	
	public int deleteEventInfoByServiceId(int ServiceId) {
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
//		return database.delete(Store.PROGRAM_TABLE, Store.PROGRAM_CHANNEL_SERVICE_ID + " = " + ServiceId, null);
		
		int row=mContext.getContentResolver().delete(CONTENT_URI_PROGRAM, 
				Store.PROGRAM_CHANNEL_SERVICE_ID + " = " + ServiceId, null);
		return row;
	}
	
	public int deleteEventInfoByType(String type) {
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
//		return database.delete(Store.PROGRAM_TABLE, Store.PROGRAM_TYPE + " = ?" , new String[] {type});
		
		int row=mContext.getContentResolver().delete(CONTENT_URI_PROGRAM, 
				Store.PROGRAM_TYPE + " = ?" , new String[] {type});
		return row;
	}
	
	
	public int updatePersonalInfoById(int eventId, int channelId,String name) {
		ContentValues values = new ContentValues();
		values.put(Store.PROGRAM_CHANNEL_SERVICE_ID, channelId);
		values.put(Store.PROGRAM_CHANNELNAME, name);
//		return database.update(Store.PROGRAM_TABLE, values, Store.PROGRAM_EVENT_ID + " = " +eventId, null);
		
		int row=mContext.getContentResolver().update(CONTENT_URI_PROGRAM, values, Store.PROGRAM_EVENT_ID + " = " +eventId, null);
		return row;
	}
	
	public int deleteEventInfoByDate(int serviceId,long timestamp) {
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
//		return database.delete(Store.PROGRAM_TABLE, Store.PROGRAM_TIMESTAMP+" < "+timestamp+" AND "+Store.PROGRAM_CHANNEL_SERVICE_ID + " = "+serviceId,  null);
		
		int row=mContext.getContentResolver().delete(CONTENT_URI_PROGRAM, 
				Store.PROGRAM_TIMESTAMP+" < "+timestamp+" AND "+Store.PROGRAM_CHANNEL_SERVICE_ID + " = "+serviceId,  null);
		return row;
	}
	

	public ProgramInfo getLiveEventInfo(int chlId,String name,String starttime) {
		Cursor cursor;
		ProgramInfo externalEventInfo = null;
		
		cursor=mContext.getContentResolver().query(CONTENT_URI_PROGRAM, 
				allColumns,Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+chlId+" AND "+Store.PROGRAM_START_TIME + " = ?", new String[] {starttime}, null, null);
		if(cursor != null) {
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				externalEventInfo = cursorToProgramInfo(cursor);
			}
		}
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
//		if (Constant.DVB) {		//DVB middleware
//			if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//			cursor = database.query(Store.PROGRAM_TABLE, allDvbColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+chlId+" AND "+Store.PROGRAM_START_TIME + " = ?", new String[] {starttime}, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				externalEventInfo = cursorToDvbProgramInfo(cursor);
//			}
//		} else {
//			cursor = database.query(Store.PROGRAM_TABLE, allColumns, Store.PROGRAM_CHANNEL_SERVICE_ID+" = "+chlId+" AND "+Store.PROGRAM_START_TIME + " = ?", new String[] {starttime}, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				externalEventInfo = cursorToProgramInfo(cursor);
//			}
//		}
//
//		// Make sure to close the cursor
//		cursor.close();
//		cursor.deactivate();
		return externalEventInfo;
	}
	
}
