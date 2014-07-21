package client;



public class Client2Server  extends TCPConnection implements Runnable{
	
	static Client2Server connection = null;
	static final String hostname= "129.10.117.100";
	static final int port=2048;
	
	private Client2Server(String host, int port){
		super(host,port);
	}
	public static Client2Server getInstance(){
		
		if(connection==null){
			connection = new Client2Server(hostname,port);
		}
		return connection;
	}
	// authenticate	 the user
	public  boolean authTheUser(User user){
		boolean close=false;
		// new thread to receive message from server
		new Thread(this).start();
		// while loop to send message to server
		while(!close){
			String input=user.getUserInput()+'\n';
			connection.sendMessage(input);
		}
		connection.close();
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
	    }
	}
}
