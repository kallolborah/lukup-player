package com.port;

import org.json.JSONObject;

import android.util.Log;

import com.port.api.util.Constant;

public class Channel {

	MessageQueue q;
	MessageQueueElement mq;
	private String TAG = "CHANNEL";
	
	public Channel(String producer, String dockID){
		
		q = MessageQueue.getInstance("sender");
		mq = new MessageQueueElement();
		mq.setProducer(producer);
		mq.setMacId(dockID);
		if(Constant.DEBUG)  Log.d(TAG , "Channel initialized from - " + producer+dockID);
	}

	public void set(String consumer, String network, String caller){
		if(Constant.DEBUG)  Log.d(TAG , "Channel initialized to - " + consumer+ " " + network);
		mq.setConsumer(consumer);
		mq.setNetwork(network);
		mq.setCaller(caller);
	}

	public void add(String method, JSONObject params, String called){
		try {
			if(Constant.DEBUG)  Log.d(TAG , "Channel add JSONObject - " + params.toString());
			if(Constant.DEBUG)  Log.d(TAG , "Channel called - " + called+", method - " + method);
			mq.setMethod(method);
			mq.setJson(params);
			mq.setCalled(called);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean send() throws InterruptedException{	
		if(Constant.DEBUG)  Log.d(TAG , "send()");
		q.push(mq);
		Consumer.getInstance(q);
		return true;
	}
	
}
