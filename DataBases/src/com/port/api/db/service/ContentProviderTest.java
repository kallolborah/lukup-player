package com.port.api.db.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ContentProviderTest extends ContentProvider{
	// database
	private Store database;
	private static final String AUTHORITY = "com.port.apps.epg.contentprovider" ;
	
	public static final Uri CONTENT_URI_BOUQUET = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.BOUQUET_TABLE);
	public static final Uri CONTENT_URI_CHANNEL = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.CHANNEL_TABLE);
	public static final Uri CONTENT_URI_PROGRAM = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.PROGRAM_TABLE);
	public static final Uri CONTENT_URI_PROFILE = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.PROFILE_TABLE);
	public static final Uri CONTENT_URI_STATUS = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.STATUS_TABLE);
	public static final Uri CONTENT_URI_CACHE = Uri.parse("content://" + AUTHORITY
			+ "/" + Store.CACHEDATA_TABLE);
	
	SQLiteDatabase db;
	@Override
	public boolean onCreate() {
		Log.i("ContentProviderTest", "OnCreate: ") ;
		database = new Store(getContext());
		Log.i("ContentProviderTest", "database: "+database) ;
		db = database.getWritableDatabase();
		return (db == null)? false:true;
//		return false;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
//		SQLiteDatabase db = database.getWritableDatabase();
		//		insert (String table, String nullColumnHack, ContentValues values)
		if(uri.toString().equalsIgnoreCase(CONTENT_URI_BOUQUET.toString())){
			//Handle TAble T1
			db.insert(Store.BOUQUET_TABLE,null,values) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_CHANNEL.toString())){
			//Handle TAble T2
			db.insert(Store.CHANNEL_TABLE,null,values) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_PROGRAM.toString())){
			//Handle TAble T3
			db.insert(Store.PROGRAM_TABLE,null,values) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_PROFILE.toString())){
			//Handle TAble T4
			db.insert(Store.PROFILE_TABLE,null,values) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_STATUS.toString())){
			//Handle TAble T4
			db.insert(Store.STATUS_TABLE,null,values) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_CACHE.toString())){
			//Handle TAble T4
			db.insert(Store.CACHEDATA_TABLE,null,values) ;
		}
		return null;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = null;
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		try{
			db = database.getWritableDatabase();
		}catch(Exception e){
			Log.i("ContentProviderTest", "query(): "+e.getMessage()) ;
		}
		Cursor cursor = null ;

		if(uri.toString().equalsIgnoreCase(CONTENT_URI_BOUQUET.toString())){
			//Handle TAble T1
			queryBuilder.setTables(Store.BOUQUET_TABLE);
			cursor=queryBuilder.query(db,projection,selection,selectionArgs,null, null, sortOrder) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_CHANNEL.toString())){
			//Handle TAble T2
			queryBuilder.setTables(Store.CHANNEL_TABLE);
			cursor=queryBuilder.query(db,projection,selection,selectionArgs,null, null, sortOrder) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_PROGRAM.toString())){
			//Handle TAble T3
			queryBuilder.setTables(Store.PROGRAM_TABLE);
			cursor=queryBuilder.query(db,projection,selection,selectionArgs,null, null, sortOrder) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_PROFILE.toString())){
			//Handle TAble T4
			queryBuilder.setTables(Store.PROFILE_TABLE);
			cursor=queryBuilder.query(db,projection,selection,selectionArgs,null, null, sortOrder) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_STATUS.toString())){
			//Handle TAble T5
			queryBuilder.setTables(Store.STATUS_TABLE);
			cursor=queryBuilder.query(db,projection,selection,selectionArgs,null, null, sortOrder) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_CACHE.toString())){
			//Handle TAble T4
			queryBuilder.setTables(Store.CACHEDATA_TABLE);
			cursor=queryBuilder.query(db,projection,selection,selectionArgs,null, null, sortOrder) ;
		}
		Log.i("ContentProviderTest", "query db: "+db) ;

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String whereClause,String[] whereArgs) {
		SQLiteDatabase db = database.getWritableDatabase();

		//		update(String table, ContentValues values, String whereClause, String[] whereArgs)

		if(uri.toString().equalsIgnoreCase(CONTENT_URI_BOUQUET.toString())){
			//Handle TAble T1
			db.update(Store.BOUQUET_TABLE, values, whereClause, whereArgs) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_CHANNEL.toString())){
			//Handle TAble T2
			db.update(Store.CHANNEL_TABLE, values, whereClause, whereArgs) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_PROGRAM.toString())){
			//Handle TAble T3
			db.update(Store.PROGRAM_TABLE, values, whereClause, whereArgs) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_PROFILE.toString())){
			//Handle TAble T4
			db.update(Store.PROFILE_TABLE, values, whereClause, whereArgs) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_STATUS.toString())){
			//Handle TAble T5
			db.update(Store.STATUS_TABLE, values, whereClause, whereArgs) ;
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_CACHE.toString())){
			//Handle TAble T4
			db.update(Store.CACHEDATA_TABLE, values, whereClause, whereArgs) ;
		}

		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		db = database.getWritableDatabase();
		int row = 0 ;
		if(uri.toString().equalsIgnoreCase(CONTENT_URI_BOUQUET.toString())){
			//Handle TAble T1
			row=db.delete(Store.BOUQUET_TABLE, selection, selectionArgs);
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_CHANNEL.toString())){
			//Handle TAble T2
			row=db.delete(Store.CHANNEL_TABLE, selection, selectionArgs);
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_PROGRAM.toString())){
			//Handle TAble T3
			row=db.delete(Store.PROGRAM_TABLE, selection, selectionArgs);
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_PROFILE.toString())){
			//Handle TAble T4
			row=db.delete(Store.PROFILE_TABLE, selection, selectionArgs);
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_STATUS.toString())){
			//Handle TAble T4
			row=db.delete(Store.STATUS_TABLE, selection, selectionArgs);
		}
		else if(uri.toString().equalsIgnoreCase(CONTENT_URI_CACHE.toString())){
			//Handle TAble T4
			row=db.delete(Store.CACHEDATA_TABLE, selection, selectionArgs);
		}
		Log.i("ContentProviderTest", "delete db row: "+row) ;
		return row ;
	}
	

	@Override
	public String getType(Uri uri) {
		return null;
	}
}
