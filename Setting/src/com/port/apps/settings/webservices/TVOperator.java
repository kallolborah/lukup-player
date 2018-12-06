package com.port.apps.settings.webservices;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;


public class TVOperator{
	
	private static int count = 0;	
	
	public static JSONArray getLiveTvOperatorsList() {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			String data = getServerResponse(Constant.GET_OPERATORS);
			if(!data.equalsIgnoreCase("") && data != null){
				JSONObject responceObject = new JSONObject(data);
				JSONArray operators = responceObject.getJSONArray("OperatorList");
				int id = 100;
				if(operators != null && operators.length() >0){
					for(int i=0;i<operators.length();i++){
						String operatorName = (String) operators.get(i);
						if(!operatorName.equalsIgnoreCase("") && operatorName != null){
							jsonObject = new JSONObject();
							jsonObject.put("id", id+i+"");
							jsonObject.put("name",operatorName);
							jsonArray.put(jsonObject);
						}
					}
				}
			}else{
				jsonArray.put(jsonObject);
			}

		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}
		return jsonArray;
	}
	
	
	public static String getServerResponse(String stringURL) {
		String response = "";
		
		if(CommonUtil.isNetworkAvailable()){
			try {
				URL url = new URL(stringURL);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	
				// get the response from the server and store it in result
				DataInputStream dataIn = new DataInputStream(connection.getInputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(dataIn));
				String inputLine;
				while ((inputLine = br.readLine()) != null) {
					response += inputLine;
				}
	
				if(Constant.DEBUG)  Log.d("SettingsWS","final response is  : "+response);
				br.close();
				dataIn.close();
	
			} catch (Exception e) {
				e.printStackTrace();
				count++;
				if(count <3){
					response = getServerResponse(stringURL);
				}else{
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
				}
			} 
		}
		count = 0;
		return response;
	}
	
}
