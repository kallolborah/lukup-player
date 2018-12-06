/*
 * Copyright (c) Lukup Media Pvt Limited, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Lukup Media Pvt Limited ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms
 * of the licence agreement you entered into with Lukup Media Pvt Limited.
 *
 */
package com.port.api.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.util.DisplayMetrics;

/**
 * This class provides methods to set proper resolution.
 * @author Anand
 *
 */
public class Resolution {

	private static int width;
	private static int height;
	private static final int DEFAULT_WIDTH = 1280;
	private static final int DEFAULT_HEIGHT = 720;
//	private static Activity activityinstance;
	private static double avg;

	public static void init(DisplayMetrics dm)
	{
		try {
			width = dm.widthPixels;
			height = dm.heightPixels;
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
	}

	public  static int getCurrentHeight(){
		/*DisplayMetrics dm = new DisplayMetrics();
		activityinstance.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;*/
		return height;
	}
	
	public  static int getCurrentWidth(){
		/*DisplayMetrics dm = new DisplayMetrics();
		activityinstance.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;*/
		return width;
	}
	public static float convertXDP(float currDp)
	{
		float condp = 0.0f;
		condp = (currDp * width)/DEFAULT_WIDTH;
		return condp;

	}
	public static float convertYDP(float currDp)
	{
		float condp = 0.0f;
		condp = (currDp * height)/DEFAULT_HEIGHT;
		return condp;
	}
	
	public static int convertResolutionWithHeight(int x){
		float val=480;
		try{
			if(x != 0){	
				double convertedResolution;
				avg= (x*100)/val;
				convertedResolution=(avg*height)/100;
				return (int) Math.ceil(convertedResolution);
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return 0;
	}
	public static int convertResolutionWithWidth(int x){
		float val=320;
		try{
			if(x != 0){	
				double convertedResolution;
				avg=(x*100)/val;
				convertedResolution=(avg*width)/100;
				return (int) Math.ceil(convertedResolution);
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return 0;
	}
	
}
