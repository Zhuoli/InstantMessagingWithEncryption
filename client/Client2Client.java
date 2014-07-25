package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client2Client implements Runnable{
	static private Client2Client instance =null;
	int timeout=1000;
	TCPConnection connection =null;
	
	
	private Client2Client(){
		
	}
	public static Client2Client getInstance(){
		if(instance==null){
			instance=new Client2Client();
		}
		return instance;
	}
	
	public Thread setUpListen(){
		Thread t = new Thread(this);
		t.start();
		return t;
	}
	public boolean send2client(String targetName, String content){
		
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
		System.out.println("Client Listening thread running...");
		ServerSocket serverSocket=null;
		try {
		 serverSocket=new ServerSocket(Client.clientPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}		// register the terminate Thread
		Socket tcpSocket;
		while(true){
			try {
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		 }
		
	}
}
