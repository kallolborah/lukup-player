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

public class ChannelGateway {
	private static final String TAG = "ChannelGateway";
	
	private Context mContext ;
	private static final String AUTHORITY = "com.port.apps.epg.contentprovider" ;
	public static final Uri CONTENT_URI_CHANNEL = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.CHANNEL_TABLE);

	/**
	 * Field declaration.
	 */
//	private SQLiteDatabase database;
	
	// 16 Field
	private String[] allColumns = { 
			Store.CHANNEL_SERVICE_ID, Store.CHANNEL_TS_ID,
			Store.CHANNEL_NETWORK_ID ,Store.CHANNEL_TYPE,
			Store.CHANNEL_NAME,
			Store.CHANNEL_BOUQUET_ID,Store.CHANNEL_USER_ID,
			Store.CHANNEL_DESCRIPTION,Store.CHANNEL_MATURITY,
			Store.CHANNEL_PRICE, Store.CHANNEL_PRICING_MODEL,
			Store.CHANNEL_EXPIRY_DATE ,Store.CHANNEL_CA_SCRAMBLED,
			Store.CHANNEL_RUNNING_STATUS,Store.CHANNEL_CATEGORY,
			Store.CHANNEL_LOGO,Store.CHANNEL_DATE,Store.CHANNEL_TIMESTAMP,Store.CHANNEL_URL};//Added 4th Aug 2015
	
	//DVB middleware
	private String[] allDvbColumns = { 
			Store.CHANNEL_SERVICE_ID, Store.CHANNEL_TS_ID,
			Store.CHANNEL_NETWORK_ID ,Store.CHANNEL_TYPE,
			Store.CHANNEL_NAME,
			Store.CHANNEL_BOUQUET_ID,Store.CHANNEL_USER_ID,
			Store.CHANNEL_DESCRIPTION,Store.CHANNEL_MATURITY,
			Store.CHANNEL_PRICE, Store.CHANNEL_PRICING_MODEL,
			Store.CHANNEL_EXPIRY_DATE ,Store.CHANNEL_CA_SCRAMBLED,
			Store.CHANNEL_RUNNING_STATUS,Store.CHANNEL_CATEGORY,
			Store.CHANNEL_EIT_SCHEDULE,Store.CHANNEL_EIT_PRESENT,
			Store.CHANNEL_LOGO,Store.CHANNEL_DATE,Store.CHANNEL_TIMESTAMP,Store.CHANNEL_LCN,Store.CHANNEL_DVBTYPE,Store.CHANNEL_URL};
	
	
