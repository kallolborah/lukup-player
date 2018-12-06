package com.port.apps.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.port.Channel;
import com.port.api.db.util.CommonUtil;
import com.port.api.fs.FileSystem;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.util.ApplicationConstant;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

//consumer : player, network : bluetooth
public class Storage extends IntentService {
	private String TAG = "Storage";
	String method = "com.port.apps.storage.";
	
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String dockID;
	Channel returner;
	String caller;
	String type;
	
	public Storage() {
		super("Storage");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		String fileName = "";
		String filePath = "";
		String action = "";
		String name = "";
		String url = "";
		if (extras != null) {
			if(extras.containsKey("ProducerNetwork")){
	    		pnetwork = extras.getString("ProducerNetwork"); //to be used to return back response
	    	}
	    	if(extras.containsKey("ConsumerNetwork")){
	    		cnetwork = extras.getString("ConsumerNetwork"); //to be used to send request onward 
	    	}
	    	if(extras.containsKey("Producer")){
	    		producer = extras.getString("Producer");
	    	}
	    	if(extras.containsKey("Caller")){
	    		caller = extras.getString("Caller");
	    	}
	    	if(extras.containsKey("macid")){
	    		dockID = extras.getString("macid");
	    	}
	    	
	    	if(returner==null){ //to ensure that there is only one returner instance for one activity
		    	returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
	    		returner.set(producer, pnetwork, caller); //setting consumer = producer, network
	    	}
	    	
	    	if(extras.containsKey("Params")){
	    		try{
		    		functionData = extras.getString("Params");
		    		JSONObject jsonObj = new JSONObject(functionData);
		    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
		    		if(jsonObj.has("filename")){
		    			fileName = jsonObj.getString("filename");
		    		}if(jsonObj.has("filepath")){
		    			filePath = jsonObj.getString("filepath");
		    		}if(jsonObj.has("action")){
		    			action = jsonObj.getString("action");
		    		}
		    		if(jsonObj.has("fstype")){
		    			type = jsonObj.getString("fstype");
		    		}
		    		if(jsonObj.has("url")){
		    			url = jsonObj.getString("url");
		    		}
		    		if(jsonObj.has("name")){
		    			name = jsonObj.getString("name");
		    		}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}	
	    	}
	    	if(extras.containsKey("Method")){
	    		try{
	    			func = extras.getString("Method");
	    			if(Constant.DEBUG)  Log.d(TAG, "Method "+func);
	    			if(func.equalsIgnoreCase("fileBrowser")){
	    				fileBrowser(type);
	    			}else if(func.equalsIgnoreCase("processFolders")){
	    				processFolders(fileName, filePath, action);
	    			}else if(func.equalsIgnoreCase("sendUSBStatus")){
	    				sendUSBStatus();
	    			}else if(func.equalsIgnoreCase("download")){
//	    				download(url, name);
	    				new DataFetch(url, name).execute();
	    			}else if(func.equalsIgnoreCase("deleteStorage")){
	    				remove(filePath);
	    			}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_STORAGE, errors.toString(), e.getMessage());
	    		}		    		
	    	}
	    	
	    }
	}
	
	private void remove(String filepath){
		File file = new File(filepath);
		boolean deleted = file.delete();
	}
	
	private class DataFetch extends AsyncTask<String, String, Void>{

    	String dataUrl;
    	String c;
    	
    	public DataFetch(String url, String name) {
    		dataUrl = url;
    		c = name;
    	}
    	
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(String... params) {
			download(dataUrl,c);
			return null;
		}
	}

	
	private void download(String Url, String name){
		int count;
		try {
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("name", name);
		
			if(CommonUtil.isNetworkAvailable() && isExternalStorageWritable()){
				URL url = new URL(Url);
				URLConnection conexion = url.openConnection();
				conexion.connect();
				
				File f = new File(getApplicationContext().getExternalFilesDir(null),name);
				
				InputStream input = new BufferedInputStream(url.openStream());
				FileOutputStream filetowrite = getApplicationContext().openFileOutput(f.getName(), getApplicationContext().MODE_WORLD_READABLE);
							
				byte dataread[] = new byte[1024];
				long total = 0;
				while ((count = input.read(dataread)) != -1) {
					total += count;
					filetowrite.write(dataread, 0, count);
				}
	
				filetowrite.flush();
				filetowrite.close();
				input.close();
				data.put("result", "success");
			}else{
				data.put("result", "failure");
			}
			resp.put("params",data);
			returner.add(method+"download", resp, "messageActivity");
			returner.send();
		} catch (Exception e) {
    		e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}
		
	}
	
	private boolean isExternalStorageWritable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			return true;
		}
		return false;
	}
    
	// for displaying file/folder in ListView	
	private void fileBrowser(String type){
		try {
			File baseUSBFile;
			String fpath = null;
			if(ApplicationConstant.model.equalsIgnoreCase("X1")){
				if(type.equalsIgnoreCase("usb")){
					baseUSBFile = new File(ApplicationConstant.X1_USB_STORAGE);
					//Added by @Tomesh 18 AUG 2015 for checking USB connected
					if(baseUSBFile.exists()){
						fpath = ApplicationConstant.X1_USB_STORAGE;
					}else{
						fpath = ApplicationConstant.X1_USB_STORAGE;
						sendErrorMessage("No USB Storage Found",method+"fileBrowser");
						
					}
				}else{
					baseUSBFile = new File(ApplicationConstant.X1_LOCAL_STORAGE);
						//Added by @Tomesh 18 AUG 2015 for checking LOCAL STORAGE connected
					if(baseUSBFile.exists()){
						fpath = ApplicationConstant.X1_LOCAL_STORAGE;
					}else{
						fpath = ApplicationConstant.X1_LOCAL_STORAGE;
						sendErrorMessage("No SDcard Found",method+"fileBrowser");
						
					}
				}
			}else{
				baseUSBFile = new File(ApplicationConstant.USB_STORAGE);
				fpath = ApplicationConstant.USB_STORAGE;
			}
			if(baseUSBFile != null && !baseUSBFile.isHidden() && baseUSBFile.canRead()){
				File[] files = baseUSBFile.listFiles();
				if(files.length > 0){
					FileSystem mFileSys = new FileSystem();
					HashMap<String, ArrayList<String>> filemap = (HashMap<String, ArrayList<String>>) mFileSys.getFile(fpath);
					if(filemap != null && filemap.size() >0){
						Set<HashMap.Entry<String, ArrayList<String>>> entry = filemap.entrySet();
						Iterator<HashMap.Entry<String, ArrayList<String>>> itr = entry.iterator();
						while (itr.hasNext()) {
							HashMap.Entry<String, ArrayList<String>> string =  itr.next();
							if(Constant.DEBUG) Log.d(TAG,"Data : "+string.getKey()+" Value :"+string.getValue().toString());
						}
						ArrayList<String> folderList = filemap.get("folder");
						if(Constant.DEBUG)  Log.d(TAG, folderList+"");
						ArrayList<String> fileList = filemap.get("files");
						if(Constant.DEBUG)  Log.d(TAG, fileList+"");
						try {
							JSONObject sendResponse = new JSONObject();
							JSONObject data = new JSONObject();
							data.put("filepath", fpath);
							JSONArray folderJsonArray = new JSONArray();
							if(folderList != null && folderList.size() >0){
								for(int i=0;i<folderList.size();i++){
									if (!folderList.get(i).equalsIgnoreCase("transcode")) {
										JSONObject jsonObject = new JSONObject();
										jsonObject.put("id", i);
										jsonObject.put("name", folderList.get(i));
										jsonObject.put("path", fpath+folderList.get(i));
										folderJsonArray.put(jsonObject);
									}
								}
							}
							data.put("folderList", folderJsonArray);
							JSONArray fileJsonArray = new JSONArray();
							if(fileList != null && fileList.size() >0){
								for(int i=0;i<fileList.size();i++){
									JSONObject jsonObject = new JSONObject();
									jsonObject.put("id", i);
									jsonObject.put("name", fileList.get(i));
									jsonObject.put("path", fpath+fileList.get(i));
									folderJsonArray.put(jsonObject);
								}
							}
							data.put("fileList", fileJsonArray);
							if((fileJsonArray != null && fileJsonArray.length() == 0) && (folderJsonArray != null && folderJsonArray.length() == 0)){
								data.put("result", "failure");
								boolean isSDCard = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
								if(Constant.DEBUG) Log.d(TAG,"fileBrowser().isSDCard : "+isSDCard);
								if (isSDCard) {
									data.put("msg", this.getResources().getString(R.string.USB_CONNECTION_EMPTY_FILE));
								}else{
									data.put("msg", this.getResources().getString(R.string.NO_USB_CONNECTION));
								}
							}else{
									data.put("result", "success");
							}
							sendResponse.put("params",data);
							returner.add(method+"fileBrowser", sendResponse,"messageActivity");
							returner.send();
							// Add consumer,Producer.....
							
						} catch (JSONException e) {
							sendErrorMessage(this.getResources().getString(R.string.UNKNOWN_ERROR),"com.port.apps.storage.Storage.fileBrowser");
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_STORAGE, errors.toString(), e.getMessage());
						}
					}
				}else{
					sendErrorMessage(this.getResources().getString(R.string.USB_CONNECTION_ERROR),"com.port.apps.storage.Storage.fileBrowser");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_STORAGE, errors.toString(), e.getMessage());
		}
		
	}
	
	// for displaying file inside folder
	private void processFolders(String fileName,String filePath,String action){
		try {
				if(filePath == null){
					sendErrorMessage(this.getResources().getString(R.string.UNKNOWN_ERROR),"com.port.apps.storage.Storage.processFolders");
					return;
				}
				File selectedFile = new File(filePath);
				if(action != null && !action.equalsIgnoreCase("") && action.equalsIgnoreCase("back")){
					if(Constant.DEBUG)  Log.d(TAG, "Actual file path "+selectedFile.getAbsolutePath());
					selectedFile = selectedFile.getParentFile();
					if(Constant.DEBUG)  Log.d(TAG, "Parent file path "+selectedFile.getAbsolutePath());
					filePath = selectedFile.getAbsolutePath();
				}
				if(selectedFile != null && !selectedFile.isHidden() && selectedFile.canRead()){
					if(selectedFile != null && selectedFile.isDirectory()){
						File[] files = selectedFile.listFiles();
						if(files.length > 0){
							FileSystem mFileSys = new FileSystem();
							HashMap<String, ArrayList<String>> filemap = (HashMap<String, ArrayList<String>>) mFileSys.getFile(filePath);
							if(filemap != null && filemap.size() >0){
								Set<HashMap.Entry<String, ArrayList<String>>> entry = filemap.entrySet();
								Iterator<HashMap.Entry<String, ArrayList<String>>> itr = entry.iterator();
								while (itr.hasNext()) {
									HashMap.Entry<String, ArrayList<String>> string =  itr.next();
									if(Constant.DEBUG) Log.d(TAG,"Data : "+string.getKey()+" Value :"+string.getValue().toString());
								}
								ArrayList<String> folderList = filemap.get("folder");
								if(Constant.DEBUG)  Log.d(TAG, folderList+"");
								ArrayList<String> fileList = filemap.get("files");
								if(Constant.DEBUG)  Log.d(TAG, fileList+"");
								try {
									JSONObject sendResponse = new JSONObject();
									JSONObject data = new JSONObject();
									data.put("filepath", filePath+"/");
									JSONArray folderJsonArray = new JSONArray();
									if(folderList != null && folderList.size() >0){
										for(int i=0;i<folderList.size();i++){
											JSONObject jsonObject = new JSONObject();
											jsonObject.put("id", i);
											jsonObject.put("name", folderList.get(i));
											jsonObject.put("path", filePath+"/"+folderList.get(i));
											folderJsonArray.put(jsonObject);
										}
									}
									data.put("folderList", folderJsonArray);
									JSONArray fileJsonArray = new JSONArray();
									if(fileList != null && fileList.size() >0){
										for(int i=0;i<fileList.size();i++){
											JSONObject jsonObject = new JSONObject();
											jsonObject.put("id", i);
											jsonObject.put("name", fileList.get(i));
											jsonObject.put("path", filePath+"/"+fileList.get(i));
											folderJsonArray.put(jsonObject);
										}
									}
									data.put("fileList", fileJsonArray);
									if((fileJsonArray != null && fileJsonArray.length() == 0) && (folderJsonArray != null && folderJsonArray.length() == 0)){
										data.put("result", "failure");
										data.put("msg", this.getResources().getString(R.string.USB_CONNECTION_EMPTY_FILE));
									}else{
										data.put("result", "success");
									}
									sendResponse.put("params",data);
									returner.add(method+"processFolders", sendResponse, "messageActivity");
									returner.send();
									
									// Add consumer,Producer.....
								} catch (JSONException e) {
									sendErrorMessage(this.getResources().getString(R.string.UNKNOWN_ERROR),"com.port.apps.storage.Storage.processFolders");
									e.printStackTrace();
									StringWriter errors = new StringWriter();
									SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_STORAGE, errors.toString(), e.getMessage());
								}
							}
						}else{
							sendErrorMessage(this.getResources().getString(R.string.USB_CONNECTION_EMPTY_FILE),"com.port.apps.storage.Storage.processFolders");
						}
					}
				}else{
					sendErrorMessage(this.getResources().getString(R.string.USB_CONNECTION_ERROR),"com.port.apps.storage.Storage.fileBrowser");
				}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_STORAGE, errors.toString(), e.getMessage());
		}
	}
	
	public void sendUSBStatus(){
		try{
			if(Constant.DEBUG) Log.d(TAG,"sendUSBStatus()");
			JSONObject sendResponse = new JSONObject();
			JSONObject data = new JSONObject();
			boolean isSDCard= false;
			//Added by Tomesh 18 AUG 2015 For X1
			if(ApplicationConstant.model.equalsIgnoreCase("X1")){
				if(Constant.DEBUG) Log.d(TAG,"sendUSBStatus().isSDCard : "+isSDCard);
				File usbFileStatus = new File(ApplicationConstant.X1_USB_STORAGE);
				isSDCard = usbFileStatus.exists();
			}else{
				isSDCard = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			}
			if(Constant.DEBUG) Log.d(TAG,"sendUSBStatus().isSDCard : "+isSDCard);
			if (isSDCard) {
				data.put("result", "success");
			}else{
				data.put("result", "failure");
			}
				
			sendResponse.put("params", data);
			returner.add(method+"sendUSBStatus", sendResponse,"messageActivity");
			returner.send();
	
		}catch(Exception e){
			e.printStackTrace();
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_STORAGE, errors.toString(), e.getMessage());
		}
	}
	
	/*****************************************************************/
	public void sendErrorMessage(String msg,String handler) {
		try{
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			
			if(handler != null){
				data.put("handler", handler);
			}
			else{
				data.put("handler", handler);
			}
			if(msg != null)
				data.put("msg", msg);
			
			resp.put("params",data);
			returner.add(handler, resp, "messageActivity");
			returner.send();
			//
		}catch(Exception e){
			e.printStackTrace();
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_STORAGE, errors.toString(), e.getMessage());
		}
	}
	
	
}
