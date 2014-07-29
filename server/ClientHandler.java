package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class ClientHandler {
	private BufferedReader in = null;
    private DataOutputStream out = null;
    
    Socket clientSocket=null;
    private Task taskThread=null;
	public ClientHandler(Socket clientSocket, Task taskThread){
		this.taskThread=taskThread;
		this.clientSocket=clientSocket;
	      try {
	    	  in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			  out = new DataOutputStream(clientSocket.getOutputStream());
		  } catch (IOException e) {
				// TODO Auto-generated catch block
			 e.printStackTrace();
  		  }
	}
	public String getClientIPAddress(){
		try{
			if(clientSocket==null){
				System.out.println("clientSocket is null");
			}
			SocketAddress s= clientSocket.getRemoteSocketAddress();
			if(s==null){
				System.out.println("Socket unconnected");
			}
			return s.toString().split(":")[0].substring(1);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public String readMessage(){
//		String ret=null;
//		try {
//			ret=in.readLine();
//		} catch(SocketTimeoutException e){
//			System.err.println("Socket Readline Timeout");
//			this.taskThread.terminate();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if(ret==null){
//			System.out.println("Read socket got null");
//		}
//		return  ret;
		return new String(readBytes());
	}
	public byte[] readBytes(){
		byte[] message=null;
		int len;
		try {
			len = Integer.parseInt(in.readLine());
			System.out.println("Gonna read length: "+len);
			if(len>0){
				message=new byte[len];
				for(int i=0;i<len;i++){
					message[i]=(byte) in.read();
				}
			}else{
				System.err.println("read error");
				System.exit(0);
			}
		} catch (IOException e) {
			System.err.println("read error");
			return message;
		}
		return message;
	}
	public boolean sendMessage(String message){
//		try {
//			out.writeBytes(message+'\n');
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		}
//		return true;
		return sendBytes(message.getBytes());
	}
	public boolean sendBytes(byte[] message){
		
		try {
			out.writeBytes(""+message.length+'\n');
			this.out.write(message);
		} catch (IOException e) {
			System.err.println("Send failed");
			System.exit(0);
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
