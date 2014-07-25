package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class TCPSender implements Runnable{
	 LinkedList<String> taskQueue=null;
     DataOutputStream out=null;
     protected TCPSender(LinkedList<String> queue, Socket clientSocket) { 
     	taskQueue = queue; 
			try {
				out = new DataOutputStream(clientSocket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
				System.err.println("sender Thread IO initial Error");
				System.exit(-1);
			}
     }
     public void run() {
    	 System.out.println("Sendering thread working...");
     	try{
     	while(!Thread.interrupted()){
     		synchronized(taskQueue){
     			while(taskQueue.isEmpty()){
     				taskQueue.wait();
     			}
     			String outstr = taskQueue.poll();
     			System.out.println("Gona send: " +outstr);
     			out.writeBytes(outstr);
     		}
     		}
     	}catch(InterruptedException e){
     		if(Client.DEBUG){
     			System.out.println("Sender Thread Interrupted");
     		}
     	} catch (IOException e) {
				// TODO Auto-generated catch block
     		if(Client.DEBUG){
     			e.printStackTrace();
     		}
     		System.err.println("Sender Thread IO Error");
		}
     }
}
