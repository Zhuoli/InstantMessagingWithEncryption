package client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class TCPConnection {
	static String host="";
	static int port=0;
	static int timeout=0;
	private Socket clientSocket=null;
	private TCPSender sender=null;
	private TCPReceiver receiver=null;
	private Thread senderThread=null;
	private Thread receiverThread = null;
	
	private LinkedList<String> receive_messages =null;
	private LinkedList<String> sender_messages=null;
	
	private TCPConnection(String host, int port,int timeout){
		TCPConnection.host=host;
		TCPConnection.port=port;
		TCPConnection.timeout=timeout;
	}
	public static TCPConnection setUpConnection(String host, int port,int timeout){
			TCPConnection instance=new TCPConnection(host,port,timeout);

			try {
				instance.clientSocket=new Socket(host,port);
				instance.clientSocket.setSoTimeout(timeout);
			}catch(SocketException e){
				System.err.println("Set Socket Timeout failed.");
				System.exit(-1);
			}
			catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
				System.err.println("Unknown Host:" + host +":"+port);
				System.exit(-1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
				System.err.println("TCP IO Error");
				System.exit(-1);
			}
			instance.receive_messages = new LinkedList<String>();
			instance.sender_messages  = new LinkedList<String>();
			instance.initThreads();
			return instance;
	}
	
	private void initThreads(){
		sender=new TCPSender(sender_messages,clientSocket);
		 senderThread = new Thread(sender);
		senderThread.start();
		
		receiver= new TCPReceiver(receive_messages,clientSocket,this);
		 receiverThread = new Thread(receiver);
		receiverThread.start();
	}
	public boolean sendMessage(String message){
		synchronized(sender_messages){
			sender_messages.add(message);
			this.sender_messages.notifyAll();
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
	public boolean terminate(){
		senderThread.interrupt();
		receiverThread.interrupt();
		try {
			senderThread.join(1000);
			receiverThread.join(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){
				e1.printStackTrace();
			}
			System.err.println("Sender and Receiver thread didn't cancelled in time");
		}
		
		try {
			this.clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clientSocket=null;
		senderThread=null;
		receiverThread=null;
		this.receive_messages=null;
		this.sender_messages=null;
		if(Client.DEBUG){
			System.out.println("TCP connection terminated");
		}
		return true;
	}

}
