package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPConnection {
	private Socket MyClient=null;
	private DataOutputStream out2server= null;
	private BufferedReader inFromServer=null;
	private Socket clientSocket=null;
	
	protected TCPConnection(String host, int port){
		try {
		    clientSocket=new Socket(host,port);
			out2server = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(
							clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	public static TCPConnection getInstance(String host, int port){
			return new TCPConnection(host,port);
	}
	
	public boolean sendMessage(String message){

		try {
			this.out2server.writeBytes(message);
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
			ret = this.inFromServer.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public boolean close(){
		try {
			this.clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
