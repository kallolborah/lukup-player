package com.port.api.apps.webservices;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.util.Log;

import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class RSS {
	private static String TAG = "RSS";

	public static JSONObject getEventJsonForRSS(String service, String rssUrl, String language) {
		String stringURL = Constant.EXTERNAL_EVENT_URL_RSS;
		if(service != null && !(service.trim().equals(""))) {
			stringURL += "service="+service;
		}
		if(rssUrl != null && !(rssUrl.trim().equals(""))) {
			if(rssUrl.contains(" ")){
				rssUrl = rssUrl.replaceAll("\\s", "%20");
			}
			stringURL += "&type="+rssUrl;
		}
		if(language != null && !(language.trim().equals(""))) {
			stringURL += "&language="+language;
		}

		if(Constant.DEBUG)  Log.d(TAG ,"Final url to get the external event is : "+stringURL);
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
