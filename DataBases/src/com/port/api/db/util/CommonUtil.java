package com.port.api.db.util;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.port.Port;
import com.port.api.db.service.Store;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;



public class CommonUtil {

	private static String TAG = "CommonUtil";
	public static boolean isWifiP2pEnabled = false;

	public static boolean isNetworkAvailable() {
		try{
			if(Constant.DEBUG)  Log.d(TAG ,"isNetworkAvailable(): ");
		    ConnectivityManager connectivityManager= (ConnectivityManager) Port.c.getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
		    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		    if(Constant.DEBUG)  Log.d(TAG,"isConnected: "+isConnected);
		    return isConnected;
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
	    }
		return false;
	}
	
	public static SQLiteDatabase open() throws SQLException {
//		Store dbHelper = CacheData.getDbHelper();
//		Activity activity = CacheData.getActivity();
//		if (activity != null) {
//			if(dbHelper == null && CacheData.getActivity().getApplicationContext()!=null){
//				if(Constant.DEBUG)  Log.d(TAG , "SQLiteDatabase "+CacheData.getActivity().getApplicationContext().toString());
//				dbHelper = new Store(CacheData.getActivity().getApplicationContext());
//				CacheData.setDbHelper(dbHelper);
//			}else{
//				
//			}
//		}else{
//			if(dbHelper == null && com.port.api.util.CacheData.getActivity().getApplicationContext()!=null){
//				if(Constant.DEBUG)  Log.d(TAG , "SQLiteDatabase "+com.port.api.util.CacheData.getActivity().getApplicationContext().toString());
//				dbHelper = new Store(com.port.api.util.CacheData.getActivity().getApplicationContext());
//				CacheData.setDbHelper(dbHelper);
//			}
//		}
//		SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
		return null;
	}

	public void close() {
//		try{
//			Store dbHelper = CacheData.getDbHelper();
//			if(dbHelper != null){
//				dbHelper.close();
//				CacheData.setDbHelper(null);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
//	    }
	}
		
	public static boolean checkConnectionForLocaldb() {
//		SQLiteDatabase database = CacheData.getDatabase();
//		if(database == null || !(database.isOpen())) {
//			for(int i=0; i<3; i++){
//				try {
//					database = open();
//				}catch(Exception e) {
//					e.printStackTrace();
//				}
//				CacheData.setDatabase(database);
//				if(database != null && database.isOpen()) {
//					return true;
//				}
//				if(Constant.DEBUG)  Log.d(TAG, "ConnectionForLocaldb is null");
//			}
//		}
//		if(database == null || !(database.isOpen())) {
//			return false;
//		}
		return true;
	}
	

