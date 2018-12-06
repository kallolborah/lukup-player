package com.port.apps.epg.util;

import java.io.DataInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import com.port.api.db.service.ChannelInfo;
import com.port.api.db.service.ProgramInfo;
import com.port.api.db.util.CacheData;
import com.port.api.util.SystemLog;


import android.util.Log;

public class AdsStatistics {
	private String TAG = "AdsStatistics";
	
	public static String getXmlStartDetails(String contenttype,ChannelInfo channelInfo,ProgramInfo programInfo, String id, String name, long duration,long tDuration, 
			long starttime, String discoveryBy, String format, String action, String entryfrom){
		StringBuffer sb = new StringBuffer();
		try{
			if(contenttype.equals("program")){
				sb.append("<program>");
				if (programInfo != null) {
					if(programInfo.getProgramId()!=-1){
						sb.append("<id><![CDATA["+programInfo.getProgramId()+"]]></id>");
					}
					if(programInfo.getEventName()!=null && programInfo.getEventName()!=""){
						sb.append("<name><![CDATA["+programInfo.getEventName()+"]]></name>");
					}
					if(programInfo.getGenre()!=null && programInfo.getGenre()!=""){
						sb.append("<genre><![CDATA["+programInfo.getGenre()+"]]></genre>");
					}
					if(programInfo.getDescription()!=null && programInfo.getDescription()!=""){
						sb.append("<info><![CDATA["+programInfo.getDescription()+"]]></info>");
					}
				}else{
					sb.append("<id><![CDATA[ ]]></id>");
					sb.append("<name><![CDATA[ ]]></name>");
					sb.append("<genre><![CDATA[ ]]></genre>");
					sb.append("<info><![CDATA[ ]]></info>");
				}
				
				if (starttime>0) {
					sb.append("<starttime><![CDATA["+starttime+"]]></starttime>");
				}
				
				if(duration>0){
					long seconds = duration/1000;
					long val = seconds % 60;
					
					String s = "00";
					if (val<=9) { s = "0"+val;
					}else{ s = val+"";}
					
				    val = (seconds / 60) % 60;
				    String m = "00";
				    if (val<=9) { m = "0"+val;
					}else{ m = val+"";}
				    
				    val = (seconds / (60 * 60)) % 24;
				    String h = "00";
				    if (val<=9) { h = "0"+val;
					}else{ h = val+"";}
				    
				    sb.append("<endtime><![CDATA["+ h + ":" + m + ":" +s +"]]></endtime>");
				}
				
				if(tDuration>0){
					long seconds = tDuration/1000;
					long val = seconds % 60;
					
					String s = "00";
					if (val<=9) { s = "0"+val;
					}else{ s = val+"";}
					
				    val = (seconds / 60) % 60;
				    String m = "00";
				    if (val<=9) { m = "0"+val;
					}else{ m = val+"";}
				    
				    val = (seconds / (60 * 60)) % 24;
				    String h = "00";
				    if (val<=9) { h = "0"+val;
					}else{ h = val+"";}
					sb.append("<totalduration><![CDATA["+ h + ":" + m + ":" +s +"]]></totalduration>");
				}
				
				if(action!=null && !action.equalsIgnoreCase("")){
					sb.append("<action><![CDATA["+action+"]]></action>");
				}
				if(entryfrom!=null && !entryfrom.equalsIgnoreCase("")){
					sb.append("<entryfrom><![CDATA["+entryfrom+"]]></entryfrom>");
				}
			} else if(contenttype.equals("ad")){
				sb.append("<ad>");
				if(name!=null && !name.equalsIgnoreCase("")){
					sb.append("<name><![CDATA["+name+"]]></name>");
				}
				if(format!=null && !format.equalsIgnoreCase("")){
					sb.append("<format><![CDATA["+format+"]]></format>");
				}
				if(action!=null && !action.equalsIgnoreCase("")){
					sb.append("<action><![CDATA["+action+"]]></action>");
				}
				if(tDuration>0){
					long seconds = tDuration/1000;
					long val = seconds % 60;
					
					String s = "00";
					if (val<=9) { s = "0"+val;
					}else{ s = val+"";}
					
				    val = (seconds / 60) % 60;
				    String m = "00";
				    if (val<=9) { m = "0"+val;
					}else{ m = val+"";}
				    
				    val = (seconds / (60 * 60)) % 24;
				    String h = "00";
				    if (val<=9) { h = "0"+val;
					}else{ h = val+"";}
					sb.append("<adduration><![CDATA["+ h + ":" + m + ":" +s +"]]></adduration>");
				}
				if(duration>0){
					long seconds = duration/1000;
					long val = seconds % 60;
					
					String s = "00";
					if (val<=9) { s = "0"+val;
					}else{ s = val+"";}
					
				    val = (seconds / 60) % 60;
				    String m = "00";
				    if (val<=9) { m = "0"+val;
					}else{ m = val+"";}
				    
				    val = (seconds / (60 * 60)) % 24;
				    String h = "00";
				    if (val<=9) { h = "0"+val;
					}else{ h = val+"";}
					sb.append("<adviewed><![CDATA["+ h + ":" + m + ":" +s +"]]></adviewed>");
				}
			} else if(contenttype.equals("channel")){
				sb.append("<channel>");
				if(channelInfo.getServiceId()!=-1){
					sb.append("<id><![CDATA["+channelInfo.getServiceId()+"]]></id>");
				}
				if(channelInfo.getChannelName()!=null && channelInfo.getChannelName()!=""){
					sb.append("<name><![CDATA["+channelInfo.getChannelName()+"]]></name>");
				}
				sb.append("<language><![CDATA["+""+"]]></language>");
				if(channelInfo.getType()!=null && channelInfo.getType()!=""){
					sb.append("<type><![CDATA["+channelInfo.getType()+"]]></type>");
				}
				if(discoveryBy!=null && discoveryBy!=""){
					sb.append("<discoveryby><![CDATA["+discoveryBy+"]]></discoveryby>");
				}
				if(channelInfo.getServiceCategory()!=null && channelInfo.getServiceCategory()!=""){
					sb.append("<category><![CDATA["+channelInfo.getServiceCategory()+"]]></category>");
				}
				if(entryfrom!=null && entryfrom!=""){
					sb.append("<entryfrom><![CDATA["+entryfrom+"]]></entryfrom>");
				}
				sb.append("<entrydatetime><![CDATA["+starttime+"]]></entrydatetime>");
	
				String externalIp = com.port.api.db.util.CacheData.getExternalIp();
				String device = "Player";
	
				if(externalIp != null && externalIp.equalsIgnoreCase("")){
					externalIp = "0.0.0.0";
				}
	
				String network = "";
				if(channelInfo.getType().equalsIgnoreCase("live") && CacheData.getOperaterName() != null){
					network = CacheData.getOperaterName();
				}else{
					network = "Lukup";
				}
				sb.append("<network><![CDATA["+network+"]]></network>");
				sb.append("<externalip><![CDATA["+externalIp+"]]></externalip>");
				sb.append("<device><![CDATA["+device+"]]></device>");
				
			}
			return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return sb.toString();
	}
	
	public static String getXmlEndDetails(long endtime, String exitto, String contenttype){
		StringBuffer sb = new StringBuffer();
		try{
			if(contenttype.equals("program")){
				sb.append("<exitto><![CDATA["+exitto+"]]></exitto>");
				sb.append("</program>");
			} else if(contenttype.equals("ad")){
				sb.append("</ad>");
			} else if(contenttype.equals("channel")){
				sb.append("<exitdatetime><![CDATA["+endtime+"]]></exitdatetime>");
				sb.append("<exitto><![CDATA["+exitto+"]]></exitto>");
				sb.append("</channel>");
			}
			return sb.toString();
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return sb.toString();
		
	}
		
	public static String getExternalIp() {
		String stringURL = "http://api.externalip.net/ip/";
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
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
		}
		return response.trim();
	}
	
}
