package com.port.api.network.bonjour;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.port.MessageQueue;
import com.port.MessageQueueElement;
import com.port.Producer;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;


public class SocketListener extends Service{
	private static final String TAG = "SocketListener";
	private DataInputStream dataInputStream;
	private ServerSocket serverSocket;
	private static final int PORT = 15834; //should go into constants
	private static volatile boolean running = true;
	private static Producer producer;
	private static ArrayList<HashMap<String,Object>> pairs = new ArrayList<HashMap<String,Object>>();
	private static MessageQueue recvq = null;

	@Override
	public void onCreate() {
		super.onCreate();
		running = true;
		if(Constant.DEBUG)  Log.d(TAG , "on create in SocketListener");
		rfListen();
	}


	public void rfListen() {
		dataInputStream = null;
		serverSocket = null;

		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		new Thread() {
			@Override
			public void run(){
				Socket socket = null;
				while(running) {

					try {
						
						if(serverSocket != null && !serverSocket.isClosed()) {
							socket = serverSocket.accept();	
						} else {
							socket = null;
							serverSocket = new ServerSocket(PORT);
						}
						if(Constant.DEBUG)  Log.d(TAG , "SocketListener started");
					} catch(Exception e){
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
						try {
							if(socket != null){
								socket.close();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						socket = null;
						continue;
					}
					
					try {
						
						dataInputStream = new DataInputStream(socket.getInputStream());
						BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
						String str = null;
						
						StringBuilder sb = new StringBuilder();
						while((str = reader.readLine())!= null){
							sb.append(str);
						}
						str = sb.toString();
						if(Constant.DEBUG)  Log.d(TAG , "Incoming request is : "+str);
						reader.close();
						JSONObject jsonObject = getObjectFromJson(sb.toString().trim(), "data");
						if(jsonObject != null){
							MessageQueueElement messageQ = new MessageQueueElement();
							String pname = jsonObject.getString("producer");
							String id = jsonObject.getString("macID");
							String cname = jsonObject.getString("consumer");
							String network = jsonObject.getString("network");
							String handler = jsonObject.getString("handler");	
							
							boolean flag = false;
							int index = -1;
							if(pairs.size()>0){
								for (int i = 0; i < pairs.size(); i++) {
									HashMap<String, Object> map = pairs.get(i);
									if(pname.equals(map.get("Producer").toString()) && id.equals(map.get("macID").toString()) && cname.equals(map.get("Consumer").toString())
											&& network.equals(map.get("Network").toString())){
										flag = true;
										index = i;
									}
								}
							}
							
							if(flag){
								if(Constant.DEBUG)  Log.d(TAG , "incoming producer is already allowed");
								//check if already allowed, then get recvq
								messageQ.setProducer(pname+id);
								messageQ.setConsumer(cname);
								messageQ.setNetwork(network);
								messageQ.setMethod(handler);
								messageQ.setJsonParams(jsonObject.getString("params"));
								
//								JSONArray dataElement = null;
//								String[][] paramsArray = null;
//								dataElement = jsonObject.getJSONObject("data").getJSONArray("params");
//								for (int i = 0; i < dataElement.length(); i++) {
//									String key = dataElement.getJSONObject(i).keys().toString();
//									String Value = dataElement.getJSONObject(i).getString(key);
//									paramsArray[i][0] = key;
//									paramsArray[i][1] = Value;
//									messageQ.setParams(paramsArray);
//								}
								HashMap<String, Object> map = pairs.get(index);
								MessageQueue recvQ = (MessageQueue) map.get("Queue");
								recvQ.push(messageQ);
								if(Constant.DEBUG)  Log.d(TAG , "New request posted to recvq");
								
							}else{ //otherwise, check if allowed
								recvq = producer.allow("Wifi", pname, id, cname, network); 
								if(Constant.DEBUG)  Log.d(TAG , "checking if producer is allowed");
								if(recvq!=null){ //if allowed	
									HashMap<String, Object> list = new HashMap<String, Object>();
									list.put("Producer",pname);
									list.put("macID", id);
									list.put("Consumer", cname);
									list.put("Network", network);
									list.put("Queue", recvq);
									pairs.add(list);
									
									messageQ.setProducer(pname+id);
									messageQ.setConsumer(cname);
									messageQ.setNetwork(network);
									messageQ.setMethod(handler);
									messageQ.setJsonParams(jsonObject.getString("params"));
									
//									JSONArray dataElement = null;
//									String[][] paramsArray = null;
//									dataElement = jsonObject.getJSONObject("data").getJSONArray("params");
//									for (int i = 0; i < dataElement.length(); i++) {
//										String key = dataElement.getJSONObject(i).keys().toString();
//										String Value = dataElement.getJSONObject(i).getString(key);
//										paramsArray[i][0] = key;
//										paramsArray[i][1] = Value;
//										messageQ.setParams(paramsArray);
//									}
									recvq.push(messageQ);
									if(Constant.DEBUG)  Log.d(TAG , "New request posted to recvq");
								}
								
							}					
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						StringWriter errors = new StringWriter();
						e.printStackTrace(new PrintWriter(errors));
						SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
					}

					finally{
						if( dataInputStream!= null){
							try {
								dataInputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if( socket != null){
							try {
								socket.close();
							} catch (IOException e) {
								e.printStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
							}
						}
					}
				}
			}
		}.start();
	}
	
	public static JSONObject getObjectFromJson(String obj1,String key){
		JSONObject value = null;
		try {
			JSONObject obj = new JSONObject(obj1);
			if(obj  != null && obj.has(key)){
				value = obj.getJSONObject(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		
		}
		return value;
		
	}

	public static void init(Producer p){
		if(Constant.DEBUG)  Log.d(TAG  , "call init()");
		producer = p;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		running = false;
		try{
			if(serverSocket != null){
				serverSocket.close();
				serverSocket = null;
			}
		}catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
		}
		super.onDestroy();
	}
}
