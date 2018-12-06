package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;
import com.port.Channel;
import com.port.Port;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ProfileGateway;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.util.CacheData;
import com.port.api.interactive.iMouse;
import com.port.api.interactive.iMouse.mBinder;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.apps.epg.util.CommonUtil;

public class Social extends Activity {
	
	private String TAG = "Social";
	private Handler mRunOnUi = new Handler();
	private String title;
	private String link;
	private String desc;
	private String imgPath;
	private String status = "";
	private int userId ;
	private ProfileGateway profileInfoGateway;

	private mSocialReceiver mReceiver;
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String caller;
	Channel returner;
	String dockID = ""; //is this correct ?
	
	private Intent i;
	private iMouse mMouse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	setContentView(R.layout.social);
    	
    	if(Constant.DEBUG)  Log.d(TAG  , "onCreate");
		CacheData.setFacebook(new Facebook(Constant.APP_ID));
		SessionStore.restore(CacheData.getFacebook(), getApplicationContext());
		
		profileInfoGateway = new ProfileGateway(Port.c.getApplicationContext()) ;
//		if(profileInfoGateway == null){
//			if(CommonUtil.checkConnectionForLocaldb()){
//				profileInfoGateway = new ProfileGateway(CacheData.getDatabase());
//				CacheData.setProfileInfoGateway(profileInfoGateway);
//			}
//		}
		
		Bundle br = this.getIntent().getExtras();
		if (br != null) {
			if (br.containsKey("Title")) {
				title = br.getString("Title");
			}if (br.containsKey("Link")) {
				link = br.getString("Link");
			}if (br.containsKey("Desc")) {
				desc = br.getString("Desc");
			}if (br.containsKey("Image")) {
				imgPath = br.getString("Image");
			}if (br.containsKey("Status")) {
				status = br.getString("Status");
			}if (br.containsKey("UserId")) {
				userId = br.getInt("UserId");
			}
			if(Constant.DEBUG)  Log.d(TAG  , "title: "+title+", link: "+link);
			if(Constant.DEBUG)  Log.d(TAG  , "status: "+status+", imgPath: "+imgPath+", desc: "+desc);
			if (status.equalsIgnoreCase("connect")) {
				try {
					onFacebookClick();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else if (status.equalsIgnoreCase("disconnect")) {
				fbLogout();
			}else{
				FaceBookLogin(title, link, desc, imgPath);
			}
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput (InputMethodManager.SHOW_FORCED,0);
		
		i = new Intent(this,iMouse.class);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE); 
	}
	
	public ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance	
        	mBinder binder = (mBinder) service;
        	mMouse = binder.getService();
        	mMouse.showCursor(0, 0);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };	

	
	@Override
	protected void onStart(){
		super.onStart();
		
		IntentFilter social = new IntentFilter("com.port.apps.epg.Social");
		mReceiver = new mSocialReceiver();
		registerReceiver(mReceiver, social);
	}
	
/************************************************************************************************/
	
	public class mSocialReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			
			if (extras != null) {
				float xCoordinate = 0;
				float yCoordinate = 0;
				
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
		    	}
				
