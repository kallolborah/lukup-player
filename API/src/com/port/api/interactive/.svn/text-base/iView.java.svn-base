package com.port.api.interactive;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.FbDialog;
import com.port.Channel;
import com.port.Consumer;
import com.port.MessageQueue;
import com.port.api.R;
import com.port.api.ads.AdsServiceUrlRecords;
import com.port.api.util.CommonUtil;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class iView extends Activity {
	
	private String TAG = "iView";
	public WebView myWebView;
	private WebSettings webSettings;
	public String pageType="";
	public String jsonTime = "";
	public String jsonVResize = "";
	public String jsonUrl = "";
	public ArrayList<String> jsonTimeList = new ArrayList<String>();  
	
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		if(Constant.DEBUG)  Log.d(TAG , "onCreate()");
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	public boolean startiView(String url){
		if(Constant.DEBUG)  Log.d(TAG , "startiView()");
		myWebView = (WebView) findViewById(R.id.webview);
		myWebView.clearHistory();
		myWebView.clearFormData();
		myWebView.clearCache(true);
//		myWebView.setVisibility(View.INVISIBLE);
		myWebView.setBackgroundColor(Color.argb(1, 255, 255, 255));
		myWebView.bringToFront();		
		myWebView.setWebViewClient(new iViewClient());
		webSettings = myWebView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setJavaScriptEnabled(true);	
		myWebView.loadUrl(url);
		return true;
	}

	private class iViewClient extends WebViewClient {
		@Override
	    public boolean shouldOverrideUrlLoading(WebView view, final String url) {
			super.onLoadResource(view, url);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// check if url to load is an interactive ad or some other external url
					String decodeurl = URLDecoder.decode(url);
					if(Constant.DEBUG)  Log.d(TAG, "iViewClient() " + decodeurl);
					
					if(decodeurl.startsWith("js-frame:")){
						//url is an interactive ad so you can report stats here
						pageType = "interactive";
						AdsServiceUrlRecords record = splitURL(decodeurl);
						if(record != null){
							if(Constant.DEBUG)  Log.d(TAG,"Loading details >>>>>>>>"+record.getProtocol()+"   "+record.getMethod()+"   "+record.getCallback()+"   "+record.getResponse());
							if(record.getMethod() != null && record.getMethod().equalsIgnoreCase("play")){
								if(Constant.DEBUG)  Log.d(TAG,"Ad Url "+record.getResponse());
								try {
									JSONObject jsonObject = new JSONObject(record.getResponse().trim());
									if(jsonObject.has("time")){
										jsonTime = jsonObject.getString("time");
										if(jsonTime != null && !jsonTime.equalsIgnoreCase("")){
								    		try {
									    		JSONArray array = new JSONArray(jsonTime);
												if(array != null && array.length() >0 ){
													for(int i=0;i<array.length();i++){
														if(!array.getString(i).equalsIgnoreCase("end")){
															jsonTimeList.add(array.getString(i));
														}
													}
												}
											} catch(Exception e){
								    			e.printStackTrace();
								    			StringWriter errors = new StringWriter();
								    			e.printStackTrace(new PrintWriter(errors));
								    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
								    		}
								    	}
									}
									
									if(jsonObject.has("vresize")){
										jsonVResize = jsonObject.getString("vresize");
									}
									
									if(jsonObject.has("url")){
										jsonUrl = jsonObject.getString("url");
										playVideoData(jsonUrl);
									}
									if(Constant.DEBUG)  Log.d(TAG , "Interactive jsonTime: "+jsonTime+", jsonUrl: "+jsonUrl);
									if(Constant.DEBUG)  Log.d(TAG , "Interactive jsonVResize: "+jsonVResize);

								} catch(Exception e){
					    			e.printStackTrace();
					    			StringWriter errors = new StringWriter();
					    			e.printStackTrace(new PrintWriter(errors));
					    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
					    		}
							}
						}
						
					}else{
						//some external url
						pageType="form";
						
					}
				}
			}).start();
			return true;
		}
		
		@Override
		public void onPageFinished(final WebView view, String url) {
			super.onPageFinished(view, url);
			if(Constant.DEBUG)  Log.d(TAG, "Finished loading web view from "+url);

			try {
				new Thread(new Runnable() {
					@Override
					public void run() {
//						view.bringToFront();
//						view.setBackgroundColor(Color.argb(1, 255, 255, 255));
						if(Constant.DEBUG)  Log.d(TAG, "onPageFinished pageType: "+pageType);
						if(pageType.equalsIgnoreCase("interactive")){
							// send message to Navigator to show DPad
							String dockID = Build.ID; //is this correct ?
							final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
							returner.set("Player", "BT", "com.player.apps.Navigator");
							JSONObject resp = new JSONObject();	
							JSONObject data = new JSONObject();
							try {
								resp.put("params",data);
							} catch(Exception e){
				    			e.printStackTrace();
				    			StringWriter errors = new StringWriter();
				    			e.printStackTrace(new PrintWriter(errors));
				    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				    		}
							returner.add("com.port.api.interactive.iView.start", resp,"startActivity");
							try {
								returner.send();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else if(pageType.equalsIgnoreCase("form")){
							// send message to Navigator to show Keypad
							String dockID = Build.ID; //is this correct ?
							final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
							returner.set("Player", "BT", "com.player.apps.Keypad");
							JSONObject resp = new JSONObject();	
							JSONObject data = new JSONObject();
							try {
								resp.put("params",data);
							} catch(Exception e){
				    			e.printStackTrace();
				    			StringWriter errors = new StringWriter();
				    			e.printStackTrace(new PrintWriter(errors));
				    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
				    		}
							returner.add("com.port.api.interactive.iView.start", resp,"startActivity");
							try {
								returner.send();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}						
						}
					}
				}).start();
			} catch(Exception e){
    			e.printStackTrace();
    			StringWriter errors = new StringWriter();
    			e.printStackTrace(new PrintWriter(errors));
    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
    		}
		}
		
		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event){
			return true;
		}
		
		@Override
		public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
			try {
				new Thread(new Runnable() {
					@Override
					public void run() {
						view.setVisibility(View.VISIBLE);
						//send error message to Player

					}
				}).start();
			} catch(Exception e){
    			e.printStackTrace();
    			StringWriter errors = new StringWriter();
    			e.printStackTrace(new PrintWriter(errors));
    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
    		}
		}
		
		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale) {
			super.onScaleChanged(view, oldScale, newScale);
		}

	}
	
	/**********************************************************************/
	synchronized public AdsServiceUrlRecords splitURL(String url){
		AdsServiceUrlRecords record = null;
		if(url != null){
			try {
				if(url.indexOf(":") != -1){
					String[] split = url.split("\\::");
					if(Constant.DEBUG)  Log.d(TAG,"Split length "+split.length);
					record = new AdsServiceUrlRecords();
					record.setProtocol(split[0]);
					record.setMethod(split[1]);
					if(split.length >2){
						record.setResponse(split[2]);
					}
					if(split.length >3){
						record.setCallback(split[3]);
					}
					return record;
				}
			} catch(Exception e){
    			e.printStackTrace();
    			StringWriter errors = new StringWriter();
    			e.printStackTrace(new PrintWriter(errors));
    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
    			return record;
			}
		}
		return record;
	}
	
	public void playVideoData(String mMediaUri){
	
	}
}