//	public ChannelGateway(SQLiteDatabase database,Context argContext){
//		this.database=database;
//		this.mContext=argContext ;
//	}
	
	public ChannelGateway(Context argContext){
		this.mContext=argContext ;
	}
	
	public int insertChannelInfo(int serviceId,int tsId,String networkId,String type, String channelName,int bouquetId, int userId,
			String Desc, String maturity, float price, String priceModel, String expiryDate, int caScrambled, int runningStatus,
			String serviceCategory,String logo,String date,long timestamp,String source) {  
		ContentValues values = new ContentValues();
		values.put(Store.CHANNEL_SERVICE_ID, serviceId);
		values.put(Store.CHANNEL_TS_ID, tsId);
		values.put(Store.CHANNEL_NETWORK_ID, networkId);
		values.put(Store.CHANNEL_TYPE, type);
		values.put(Store.CHANNEL_CATEGORY, serviceCategory);
		values.put(Store.CHANNEL_NAME, channelName);
		values.put(Store.CHANNEL_BOUQUET_ID, bouquetId);
		values.put(Store.CHANNEL_USER_ID, userId);
		values.put(Store.CHANNEL_DESCRIPTION, Desc);
		values.put(Store.CHANNEL_MATURITY, maturity);
		values.put(Store.CHANNEL_PRICE, price);
		values.put(Store.CHANNEL_PRICING_MODEL, priceModel);
		values.put(Store.CHANNEL_EXPIRY_DATE, expiryDate);
		values.put(Store.CHANNEL_CA_SCRAMBLED, caScrambled);
		values.put(Store.CHANNEL_RUNNING_STATUS, runningStatus);
		values.put(Store.CHANNEL_LOGO, logo);
		values.put(Store.CHANNEL_DATE, date);
		values.put(Store.CHANNEL_TIMESTAMP, timestamp);
		values.put(Store.CHANNEL_URL, source);//Added by Tomesh
		mContext.getContentResolver().insert(CONTENT_URI_CHANNEL, values) ;
			
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
//			//LogUtil.log(TAG, "database is not null and opened.");
//		}
//		if(Constant.DEBUG)  Log.d(TAG, "Service values "+values);
//		try{
//			database.insert(Store.CHANNEL_TABLE, null, values);
//			return maxRow();
//		}catch(Exception e){
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//			return 0;
//		}
		return 0;
	}
	
	//DVB middleware
	public int insertDvbChannelInfo(int serviceId,int lcn,int dvbtype,int tsId,int networkId,String type, String channelName,int bouquetId, int userId,
			String Desc, String maturity, float price, String priceModel, String expiryDate, int caScrambled, int runningStatus,
			String serviceCategory,String logo,int eschedule,int epresent,String date,long timestamp,String source) {  
		ContentValues values = new ContentValues();
		values.put(Store.CHANNEL_SERVICE_ID, serviceId);
		values.put(Store.CHANNEL_LCN, lcn);
		values.put(Store.CHANNEL_DVBTYPE, dvbtype);
		values.put(Store.CHANNEL_TS_ID, tsId);
		values.put(Store.CHANNEL_NETWORK_ID, networkId);
		values.put(Store.CHANNEL_TYPE, type);
		values.put(Store.CHANNEL_CATEGORY, serviceCategory);
		values.put(Store.CHANNEL_NAME, channelName);
		values.put(Store.CHANNEL_BOUQUET_ID, bouquetId);
		values.put(Store.CHANNEL_USER_ID, userId);
		values.put(Store.CHANNEL_DESCRIPTION, Desc);
		values.put(Store.CHANNEL_MATURITY, maturity);
		values.put(Store.CHANNEL_PRICE, price);
		values.put(Store.CHANNEL_PRICING_MODEL, priceModel);
		values.put(Store.CHANNEL_EXPIRY_DATE, expiryDate);
		values.put(Store.CHANNEL_CA_SCRAMBLED, caScrambled);
		values.put(Store.CHANNEL_RUNNING_STATUS, runningStatus);
		values.put(Store.CHANNEL_LOGO, logo);
		
		values.put(Store.CHANNEL_EIT_SCHEDULE, eschedule);
		values.put(Store.CHANNEL_EIT_PRESENT, epresent);
		
		values.put(Store.CHANNEL_DATE, date);
		values.put(Store.CHANNEL_TIMESTAMP, timestamp);
		values.put(Store.CHANNEL_URL, source);//Added by Tomesh
		mContext.getContentResolver().insert(CONTENT_URI_CHANNEL, values) ;
		return maxRow();
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
//			//LogUtil.log(TAG, "database is not null and opened.");
//		}
//		if(Constant.DEBUG)  Log.d(TAG, "Service values "+values);
//		try{
//			database.insert(Store.CHANNEL_TABLE, null, values);
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
		
		Cursor c=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
				allColumns, null, null, null) ;
		return c.getPosition();
