package com.port.api.network.bonjour;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.prefs.Preferences;

import com.port.api.util.SystemLog;

//import com.lukup.utils.Preferences;

import android.content.Context;
import android.util.Log;


public class SocketTransmitter {

	private static final String TAG = "SocketTransmitter";
	private Context mcontext;
	private Preferences pref;
	private static final int PORT = 15834; //should go into constants
	public SocketTransmitter(){
		
	}
	
	public SocketTransmitter(Context context) {
		mcontext = context;
	}

	public static void sendMessage(final String data, final String Client_IP){
		//pref = new Preferences(mcontext);
		new Thread() {
			@Override
			public void run(){
				try{
					Log.d(TAG, "JSON Transmit to Remote : "+data);
					Socket socket = null;
					for(int i=0; i<3; i++){
						try{
							socket = new Socket(Client_IP, PORT);
							Log.w(TAG, "Socket: "+socket);
						}catch(Exception exc){
							exc.printStackTrace();
							socket= null;
						}
						if(socket != null) {
							break;
						}
					}

					if(socket == null) {
						Log.d(TAG, "socket transmitter connection is not establishing...");
						return ;
					} else {
						Log.d(TAG, "socket transmitter connection is establishing...");
						PrintWriter pw = new PrintWriter(socket.getOutputStream());
						pw.println(data);
						pw.flush();
						pw.close();
					}
					socket.close();
				}catch(final Exception e){
					e.printStackTrace();
					StringWriter errors = new StringWriter();
					e.printStackTrace(new PrintWriter(errors));
					SystemLog.createErrorLogXml(SystemLog.TYPE_DOCK,SystemLog.LOG_ETHERNET, errors.toString(), e.getMessage());
				}
			}
		}.start();
	}
}
