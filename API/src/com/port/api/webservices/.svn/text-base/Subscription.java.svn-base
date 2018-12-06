package com.port.api.webservices;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

//import com.port.api.service.ProfileInfo;
import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.CacheData;
import com.port.api.util.SyslogException;
import com.port.api.util.SystemLog;

import android.util.Log;


public class Subscription {

	private static String TAG = "Subscription";

	public static JSONObject registerSubscriberId(String subscriberId,String distibutorID,String distributorPwd, String mProductID,String mProductType, String playerapp, String playerfirmware, String portapp, String portfirmware) {

		String stringURL = Constant.REGISTER_SUBSCRIBER_ID;
		if(subscriberId != null && !subscriberId.equalsIgnoreCase("") ){
			stringURL += subscriberId;
		}
		if(distibutorID!= null && !distibutorID.equalsIgnoreCase("")){
			stringURL += "&distributorid="+distibutorID;
		}
		if(distributorPwd!=null && !distributorPwd.equalsIgnoreCase("")){
			stringURL += "&distributorpwd="+distributorPwd;			
		}
		if(mProductID!= null && !mProductID.equalsIgnoreCase("")){
			stringURL += "&productid="+mProductID;
		}
		if(mProductType!= null && !mProductType.equalsIgnoreCase("")){
			stringURL += "&producttype="+mProductType;
		}
		if(playerapp!= null && !playerapp.equalsIgnoreCase("")){
			stringURL += "&playerapp="+playerapp;
		}
		if(playerfirmware!= null && !playerfirmware.equalsIgnoreCase("")){
			stringURL += "&playerfirmware="+playerfirmware;
		}
		if(portapp!= null && !portapp.equalsIgnoreCase("")){
			stringURL += "&portapp="+portapp;
		}
		if(portfirmware!= null && !portfirmware.equalsIgnoreCase("")){
			stringURL += "&portfirmware="+portfirmware;
		}

		if(Constant.DEBUG)  Log.d(TAG,"Final url to register the subscriber id is : "+stringURL);

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
	
	
	public static JSONObject subscribeForService(String subscriberid, String mProfileName, String chid, String type, String pricingmodel, String password) {
		String stringURL = Constant.SUBSCRIBE_UNSUBSCRIBE_SERVICE;
		if(chid != null && !(chid.trim().equals(""))) {
			stringURL += "?chid="+chid;
		}

		if(type != null && !(type.trim().equals(""))) {
			stringURL += "&type="+type;
		}

//		String subscriberid = CacheData.getSubscriberId();
		if(subscriberid!= null && !subscriberid.equalsIgnoreCase("")) {
			stringURL += "&subscriberid="+subscriberid;
		}

		if(pricingmodel != null && !(pricingmodel.trim().equals(""))) {
			stringURL += "&pricingmodel="+pricingmodel;
		}

		if(password != null && !(password.trim().equals(""))) {
			stringURL += "&adminPassword="+password;
		}

//		String mProfileName = "Guest";
//		ProfileInfo selectedProfileInfo = CacheData.getSelectedProfileInfo();
//		if(selectedProfileInfo != null){
//			mProfileName = selectedProfileInfo.getUserName();
//		}
		stringURL += "&profile="+mProfileName;

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
	
	
	public static JSONObject Accountinfo(String subscriberId) {
		String stringURL = Constant.ACCOUNT_DATA;
		if(subscriberId != null && !(subscriberId.trim().equals(""))) {
			stringURL += subscriberId;
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
	
	//function for getting distributor id, added by Kallol Borah 24 May 2015
	public static JSONObject getDistributorId(String subscriberId) {
		String stringURL = Constant.DISTRIBUTOR_DATA;
		if(subscriberId != null && !(subscriberId.trim().equals(""))) {
			stringURL += subscriberId;
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
	
	public static JSONObject freeSubscription(String stringURL) {

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
	
	public static JSONObject SubscribedData(String subscriberId) {
		String stringURL = Constant.SUBSCRIBED_DATA;
		if(subscriberId != null && !(subscriberId.trim().equals(""))) {
			stringURL += subscriberId;
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
	
}
