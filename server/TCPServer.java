package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	static int PortNumber=2048;
	ServerSocket serverSocket=null;
	private  static TCPServer server=null;
	
	private TCPServer(){
		serverSocket=null;
		try {
			serverSocket = new ServerSocket(PortNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static TCPServer getServer(){
		if(server==null){
			server=new TCPServer();
		}
		return server;
	}
	
	public Socket accept(){
		Socket soc=null;
		try {
			soc=serverSocket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return soc;
	}
	
	
}
