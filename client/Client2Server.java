package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Client2Server  extends TCPConnection implements Runnable{
	
	static Client2Server connection = null;
	static String hostname= "";
	static int port=0;
	static boolean close=false;
	
	private Client2Server(String host, int port){
		super(host,port);
	}
	public static Client2Server getInstance(){
		if(connection==null){
			try {
				BufferedReader br = new BufferedReader(new FileReader("./src/client/setting.conf"));
				hostname = br.readLine().split(":")[1].trim();
				port=Integer.parseInt(br.readLine().split(":")[1].trim());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
				System.err.println("Client configure file:" + System.getProperty("user.dir") + "/setting.conf not exist.");
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
				System.err.println("Client configure file 'setting.conf' format not correct:\nhostname: IP\nport: number");
				return null;
			}
			connection = new Client2Server(hostname,port);
		}
		return connection;
	}
	// authenticate	 the user
	public  boolean authTheUser(User user){
		if(connection==null)
			return false;
		// new thread to receive message from server
		//new Thread(this).start();
		ExecutorService executor = Executors.newFixedThreadPool(2);
		// send thread
		executor.submit(new Runnable(User user){

			@Override
			public void run() {
				// while loop to send message to server
				while(!close){
					String input=user.getUserInput()+'\n';
					connection.sendMessage(input);
					
				}
			}
			
		});

		System.out.println("connection closing...");
		connection.close();
		System.out.println("Done");
		return true;
	}

	@Override
	public void run() {
	    /*
	     * Keep on reading from the socket till we receive "Bye" from the
	     * server. Once we received that then we want to break.
	     */
	    String responseLine;
	    while ((responseLine = connection.readMessage()) != null) {
	      System.out.println(responseLine);
	      if (responseLine.startsWith("/quit")) {
	    	  System.out.println(responseLine);
	          break;
	       }
	    }
	    System.out.println("Client Gonna Quit");
	    this.close=true;
	    System.out.println("Thread Close state: " + this.close);
	}
}
