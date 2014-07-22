package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class TCPConnection {
	private Socket clientSocket=null;
	private Thread senderThread=null;
	private Thread receiverThread=null;
	
	private LinkedList<String> receive_messages =null;
	private LinkedList<String> sender_messages=null;
	
	private TCPConnection(String host, int port,int timeout){
		try {
		    clientSocket=new Socket(host,port);
		    clientSocket.setSoTimeout(timeout);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){
				e.printStackTrace();
			}
			System.err.println("Unknown Host:" + host +":"+port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){
				e.printStackTrace();
			}
			System.err.println("TCP IO Error");
		}
		receive_messages = new LinkedList<String>();
		sender_messages  = new LinkedList<String>();
		this.initThreads();
	}
	public static TCPConnection setUpConnection(String host, int port,int timeout){
			return new TCPConnection(host,port,timeout);
	}
	
	private void initThreads(){
		class SenderTask implements Runnable {
	        LinkedList<String> taskQueue=null;
	        DataOutputStream out=null;
	        SenderTask(LinkedList<String> queue, Socket clientSocket) { 
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
	        	while(!taskQueue.isEmpty()){
	        		try {
						out.writeBytes(taskQueue.poll());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						if(Client.DEBUG){
							e.printStackTrace();
						}
						System.err.println("sender Thread IO Error");
					}
	        	}
	        }
	    }
		class ReceiverThread implements Runnable{
			LinkedList<String> recvTask=null;
		    BufferedReader in=null;
		    ReceiverThread(LinkedList<String> queue, Socket clientSocket){
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
				// TODO Auto-generated method stub
				
			}
			
		}
		senderThread = new Thread(new SenderTask(sender_messages,clientSocket));
		senderThread.start();
	}
	public boolean sendMessage(String message){

		return true;
	}
	public String readMessage(){
		String ret="";
		try {
			ret = this.in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){
				e.printStackTrace();
			}
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