//		String query = "SELECT MAX("+Store.CHANNEL_SERVICE_ID+") AS max_id FROM "+Store.CHANNEL_TABLE;
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
	
	public int updateChannelInfo (/*int channelId,*/ int serviceId,int tsId,String networkId,String type, String channelName,int bouquetId, int userId,
			String Desc, String maturity, float price, String priceModel, String expiryDate, int caScrambled, int runningStatus,
			String serviceCategory,String logo,String date,long timestamp,String source) {
		ContentValues values = new ContentValues();
		values.put(Store.CHANNEL_SERVICE_ID, serviceId);
		values.put(Store.CHANNEL_TS_ID, tsId);
		values.put(Store.CHANNEL_NETWORK_ID, networkId);
		values.put(Store.CHANNEL_TYPE, type);
		values.put(Store.CHANNEL_CATEGORY, serviceCategory);
		values.put(Store.CHANNEL_NAME, channelName);
		values.put(Store.CHANNEL_BOUQUET_ID, bouquetId);
		values.put(Store.CHANNEL_USER_ID, userId);
		values.put(Store.CHANNEL_DESCRIPTION, Desc);
		values.put(Store.CHANNEL_MATURITY, maturity);
		values.put(Store.CHANNEL_PRICE, price);
		values.put(Store.CHANNEL_PRICING_MODEL, priceModel);
		values.put(Store.CHANNEL_EXPIRY_DATE, expiryDate);
		values.put(Store.CHANNEL_CA_SCRAMBLED, caScrambled);
		values.put(Store.CHANNEL_RUNNING_STATUS, runningStatus);
		values.put(Store.CHANNEL_LOGO, logo);
		values.put(Store.CHANNEL_DATE, date);
		values.put(Store.CHANNEL_TIMESTAMP, timestamp);
		values.put(Store.CHANNEL_URL, source); // Added by Tomesh
		mContext.getContentResolver().update(CONTENT_URI_CHANNEL, values, Store.CHANNEL_SERVICE_ID + " = " +serviceId, null);

//		 database.update(Store.CHANNEL_TABLE, values, Store.CHANNEL_SERVICE_ID + " = " +serviceId, null);
		 return serviceId;
	}
	
	//DVB middleware
	public int updateDvbChannelInfo (/*int channelId,*/ int serviceId,int lcn,int dvbtype,int tsId,int networkId,String type, String channelName,int bouquetId, int userId,
			String Desc, String maturity, float price, String priceModel, String expiryDate, int caScrambled, int runningStatus,
			String serviceCategory,String logo,int eschedule,int epresent,String date,long timestamp,String source) {
		ContentValues values = new ContentValues();
		values.put(Store.CHANNEL_SERVICE_ID, serviceId);
		values.put(Store.CHANNEL_LCN, lcn);
		values.put(Store.CHANNEL_DVBTYPE, dvbtype);
		values.put(Store.CHANNEL_TS_ID, tsId);
		values.put(Store.CHANNEL_NETWORK_ID, networkId);
		values.put(Store.CHANNEL_TYPE, type);
		values.put(Store.CHANNEL_CATEGORY, serviceCategory);
		values.put(Store.CHANNEL_NAME, channelName);
		values.put(Store.CHANNEL_BOUQUET_ID, bouquetId);
		values.put(Store.CHANNEL_USER_ID, userId);
		values.put(Store.CHANNEL_DESCRIPTION, Desc);
		values.put(Store.CHANNEL_MATURITY, maturity);
		values.put(Store.CHANNEL_PRICE, price);
		values.put(Store.CHANNEL_PRICING_MODEL, priceModel);
		values.put(Store.CHANNEL_EXPIRY_DATE, expiryDate);
		values.put(Store.CHANNEL_CA_SCRAMBLED, caScrambled);
		values.put(Store.CHANNEL_RUNNING_STATUS, runningStatus);
		values.put(Store.CHANNEL_LOGO, logo);
		
		values.put(Store.CHANNEL_EIT_SCHEDULE, eschedule);
		values.put(Store.CHANNEL_EIT_PRESENT, epresent);
		
		values.put(Store.CHANNEL_DATE, date);
		values.put(Store.CHANNEL_TIMESTAMP, timestamp);
		values.put(Store.CHANNEL_URL, source); // Added by Tomesh
		mContext.getContentResolver().update(CONTENT_URI_CHANNEL, values, Store.CHANNEL_SERVICE_ID + " = " +serviceId, null);

//		 database.update(Store.CHANNEL_TABLE, values, Store.CHANNEL_SERVICE_ID + " = " +serviceId, null);
		 return serviceId;
	}
	
	
	private ChannelInfo cursorToChannelInfo(Cursor cursor) {
		ChannelInfo channelInfo = new ChannelInfo();
		channelInfo.setServiceId(cursor.getInt(0));
		channelInfo.setTsId(cursor.getInt(1));
		channelInfo.setNetworkId(cursor.getString(2));
		channelInfo.setType(cursor.getString(3));
		channelInfo.setChannelName(cursor.getString(4));
		channelInfo.setBouquetId(cursor.getInt(5));
		channelInfo.setUserId(cursor.getInt(6));
		channelInfo.setDesc(cursor.getString(7));
		channelInfo.setMaturity(cursor.getString(8));
		channelInfo.setPrice(cursor.getFloat(9));
		channelInfo.setPriceModel(cursor.getString(10));
		channelInfo.setExpiryDate(cursor.getString(11));
		channelInfo.setCaScrambled(cursor.getInt(12));
		channelInfo.setRunningStatus(cursor.getInt(13));
		channelInfo.setServiceCategory(cursor.getString(14));
		channelInfo.setChannelLogo(cursor.getString(15));
		channelInfo.setDate(cursor.getString(16));
		channelInfo.setTimeStamp(cursor.getLong(17));
		channelInfo.setChannelurl(cursor.getString(18));// Added by tomesh
		if(Constant.DEBUG){Log.i(TAG, "getServiceId"+channelInfo.getServiceId()+"getPriceModel" +channelInfo.getPriceModel()+ " getPrice "+channelInfo.getPrice()+" timestamp"+channelInfo.getTimeStamp());}
		return channelInfo;
	}
	
	//DVB middleware
	private ChannelInfo cursorToDvbChannelInfo(Cursor cursor) {
		ChannelInfo channelInfo = new ChannelInfo();
		channelInfo.setServiceId(cursor.getInt(0));Log.i(TAG, cursor.getColumnName(0));
		channelInfo.setTsId(cursor.getInt(1));Log.i(TAG, cursor.getColumnName(1));
		channelInfo.setNetworkid(cursor.getInt(2));Log.i(TAG, cursor.getColumnName(2));
		channelInfo.setType(cursor.getString(3));Log.i(TAG, cursor.getColumnName(3));
		channelInfo.setChannelName(cursor.getString(4));Log.i(TAG, cursor.getColumnName(4));
		channelInfo.setBouquetId(cursor.getInt(5));Log.i(TAG, cursor.getColumnName(5));
		channelInfo.setUserId(cursor.getInt(6));Log.i(TAG, cursor.getColumnName(6));
		channelInfo.setDesc(cursor.getString(7));Log.i(TAG, cursor.getColumnName(7));
		channelInfo.setMaturity(cursor.getString(8));Log.i(TAG, cursor.getColumnName(8));
		channelInfo.setPrice(cursor.getFloat(9));Log.i(TAG, cursor.getColumnName(9));
		channelInfo.setPriceModel(cursor.getString(10));Log.i(TAG, cursor.getColumnName(10));
		channelInfo.setExpiryDate(cursor.getString(11));Log.i(TAG, cursor.getColumnName(11));
		channelInfo.setCaScrambled(cursor.getInt(12));Log.i(TAG, cursor.getColumnName(12));
		channelInfo.setRunningStatus(cursor.getInt(13));Log.i(TAG, cursor.getColumnName(13));
		channelInfo.setServiceCategory(cursor.getString(14));Log.i(TAG, cursor.getColumnName(14));
		channelInfo.setChannelLogo(cursor.getString(15));Log.i(TAG, cursor.getColumnName(15));
		channelInfo.setEitSchedule(cursor.getInt(16));Log.i(TAG, cursor.getColumnName(16));
		channelInfo.setEitPresent(cursor.getInt(17));Log.i(TAG, cursor.getColumnName(17));
		channelInfo.setDate(cursor.getString(18));Log.i(TAG, cursor.getColumnName(18));
		channelInfo.setTimeStamp(cursor.getLong(19));Log.i(TAG, cursor.getColumnName(19));
		channelInfo.setLCN(cursor.getInt(20));Log.i(TAG, cursor.getColumnName(20));
		channelInfo.setDvbType(cursor.getInt(21));Log.i(TAG, cursor.getColumnName(21));
		channelInfo.setChannelurl(cursor.getString(22));Log.i(TAG, cursor.getColumnName(22));
		
		
		// Added by @Tomesh for Live channels URL
		
		
		if (Constant.DEBUG) {
			Log.i(TAG,
					"getServiceId" + channelInfo.getServiceId() + "getTsId"
							+ channelInfo.getTsId() + " networkID "
							+ channelInfo.getNetworkid() + "TYpe"
							+ channelInfo.getType() + " channel name"
							+ channelInfo.getChannelName());
		}
		if (Constant.DEBUG) {
			Log.i(TAG,
					"getBouquetId" + channelInfo.getBouquetId() + " getUserId"
							+ channelInfo.getUserId() + " getDesc "
							+ channelInfo.getDesc() + " getMaturity"
							+ channelInfo.getMaturity() + " getPrice"
							+ channelInfo.getPrice() + " getPriceModel"
							+ channelInfo.getPriceModel() + "getExpiryDate"
							+ channelInfo.getExpiryDate()+" getCaScrambled"
							+ channelInfo.getCaScrambled()+" getRunningStatus"
							+ channelInfo.getRunningStatus() + " getServiceCategory"
							+ channelInfo.getServiceCategory()+" getChannelLogo"
							+ channelInfo.getChannelLogo()+" getEitSchedule"
							+ channelInfo.getEitSchedule()+" getEitPresent"
							+channelInfo.getEitPresent()+" getDate"
							+ channelInfo.getDate()+" getTimeStamp"
							+ channelInfo.getTimeStamp()+" getLCN"
							+ channelInfo.getLCN()+" getDvbType" 
							+ channelInfo.getDvbType()+" getChannelurl"
							+ channelInfo.getChannelurl());
			
		}
		return channelInfo;
	}
	
	public List<ChannelInfo> getAllChannelInfo() {
		List<ChannelInfo> channelInfos = new ArrayList<ChannelInfo>();
		Cursor cursor = null;
		try {//Added by @Tomesh only DVB PART on 26 AUG 2015
			if(Constant.DVB){
				cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
						allDvbColumns, null, null, null) ;
	
				if (cursor.moveToFirst()) {
					do {
						ChannelInfo channelInfo = cursorToDvbChannelInfo(cursor);
						channelInfos.add(channelInfo); 
					} while (cursor.moveToNext());
				}
			}else{
			cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
					allColumns, null, null, null) ;
			
			if (cursor.moveToFirst()) {
				do {
					ChannelInfo channelInfo = cursorToChannelInfo(cursor);
					channelInfos.add(channelInfo); 
				} while (cursor.moveToNext());
			}
		}
			return channelInfos;
			
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
//			if (Constant.DVB) {//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = this.database.query(Store.CHANNEL_TABLE, allDvbColumns, null, null, null, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ChannelInfo channelInfo = cursorToDvbChannelInfo(cursor);
//						channelInfos.add(channelInfo); 
//					} while (cursor.moveToNext());
//				}
//			} else {
//				cursor = this.database.query(Store.CHANNEL_TABLE, allColumns, null, null, null, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ChannelInfo channelInfo = cursorToChannelInfo(cursor);
//						channelInfos.add(channelInfo); 
//					} while (cursor.moveToNext());
//				}
//			}
//			
//			if (cursor != null && !cursor.isClosed()) {
//				cursor.deactivate();
//			}
//			cursor.close();
//			return channelInfos;
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} 
		return null;
	}
	
	
	
