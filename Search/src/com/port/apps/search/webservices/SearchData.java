package com.port.apps.search.webservices;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

import com.port.api.util.Constant;
import com.port.api.util.SystemLog;


public class SearchData {

	//fetch search results
	public static JSONObject getJsonSearchData(String category, String keyword) {
		InputStream inputStream = null;
		JSONObject jsonData = null;
        String response = "";
        String json = "";
		
        String stringURL = Constant.TEMP_SEARCH_URL;
		
		if(Constant.DEBUG)  Log.d("SearchWS", "getJsonSearchData final url is : "+stringURL);
        
		try{
			 HttpClient httpclient = new DefaultHttpClient();
	         HttpPost httpPost = new HttpPost(stringURL);
	         JSONObject jsonObject = new JSONObject();
	         
	         jsonObject.accumulate("keyword", keyword);
	         jsonObject.accumulate("category", category);
	         jsonObject.accumulate("devicetype", "mediaplayer");
	         jsonObject.accumulate("network", "all");
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
				 Log.d("ConnectClass", "final response of getJsonSearchData is  : "+response);
				 
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
