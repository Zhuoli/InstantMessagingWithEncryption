package server;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
	static final int SERVER_MAX_NUM=10;
	static final boolean DEBUG = true;
	static int count=0;
	static ExecutorService executor=null;
	// terminate App. properly in case of user interrupting
	static class ExitHandler extends Thread{
		private ExitHandler(){
			super("Exit Handler");
		}
		public void run(){
			executor.shutdownNow();
			System.out.println("Server Control-C interrupted");
		}
		public static ExitHandler getInstance(){
			return new ExitHandler();
		}
	}
	public static void main(String[] argvs){

		System.out.println("Welcome to the Encypted Instant Messaging App.\nServer Running...");
	    executor = Executors.newFixedThreadPool(SERVER_MAX_NUM);
		TCPServer tcpServer= TCPServer.getServer();

		// register the terminate Thread
		Runtime.getRuntime().addShutdownHook(Server.ExitHandler.getInstance());
		while(true){
			Socket tcpSocket=tcpServer.accept();
			if(tcpSocket==null){
				continue;
			}
			System.out.println("New user has joined");
			executor.submit(new Task(tcpSocket, count++,10000));
		 }
	}
}