	public static void sendErrorMessage(String msg,String handler) {
		try{
			JSONObject resp = new JSONObject();
			JSONObject data = new JSONObject();
			JSONObject childdata = new JSONObject();
			
			if(handler != null){
				data.put("handler", handler);
			}
			else{
				data.put("handler", handler);
			}
			if(msg != null)
				data.put("msg", msg);
			
			resp.put("params",data);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getCurrentDate() {
		String date=null;
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta")); 
		String mYear = c.get(Calendar.YEAR)+""; 
		String mMonth = c.get(Calendar.MONTH) +""; 
		String mDay = c.get(Calendar.DAY_OF_MONTH)+""; 
		if(mYear.length()==1){
			mYear="0"+mYear;
		}
		if(mMonth.length()==1){
			mMonth="0"+mMonth;
		}
		if(mDay.length()==1){
			mDay="0"+mDay;
		}
		date=mYear+"-"+mMonth+"-"+mDay;
		return date;
	}

	
	public static  String getCurrentTime() {
		String time=null;
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta")); 
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int seconds = c.get(Calendar.SECOND);
		String  sthr=hours+"";
		String stmin=minutes+"";
		String stsec=seconds+"";
		if(sthr.length()==1){
			sthr="0"+sthr;
		}
		if(stmin.length()==1){
			stmin="0"+stmin;
		}
		if(stsec.length()==1){
			stsec="0"+stsec;
		}
		time=sthr+":"+stmin+":"+stsec;
		return time;
	}
	
	
	public static  String getCurrentHourValue() {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
		int min = cal.get(Calendar.MINUTE);
		if( min < 30)
			min = 0;
		if(min >= 30)
			min = 30;
		int hr = cal.get(Calendar.HOUR_OF_DAY);
		String per = "AM";
		if(hr > 12){
			hr = hr - 12;
			per = "PM";
		}
		String strhr =  hr+ "";
		String strmn = min + "";

		if (strmn.length() == 1) {
			strmn = "0" + strmn;
		}

		String currTime = strhr + ":" + strmn;
		return currTime;
	}
	
	

	public static String getInHourFormat(int duration) {
		String stringDuration = "";
		if(duration > 0) {
			int a = duration/3600;
			int b,c,d=0;
			if(a > 0) {
				b = duration%a;
				c = b/60;
				if(c>0)
					d = b%c;
			} else {
				c = duration/60;
				if(c > 0) {
					d = duration%c;
				} else {
					d = duration;
				}
			}

			if(a<10) {
				stringDuration += "0"+a;
			} else {
				stringDuration += ""+a;
			}

			if(c<10) {
				stringDuration += ":0"+c;
			} else {
				stringDuration += ":"+c;
			}

			if(d<10) {
				stringDuration += ":0"+d;
			} else {
				stringDuration += ":"+d;
			}

			if(Constant.DEBUG)  Log.d(TAG,"Formated duration : "+stringDuration);

		} else {
			stringDuration = "00:00:00";
		}

		return stringDuration;
	}
	
	public static long getDateTime() {
		try{
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
			SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");		// "30-04-2014 20:07:17"
			String currdate = timeFormat.format(c.getTime());
			if(Constant.DEBUG)  Log.d(TAG,"current date : "+currdate);
			Date date1 = timeFormat.parse(currdate);
			long currentTimeStamp = date1.getTime();
			if(Constant.DEBUG)  Log.d(TAG,"current timeStamp : "+currentTimeStamp);
	        return currentTimeStamp;
		}catch(Exception e){
    		if(Constant.DEBUG)  Log.d(TAG, "Exception Converter():"+e.toString());
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
    	}
		return 0;
	}
	
	public static String getDate() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");		// "30-04-2014"
		String currdate = dateFormat.format(c.getTime());
//		if(Constant.DEBUG)  Log.d(TAG,"current date : "+currdate);
        return currdate;
	}
	
	public static String getDates() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");		// "2014-04-13"
		String currdate = dateFormat.format(c.getTime());
        return currdate;
	}
	
	
	public static boolean compareDates(String currentDate,String compareDate){
		try {
			SimpleDateFormat sdf = null;
			if(sdf==null)
				sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date mDate1 = sdf.parse(currentDate);
			Date mDate2 = sdf.parse(compareDate);
			if(mDate2.before(mDate1)){
				return false;
			}
			if(mDate2.after(mDate1) || mDate2.equals(mDate1)){
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			return false;
		}
		return false;
	}

	
	public static Date getCurrentDateAndTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss");
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
		return calendar.getTime();
	}
	
	
	public static String getANSIDATEValue(Date date){
		String dateval = null;
		double julianDate = dateToJulian(date);
		try {
			int val = (int) Math.floor((julianDate-Constant.ANSI_STD_CALCULATOR));
			dateval = val+"";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateval;
	}
	
	public static double dateToJulian(Date date) {
		if(date == null){
			date = getCurrentDateAndTime();
		}
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		int year;
		int month;
		float day;
		int a;
		int b;
		double d;
		double frac;
		frac = (calendar.get(Calendar.HOUR_OF_DAY) / 0.000024 + calendar.get(Calendar.MINUTE) / 0.001440);
		b = 0;
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;

		DecimalFormat ceroPlaces = new DecimalFormat("0");
		day = calendar.get(Calendar.DAY_OF_MONTH);
		day = Float.parseFloat(ceroPlaces.format(day) + "." + ceroPlaces.format(Math.round(frac)));

		if (month < 3) {
			year--;
			month += 12;
		}
		if (compararFechas(calendar.getTime(), calendar.getGregorianChange()) > 0) {
			a = year / 100;
			b = 2 - a + a / 4;
		}
		d = Math.floor(365.25 * year) + Math.floor(30.6001 * (month + 1)) + day + 1720994.5 + b;
		return (d); 
	}
	
	/**
	 * Compare 2 dates. If the first is after the second result will be positive, if the second is after then negative, 0 if they are equal.
	 */
	  public static int compararFechas(Date d1, Date d2) {

	    Calendar c1 = new GregorianCalendar();
	    c1.setTime(d1);
	    Calendar c2 = new GregorianCalendar();
	    c2.setTime(d2);

	    if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) {
	      if (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)) {
	        return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
	      } else {
	        return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
	      }
	    } else {
	      return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
	    }
	  }
	
	
	public static JSONObject getJsonData(String line,String value) throws JSONException {
		try {
			if(line != null && !(line.toString().trim().equalsIgnoreCase(""))) {
				JSONObject jsonObject = new JSONObject(line.toString());
				if(jsonObject != null){
					JSONObject jsonData = jsonObject.getJSONObject(value);
					if(jsonData != null) {
						return jsonData;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return null;
	}
	
	
	public static String getDataFromJSON(final JSONObject jsonData, String info) {
		try {
			if(jsonData.has(info)){
				return jsonData.getString(info);
			}else{
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			return null;
		}
	}
	
	
	public static String ExtentionFilter(String FileName){
    	String ext = "";
		int dotposition= FileName.lastIndexOf(".");
	    ext = FileName.substring(dotposition + 1, FileName.length());  
	    return ext;
	}
	

	public static long TimeConverter(String CurrentBar){
    	if(CurrentBar != null && !CurrentBar.equalsIgnoreCase("")){
	        long MilliSec = 0;	
	    	try{
	    		SimpleDateFormat for_mat = new SimpleDateFormat("HH:mm:ss:yyyy-MM-dd");
	        	Date date1 = for_mat.parse(CurrentBar);
	        	MilliSec = date1.getTime();
		    	return MilliSec;
	    	
	    	}catch(Exception e){
	    		e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
	    	}
    	}
		return 0;
    }
	
	public static Bitmap StringToBitMap(String image, int size) {
		Bitmap bitmap;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = size;

			byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length, options);

		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,
					SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			return null;
		}
		return bitmap;
	}
	
	public static long CurrentTimeProgram(String starttime){
		long UTC = 0;
		if(starttime != null && !starttime.equalsIgnoreCase("")){
			try {
				Date parsed;
				Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
//				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm-yyyyMMdd");		// "20:07-20140902"
//				SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
				parsed = timeFormat.parse(starttime);
				UTC = parsed.getTime();
				if(Constant.DEBUG)  Log.d(TAG , "starttime :"+ starttime+", UTC :"+ UTC);
				return UTC;
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}
		}
		return UTC;
	}
	
	public static String timeFormat(String starttime){
		Date parsed;
		if(starttime != null && !starttime.equalsIgnoreCase("")){
			try {
				Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
				SimpleDateFormat timeFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//				SimpleDateFormat timeFormat2 = new SimpleDateFormat("hh:mm a");
				SimpleDateFormat timeFormat2 = new SimpleDateFormat("hh:mm a dd-MMM-yyyy");
				parsed = timeFormat1.parse(starttime);
				String result = timeFormat2.format(parsed);
				return result;
			}catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}
		}
		return starttime;
	}
	
	public static String dbTimeFormat(String starttime){
		Date parsed;
		if(starttime != null && !starttime.equalsIgnoreCase("")){
			try {
				Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
				SimpleDateFormat timeFormat1 = new SimpleDateFormat("HH:mm-yyyyMMdd");
				SimpleDateFormat timeFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				parsed = timeFormat1.parse(starttime);
				String result = timeFormat2.format(parsed);
				return result;
			}catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}
		}
		return starttime;
	}
	
	
	public static ArrayList<String> Tokenizer(String value,String token){
		ArrayList<String> list = new ArrayList<String>();
		if(value.length()>0){
			StringTokenizer tokens = new StringTokenizer(value, token);
	    	while(tokens.hasMoreTokens()){
	    		list.add(tokens.nextToken());
	    	}
		}
		return list;
	}
	
	
	public static String getTime() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");		
		String currdate = dateFormat.format(c.getTime());
//		if(Constant.DEBUG)  Log.d(TAG,"current date : "+currdate);
        return currdate;
	}
	
	
	public static String getExternalIp() {
		String stringURL = "http://checkip.amazonaws.com/";
		String response = "";
		try {

			URL url = new URL(stringURL);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// get the response from the server and store it in result
			DataInputStream dataIn = null;
			try {
				dataIn = new DataInputStream(connection.getInputStream());
			} catch(Exception e){
				e.printStackTrace();
			}
			if(dataIn != null){
				String inputLine;
				while ((inputLine = dataIn.readLine()) != null) {
					response += inputLine;
				}

				Log.i("ExternalIP", "final response is  : "+response);

				dataIn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response.trim();
	}
}
