package com.port;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import android.util.Log;

import com.port.api.network.Listener;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class Producer {
	
	private String TAG = "Producer";	
	private Port port;
	
	public Producer(Port p) {
		port = p;
		if(Constant.DEBUG)  Log.d(TAG  , "BluetoothListener & SocketListener initialized");
		Listener.init(this);
	}
	
	public MessageQueue allow(String pnetwork, String producer, String macID, String consumer, String network){
		//pnetwork is "Bluetooth" in case call from Bluetooth Handler, otherwise it is IP address for inbound Socket listeners
		try{
			if(Port.pairs.size()==0){
				MessageQueue recvq = port.pair(pnetwork, producer,macID,consumer,network);
				return recvq;
				
			}else {
				//check if some other producer is linked to consumer. if linked, return false, otherwise pair
				String pElement = null,mElement = null,cElement = null,nElement = null;
				
				for (int i = 0; i < Port.pairs.size(); i++) {
					HashMap<String, String> map = Port.pairs.get(i);
					pElement = map.get("Producer");
					mElement = map.get("macID");
					cElement = map.get("Consumer");
					nElement = map.get("Network");
			    	if(!producer.equalsIgnoreCase(pElement) && (consumer.equalsIgnoreCase(cElement) && network.equalsIgnoreCase(nElement))){
			    		return null; // do not allow pairing
			    	}else {
			    		MessageQueue recvq = port.pair(pnetwork, pElement,mElement,cElement,nElement);
			    		return recvq; //allow pairing even if there are many consumers for a single producer
			    	}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
		}
		return null;
		
	}
	

}
