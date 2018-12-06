package com.port.api.apps.webservices;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.util.Log;

import com.port.Port;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.util.CacheData;
import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.apps.epg.Home;

public class TVGuide {

	private static String TAG = "TVGuide";
	
	//returns bouquets and services for VOD
	public static JSONObject getExternalBouquetAndServiceJson() {
		
		String distributorid = CacheData.getDistributorId();
		if(distributorid!=null){
			if(distributorid.equalsIgnoreCase("")){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					distributorid = info.getDistributor();
					CacheData.setDistributorId(distributorid);;
				}
			}
		}
		if (Constant.DEBUG)	Log.d(TAG, "Distributor id  : " + distributorid);
		if(distributorid !=null && !distributorid.equalsIgnoreCase("")){
			String stringURL = Constant.EXTERNAL_BOUQUET_SERVICE_URL;
			stringURL += distributorid;
			JSONObject jsonObject = null;
			String response = "";
			DataInputStream dataIn = null;
			BufferedReader br = null;
			if (CommonUtil.isNetworkAvailable()) {
				try {
					if (Constant.DEBUG)
						Log.d(TAG, "Bouquet url  : " + stringURL);
					URL url = new URL(stringURL.trim());
					URLConnection connection = url.openConnection();
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setUseCaches(false);
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
	
					// get the response from the server and store it in result
					dataIn = new DataInputStream(connection.getInputStream());
	
					if (dataIn != null) {
						br = new BufferedReader(new InputStreamReader(dataIn));
						String inputLine;
						while ((inputLine = br.readLine()) != null) {
							response += inputLine;
						}
	
						if (Constant.DEBUG)
							Log.d(TAG, "final response is  : " + response);
	
						if (response != null && !(response.trim().equals(""))) {
							jsonObject = new JSONObject(response);
						}
						br.close();
						dataIn.close();
					}
					return jsonObject;
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
	
					try {
						if (br != null) {
							br.close();
						}
						if (dataIn != null) {
							dataIn.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
			return jsonObject;
		}
		return null;
	}
	
	//Fetch programs for channel
	public static JSONObject getExternalEventJson(int serviceid) {
		String stringURL = Constant.EXTERNAL_EVENT_URL;

		String distributorid = CacheData.getDistributorId();
		if(distributorid!=null){
			if(distributorid.equalsIgnoreCase("")){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					distributorid = info.getDistributor();
					CacheData.setDistributorId(distributorid);;
				}
			}
		}
		if (Constant.DEBUG)	Log.d(TAG, "Distributor id  : " + distributorid);
		if(distributorid !=null && !distributorid.equalsIgnoreCase("")){
			if (serviceid >-1) {
				stringURL += "serviceid=" + serviceid;
				stringURL += "&distributorid=" + distributorid;
			} else {
				stringURL += "serviceid=";
				stringURL += "&distributorid=";
			}
	
			if (Constant.DEBUG)
				Log.d(TAG, "Final url to get the external event from sms is : "
						+ stringURL);
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
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
					}
					if (dataIn != null) {
						br = new BufferedReader(new InputStreamReader(dataIn));
						String inputLine;
						while ((inputLine = br.readLine()) != null) {
							response += inputLine;
						}
						if (Constant.DEBUG)
							Log.d(TAG, "EVENT data is  : " + response);
	
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
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
					try {
						if (br != null) {
							br.close();
						}
						if (dataIn != null) {
							dataIn.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						StringWriter errors1 = new StringWriter();
						e1.printStackTrace(new PrintWriter(errors1));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors1.toString(), e1.getMessage());
					}
				}
			}
			return jsonObject;
		}
		return null;
	}
	