//Added by Tomesh
	public ChannelInfo getDVBServiceInfoByServiceId(int serviceId) {
		Cursor cursor;
		ChannelInfo serviceInfo = null;
		
		if(Constant.DVB){
			cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
					allDvbColumns,Store.CHANNEL_SERVICE_ID+" = "+serviceId,null, null, null) ;
			
			if(cursor.moveToFirst())
			serviceInfo = cursorToChannelInfo(cursor);
			
			if(Constant.DEBUG)Log.i("getDVBServiceInfoByServiceId :", serviceInfo + "");
		}return serviceInfo;
	}
//Added till Here	
	
	public ChannelInfo getServiceInfoByServiceId(int serviceId,String type) {
		Cursor cursor;
		ChannelInfo serviceInfo = null;
		
		if(Constant.DVB){//Added by @Tomesh on26 AUG 2015
			cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
					allDvbColumns,Store.CHANNEL_SERVICE_ID+" = "+serviceId+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null) ;
			
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				serviceInfo = cursorToDvbChannelInfo(cursor);
			}
		}else{
			cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
					allColumns,Store.CHANNEL_SERVICE_ID+" = "+serviceId+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null) ;
			
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				serviceInfo = cursorToChannelInfo(cursor);
			}
		}
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
//			//			LogUtil.log(TAG, "database is not null and opened.");
//		}
//		
//		if (Constant.DVB) {	//DVB middleware
//			if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//			cursor = database.query(Store.CHANNEL_TABLE, allDvbColumns, Store.CHANNEL_SERVICE_ID+" = "+serviceId+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				serviceInfo = cursorToDvbChannelInfo(cursor);
//			}
//		} else {
//			cursor = database.query(Store.CHANNEL_TABLE, allColumns, Store.CHANNEL_SERVICE_ID+" = "+serviceId+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				serviceInfo = cursorToChannelInfo(cursor);
//			}
//		}
//		// Make sure to close the cursor
//		cursor.close();
//		cursor.deactivate();
		return serviceInfo;
	}
	
	public ChannelInfo getServiceInfoByDvbLCN(int lcn) {
		Cursor cursor = null;
		ChannelInfo serviceInfo = null;
		try{
			cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
					allDvbColumns, Store.CHANNEL_LCN+" = "+lcn, null, null, null) ;
			
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				serviceInfo = cursorToDvbChannelInfo(cursor);
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
//			if (Constant.DVB) {	//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = database.query(Store.CHANNEL_TABLE, allDvbColumns, Store.CHANNEL_LCN+" = "+lcn, null, null, null, null);
//				cursor.moveToFirst();
//				if (!cursor.isAfterLast()) {
//					serviceInfo = cursorToDvbChannelInfo(cursor);
//				}
//			} 
//			// Make sure to close the cursor
//			cursor.close();
//			cursor.deactivate();
			return serviceInfo;
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return serviceInfo; 
	}
	
	public ChannelInfo getServiceInfoByServiceId(int channelId) {
		Cursor cursor;
		ChannelInfo serviceInfo = null;
		
		if(Constant.DVB){//Added by @Tomesh on26 AUG 2015
			cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
					allDvbColumns, Store.CHANNEL_SERVICE_ID+" = "+channelId, null, null, null) ;
	
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				serviceInfo = cursorToDvbChannelInfo(cursor);
			}//Added till here
	
