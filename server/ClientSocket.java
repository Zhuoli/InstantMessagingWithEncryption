package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientSocket {
	private BufferedReader in = null;
    private DataOutputStream out = null;
    Socket clientSocket=null;
	public ClientSocket(Socket clientSocket){
	      try {
	    	  in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			  out = new DataOutputStream(clientSocket.getOutputStream());
		  } catch (IOException e) {
				// TODO Auto-generated catch block
			 e.printStackTrace();
  		  }
	}
	
	public String readMessage(){
		String ret=null;
		try {
			ret=in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  ret;
	}
	public boolean writeMessage(String message){
		try {
			out.writeBytes(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public void close(){
		try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
