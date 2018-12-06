package com.port.apps.epg;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.jets3t.service.CloudFrontService;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.utils.ServiceUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.drm.DrmErrorEvent;
import android.drm.DrmEvent;
import android.drm.DrmInfoEvent;
import android.drm.DrmInfoRequest;
import android.drm.DrmManagerClient;
import android.drm.DrmStore;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer.ExoPlayer;
import com.port.Channel;
import com.port.Consumer;
import com.port.Port;
import com.port.Schedule;
import com.port.api.ads.AdInfo;
import com.port.api.ads.XMLParser;
import com.port.api.apps.webservices.AmazonS3Bucket;
import com.port.api.db.service.ChannelGateway;
import com.port.api.db.service.ChannelInfo;
import com.port.api.db.service.ProgramGateway;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.service.StatusGateway;
import com.port.api.db.service.StatusInfo;
import com.port.api.db.util.CacheData;
import com.port.api.interactive.iView;
import com.port.api.network.Listener;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
import com.port.apps.epg.exoplayer.DashRendererBuilder;
import com.port.apps.epg.exoplayer.DemoUtil;
import com.port.apps.epg.exoplayer.HlsRendererBuilder;
import com.port.apps.epg.exoplayer.LukupPlayer;
import com.port.apps.epg.exoplayer.UnsupportedDrmException;
import com.port.apps.epg.exoplayer.WidevineTestMediaDrmCallback;
import com.port.apps.epg.util.AdsStatistics;
import com.port.util.ApplicationConstant;

// TODO: Auto-generated Javadoc
/**
 * The Class Play.
 */
public class Play extends iView implements OnCompletionListener, SurfaceHolder.Callback, LukupPlayer.Listener {

	/** The activity. */
	Activity activity;
	
	/** The video view. */
	public static VideoView videoView;
	
	/** The image view. */
	public static ImageView imageView;
	
	/** The method. */
	private String method = "com.port.apps.epg.Play.";
	
	/** The ad control timer. */
	private Timer adControlTimer = null;
	
	/** The m media control timer. */
	private Timer mMediaControllTimer = null;
	
	/** The tag. */
	private String TAG = "Play";
	
	/** The DRM url. */
	private String DRMUrl;
	
	/** The drm manager. */
	private DrmManagerClient drmManager = null;
	
	/** The xml parser data. */
	private ArrayList<HashMap<String,String>> xmlParserData = new ArrayList<HashMap<String,String>>();
	
	/** The Tracking url. */
	private String TrackingUrl;
	
	/** The Impression url. */
	private String ImpressionUrl;
	
	/** The Ad flag. */
	private boolean AdFlag;
	
	/** The source url. */
	private String sourceUrl="";
	
	/** The pg start time. */
	private long CurrentTime = 0;
	private long StartTime = 0;
	
	/** For MediaController */
	private long cTime = 0;
	
	/** The pg end time. */
	private long pgEndTime = 0;
	
	/** The ad total duration. */
	private long adTotalDuration = 0;
	
	/** The ad end time. */
	private long adEndTime = 0;
	
	/** The pg total duration. */
	private long pgTotalDuration = 0;
	private long eventduration = 0;
//	private String duration = "";
	
	/** The Channel id. */
	private String ChannelId;
	
	/** The current spots. */
	private int currentSpots;
	
	/** The ad info. */
	private AdInfo adInfo;	
	
	/** The interactive. */
	private boolean interactive=false;
	
	/** The Ad source url. */
	private String AdSourceUrl;
	
	/** The position. */
	private int position;
	
	/** The Ad type. */
	private String AdType;
	
	/** The liked. */
	private int liked = 0;
	
	/** The clicked. */
	private int clicked = 0;
	
	/** The click. */
	private String click = "";
	
	/** The program action. */
	private String programAction = "";
	
	/** The device type. */
	private static String deviceType = "media%20player";
	
	/** The calledfrom. */
	private String calledfrom = "zap";
	
	/** The program info. */
	private ProgramInfo programInfo;
	
	/** The channel info. */
	private ChannelInfo channelInfo;	
	
	/** The current playing url. */
	private String currentPlayingUrl ="";
	
	/** The channel number. */
	private static String channelNumber = "ID";

	/** The xml string buffer. */
	public static StringBuffer xmlStringBuffer = new StringBuffer();
	
	/** The xml channel buffer. */
	public static StringBuffer xmlChannelBuffer = new StringBuffer();
	
	/** The Constant SIZE_25_KB. */
	private static final int SIZE_25_KB = 3000;
	
	/** The progress dialog. */
	private ProgressDialog progressDialog;
	
	/** The func. */
	String func;
	
	/** The function data. */
	private String functionData;
	
	/** The pnetwork. */
	String pnetwork;
	
	/** The cnetwork. */
	String cnetwork;
	
	/** The producer. */
	String producer;
	
	/** The caller. */
	String caller;
	
	/** The returner. */
	Channel returner=null;
	
	/** The dock id. */
	String dockID=""; //is this correct ?

	/** The event id. */
	String eventId = "";
	
	/** The service id. */
	String serviceId = "";
	
	/** The url. */
	String url = "";
	
	/** The type. */
	String type = "";
	
	/** The subtype. */
	String subtype = "";
	
	/** The asset id. */
	String assetId = "";
	
	/** The layout. */
	private RelativeLayout layout;
	
	/** The unix date. */
	private String unixDate;
	
	//Exoplayer related
	/** The player. */
	private  LukupPlayer player;
	
	/** The surface view. */
	private SurfaceView surfaceView;
	private String pricingmodel = "";
	
	private String base="";
	private String chname ="";
	private String starttime ="";
	
