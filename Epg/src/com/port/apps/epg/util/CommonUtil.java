package com.port.apps.epg.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.port.api.db.service.Store;
import com.port.api.db.util.CacheData;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class CommonUtil {
	
	private static String TAG = "CommonUtil";
	
//	public static SQLiteDatabase open() throws SQLException {
//		Store dbHelper = CacheData.getDbHelper();
//		if(dbHelper == null && CacheData.getActivity().getApplicationContext()!=null){
//			if(Constant.DEBUG)  Log.d(TAG , "SQLiteDatabase "+CacheData.getActivity().getApplicationContext().toString());
//			dbHelper = new Store(CacheData.getActivity().getApplicationContext());
//			CacheData.setDbHelper(dbHelper);
//		}else{
//			
//		}
//		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
//		return writableDatabase;
//	}
//
//	public void close() {
//		try{
//			Store dbHelper = CacheData.getDbHelper();
//			if(dbHelper != null){
//				dbHelper.close();
//				CacheData.setDbHelper(null);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//	    }
//	}
		
//	public static boolean checkConnectionForLocaldb() {
//		SQLiteDatabase database = CacheData.getDatabase();
//		if(database == null || !(database.isOpen())) {
//			for(int i=0; i<3; i++){
//				try {
//					database = open();
//				}catch(Exception e) {
//					e.printStackTrace();
//				}
//				CacheData.setDatabase(database);
//				if(database != null && database.isOpen()) {
//					return true;
//				}
//				if(Constant.DEBUG)  Log.d(TAG, "ConnectionForLocaldb is null");
//			}
//		}
//		if(database == null || !(database.isOpen())) {
//			return false;
//		}
//		return true;
//	}
}
