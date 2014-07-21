package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPConnection {
	static TCPConnection instance=null;
	private Socket MyClient=null;
	public DataOutputStream out2server= null;
	public BufferedReader inFromServer=null;
	final String ServerName = "127.0.0.1";
	final int port = 2048;
	
	private TCPConnection(){
	    try {
	           MyClient = new Socket(ServerName, port);
	    }
	    catch (IOException e) {
	        System.out.println(e);
	        System.exit(-1);
	    }
	    try {
			out2server=new DataOutputStream(instance.MyClient.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			inFromServer = new BufferedReader(new InputStreamReader(instance.MyClient.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	public static TCPConnection getInstance(){
		if(instance==null){
			instance=new TCPConnection();
		}
		return instance;
	}
	
	public boolean write2server(String message){

		try {
			instance.out2server.writeBytes(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){
				e.printStackTrace();
			}
			return false;
		}
		return true;
	}
	
	public String readMessage(){
		String ret="";
		try {
			instance.inFromServer.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
}
