package com.port.api.db.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
//import com.port.api.util.CommonUtil;
import android.util.Log;

import com.port.api.util.Constant;

public class BouquetGateway {

	private static final String TAG = "BouquetGateway";

	private static final String AUTHORITY = "com.port.apps.epg.contentprovider" ;
	public static final Uri CONTENT_URI_BOUQUET = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.BOUQUET_TABLE);

	private Context mContext ;
	/**
	 * Field declaration.
	 */
//	private SQLiteDatabase database;

	// 4 Fields
	private String[] allColumns = { Store.BOUQUET_ID,
			Store.BOUQUET_NAME, Store.BOUQUET_CATEGORY,
			Store.BOUQUET_DATE,Store.BOUQUET_TIMESTAMP};

	//DVB middleware
	private String[] allDvbColumns = { Store.BOUQUET_ID,
			Store.BOUQUET_NAME, Store.BOUQUET_CATEGORY,
			Store.BOUQUET_SERVICE_ID, Store.BOUQUET_TS_ID,Store.BOUQUET_LCN,
			Store.BOUQUET_DATE,Store.BOUQUET_TIMESTAMP};

//	public BouquetGateway(SQLiteDatabase database,Context argContext){
//		this.database=database;
//		this.mContext=argContext ;
//	}
	
	public BouquetGateway(Context argContext){
		this.mContext=argContext ;
	}

	public int insertBouquetInfo(/*int Id,*/ String bouquetName,String category,String date,long timestamp) {
		if (Constant.DEBUG) Log.d(TAG, "bouquetName: "+bouquetName);
		
		ContentValues values = new ContentValues();
		values.put(Store.BOUQUET_NAME, bouquetName);
		values.put(Store.BOUQUET_CATEGORY, category);
		values.put(Store.BOUQUET_DATE, date);
		values.put(Store.BOUQUET_TIMESTAMP, timestamp);

		if (Constant.DEBUG) Log.d(TAG, "values: "+values.toString());
		mContext.getContentResolver().insert(CONTENT_URI_BOUQUET, values) ;
		return maxRow();
		//-----------------------------------------------------
		//		if(database == null || !database.isOpen()){
		//			if(CommonUtil.checkConnectionForLocaldb()){
		//				database = CacheData.getDatabase();
		//			} else {
		//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
		//			}
		//		} else {
		//			//LogUtil.log(TAG, "database is not null and opened.");
		//		}
		//		if(Constant.DEBUG)  Log.d(TAG, "Bouquet values "+values);
		//		try{
		//			 database.insert(Store.BOUQUET_TABLE, null, values);
		//			 return maxRow();
		//		}catch(Exception e){
		//			e.printStackTrace();
		//			StringWriter errors = new StringWriter();
		//			e.printStackTrace(new PrintWriter(errors));
		//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		//			return 0;
		//		}
		
	}

	public int insertDvbBouquetInfo(String bouquetid ,String bouquetName,String category,int serviceId,int tsid,int lcn,String date,long timestamp) {
		ContentValues values = new ContentValues();
		values.put(Store.BOUQUET_ID,bouquetid);
		values.put(Store.BOUQUET_NAME, bouquetName);
		values.put(Store.BOUQUET_CATEGORY, category);

		values.put(Store.BOUQUET_SERVICE_ID, serviceId);
		values.put(Store.BOUQUET_TS_ID, tsid);
		values.put(Store.BOUQUET_LCN, lcn);

		values.put(Store.BOUQUET_DATE, date);
		values.put(Store.BOUQUET_TIMESTAMP, timestamp);
		
		mContext.getContentResolver().insert(CONTENT_URI_BOUQUET, values) ;
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
//		if(Constant.DEBUG)  Log.d(TAG, "Bouquet values "+values);
//		try{
//			database.insert(Store.BOUQUET_TABLE, null, values);
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

		Cursor c=mContext.getContentResolver().query(CONTENT_URI_BOUQUET, 
				allColumns, null, null, null) ;
		if (Constant.DEBUG) Log.d(TAG, "maxRow  (): "+c.getCount());
		
		// return biggest bouquet id  
		return c.getCount();
		//    	String query = "SELECT MAX("+Store.BOUQUET_ID+") AS max_id FROM "+Store.BOUQUET_TABLE;
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
		
		
	}

	public int updateBouquetInfo (int bouquetId, String bouquetName,String category,String date,long timestamp) {
		ContentValues values = new ContentValues();
		values.put(Store.BOUQUET_ID, bouquetId);
		values.put(Store.BOUQUET_NAME, bouquetName);
		values.put(Store.BOUQUET_CATEGORY, category);
		values.put(Store.BOUQUET_DATE, date);
		values.put(Store.BOUQUET_TIMESTAMP, timestamp);
		
		mContext.getContentResolver().update(CONTENT_URI_BOUQUET, values, Store.BOUQUET_ID + " = " +bouquetId, null);

//		database.update(Store.BOUQUET_TABLE, values, Store.BOUQUET_ID + " = " +bouquetId, null);
		return bouquetId;
	}

	public int updateDvbBouquetInfo (int bouquetId, String bouquetName,String category,int serviceId,int tsid,int lcn,String date,long timestamp) {
		ContentValues values = new ContentValues();
		values.put(Store.BOUQUET_ID, bouquetId);
		values.put(Store.BOUQUET_NAME, bouquetName);
		values.put(Store.BOUQUET_CATEGORY, category);

		values.put(Store.BOUQUET_SERVICE_ID, serviceId);
		values.put(Store.BOUQUET_TS_ID, tsid);
		values.put(Store.BOUQUET_LCN, lcn);

		values.put(Store.BOUQUET_DATE, date);
		values.put(Store.BOUQUET_TIMESTAMP, timestamp);

		mContext.getContentResolver().update(CONTENT_URI_BOUQUET, values, Store.BOUQUET_ID + " = " +bouquetId, null);
//		database.update(Store.BOUQUET_TABLE, values, Store.BOUQUET_ID + " = " +bouquetId, null);
		return bouquetId;
	}

	private BouquetInfo cursorToBouquetInfo(Cursor cursor) {
		BouquetInfo bouquetInfo = new BouquetInfo();
		bouquetInfo.setBouquetId(cursor.getInt(0));
		bouquetInfo.setBouquetName(cursor.getString(1));
		bouquetInfo.setCategory(cursor.getString(2));
		bouquetInfo.setDate(cursor.getString(3));
		bouquetInfo.setTimeStamp(cursor.getLong(4));
		if(Constant.DEBUG)Log.i(TAG, "cursorToBouquetInfo :"+bouquetInfo.getBouquetId()) ;

		return bouquetInfo;
	}

	private BouquetInfo cursorToDvbBouquetInfo(Cursor cursor) {
		BouquetInfo bouquetInfo = new BouquetInfo();
		bouquetInfo.setBouquetId(cursor.getInt(0));
		if(Constant.DEBUG)Log.i("cursorToDvbBouquetInfo getBouquetId", bouquetInfo.getBouquetId()+"");
		bouquetInfo.setBouquetName(cursor.getString(1));
		if(Constant.DEBUG)Log.i("cursorToDvbBouquetInfo getBouquetName", bouquetInfo.getBouquetName());
		
		bouquetInfo.setServiceId(cursor.getInt(2));
		if(Constant.DEBUG)Log.i("cursorToDvbBouquetInfo getServiceId", bouquetInfo.getServiceId()+"");
		bouquetInfo.setTSId(cursor.getInt(3));
		if(Constant.DEBUG)Log.i("cursorToDvbBouquetInfo getTSId", bouquetInfo.getTSId()+"");
		bouquetInfo.setLCN(cursor.getInt(4));
		

		bouquetInfo.setCategory(cursor.getString(5));
		bouquetInfo.setDate(cursor.getString(6));
		bouquetInfo.setTimeStamp(cursor.getLong(7));

		return bouquetInfo;
	}

	public List<BouquetInfo> getAllBouquetInfo() {
		//Added by @Tomesh only DVB Part for all DVB columns on 25 AUG 2015	
		if (Constant.DVB){
			List<BouquetInfo> bouquetInfos = new ArrayList<BouquetInfo>();
			 
//			Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_BOUQUET, 
//					allDvbColumns, null, null, null) ;
			//Added by @Tomesh for Distinct Value from Bouquet table on 25 Aug 2015 
			Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_BOUQUET, 
					new String[]{"DISTINCT Bouquet_id",
					Store.BOUQUET_NAME, Store.BOUQUET_CATEGORY,
					Store.BOUQUET_SERVICE_ID, Store.BOUQUET_TS_ID,Store.BOUQUET_LCN,
					Store.BOUQUET_DATE,Store.BOUQUET_TIMESTAMP}, "Bouquet_id IS NOT NULL) GROUP BY (Bouquet_id", null, null) ;
			if (cursor.moveToFirst()) {
				do {
					BouquetInfo bouquetInfo = cursorToDvbBouquetInfo(cursor);
					bouquetInfos.add(bouquetInfo); 
				} while (cursor.moveToNext());
			}
			return bouquetInfos ;		
		}else{
			List<BouquetInfo> bouquetInfos = new ArrayList<BouquetInfo>();
			Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_BOUQUET, 
					allColumns, null, null, null) ;
			if(Constant.DEBUG)Log.i(TAG, "getAllBouquetInfo");
			if (cursor.moveToFirst()) {
				do {
					BouquetInfo bouquetInfo = cursorToBouquetInfo(cursor);
					bouquetInfos.add(bouquetInfo); 
				} while (cursor.moveToNext());
			}
			return bouquetInfos ;

		}
		
	
		//    	try {
			//
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
		//			if (Constant.DVB) {	//DVB middleware
		//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
		////				cursor = this.database.query(Store.BOUQUET_TABLE, allDvbColumns, null, null, null, null, null);
		////				select * from bouquet group by bouquet_id ;
		//				cursor = this.database.query(Store.BOUQUET_TABLE, allDvbColumns, null, null, Store.BOUQUET_ID, null, null);  
		////				String query ="SELECT DISTINCT "+Store.BOUQUET_ID+", "+Store.BOUQUET_NAME+" FROM "+Store.BOUQUET_TABLE;
		////				cursor = this.database.rawQuery(query, null);
		//				if (cursor.moveToFirst()) {
		//					do {
		//						BouquetInfo bouquetInfo = cursorToDvbBouquetInfo(cursor);
		//						bouquetInfos.add(bouquetInfo); 
		//					} while (cursor.moveToNext());
		//				}
		//			} else {
		//				cursor = this.database.query(Store.BOUQUET_TABLE, allColumns, null, null, null, null, null);
		//				if (cursor.moveToFirst()) {
		//					do {
		//						BouquetInfo bouquetInfo = cursorToBouquetInfo(cursor);
		//						bouquetInfos.add(bouquetInfo); 
		//					} while (cursor.moveToNext());
		//				}
		//			}
		//			if (cursor != null && !cursor.isClosed()) {
		//				cursor.deactivate();
		//			}
		//			cursor.close();
		//			return bouquetInfos;
		//		}catch (Exception e) {
		//			e.printStackTrace();
		//			StringWriter errors = new StringWriter();
		//			e.printStackTrace(new PrintWriter(errors));
		//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		//		} 
		//		return null;
	}


	public BouquetInfo getBouquetInfoByName(String name) {
		BouquetInfo bouquetInfo = null;
		//Added by @Tomesh only DVB Part for all DVB columns on 25 AUG 2015
		if(Constant.DVB){
			Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_BOUQUET, 
					allDvbColumns, Store.BOUQUET_NAME + " = ? ", new String[] {name}, null, null);
			if(cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					bouquetInfo = cursorToDvbBouquetInfo(cursor);
				}
			}
			
		}else{
			Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_BOUQUET, 
					allColumns, Store.BOUQUET_NAME + " = ? ", new String[] {name}, null, null);
			if(cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					bouquetInfo = cursorToBouquetInfo(cursor);
				}
			}
		}
		return bouquetInfo ;

		//		
		//		if(database == null || !database.isOpen()){
		//			if(CommonUtil.checkConnectionForLocaldb()){
		//				database = CacheData.getDatabase();
		//			} else {
		//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
		//			}
		//		} else {
		////			//LogUtil.log(TAG, "database is not null and opened.");
		//		}
		//		try {
		//			if (Constant.DVB) {	//DVB middleware
		//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
		//				cursor = database.query(Store.BOUQUET_TABLE, allDvbColumns, Store.BOUQUET_NAME + " = ? ", new String[] {name}, null, null, null);
		//				if(cursor != null) {
		//					cursor.moveToFirst();
		//					if (!cursor.isAfterLast()) {
		//						bouquetInfo = cursorToDvbBouquetInfo(cursor);
		//					}
		//				}
		//			} else {
		//				cursor = database.query(Store.BOUQUET_TABLE, allColumns, Store.BOUQUET_NAME + " = ? ", new String[] {name}, null, null, null);
		//				if(cursor != null) {
		//					cursor.moveToFirst();
		//					if (!cursor.isAfterLast()) {
		//						bouquetInfo = cursorToBouquetInfo(cursor);
		//					}
		//				}
		//			}
		//		}catch (Exception e) {
		//			e.printStackTrace();
		//			StringWriter errors = new StringWriter();
		//			e.printStackTrace(new PrintWriter(errors));
		//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		//		}
		//		// Make sure to close the cursor
		//		cursor.close();
		//		cursor.deactivate();
		//		return bouquetInfo;
	}

	
