package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	static int PortNumber=2048;
	static TCPServer server=null;
	BufferedReader inFromClient = null;
	DataOutputStream outToClient=null;
	
	
	private TCPServer(){
		ServerSocket server=null;
		Socket connection=null;
		try {
			 server = new ServerSocket(PortNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		try {
		 connection=server.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			inFromClient = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			outToClient=new DataOutputStream(connection.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static TCPServer getInstance(){
		if(TCPServer.server==null){
			TCPServer.server=new TCPServer();
		}
		return TCPServer.server;
	}
	
	public String readMessage(){
		String ret="";
		try {
			ret=server.inFromClient.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		return ret;
	}
	public boolean sendMessage(String message){
		try {
			server.outToClient.writeBytes(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
