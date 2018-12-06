package com.port.apps.settings.webservices;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.util.Log;

import com.port.api.db.util.CacheData;
import com.port.api.db.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SyslogException;
import com.port.api.util.SystemLog;


public class Subscriber {
	private static String TAG = "Subscriber";

	public static JSONObject getUserProfiles() {
		String stringURL = Constant.GET_USERS_BY_SUBSCRIBER_ID;
		String subscriberId = CacheData.getSubscriberId();
		if(Constant.DEBUG)  Log.d(TAG, "subscriberId. "+subscriberId);
		if(!subscriberId.equalsIgnoreCase("") && subscriberId != null){
			stringURL += subscriberId;
		}

		if(Constant.DEBUG)  Log.d(TAG ,"Final url to get the user profiles is : "+stringURL);

		JSONObject jsonObject = null;
		String response = "";
		
		if(CommonUtil.isNetworkAvailable()){
			try {
	
				URL url = new URL(stringURL);
				URLConnection connection = url.openConnection();
	
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	
				// get the response from the server and store it in result
				DataInputStream dataIn = null;
				try {
					dataIn = new DataInputStream(connection.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
				} 
	
				if(dataIn != null){
					BufferedReader br = new BufferedReader(new InputStreamReader(dataIn));
					String inputLine;
					while ((inputLine = br.readLine()) != null) {
						response += inputLine;
					}
	
					if(Constant.DEBUG)  Log.d(TAG,"final response to get the refresh user list is  : "+response);
	
					if(response != null && !(response.trim().equals(""))) {
						jsonObject = new JSONObject(response);
					}
					br.close();
					dataIn.close();
				}
	
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
			}
		}
		return jsonObject;
	}
	
	public static JSONObject registerNewProfileName(String name, String type) {

		String stringURL = Constant.REGISTER_OR_UPDATE_USER;

		if(name != null && !(name.trim().equals(""))) {
			name = name.replaceAll(" ", "%20");
			stringURL += "name="+name;
		}

		if(type != null && !(type.trim().equals(""))) {
			stringURL += "&method="+type;
		}

		String subscriberid = CacheData.getSubscriberId();
		if(!subscriberid.equalsIgnoreCase("") && subscriberid != null){
			stringURL += "&subscriberid="+subscriberid;
		}

		if(Constant.DEBUG)  Log.d(TAG,"final url is : "+stringURL);

		JSONObject jsonObject = null;
		String response = "";
		if(CommonUtil.isNetworkAvailable()){
			try {
				URL url = new URL(stringURL);
				URLConnection connection = url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	
				// get the response from the server and store it in result
				DataInputStream dataIn = new DataInputStream(connection.getInputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(dataIn));
				String inputLine;
				while ((inputLine = br.readLine()) != null) {
					response += inputLine;
				}
	
				if(Constant.DEBUG)  Log.d(TAG,"final response is  : "+response);
	
				if(response != null && !(response.trim().equals(""))) {
					jsonObject = new JSONObject(response);
				}
				br.close();
				dataIn.close();
	
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
			} 
		}
		return jsonObject;
	}
	
	
	public static JSONObject deleteProfileFromSMS(String id) {

		String stringURL = Constant.DELETE_USER_BY_SUBSCRIBER_ID;

		if(id != null && !id.equalsIgnoreCase("")){
			stringURL += id;
		}

		String subscriberId = CacheData.getSubscriberId();
		if(subscriberId != null && !subscriberId.equalsIgnoreCase("")){
			stringURL += "&subscriberid="+subscriberId;
		}

		if(Constant.DEBUG)  Log.d(TAG,"Final url to get the user profiles is : "+stringURL);

		JSONObject jsonObject = null;
		String response = "";
		if(CommonUtil.isNetworkAvailable()){
			try {
	
				URL url = new URL(stringURL);
				URLConnection connection = url.openConnection();
	
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	
				// get the response from the server and store it in result
				DataInputStream dataIn = null;
				try {
					dataIn = new DataInputStream(connection.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
				} 
	
				if(dataIn != null){
					BufferedReader br = new BufferedReader(new InputStreamReader(dataIn));
					String inputLine;
					while ((inputLine = br.readLine()) != null) {
						response += inputLine;
					}
	
					if(Constant.DEBUG)  Log.d(TAG,"final response is  : "+response);
	
					if(response != null && !(response.trim().equals(""))) {
						jsonObject = new JSONObject(response);
					}
					br.close();
					dataIn.close();
				}
	
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
			} 
		}

		return jsonObject;
	}
	
}
