package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	static final int SERVER_MAX_NUM=10;
	static int count=0;
	public static void main(String[] argvs){

		System.out.println("Welcome to the Encypted Instant Messaging App.\nServer Running...");
		ExecutorService executor = Executors.newFixedThreadPool(SERVER_MAX_NUM);
		TCPServer tcpServer= TCPServer.getServer();
		while(true){
			Socket tcpSocket=tcpServer.accept();
			if(tcpSocket==null){
				continue;
			}
			System.out.println("New user has joined");
			executor.submit(new Task(tcpSocket, count++));
		 }
	}
}
