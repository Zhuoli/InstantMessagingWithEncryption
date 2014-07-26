package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
			System.out.println("Setting up connection...");
			TCPConnection instance=new TCPConnection(timeout);

			try {
				System.out.println("Setting up connection...new socket: " +host+"/"+port);
				instance.clientSocket=new Socket(host,port);
				System.out.println("Setting up connection...setSoTimeout");
				instance.clientSocket.setSoTimeout(timeout);
				System.out.println("Setting up connection...");
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

		  		 System.out.println("Setting up connection...");
		  		} catch (IOException e) {
					// TODO Auto-generated catch block
				 e.printStackTrace();
	  		  }
		      System.out.println("Setted up connection");
			return instance;
	}
	
	public boolean sendMessage(String message){
		try {
			this.out.writeBytes(message+'\n');
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public String readMessage(){
		//Date date = new Date();
		//System.out.println("read time: " + date.toString());
		try {
			return in.readLine();
		}catch(SocketTimeoutException e){
			System.err.println("Socket timeout. timeout=="+timeout);
			return null;
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