//Added By Tomesh 26 AUG 2015	
	
	public  List<BouquetInfo> getDVBBouquetInfoById(int Id) {
		List<BouquetInfo> bouquetInfo = new ArrayList<BouquetInfo>();
		//Added by @Tomesh only DVB Part for all DVB columns on 25 AUG 2015
		if(Constant.DVB){
			Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_BOUQUET, 
					allDvbColumns, Store.BOUQUET_ID + " = "+ Id, null, null, null);
			if(cursor.moveToFirst()) {
				do {
					BouquetInfo bouquetInfos = cursorToDvbBouquetInfo(cursor);
					bouquetInfo.add(bouquetInfos); 
				} while (cursor.moveToNext());
			}
		}
		return bouquetInfo ;
	}
	
	
//	
	public BouquetInfo getBouquetInfoById(int Id) {
		BouquetInfo bouquetInfo = null;
		//Added by @Tomesh only DVB Part for all DVB columns on 25 AUG 2015
		if(Constant.DVB){
			Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_BOUQUET, 
					allDvbColumns, Store.BOUQUET_ID + " = "+ Id, null, null, null);
			if(cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					bouquetInfo = cursorToDvbBouquetInfo(cursor);
				}
			}
		}else{
			Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_BOUQUET, 
					allColumns, Store.BOUQUET_ID + " = "+ Id, null, null, null);
			if(cursor != null) {
				cursor.moveToFirst();
				if (!cursor.isAfterLast()) {
					bouquetInfo = cursorToBouquetInfo(cursor);
				}
			}
		}
		return bouquetInfo ;