	/* (non-Javadoc)
	 * @see com.port.api.interactive.iView#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		
		if(Constant.DEBUG)  Log.d(TAG,"onCreate()");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
    	activity = this;
		
    	setContentView(R.layout.videoviewer);
		
	}
	
	/* (non-Javadoc)
	 * @see android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media.MediaPlayer)
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		try {
			if(Constant.DEBUG)  Log.d(TAG,"inside OnCompletionListener");
			if(mMediaControllTimer != null){
				mMediaControllTimer.cancel();
			}
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
	
			//if ad is being played
			if (AdFlag) { 
				AdFlag = false;
				if(clicked==0){
					closeAd();
				}
				
			}else if(interactive){
				myWebView.setVisibility(View.INVISIBLE);
				
				if(adControlTimer != null){
					adControlTimer.cancel();
				}
				pageType="";
				String action= "'trigger'";
	    		String url = "javascript:com.lukup.eventHandler"+"("+action+",\"end\");";
                myWebView.loadUrl(url);
                
                returner.set(Listener.pname, pnetwork, "com.player.apps.Navigator" ); 			
	    		returner.add("com.port.apps.epg.Play.StopiView", resp, "messageActivity");		
				data.put("state", "stop");
				data.put("interactive","true");
				resp.put("params", data);		    	
				returner.send();
				
				Play.this.finish();
				
			} else {
				videoView.stopPlayback();
				currentPlayingUrl = "";
						
				if(Constant.DEBUG) Log.e(TAG, "setOnCompletionListener().pgStartTime"+CurrentTime);
				
				returner.set(Listener.pname, pnetwork, "com.player.apps.PlayBack" ); 			
	    		returner.add("com.port.apps.epg.Play.Stop", resp, "messageActivity");		
				data.put("state", "stop");
				resp.put("params", data);		    	
				returner.send();			
				
				Play.this.finish();
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}   
	
	/**
	 * Close ad.
	 */
	private void closeAd(){
		JSONObject resp = new JSONObject();
		JSONObject data = new JSONObject();
		
		try{
			myWebView.setVisibility(View.INVISIBLE);
			
			if(adControlTimer != null){
				adControlTimer.cancel();
			}
			
			String action= "'trigger'";
			String url = "javascript:com.lukup.eventHandler"+"("+action+",\"end\");";
	        myWebView.loadUrl(url);
	        
	        returner.set(Listener.pname, pnetwork, caller); 			
			returner.add("com.port.apps.epg.Play.StopiView", resp, "messageActivity");		
			data.put("state", "stop");
			resp.put("params", data);		    	
			returner.send();
			
			progressDialog = new ProgressDialog(this,R.style.MyTheme);
			progressDialog.setCancelable(true);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
			progressDialog.show();
			
			if(DRMUrl.equalsIgnoreCase(".wvm")){
				new DrmManagerImpl(sourceUrl).execute();
			}else if(DRMUrl.equalsIgnoreCase(".mpd")){
				 lukupExoPlayer(sourceUrl);
			}else{
				playVideoData(sourceUrl);
			}
			
			pageType="";					
			
			adTotalDuration = pgTotalDuration;
			adEndTime = pgTotalDuration;
			
			if(Constant.DEBUG) Log.e(TAG, "setOnCompletionListener().pgStartTime"+CurrentTime);
			ImpressionCal(100, 100);
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		//Added by Tomesh for switching vob to live
		setIntent(intent);
		url="";
		
		if(Constant.DEBUG)  Log.d(TAG,"onNewIntent()");
		surfaceView = (SurfaceView)findViewById(R.id.surface_view);
		layout = (RelativeLayout)findViewById(R.id.layout);
		videoView = (VideoView)findViewById(R.id.videoPlayer);
		imageView = (ImageView)findViewById(R.id.image);
		myWebView = (WebView) findViewById(R.id.webview);

		CacheData.setHomeVisibility("invisible");
		
//		Date currentTime = new Date(com.port.api.util.CommonUtil.getDateTime()+ 3*60*60*1000);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // the format of your date("2014-02-18T12:42:00.000Z")
//		unixDate = sdf.format(currentTime);
//		if(Constant.DEBUG)  Log.i(TAG, "formattedDate"+unixDate);
		
		CacheData.Play(true);
		
		Bundle extras = intent.getExtras();
		String seekto = "";
		String state = "";
		String direction = "";
		String key ="";
		
		//added 0n 25 Feb 2015
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
		
		progressDialog = new ProgressDialog(this,R.style.MyTheme);
		progressDialog.setCancelable(true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
		progressDialog.show();
		
		videoView.setOnCompletionListener(this);
		
		if(Constant.DEBUG) Log.e(TAG, "Received request in onNewIntent");
		
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
	    		if(Constant.DEBUG) Log.e(TAG, "Received request in onNewIntent with dock ID " + dockID);
	    		returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
	    	}
			
	    	if(extras.containsKey("Params")){
	    		try{
		    		functionData = extras.getString("Params");
		    		JSONObject jsonObj = new JSONObject(functionData);
		    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
		    		if(jsonObj.has("id")){
		    			eventId = jsonObj.getString("id");
		    		}if(jsonObj.has("serviceid")){
		    			serviceId = jsonObj.getString("serviceid");
		    		}if(jsonObj.has("url")){
		    			url = jsonObj.getString("url").trim();
		    		}if(jsonObj.has("seekto")){
		    			seekto = jsonObj.getString("seekto");
		    		}if(jsonObj.has("state")){
		    			state = jsonObj.getString("state");
		    		}if(jsonObj.has("type")){
		    			type = jsonObj.getString("type");
		    		}if(jsonObj.has("subtype")){
		    			subtype = jsonObj.getString("subtype");
		    		}if(jsonObj.has("direction")){
		    			direction = jsonObj.getString("direction");
		    		}if(jsonObj.has("key")){
		    			key = jsonObj.getString("key");
		    		}if(jsonObj.has("activity")){
		    			calledfrom = jsonObj.getString("activity");
		    		}if(jsonObj.has("starttime")){
		    			starttime = jsonObj.getString("starttime");
		    		}if(jsonObj.has("pricingmodel")){
		    			pricingmodel = jsonObj.getString("pricingmodel");
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
	    			if(Constant.DEBUG)  Log.d(TAG, "onNewIntent func : "+func);
	    			if(!func.equalsIgnoreCase("PlayOn")){
	    				if(progressDialog.isShowing()){
							progressDialog.dismiss();
						}
	    			}
	    			
	    			if(func.equalsIgnoreCase("PlayOn")){
	    				String extension = "";
	    	    		if (url != null && !url.equalsIgnoreCase("")) {
	    	    			extension = url.trim().substring(url.trim().lastIndexOf("."));
	    				}
	    	    		
	    				if(!eventId.equalsIgnoreCase("") && extension.equalsIgnoreCase(".html")){
	    	    			myWebView.setVisibility(View.VISIBLE);
	    		    		startiView(AdSourceUrl);
	    		    		interactive = true;
	    				} else if((!eventId.equalsIgnoreCase("")||!serviceId.equalsIgnoreCase(""))&&!extension.equalsIgnoreCase(".html")){
	    					if(mMediaControllTimer != null){
	    						mMediaControllTimer.cancel();
	    					}
//	    					status="true";
	    					if (type.equalsIgnoreCase("live")) {
	    						if (calledfrom.equalsIgnoreCase("DVBRemote")) {
									playDVBLiveData(Integer.parseInt(serviceId),type, starttime);
								}else{
									PlayOn(Integer.parseInt(serviceId),Integer.parseInt(eventId), url, type, starttime);
								}
							} else {
								if(!url.equalsIgnoreCase(currentPlayingUrl)){
		    						PlayOn(Integer.parseInt(serviceId),Integer.parseInt(eventId), url, type, starttime);
		    					}else{
		    						if(progressDialog.isShowing()){
		    							progressDialog.dismiss();
		    						}
//		    						mediaController();
		    					}
							}
	    					
	    	    		}else if(eventId.equalsIgnoreCase("") && subtype.equalsIgnoreCase("video")){
//	    	    			status="true";
	    	    			if(!url.equalsIgnoreCase(currentPlayingUrl)){
	    	    				playVideoData(url);
	    	    			}else{
	    						if(progressDialog.isShowing()){
	    							progressDialog.dismiss();
	    						}
	    					}
	    	    		}else if(eventId.equalsIgnoreCase("") && subtype.equalsIgnoreCase("music")){
	    	    			if(!url.equalsIgnoreCase(currentPlayingUrl)){
	    	    				playVideoData(url);
	    	    			}else{
	    						if(progressDialog.isShowing()){
	    							progressDialog.dismiss();
	    						}
	    					}
	    	    		}else if(eventId.equalsIgnoreCase("") && subtype.equalsIgnoreCase("image")){
	    	    			videoView.setVisibility(View.GONE);
	    	    			imageView.setVisibility(View.VISIBLE);
	    	    			imageView.setImageURI(Uri.parse(url));
	    	    			if(progressDialog.isShowing()){
								progressDialog.dismiss();
							}
	    	    			try{
	    		    			JSONObject resp = new JSONObject();
	    						JSONObject data = new JSONObject();
	    	
	    						returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
	    			    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
	    						data.put("state", "start");
	    						resp.put("params", data);		    	
	    						returner.send();
	    	    			} catch(Exception e){
	    		    			e.printStackTrace();
	    		    			StringWriter errors = new StringWriter();
	    		    			e.printStackTrace(new PrintWriter(errors));
	    		    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
	    		    		}
	    	    		}else if(subtype.equalsIgnoreCase("recorded")){
	    	    			if(Constant.DEBUG)  Log.d(TAG, "Going to play recorded url "+ url);
	    	    			if(!url.equalsIgnoreCase(currentPlayingUrl)){
	    	    				String base="";
	    	    				String protocol="";
	    	    				String chname="";
	    	    				String title="";
	    	    				String urltoplay="";
	    	    				String timestamp = "";
	    	    				int count=0;
	    	    				StringTokenizer st = new StringTokenizer(url, "/");
	    	    				while(st.hasMoreTokens()){
	    							if(count==0) protocol = st.nextToken();
	    							if(count==2) base = protocol+"//"+st.nextToken();
	    							if(count==3) chname = st.nextToken();
	    							if(count==4) timestamp = st.nextToken();
	    							if(count==5) title = st.nextToken();
	    							count++;
	    						}
	    	    				if(Constant.model.equalsIgnoreCase("S") ||  Constant.model.equalsIgnoreCase("X")){
	    							urltoplay = base + "/hlsvod" + "/" + chname + "/" + timestamp + "/" + title + "/index.m3u8";
	    						}else{
	    							urltoplay = base + "/dashvod" + "/" + chname + "/" + timestamp + "/" + title + "/manifest.mpd";
	    						}
	    	    				playVideoData(urltoplay);
	    	    			}else{
	    						if(progressDialog.isShowing()){
	    							progressDialog.dismiss();
	    						}
	    					}
	    	    		}
	    			}else if(func.equalsIgnoreCase("mediaController")){
	    				mediaController();
	    			}else if(func.equalsIgnoreCase("seekTo")){
	    				seekTo(seekto);
	    			}else if(func.equalsIgnoreCase("playPauseToggle")){
	    				playPauseToggle(state);
	    			}else if(func.equalsIgnoreCase("stopLiveTV")){
	    				stopAV();
	    			}else if(func.equalsIgnoreCase("doStartOver")){
	    				doStartOver();
	    			}else if(func.equalsIgnoreCase("timeShift")){
	    				timeShift(seekto);
	    			}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}		    		
	    	}
	    	if(extras.containsKey("Method")){
	    		if(Constant.DEBUG)  Log.d(TAG , "iViewReceiver in onNewIntent");
	    		
	    		String func = extras.getString("Method");
	    		if (func.equalsIgnoreCase("Fling")) {
	    			clicked = 1;
	    			if(direction.equalsIgnoreCase("UP")){ 
	    				if(Constant.DEBUG)  Log.d(TAG , "onNewIntent iViewReceiver UP ");
	    				activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								myWebView.loadUrl("javascript:com.lukup.eventHandler(\"swipeup\");");
							}
						});
	    			} else if (direction.equalsIgnoreCase("DOWN")){
	    				if(Constant.DEBUG)  Log.d(TAG , "onNewIntent iViewReceiver DOWN ");
	    				activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								myWebView.loadUrl("javascript:com.lukup.eventHandler(\"swipedown\");");
							}
						});
	    			} else if (direction.equalsIgnoreCase("RIGHT")){
	    				if(Constant.DEBUG)  Log.d(TAG , "onNewIntent iViewReceiver RIGHT ");
	    				activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								myWebView.loadUrl("javascript:com.lukup.eventHandler(\"swiperight\");");
							}
						});
	    			} else if (direction.equalsIgnoreCase("LEFT")){
	    				if(Constant.DEBUG)  Log.d(TAG , "onNewIntent iViewReceiver LEFT ");
	    				activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								myWebView.loadUrl("javascript:com.lukup.eventHandler(\"swipeleft\");");
							}
						});
	    			}
				}else if(func.equalsIgnoreCase("Select")){
					clicked = 1;
					if(Constant.DEBUG)  Log.d(TAG , "onNewIntent iViewReceiver SELECT ");		
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							myWebView.loadUrl("javascript:com.lukup.eventHandler(\"tap\");");
						}
					});
				}else if(func.equalsIgnoreCase("Back")){
		    		if(Constant.DEBUG)  Log.d(TAG , "onNewIntent iViewReceiver BACK ");
		    		myWebView.goBack();
		    		if(!AdFlag)
		    			closeAd();
		    	}
	    	}
	    }
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() throws NumberFormatException{
		super.onStart();
		if(Constant.DEBUG) Log.w(TAG, "onStart()");
		surfaceView = (SurfaceView)findViewById(R.id.surface_view);
		layout = (RelativeLayout)findViewById(R.id.layout);
		videoView = (VideoView)findViewById(R.id.videoPlayer);
		imageView = (ImageView)findViewById(R.id.image);
		myWebView = (WebView) findViewById(R.id.webview);
		
		String starttime ="";

		CacheData.setHomeVisibility("invisible");
		
//		Date currentTime = new Date(com.port.api.util.CommonUtil.getDateTime()+ 3*60*60*1000);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // the format of your date("2014-02-18T12:42:00.000Z")
//		unixDate = sdf.format(currentTime);
//		if(Constant.DEBUG)  Log.i(TAG, "formattedDate"+unixDate);
		
		CacheData.Play(true);
		
		//added 0n 25 Feb 2015
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
		
		progressDialog = new ProgressDialog(this,R.style.MyTheme);
		progressDialog.setCancelable(true);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
		progressDialog.show();
		
		videoView.setOnCompletionListener(this);
		
    	returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
		
		Bundle extras = this.getIntent().getExtras();
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
	    	
			if(extras.containsKey("Params")){
	    		try{
		    		functionData = extras.getString("Params");
		    		JSONObject jsonObj = new JSONObject(functionData);
		    		if(Constant.DEBUG)  Log.d(TAG, "jsonObj : "+jsonObj);
		    		if(jsonObj.has("id")){
		    			eventId = jsonObj.getString("id");
		    		}if(jsonObj.has("serviceid")){
		    			serviceId = jsonObj.getString("serviceid");
		    		}if(jsonObj.has("url")){
		    			url = jsonObj.getString("url").trim();
		    		}if(jsonObj.has("type")){
		    			type = jsonObj.getString("type");
		    		}if(jsonObj.has("subtype")){
		    			subtype = jsonObj.getString("subtype");
		    		}if(jsonObj.has("activity")){
		    			calledfrom = jsonObj.getString("activity");
		    		}if(jsonObj.has("pricingmodel")){
		    			pricingmodel = jsonObj.getString("pricingmodel");
		    		}if(jsonObj.has("starttime")){
		    			starttime = jsonObj.getString("starttime");
		    		}
	    		} catch(Exception e){
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}
	    		
	    		String extension = "";
	    		if (url != null && !url.equalsIgnoreCase("")) {
	    			extension = url.trim().substring(url.trim().lastIndexOf("."));
				}
	    		
	    		if(!eventId.equalsIgnoreCase("") && extension.equalsIgnoreCase(".html")){
	    			myWebView.setVisibility(View.VISIBLE);
		    		startiView(AdSourceUrl);
		    		interactive = true;
	    		}else if((!eventId.equalsIgnoreCase("")||!serviceId.equalsIgnoreCase(""))&&!extension.equalsIgnoreCase(".html")){
	    			if(mMediaControllTimer != null){
	    				mMediaControllTimer.cancel();
	    			}
	    			try{
//	    			status="true";
		    			if (type.equalsIgnoreCase("live")) {
		    				if (calledfrom.equalsIgnoreCase("DVBRemote")) {
								playDVBLiveData(Integer.parseInt(serviceId),type, starttime);
							}else{
								PlayOn(Integer.parseInt(serviceId),Integer.parseInt(eventId), url, type, starttime);
							}
						} else {
							if(!url.equalsIgnoreCase(currentPlayingUrl)){
	    					
								PlayOn(Integer.parseInt(serviceId),Integer.parseInt(eventId), url, type, starttime);
	    					}else{
	    						if(progressDialog.isShowing()){
	    							progressDialog.dismiss();
	    						}
//	    						mediaController();
	    					}
						}
	    			}catch(Exception e){
		    			e.printStackTrace();
		    			StringWriter errors = new StringWriter();
		    			e.printStackTrace(new PrintWriter(errors));
		    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		    		}
	    		}else if(eventId.equalsIgnoreCase("") && subtype.equalsIgnoreCase("video")){
//	    			status="true";
	    			if(!url.equalsIgnoreCase(currentPlayingUrl)){
	    				playVideoData(url);
	    			}else{
						if(progressDialog.isShowing()){
							progressDialog.dismiss();
						}
					}
	    		}else if(eventId.equalsIgnoreCase("") && subtype.equalsIgnoreCase("music")){
	    			if(!url.equalsIgnoreCase(currentPlayingUrl)){
	    				playVideoData(url);
	    			}else{
						if(progressDialog.isShowing()){
							progressDialog.dismiss();
						}
					}
	    		}else if(eventId.equalsIgnoreCase("") && subtype.equalsIgnoreCase("image")){
	    			videoView.setVisibility(View.GONE);
	    			imageView.setVisibility(View.VISIBLE);
	    			if(Constant.DEBUG) Log.e(TAG, "Showing imageView "+ url);
	    			imageView.setImageURI(Uri.parse(url));
	    			if(progressDialog.isShowing()){
						progressDialog.dismiss();
					}
	    			try{
		    			JSONObject resp = new JSONObject();
						JSONObject data = new JSONObject();
	
						returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
			    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
						data.put("state", "start");
						resp.put("params", data);		    	
						returner.send();
	    			} catch(Exception e){
		    			e.printStackTrace();
		    			StringWriter errors = new StringWriter();
		    			e.printStackTrace(new PrintWriter(errors));
		    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		    		}
	    		}else if(subtype.equalsIgnoreCase("recorded")){
	    			if(Constant.DEBUG)  Log.d(TAG, "Going to play recorded url "+ url);
	    			if(!url.equalsIgnoreCase(currentPlayingUrl)){
	    				String base="";
	    				String protocol="";
	    				String chname="";
	    				String title="";
	    				String urltoplay="";
	    				String timestamp = "";
	    				int count=0;
	    				StringTokenizer st = new StringTokenizer(url, "/");
	    				while(st.hasMoreTokens()){
							if(count==0) protocol = st.nextToken();
							if(count==2) base = protocol+"//"+st.nextToken();
							if(count==3) chname = st.nextToken();
							if(count==4) timestamp = st.nextToken();
							if(count==5) title = st.nextToken();
							count++;
						}
	    				if(Constant.model.equalsIgnoreCase("S") ||  Constant.model.equalsIgnoreCase("X")){
							urltoplay = base + "/hlsvod" + "/" + chname + "/" + timestamp + "/" + title + "/index.m3u8";
						}else{
							urltoplay = base + "/dashvod" + "/" + chname + "/" + timestamp + "/" + title + "/manifest.mpd";
						}
	    				playVideoData(urltoplay);
	    			}else{
						if(progressDialog.isShowing()){
							progressDialog.dismiss();
						}
					}
	    		}
	    	}	
		}
	}
	
    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
	protected void onPause() {
		super.onPause();
		if(Constant.DEBUG) Log.w(TAG, "onPause()");
    }
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		if (Constant.DEBUG) Log.w(TAG, "onStop()");
		if(!eventId.equalsIgnoreCase("")){
			endXML();
		}
	}
    
    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
    	super.onResume();
    	if(Constant.DEBUG) Log.w(TAG, "onResume()");
    };
    
    /* (non-Javadoc)
     * @see com.port.api.interactive.iView#onDestroy()
     */
    @Override
	public void onDestroy() {
		
		if(Constant.DEBUG) Log.w(TAG, "onDestroy()");
		
		if(videoView.isPlaying()){
			videoView.stopPlayback();
		}
		
		if(mMediaControllTimer != null){
			mMediaControllTimer.cancel();
		}
		
		if(adControlTimer != null){
			adControlTimer.cancel();
		}
		
		//added 0n 25 Feb 2015
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
		
		//exoplayer
		if(player!=null){
	      player.release();
	      player = null;
		}
		
		CacheData.Play(false);
		super.onDestroy();
	}
    
    /* (non-Javadoc)
     * @see android.app.Activity#finish()
     */
    @Override
	public void finish() {

    	if(videoView.isPlaying())
			videoView.stopPlayback();
    	//Added by Tomesh For ExoPlayer
    	if(player !=null && player.getPlayerControl().isPlaying())
			player.release();
		
    	//added 0n 25 Feb 2015
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
		
		super.finish();
	}
	
    
	/**
	 * ******************************PLAY*****************************.
	 *
	 * @param lcn the lcn
	 * @param type the type
	 */
    
    private void playDVBLiveData(int lcn,String type, String starttime) {
    	ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
		
		channelInfo = channelGateway.getServiceInfoByDvbLCN(lcn);
		PlayOn(channelInfo.getServiceId(), 0, "", type, starttime);
	}
    
	//consumer - network, eg, TV-HDMI, TV-Wifi, MusicSystem-A2DP
	//play media on dock if required, otherwise use consumer to send media link
	/**
	 * Play on.
	 * @param channelId the channel id
	 * @param eventId the event id
	 * @param url the url
	 * @param type the type
	 */
	private void PlayOn(int channelId, int eventId, String url, String type, String starttime){
		try{
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			imageView.setVisibility(View.GONE);
			
			CacheData.setCurrentServiceID(Integer.toString(channelId));
			CacheData.setCurrentEventID(Integer.toString(eventId));
			if(type.equalsIgnoreCase("live")){
				CacheData.setCurrentPricingmodel("PPC");
			}else{
				CacheData.setCurrentPricingmodel(pricingmodel);
			}
				
    		getChannelId(channelId, eventId);
    		ChannelId = channelId+"";
			if(Constant.DEBUG)  Log.d(TAG , "channelNumber: "+channelNumber+", ChannelId: "+ChannelId);
			if(Constant.DEBUG)  Log.d(TAG , "channelURl: "+url+", Channel Type: "+type);
			startXML("channel");
				
			JSONObject attribs = getAttributes(channelId, eventId);
			String isLock = ""; 
			String service_type = "";
			if(attribs.has("isLock")){
				if(attribs.getString("isLock")!=null && !attribs.getString("isLock").equalsIgnoreCase("")){
					isLock = attribs.getString("isLock");
					if(Constant.DEBUG)Log.i("getAttributes() :isLock", isLock);
				}
			}
			if(attribs.has("service_type")){
				if(attribs.getString("service_type")!=null && !attribs.getString("service_type").equalsIgnoreCase("")){
					service_type = attribs.getString("service_type");
					if(Constant.DEBUG)Log.i("getAttributes() :service_type", service_type);
				}
			}

//			CurrentTime = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).getTimeInMillis();
			CurrentTime = System.currentTimeMillis();
			String startdate = "";
			String duration = "";
			if(programInfo!=null){
				startdate = programInfo.getDate();
				duration = programInfo.getDuration();
			}
			if(Constant.DEBUG)Log.d(TAG, "Current time is " + CurrentTime + " , Program date is "+startdate+" ,and Program time to start is " + starttime);
			if(type.equalsIgnoreCase("live")){
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
				formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
				Date date = formatter.parse(startdate+" "+starttime);
				StartTime = date.getTime();
				if(Constant.DEBUG)Log.e(TAG, "Start time "+StartTime);
				StringTokenizer tokens = new StringTokenizer(duration, ":");
				int h = 0;
				int min = 0;
				int sec = 0;
				if (tokens.hasMoreTokens()) {
					h = Integer.parseInt(tokens.nextToken());
				}
				if(tokens.hasMoreTokens()){
					min = Integer.parseInt(tokens.nextToken());
				}
				if(tokens.hasMoreTokens()){
					sec = Integer.parseInt(tokens.nextToken());
				}
				if(Constant.DEBUG)  Log.d(TAG, "Duration Hours: "+h+", Minute: "+min);
				eventduration = ((((h*60)+min)*60)+sec)*1000;
			}
			
			if(type.equalsIgnoreCase("live") && url.equalsIgnoreCase("")){ //DVB or OTT
				
				//Added ServiceType to check Live or Radio channel by @kallol on 31 AUG 2015 
				if(Constant.DVB){ 
					if(Home.Zapping_status){
						
						if(isLock.equalsIgnoreCase("false")){
							
							playLiveData(service_type);
						}else{
							//let Player know of Lock status 
							if(Constant.DEBUG)Log.i("getAttributes() :LIVE isLock", isLock);
							data.put("type", "live");
							data.put("state", isLock);
							data.put("serviceid", ChannelId);
							resp.put("params", data);
							returner.set(Listener.pname, pnetwork, caller); 
							returner.add(method+"lockStatus", resp,"messageActivity");
							returner.send();
						}
					}
					else{
						JSONObject sendResponse = new JSONObject();
						JSONObject datasend = new JSONObject();
						returner.set(Listener.pname, pnetwork, "com.player.NotificationsService");
						datasend.put("result", "success");
						datasend.put("msg", getResources().getString(R.string.TUNERERROR));
						sendResponse.put("params", datasend);
						returner.add("com.port.apps.epg.Play.PlayOn", sendResponse,"startService");
						returner.send();
						Toast.makeText(Port.c, R.string.TUNERERROR, Toast.LENGTH_LONG).show();
					}
				}else { //OTT
					//let Player know of Lock status 
					if(Constant.DEBUG)Log.i("getAttributes() :LIVE isLock", isLock);
					data.put("type", "live");
					data.put("state", isLock);
					data.put("serviceid", ChannelId);
					resp.put("params", data);
					returner.set(Listener.pname, pnetwork, "com.player.apps.PlayBack"); 
					returner.add(method+"lockStatus", resp,"messageActivity");
					returner.send();
					
					if(isLock.equalsIgnoreCase("false")){
						playLiveData(service_type);
					}
					
				}
				
			}else{
				String extension = "";
				if(attribs.has("extension")){
					if(attribs.getString("extension")!=null && !attribs.getString("extension").equalsIgnoreCase("")){
						extension = attribs.getString("extension");
					}
				}
				String event_category = "";
				if(attribs.has("event_category")){
					if(attribs.getString("event_category")!=null && !attribs.getString("event_category").equalsIgnoreCase("")){
						event_category = attribs.getString("event_category");
					}
				}		
				if(Constant.DEBUG)  Log.d(TAG , "isLock: "+isLock+", extension: "+extension+", event_category: "+event_category);
				
				adInfo = checkingForAds(ChannelId+"",eventId);				
				if(url != null && !url.equalsIgnoreCase("")){// && !type.equalsIgnoreCase("personal")){
					if(Constant.DEBUG)  Log.d(TAG , "url to play "+ url);
					this.sourceUrl = url;
					
					base="";
					String protocol="";
					chname="";
					String title="";
//					String urltoplay="";
					int count=0;
					if(!extension.equalsIgnoreCase(".mp4")){
						StringTokenizer st = new StringTokenizer(sourceUrl, "/");
						while(st.hasMoreTokens()){
							if(count==0) protocol = st.nextToken();
							if(count==2) base = protocol+"//"+st.nextToken();
							if(count==3) chname = st.nextToken();
							if(count==4) title = st.nextToken();
							count++;
						}
					}
					int x = new BigInteger(String.valueOf(CurrentTime)).compareTo(new BigInteger(String.valueOf(StartTime+eventduration)));
					if(Constant.DEBUG)  Log.d(TAG , "Current time "+ (CurrentTime/(1000)) + " Start Time " + (StartTime/(1000)) + " Duration " + eventduration + " Stop time " + ((StartTime/1000)+eventduration));
					if(Constant.model.equalsIgnoreCase("X1")){
						if(type.equalsIgnoreCase("live")){
							if(x==1){
//							if((CurrentTime/1000) > ((StartTime/1000) +(eventduration/1000))){
								Uri u = Uri.parse(base+ "/dash/" + chname + "/manifest.mpd?")
										.buildUpon()
										.appendQueryParameter("starttime", String.valueOf(StartTime))
										.appendQueryParameter("stoptime", String.valueOf(StartTime+eventduration))
										.build();
								sourceUrl = u.toString();
//								sourceUrl = base + "/dash/" + chname + "/manifest.mpd?starttime="+StartTime+"&stoptime="+CurrentTime;
							}else{
								sourceUrl = base + "/dash/" + chname + "/manifest.mpd";
							}
						}else if(type.equalsIgnoreCase("vod")){ //vod
							if(extension.equalsIgnoreCase(".mp4")){
								sourceUrl = url;
							}else{
								sourceUrl = base + "/dash/" + chname + "/" + title + "/manifest.mpd";
							}
						}else{
							sourceUrl = url;
						}
					}else{ //if S or X
						if(type.equalsIgnoreCase("live")){
							if(x==1){
//							if((CurrentTime/1000) > ((StartTime/1000)+(eventduration/1000))){
								sourceUrl = base + "/hls/" + chname + "/index.m3u8?starttime="+StartTime+"&stoptime="+(StartTime+eventduration);
							}else{
								sourceUrl = base + "/hls/" + chname + "/index.m3u8";
							}
						}else if(type.equalsIgnoreCase("vod")){ //vod
							if(extension.equalsIgnoreCase(".mp4")){
								sourceUrl = url;
							}else{
								sourceUrl = base + "/widevine/" +chname + "/" + title + ".wvm";
							}
						}else{
							sourceUrl = url;
						}
					}
					if(Constant.DEBUG)  Log.d(TAG , "source url to play "+ sourceUrl);
					DRMUrl = sourceUrl.trim().substring(sourceUrl.trim().lastIndexOf("."));
					
					if(Constant.DEBUG)  Log.d(TAG , "DRM extension : "+DRMUrl+", AdFlag: "+AdFlag);
				}
				
				if(isLock.equalsIgnoreCase("false")){
					//switchover
					if(!Constant.model.equalsIgnoreCase("X1")){ //DVB or OTT
						if(Port.hdmi==1){
	//						if(videoView.isPlaying()){
	//		    				videoView.stopPlayback();
	//		    			}
			    		} else {
			    			Port.nativeHdmi.CVBSSwitch(97,1,0);
			    		}
					}
					
					if(Constant.DVB){ //stop previously running live DVB stream
						Port.guiMwDvb.StopAvService();
					}
					
					if(sourceUrl != null && !sourceUrl.equalsIgnoreCase("")){ //reconfirm that it is VOD				
						if(AdFlag){
							if(Constant.DEBUG)  Log.i(TAG , "sourceUrl: "+sourceUrl);
							if(adInfo != null){
					    		AdType = adInfo.getType();
					    		AdSourceUrl = adInfo.getWidgetUrl();
						    	
						    	if(Constant.DEBUG)  Log.i("PlayOn()" , "Type: "+AdType+", AdSourceUrl: "+AdSourceUrl);

						    	if(AdType.equalsIgnoreCase("Network")){
						    		if (AdSourceUrl.contains("[CACHE_BREAKER]")){
						        		Random aRandom = new Random();
						    		    String randomNum = ""+(aRandom.nextInt(100000)+1);
						    		    AdSourceUrl = AdSourceUrl.replace("[CACHE_BREAKER]", randomNum);
						            }
						    		
									new XMLParsingDoingInBackground(AdSourceUrl).execute();
						    	}else if(AdType.equalsIgnoreCase("Overlay")){
						    		Log.d("Adtype()" , "Type: inside Overlay AdSourceUrl");
						    		myWebView.setVisibility(View.VISIBLE);
						    		startiView(AdSourceUrl);
						    		
						    	}else{	 //regular video ads
						    		Log.d("regular()" , "Type: inside regular video ads"+AdSourceUrl);
						    		String extention = AdSourceUrl.trim().substring(AdSourceUrl.trim().lastIndexOf("."));
						    		Log.d("regular()" , "Type:inside regular video adsextention" + extention);
						    		if (extention != null && extention.equalsIgnoreCase(".mp4")) {
						    			playVideoData(AdSourceUrl);
									} else if(extention.equalsIgnoreCase(".wvm")){								
										new DrmManagerImpl(AdSourceUrl).execute();
									} else if(extention.equalsIgnoreCase(".mpd")){
										if(Constant.DEBUG)  Log.d("AdSourceUrl", "LukupExo-Player" +AdSourceUrl);
										lukupExoPlayer(AdSourceUrl);
									}
						    	}
							}
						}else{
							if(Constant.DEBUG)  Log.d(TAG , "sourceUrl: "+sourceUrl);

							if(DRMUrl.contains(".wvm")){								
								new DrmManagerImpl(sourceUrl).execute();														
							}else if(DRMUrl.contains(".mpd")){
								if(Constant.DEBUG)  Log.i(TAG, "LukupExo-Player");
								lukupExoPlayer(sourceUrl);
							}else if(extension.contains(".mp4")){ 						
								playVideoData(sourceUrl);
							}else if(DRMUrl.contains(".m3u8")){
								if(type.equalsIgnoreCase("vod")){
									playVideoData(sourceUrl);
								}else if(type.equalsIgnoreCase("live")){
									playVideoData(sourceUrl);
								}
							}else if(event_category.equalsIgnoreCase("image")){
								videoView.setVisibility(View.GONE);
				    			imageView.setVisibility(View.VISIBLE);
				    			if(Constant.DEBUG) Log.e(TAG, "Showing imageView "+ sourceUrl);
				    			imageView.setImageURI(Uri.parse(sourceUrl));
				    			if(progressDialog.isShowing()){
									progressDialog.dismiss();
								}
				    			try{
									returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
						    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
									data.put("state", "start");
									resp.put("params", data);		    	
									returner.send();
				    			} catch(Exception e){
					    			e.printStackTrace();
					    			StringWriter errors = new StringWriter();
					    			e.printStackTrace(new PrintWriter(errors));
					    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
					    		}
							}
						}
						
						
					}
//					else if(sourceUrl != null && !sourceUrl.equalsIgnoreCase("")){ //personal
//						if(Constant.DEBUG)  Log.i("playing =========================" , "sourceUrl: "+sourceUrl);
//						if(event_category.equalsIgnoreCase("videos") && extension.equalsIgnoreCase(".mp4")){
//							playVideoData(sourceUrl);
//						}else if(event_category.equalsIgnoreCase("image")){
//							videoView.setVisibility(View.GONE);
//			    			imageView.setVisibility(View.VISIBLE);
//			    			if(Constant.DEBUG) Log.e(TAG, "Showing imageView "+ sourceUrl);
//			    			imageView.setImageURI(Uri.parse(sourceUrl));
//			    			if(progressDialog.isShowing()){
//								progressDialog.dismiss();
//							}
//			    			try{
//								returner.set(Listener.pname, "BT", "com.player.UpdateService"); 			
//					    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
//								data.put("state", "start");
//								resp.put("params", data);		    	
//								returner.send();
//			    			} catch(Exception e){
//				    			e.printStackTrace();
//				    			StringWriter errors = new StringWriter();
//				    			e.printStackTrace(new PrintWriter(errors));
//				    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
//				    		}
//						}
//					}	
				}else{
					//let Player know of lock status
					data.put("type", type);
					data.put("state", isLock);
					data.put("serviceid", ChannelId);
					resp.put("params", data);
					returner.set(Listener.pname, pnetwork, caller); 
					returner.add(method+"lockStatus", resp,"messageActivity");
					returner.send();
				}
			}			
			
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}
	
	
// LukupExo Player 29July
	/**
 * Lukup exo player.
 *
 * @param sourceUrl the source url
 */
	public void lukupExoPlayer(String sourceUrl){
			
		if(videoView.isPlaying()){
			videoView.stopPlayback();
			Log.d(TAG, "stoping Video view in Exoplayer");
		}
	
		if (player != null) {
			player.release();
		}
		currentPlayingUrl = sourceUrl;
		if(Constant.DEBUG)  Log.d(TAG, "Going to play in exoplayer " + sourceUrl);
		surfaceView = (SurfaceView)findViewById(R.id.surface_view);
		surfaceView.getHolder().addCallback(this);
		videoView.setVisibility(View.INVISIBLE);
		surfaceView.setVisibility(View.VISIBLE);	
	    String userAgent = DemoUtil.getUserAgent(this);////http://ec2-174-129-78-19.compute-1.amazonaws.com/dark_knight/DARK_KNIGHT.mpd
		//"http://ec2-54-237-119-34.compute-1.amazonaws.com:8080/movie/dark_knight/DARK_KNIGHT.mpd"
//	 		    player = new LukupPlayer(new DashRendererBuilder(userAgent,sourceUrl,"",new WidevineTestMediaDrmCallback(""), null)); 
		if(DRMUrl.contains(".mpd")){
			player = new LukupPlayer(new DashRendererBuilder(this, userAgent, sourceUrl, new WidevineTestMediaDrmCallback("","")));
		}else if(DRMUrl.contains(".m3u8")){
			player = new LukupPlayer(new HlsRendererBuilder(this, userAgent, sourceUrl));
		}
		player.addListener(this);
		player.prepare();
		if(Constant.DEBUG)  Log.d(TAG, "Preparing ExoPlayer ");
        player.setSurface(surfaceView.getHolder().getSurface());
	    player.setPlayWhenReady(true);
		if(Constant.DEBUG)  Log.d(TAG, "Loading.........Playing in Exoplayer ");
		mediaController();
		
	}
	
	/* (non-Javadoc)
	 * @see com.port.api.interactive.iView#playVideoData(java.lang.String)
	 */
	public void playVideoData(final String mMediaUri){
		if(Constant.DEBUG)  Log.d(TAG, "playVideoData mMediaUri: "+mMediaUri);
		try{
			imageView.setVisibility(View.GONE);
			currentPlayingUrl = mMediaUri;
			if (mMediaUri.contains("de4m7c0oq5b1u.cloudfront.net")) {
				if(Constant.DEBUG)  Log.i("SignedUrlActivity", "its a SignedUrl");
				new PlayWithEncryption(mMediaUri,".mp4").execute();
			}else{
				if(Constant.DEBUG)  Log.i(TAG, "Not a SignedUrl");
				if(activity != null ){
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(currentPlayingUrl.substring(currentPlayingUrl.lastIndexOf(".")).equalsIgnoreCase(".mp3")){
								if(Consumer.mp!=null && Consumer.mp.isPlaying()){
									Consumer.mp.stop();		
									Consumer.mp.release();
								}
							}
							if(videoView.isPlaying()){
								videoView.stopPlayback();
							}
							surfaceView.setVisibility(View.INVISIBLE);
							videoView = (VideoView)findViewById(R.id.videoPlayer);
							videoView.setVisibility(View.VISIBLE);
							videoView.setVideoURI(Uri.parse(mMediaUri));
							videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
								@SuppressWarnings("deprecation")
								@Override
								public void onPrepared(MediaPlayer pMp) {
									try{
										if(Constant.DEBUG)  Log.d(TAG , "mMediaUri: "+mMediaUri+", ChannelId: "+ChannelId);
										
										JSONObject resp = new JSONObject();
										JSONObject data = new JSONObject();
		
										returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
							    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
										data.put("state", "start");
										resp.put("params", data);		    	
										returner.send();
										
										videoView.requestFocus();
										videoView.start();
									
										if(Constant.DEBUG)  Log.d(TAG, "playVideoData: "+videoView.isPlaying());

									}catch(Exception e){
										e.printStackTrace();
										StringWriter errors = new StringWriter();
										e.printStackTrace(new PrintWriter(errors));
										SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
									}		
									
									mediaController();
									if(pageType.equalsIgnoreCase("interactive")){
//										myWebView.setVisibility(View.VISIBLE);
										adControlTimer = new Timer("adcontroller",true);
										adControlTimer.scheduleAtFixedRate(new TimerTask() {
											@Override
											public void run() {
												if(Constant.DEBUG)  Log.d(TAG, "AdControlTimer Running");
												if(videoView != null && videoView.isPlaying()){
													position = 0;
													startAdController();
												}
											}
										}, 0, 1000);									
									}
									if(progressDialog.isShowing()){
										progressDialog.dismiss();
									}
								}
							});
						}
					});
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}
	
	/**
	 * Gets the attributes.
	 *
	 * @param ChannelId the channel id
	 * @param eventId the event id
	 * @return the attributes
	 */
	private JSONObject getAttributes(int ChannelId, int eventId){
		JSONObject data = new JSONObject();
		try{
			if(Constant.DEBUG)  Log.d(TAG,"getAttributes().eventId "+eventId);
			if(Constant.DEBUG)  Log.d(TAG,"getAttributes().ChannelId "+ChannelId);
			
			ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			
			if(eventId>0){
				ProgramInfo selectedEventInfo = programGateway.getProgramInfoByEventId(eventId);
				if(selectedEventInfo != null){
				data.put("event_category", selectedEventInfo.getEventCategory());
				data.put("url", selectedEventInfo.getEventSrc());
				}
			}
			
			ChannelInfo chlInfo = channelGateway.getServiceInfoByServiceId(ChannelId);
			
			if (chlInfo!=null) {
				if(Attributes.isServiceLocked(ChannelId)){
					data.put("isLock", "true");
				} else {
					data.put("isLock", "false");
				}
				data.put("service_type", chlInfo.getType());
				data.put("service_category", chlInfo.getServiceCategory());
				if(url != null && !url.equalsIgnoreCase("")){
					data.put("extension", url.trim().substring(url.trim().lastIndexOf(".")));
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
		return data;		
	}	

	
	/**
	 * Play live data.
	 *
	 * @param type the type
	 */
	private void playLiveData(final String type){
		if(activity != null ){
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if(videoView.isPlaying()){
							videoView.stopPlayback();
						}
						//Added by tomesh for switch from vod to Live
						/*videoView.setVisibility(View.GONE);
						surfaceView.setVisibility(View.GONE);*/
						//currentPlayingUrl ="";
						if(Constant.DEBUG)  Log.d(TAG, "Playing live data called");
						if(!Constant.model.equalsIgnoreCase("X1")){
							if(Port.hdmi==1){
								if(Constant.DEBUG)Log.i(TAG +"playLiveData", "Port.hdmi ===========1 DVB"+Port.hdmi);
								if (Constant.DVB) { //DVB Middleware
									int errorCode = -1;
									try {
										Port.guiMwDvb.SelectAvService(Integer.parseInt(ChannelId)); //DVB Middleware
									
										JSONObject resp = new JSONObject();
										JSONObject data = new JSONObject();
		
										returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
							    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
										data.put("state", "start");
										resp.put("params", data);		    	
										returner.send();
									}catch(Exception e){
										e.printStackTrace();
										StringWriter errors = new StringWriter();
										e.printStackTrace(new PrintWriter(errors));
										SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_DVBMIDDLE, errorCode+"", SystemLog.DVBMiddleError(errorCode));
									}
								}else{ //OTT
									try {
										Uri videoUri = Uri.parse("brcm:hdmi_in");
										if(videoUri != null){
											if(Constant.DEBUG) Log.d(TAG, "Playing HDMI called :videoUri : "+videoUri.toString());
											videoView.setVideoURI(videoUri);
											videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
												@Override
												public void onPrepared(MediaPlayer pMp) {
													try{
														JSONObject resp = new JSONObject();
														JSONObject data = new JSONObject();
						
														returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
											    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
														data.put("state", "start");
														resp.put("params", data);		    	
														returner.send();
													}catch(Exception e){
														e.printStackTrace();
														StringWriter errors = new StringWriter();
														e.printStackTrace(new PrintWriter(errors));
														SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
													}
													videoView.requestFocus();
													videoView.start();
													if(Constant.DEBUG)  Log.d(TAG, "playLiveData: "+videoView.isPlaying());
												}
											});
										}
									}catch(Exception e){
										e.printStackTrace();
										StringWriter errors = new StringWriter();
										e.printStackTrace(new PrintWriter(errors));
										SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_HDMI, errors.toString(), e.getMessage());
									}
								}
							} else {
								if(Constant.DEBUG)  Log.d(TAG, "Playing live data in CVBS");
								try{	if(Constant.DEBUG)Log.i(TAG +"playLiveData", "Port.hdmi ===========0 DVB");
									Port.nativeHdmi.CVBSSwitch(97,1,1);
									if (Constant.DVB) { //DVB Middleware
										int errorCode = -1;
										try {
											Port.guiMwDvb.SelectAvService(Integer.parseInt(ChannelId)); //DVB Middleware
										}catch(Exception e){
											e.printStackTrace();
											StringWriter errors = new StringWriter();
											e.printStackTrace(new PrintWriter(errors));
											SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_DVBMIDDLE, errorCode+"", SystemLog.DVBMiddleError(errorCode));
										}
									}
									
									try{
										JSONObject resp = new JSONObject();
										JSONObject data = new JSONObject();
										if(Constant.DEBUG)  Log.d(TAG, "Create json data in CVBS");
										returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
							    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
										data.put("state", "start");
										resp.put("params", data);		    	
										returner.send();
									}catch(Exception e){
										e.printStackTrace();
										StringWriter errors = new StringWriter();
										e.printStackTrace(new PrintWriter(errors));
										SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
									}
									
								}catch(Exception e){
									e.printStackTrace();
									StringWriter errors = new StringWriter();
									e.printStackTrace(new PrintWriter(errors));
									SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_CVBS, errors.toString(), e.getMessage());
								}
							}
						}
						if(progressDialog.isShowing()){
							progressDialog.dismiss();
						}				

					} catch (Exception e) {
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
					}
				}
			});
		}
	}
	
	
	/**
	 * Stop av.
	 */
	private void stopAV(){
		if(Constant.DEBUG)  Log.d(TAG, "stopLiveTV");
		if(activity != null ){
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						if(mMediaControllTimer != null){
							mMediaControllTimer.cancel();
						}
						if(Constant.DEBUG)  Log.d(TAG, "stopLiveTV "+videoView.isPlaying());
						
						JSONObject resp = new JSONObject();
						JSONObject data = new JSONObject();

						returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
			    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
						data.put("state", "stop");
						resp.put("params", data);		    	
						returner.send();
						
						if(videoView.isPlaying()){
							if(Constant.DEBUG)  Log.d(TAG, "stopLiveTV "+videoView.isPlaying());
							videoView.stopPlayback();
						}
						
						//Added by Tomesh For ExoPlayer
						if(player!=null && player.getPlayerControl().isPlaying()){
							if(Constant.DEBUG)  Log.d(TAG, "stopLiveTV "+player.getPlayerControl().isPlaying());
							player.release();
						}
						
						if(progressDialog.isShowing()){
							progressDialog.dismiss();
						}		
						
						adTotalDuration = pgTotalDuration;
						adEndTime = pgEndTime;
						
						if (type.equalsIgnoreCase("live")) {
							long endTime = System.currentTimeMillis();
							if(Constant.DEBUG)  Log.d(TAG, "stopLiveTV endTime: "+endTime);
//							pgEndTime = endTime - CurrentTime;
							pgEndTime = CurrentTime;
							if(Constant.DEBUG)  Log.d(TAG, "stopLiveTV pgEndTime: "+pgEndTime);
							if (programInfo != null) {
								if(Constant.DEBUG)  Log.d(TAG, "stopLiveTV pgEndTime: "+programInfo.getDuration());
								StringTokenizer tokens = new StringTokenizer(programInfo.getDuration(), ":");
								int h = 0;
								int min = 0;
								int sec = 0;
								while (tokens.hasMoreTokens()) {
									h = Integer.parseInt(tokens.nextToken());
									min = Integer.parseInt(tokens.nextToken());
									sec = Integer.parseInt(tokens.nextToken());
									if(Constant.DEBUG)  Log.d(TAG, "Hours: "+h+", Minute: "+min + "Seconds: "+ sec);
								}
								
								pgTotalDuration = ((h*60)+min)*60*1000;
							}
						}
						if(Constant.model.equalsIgnoreCase("X1")){
//							if(Port.hdmi==1){
//								if(Constant.DEBUG)Log.i(TAG, "stopAV Port.hdmi ===========1 X1");
//								Play.this.finish();
//							}else {
								if(Constant.DEBUG)Log.i(TAG, "Port.hdmi ===========0 X1");
								Play.this.finish();
//							}
						}else{ //S or X
							if(Constant.DVB){ //check if DVB is true in S or X
								if(Port.hdmi==1){
									if(Constant.DEBUG)Log.i(TAG, "stopAV Port.hdmi ===========1 DVB");
									Port.guiMwDvb.StopAvService();
									Play.this.finish();
								}
								else{ //if component video, stop AV and then switch to video view
									if(Constant.DEBUG)Log.i(TAG, "stopAV Port.hdmi ===========0 DVB");
									Port.guiMwDvb.StopAvService();
									Port.nativeHdmi.CVBSSwitch(97,1,0);
									Play.this.finish();
								}
							}else{ //if OTT
								if(Port.hdmi==1){
									if(Constant.DEBUG)Log.i(TAG, "stopAV Port.hdmi ===========1 OTT");
									Play.this.finish();
								}
								else{ //if component video, stop AV and then switch to video view
									if(Constant.DEBUG)Log.i(TAG, "stopAV Port.hdmi ===========0 OTT");
									Port.nativeHdmi.CVBSSwitch(97,1,0);
									Play.this.finish();
								}
							}
						}
					}catch(Exception e){
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
					}
				}
			});
		}
	}
	
	/**
	 * Gets the channel id.
	 *
	 * @param serviceId the service id
	 * @param eventId the event id
	 * @return the channel id
	 */
	private void getChannelId(int serviceId, int eventId){
		try{	
			if(Constant.DEBUG)Log.i("getChannelId() ServiceID : "+serviceId,"getChannelId() eventID : "+eventId);
			ChannelGateway channelGateway = new ChannelGateway(Port.c.getApplicationContext()) ;
			ProgramGateway programGateway = new ProgramGateway(Port.c.getApplicationContext()) ;
			StatusGateway statusGateway = new StatusGateway(Port.c.getApplicationContext()) ;
			
			if(eventId>0){
				programInfo = programGateway.getProgramInfoByEventId(eventId);
				StatusInfo likeInfo = statusGateway.getInfoByType(eventId, 1, "event");
				StatusInfo recordInfo = statusGateway.getInfoByType(eventId, 4, "event");
				if (likeInfo != null && recordInfo != null) {
					programAction = "Like,Record";
				} else if (likeInfo != null){
					programAction = "Like";
				} else if (recordInfo != null){
					programAction = "Record";
				}
			}
			if(Constant.DEBUG)  Log.d(TAG, "programAction: "+programAction);
			channelInfo = channelGateway.getServiceInfoByServiceId(serviceId);
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}
	
	/**
	 * **************************Action****************************************.
	 */
	private void startMediaControllerForVod(){
		try{
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			if(videoView != null){
				if(Constant.DEBUG)  Log.d(TAG, "MediaControlTimer Running CurrentPosition: " + videoView.getCurrentPosition());
				if(Constant.DEBUG)  Log.d(TAG, "MediaControlTimer Running Duration: " + videoView.getDuration());
				data.put("position", videoView.getCurrentPosition());
				data.put("duration", videoView.getDuration());
				pgEndTime = videoView.getCurrentPosition();
				pgTotalDuration = videoView.getDuration();
				
			}
			String state = "play";
			if(videoView != null){
				if(videoView.isPlaying()){
					state = "play";
				}else{
					state = "pause";
				}
			}
			data.put("type","video");
			data.put("state",state);
			resp.put("params",data);
			if(Constant.DEBUG)  Log.d(TAG, "Current time : "+ videoView.getCurrentPosition() + " ,Total duration : " + videoView.getDuration());
    		returner.set(producer, pnetwork, "com.player.apps.PlayBack" ); 			
			returner.add("com.port.apps.epg.Play.mediaController", resp,"messageActivity");
			returner.send();
			
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}	
	
	//Added by @Tomesh 29 july 	
		/**
	 * Start media controller exo player.
	 */
	public void startMediaControllerForLive()
	{	try{
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			cTime+=5000;
			data.put("position", cTime);
			data.put("duration", eventduration);

			String state = "play";
			if(player != null){
				if(player.getPlayerControl().isPlaying()){
					state = "play";
				}else{
					state = "pause";
				}
			}
			if(videoView != null){
				if(videoView.isPlaying()){
					state = "play";
				}else{
					state = "pause";
				}
			}
			data.put("type","video");
			data.put("state",state);
			resp.put("params",data);
			if(Constant.DEBUG)  Log.d(TAG, "Current time : "+ cTime + " ,Total duration : " + eventduration);
    		returner.set(producer, pnetwork, "com.player.apps.PlayBack"); 			
			returner.add("com.port.apps.epg.Play.mediaController", resp,"messageActivity");
			returner.send();
			
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}
	
	
	//updates media controller when media is playing on dock
	/**
	 * Media controller.
	 */
	private void mediaController(){
		if(mMediaControllTimer != null){
			mMediaControllTimer.cancel();
		}

		mMediaControllTimer = new Timer("mediacontroller",true);
		if(CurrentTime >(StartTime+eventduration)){
			cTime = 0;
		}else{
			cTime = CurrentTime-StartTime;
		}
		mMediaControllTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(videoView != null && videoView.isPlaying()){
					if(Constant.DEBUG)  Log.d(TAG, "MediaControlTimer Running: "+videoView.isPlaying());
					if(type.equalsIgnoreCase("live")){
						startMediaControllerForLive();
					}else{
						startMediaControllerForVod();
					}
				}else if(player!=null && player.getPlayerControl().isPlaying()){ // Added 29 @Tomesh July Exoplayer
					if(Constant.DEBUG)  Log.d(TAG, "Exoplayer MediaControlTimer Running: "+player.getPlayerControl().isPlaying());
					startMediaControllerForLive();
				}
			}
		}, 0, 5000);
	}
	
	private void timeShift(final String seekto){ //used for live only
		try{
			final Activity instance = activity;
			if(Constant.DEBUG)Log.e(TAG, "Time shifting by % " + seekto);
			instance.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int persentage = Integer.parseInt(seekto);
					int milSec = progressToTimer(persentage, (int)(long)eventduration);						
					if(Constant.DEBUG)  Log.d(TAG, "LukupExoPlayer after rewind position : "+milSec);
					
					if(!progressDialog.isShowing()){
						progressDialog.show();
					}
					if(player!=null){
						if(Constant.DEBUG)Log.e(TAG, "Timeshifting on url : "+base + "/dash/" + chname + "/index.mpd" + " ,by " + (CurrentTime-milSec));
						lukupExoPlayer(base + "/dash/" + chname + "/index.mpd?starttime=-"+ milSec);
					}else{
						playVideoData(base + "/hls/" + chname + "/index.m3u8?startime=-"+ milSec);
					}
					if(progressDialog.isShowing()){
						progressDialog.dismiss();
					}
				}
			});
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}
	
	
	/**
	 * Seek to.
	 *
	 * @param seekto the seekto
	 */
	private void seekTo(final String seekto) { //used for vod only
		try{
			final Activity instance = activity;
			if(Constant.DEBUG)  Log.d(TAG, "seekTo() : "+seekto);
			if(videoView != null && videoView.isPlaying()) {
				if(Constant.DEBUG)  Log.d(TAG, "Timeshifting in video view");
				instance.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						int milSec=0;
						if(seekto != null && !seekto.equalsIgnoreCase("")){
							int persentage=0;
							try{
								persentage = Integer.parseInt(seekto);
								if(Constant.DEBUG)  Log.d(TAG, "after persentage : "+persentage);
							}catch(Exception e){
								e.printStackTrace();
							}
							milSec = progressToTimer(persentage, videoView.getDuration());
						}
						if(Constant.DEBUG)  Log.d(TAG, "after position : "+milSec);
						if(!progressDialog.isShowing()){
							progressDialog.show();
						}
						videoView.seekTo(milSec);
						if(progressDialog.isShowing()){
							progressDialog.dismiss();
						}
					}
				});
	
			}
