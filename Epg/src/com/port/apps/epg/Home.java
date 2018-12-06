package com.port.apps.epg;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.port.Channel;
import com.port.Port;
import com.port.api.db.service.BouquetGateway;
import com.port.api.db.service.BouquetInfo;
import com.port.api.db.service.CacheGateway;
import com.port.api.db.service.CacheInfo;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ChannelInfo;
import com.port.api.db.service.ProfileInfo;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.service.StatusInfo;
import com.port.api.db.service.Store;
import com.port.api.db.util.CacheData;
import com.port.api.network.Listener;
import com.port.api.util.Constant;
import com.port.api.util.Resolution;
import com.port.api.util.SntpClient;
import com.port.api.util.SystemLog;
import com.port.apps.epg.util.CommonUtil;


public class Home extends Activity implements HomeFragment.OnSelectedListener {
	private String TAG = "Home";
	Activity activity;
	String method = "com.port.apps.epg.Home.";	
	
	//Fragment related
	private HomeFragment home;
	public static String state = "Home";	
	private static Bundle savedState;
	public static String enterFrom = "Started";
	private int recommendation_focussed;
	public static Context mContext;
	
	//Root layout
	public static RelativeLayout mainLayout;
	protected static RelativeLayout detailLayout;
	private ImageView homeImage;
	private TextView homeTitle;
	private TextView homeDec;
	private Button goButton;
	private Timer updateEPGTimer=null;
	private Timer featuredTimer = null;
	private static int count = 0; 
	
	//Receiver
	mHomeReceiver receiver;
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String caller;
	String dockID;
	Channel returner;
//	private SharedPreferences settingData;
	public static long now = 0;
	private TextView textMsg;
	
	//For DVBMSG
	public static boolean  signal_status = false;
	public static boolean tables_collected = false;
	public static boolean Zapping_status = false;
	
	//PowerManager
	private PowerManager powerManager;
	private PowerManager.WakeLock wakeLock;
	private int field = 0x00000020;
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mContext = this;
		savedState = savedInstanceState;
		if(Constant.DEBUG)  Log.d(TAG,"onCreate()");
		
		receiver = new mHomeReceiver();
		
		IntentFilter home = new IntentFilter("com.port.apps.epg.Home");
		registerReceiver(receiver, home);
		
		IntentFilter catalogue = new IntentFilter("CATALOGUE");
		registerReceiver(receiver, catalogue);
		
		IntentFilter navigate = new IntentFilter("NAVIGATE");
		registerReceiver(receiver, navigate);
		
		IntentFilter network = new IntentFilter("NETWORKSTATUS");
		registerReceiver(receiver, network);		

		IntentFilter dvbmsg = new IntentFilter("DVBMSG");
		registerReceiver(receiver, dvbmsg);
		
