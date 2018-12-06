package com.port.api.interactive;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.port.Channel;
import com.port.api.R;
import com.port.api.interactive.iMouse.mBinder;
//import com.port.api.service.ProfileInfo;
//import com.port.api.service.ProgramGateway;
//import com.port.api.service.ProgramInfo;
//import com.port.api.service.StatusGateway;
//import com.port.api.service.StatusInfo;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class iApp extends Activity {
	
	private String TAG = "iApp";
	String method = "com.port.api.interactive.iApp.";
	
	//Receiver
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String caller;
	Channel returner;
	String dockID;
	
	private Intent i;
	private iMouse mMouse;
	private mAppReceiver mReceiver;
	private String handler;
	
	int EventId = 0;
	String appUrl = "";
	float xCoordinate = 0;
	float yCoordinate = 0;

	
	public static final int  INSTALLGAME = 12;
	public static final int  UNINSTALLGAME = 13;
	
	//globals
	private String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	setContentView(R.layout.social);
    	
		i = new Intent(this,iMouse.class);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE); 
		
		Bundle br = this.getIntent().getExtras();
		if (br != null) {
			if(br.containsKey("ProducerNetwork")){
	    		pnetwork = br.getString("ProducerNetwork"); //to be used to return back response
	    	}
	    	if(br.containsKey("ConsumerNetwork")){
	    		cnetwork = br.getString("ConsumerNetwork"); //to be used to send request onward 
	    	}
	    	if(br.containsKey("Producer")){
	    		producer = br.getString("Producer");
	    	}
	    	if(br.containsKey("Caller")){
	    		caller = br.getString("Caller");
	    	}    	
			
	    	if(returner==null){ //to ensure that there is only one returner instance for one activity
		    	dockID = Build.ID; //is this correct ?
		    	returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
	    		returner.set(producer, pnetwork, caller); //setting consumer = producer, network
	    	}
	    	
	    	if(br.containsKey("Params")){
	    		try{
		    		functionData = br.getString("Params");
		    		JSONObject jsonObj = new JSONObject(functionData);
		    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
		    		if(jsonObj.has("url")){
		    			appUrl = jsonObj.getString("url");
		    		}
		    		if(jsonObj.has("name")){
		    			name = jsonObj.getString("name");
		    			name = name.replaceAll("\\s+","");
		    		}
		    		if(jsonObj.has("id")){
		    			EventId = Integer.parseInt(jsonObj.getString("id"));
		    		}
		    		if(jsonObj.has("x")){
		    			xCoordinate = Float.parseFloat(jsonObj.getString("x"));
		    		}
		    		if(jsonObj.has("y")){
		    			yCoordinate = Float.parseFloat(jsonObj.getString("y"));
		    		}
	    		} catch(Exception e){
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}
	    	}
			
			
			if (br.containsKey("Method")) {
				handler = br.getString("Method");
//				if(handler.equalsIgnoreCase("getGames")){
//    				//get list of games
//    				getGames();
//	    		}	
    			if(handler.equalsIgnoreCase("installGame")){
    				//install game
    				installGame(EventId,appUrl,name);
    			}
    			if(handler.equalsIgnoreCase("uninstallGame")){
    				//uninstall game
    				uninstallGame(appUrl);
    			}
    			if(handler.equalsIgnoreCase("playGame")){
    				//uninstall game
    				playGame(name);
    			}
			}
		}
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		IntentFilter gApp = new IntentFilter("com.port.api.interactive.iApp");
		mReceiver = new mAppReceiver();
		registerReceiver(mReceiver, gApp);
	}
	
	public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,IBinder service) {
        	mBinder binder = (mBinder) service;
        	mMouse = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };	
    
    @Override
	public void onDestroy() {
		if(Constant.DEBUG) Log.w(TAG, "OnDestroy()");
		if(mReceiver != null){
			unregisterReceiver(mReceiver);
		}
        stopService(i);
		super.onDestroy();
	}


	public class mAppReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			int EventId = 0;
			String appUrl = "";
			String name = "";
			float xCoordinate = 0;
			float yCoordinate = 0;
			
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
				
		    	if(returner==null){ //to ensure that there is only one returner instance for one activity
			    	dockID = Build.ID; //is this correct ?
			    	returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
		    		returner.set(producer, pnetwork, caller); //setting consumer = producer, network
		    	}
		    	
		    	if(extras.containsKey("Params")){
		    		try{
			    		functionData = extras.getString("Params");
			    		JSONObject jsonObj = new JSONObject(functionData);
			    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
			    		if(jsonObj.has("url")){
			    			appUrl = jsonObj.getString("url");
			    		}
			    		if(jsonObj.has("name")){
			    			name = jsonObj.getString("name");
			    			name = name.replaceAll("\\s+","");
			    		}
			    		if(jsonObj.has("id")){
			    			EventId = Integer.parseInt(jsonObj.getString("id"));
			    		}
			    		if(jsonObj.has("x")){
			    			xCoordinate = Float.parseFloat(jsonObj.getString("x"));
			    		}
			    		if(jsonObj.has("y")){
			    			yCoordinate = Float.parseFloat(jsonObj.getString("y"));
			    		}
		    		} catch(Exception e){
		    			e.printStackTrace();
		    			StringWriter errors = new StringWriter();
		    			e.printStackTrace(new PrintWriter(errors));
		    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		    		}
		    	}
		    	if(extras.containsKey("Method")){
		    		try{
		    			func = extras.getString("Method");
		    			if(Constant.DEBUG)  Log.d(TAG, "Method name : "+func);
//		    			if(func.equalsIgnoreCase("getGames")){
//		    				//get list of games
//		    				getGames();
//			    		}	
		    			if(func.equalsIgnoreCase("installGame")){
		    				//install game
		    				installGame(EventId,appUrl,name);
		    			}
		    			if(func.equalsIgnoreCase("uninstallGame")){
		    				//uninstall game
		    				uninstallGame(appUrl);
		    			}
		    			if(func.equalsIgnoreCase("playGame")){
		    				//uninstall game
		    				playGame(name);
		    			}
		    			if(func.equalsIgnoreCase("Mouse")) {
			    			if(Constant.DEBUG)  Log.d(TAG ,"Mouse xCoordinate: "+xCoordinate+", yCoordinate: "+yCoordinate);
	    					int ix = 0;
	    					int iy = 0;
	    					if(Constant.DEBUG)  Log.d(TAG ,"Percentage xCoordinate: "+xCoordinate+", yCoordinate: "+yCoordinate);
	    					
	    					ix = mMouse.coordinates("x", xCoordinate,iApp.this);
	    					iy = mMouse.coordinates("y", yCoordinate,iApp.this);
	    					mMouse.showCursor(ix, iy);
			    		}
		    		} catch(Exception e){
		    			e.printStackTrace();
		    			StringWriter errors = new StringWriter();
		    			e.printStackTrace(new PrintWriter(errors));
		    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		    		}	    		
		    	}
		    }
		}
	}
	
	
	private void installGame(int id, String Url,String name){
		if(Constant.DEBUG)  Log.d(TAG, "installGame Url : "+Url);
		try{
			
			new downloadAppFile(Url, id).execute();
//			if(DownloadFile(Url,"app.apk")){
//				Uri uri = Uri.fromFile(new File(getFilesDir(),"app.apk").getAbsoluteFile());
//				if(Constant.DEBUG)  Log.d(TAG, "Game Uri: " + uri.toString());
//				
//				Intent intent = new Intent(Intent.ACTION_VIEW);
//				intent.setDataAndType(uri, "application/vnd.android.package-archive");
//		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		        startActivity(intent);
//				setGames(id, "add");
//			}

		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	public class downloadAppFile extends AsyncTask<String, String, Boolean> {
		String Path;
		int Id;
		public downloadAppFile(String path,int id) {
			Path = path;
			Id = id;
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			boolean value = DownloadFile(Path, "app.apk");
			return value;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(result){
				if(Constant.DEBUG)  Log.d(TAG, "onPostExecute "+result);
				Uri uri = Uri.fromFile(new File(getFilesDir(),"app.apk").getAbsoluteFile());
				if(Constant.DEBUG)  Log.d(TAG, "Game Uri: " + uri.toString());
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri, "application/vnd.android.package-archive");
		        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intent);
//				setGames(Id, "add");
			}
		}
	}
	
	private void uninstallGame(String apkUri){
		Intent intent = new Intent(Intent.ACTION_DELETE, Uri.fromParts("package",
		getPackageManager().getPackageArchiveInfo(Uri.parse(apkUri).getPath(), 0).packageName,null));
		startActivity(intent);
	}
	