//		BouquetInfo bouquetInfo = null;
//		Cursor cursor = null;
//
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			} else {
//				if(Constant.DEBUG)  Log.d(TAG, "still database is null or closed.");
//			}
//		} else {
//			//			//LogUtil.log(TAG, "database is not null and opened.");
//		}
//		try {
//			if (Constant.DVB) {	//DVB middleware
//				if (Constant.DEBUG) Log.d(TAG, "DVB Module");
//				cursor = database.query(Store.BOUQUET_TABLE, allDvbColumns, Store.BOUQUET_ID + " = "+ Id, null, null, null, null);
//				if(cursor != null) {
//					cursor.moveToFirst();
//					if (!cursor.isAfterLast()) {
//						bouquetInfo = cursorToDvbBouquetInfo(cursor);
//					}
//				}
//			} else {
//				cursor = database.query(Store.BOUQUET_TABLE, allColumns, Store.BOUQUET_ID + " = "+ Id, null, null, null, null);
//				if(cursor != null) {
//					cursor.moveToFirst();
//					if (!cursor.isAfterLast()) {
//						bouquetInfo = cursorToBouquetInfo(cursor);
//					}
//				}
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//		}
//		// Make sure to close the cursor
//		cursor.close();
//		cursor.deactivate();
//		return bouquetInfo;
	}


	public int deleteBouquetInfoByBouquetId(int bouquetId, long timestamp) {
		int row=mContext.getContentResolver().delete(CONTENT_URI_BOUQUET, 
				Store.BOUQUET_TIMESTAMP+" < "+timestamp+" AND "+Store.BOUQUET_ID+" = "+bouquetId, null) ;
		return row ;		
//				
//				
//				query(CONTENT_URI_BOUQUET, 
//				allColumns, Store.BOUQUET_ID + " = "+ Id, null, null, null);
//		
//
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
//
//		return database.delete(Store.BOUQUET_TABLE, Store.BOUQUET_TIMESTAMP+" < "+timestamp+" AND "+Store.BOUQUET_ID+" = "+bouquetId, null);
	}

	public int deleteBouquetInfoByBouquetId(int bouquetId) {
		int row=mContext.getContentResolver().delete(CONTENT_URI_BOUQUET, 
				 Store.BOUQUET_ID+" = "+bouquetId, null) ;
		return row ;	
		
		
//		if(database == null || !database.isOpen()){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				database = CacheData.getDatabase();
//			}
//		}
//
//		return database.delete(Store.BOUQUET_TABLE, Store.BOUQUET_ID+" = "+bouquetId, null);
	}

}
