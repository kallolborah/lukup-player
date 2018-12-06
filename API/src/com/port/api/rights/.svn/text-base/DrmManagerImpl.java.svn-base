package com.port.api.rights;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import com.port.api.util.CacheData;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.drm.DrmErrorEvent;
import android.drm.DrmEvent;
import android.drm.DrmInfoEvent;
import android.drm.DrmInfoRequest;
import android.drm.DrmManagerClient;
import android.drm.DrmStore;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DrmManagerImpl extends AsyncTask<String, String, String> implements DrmManagerClient.OnEventListener,DrmManagerClient.OnInfoListener,DrmManagerClient.OnErrorListener{
	private static final String TAG = "DrmManagerImpl";
	private String url_link;
	private DrmManagerClient drmManager = null;
	private String assetUri = "widevine://pmweb.widevine.net/content/wvm/sintel_base_360p_3br_tp_short.wvm";

	
	@Override
	protected String doInBackground(String... params) {
		// Create a drm info object
		startDrmRegister();
		return assetUri;
	}
	private void startDrmRegister() {
		try{
			if(drmManager != null){
				if(Constant.DEBUG)  Log.d(TAG, "Drm Manager Set Listener called");
				drmManager.setOnErrorListener(this);
				drmManager.setOnEventListener(this);
				drmManager.setOnInfoListener(this);
				DrmInfoRequest drmRequest = new DrmInfoRequest(
						DrmInfoRequest.TYPE_RIGHTS_ACQUISITION_INFO,"video/wvm");
				// Setup drm info object
				drmRequest.put("WVDRMServerKey","https://staging.shibboleth.tv/widevine/cypherpc/cgi-bin/GetEMMs.cgi");
				drmRequest.put("WVAssetURIKey", assetUri);
				drmRequest.put("WVDeviceIDKey", "device1");
				drmRequest.put("WVPortalKey", "OEM");
				// Request license
				drmManager.acquireRights(drmRequest);
	
			}else{
				if(Constant.DEBUG)  Log.d(TAG, "Drm Manager Null");
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_DRM, errors.toString(), e.getMessage());
	    }
	}
	protected void onPostExecute(String obj) {
		if(Constant.DEBUG)  Log.d(TAG, "Inside onPostExecute");
		super.onPostExecute(obj);
		try {
//			if(DataStorage.getVideoView() != null){
//				DataStorage.getVideoView().setVisibility(View.VISIBLE);
//				DataStorage.getVideoView().setVideoPath(assetUri);
//				DataStorage.getVideoView().start();
//			}
			// View DRM License Information

			if(drmManager != null){
				ContentValues values = drmManager.getConstraints(assetUri,DrmStore.Action.PLAY);
				if(values != null){
					Set<String> keys = values.keySet();
					StringBuilder builder = new StringBuilder();
					for(String key: keys){
						builder.append(key);
						builder.append(" = ");
						builder.append(values.get(key));
						builder.append("\n");
					}
					if(Constant.DEBUG)  Log.d(TAG,"DRM License Information : "+builder.toString());
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_DRM, errors.toString(), e.getMessage());
	    }
	}
	@Override
	public void onError(DrmManagerClient client, DrmErrorEvent event) {
		if(Constant.DEBUG)  Log.d(TAG,"DRM Error Code "+event.getMessage());
		switch(event.getType()){

		case DrmInfoEvent.TYPE_ACCOUNT_ALREADY_REGISTERED:
			// Handle event
			if(Constant.DEBUG)  Log.d(TAG,"DRM Already Register");
			break;
		case DrmInfoEvent.TYPE_ALREADY_REGISTERED_BY_ANOTHER_ACCOUNT:
			// Handle event
			if(Constant.DEBUG)  Log.d(TAG,"DRM Rights Register with other account");
			break;
		case DrmInfoEvent.TYPE_RIGHTS_REMOVED:
		}
	}
	@Override
	public void onInfo(DrmManagerClient client, DrmInfoEvent event) {
		if(Constant.DEBUG)  Log.d(TAG,"DRM Info Code "+event.getMessage());
		switch(event.getType()){
		case DrmInfoEvent.TYPE_RIGHTS_INSTALLED:
			// Handle event
			if(Constant.DEBUG)  Log.d(TAG,"Rights Installed");
			break;
		case DrmInfoEvent.TYPE_RIGHTS_REMOVED:
			// Handle event
			if(Constant.DEBUG)  Log.d(TAG,"Rights Removed");
			break;
		}

	}
	@Override
	public void onEvent(DrmManagerClient client, DrmEvent event) {
		if(Constant.DEBUG)  Log.d(TAG,"DRM On Event Code "+event.getMessage());
		switch(event.getType()){
		case DrmEvent.TYPE_DRM_INFO_PROCESSED:
			// Handle event
			if(Constant.DEBUG)  Log.d(TAG,"DRM Info Processed");
			break;
		case DrmEvent.TYPE_ALL_RIGHTS_REMOVED:
			// Handle event
			if(Constant.DEBUG)  Log.d(TAG,"All Rights Removed");
			break;
		}

	}

}
