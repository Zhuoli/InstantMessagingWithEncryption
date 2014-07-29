package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client2Client implements Runnable{
	static private Client2Client instance =null;
	User user = null;
	int timeout=1000;
	protected Thread t = null;
	TCPConnection connection =null;
	ServerSocket serverSocket=null;
	
	
	private Client2Client(User user){
		this.user=user;
	}
	public static Client2Client getInstance(User user){
		if(instance==null){
			instance=new Client2Client(user);
			instance.t = new Thread(instance);
			instance.t.start();
		}
		return instance;
	}
	
	public Thread setUpListen(){
		Thread t = new Thread(this);
		t.start();
		return t;
	}
	public boolean send2client(String targetName, String content){
		if(!UserIPDatabase.getInstance().hasThisUser(targetName)){
			UserIPDatabase.getInstance().update();
		}
		if(!UserIPDatabase.getInstance().hasThisUser(targetName)){
			System.out.println("Sending ERROR: The use: '" + targetName + "' is not online currentlly");
			return false;
		}
		String ip =UserIPDatabase.getInstance().getIP(targetName);
		int port = UserIPDatabase.getInstance().getPort(targetName);
		TCPConnection connection = TCPConnection.setUpConnection(ip,port, timeout);
		connection.sendMessage(content+'/'+user.getUsername());
		System.out.println("Send to user: " + targetName +": "+content+"/"+user.getUsername());
		connection.terminate();
		return true;
	}
	public void connectionTerminate(){
		if(this.connection!=null){
			this.connection.terminate();
		}
		this.connection=null;
	}
	@Override
	public void run() {
		if(Client.DEBUG){
			System.out.println("Client Listening thread running...");
		}
		Client.clientPort=22048;
		while(true){
			try {
			 serverSocket=new ServerSocket(Client.clientPort);
			 break;
			 } catch (IOException e) {
				 Client.clientPort++;
				 continue;
			}		// register the terminate Thread
		  }
		Socket tcpSocket;

		try {
			while(!Thread.interrupted()){
					tcpSocket = serverSocket.accept();
					if(tcpSocket==null){
						continue;
					}
					try{
						tcpSocket.setSoTimeout(timeout);
						BufferedReader br = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
						System.out.println("Income: " + br.readLine());
						br.close();
						tcpSocket.close();
					}
					catch(SocketTimeoutException e){
						System.out.println("Income Client Socket time out");
						continue;
					}
				}
			
		 } catch (IOException e) {
		}
		if(Client.DEBUG){
			System.out.println("Client2Client Thread interrupted, gonna quit");
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){	
			e.printStackTrace();
			}
		}
		
	}
	public void terminate(){
		try {
			if(serverSocket!=null){
				serverSocket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
