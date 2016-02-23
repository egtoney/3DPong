package com.ethan.pong.lazer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.TimerTask;

public class LazerCommunication extends TimerTask{

	public static final String HOSTNAME = "76.65.90.82";
	public static final int PORT = 30000;
	public static final int TIMEOUT = 10000;

	private DataOutputStream out;
	private Socket piSocket = null;
	public static String data = "";
	
	public LazerCommunication(){
		init();
	}
	
	private void init(){
		try {
			if( piSocket != null )
				piSocket.close();
			
			piSocket = new Socket();
			piSocket.connect(new InetSocketAddress(HOSTNAME, PORT), 10);
			
			out = new DataOutputStream(piSocket.getOutputStream());
			BufferedReader in = new BufferedReader( new InputStreamReader(piSocket.getInputStream()));
				
		} catch (UnknownHostException e){
//			System.err.println("Don't know about host " + HOSTNAME);
            
		} catch (IOException e) {
			e.printStackTrace();
//			System.err.println("Couldn't get I/O for the connection to " + HOSTNAME);
		}
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Send "+data.length()+" numbers");
			String message = data+((char)255);
//			String message = data;
			
			out.write(message.getBytes("US-ASCII"));
			out.flush();
			
		} catch (IOException e) {
			init();
			e.printStackTrace();
		}
	}
		
}
