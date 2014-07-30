package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
public class TCPConnection {
	private int timeout=0;
	private Socket clientSocket=null;
	private BufferedReader in = null;
    private DataOutputStream out = null;
	
	private TCPConnection(int timeout){
		this.timeout=timeout;
	}
	public static TCPConnection setUpConnection(String host, int port,int timeout){
			TCPConnection instance=new TCPConnection(timeout);

			try {
				instance.clientSocket=new Socket(host,port);
				instance.clientSocket.setSoTimeout(timeout);
			}catch(SocketException e){
				System.err.println("Set Socket Timeout failed or Server connection refused.");
				//e.printStackTrace();
				System.exit(-1);
			}
			catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
				System.err.println("Unknown Host:" + host +":"+port);
				System.exit(-1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
				System.err.println("TCP IO Error");
				System.exit(-1);
			}
		      try {
		    	  instance.in =new BufferedReader(new InputStreamReader(instance.clientSocket.getInputStream()));
		    	  instance.out = new DataOutputStream(instance.clientSocket.getOutputStream());

		  		} catch (IOException e) {
					// TODO Auto-generated catch block
				 e.printStackTrace();
	  		  }
			return instance;
	}
	public static TCPConnection setUpConnection(Socket clientSocket, int timeout){
		TCPConnection instance = new TCPConnection(timeout);
		instance.clientSocket=clientSocket;
	      try {
	    	  instance.in =new BufferedReader(new InputStreamReader(instance.clientSocket.getInputStream()));
	    	  instance.out = new DataOutputStream(instance.clientSocket.getOutputStream());

	  		} catch (IOException e) {
				// TODO Auto-generated catch block
			 e.printStackTrace();
  		  }
	      return instance;
	}
	
	public boolean sendMessage(String message){
//		try {
//			this.out.writeBytes(message+'\n');
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		return true;
		try {
			return sendBytes(message.getBytes("US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			return false;
		}
	}
	public boolean sendBytes(byte[] message){
		StringBuilder stb=new StringBuilder();
		for(int i : message){
			stb.append(i+" ");
		}
		String str = stb.toString();
	//	System.out.println("Gonna send:\n" +str);
		
		try {
			this.out.writeBytes(str+'\n');
		} catch (IOException e) {
			System.err.println("Send failed");
			System.exit(0);
			return false;
		}
		return true;
	}
	public String readMessage(){
//		//Date date = new Date();
//		//System.out.println("read time: " + date.toString());
//		try {
//			return in.readLine();
//		}catch(SocketTimeoutException e){
//			System.err.println("Socket timeout. timeout=="+timeout);
//			return null;
//			
//		}catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
		return new String(readBytes());
	}
	public byte[] readBytes(){
		byte[] message=null;
		String str = null;
		try{
			str=in.readLine();
			str=str.trim();
		}catch(SocketTimeoutException e){
			System.err.println("Socket timeout. timeout=="+timeout);
			return null;
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String[] strs = str.split(" ");
		message=new byte[strs.length];
		for(int i=0;i<strs.length;i++){
			message[i]=(byte) Integer.parseInt(strs[i]);
		}
		return message;
	}
	public boolean terminate(){

		try {
			this.clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){
				e.printStackTrace();
			}
		}
		clientSocket=null;
		if(Client.DEBUG){
			System.out.println("TCP connection terminated");
		}
		return true;
	}

}
