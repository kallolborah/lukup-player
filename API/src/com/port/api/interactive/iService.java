package com.port.api.interactive;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.json.JSONObject;

import com.port.Channel;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.app.Instrumentation;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

public class iService extends IntentService{
	
	private String TAG = "iService";
	
	//Receiver
	String func;
	private String functionData;
	String pnetwork;
	String cnetwork;
	String producer;
	String caller;
	Channel returner;
	String dockID;
	int keycode;
	float xCoordinate;
	float yCoordinate;

	public iService() {
		super("iService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		
		String appName = "";
		String modifier = "";
		String key="";
		
		dockID = Build.ID; //is this correct ?
		returner = new Channel("Dock", dockID); //only to be used to send back responses from Dock to Requestor, eg, Player
		
		
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
		    		if(jsonObj.has("name")){
		    			appName = jsonObj.getString("name");
		    		}
		    		if(jsonObj.has("modifier")){
		    			modifier = jsonObj.getString("modifier");
		    		}
		    		if(jsonObj.has("key")){
		    			key = jsonObj.getString("key");
		    		}
		    		if(jsonObj.has("keycode")){
		    			keycode = Integer.parseInt(jsonObj.getString("keycode"));
		    		}
		    		if(jsonObj.has("x")){
		    			xCoordinate = Float.parseFloat(jsonObj.getString("x"));
		    		}
		    		if(jsonObj.has("y")){
		    			yCoordinate = Float.parseFloat(jsonObj.getString("y"));
		    		}
		    		
		    		
	    		}  catch(Exception e){
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}
	    	}
		