//			else if(player != null && player.getPlayerControl().isPlaying()) {
//				if(Constant.DEBUG)  Log.d(TAG, "Timeshifting in exoplayer");
//				instance.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						int milSec = player.getPlayerControl().getCurrentPosition();
//						if(Constant.DEBUG)  Log.d(TAG, "LukupExoPlayer before rewind CurrentPosition : "+milSec);
//						if(seekto != null && !seekto.equalsIgnoreCase("")){
//							int persentage = getProgressPercentage(player.getPlayerControl().getCurrentPosition(), player.getPlayerControl().getDuration());
//							if(Constant.DEBUG)  Log.d(TAG, "before persentage : "+persentage);
//							try{
//								persentage = Integer.parseInt(seekto);
//								if(Constant.DEBUG)  Log.d(TAG, "after persentage : "+persentage);
//							}catch(Exception e){
//								e.printStackTrace();
//							}
////							milSec = progressToTimer(persentage, player.getPlayerControl().getDuration());
//							milSec = progressToTimer(persentage, (int)(StartTime+eventduration));
//						}
//						if(Constant.DEBUG)  Log.d(TAG, "after position : "+milSec);
//						if(!progressDialog.isShowing()){
//							progressDialog.show();
//						}
//						if(milSec > player.getPlayerControl().getCurrentPosition()){ //going forward
//							if(player.getPlayerControl().canSeekForward()){
//								player.getPlayerControl().seekTo(milSec);
//							}
//						}else{ //rewinding
//							if(player.getPlayerControl().canSeekBackward()){
//								player.getPlayerControl().seekTo(milSec);
//							}
//						}
//						if(progressDialog.isShowing()){
//							progressDialog.dismiss();
//						}
//					}
//				});
//	
//			}
			else {
				JSONObject sendResponse = new JSONObject();
				JSONObject data = new JSONObject();
				returner.set(producer, pnetwork, "com.player.apps.PlayBack" ); 
				data.put("result", "failure");
				data.put("msg", this.getResources().getString(R.string.UNKNOWN_ERROR));
				sendResponse.put("params", data);
				returner.add(method+"seekTo", sendResponse,"messageActivity");
				returner.send();
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}
	
	
	/**
	 * Play pause toggle.
	 *
	 * @param state the state
	 */
	private void playPauseToggle(String state) {
		try{if(Constant.DEBUG)  Log.d(TAG, "playPauseToggle-State--" + state);
			if(state.equalsIgnoreCase("play")){
				if (videoView != null && !(videoView.isPlaying())) {
					if(Constant.DEBUG)  Log.d(TAG, "playPauseToggle-videoView-State--" + state);
					activity.runOnUiThread(new  Runnable() {
						@Override
						public void run() {
							videoView.start();
						}
					});
				} 
//				if (player != null && !(player.getPlayerControl().isPlaying())) { //Added By Tomesh 29 July ExoPlayer
//					if(Constant.DEBUG)  Log.d(TAG, "playPauseToggle-Exoplayer-Play");
//					activity.runOnUiThread(new  Runnable() {
//						@Override
//						public void run() {
//							player.getPlayerControl().start();
//						}
//					});
//				}
				else {
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					returner.set(producer, pnetwork, "com.player.apps.PlayBack" ); 
					data.put("result", "failure");
					sendResponse.put("params", data);
					returner.add(method+"playPauseToggle", sendResponse,"messageActivity");
					returner.send();
				}
			}else if(state.equalsIgnoreCase("pause")){
				if (videoView != null && videoView.canPause()) {
					activity.runOnUiThread(new  Runnable() {
						@Override
						public void run() {
							if(videoView.isPlaying())
								videoView.pause();
						}
					});
	
				} 
//				else if (player != null && player.getPlayerControl().canPause()) { //Added By Tomesh 29 July ExoPlayer
//					if(Constant.DEBUG)  Log.d(TAG, "playPauseToggle-Exoplayer-pause");
//					activity.runOnUiThread(new  Runnable() {
//						@Override
//						public void run() {
//							if(player.getPlayerControl().isPlaying())
//								player.getPlayerControl().pause();
//						}
//					});
//					
//				} 
				else {
					JSONObject sendResponse = new JSONObject();
					JSONObject data = new JSONObject();
					returner.set(producer, pnetwork, caller); 
					data.put("result", "failure");
					sendResponse.put("params", data);
					returner.add(method+"playPauseToggle", sendResponse,"messageActivity");
					returner.send();
				}
			}
//			else if(state.equalsIgnoreCase("stop")){
//				if(videoView!=null){
//					activity.runOnUiThread(new  Runnable() {
//						@Override
//						public void run() {
//							JSONObject sendResponse = new JSONObject();
//							JSONObject data = new JSONObject();
//							try {
//								data.put("duration", videoView.getDuration());
//								if(videoView.isPlaying()){
//									videoView.seekTo(0);
//									videoView.pause();
//								}
//							
//								if(videoView != null){
//									data.put("position", 100);
//								}
//								data.put("type","video");
//								data.put("state","pause");
//								sendResponse.put("params",data);
//
//								returner.set(producer, pnetwork, "com.player.apps.PlayBack" ); 			
//								returner.add(method+"mediaController", sendResponse,"messageActivity");
//								returner.send();
//								
//							} catch (JSONException e) {
//								e.printStackTrace();
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//					});
//				}else if(player !=null){  //Added By Tomesh 29 July ExoPlayer
//					if(Constant.DEBUG)  Log.d(TAG, "playPauseToggle-Exoplayer-Stop");
//					activity.runOnUiThread(new  Runnable() {
//						@Override
//						public void run() {
//							JSONObject sendResponse = new JSONObject();
//							JSONObject data = new JSONObject();
//							try {
//								data.put("duration", eventduration);
//								if(player.getPlayerControl().isPlaying()){
//									player.getPlayerControl().seekTo(0);
//									player.getPlayerControl().pause();
//								}
//							
//								if(player != null){
//									data.put("position", 100);
//								}
//								data.put("type","video");
//								data.put("state","pause");
//								sendResponse.put("params",data);
//
//								returner.set(producer, pnetwork, "com.player.apps.PlayBack" ); 			
//								returner.add(method+"mediaController", sendResponse,"messageActivity");
//								returner.send();
//								
//							} catch (JSONException e) {
//								e.printStackTrace();
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}
//					});
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}	
	
	
	
	/**
	 * Do start over .
	 *
	 * @param arg_starttime the arg_starttime
	 */
	public void doStartOver(){
		if(Constant.DEBUG)Log.e(TAG, "doStartOver");
		final Activity instance = activity;
		instance.runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				CurrentTime = System.currentTimeMillis();
				String startdate = "";
				String duration = "";
				if(programInfo!=null){
					startdate = programInfo.getDate();
					duration = programInfo.getDuration();
				}
				if(Constant.DEBUG)Log.d(TAG, "Current time is " + CurrentTime + " , Program date is "+startdate+" ,and Program time to start is " + starttime + " duration " + duration);
				try{
					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
					formatter.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
					Date date = formatter.parse(startdate+" "+starttime);
					StartTime = date.getTime();
					if(Constant.DEBUG)Log.e(TAG, "Start time "+StartTime);
					StringTokenizer tokens = new StringTokenizer(duration, ":");
					int h = 0;
					int min = 0;
					int sec = 0;
					if (tokens.hasMoreTokens()) {
						h = Integer.parseInt(tokens.nextToken());
					}
					if(tokens.hasMoreTokens()){
						min = Integer.parseInt(tokens.nextToken());
					}
					if(tokens.hasMoreTokens()){
						sec = Integer.parseInt(tokens.nextToken());
					}
					if(Constant.DEBUG)  Log.d(TAG, "Duration Hours: "+h+", Minute: "+min);
					eventduration = ((((h*60)+min)*60)+sec)*1000;
				
					if(player!=null){
//						if((CurrentTime/1000) > (StartTime/1000)){
							lukupExoPlayer(currentPlayingUrl+"?starttime="+StartTime+"&duration="+eventduration);
//						}else{
//							lukupExoPlayer(currentPlayingUrl);
//						}
					}else{ 
//						if((CurrentTime/1000) > (StartTime/1000)){
							playVideoData(currentPlayingUrl+"?starttime="+StartTime+"&duration="+eventduration);
//						}else{
//							playVideoData(currentPlayingUrl);
//						}
					}
					mediaController();
				}catch(Exception e){
					
				}
			}
		});
	}
	
	/**
	 * Gets the progress percentage.
	 *
	 * @param currentDuration the current duration
	 * @param totalDuration the total duration
	 * @return the progress percentage
	 */
	public int getProgressPercentage(long currentDuration, long totalDuration){
		if(Constant.DEBUG)  Log.d(TAG,"currentDuration: "+currentDuration+", totalDuration: "+totalDuration);
		Double percentage = (double) 0;
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		// return percentage
		return percentage.intValue();
	}

	
	/**
	 * Progress to timer.
	 *
	 * @param progress the progress
	 * @param totalDuration the total duration
	 * @return the int
	 */
	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		// return current duration in milliseconds
		return currentDuration * 1000;
	}
	
	
	/**
	 * *****************************VIEWERSHIP*************************************.
	 *
	 * @param actionType the action type
	 */
	
	private void startXML(String actionType){	//actionType like channel.....	action like tune
		if(Constant.DEBUG) Log.e(TAG, "WriteXML().channelNumber "+channelNumber+", Home.enterFrom: "+Home.enterFrom+", ChannelId: "+ChannelId);
		String entryFrom = null;
		try {
			if(Home.enterFrom.equalsIgnoreCase("Started")){
				entryFrom = Home.enterFrom;
			}else{
				entryFrom = channelNumber;
			}
			String discoveryby = getDiscoveryBy(calledfrom, type);
			if(ChannelId != null && !ChannelId.trim().equalsIgnoreCase(channelNumber)){
				channelNumber =channelInfo.getServiceId()+"";
				if(xmlChannelBuffer.length() > 0){
					xmlStringBuffer.append(AdsStatistics.getXmlEndDetails(System.currentTimeMillis(), channelNumber, "channel"));
					xmlChannelBuffer.append(xmlStringBuffer);
				}
				xmlStringBuffer = new StringBuffer();
				if(Constant.DEBUG) Log.e(TAG, "WriteXML().Changed channelNumber: "+channelNumber);
				try {
					xmlChannelBuffer.append(AdsStatistics.getXmlStartDetails(actionType, channelInfo,programInfo, null, null, 0, 0, com.port.api.util.CommonUtil.getDateTime(), discoveryby,"", "", entryFrom));
					if(Constant.DEBUG) Log.i("XML WriteXML()", ""+xmlChannelBuffer.toString());
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
				}
			}else if(xmlChannelBuffer.length() <= 0){
				try {
					xmlChannelBuffer.append(AdsStatistics.getXmlStartDetails(actionType, channelInfo,programInfo, null, null, 0, 0, com.port.api.util.CommonUtil.getDateTime(), discoveryby,"", "", entryFrom));
					if(Constant.DEBUG) Log.i("if(xmlChannelBuffer.length() <= 0)", ""+xmlChannelBuffer.toString());
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
	}
	
	/**
	 * End xml.
	 */
	private void endXML(){
		String entryFrom = null;
		if(Constant.DEBUG) Log.i("XML endXML pgEndTime: ", ""+pgEndTime+", pgTotalDuration: "+pgTotalDuration);
		if(AdSourceUrl != null && AdSourceUrl.length() != 0){
			if(AdFlag == true){
				try {
					if(adInfo != null){
						if (click != null && click.equalsIgnoreCase("click")) {
							xmlStringBuffer.append(AdsStatistics.getXmlStartDetails("ad", null,null, adInfo.getAppId()+"", adInfo.getName(), adEndTime, adTotalDuration, 0, null,AdType, "click", ""));
						}else{
							xmlStringBuffer.append(AdsStatistics.getXmlStartDetails("ad", null,null, adInfo.getAppId()+"", adInfo.getName(), adEndTime, adTotalDuration, 0, null,AdType, "impression", ""));
						}
						xmlStringBuffer.append(AdsStatistics.getXmlEndDetails(0, "", "ad"));
					}
					if(Constant.DEBUG) Log.i("XML Starting AdData", ""+xmlStringBuffer.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(AdFlag == false){
				if(Home.enterFrom.equalsIgnoreCase("Started") && programInfo != null){
					entryFrom = programInfo.getProgramId()+"";
				}else{
					entryFrom = Home.enterFrom;
				}
				try {
					
					if (click != null && click.equalsIgnoreCase("click")) {
						xmlStringBuffer.append(AdsStatistics.getXmlStartDetails("ad", null,null,  adInfo.getAppId()+"", adInfo.getName(), adEndTime, adTotalDuration, 0, null,AdType, "click", ""));
					}else{
						xmlStringBuffer.append(AdsStatistics.getXmlStartDetails("ad", null,null,  adInfo.getAppId()+"", adInfo.getName(), adEndTime, adTotalDuration, 0, null,AdType, "impression", ""));
					}
					xmlStringBuffer.append(AdsStatistics.getXmlEndDetails(0, "", "ad"));
					if(Constant.DEBUG) Log.i("XML Starting AdData", ""+xmlStringBuffer.toString());
					if (programInfo != null) {	
					xmlStringBuffer.append(AdsStatistics.getXmlStartDetails("program", null,programInfo, null, null, pgEndTime, pgTotalDuration, CurrentTime, null,"", programAction, entryFrom));
					xmlStringBuffer.append(AdsStatistics.getXmlEndDetails(pgEndTime, programInfo.getProgramId()+"", "program"));
					if(Constant.DEBUG) Log.i("XML Starting ProgData", ""+xmlStringBuffer.toString());
//					if (programInfo != null) {		
						Home.enterFrom = programInfo.getProgramId()+"";
					}
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
				}
//				Home.enterFrom = programInfo.getProgramId()+"";
			}
			if(Constant.DEBUG) Log.i("with Ads XML ProgData 1", ""+xmlChannelBuffer.toString());
			xmlChannelBuffer.append(xmlStringBuffer);
			xmlStringBuffer = new StringBuffer();
			
			if(Constant.DEBUG) Log.i("with Ads XML ProgData 2", ""+xmlChannelBuffer.toString());
		}else{
			if(Home.enterFrom.equalsIgnoreCase("Started") && programInfo != null){
				entryFrom = programInfo.getProgramId()+"";
			}else{
				entryFrom = Home.enterFrom;
			}
//			pgEndTime = System.currentTimeMillis();
			if(Constant.DEBUG) Log.e(TAG, "endXML().pgEndTime"+pgEndTime);
			try {
				if (programInfo != null) {
					xmlStringBuffer.append(AdsStatistics.getXmlStartDetails("program", null,programInfo, null, null, pgEndTime, pgTotalDuration, CurrentTime, null,"", programAction, entryFrom));
					xmlStringBuffer.append(AdsStatistics.getXmlEndDetails(pgEndTime, programInfo.getProgramId()+"", "program"));
					if(Constant.DEBUG) Log.i("XML Starting ProgData", ""+xmlStringBuffer.toString());
//				if (programInfo != null) {
					Home.enterFrom = programInfo.getProgramId()+"";
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
			}
//			Home.enterFrom = programInfo.getProgramId()+"";
			if(Constant.DEBUG) Log.i("without Ads XML ProgData 1", ""+xmlChannelBuffer.toString());
			xmlChannelBuffer.append(xmlStringBuffer);
			xmlStringBuffer = new StringBuffer();
			if(Constant.DEBUG) Log.i("without Ads XML ProgData 2", ""+xmlChannelBuffer.toString());
		}
		
		if(Constant.DEBUG) Log.i("xmlChannelBuffer", ""+ xmlChannelBuffer.toString());
		if(Constant.DEBUG) Log.w("XML FullData", "xmlChannelBuffer Size: "+xmlChannelBuffer.length());
		pgEndTime = System.currentTimeMillis();
		
		if((xmlChannelBuffer.length()) >= SIZE_25_KB){
	        int UserId = CacheData.getUserId();
	        String subscribeId = CacheData.getSubscriberId();
			xmlStringBuffer.append(AdsStatistics.getXmlEndDetails(System.currentTimeMillis(), "", "channel"));
			xmlStringBuffer.append("</userid>");	
			xmlStringBuffer.append(System.getProperty("line.separator"));
			xmlStringBuffer.append("</appid>");
			xmlChannelBuffer.append(xmlStringBuffer);
			xmlStringBuffer = new StringBuffer();
			String startElement = "<appid><app-id>101</app-id><userid><subscriberid>"+subscribeId+"</subscriberid><user-id>"+UserId+"</user-id>";
			xmlChannelBuffer.insert(0, startElement);
			
//			if(Constant.DEBUG) Log.w("XML FullData", ""+xmlChannelBuffer.toString());
			if(Constant.DEBUG) Log.d(TAG,"final size of xmlStringBuffer sending to S3 : "+xmlChannelBuffer.length());
			
			if(Constant.DEBUG) Log.i("Full xmlChannelBuffer", ""+ xmlChannelBuffer.toString());
			new AmazonS3Bucket(Play.xmlChannelBuffer.toString()).execute();
		}
	}
	
	/**
	 * Gets the discovery by.
	 *
	 * @param className the class name
	 * @param type the type
	 * @return the discovery by
	 */
	private String getDiscoveryBy(String className,String type){
		String value="";
		if (className.equalsIgnoreCase("Search")) {
			value = "search";
		} else if (className.equalsIgnoreCase("PlayList")) {
			value = "epg";
		} else if (className.equalsIgnoreCase("Guide")) {
			value = "epg";
		} else if (className.equalsIgnoreCase("Navigator")) {
			value = "zap";
		} else if (className.equalsIgnoreCase("DVBRemote")){
			value = "zap";
		}
		return value;
	}
	
	/**
	 * **************************Signed Url***********************************.
	 */
	public class PlayWithEncryption extends AsyncTask<String, Void, String> {
		
		/** The Url. */
		String Url;
		
		/** The Ext. */
		String Ext;
		
		/**
		 * Instantiates a new play with encryption.
		 *
		 * @param url the url
		 * @param ext the ext
		 */
		public PlayWithEncryption(String url,String ext){
			Url = url;
			Ext = ext;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String doInBackground(String... params) {
			InputStream inputStream = Play.this.getResources().openRawResource(R.raw.cf);
			String mEnCritedUrl = getEncryptedUrl(inputStream,Url);
			if(Constant.DEBUG) Log.i("SignedUrlActivity","Getting Encripted Url" + inputStream);
			return mEnCritedUrl;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(final String result) {
			super.onPostExecute(result);
			if (result != null && !result.equalsIgnoreCase("")&& videoView != null) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if(videoView.isPlaying()){
							videoView.stopPlayback();
						}
						videoView.setVisibility(View.VISIBLE);
						videoView.setVideoURI(Uri.parse(result));
						videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
							@Override
							public void onPrepared(MediaPlayer pMp) {
								try{
									JSONObject resp = new JSONObject();
									JSONObject data = new JSONObject();
	
									returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
						    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
									data.put("state", "start");
									resp.put("params", data);		    	
									returner.send();
								}catch(Exception e){
									e.printStackTrace();
									StringWriter errors = new StringWriter();
									e.printStackTrace(new PrintWriter(errors));
									SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
								}		
								if(Constant.DEBUG)  Log.d(TAG , "ChannelId: "+ChannelId);
								videoView.requestFocus();
								videoView.start();
								mediaController();
								if(pageType.equalsIgnoreCase("interactive")){
									adControlTimer = new Timer("adcontroller",true);
									adControlTimer.scheduleAtFixedRate(new TimerTask() {
										@Override
										public void run() {
											if(Constant.DEBUG)  Log.d(TAG, "AdControlTimer Running");
											if(videoView != null && videoView.isPlaying()){
												position = 0;
												startAdController();
											}
										}
									}, 0, 1000);									
								}
								if(progressDialog.isShowing()){
									progressDialog.dismiss();
								}
							}
						});
					}
				});
			}
		}
	}

	/**
	 * Gets the encrypted url.
	 *
	 * @param derFilePath the der file path
	 * @param url the url
	 * @return the encrypted url
	 */
	private String getEncryptedUrl(InputStream derFilePath,String url) {
		String enCryptedUrl = null;
		try {

			String s3ObjectKey = "";
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			
			String[] val = url.split("de4m7c0oq5b1u.cloudfront.net");
			for (int x = 0; x < 2; x++) {
				s3ObjectKey = val[x];
			}
			if(Constant.DEBUG) Log.i("SignedUrlActivity","s3ObjectKey: "+s3ObjectKey);
			String distributionDomain = "de4m7c0oq5b1u.cloudfront.net";
			
			// Convert your DER file into a byte array.
			byte[] derPrivateKey = ServiceUtils.readInputStreamToBytes(derFilePath);
			String signedUrlCanned = CloudFrontService.signUrlCanned(
					"http://" + distributionDomain + s3ObjectKey, // Resource URL or path
					"APKAINX6BLXGB6A6T4OQ", // Certificate identifier,
					// an active trusted signer for the distribution
					derPrivateKey, // DER Private key data
					ServiceUtils.parseIso8601Date(unixDate) // DateLessThan
					);
			
			if(Constant.DEBUG)  Log.d(TAG ,"signedUrlCanned >>>>>>>> " + signedUrlCanned);
			enCryptedUrl = signedUrlCanned;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		} catch (CloudFrontServiceException e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
		}
		return enCryptedUrl;
	}
	
	
	/**
	 * ******************************DRM**************************************.
	 */
	
	public class DrmManagerImpl extends AsyncTask<String, String, String> implements DrmManagerClient.OnEventListener,
		DrmManagerClient.OnInfoListener, DrmManagerClient.OnErrorListener {
		
		/** The Constant TAG. */
		private static final String TAG = "DRMVideo";
		
		/** The url_link. */
		private String url_link;
	
		/**
		 * Instantiates a new drm manager impl.
		 *
		 * @param url the url
		 */
		public DrmManagerImpl(String url) {
			url_link  = url.replace("http", "widevine");
		//	url_link = url;
			drmManager = new DrmManagerClient(activity);
			if(Constant.DEBUG) Log.e(TAG, "Inside DrmManagerImpl url"+url_link);
		}
			
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected String doInBackground(String... params) {
			if (drmManager == null) {
				drmManager = new DrmManagerClient(activity);
			}
			startDrmRegister();
			return url_link;
		}
	
		/**
		 * Start drm register.
		 */
		@SuppressLint("NewApi")
		private void startDrmRegister() {
			if (drmManager != null) {
				if(Constant.DEBUG) Log.d(TAG, "Drm Manager Set Listener called");
				drmManager.setOnErrorListener(this);
				drmManager.setOnEventListener(this);
				drmManager.setOnInfoListener(this);
				DrmInfoRequest drmRequest = new DrmInfoRequest(DrmInfoRequest.TYPE_RIGHTS_ACQUISITION_INFO,"video/wvm");
				// Setup drm info object
				String cgiProto = "http://license.lukup.com/widevine/cypherpc/cgi-bin/GetEMMs.cgi";
								
				if(Constant.DEBUG) Log.d(TAG, "cgiProto : "+cgiProto);
				drmRequest.put("WVDRMServerKey", cgiProto);
				drmRequest.put("WVAssetURIKey", url_link);
				drmRequest.put("WVDeviceIDKey", "device1");
				drmRequest.put("WVPortalKey", "lukup");
				// Request license
				drmManager.acquireRights(drmRequest);
			} else {
				if(Constant.DEBUG)  Log.d(TAG, "Drm Manager Null");
			}
		}
	
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		protected void onPostExecute(String obj) {
			super.onPostExecute(obj);
			if(progressDialog.isShowing()){
				progressDialog.dismiss();
			}
			playEvent();
		}
		
		/**
		 * Play event.
		 */
		private void playEvent() {
			try {
				if(Constant.DEBUG) Log.e(TAG, "Inside playEvent url"+url_link);
				videoView.setVisibility(View.VISIBLE);
				Uri mMediaUri = Uri.parse(url_link);
				videoView.setVideoURI(mMediaUri);
				videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer pMp) {
						try{
							JSONObject resp = new JSONObject();
							JSONObject data = new JSONObject();

							returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
				    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
							data.put("state", "start");
							resp.put("params", data);		    	
							returner.send();
						}catch(Exception e){
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_DRM, errors.toString(), e.getMessage());
						}
						videoView.start();
						mediaController();
					
					}
				});
				
				// View DRM License Information
				if (drmManager != null) {
					ContentValues values = drmManager.getConstraints(url_link,DrmStore.Action.PLAY);
					if (values != null) {
						Set<String> keys = values.keySet();
						StringBuilder builder = new StringBuilder();
						for (String key : keys) {
							builder.append(key);
							builder.append(" = ");
							builder.append(values.get(key));
							builder.append("\n");
						}
						if(Constant.DEBUG)  Log.d(TAG,"DRM License Information : "+ builder.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_DRM, errors.toString(), e.getMessage());
			}
		}
	
		/* (non-Javadoc)
		 * @see android.drm.DrmManagerClient.OnErrorListener#onError(android.drm.DrmManagerClient, android.drm.DrmErrorEvent)
		 */
		@Override
		public void onError(DrmManagerClient client, DrmErrorEvent event) {
			if(Constant.DEBUG)  Log.d(TAG, "DRM Error Code " + event.getUniqueId() + " Message  " + event.getMessage() + " , Type  " + event.getType());
			switch (event.getType()) {
			case DrmErrorEvent.TYPE_ACQUIRE_DRM_INFO_FAILED:
				if(Constant.DEBUG)  Log.d(TAG,"DRM TYPE_ACQUIRE_DRM_INFO_FAILED");
				break;
				
			case DrmErrorEvent.TYPE_DRM_INFO_PROCESSED:
				if(Constant.DEBUG)  Log.d(TAG, "DRM TYPE_DRM_INFO_PROCESSED");
				break;
				
			case DrmErrorEvent.TYPE_NOT_SUPPORTED:
				if(Constant.DEBUG)  Log.d(TAG, "DRM TYPE_NOT_SUPPORTED");
				break;
				
			case DrmErrorEvent.TYPE_NO_INTERNET_CONNECTION:
				if(Constant.DEBUG)  Log.d(TAG,"DRM TYPE_NO_INTERNET_CONNECTION");
				break;
			}
		}
	
		/* (non-Javadoc)
		 * @see android.drm.DrmManagerClient.OnInfoListener#onInfo(android.drm.DrmManagerClient, android.drm.DrmInfoEvent)
		 */
		@Override
		public void onInfo(DrmManagerClient client, DrmInfoEvent event) {
			if(Constant.DEBUG)  Log.d(TAG, "DRM Info Code  " + event.getUniqueId() + " Message  "
					+ event.getMessage() + " , Type  " + event.getType());
			switch (event.getType()) {
			case DrmInfoEvent.TYPE_RIGHTS_INSTALLED:
				if(Constant.DEBUG)  Log.d(TAG,"Rights Installed");
				break;
			case DrmInfoEvent.TYPE_RIGHTS_REMOVED:
				if(Constant.DEBUG)  Log.d(TAG,"Rights Removed");
				break;
			}
		}
	
		/* (non-Javadoc)
		 * @see android.drm.DrmManagerClient.OnEventListener#onEvent(android.drm.DrmManagerClient, android.drm.DrmEvent)
		 */
		@Override
		public void onEvent(DrmManagerClient client, DrmEvent event) {
			if(Constant.DEBUG)  Log.d(TAG,"DRM On Event Code  " + event.getUniqueId() + " Message  " + event.getMessage() + " , Type  " + event.getType());
			switch (event.getType()) {
			case DrmEvent.TYPE_DRM_INFO_PROCESSED:
				if(Constant.DEBUG)  Log.d(TAG,"DRM Info Processed");
				break;
			case DrmEvent.TYPE_ALL_RIGHTS_REMOVED:
				if(Constant.DEBUG)  Log.d(TAG,"All Rights Removed");
				break;
			}
		}
	}
	
	/**
	 * **************************MODULAR DRM*******************************.
	 *
	 * @param playWhenReady the play when ready
	 * @param playbackState the playback state
	 */
	 @Override
		public void onStateChanged(boolean playWhenReady, int playbackState) {
	    	if(Constant.DEBUG)  Log.i("ModularDRM","onStateChanged- playWhenReady" + playWhenReady);
	    	if (playbackState == ExoPlayer.STATE_ENDED) {
	    		JSONObject resp = new JSONObject();
				JSONObject data = new JSONObject();
	    		try {
	    			if(Constant.DEBUG)  Log.i("ModularDRM","onStateChanged- playbackState-Ended" + playbackState);
	    			returner.set(Listener.pname, pnetwork, "com.player.apps.PlayBack"); 			
		    		returner.add("com.port.apps.epg.Play.Stop", resp, "messageActivity");		
					resp.put("params", data);
					data.put("state", "stop");
					returner.send();			
					Play.this.finish();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}		    	
				
	    	    
	    	 }
	    	if(progressDialog != null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
		}

		/* (non-Javadoc)
		 * @see com.port.apps.epg.exoplayer.LukupPlayer.Listener#onError(java.lang.Exception)
		 */
		@Override
		public void onError(Exception e) {
		//	if(Constant.DEBUG)  Log.i("ModularDRM-onError", e.getMessage().toString());
			 if (e instanceof UnsupportedDrmException) {
			      // Special case DRM failures.
			      UnsupportedDrmException unsupportedDrmException = (UnsupportedDrmException) e;
			      int stringId = unsupportedDrmException.reason == UnsupportedDrmException.REASON_NO_DRM
			          ? R.string.drm_error_not_supported
			          : unsupportedDrmException.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
			          ? R.string.drm_error_unsupported_scheme
			          : unsupportedDrmException.reason == UnsupportedDrmException.SUBSCRIBER_BLOCKED
			          ? R.string.drm_error_not_Subscriber
			          : R.string.drm_error_unknown;
			      Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_LONG).show();
			    }
			 Toast.makeText(getApplicationContext(), getResources().getString(R.string.TUNERERROR), Toast.LENGTH_LONG).show();
			// stopAV();
			   
		}

		/* (non-Javadoc)
		 * @see com.port.apps.epg.exoplayer.LukupPlayer.Listener#onVideoSizeChanged(int, int, float)
		 */
		public void onVideoSizeChanged(int width, int height,
				float pixelWidthHeightRatio) {
			if(Constant.DEBUG)  Log.i("ModularDRM","onVideoSizeChanged");
		}

		/* (non-Javadoc)
		 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
		 */
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if(Constant.DEBUG)  Log.i("ModularDRM","surfaceCreated");
			if (player != null) {
			      player.setSurface(holder.getSurface());
				    try{
						JSONObject resp = new JSONObject();
						JSONObject data = new JSONObject();

						returner.set(Listener.pname, pnetwork, "com.player.UpdateService"); 			
			    		returner.add("com.port.apps.epg.Play.PlayOn", resp, "startService");		
						data.put("state", "start");
						resp.put("params", data);		    	
						returner.send();
					}catch(Exception e){
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_DRM, errors.toString(), e.getMessage());
					}
			}		
			
				
		}

		/* (non-Javadoc)
		 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
		 */
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			if(Constant.DEBUG)  Log.i("ModularDRM","surfaceChanged");
			
		}

		/* (non-Javadoc)
		 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
		 */
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if(Constant.DEBUG)  Log.i("ModularDRM","surfaceDestroyed");
		    if (player != null) {
		        player.blockingClearSurface();
		    }
		}

	/**
	 * **************************ADS****************************************.
	 *
	 * @param ChannelId the channel id
	 * @param eventId the event id
	 * @return the ad info
	 */
	private AdInfo checkingForAds(String ChannelId, int eventId){
		ArrayList<AdInfo> IndexList = new ArrayList<AdInfo>();
		
		for(int i = 0; i < Schedule.AdsList.size(); i++){
    		if(ChannelId.equalsIgnoreCase(Schedule.AdsList.get(i).getChannelId())){
    			if(Constant.DEBUG)  Log.d("checkingForAds()" , "Channel: "+Schedule.AdsList.get(i).getChannelId());
    			IndexList.add(Schedule.AdsList.get(i));
    		}
		}
		if(IndexList.size()>0){
			AdInfo smallestValue = IndexList.get(0);
			for (int a = 0; a < IndexList.size();a++){
	    		if(IndexList.get(a).getSpots() > smallestValue.getCount() ){
		    		if(smallestValue.getCount() > IndexList.get(a).getCount()){
			    		smallestValue = IndexList.get(a);
						if(Constant.DEBUG)  Log.i("AdsDisplaySchedulihg()" , "Changed Count: "+smallestValue.getCount()+", Changed Spots: "+smallestValue.getSpots() +" & finalUrl: "+smallestValue.getWidgetUrl());
					}
	    		}
	        }  
			adInfo = smallestValue;
		}
		if(adInfo != null){			
			AdFlag = true;
			if(Constant.DEBUG)  Log.i("checkingForAds()" , "adUrl: "+adInfo.getWidgetUrl()+", AdFlag: "+AdFlag);
			return adInfo;
		}
		return adInfo;		
	}
	
	/**
	 * Start ad controller.
	 */
	private void startAdController(){
		if(Constant.DEBUG)  Log.i(TAG,"jsonTimeList() size "+ jsonTimeList.size());
		if(jsonTimeList != null && jsonTimeList.size() >0  && position < jsonTimeList.size()){
			if(Constant.DEBUG)  Log.i(TAG , "CurrentPosition:  "+videoView.getCurrentPosition());
			if(Constant.DEBUG)  Log.i(TAG , "JsonTimeList position:  "+Integer.parseInt(jsonTimeList.get(position)));
			if(videoView.getCurrentPosition() >= Integer.parseInt(jsonTimeList.get(position))){
				if(myWebView != null){
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(Constant.DEBUG)  Log.i(TAG,"Inside Runnable() "+position);
							String action= "'trigger'";
							String url = "javascript:com.lukup.eventHandler"+"("+action+","+Integer.parseInt(jsonTimeList.get(position))+");";
							if(Constant.DEBUG)  Log.d(TAG,"Loading Url "+url.toString().trim());
							myWebView.loadUrl(url.toString().trim());
							position+=1;
						}
					});
				}	
			}	
		}else{
			position=0;
			adControlTimer.cancel();
		}
		if(Constant.DEBUG)  Log.i(TAG,"startAdController() "+position);
	}
	
    /**
     * The Class XMLParsingDoingInBackground.
     */
    public class XMLParsingDoingInBackground extends AsyncTask<String, String, Document> {

		/** The Url. */
		private String Url;
		
		/** The parser. */
		XMLParser parser;
		
		/** The Key. */
		ArrayList<String> Key = new ArrayList<String>();
		
		/** The Value. */
		ArrayList<String> Value = new ArrayList<String>();

		/**
		 * Instantiates a new XML parsing doing in background.
		 *
		 * @param urlData the url data
		 */
		public XMLParsingDoingInBackground(String urlData) {
			Url = urlData;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Document doInBackground(String... params) {
			parser = new XMLParser();
			String xml = parser.getXmlFromUrl(Url); // getting XML
			if(Constant.DEBUG)  Log.i("VAST XML", ""+xml);
			Document doc = parser.getDomElement(xml);
			return doc;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		protected void onPostExecute(Document obj) {
			super.onPostExecute(obj);
			try {
				HashMap<String,String> adsXmlValues = new HashMap<String, String>();
				NodeList item1 = obj.getElementsByTagName("Duration");
				String Duration = item1.item(0).getChildNodes().item(0).getNodeValue();
//				Key.add("Duration");
//				Value.add(Duration);
				adsXmlValues.put("Duration", Duration);

				NodeList item2 = obj.getElementsByTagName("MediaFile");
				AdSourceUrl = item2.item(0).getChildNodes().item(0).getNodeValue();
//				Key.add("MediaFile");
//				Value.add(MediaFile);
				adsXmlValues.put("MediaFile", AdSourceUrl);
				
				NodeList item3 = obj.getElementsByTagName("Tracking");
				for (int i = 0; i < item3.getLength(); i++) {
					Element e = (Element) item3.item(i);
					String Tracking = item3.item(i).getChildNodes().item(0).getNodeValue();
//					Log.i("XMLParsingDoingInBackground", "Tracking: "+i +"  "+ Tracking);
//					Key.add("Tracking:"+i);
//					Value.add(Tracking);
					adsXmlValues.put("Tracking:"+i, Tracking);
				}
				xmlParserData.add(adsXmlValues);
				
				
//				LOG.i("XMLParsingDoingInBackground", "Duration: " + Duration+", MediaFile: " + MediaFile);
//				Log.i("XMLParsingDoingInBackground", "Key.size(): " + Key.size());
				for (int i = 0; i < Key.size(); i++) {
//					Log.i("XMLParsingDoingInBackground", "Key: " + Key.get(i)+ ", Value: " + Value.get(i));
				}
//				Log.w("XMLParsingDoingInBackground", "ImpValue: " +ImpValue);

				AdFlag = true;
	    		playVideoData(AdSourceUrl);
	    		
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_PLAYBACK, errors.toString(), e.getMessage());
			}
		}
	}
    
    /**
     * Impression cal.
     *
     * @param currDuration the curr duration
     * @param Duration the duration
     */
    private void ImpressionCal(int currDuration, int Duration) {
    	if(Constant.DEBUG)  Log.w(TAG, "Duration :" + Duration + ", currDuration :" + currDuration);
		double value = Duration / currDuration;
		if(Constant.DEBUG)  Log.w(TAG, "value :" + value + ", Duration :" + Duration + ", currDuration :" + currDuration);
		String trackEvent = null;
		if (value == 1) {
			trackEvent = "Tracking:4";	// complete
		} else if (value < 1.0 && value >= 1.33) {
			trackEvent = "Tracking:3";	// thirdQuartile
		} else if (value > 1.33 && value <= 2.0) {
			trackEvent = "Tracking:2";	// midpoint
		} else if (value > 2.0 && value <= 4.0) {
			trackEvent = "Tracking:1";	// firstQuartile
		} else if (value > 4.0 && value <= Duration) {
			trackEvent = "Tracking:0";  // start
		}
		if(Constant.DEBUG)  Log.e(TAG, "Inside ImpressionUrl.trackEvent: "+trackEvent);
		ImpressionUrls(currDuration, Duration, trackEvent);
	}
    
    /**
     * Impression urls.
     *
     * @param currDuration the curr duration
     * @param Duration the duration
     * @param event the event
     */
    private void ImpressionUrls(int currDuration, int Duration, String event) {
		double value = Duration / currDuration;
		if(Constant.DEBUG)  Log.w(TAG, "value :" + value + ", Duration :" + Duration + ", currDuration :" + currDuration);
		String eventUrl = null;
		if (value == 1) {
			eventUrl = "complete";
		} else if (value <= 1.0 && value > 1.33) {
			eventUrl = "complete";
		} else if (value >= 1.33 && value < 2.0) {
			eventUrl = "thirdQuartile";		
		} else if (value >= 2.0 && value < 4.0) {
			eventUrl = "midpoint";	
		}else if (value >= 4.0) {
			eventUrl = "firstQuartile"; 
		}
		if(Constant.DEBUG)  Log.e(TAG, "Inside ImpressionUrls.eventUrl: "+eventUrl);
		ImpressionUrl = Constant.ADS_IMPRESSTION_URL;
		
		if (adInfo != null) {
			if (clicked > 0) {
				click = "click";
			}
			
			ImpressionUrl += adInfo.getAppId()+"&campId=" + adInfo.getCampId() + "&serviceId=" + adInfo.getChannelId() + "&viewership="+eventUrl
					+"&device="+ deviceType +"-AND"+Build.VERSION.RELEASE+"&click="+clicked+"&like="+liked+"&adtype="+adInfo.getType()
					+"&pub_camp_id="+adInfo.getPubCampId()+"&publisher_id="+adInfo.getPublisherid();
			clicked = 0;
			if(Constant.DEBUG)  Log.e(TAG, "Inside ImpressionUrls.ImpressionUrl: "+ImpressionUrl);
		}else{
			
		}
		if (xmlParserData.size() > 0) {
			if(Constant.DEBUG)  Log.w(TAG,"xmlParserData size() :" + xmlParserData.size()+ ", trackEvent :" + event);
			for (int i = 0; i < xmlParserData.size(); i++) {
				HashMap<String,String> map = xmlParserData.get(i);
				if (xmlParserData.contains(event)) {
					TrackingUrl = map.get(event);
				}
			}
			if(Constant.DEBUG)  Log.e(TAG, "Inside ImpressionUrl.TrackingUrl: "+TrackingUrl);
			new AdsBackgroundTask(TrackingUrl, "Adsimpression").execute();
		}else{
			new AdsBackgroundTask(ImpressionUrl, "Lukupimpression").execute();
		}
	}
    
    /**
     * The Class AdsBackgroundTask.
     */
    public class AdsBackgroundTask extends AsyncTask<String, String, Boolean> {
		
		/** The ads url. */
		String adsUrl;
		
		/** The Type. */
		String Type;

		/**
		 * Instantiates a new ads background task.
		 *
		 * @param Url the url
		 * @param type the type
		 */
		public AdsBackgroundTask(String Url, String type) {
			adsUrl = Url;
			Type = type;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
		 */
		@Override
		protected Boolean doInBackground(String... params) {
			JSONObject jObj = null;
			String response = "";
			try {
				if(Constant.DEBUG)  Log.w(TAG, "doInBackground().adsUrl: " +adsUrl);
				URL url = new URL(adsUrl.toString());
				URLConnection connection = url.openConnection();
				
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	
				// get the response from the server and store it in result
				DataInputStream dataIn = null;
				try {
					dataIn = new DataInputStream(connection.getInputStream());
				} catch (Exception e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
				} 
				
				if(dataIn != null){
					BufferedReader br = new BufferedReader(new InputStreamReader(dataIn));
					String inputLine;
					while ((inputLine = br.readLine()) != null) {
						response += inputLine;
					}
					if(Constant.DEBUG)  Log.d(TAG,"final response to get the refresh user list is  : "+response);
					if(response != null && !(response.trim().equals(""))) {
						jObj = new JSONObject(response);
						currentSpots = Integer.parseInt(jObj.getJSONObject("data").getString("spots"));
						 if(Constant.DEBUG)  Log.d(TAG, "currentSpots: "+currentSpots);
						 for (int i = 0; i < Schedule.AdsList.size(); i++) {
							if(Schedule.AdsList.get(i).getAppId() ==  adInfo.getAppId()){
								int changedcount = adInfo.getCount();
								if(Constant.DEBUG) Log.w(TAG, "AdsBackgroundTask : " +i+ ", currentSpots: "+currentSpots+ ", changedcount: "+changedcount);
								Schedule.AdsList.set(i, new AdInfo(adInfo.getUniqueAppId(), currentSpots, adInfo.getCampId(), adInfo.getAppId(), adInfo.getName(),
										adInfo.getType(), adInfo.getWidgetUrl(), adInfo.getChannelId(), adInfo.getStime(), adInfo.getEtime(), adInfo.getPublisherid(),
										adInfo.getPubCampId(),changedcount+1));
							}
						 }
						 
//						 for (int i = 0; i < Catalogue.AdsList.size(); i++) {
//							 if(Constant.DEBUG) Log.w(TAG, "After AppId: " +Catalogue.AdsList.get(i).getAppId()+"count: " +Catalogue.AdsList.get(i).getCount()+", Spots: " +Catalogue.AdsList.get(i).getSpots());
//						 }
					}
					br.close();
					dataIn.close();
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if(Constant.DEBUG)  Log.w(TAG, "onPostExecute().Type: " + Type+", result: " + result);
			if(Type.equals("Adsimpression")){
				if(TrackingUrl != null && TrackingUrl.length() != 0) {
						new AdsBackgroundTask(ImpressionUrl, "Lukupimpression").execute();
				}
			}
		}
	}

	@Override
	public void onVideoSizeChanged(int width, int height,
			int unappliedRotationDegrees, float pixelWidthHeightRatio) {
		// TODO Auto-generated method stub
		
	}

   
}
