package com.port.api.network.wifidirect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.port.Port;
import com.port.api.network.Listener;
import com.port.api.util.Constant;
import com.port.api.util.SystemLog;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class WifiP2PConnectionService extends Service {
	
	// Debugging
	private static final String TAG = "WifiP2PConnectionService";
	
	private Handler mHandler; 
	private WifiConnectionThread ConnectionThread;
	
	public final IBinder mBinder = (IBinder) new WifiBinder();

	public class WifiBinder extends Binder {
		public WifiP2PConnectionService getService() {
            // Return this instance of LocalService so clients can call public methods
			if (Constant.DEBUG) Log.d(TAG, "returning Wifi direct binder");
			mHandler = new Listener();
			return WifiP2PConnectionService.this ;
        }
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	public synchronized void start() throws IOException {
		if (Constant.DEBUG) Log.d(TAG, "starting WifiP2P Listener");
		
		// Start the thread to listen on a ServerSocket
		if (ConnectionThread == null) {
			ConnectionThread = new WifiConnectionThread();
			ConnectionThread.start();
		}
		
	}
	
	public synchronized void stop() {
		if (Constant.DEBUG) Log.d(TAG, "stop");

		if (ConnectionThread != null) {ConnectionThread.cancel(); ConnectionThread = null;}
	}
	
	private class WifiConnectionThread extends Thread {
		// The local server socket
		private final ServerSocket mmServerSocket;
		private OutputStream os;
		private InputStream is;
		
		public WifiConnectionThread() throws IOException {
			Log.i(TAG, "Opening new server socket");
			mmServerSocket = new ServerSocket(8988);
		}

		@Override
		public void run() {
			Socket client;
			try {
				client = mmServerSocket.accept();
				if(client!=null){
					Log.i(TAG, "Accepting connection on server socket");
					is = client.getInputStream();
					os = client.getOutputStream();
					while(true){
						Log.i(TAG, "Begin reading WifiP2P inputstream");
						byte[] buffer = new byte[1024];
						int bytes;

						// Keep listening to the InputStream while connected
						while (true) {
							try {
								// Read from the InputStream
								bytes = is.read(buffer);
								if(buffer.length>0){
									// Send the obtained bytes to the UI Activity
									mHandler.obtainMessage(Constant.MESSAGE_READ, bytes, -1, buffer)
									.sendToTarget();
								}else{
									break;
								}
									
							} catch (IOException e) {
								Log.e(TAG, "disconnected", e);
								e.printStackTrace();
								StringWriter errors = new StringWriter();
								e.printStackTrace(new PrintWriter(errors));
								SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
								connectionLost();
								break;
							}
						}				
						Listener.readBTMessage ="";
					}	
//					Log.i(TAG, "Port is not wifi bound, so can't read from input stream");
				}else
					Log.i(TAG, "Something is wrong with accepting connections on server socket");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void cancel() {
			if (Constant.DEBUG) Log.d(TAG, "cancel " + this);
			try {
				mmServerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
			}
		}
		
		synchronized public void write(byte[] buffer) {
			try {
				Listener.readBTMessage ="";
				os.write(buffer);

			} catch (IOException e) {
				Log.e(TAG, "Exception during write", e);
				e.printStackTrace();
				StringWriter errors = new StringWriter();
				e.printStackTrace(new PrintWriter(errors));
				SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
			}
		}
	}
	
	private void connectionLost() {
		try{
			// Send a failure message back to the Activity
			Message msg = mHandler.obtainMessage(Constant.MESSAGE_LOST);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.TOAST, "Device connection was lost");
			msg.setData(bundle);
			mHandler.sendMessage(msg);
			if (Constant.DEBUG) Log.d(TAG, "Wifi P2P connection lost ");
			stop();
			start();
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
		}
	}

	synchronized public void write(byte[] out) {
		try{
			// Create temporary object
			WifiConnectionThread r;
			// Synchronize a copy of the ConnectedThread
			if (Constant.DEBUG) Log.d(TAG, "checking state before sending message");
			synchronized (this) {
				if (ConnectionThread == null) return;
				r = ConnectionThread;
			}
			// Perform the write unsynchronized
			r.write(out);
		}catch (Exception e) {
			e.printStackTrace();
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
		}
	}
	
	@Override
    public void onCreate(){
        super.onCreate();
        IntentFilter app = new IntentFilter("send.data.wifi");
        registerReceiver(mReceiver,app);
    }
   
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            if(extras != null){
                try {
                    if(extras.containsKey("Value")){
                        String value = extras.getString("Value");
                        if(Constant.DEBUG)  Log.d(TAG , "JSON:  "+value);
                        byte[] send = value.getBytes();
                        try{
                            write(send);
                        }catch(Exception e){
                            e.printStackTrace();
                            StringWriter errors = new StringWriter();
                            e.printStackTrace(new PrintWriter(errors));
                            SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_BT, errors.toString(), e.getMessage());
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    StringWriter errors = new StringWriter();
                    e.printStackTrace(new PrintWriter(errors));
                    SystemLog.createErrorLogXml(SystemLog.TYPE_PLAYER,SystemLog.LOG_APPLICATION, errors.toString(), e.getMessage());
                }
            }
        }
    };
   
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mReceiver != null){
            if(Constant.DEBUG)  Log.d(TAG , "onDestroy() mReceiver");
            unregisterReceiver(mReceiver);
        }
    }
	
}