		CacheData.setActivity(Home.this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	setContentView(R.layout.mainview);    	   	
    	
    	View decorView = getWindow().getDecorView();

    	int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    	decorView.setSystemUiVisibility(uiOptions);
    
    	activity = this;
    	mainLayout = (RelativeLayout) findViewById(R.id.lukup);
    	detailLayout = (RelativeLayout) findViewById(R.id.hometop);
		homeImage = (ImageView) findViewById(R.id.imageView);
		homeTitle = (TextView) findViewById(R.id.title);
		homeDec = (TextView) findViewById(R.id.desc);
		goButton = (Button) findViewById(R.id.gobutton);
		goButton.setFocusable(false);
    	/** display splash screen initially **/
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Resolution.init(dm);
		
		com.port.api.db.util.CacheData.setHomeVisibility("visible");
		
		// 23 Feb 2015
		textMsg = (TextView) findViewById(R.id.network);
		textMsg.setVisibility(View.VISIBLE);
		
		//screen on and off
		try{
			field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
		}catch(Exception e){
			
		}
		powerManager = (PowerManager)getSystemService(POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(field, getLocalClassName());
		
		pnetwork = Listener.pnetwork;
		producer = Listener.pname;
	}	
	
	@Override
	protected void onStart(){
		super.onStart();
		if(Constant.DEBUG)  Log.d(TAG,"onStart()");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		com.port.api.db.util.CacheData.setHomeVisibility("visible");
		if(Constant.DEBUG) Log.i(TAG, "onResume()");
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		featuredTimer.cancel();
		updateEPGTimer.cancel();
		unregisterReceiver(receiver);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(Constant.DEBUG)  Log.d(TAG,"onPause()");
	}
	
	
	public class mHomeReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(Constant.DEBUG)  Log.d(TAG,"mHomeReceiver()");
			Bundle extras = intent.getExtras();
		    if (extras != null) {	
		    	
		    	if(extras.containsKey("ProducerNetwork")){
		    		pnetwork = extras.getString("ProducerNetwork"); 
		    		//to be used to return back response
		    	}
		    	if(extras.containsKey("ConsumerNetwork")){
		    		cnetwork = extras.getString("ConsumerNetwork"); 
		    		//to be used to send request onward 
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
		    	if(extras.containsKey("ShowHome")){
		    		showHome();
		    	}
		    	if(extras.containsKey("ShowFeatured")){
		    		showCarousel();
		    	}
		    	if(extras.containsKey("ShowNavigator")){
		    		if(CacheData.getSubscriberId() != null && !CacheData.getSubscriberId().equalsIgnoreCase("")){
		    			try {
							showNavigator();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		    		}else{
		    			if(Constant.DEBUG)  Log.d(TAG , "Setup Screen ");
		    		}
		    	}
		    	if(extras.containsKey("Connection")){
		    		if(Constant.DEBUG)  Log.d(TAG , "Connection featuredList size(): "+Catalogue.featuredList.size());
		    		if(extras.getString("Connection").equalsIgnoreCase("true")){
		    			try {
		    				new SetTime().execute();
		    				if (textMsg != null && (Port.mBTBound || Port.mWifiBound) && Catalogue.featuredList.size()<=0) {
		    					textMsg.setText("Loading...");
		    				}else if (textMsg != null && (!Port.mBTBound || !Port.mWifiBound)) {
		    					if(Constant.model.equalsIgnoreCase("S")){
		    						textMsg.setText("Connect with Bluetooth using " + Port.BTAddress);
		    					}else{
//		    						textMsg.setText("Connect with Wifi using " + Port.WifiAdapterAddress);
		    						textMsg.setText("Connect with Bluetooth using " + Port.BTAddress + " , or Wifi using " + Port.WifiAdapterAddress);
		    					}
		    				}else if (textMsg != null) {
			    				textMsg.setVisibility(View.INVISIBLE);
							}
		    			} catch (Exception e) {
		    				e.printStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		    			} 
		    			if(Catalogue.featuredList.size()<=0){
		    				if(Constant.DEBUG)  Log.d(TAG,"mHomeReceiver().featured");
		    				Intent mServiceIntent = new Intent(Home.this, Catalogue.class);
		    				mServiceIntent.putExtra("Title", "featured");
		    				mServiceIntent.putExtra("macid", dockID);
		    				mServiceIntent.putExtra("network", pnetwork);
		    				getApplicationContext().startService(mServiceIntent);
			    		}
		    		}else if(extras.getString("Connection").equalsIgnoreCase("false")){
		    			if (textMsg != null) {
							textMsg.setText(activity.getResources().getString(R.string.NETWORKERROR));
						}
		    		}else if(extras.getString("Connection").equalsIgnoreCase("gone")){
//		    			if (textMsg != null) {
//		    				textMsg.setText("Connect with Bluetooth using " + Port.BTAddress + " , or Wifi using " + Port.WifiAdapterAddress);
//						}
		    			if(Constant.model.equalsIgnoreCase("S")){
    						textMsg.setText("Connect with Bluetooth using " + Port.BTAddress);
    					}else{
//    						textMsg.setText("Connect with Wifi using " + Port.WifiAdapterAddress);
    						textMsg.setText("Connect with Bluetooth using " + Port.BTAddress + " , or Wifi using " + Port.WifiAdapterAddress);
    					}
		    		}else if(extras.getString("Connection").equalsIgnoreCase("backon")){
//		    			init();
		    			new SetTime().execute();
		    			if (textMsg != null && (Port.mBTBound || Port.mWifiBound) && Catalogue.featuredList.size()<=0) {
	    					textMsg.setText("Loading...");
	    				}else if (textMsg != null) {
		    				textMsg.setVisibility(View.INVISIBLE);
						}
		    		}
		    	}
		    	if(extras.containsKey("updateTime")){
		    		if(extras.getString("updateTime").equalsIgnoreCase("updated")){
		    			if(updateEPGTimer==null){
		    				if(Constant.DEBUG) Log.d(TAG, "Setting timer to update EPG every 24 hrs ");
		    				updateEPGTimer = new Timer("Update EPG");
		    				updateEPGTimer.schedule(updateEPG, 0L, 24*60*60*1000L);
		    			}
		    			
//		    			if(state=="Home"){
//		    				long currentTime = com.port.api.util.CommonUtil.getDateTime();
//		    				
//		    				ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
//		    				BouquetGateway bouquetInfoGateway =new BouquetGateway(Port.c.getApplicationContext()) ;
//		    				
//		    				List<BouquetInfo> bouquetInfos = bouquetInfoGateway.getAllBouquetInfo();
//		    				List<ChannelInfo> vodSizeList = channelGateway.getAllServiceInfoByType("vod");
//		    				if(Constant.DEBUG)  Log.d(TAG , "vodSizeList "+vodSizeList.size()+", bouquetInfos "+bouquetInfos.size());
//		    				if(Constant.DEBUG)  Log.d(TAG , "Data Download  currentTime "+currentTime);
//		    				List<ChannelInfo> vodchannelList = channelGateway.getAllServiceInfoByTimeStamp(currentTime-(24*60*60*1000), "vod");
//		    		       	if((vodchannelList.size()!=0 && Catalogue.programImageList.size()==0) || vodSizeList.size()==0){
//		    			       	try {
//		    			       		if(Constant.DEBUG)  Log.d(TAG , "vodSizeList "+vodSizeList.size()+", vodchannelList "+vodchannelList.size());
//		    						
//		    			       		Intent mServiceIntent = new Intent(Home.this, Catalogue.class);
//		    						mServiceIntent.putExtra("Title", "vod-updates");
//		    						getApplicationContext().startService(mServiceIntent);
//		    					} catch (Exception e) {
//		    						e.printStackTrace();
//		    						StringWriter errors = new StringWriter();
//		    						e.printStackTrace(new PrintWriter(errors));
//		    						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//		    					}
//		    		       	}
//		    		       	
//		    		       	SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
//		    		       	dateFormatGmt.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//		    		       	Date date = null;
//		    		       	
//		    		       	//Local time zone   
//		    		       	SimpleDateFormat dateFormatLocal = new SimpleDateFormat("HH");
//		    		       	try {
//								date = dateFormatLocal.parse( dateFormatGmt.format(new Date()) );
//							} catch (ParseException e1) {
//								e1.printStackTrace();
//								StringWriter errors = new StringWriter();
//								e1.printStackTrace(new PrintWriter(errors));
//								SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e1.getMessage());
//							}
//		    		       	int currentHr=date.getHours();
//		    		       	if(Constant.DEBUG)  Log.d(TAG , "currentHr: "+currentHr);
//		    		       	
//		    		       	if (Constant.DVB) {	//DVB middleware
//								if (Constant.DEBUG) Log.d(TAG, "Not in DVB Module");
//							} else {
//			    				List<ChannelInfo> livechannelList = channelGateway.getAllServiceInfoByTimeStamp(currentTime-(currentHr*60*60*1000), "live");
//			    				if(Constant.DEBUG)  Log.d(TAG , "Live Data Download  System time - " + currentHr + " hours "+(currentTime-(currentHr*60*60*1000)));
//			    				if(Constant.DEBUG)  Log.d(TAG , "livechannelList.size(): "+livechannelList.size());
//			    				if(livechannelList.size()!=0){
//			    			    	try {
//			    			    		if(Constant.DEBUG)  Log.d(TAG , "livechannelList "+livechannelList.size());
//			    						Intent mServiceIntent = new Intent(Home.this, Catalogue.class);
//			    						mServiceIntent.putExtra("Title", "epg-updates");
//			    						getApplicationContext().startService(mServiceIntent);
//			    					} catch (Exception e) {
//			    						e.printStackTrace();
//			    						StringWriter errors = new StringWriter();
//			    						e.printStackTrace(new PrintWriter(errors));
//			    						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//			    					}
//			    		       	}    
//							}
//		    			}
		    		}
		    	}
		    	if(extras.containsKey("type")){
		    	
		    		String type = extras.getString("type");
		    		if(Constant.DEBUG)Log.i(TAG, "DVB MSG =========>"+type);
		    		String msg = extras.getString("msg");
		    		int msg_no = extras.getInt("no");
		    		JSONObject sendResponse = new JSONObject();
					JSONObject datasend = new JSONObject();
		    		if(type.equalsIgnoreCase("status")){
		    			try {
							if(msg_no==22){
								tables_collected= true;
							}else if(msg_no == 23){
								signal_status = true;
								if(Constant.DEBUG)Log.e(TAG+"signal_status============>", signal_status + "true");
							}
							if(signal_status && tables_collected){
								Zapping_status = true;
								if(Constant.DEBUG)Log.e(TAG+"============>", Zapping_status + "true");
							}
							
							returner.set(producer, pnetwork, "com.player.NotificationsService");
							datasend.put("result", "success");
							datasend.put("msg", msg);
							sendResponse.put("params", datasend);
							returner.add("com.port.apps.epg.DvbMsg.sendStatusMsg", sendResponse,"startService");
							returner.send();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
		    			
		    		}else if(type.equalsIgnoreCase("error")){
		    			if(msg_no == 0){
							signal_status = false;
							Zapping_status = false;
							if(Constant.DEBUG)Log.e(TAG+"signal_status============>", signal_status + "");
						}
		    			if(signal_status && tables_collected){
							Zapping_status = true;
							if(Constant.DEBUG)Log.e(TAG+"============>", Zapping_status + "true");
						}
						try {
							returner.set(producer, pnetwork, "com.player.NotificationsService");
							datasend.put("result", "success");
							datasend.put("msg", getResources().getString(R.string.TUNERERROR));
							sendResponse.put("params", datasend);
							returner.add("com.port.apps.epg.DvbMsg.sendStatusMsg", sendResponse,"startService");
							returner.send();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
		    		}
		    	}
		    			    	
		    	if(returner==null){ //to ensure that there is only one returner instance for one activity
			    	returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
		    		returner.set(producer, pnetwork, caller); //setting consumer = producer, network, package:class to call
		    	}
		    	
		    	String direction = null;
		    	
		    	if(extras.containsKey("Params")){
		    		try{
			    		functionData = extras.getString("Params");
			    		JSONObject jsonObj = new JSONObject(functionData);
			    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
			    		if(jsonObj.has("direction")){
			    			direction = jsonObj.getString("direction");
			    		}if(jsonObj.has("subscriberid")){
							CacheData.setSubscriberId(jsonObj.getString("subscriberid"));
			    		}
						if(jsonObj.has("distributorId")){
			    			CacheData.setDistributorId(jsonObj.getString("distributorId"));
			    		}
						if(jsonObj.has("distributorPwd")){
							CacheData.setDistributorPwd(jsonObj.getString("distributorPwd"));
			    		}
			    		
		    		} catch (JSONException e) {
						e.printStackTrace();
					}
		    	}
		    	
		    	if(extras.containsKey("Method")){
		    		func = extras.getString("Method");
		    		if (func.equalsIgnoreCase("Fling")) {
		    			if(direction.equalsIgnoreCase("UP")){
		    				if(Constant.DEBUG)  Log.d(TAG , "mHomeReceiver UP "+goButton.isFocusable());
		    				new Thread(new Runnable() {         
		                        @Override
		                        public void run() {                 
		                            new Instrumentation().sendKeySync(new KeyEvent(0,KeyEvent.KEYCODE_DPAD_UP));
		                        }   
		                    }).start();
		    			} else if (direction.equalsIgnoreCase("DOWN")){
		    				if(Constant.DEBUG)  Log.d(TAG , "mHomeReceiver DOWN "+goButton.isFocusable());
		    				if(goButton.isFocusable()){
		    					if(Constant.DEBUG)  Log.d(TAG , "DPAD DOWN "+goButton.isFocusable());
		    					home.rListView.setFocusable(true);
		    					home.rListView.getChildAt(recommendation_focussed-home.rListView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.pink));
		    					goButton.setBackgroundColor(0xDDAEA8A8);
		    					goButton.setFocusable(false);
		    				}
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_DPAD_DOWN));
		                        }   
		                    }).start();
		    					
		    			} else if (direction.equalsIgnoreCase("RIGHT")){
		    				if(Constant.DEBUG)  Log.d(TAG , "mHomeReceiver RIGHT "+goButton.isFocusable());
		    				new Thread(new Runnable() {         
		                        @Override
		                        public void run() {                 
		                            new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_DPAD_RIGHT));
		                        }   
		                    }).start();
		    			} else if (direction.equalsIgnoreCase("LEFT")){
		    				if(Constant.DEBUG)  Log.d(TAG , "mHomeReceiver LEFT "+goButton.isFocusable());
		    				new Thread(new Runnable() {         
		                        @Override
		                        public void run() {                 
		                            new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_DPAD_LEFT));
		                        }   
		                    }).start();
		    			}
					}else if(func.equalsIgnoreCase("Select")){
						if(Constant.DEBUG)  Log.d(TAG , "mHomeReceiver SELECT "+goButton.isFocusable());
						if(goButton.isFocusable()){
					    	final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
							returner.set(producer, pnetwork, "com.player.apps.Navigator");
							// send Detail on Player to show on Info screen
							
							int value = 0;
							if(count>0){
								value = count - 1;
							}else{
								value = Catalogue.featuredImageList.size() - 1;
							}
							
							String type = Catalogue.featuredImageList.get(value).get("type");
							if(Constant.DEBUG)  Log.d(TAG,"goButton.OnClickListener().type: "+type+", value: "+value);
							try{
								JSONObject sendResponse = new JSONObject();
								JSONObject data = new JSONObject();
								
								ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
								ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
								StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
								
								int userId=CacheData.getUserId();
								if(userId==0){
									CacheGateway cache  = new CacheGateway(Port.c);
									CacheInfo info = cache.getCacheInfo(1000);
									if (info != null) {
										userId = Integer.valueOf(info.getProfile());
										CacheData.setUserId(userId);
									}
								}
								
								int itemId;
								if(type.equalsIgnoreCase("event")){
									itemId = Integer.parseInt(Catalogue.featuredImageList.get(value).get("imageId"));
									ProgramInfo selectedEventInfo = programGateway.getProgramInfoByUniqueId(itemId+"");
									if(selectedEventInfo != null){
										ChannelInfo chlInfo = channelGateway.getServiceInfoByServiceId(selectedEventInfo.getChannelServiceId());
										String pricingmodel = selectedEventInfo.getPriceModel();
										boolean subscribe = false;
										if(pricingmodel != null && (pricingmodel.trim().equalsIgnoreCase("PPC") || pricingmodel.trim().equalsIgnoreCase("PPV"))){
											StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(selectedEventInfo.getChannelServiceId(), 9,"service");
											if(info != null) {
												if(info.getStatus() == 9) {
													subscribe = true;
												} else {
													subscribe = false;
												}
											} else {
												subscribe = false;
											}
											data.put("subscribe", subscribe);
										}
										data.put("pricingmodel",selectedEventInfo.getPriceModel());
										data.put("id", selectedEventInfo.getEventId());
										data.put("servicetype", selectedEventInfo.getChannelType());
										data.put("serviceid", selectedEventInfo.getChannelServiceId());
										data.put("channelPrice", chlInfo.getPrice());
										data.put("url", selectedEventInfo.getEventSrc());
										data.put("name", selectedEventInfo.getEventName());
										data.put("releasedate", selectedEventInfo.getDateAdded());
										data.put("actors", selectedEventInfo.getActors());
										data.put("rating", selectedEventInfo.getRating());
										data.put("genre", selectedEventInfo.getGenre());
										data.put("image", selectedEventInfo.getImage());
										data.put("description", selectedEventInfo.getDescription());
										data.put("director", selectedEventInfo.getDirector());
										data.put("production", selectedEventInfo.getProductionHouse());
										data.put("musicdirector", selectedEventInfo.getMusicDirector());
										data.put("price", selectedEventInfo.getPrice());
										data.put("type", "event");
										sendResponse.put("params", data);
										returner.add("com.port.apps.epg.Home.showFeaturedInfo", sendResponse,"messageActivity");
										returner.send();
									}
								}else if(type.equalsIgnoreCase("service")){
									itemId = Integer.parseInt(Catalogue.featuredImageList.get(value).get("imageId"));
								}else{
									itemId = Integer.parseInt(Catalogue.featuredImageList.get(value).get("imageId"));
									sendPackageInfo(itemId);
								}
								
							} catch (Exception e) {
								e.printStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
							}
							return;
						}
						
						new Thread(new Runnable() {         
	                        @Override
	                        public void run() {                 
	                            new Instrumentation().sendKeySync(new KeyEvent(0,KeyEvent.KEYCODE_ENTER));
	                        }   
	                    }).start();						
			    	} else if(func.equalsIgnoreCase("Back") && !state.equalsIgnoreCase("Home")){
			    		if(Constant.DEBUG)  Log.d(TAG , "mHomeReceiver BACK "+goButton.isFocusable());
						getState();
			    	}else if(func.equalsIgnoreCase("power")){
			    		if(Constant.DEBUG)  Log.d(TAG , "Power wakelock state "+ wakeLock.isHeld());

			    		if(!wakeLock.isHeld()){
			    			wakeLock.acquire();
			    		}
			    		if(wakeLock.isHeld()){
			    			wakeLock.release();
			    		}
			    	}else{
			    		if(Constant.DEBUG)  Log.d(TAG , "mHomeReceiver state: "+state);
			    		state = "Featured";	
			    	}
    		
		    	}
		    }
		}
		
	}	
	
	private TimerTask updateEPG = new TimerTask() {
		@Override
		public void run() {
			activity.runOnUiThread(new Runnable() {
			     public void run() {

			    	if(Constant.DEBUG)  Log.d(TAG,"mHomeReceiver().featured");
					Intent mServiceIntent = new Intent(Home.this, Catalogue.class);
					mServiceIntent.putExtra("Title", "featured");
					mServiceIntent.putExtra("macid", dockID);
					mServiceIntent.putExtra("network", pnetwork);
					getApplicationContext().startService(mServiceIntent);		
				
					if(Constant.DEBUG)  Log.d(TAG , "livechannelList ");
					Intent mServiceIntentEpg = new Intent(Home.this, Catalogue.class);
					mServiceIntentEpg.putExtra("Title", "vod-updates");
					mServiceIntent.putExtra("macid", dockID);
					mServiceIntent.putExtra("network", pnetwork);
					getApplicationContext().startService(mServiceIntentEpg);
					
			     }
			});
		}
	};	
	
	
	public class SetTime extends AsyncTask<Boolean, Boolean, Boolean>{
		SntpClient client = new SntpClient();
		@Override
		protected Boolean doInBackground(Boolean... params) {
			if(Constant.DEBUG) Log.d(TAG, "SetTime().Network Status: "+com.port.api.util.CommonUtil.isNetworkAvailable());
			return client.requestTime(Constant.SNTP_TIME,1800000);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(Constant.DEBUG) Log.d(TAG, "client: "+result);
			if (result) {
				try{
					 now = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
					 if(Constant.DEBUG) Log.d(TAG, "Port init() timestamp: "+now);
					 //Set system date and time	
					 Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
					 c.setTimeInMillis(now);
					 
					//send message to Home activity to update VOD and TV EPGs
					 Intent home = new Intent();
					 home.setAction("CATALOGUE");
					 home.putExtra("updateTime","updated");
					 sendBroadcast(home);
				} catch(Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
				}
			}  
		}
	}
	

	private void sendPackageInfo(int itemId){
		try{
	    	final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
			returner.set(producer, pnetwork, "com.player.apps.Navigator");
			
			String pgmList = "";
			String chlList = "";
			String name = "";
			String price = "";
			ArrayList<String> list = new ArrayList<String>();
			JSONObject sendResponse = new JSONObject();
			JSONObject data = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			JSONObject jsonObject = null;
			
			ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			
			for (int i = 0; i < Catalogue.packageList.size(); i++) {
				if(Catalogue.packageList.get(i).getPackageId().equalsIgnoreCase(itemId+"")){
					name = Catalogue.packageList.get(i).getPackageName();
					pgmList = Catalogue.packageList.get(i).getPackagePrograms();
					chlList = Catalogue.packageList.get(i).getPackageChannels();
					price = Catalogue.packageList.get(i).getPackagePrice();
					if(Constant.DEBUG)  Log.d(TAG , "Package Name: "+name+", Price "+price);
					if(Constant.DEBUG)  Log.d(TAG , "pgmList "+pgmList);
					if(Constant.DEBUG)  Log.d(TAG , "chlList "+chlList);
				}
			}
			if(pgmList.length()>0){
				list = com.port.api.util.CommonUtil.Tokenizer(pgmList, ",");
				for (int j = 0; j < list.size(); j++) {
					ProgramInfo Info = programGateway.getProgramInfoByUniqueId(list.get(j));
					jsonObject = new JSONObject();
					jsonObject.put("id", Info.getEventId());
					jsonObject.put("name", Info.getEventName());
					jsonObject.put("desc", Info.getDescription());
					jsonObject.put("type", "event");
					jsonArray.put(jsonObject);
				}
			}
			
			
			if(chlList.length()>0){
				list = com.port.api.util.CommonUtil.Tokenizer(chlList, ",");
				for (int j = 0; j < list.size(); j++) {
					ChannelInfo Info = channelGateway.getServiceInfoByServiceId(Integer.parseInt(list.get(j)));
					jsonObject = new JSONObject();
					jsonObject.put("id", Info.getServiceId());
					jsonObject.put("name", Info.getChannelName());
					jsonObject.put("desc", Info.getDesc());
					jsonObject.put("type", "service");
					jsonArray.put(jsonObject);
				}
			}
			if(jsonArray.length()<=0){
				data.put("result", "failure");
				sendResponse.put("params",data);
				returner.add("com.port.apps.epg.Home.showFeaturedInfo", sendResponse,"messageActivity");
				returner.send();
			}else{
				jsonArray.put(jsonObject);
				data.put("packageList", jsonArray);
				data.put("id", itemId+"");
				data.put("name", name);
				data.put("price", price);
				data.put("type", "package");
				data.put("result", "success");
				sendResponse.put("params",data);
				returner.add("com.port.apps.epg.Home.showFeaturedInfo", sendResponse,"messageActivity");
				returner.send();
			}
			
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}
	
	
	public void showHome(){
		if(Constant.DEBUG)  Log.d(TAG,"showHome()");
		
		if(state.equalsIgnoreCase("home")){
			if(com.port.api.util.CommonUtil.isNetworkAvailable()){
				//send message to Player to show Navigator, send as "startActivity"
		    	final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
				returner.set(producer, pnetwork, "com.player.UpdateService");
				
				if(mainLayout != null){
					try {
						getFeaturedBackground();
						state = "Featured";	
						
						//send to UpdateService in Player and set video is running state to false
						JSONObject resp = new JSONObject();	
						JSONObject data = new JSONObject();
						try {
							data.put("state", "stop");
							resp.put("params",data);
						} catch (JSONException e) {
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
						}
						returner.add("com.port.apps.epg.Play.Stop", resp,"startService");
						returner.send();
						
					} catch (Exception e) {
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
					}
				}
			}else{
				Toast.makeText(Port.c, R.string.NETWORKERROR, Toast.LENGTH_LONG).show();
			}
		}
	}
	
	private void showCarousel(){
		if(Constant.DEBUG)  Log.d(TAG,"showCarousel().state: "+state);
		if(state.equalsIgnoreCase("Featured")){		
			if (textMsg != null) {
				textMsg.setVisibility(View.INVISIBLE);
			}
			
			if(savedState==null && home==null){		
				home = new HomeFragment();
				getFragmentManager().beginTransaction().add(R.id.fragment_container, home).commit();
				
			}			
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try{
						if(home.rListView!=null){
							if (home.rListView.isShown()) {
								home.rListView.setAdapter(home.new RecommendedAdapter(Catalogue.featuredImageList));	// added jan 8 2015
							}
							home.rpos = 0;
						}
					}catch(Exception e){
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
					}
				}
			});
		}
	}	
	
	private void showNavigator() throws InterruptedException{
		if(Constant.DEBUG)  Log.d(TAG,"showNavigator()");
		if(state.equalsIgnoreCase("Featured") || state.equalsIgnoreCase("Home")){
			//send message to Player to show Navigator, send as "startActivity"
			final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
			returner.set(producer, pnetwork, "com.player.apps.Navigator");
			JSONObject resp = new JSONObject();	
			JSONObject data = new JSONObject();
			try {
				resp.put("params",data);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			returner.add("com.port.apps.epg.Home.showHome", resp,"startActivity");
			returner.send();
		}
	}
	
	private void getFeaturedBackground(){
		if(featuredTimer==null){
			featuredTimer = new Timer("Update Featured");
			featuredTimer.schedule(updateFeatured, 0L, 60000L);
		}
	}
	
	private TimerTask updateFeatured = new TimerTask() {
		@Override
		public void run() {
			activity.runOnUiThread(new Runnable() {
			     public void run() {
		    	 try {
		    		if(Constant.DEBUG)  Log.d(TAG,"Featured catalog size for Dock home screen : "+Catalogue.featuredImageList.size());
		    		if(Catalogue.featuredImageList.size()>0){		
		    			//added on 18 feb 2015
		    			int i = count;
		    			while(i < Catalogue.featuredImageList.size()) {
		    				if(Catalogue.getBitmapFromMemCache("F_"+Catalogue.featuredImageList.get(count).get("imageId"))!=null){
		    					break;
		    				}else{
		    					count++;
		    				}
		    				i = count;
						}
		    			
		    			if(Constant.DEBUG)  Log.d(TAG,"Featured count :"+count);
		    			//****************************************//	
		    			detailLayout.setVisibility(View.VISIBLE);
		    			if(Constant.DEBUG)  Log.d(TAG,"Featured image, image id "+ "F_"+Catalogue.featuredImageList.get(count).get("imageId"));
		    			if(Catalogue.getBitmapFromMemCache("F_"+Catalogue.featuredImageList.get(count).get("imageId"))!=null){
	    					if(Constant.DEBUG)  Log.d(TAG,"Featured image is null, image id "+ "F_"+Catalogue.featuredImageList.get(count).get("imageId"));
	    					homeImage.setImageBitmap(Catalogue.getBitmapFromMemCache("F_"+Catalogue.featuredImageList.get(count).get("imageId")));
	    				}
	    				homeTitle.setText(Catalogue.featuredImageList.get(count).get("name"));
	    				homeDec.setText(Catalogue.featuredImageList.get(count).get("desc"));	
	    				
	    				if(count == (Catalogue.featuredImageList.size()-1)){
							count = 0;
						}else{
							++count;
						}
		    			
		    		}else{
		    			if(Constant.DEBUG)  Log.d(TAG,"updateHomeScreen().size: "+Catalogue.featuredImageList.size());
//		    			homeImage.setImageResource(android.R.color.transparent);
		    			detailLayout.setVisibility(View.INVISIBLE);
		    			try {
		    				Intent mServiceIntent = new Intent(Home.this, Catalogue.class);
		    				mServiceIntent.putExtra("Title", "featured");
		    				mServiceIntent.putExtra("macid", dockID);
		    				mServiceIntent.putExtra("network", pnetwork);
		    				getApplicationContext().startService(mServiceIntent);
		    				count = 0;
		    			} catch (Exception e) {
		    				e.printStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		    			}
		    		}
		    		 

					} catch (Exception e) {
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
					}
			    }
			});
		}
	};


	@Override
	public void onRecommendation(int id) {
		
		//added on jan 8 2015
		try{
			if(Constant.DEBUG)  Log.d(TAG,"onRecommendation().Id: "+id);
			
			final Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
			returner.set(producer, pnetwork, "com.player.apps.Navigator");
			
			JSONObject sendResponse = new JSONObject();
			JSONObject data = new JSONObject();
			
			ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
			
			boolean subscribe = false;
			
//			int userId=CacheData.getUserId();
//			if(userId==0){
//				CacheGateway cache  = new CacheGateway(Port.c);
//				CacheInfo cinfo = cache.getCacheInfo(1000);
//				if (cinfo != null) {
//					userId = Integer.valueOf(cinfo.getProfile());
//					CacheData.setUserId(userId);
//				}
//			}
			
			String type = "";
			for (int i = 0; i < Catalogue.featuredImageList.size(); i++) {
				if (Catalogue.featuredImageList.get(i).get("imageId").equalsIgnoreCase(id+"")) {
					type = Catalogue.featuredImageList.get(i).get("type");
				}
			}
			
			if(type.equalsIgnoreCase("event")){
				ProgramInfo selectedEventInfo = programGateway.getProgramInfoByUniqueId(id+"");
				if(selectedEventInfo != null){
					ChannelInfo chlInfo = channelGateway.getServiceInfoByServiceId(selectedEventInfo.getChannelServiceId());
					String pricingmodel = selectedEventInfo.getPriceModel();
					if(pricingmodel != null && (pricingmodel.trim().equalsIgnoreCase("PPV"))){
						StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(selectedEventInfo.getProgramId()/*getChannelServiceId()*/, 9,"event");
						if(Constant.DEBUG)  Log.d(TAG,"Program Id: "+ selectedEventInfo.getProgramId());
						if(info != null) {
							if(info.getStatus() == 9) {
								if(Constant.DEBUG)  Log.d(TAG,"Subscribed ");
								subscribe = true;
							} else {
								if(Constant.DEBUG)  Log.d(TAG,"Not Subscribed ");
								subscribe = false;
							}
						} else {
							subscribe = false;
						}
						data.put("subscribe", subscribe);
					}
					data.put("pricingmodel",selectedEventInfo.getPriceModel());
					data.put("id", selectedEventInfo.getEventId());
					data.put("servicetype", selectedEventInfo.getChannelType());
					data.put("serviceid", selectedEventInfo.getChannelServiceId());
					data.put("channelPrice", chlInfo.getPrice());
					data.put("url", selectedEventInfo.getEventSrc());
					data.put("name", selectedEventInfo.getEventName());
					data.put("releasedate", selectedEventInfo.getDateAdded());
					data.put("actors", selectedEventInfo.getActors());
					data.put("rating", selectedEventInfo.getRating());
					data.put("genre", selectedEventInfo.getGenre());
					data.put("image", selectedEventInfo.getImage());
					data.put("description", selectedEventInfo.getDescription());
					data.put("director", selectedEventInfo.getDirector());
					data.put("production", selectedEventInfo.getProductionHouse());
					data.put("musicdirector", selectedEventInfo.getMusicDirector());
					data.put("price", selectedEventInfo.getPrice());
					data.put("type", "event");
					sendResponse.put("params", data);
					returner.add("com.port.apps.epg.Home.showFeaturedInfo", sendResponse,"messageActivity");
					returner.send();
				}
			}else if(type.equalsIgnoreCase("service")){
				ChannelInfo channelInfo = channelGateway.getServiceInfoByServiceId(id);
				if(channelInfo != null){
					String pricingmodel = channelInfo.getPriceModel();
					if(pricingmodel != null && pricingmodel.trim().equalsIgnoreCase("PPC")){
						StatusInfo info = statusGateway.getSubscribeInfoByUniqueId(channelInfo.getServiceId(), 9,"service");
						if(info != null) {
							if(info.getStatus() == 9) {
								subscribe = true;
							} else {
								subscribe = false;
							}
						} else {
							subscribe = false;
						}
						data.put("subscribe", subscribe);
					}
					data.put("pricingmodel",channelInfo.getPriceModel());
					data.put("id", channelInfo.getServiceId());
					data.put("servicetype", channelInfo.getType());
					data.put("serviceid", channelInfo.getServiceId());
					data.put("channelPrice", channelInfo.getPrice());
					data.put("url", "");
					data.put("name", channelInfo.getChannelName());
					data.put("releasedate", "");
					data.put("actors", "");
					data.put("rating", "");
					data.put("genre", channelInfo.getServiceCategory());
					data.put("image", "");
					data.put("description", channelInfo.getDesc());
					data.put("director", "");
					data.put("production", "");
					data.put("musicdirector", "");
					data.put("price", "");
					data.put("type", "service");
					sendResponse.put("params", data);
					returner.add("com.port.apps.epg.Home.showFeaturedInfo", sendResponse,"messageActivity");
					returner.send();
				}
			}else{
				sendPackageInfo(id);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		
	}

	@Override
	public void goFeature(int id) {
		recommendation_focussed = id;
		goButton.setFocusable(true);
		goButton.setBackgroundColor(0xFFEC108C);
	}	
	
	public void getState(){
		
		JSONObject sendResponse = new JSONObject();
		JSONObject data = new JSONObject();
		try {
			//return state
			if(state.equalsIgnoreCase("Program")){
				state = "Channel";
				getFragmentManager().popBackStack();
				
			} else if (state.equalsIgnoreCase("Channel")){
				state = "Featured";
				detailLayout.setVisibility(View.VISIBLE);
				getFragmentManager().popBackStack();

			} else if (state.equalsIgnoreCase("Featured")){
				state = "Home";
			}
			data.put("state", state);
			sendResponse.put("params", data);
			
	    	Channel returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
    		returner.set(producer, pnetwork, "com.player.apps.Navigator"); 			
			returner.add("com.port.apps.epg.Home.getState", sendResponse,"messageActivity");
			
			returner.send();
		} catch (JSONException e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
}