		    	if(extras.containsKey("Params")){
		    		try{
			    		functionData = extras.getString("Params");
			    		JSONObject jsonObj = new JSONObject(functionData);
			    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
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
		    			if(Constant.DEBUG)  Log.d(TAG, "func : "+func);
		    			if(func.equalsIgnoreCase("close")){
		    				close();
		    			}else if(func.equalsIgnoreCase("Mouse")) {
			    			if(Constant.DEBUG)  Log.d(TAG ,"Mouse xCoordinate: "+xCoordinate+", yCoordinate: "+yCoordinate);
	    					int ix = 0;
	    					int iy = 0;
	    					if(Constant.DEBUG)  Log.d(TAG ,"Percentage xCoordinate: "+xCoordinate+", yCoordinate: "+yCoordinate);
	    					
	    					ix = mMouse.coordinates("x", xCoordinate,Social.this);
	    					iy = mMouse.coordinates("y", yCoordinate,Social.this);
	    					mMouse.showCursor(ix, iy);
			    		}
		    		}catch(Exception e){
		    			e.printStackTrace();
		    			StringWriter errors = new StringWriter();
		    			e.printStackTrace(new PrintWriter(errors));
		    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		    		}		    		
		    	}
		    }
		}
	}
	
	
	private void close(){
		finish();
	}
	
	@Override
	public void onDestroy() {
		if(Constant.DEBUG) Log.w(TAG, "OnDestroy()");
		if(mReceiver != null){
			unregisterReceiver(mReceiver);
		}
        stopService(i);
		super.onDestroy();
	}
    
	
	/*************************** FaceBook ****************************************************/
	private void FaceBookLogin(String title,String link,String desc,String imagePath){
		if(Constant.DEBUG)  Log.d(TAG," FaceBookLogin()");
		if (CacheData.getFacebook().isSessionValid()) {
			String name = SessionStore.getName(getApplicationContext());
			name = (name.equals("")) ? "Unknown" : name;
			if(Constant.DEBUG)  Log.d(TAG ,"  Connected to (" + name + ")");
			if(Constant.DEBUG)  Log.d(TAG," mFacebook.isSessionValid()"+CacheData.getFacebook().isSessionValid());
			if(Constant.DEBUG)  Log.d(TAG," mFacebook.getAccessToken()"+CacheData.getFacebook().getAccessToken());
			if(Constant.DEBUG)  Log.d(TAG," mFacebook.getAccessExpires()"+CacheData.getFacebook().getAccessExpires());
			postToFacebook(title,link,desc,imagePath);
		}else{
			try {
				onFacebookClick();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void postToFacebook(String title,String link,String desc,String imaghePath) {	
		if(Constant.DEBUG)  Log.d(TAG,"postToFacebook()");
		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(CacheData.getFacebook());
		Bundle params = new Bundle();
		params.putString("name", title);
		params.putString("caption", "www.lukup.com");
		params.putString("link", link);
		params.putString("description", desc);
		params.putString("picture", imaghePath);
		
		mAsyncFbRunner.request("me/feed", params, "POST", new WallPostListener());
	}
	
	private final class WallPostListener extends BaseRequestListener {
        public void onComplete(final String response) {
        	mRunOnUi.post(new Runnable() {
        		@Override
        		public void run() {
        			if(Constant.DEBUG)  Log.d("WallPostListener()", "onComplete().Posted to Facebook");
        			finish();
        		}
        	});
        }
    }
	
	private void onFacebookClick() throws InterruptedException {
		if(Constant.DEBUG)  Log.d(TAG,"onFacebookClick()");
		if (!CacheData.getFacebook().isSessionValid()) {
			CacheData.getFacebook().authorize(this, Constant.PERMISSIONS, -1, new FbLoginDialogListener());
			
			if(Constant.DEBUG)  Log.d(TAG, "onFacebookClick()");
			final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
			returner.set(producer, pnetwork, "com.player.apps.Keypad");
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
			returner.add("com.port.apps.epg.Social.start", resp,"startActivity");
			returner.send();
		}
	}
    
    private final class FbLoginDialogListener implements DialogListener {
        public void onComplete(Bundle values) {
            SessionStore.save(CacheData.getFacebook(), getApplicationContext());
            getFbName();
        }

        public void onFacebookError(FacebookError error) {
           if(Constant.DEBUG)  Log.d("FbLoginDialogListener()", "onFacebookError().Facebook connection failed");
        }
        
        public void onError(DialogError error) {
        	if(Constant.DEBUG)  Log.d("FbLoginDialogListener()", "onError().Facebook connection failed");
        }

        public void onCancel() {
        }
    }
    
	private void getFbName() {
		new Thread() {
			@Override
			public void run() {
		        String name = "";
		        int what = 1;
		        
		        try {
		        	String me = CacheData.getFacebook().request("me");
		        	
		        	JSONObject jsonObj = (JSONObject) new JSONTokener(me).nextValue();
		        	name = jsonObj.getString("name");
		        	what = 0;
		        } catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
				}
		        
		        mFbHandler.sendMessage(mFbHandler.obtainMessage(what, name));
			}
		}.start();
		
		if (!status.equalsIgnoreCase("connect")) {
			postToFacebook(title,link,desc,imgPath);
		}else{
			finish();
		}
	}
	
	private void fbLogout() {
		new Thread() {
			@Override
			public void run() {
				SessionStore.clear(getApplicationContext());
				int what = 1;
		        try {
		        	CacheData.getFacebook().logout(getApplicationContext());
		        	what = 0;
		        } catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
				}
		        mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}
	
	private Handler mFbHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				String username = (String) msg.obj;
		        username = (username.equals("")) ? "No Name" : username;
		        SessionStore.saveName(username, getApplicationContext());
		        if(Constant.DEBUG)  Log.d(TAG,"  Connected to (" + username + ")");
		        if(Constant.DEBUG)  Log.d("mFbHandler()", "Connected to Facebook as " + username);
		        profileInfoGateway.setFbStatus("connect", userId);
			} else {
				if(Constant.DEBUG)  Log.d("mFbHandler()", "Connected to Facebook");
			}
		}
	};
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				if(Constant.DEBUG)  Log.d(TAG,"  Disconnected");
				if(Constant.DEBUG)  Log.d("mHandler()", "Disconnected logout failed");
			} else {
				if(Constant.DEBUG)  Log.d("mHandler()", "Disconnected from Facebook");
				profileInfoGateway.setFbStatus("disconnect", userId);
			}
			finish();
		}
	};
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	};
	
	/*************************** Mouse cursor********************/
}
