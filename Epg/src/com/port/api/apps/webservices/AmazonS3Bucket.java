package com.port.api.apps.webservices;

import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.jets3t.service.S3Service;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

import com.port.api.util.Constant;
import com.port.api.util.SystemLog;
//import com.port.api.util.Constant;
//import com.port.api.util.SystemLog;
import com.port.apps.epg.Play;

import android.os.AsyncTask;
import android.util.Log;

public class AmazonS3Bucket extends AsyncTask<String, String, Boolean>{
	private String buffer;
	private S3Bucket bucket = null;
	private S3Service service = null;
	private String TAG = "AmozonS3Bucket";
	
	public AmazonS3Bucket(String msg) {
		buffer = msg;
	}

	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		try {
			
			if (buffer != null) {
				System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
				AWSCredentials credentials = new AWSCredentials(Constant.AMAZON_KEY, Constant.AMAZON_SECRET);
				if(Constant.DEBUG)  Log.d(TAG ,"going to create the instance for S3Service...");
				try{
					service = new RestS3Service(credentials);
					if(Constant.DEBUG) if(Constant.DEBUG)  Log.d(TAG,"going to get  Bucket");
					bucket = service.getBucket(Constant.S3_BUCKET_NAME);
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
				if (bucket == null) {
					if(Constant.DEBUG) if(Constant.DEBUG)  Log.d(TAG,"going to create Bucket");
					bucket = service.createBucket(Constant.S3_BUCKET_NAME);
				}
				String subfol = "urls-" + (System.currentTimeMillis())+"_vs.xml";
				if(Constant.DEBUG) if(Constant.DEBUG)  Log.d(TAG,"xml file Name subfol:"+subfol);
				S3Object stringObject = new S3Object(subfol);
				ByteArrayInputStream xmlIS = new ByteArrayInputStream(buffer.getBytes());
				stringObject.setDataInputStream(xmlIS);
				stringObject.setContentLength(xmlIS.available());
				stringObject.setContentType("text/xml");
				stringObject.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ); //KB added
				service.putObject(bucket, stringObject);
				if(Constant.DEBUG) Log.d(TAG,"sent xml file to cloude : "+subfol);
				return true;
			} 
		} catch (Exception e) {
			if(Constant.DEBUG) if(Constant.DEBUG)  Log.e("Exception", e.toString());
			Play.xmlChannelBuffer = new StringBuffer();
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		try{
			if(result){
				if(Constant.DEBUG)  Log.i(TAG, "sent xml file to cloude successfully");
				Play.xmlChannelBuffer = new StringBuffer();
				Play.xmlStringBuffer = new StringBuffer();
			}else{
				if(Constant.DEBUG)  Log.e(TAG, "sent xml file to cloude unsuccessfully");
				Play.xmlChannelBuffer = new StringBuffer();
				Play.xmlStringBuffer = new StringBuffer();
			}
		} catch (Exception e) {
			if(Constant.DEBUG)  Log.e(TAG, "Error !"+e);
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_WEBSERVICE, errors.toString(), e.getMessage());
		}
	}
}