	//Fetch Live TV EPG
	public static JSONObject getLiveEPGJsonData() {
		
		String operator = "";
		
		if(!Constant.DVB){
			operator = "lukup";
		}
		if (CacheData.getOperaterName() != null && !CacheData.getOperaterName().equalsIgnoreCase("")) {
			operator = CacheData.getOperaterName();
		}

		String distributorid = CacheData.getDistributorId();
		if(distributorid!=null){
			if(distributorid.equalsIgnoreCase("")){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					distributorid = info.getDistributor();
					CacheData.setDistributorId(distributorid);;
				}
			}
		}
		if (Constant.DEBUG)	Log.d(TAG, "Distributor id  : " + distributorid);
		if(distributorid !=null && !distributorid.equalsIgnoreCase("")){
		
			String stringURL = Constant.LIVE_URL + operator.toLowerCase();
			stringURL += "&devicetype=tv";
			stringURL += "&distributorid=" + distributorid;
			if (!stringURL.equalsIgnoreCase("") && stringURL != null) {
				stringURL = stringURL.trim().replaceAll(" ", "%20");
			}
		
			DataInputStream dataIn = null;
			BufferedReader br = null;
			JSONObject jsonObject = null;
			String response = "";
			if (Constant.DEBUG)
				Log.d(TAG, "Final url to get " + operator + " live data is : " + stringURL);
			if (CommonUtil.isNetworkAvailable()) {
				if (operator != null && !operator.equalsIgnoreCase("")) {
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
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
							dataIn = null;
						}
						if (Constant.DEBUG)
							Log.d(TAG, "Data fetched from Live TV SMS is  : " + dataIn);
	
						if (dataIn != null) {
							br = new BufferedReader(new InputStreamReader(dataIn));
							String inputLine;
							while ((inputLine = br.readLine()) != null) {
								response += inputLine;
							}
	
							if (Constant.DEBUG)
								Log.d(TAG, "Live TV EPG data is  : " + response);
	
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
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
						try {
							if (br != null) {
								br.close();
							}
							if (dataIn != null) {
								dataIn.close();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
							StringWriter errors1 = new StringWriter();
							e1.printStackTrace(new PrintWriter(errors1));
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors1.toString(), e1.getMessage());
						}
					}
				}
			}
			return jsonObject;
		}
		return null;
	}
	
	//Fetch featured titles and packages
	public static JSONObject getFeaturedDetails() {
		String stringURL = Constant.FEATURED_DATA;
		
		String distributorid = CacheData.getDistributorId();
		if(distributorid!=null){
			if(distributorid.equalsIgnoreCase("")){
				CacheGateway cache  = new CacheGateway(Port.c);
				CacheInfo info = cache.getCacheInfo(1000);
				if (info != null) {
					distributorid = info.getDistributor();
					CacheData.setDistributorId(distributorid);;
				}
			}
		}
		if (Constant.DEBUG)	Log.d(TAG, "Distributor id  : " + distributorid);
		if(distributorid !=null && !distributorid.equalsIgnoreCase("")){
			stringURL += "&distributorid=" + distributorid;
	
			if (Constant.DEBUG)
				Log.d(TAG, "Final url to get the featured data is : " + stringURL);
	
			DataInputStream dataIn = null;
			BufferedReader br = null;
			JSONObject jsonObject = null;
			String response = "";
	
			if (CommonUtil.isNetworkAvailable()) {
				try {
					URL url = new URL(stringURL.trim());
					URLConnection connection = url.openConnection();
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setUseCaches(false);
					connection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
	
					// get the response from the server and store it in result
					try {
						dataIn = new DataInputStream(connection.getInputStream());
					} catch (Exception e) {
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
					}
	
					if (dataIn != null) {
						br = new BufferedReader(new InputStreamReader(dataIn));
						String inputLine;
						while ((inputLine = br.readLine()) != null) {
							response += inputLine;
						}
	
						if (Constant.DEBUG)
							Log.d(TAG,
									"FINAL RESPONSE TO GET THE FEATURED LIST IS  : "
											+ response);
	
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
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
					try {
						if (br != null) {
							br.close();
						}
						if (dataIn != null) {
							dataIn.close();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						StringWriter errors1 = new StringWriter();
						e1.printStackTrace(new PrintWriter(errors1));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors1.toString(), e1.getMessage());
					}
				}
			}
			return jsonObject;
		}
		return null;
	}

	//fetch recommendations
	public static JSONObject getRecommendedData() throws Exception {
		InputStream inputStream = null;
		JSONObject jsonData = null;
        String response = "";
        String json = "";
		String stringURL = Constant.SEARCH_RECOMMENDED_DATA;
		if (Constant.DEBUG) Log.d(TAG, "Final url to get the Recommended data is : "+ stringURL);
		
		try{
			 HttpClient httpclient = new DefaultHttpClient();
	         HttpPost httpPost = new HttpPost(stringURL);
	         JSONObject jsonObject = new JSONObject();
	
	         jsonObject.accumulate("device", "all");
	         jsonObject.accumulate("category", "all");
	         jsonObject.accumulate("genre", "all");
	         jsonObject.accumulate("language", "all");
	         jsonObject.accumulate("pricingmodel", "all");
	         jsonObject.accumulate("limit", 20);
			
	         json = jsonObject.toString();
	         StringEntity se = new StringEntity(json);
	         httpPost.setEntity(se);
	
	         httpPost.setHeader("Accept", "application/json");
	         httpPost.setHeader("Content-type", "application/json");
	         
	         HttpResponse httpResponse = httpclient.execute(httpPost);
	         inputStream = httpResponse.getEntity().getContent();
	
	         if (inputStream != null){
	        	 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	             String inputLine;
				 while ((inputLine = bufferedReader.readLine()) != null) {
					response += inputLine;
				 }
				 if(Constant.DEBUG)  Log.d("SearchWS", "final response of getRecommendedData is  : "+response);
				 
	             bufferedReader.close();
	         }
	         inputStream.close();
	         if (response != null && !(response.trim().equals(""))) {
	        	 jsonData = new JSONObject(response);
			 }
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}
         
		return jsonData;
	}

}
