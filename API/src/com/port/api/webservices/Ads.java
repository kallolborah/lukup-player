package com.port.api.webservices;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.util.Log;

import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SyslogException;
import com.port.api.util.SystemLog;

public class Ads {
	private static JSONObject jObj = null;
	private static String json = null;
	private static String TAG;
	
//	public static JSONObject getJSONData(String url) {
//		InputStream is = null;
//		try {
//			String userAgent = System.getProperty("http.agent");
//			DefaultHttpClient httpClient = new DefaultHttpClient();
//			httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
//					userAgent);
//			HttpPost httpPost = new HttpPost(url);
//
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			HttpEntity httpEntity = httpResponse.getEntity();
//			is = httpEntity.getContent();
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					is, "iso-8859-1"), 8);
//			StringBuilder sb = new StringBuilder();
//			String line = null;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//			is.close();
//			json = sb.toString();
//			jObj = new JSONObject(json);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//
//		} catch (JSONException e) {
//			// Log.e("JSON Parser", "Error parsing data " + e.toString());
//		}
//		return jObj;
//	}
	
	public static JSONObject getJSONData(String stringURL) {

		JSONObject jsonObject = null;
		
		String response = "";
		if(CommonUtil.isNetworkAvailable()){
			try {
				if(Constant.DEBUG)  Log.d(TAG,"Final url to get the Ads data is : "+stringURL);
				URL url = new URL(stringURL);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setConnectTimeout(30 * 1000);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	
				// get the response from the server and store it in result
				DataInputStream dataIn = null;
				try {
					dataIn = new DataInputStream(connection.getInputStream());
				} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
					if(dataIn != null){
						dataIn.close();
						dataIn = null;
					}
				}
				if(dataIn != null){
					BufferedReader br = new BufferedReader(new InputStreamReader(dataIn));
					String inputLine;
					while ((inputLine = br.readLine()) != null) {
						response += inputLine;
					}
					if(Constant.DEBUG)  Log.d(TAG,"final response get Ads data  : "+response);
	
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
