package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;

public class TCPReceiver implements Runnable{
	LinkedList<String> recvTask=null;
    BufferedReader in=null;
    TCPConnection connection=null;
    TCPReceiver(LinkedList<String> queue, Socket clientSocket, TCPConnection connection){
    	recvTask=queue;
		try {
			in = new BufferedReader(new InputStreamReader(
							clientSocket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){
				e.printStackTrace();
			}
			System.err.println("receive Thread IO initiate Error");
			System.exit(-1);
		}
    }
	@Override
	public void run() {
		String str=null;
     	try{
	     	while(!Thread.interrupted()){
	     		synchronized(this){
	     			while((str=in.readLine())==null){
	     			}
	     			recvTask.add(str);
	     		}
     		}
     	}catch(SocketTimeoutException e){
     		if(Client.DEBUG){
     			System.err.println("Socket Timeout");
     		}
     		connection.terminate();
     	}
     	catch (IOException e) {
				// TODO Auto-generated catch block
     		if(Client.DEBUG){
     			e.printStackTrace();
     		}
     		System.err.println("Sender Thread IO Error");
			}
     	if(Client.DEBUG){
     		System.out.println("Receiver Thread Interrupted");
     	}
	}
}
