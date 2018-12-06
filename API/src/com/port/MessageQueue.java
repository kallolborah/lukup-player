package com.port;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.port.MessageQueueElement;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

public class MessageQueue {
	
	private String TAG = "MESSAGE-QUEUE";
	private List<MessageQueueElement> list;
	private static MessageQueue mq=null;
	private static MessageQueue rq = null;
	private static MessageQueue sq = null;
	public ArrayList<HashMap<String,String>> paramsData;
	private boolean initialized =false;
	
	protected MessageQueue(){
		//prevent instantiation
	}
	
	public static MessageQueue getInstance(String type){
		
		if(type.equalsIgnoreCase("receiver")){
			if(rq==null){
				rq = new MessageQueue();
				rq.list = new LinkedList<MessageQueueElement>();
			}
			mq = rq;
		}else if(type.equalsIgnoreCase("sender")){
			if(sq==null){
				sq = new MessageQueue();
				sq.list = new LinkedList<MessageQueueElement>();
			}
			mq = sq;
		}	
		
		return mq;
	}	
	
	/**
	 * Data pushed into activation queue.
	 * @param data
	 * @throws InterruptedException 
	 */
	synchronized public void push(MessageQueueElement data) throws InterruptedException{
		if(Constant.DEBUG)  Log.d(TAG, "push data to MessageQueue : "+data.toString());
		
		if(list.size() >25){
			wait();			
		}else{
			list.add(data);
			notify();
		}
		return;
//		Collections.sort(list, new Comparator<MessageQueueElement>(){
//			@Override
//			public int compare(MessageQueueElement object1, MessageQueueElement object2) {
//				int lpriority = object1.getPriority();
//				int rpriority = object2.getPriority();
//				if(lpriority > rpriority)
//					return -1;
//				else if(lpriority < rpriority)
//					return 1;
//				else
//					return 0;
//			}
//
//		});
	}

	synchronized public void removeAllData(){
		if(list != null && list.size() >0){
			for(int i=0;i<list.size();i++){
				list.remove(0);
			}
		}
	}

	/**
	 * pops data from queue
	 * @return returns event data pushed.AsyncTask
	 * @throws InterruptedException 
	 */
	synchronized public MessageQueueElement pop() throws InterruptedException{
		MessageQueueElement data = null;
		if(list.size() > 0){
			data = list.remove(0);
			if(data!=null){
				if(Constant.DEBUG)  Log.d(TAG, "pop data from MessageQueue : "+data.toString());
			}
		}else{
			wait();
		}
		return data;
	}

	public List<MessageQueueElement> getAllData(){
		return list;
	}
	
	public void receive(final String pnetwork, final Port p) {		
		new Thread() {
				@Override
				public void run(){					
					
					while(true) { //keep calling handlers
						try{
							MessageQueueElement data = pop();
							
							if(data != null){
								
								String method = data.getMethod(); 
								int index = method.lastIndexOf('.');
								String PackageClass = method.substring(0, index);
								String MethodName = method.substring(index + 1);
							
								p.processMessage(PackageClass, MethodName, data, pnetwork);
							}
						}catch(Exception e){
							e.printStackTrace();
							StringWriter errors = new StringWriter();
							e.printStackTrace(new PrintWriter(errors));
							SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
						}
					}					
				}
			}.start();
		}		
	
}