//			if(cursor.moveToFirst())
//			serviceInfo = cursorToDvbChannelInfo(cursor);
		}else{
			cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
					allColumns, Store.CHANNEL_SERVICE_ID+" = "+channelId, null, null, null) ;
			
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				serviceInfo = cursorToChannelInfo(cursor);
			}
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
//		if (Constant.DVB) {	//DVB middleware
//			if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//			cursor = database.query(Store.CHANNEL_TABLE, allDvbColumns, Store.CHANNEL_SERVICE_ID+" = "+channelId, null, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				serviceInfo = cursorToDvbChannelInfo(cursor);
//			}
//		} else {
//			cursor = database.query(Store.CHANNEL_TABLE, allColumns, Store.CHANNEL_SERVICE_ID+" = "+channelId, null, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				serviceInfo = cursorToChannelInfo(cursor);
//			}
//		}
//		// Make sure to close the cursor
//		cursor.close();
//		cursor.deactivate();
		return serviceInfo;
	}
	
	public List<ChannelInfo> getAllServiceList() {
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
//			//LogUtil.log(TAG, "database is not null and opened.");
//		}

		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		Cursor cursor = null;
		try {
			if(Constant.DVB){//Added by TOmesh For DVB Columns
				cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
						 allDvbColumns, null, null, null, null) ;
				
				if (cursor.moveToFirst()) {
					do {
						ChannelInfo Info = cursorToDvbChannelInfo(cursor);
						list.add(Info); 
					} while (cursor.moveToNext());
				}//Added by TOmesh For DVB Columns
			}else{
				cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
						 allColumns, null, null, null, null) ;
				
				if (cursor.moveToFirst()) {
					do {
						ChannelInfo Info = cursorToChannelInfo(cursor);
						list.add(Info); 
					} while (cursor.moveToNext());
				}
			}
			
			
			
