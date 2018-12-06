package com.port.api.network;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.port.MessageQueue;
import com.port.MessageQueueElement;
import com.port.Producer;
import com.port.api.network.bt.BluetoothConnectionService;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class Listener extends Handler {
	
	private static String TAG = "BluetoothListener";
	private static MessageQueue recvq = null;
	private static Producer producer = null;
	public static String readBTMessage = "";
	private static ArrayList<HashMap<String,Object>> pairs = new ArrayList<HashMap<String,Object>>();
	
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.MESSAGE_STATE_CHANGE:
				if(Constant.DEBUG)  Log.d(TAG , "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothConnectionService.STATE_CONNECTED:
					if(Constant.DEBUG)  Log.d(TAG, "MESSAGE_STATE_CHANGE: STATE_CONNECTED");
					break;
				case BluetoothConnectionService.STATE_CONNECTING:
					if(Constant.DEBUG)  Log.d(TAG, "MESSAGE_STATE_CHANGE: STATE_CONNECTING");
					break;
				case BluetoothConnectionService.STATE_LISTEN:
					if(Constant.DEBUG)  Log.d(TAG, "MESSAGE_STATE_CHANGE: STATE_LISTEN");
					break;
				case BluetoothConnectionService.STATE_NONE:
					if(Constant.DEBUG)  Log.d(TAG, "MESSAGE_STATE_CHANGE: STATE_NONE");
					break;
				}
				break;

			case Constant.MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				String readMessage = new String(readBuf, 0, msg.arg1);	
				writeMessage(readMessage);
				break;
			case Constant.MESSAGE_DEVICE_NAME:
				// save the connected device's name
				break;
			case Constant.MESSAGE_TOAST:
				readBTMessage = "";
				break;
			}
		}

	
	public static void init(Producer p){
		if(Constant.DEBUG)  Log.d(TAG  , "call init()");
		producer = p;
	}
	
	
	private void writeMessage(String readMessage){		
		try{
			if(readBTMessage==""){
				readBTMessage = readMessage;
	//			if(Constant.DEBUG)  Log.v("writeMessage","Return after creating new read string " + readBTMessage);
				if(!checkValidJson(readBTMessage.trim())){
					return;
				}
			}else{
				readBTMessage = readBTMessage + readMessage;
				if(!checkValidJson(readBTMessage.trim())){
	//				if(Constant.DEBUG)  Log.v("writeMessage","Return after appending " + readBTMessage); 
					return;
				}
			}
			if(readBTMessage != null && !readBTMessage.equalsIgnoreCase("")){
	//			if(Constant.DEBUG)  Log.v("writeMessage","Valid Receiving Message:  " + readBTMessage);
				try {
					sendMessagetoQueue(readBTMessage);
				} catch (JSONException e) {
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
				}
				readBTMessage = "";
			}
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
		}
	}
	
	private boolean checkValidJson(String data){
		try{
			new JSONObject(data);			
			return true;
		}catch(JSONException e){
			return false;
		}
		
	}
	
	private static void sendMessagetoQueue(String message) throws JSONException{
		String pname = "";
		String id = "";
		String cname = "";
		String network = "";
		String handler = "";
		String called = "";
		String caller = "";

		if(Constant.DEBUG)  Log.d(TAG , "incoming to sendMessagetoQ " + message.toString().trim());
		if(message!=null && !(message.toString().trim().equalsIgnoreCase(""))) {
			try {
				JSONObject objData = new JSONObject(message.toString());
				JSONObject jsonObject = objData.getJSONObject("data");
				if(jsonObject != null){
					MessageQueueElement messageQ = new MessageQueueElement();
					if(jsonObject.has("producer")){
						pname = jsonObject.getString("producer");
					}if(jsonObject.has("macID")){
						id = jsonObject.getString("macID");
					}if(jsonObject.has("consumer")){
						cname = jsonObject.getString("consumer");
					}if(jsonObject.has("network")){
						network = jsonObject.getString("network");
					}if(jsonObject.has("handler")){
						handler = jsonObject.getString("handler");	
					}if(jsonObject.has("caller")){
						caller = jsonObject.getString("caller");	
					}if(jsonObject.has("called")){
						called = jsonObject.getString("called");	
					}
					
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
						messageQ.setCaller(caller);
						messageQ.setCalled(called);
						messageQ.setJsonParams(jsonObject.getString("params"));
						
						HashMap<String, Object> map = pairs.get(index);
						MessageQueue recvQ = (MessageQueue) map.get("Queue");
						recvQ.push(messageQ);
						if(Constant.DEBUG)  Log.d(TAG , "New request posted to recvq");
						
					}else{ //otherwise, check if allowed
						recvq = producer.allow(network, pname, id, cname, network); 
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
							messageQ.setCaller(caller);
							messageQ.setCalled(called);
							messageQ.setJsonParams(jsonObject.getString("params"));
							recvq.push(messageQ);
							if(Constant.DEBUG)  Log.d(TAG , "New request posted to recvq");
						}
						
					}					
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
			}
			
			
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
	
}
