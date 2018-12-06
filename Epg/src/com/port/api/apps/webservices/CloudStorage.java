package com.port.api.apps.webservices;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.exoplayer.upstream.cache.Cache;
import com.port.Port;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.service.ProfileGateway;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.util.CacheData;
import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.apps.epg.Home;
import com.port.util.ApplicationConstant;
import com.port.util.XmlUtil;

public class CloudStorage {	
	
	private static String TAG = "RecordAPI";
	
	public static JSONObject DeleteRecording(String objId, String eventId){
		String stringURL = Constant.DELETE_RECORD;
		
		int userId=CacheData.getUserId();
		String subscriberid = CacheData.getSubscriberId();	
		if(subscriberid.equalsIgnoreCase("") || userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				subscriberid = info.getSubscriber();
				CacheData.setSubscriberId(subscriberid);
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}
		
		if(subscriberid != null && !(subscriberid.trim().equals(""))) {
			stringURL += "method=delete&subscriberid=" + subscriberid + "&objectid=" + objId + "&eventid" + eventId;
		}
		
		if (Constant.DEBUG) Log.d(TAG , "Final url : "+ stringURL);
		JSONObject jsonObject = null;
		String response = "";

		DataInputStream dataIn = null;
		BufferedReader br = null;
		if (CommonUtil.isNetworkAvailable()) {
			try {
				URL url = new URL(stringURL.trim());
				URLConnection connection = url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				// get the response from the server and store it in result

				try {
					dataIn = new DataInputStream(connection.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
				}
				if (dataIn != null) {
					br = new BufferedReader(new InputStreamReader(dataIn));
					String inputLine;
					while ((inputLine = br.readLine()) != null) {
						response += inputLine;
					}

					if (Constant.DEBUG) Log.d(TAG, "RECORDING RESPONSE(Delete) : " + response);

					if (response != null && !(response.trim().equals(""))) {
						jsonObject = new JSONObject(response);
					}
					br.close();
					dataIn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
				try {
					if (br != null) { br.close();}
					if (dataIn != null) { dataIn.close();}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return jsonObject;
	}
	
	
	public static JSONObject GetRecords(){
		
		String stringURL = Constant.GET_RECORDS;
		
		int userId=CacheData.getUserId();
		String subscriberid = CacheData.getSubscriberId();	
		if(subscriberid.equalsIgnoreCase("") || userId==0){
			CacheGateway cache  = new CacheGateway(Port.c);
			CacheInfo info = cache.getCacheInfo(1000);
			if (info != null) {
				subscriberid = info.getSubscriber();
				CacheData.setSubscriberId(subscriberid);
				userId = Integer.valueOf(info.getProfile());
				CacheData.setUserId(userId);
			}
		}
		
		if(subscriberid != null && !(subscriberid.trim().equals(""))) {
			stringURL += "method=retrieve&subscriberid="+subscriberid;
		}
		
		if (Constant.DEBUG) Log.d(TAG , "Final url : "+ stringURL);
		JSONObject jsonObject = null;
		String response = "";

		DataInputStream dataIn = null;
		BufferedReader br = null;
		if (CommonUtil.isNetworkAvailable()) {
			try {
				URL url = new URL(stringURL.trim());
				URLConnection connection = url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				try {
					dataIn = new DataInputStream(connection.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
				}
				if (dataIn != null) {
					br = new BufferedReader(new InputStreamReader(dataIn));
					String inputLine;
					while ((inputLine = br.readLine()) != null) {
						response += inputLine;
					}

					if (Constant.DEBUG) Log.d(TAG, "RECORDING RESPONSE(Data) : " + response);
					if (response != null && !(response.trim().equals(""))) {
						jsonObject = new JSONObject(response);
					}
					br.close();
					dataIn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
				try {
					if (br != null) { br.close();}
					if (dataIn != null) { dataIn.close();}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return jsonObject;
	}
	
	public static JSONObject ScheduleRecording(String eventId, String serviceId, String eventName, String eventTime, String endTime, String subscriberid, String userID){
		
		String stringURL = Constant.SCHEDULE_RECORD + "eventid=" + eventId + "&serviceid=" + serviceId + "&eventname=" + eventName + "&starttime=" + eventTime + "&endtime=" + endTime + "&subscriberid=" + subscriberid + "&userid=" + userID; 
		if (Constant.DEBUG) Log.d(TAG , "Final url : "+ stringURL);
		JSONObject jsonObject = null;
		String response = "";

		DataInputStream dataIn = null;
		BufferedReader br = null;
		if (CommonUtil.isNetworkAvailable()) {
			try {
				URL url = new URL(stringURL.trim());
				URLConnection connection = url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				// get the response from the server and store it in result

				try {
					dataIn = new DataInputStream(connection.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
				}
				if (dataIn != null) {
					br = new BufferedReader(new InputStreamReader(dataIn));
					String inputLine;
					while ((inputLine = br.readLine()) != null) {
						response += inputLine;
					}

					if (Constant.DEBUG) Log.d(TAG, "RECORDING RESPONSE(Info) : " + response);

					if (response != null && !(response.trim().equals(""))) {
						jsonObject = new JSONObject(response);
					}
					br.close();
					dataIn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
				try {
					if (br != null) { br.close();}
					if (dataIn != null) { dataIn.close();}
				} catch (IOException e1) {
					e1.printStackTrace();
			   }
			}
		}
		return jsonObject;
	}
	
	public static JSONObject cancelRecording(String serviceId, String eventName, String eventTime, String endTime, String subscriberid, String eventid){
		
		String stringURL = Constant.SCHEDULE_RECORD + "method=remove&serviceid=" + serviceId + "&eventname=" + eventName + "&starttime=" + eventTime + "&endtime=" + endTime + "&subscriberid=" + subscriberid;// + "&eventid=" + eventid; 
		if (Constant.DEBUG) Log.d(TAG , "Final url : "+ stringURL);
		JSONObject jsonObject = null;
		String response = "";

		DataInputStream dataIn = null;
		BufferedReader br = null;
		if (CommonUtil.isNetworkAvailable()) {
			try {
				URL url = new URL(stringURL.trim());
				URLConnection connection = url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				// get the response from the server and store it in result

				try {
					dataIn = new DataInputStream(connection.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
				}
				if (dataIn != null) {
					br = new BufferedReader(new InputStreamReader(dataIn));
					String inputLine;
					while ((inputLine = br.readLine()) != null) {
						response += inputLine;
					}

					if (Constant.DEBUG) Log.d(TAG, "RECORDING RESPONSE(Info) : " + response);

					if (response != null && !(response.trim().equals(""))) {
						jsonObject = new JSONObject(response);
					}
					br.close();
					dataIn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_TRANSCODER, errors.toString(), e.getMessage());
				try {
					if (br != null) { br.close();}
					if (dataIn != null) { dataIn.close();}
				} catch (IOException e1) {
					e1.printStackTrace();
			   }
			}
		}
		return jsonObject;
	}
}