//			if (Constant.DVB) {	//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = this.database.query(Store.CHANNEL_TABLE,  allDvbColumns, null, null, null, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ChannelInfo Info = cursorToDvbChannelInfo(cursor);
//						list.add(Info); 
//					} while (cursor.moveToNext());
//				}
//			} else {
//				cursor = this.database.query(Store.CHANNEL_TABLE,  allColumns, null, null, null, null, null);
//				if (cursor.moveToFirst()) {
//					do {
//						ChannelInfo Info = cursorToChannelInfo(cursor);
//						list.add(Info); 
//					} while (cursor.moveToNext());
//				}
//			}
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

//		if(cursor != null){
//			cursor.close();
//			cursor.deactivate();
//		}
		return list;
	}
	
	public List<ChannelInfo> getAllServiceInfoByBouquetId(int bouquetId) {
		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		Cursor cursor = null;
		/*if(Constant.DVB){//Added by TOmesh For DVB Columns
			
			cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
					allDvbColumns, Store.CHANNEL_BOUQUET_ID+" = "+bouquetId, null, null, null) ;
			if (cursor.moveToFirst()) {
				do {
					ChannelInfo Info = cursorToChannelInfo(cursor);
					list.add(Info); 
				} while (cursor.moveToNext());
			}//Till Here
		}else*/{
			cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
					allColumns, Store.CHANNEL_BOUQUET_ID+" = "+bouquetId, null, null, null) ;
			if (cursor.moveToFirst()) {
				do {
					ChannelInfo Info = cursorToChannelInfo(cursor);
					list.add(Info); 
				} while (cursor.moveToNext());
			}
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
//		if (Constant.DVB) {	//DVB middleware
//			
////			cursor = this.database.query(Store.CHANNEL_TABLE, allDvbColumns, Store.CHANNEL_BOUQUET_ID+" = "+bouquetId, null, null, null,null);
//			
//			String query ="select  a.* from channel a, bouquet b where b.bouquet_id = "+bouquetId+" and a.service_id = b.service_id and a.ts_id = b.ts_id group by channel_name";
//			cursor = this.database.rawQuery(query, null);
//			if (Constant.DEBUG) Log.d(TAG, "DVB Module ");
//			if (cursor.moveToFirst()) {
//				do {
//					ChannelInfo Info = cursorToDvbChannelInfo(cursor);
//					list.add(Info); 
//				} while (cursor.moveToNext());
//			}
//		} else {
//			cursor = this.database.query(Store.CHANNEL_TABLE, allColumns, Store.CHANNEL_BOUQUET_ID+" = "+bouquetId, null, null, null,null);
//			if (cursor.moveToFirst()) {
//				do {
//					ChannelInfo Info = cursorToChannelInfo(cursor);
//					list.add(Info); 
//				} while (cursor.moveToNext());
//			}
//		}
//
//		if (cursor != null && !cursor.isClosed()) {
//			cursor.deactivate();
//		}
//		cursor.close();
		return list;
	}
	
	
	public ChannelInfo getPersonalServiceInfo(String name) {
		Cursor cursor;
		ChannelInfo serviceInfo = null;
		
		cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
				allColumns, Store.CHANNEL_NAME+" = ? "+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {name, "personal"}, null, null) ;
		
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			serviceInfo = cursorToChannelInfo(cursor);
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
//		if (Constant.DVB) {	//DVB middleware
//			if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//			cursor = database.query(Store.CHANNEL_TABLE, allDvbColumns, Store.CHANNEL_NAME+" = ? "+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {name, "personal"}, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				serviceInfo = cursorToDvbChannelInfo(cursor);
//			}
//		} else {
//			cursor = database.query(Store.CHANNEL_TABLE, allColumns, Store.CHANNEL_NAME+" = ? "+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {name, "personal"}, null, null, null);
//			cursor.moveToFirst();
//			if (!cursor.isAfterLast()) {
//				serviceInfo = cursorToChannelInfo(cursor);
//			}
//		}
//		// Make sure to close the cursor
//		cursor.close();
//		cursor.deactivate();
		return serviceInfo;
	}
	
	
	public int renameServiceInfoByChannelId(int channelId, String newName) {
		ContentValues values = new ContentValues();
		values.put(Store.CHANNEL_NAME, newName);
		
		int row=mContext.getContentResolver().update(CONTENT_URI_CHANNEL, values, Store.CHANNEL_SERVICE_ID + " = " +channelId, null);
		return row;
		
//		return database.update(Store.CHANNEL_TABLE, values, Store.CHANNEL_SERVICE_ID + " = " +channelId, null);
	}
	
	public int deleteServiceInfoByChannelId(int channelId) {
		int row=mContext.getContentResolver().delete(CONTENT_URI_CHANNEL, 
				Store.CHANNEL_SERVICE_ID + " = " + channelId, null);
		return row;
//		return database.delete(Store.CHANNEL_TABLE, Store.CHANNEL_SERVICE_ID + " = " + channelId, null);
	}
	
	public List<ChannelInfo> getAllServiceInfoByType(String type) {
		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		Cursor cursor;
		
		cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
				allColumns, Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null) ;
		
		if (cursor.moveToFirst()) {
			do {
				ChannelInfo Info = cursorToChannelInfo(cursor);
				list.add(Info); 
			} while (cursor.moveToNext());
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
//		if (Constant.DVB) {	//DVB middleware
//			if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//			cursor = this.database.query(Store.CHANNEL_TABLE, allDvbColumns, Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null,null);
//			if (cursor.moveToFirst()) {
//				do {
//					ChannelInfo Info = cursorToDvbChannelInfo(cursor);
//					list.add(Info); 
//				} while (cursor.moveToNext());
//			}
//		} else {
//			cursor = this.database.query(Store.CHANNEL_TABLE, allColumns, Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null,null);
//			if (cursor.moveToFirst()) {
//				do {
//					ChannelInfo Info = cursorToChannelInfo(cursor);
//					list.add(Info); 
//				} while (cursor.moveToNext());
//			}
//		}
//
//		if (cursor != null && !cursor.isClosed()) {
//			cursor.deactivate();
//		}
//		cursor.close();
		return list;
	}
		
	public int deleteServiceInfoByDate(long timestamp,String type) {
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
		
		int row=mContext.getContentResolver().delete(CONTENT_URI_CHANNEL, 
				Store.CHANNEL_TIMESTAMP+" < "+timestamp+" AND "+Store.CHANNEL_TYPE+" = ? ",  new String[] {type});
		return row;
//		return database.delete(Store.CHANNEL_TABLE, Store.CHANNEL_TIMESTAMP+" < "+timestamp+" AND "+Store.CHANNEL_TYPE+" = ? ",  new String[] {type});
	}
	
	public List<ChannelInfo> getAllServiceInfoByTimeStamp(long timestamp,String type) {
		List<ChannelInfo> list = new ArrayList<ChannelInfo>();
		Cursor cursor;
		
		cursor=mContext.getContentResolver().query(CONTENT_URI_CHANNEL, 
				allColumns,Store.CHANNEL_TIMESTAMP+" < "+timestamp+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null) ;
		
		if (cursor.moveToFirst()) {
			do {
				ChannelInfo Info = cursorToChannelInfo(cursor);
				list.add(Info); 
			} while (cursor.moveToNext());
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
//		if (Constant.DVB) {	//DVB middleware
//			if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//			cursor = this.database.query(Store.CHANNEL_TABLE, allDvbColumns, Store.CHANNEL_TIMESTAMP+" < "+timestamp+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null,null);
//			if(Constant.DEBUG)  Log.d(TAG, "cursor:  "+cursor.getCount());
//			if (cursor.moveToFirst()) {
//				do {
//					ChannelInfo Info = cursorToDvbChannelInfo(cursor);
//					if(Constant.DEBUG)  Log.d(TAG, "CHANNEL_TIMESTAMP:  "+Info.getTimeStamp());
//					list.add(Info); 
//				} while (cursor.moveToNext());
//			}
//		} else {
//			cursor = this.database.query(Store.CHANNEL_TABLE, allColumns, Store.CHANNEL_TIMESTAMP+" < "+timestamp+" AND "+Store.CHANNEL_TYPE+" = ? ", new String[] {type}, null, null,null);
//			if(Constant.DEBUG)  Log.d(TAG, "cursor:  "+cursor.getCount());
//			if (cursor.moveToFirst()) {
//				do {
//					ChannelInfo Info = cursorToChannelInfo(cursor);
//					if(Constant.DEBUG)  Log.d(TAG, "CHANNEL_TIMESTAMP:  "+Info.getTimeStamp());
//					list.add(Info); 
//				} while (cursor.moveToNext());
//			}
//		}
//
//		if (cursor != null && !cursor.isClosed()) {
//			cursor.deactivate();
//		}
//		cursor.close();
		return list;
	}
	
	
	
}
