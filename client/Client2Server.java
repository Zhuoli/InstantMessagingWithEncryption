package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Client2Server  implements Runnable{
	
	static private Client2Server instance = null;
	static private String hostname= "";
	static private int port=0;
	static private boolean close=false;
	TCPConnection connection =null;
	
	private User user=null;
	
	private Client2Server(String host, int port){
	}
	public static Client2Server getInstance(){
		if(instance==null){
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
		}
		return instance;
	}
	// authenticate	 the user
	public  boolean authTheUser(User user){
		this.user=user;
		if(connection==null){
			connection = TCPConnection.setUpConnection(hostname, port);
		}
		//start auth...
		connection.sendMessage("my public key, Random number");
		String rec = connection.readMessage();
		if(rec!="Random Number"){
			this.connectionTerminate();
			System.out.println("Auth Failed: Server Can't be authered");
			return false;
		}
		connection.sendMessage(user.getUsername()+" " + user.getHashedKey());
		rec=connection.readMessage();
		if(rec!="TRUE"){
			this.connectionTerminate();
			System.out.println("Auth Failed: User Can't be authered");
			return false;
		}
		// ...auth done
		this.connectionTerminate();
		return true;
	}
	public void connectionTerminate(){
		this.connection.close();
		this.connection=null;
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