	    	if(extras.containsKey("Method")){
	    		if(Constant.DEBUG)  Log.d(TAG , "Games direction");
	    		try{
		    		String func = extras.getString("Method");
		    		if (func.equalsIgnoreCase("gamepad")) {
		    			if(key.equalsIgnoreCase("DPAD_UP")){ 
		    				if(Constant.DEBUG)  Log.d(TAG , "Games direction UP ");
		    				new Thread(new Runnable() {         
		                        @Override
		                        public void run() {                 
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_DPAD_UP));
		                        }   
		                    }).start();
		    			} else if (key.equalsIgnoreCase("DPAD_DOWN")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games direction DOWN ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_DPAD_DOWN));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("DPAD_LEFT")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games direction LEFT ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_DPAD_LEFT));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("DPAD_RIGHT")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games direction RIGHT ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_DPAD_RIGHT));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("BUTTON_X")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games button BUTTON_X ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_BUTTON_X));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("BUTTON_Y")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games button BUTTON_Y ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_BUTTON_Y));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("BUTTON_A")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games button BUTTON_A ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_BUTTON_A));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("BUTTON_B")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games button BUTTON_B ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_BUTTON_B));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("BUTTON_THUMBL")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games Start BUTTON_THUMBL ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_BUTTON_THUMBL));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("BUTTON_THUMBR")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games Select BUTTON_THUMBR ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_BUTTON_THUMBR));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("BUTTON_L1")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games button BUTTON_L1 ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_BUTTON_L1));
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("BUTTON_R1")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games button BUTTON_R1 ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_BUTTON_R1));
		                        }   
		                    }).start();
		    			}
		    			
		    			else if (key.equalsIgnoreCase("AXIS_THROTTLE")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games button AXIS_THROTTLE ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	
		                        }   
		                    }).start();
		    			}else if (key.equalsIgnoreCase("AXIS_BRAKE")){
		    				if(Constant.DEBUG)  Log.d(TAG , "Games button AXIS_BRAKE ");
	    					new Thread(new Runnable() {         
		                        @Override
		                        public void run() {    
		                        	
		                        }   
		                    }).start();
		    			}
		    		}else if(func.equalsIgnoreCase("keyboard")) {
		    			if (modifier.equalsIgnoreCase("")) {
		    				new Thread(new Runnable() {         
		                        @Override
		                        public void run() {                 
		                        	new Instrumentation().sendKeySync(new KeyEvent(0, keycode));
		                        	if(keycode == 66){ //user has pressed 'enter'
		                        		
		                        	}
		                        }   
		                    }).start();
						} else {
							if(Constant.DEBUG)  Log.d(TAG , "Shift pressed");
							if(keycode == KeyEvent.KEYCODE_A){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("A");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_B){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("B");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_C){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("C");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_D){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("D");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_E){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("E");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_F){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("F");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_G){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("G");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_H){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("H");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_I){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("I");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_J){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("J");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_K){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("K");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_L){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("L");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_M){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("M");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_N){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("N");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_O){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("O");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_P){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("P");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_Q){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("Q");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_R){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("R");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_S){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("S");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_T){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("T");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_U){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("U");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_V){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("V");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_W){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("W");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_X){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("X");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_Y){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("Y");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_Z){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("Z");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_1){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("!");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_2){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_AT));
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_3){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_POUND));
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_4){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("$");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_5){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {   
			                        	new Instrumentation().sendStringSync("%");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_6){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("^");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_7){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("&");
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_8){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_NUMPAD_MULTIPLY));
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_9){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_NUMPAD_LEFT_PAREN));
			                        }   
			                    }).start();
							}else if(keycode == KeyEvent.KEYCODE_0){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_NUMPAD_RIGHT_PAREN));
			                        }   
			                    }).start();
							}else if(keycode == 69){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("_");
			                        }   
			                    }).start();
							}else if(keycode == 76){ 
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("?");
			                        }   
			                    }).start();
							}else if(keycode == 74){ 
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync(":");
			                        }   
			                    }).start();
							}else if(keycode == 75){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("\"");
			                        }   
			                    }).start();
							}else if(keycode == 70){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("+");
			                        }   
			                    }).start();
							}else if(keycode == 73){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("|");
			                        }   
			                    }).start();
							}else if(keycode == 68){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("~");
			                        }   
			                    }).start();
							}else if(keycode == 71){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("{");
			                        }   
			                    }).start();
							}else if(keycode == 72){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("}");
			                        }   
			                    }).start();
							}else if(keycode == 55){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync("<");
			                        }   
			                    }).start();
							}else if(keycode == 56){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendStringSync(">");
			                        }   
			                    }).start();
							}else if(keycode == 61){
								new Thread(new Runnable() {         
			                        @Override
			                        public void run() {                 
			                        	new Instrumentation().sendKeySync(new KeyEvent(0, KeyEvent.KEYCODE_TAB));
			                        }   
			                    }).start();
							}
							
						}
		    		}
		    		else if(func.equalsIgnoreCase("SingleTap")) {
		    			if(Constant.DEBUG)  Log.d(TAG ,"DoubleTap xCoordinate: "+xCoordinate+", yCoordinate: "+yCoordinate);
		    			int ix = 0;
    					int iy = 0;
    					if(Constant.DEBUG)  Log.d(TAG ,"Percentage xCoordinate: "+xCoordinate+", yCoordinate: "+yCoordinate);
    					
						DisplayMetrics metrics = new DisplayMetrics();
						try {
							WindowManager winMgr = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
							winMgr.getDefaultDisplay().getMetrics(metrics);
							int width = winMgr.getDefaultDisplay().getWidth();
							int height = winMgr.getDefaultDisplay().getHeight();
							if (Constant.DEBUG)Log.d(TAG, "TV Resolution width " + width + ", hieght " + height);
							ix = (int)(xCoordinate * width);
							iy = (int)(yCoordinate * height);
						} catch (Exception e) {
							e.printStackTrace();
						}
    					Instrumentation m_Instrumentation = new Instrumentation();
						m_Instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN,ix, iy, 0));
						m_Instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
						SystemClock.uptimeMillis(),MotionEvent.ACTION_UP,ix, iy, 0));
		    		}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    			StringWriter errors = new StringWriter();
	    			e.printStackTrace(new PrintWriter(errors));
	    			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    		}		    		
	    	}
		}
	}	
	
	
}
