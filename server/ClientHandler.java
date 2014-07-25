package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
	private BufferedReader in = null;
    private DataOutputStream out = null;
    Socket clientSocket=null;
    private Task taskThread=null;
	public ClientHandler(Socket clientSocket, Task taskThread){
		this.taskThread=taskThread;
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
		} catch(SocketTimeoutException e){
			System.err.println("Socket Readline Timeout");
			this.taskThread.terminate(-1);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(ret==null){
			System.out.println("Read socket got null");
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
	public void terminate(){
		try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Server.DEBUG){
				e.printStackTrace();
			}
		}
	}
}
