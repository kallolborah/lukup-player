package com.port.api.db.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class CacheGateway {
	private static final String TAG = "CacheGateway";	//	pushFile

	private static final String AUTHORITY = "com.port.apps.epg.contentprovider" ;
	public static final Uri CONTENT_URI_CACHEDATA = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.CACHEDATA_TABLE);

	private Context mContext;
	
	private String[] allColumns = {
			Store.CACHEDATA_ID,
			Store.CACHEDATA_SSID,
			Store.CACHEDATA_BSSID, 
			Store.CACHEDATA_WIFIPWD,
			Store.CACHEDATA_WIFISECURITY,
			Store.CACHEDATA_SUBSCRIBER,
			Store.CACHEDATA_DISTRIBUTOR,
			Store.CACHEDATA_PROFILE,
			Store.CACHEDATA_HOTSPOTNAME,
			Store.CACHEDATA_HOTSPOTPWD};

	public CacheGateway(Context argContext){
		this.mContext=argContext ;
	}
	
	public void insertCacheInfo(int id,String ssid,String bssid,String pwd,String security, String subscriber, String distributor, String profile, String hotspotname, String hotspotpwd) {

		ContentValues values = new ContentValues();
		values.put(Store.CACHEDATA_ID, id);
		values.put(Store.CACHEDATA_SSID, ssid);
		values.put(Store.CACHEDATA_BSSID, bssid);
		values.put(Store.CACHEDATA_WIFIPWD, pwd);
		values.put(Store.CACHEDATA_WIFISECURITY, security);
		values.put(Store.CACHEDATA_SUBSCRIBER, subscriber);
		values.put(Store.CACHEDATA_DISTRIBUTOR, distributor);
		values.put(Store.CACHEDATA_PROFILE, profile);
		values.put(Store.CACHEDATA_HOTSPOTNAME, hotspotname);
		values.put(Store.CACHEDATA_HOTSPOTPWD, hotspotpwd);
		mContext.getContentResolver().insert(CONTENT_URI_CACHEDATA, values) ;
	}
	
	public void updateCacheInfo(int id,String ssid,String bssid,String pwd,String security, String subscriber, String distributor, String profile, String hotspotname, String hotspotpwd) {

		ContentValues values = new ContentValues();
		values.put(Store.CACHEDATA_ID, id);
		values.put(Store.CACHEDATA_SSID, ssid);
		values.put(Store.CACHEDATA_BSSID, bssid);
		values.put(Store.CACHEDATA_WIFIPWD, pwd);
		values.put(Store.CACHEDATA_WIFISECURITY, security);
		values.put(Store.CACHEDATA_SUBSCRIBER, subscriber);
		values.put(Store.CACHEDATA_DISTRIBUTOR, distributor);
		values.put(Store.CACHEDATA_PROFILE, profile);
		values.put(Store.CACHEDATA_HOTSPOTNAME, hotspotname);
		values.put(Store.CACHEDATA_HOTSPOTPWD, hotspotpwd);
		mContext.getContentResolver().update(CONTENT_URI_CACHEDATA, values,Store.CACHEDATA_ID + " = " +id, null);
	}
	
	private CacheInfo cursorToCacheInfo(Cursor cursor) {
		CacheInfo cacheInfo = new CacheInfo();
		cacheInfo.setId(cursor.getInt(0));
		cacheInfo.setSsid(cursor.getString(1));
		cacheInfo.setBssid(cursor.getString(2));
		cacheInfo.setPwd(cursor.getString(3));
		cacheInfo.setSecurity(cursor.getString(4));
		cacheInfo.setSubscriber(cursor.getString(5));
		cacheInfo.setDistributor(cursor.getString(6));
		cacheInfo.setProfile(cursor.getString(7));
		cacheInfo.setHotspotName(cursor.getString(8));
		cacheInfo.setHotspotPwd(cursor.getString(9));
		return cacheInfo;
	}
	
	public CacheInfo getCacheInfo(int Id) {
		
		Cursor cursor=mContext.getContentResolver().query(CONTENT_URI_CACHEDATA, 
				allColumns,Store.CACHEDATA_ID + " = "+Id, null, null);
		cursor.moveToFirst();
		CacheInfo cacheInfo = null;
		if (!cursor.isAfterLast()) {
			cacheInfo = cursorToCacheInfo(cursor);
		}
		return cacheInfo;
	}
	
	public int deleteCacheInfo(int Id) {
		int row=mContext.getContentResolver().delete(CONTENT_URI_CACHEDATA, 
				Store.CACHEDATA_ID+" = "+Id ,null);
		return row;
	}
	
}