//	private void setGames(int Id,String tag){
//		try {
//			
//			StatusGateway statusGateway = CacheData.getStatusGateway();
//			if(statusGateway == null){
//				if(CommonUtil.checkConnectionForLocaldb()){
//					statusGateway = new StatusGateway(CacheData.getDatabase());
//					CacheData.setStatusGateway(statusGateway);
//				}
//			}
//			
//			int userId = 1000;
//			ProfileInfo selectedProfileInfo = CacheData.getSelectedProfileInfo();
//			if(selectedProfileInfo != null){
//				userId = selectedProfileInfo.getUserId();
//			}
//			
//			if (tag.equalsIgnoreCase("add")) {
//				StatusInfo gamesInfo = statusGateway.getInfoByType(Id, INSTALLGAME, "event");
//				if(gamesInfo == null) {
//					if(Constant.DEBUG)  Log.d(TAG, "Add Game detail in DB");
//					statusGateway.insertStatusInfo(userId, 0, Id, 0, INSTALLGAME, 0, 0, CommonUtil.getDate(), CommonUtil.getDateTime());
//				} else {
//					if(Constant.DEBUG)  Log.d(TAG, "Games already exist.");
//				}
//			} else {
//				StatusInfo gamesInfo = statusGateway.getInfoByType(Id, UNINSTALLGAME, "event");
//				if(gamesInfo != null) {
//					if(Constant.DEBUG)  Log.d(TAG, "Delete Game data from DB");
//					statusGateway.deleteStatusInfoById(Id, "event", UNINSTALLGAME);
//				} else {
//					if(Constant.DEBUG)  Log.d(TAG, "Games not exist.");
//				}
//			}
//			
//			String dockID = Build.ID; //is this correct ?
//			final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
//			returner.set("Player", "BT", "com.player.apps.Keypad");
////			returner.set("Player", "BT", "com.player.apps.Navigator"); 
//			JSONObject resp = new JSONObject();	
//			JSONObject data = new JSONObject();
//			resp.put("params",data);
//			returner.add(method+"start", resp,"startActivity");
//			returner.send();
//		}catch(Exception e){
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//		}
//	}
//	
//	private void getGames(){
//		//return list of games from installs table
//		try {
//			JSONObject resp = new JSONObject();
//			JSONObject data = new JSONObject();
//			JSONArray jsonArray = null;
//			JSONObject jsonObject = null;
//			List<ProgramInfo> list = new ArrayList<ProgramInfo>();
//			StatusGateway statusGateway = CacheData.getStatusGateway();
//			if(statusGateway == null){
//				if(CommonUtil.checkConnectionForLocaldb()){
//					statusGateway = new StatusGateway(CacheData.getDatabase());
//					CacheData.setStatusGateway(statusGateway);
//				}
//			}
//			
//			ProgramGateway programGateway = null;
//			if (programGateway == null) {
//				if (CommonUtil.checkConnectionForLocaldb()) {
//					programGateway = new ProgramGateway(CacheData.getDatabase());
//					CacheData.setProgramGateway(programGateway);
//				}
//			}
//			
//			int userId = 1000;
//			ProfileInfo selectedProfileInfo = CacheData.getSelectedProfileInfo();
//			if(selectedProfileInfo != null){
//				userId = selectedProfileInfo.getUserId();
//			}
//			
//			List<StatusInfo> gameListInfos = statusGateway.getAllStatusInfoByUserId(userId+"",INSTALLGAME,"event");
//			if(Constant.DEBUG)  Log.d(TAG, "gameListInfos: "+gameListInfos.size());
//			if(gameListInfos != null && gameListInfos.size()>0){
//				jsonArray = new JSONArray();
//				for (int i = 0; i < gameListInfos.size(); i++) {
//					if (gameListInfos.get(i) != null) {
//						ProgramInfo programInfo = programGateway.getProgramInfoByEventId(gameListInfos.get(i).getEventId());
//						if (programInfo != null) {
//							jsonObject = new JSONObject();
//							jsonObject.put("id", programInfo.getEventId()+"");
//							jsonObject.put("name", programInfo.getEventName());
//							if(Constant.DEBUG)  Log.d(TAG, "jsonObject: "+jsonObject.toString());
//							jsonArray.put(jsonObject);
//						}
//					}
//				}
//				
//				data.put("gameList", jsonArray);
//				data.put("result", "success");
//				resp.put("params", data);
//				returner.add(method+"getGames", resp, "messageActivity");
//				returner.send();
//			}else{
//				data.put("result", "failure");
//				resp.put("params", data);
//				returner.add(method+"getGames", resp, "messageActivity");
//				returner.send();
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//		}
//	}
	
	
	private void playGame(String name){
		try{
			
		String packagename = name.trim().substring(0,name.trim().lastIndexOf("."));
		
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.setComponent(new ComponentName(packagename, name));
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
			
			//send message to Player to startactivity gamepad
			String dockID = Build.ID; //is this correct ?
			final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
			returner.set("Player", "BT", "com.player.apps.GamePad");
			
			JSONObject resp = new JSONObject();	
			JSONObject data = new JSONObject();
			try {
				resp.put("params",data);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			returner.add("com.port.api.interactive.iView.start", resp,"startActivity");
			returner.send();	
			finish();
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	
	
	private boolean DownloadFile(String Url,String name){
		int count;
		
		try {
			URL url = new URL(Url);
			URLConnection conexion = url.openConnection();
			conexion.connect();

			InputStream input = new BufferedInputStream(url.openStream());
			FileOutputStream filetowrite = getApplicationContext().openFileOutput("app.apk", getApplicationContext().MODE_WORLD_READABLE);
						
			byte data[] = new byte[1024];
			long total = 0;
			while ((count = input.read(data)) != -1) {
				total += count;
				filetowrite.write(data, 0, count);
			}

			filetowrite.flush();
			filetowrite.close();
			input.close();
			
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
			return false;
		}
		return true;
	}
	
	

}
