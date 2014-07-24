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
	private TCPSender sender=null;
	private TCPReceiver receiver=null;
	private Thread senderThread=null;
	private Thread receiverThread = null;
	
	private LinkedList<String> receive_messages =null;
	private LinkedList<String> sender_messages=null;
	
	private TCPConnection(String host, int port,int timeout){
		try {
		    clientSocket=new Socket(host,port);
		   // clientSocket.setSoTimeout(timeout);
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
		sender=new TCPSender(sender_messages,clientSocket);
		 senderThread = new Thread(sender);
		senderThread.start();
		
		receiver= new TCPReceiver(receive_messages,clientSocket);
		 receiverThread = new Thread(receiver);
		receiverThread.start();
	}
	public boolean sendMessage(String message){
		synchronized(sender_messages){
			sender_messages.add(message);
			this.sender_messages.notify();
		}
		return true;
	}
	public String readMessage(){
		String ret="";
		synchronized(sender_messages){
			ret=sender_messages.poll();
		}
		return ret;
	}
	public boolean close(){
		senderThread.interrupt();
		receiverThread.interrupt();
		try {
			senderThread.join(3000);
			receiverThread.join(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			this.clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("TCP connection terminated");
		return true;
	}

}
