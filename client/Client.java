package client;


public class Client {
	static boolean DEBUG=false;
	static Client2Server c2server=null;
	static Client2Client c2clientThread=null;
	static byte[] clientPublicKey=null;
	static byte[] clientPrivateKey=null;
	static User user = null;
	
	// terminate App. properly in case of user interrupting control^c
	public static class ExitHandler extends Thread {
		protected ExitHandler(){
			super("Exit Handler");
		}
		public void run(){
			Client.terminate();
		}
		private static ExitHandler getInstance(){
			return new ExitHandler();
		}
	}
	
	public static void main(String[] argvs){
		Object clientPortSynchronism = new Object();
		user = User.login();
		generateKeyPair();
		// register the terminate Thread
		Runtime.getRuntime().addShutdownHook(Client.ExitHandler.getInstance());
		try{
			// set up a listen socket port to connection from other clients
			c2clientThread =Client2Client.getInstance(user,clientPortSynchronism);
			synchronized(clientPortSynchronism){
				clientPortSynchronism.wait();
			}
			c2server =Client2Server.getInstance(user);
			// if auth. failed
			if(!c2server.requestUpdateUsersInfo()){
				System.out.println("User name or password not correct, please try again");
				terminate();
			}else{
				System.out.println("Auth succeed");
				// interact console
				userInteractive(user);
			}
		}catch(Exception e){
			//System.err.println("Exception occured, client gonna quit");
			terminate();
		}
		

	}
	/**
	 * Interact with user's input
	 * @param user
	 */
	private static void userInteractive(User user)throws Exception{
		while(true){
			String userInput = user.getUserInput();
			Client.processUserInput(userInput);
		}
	}
	
	/**
	 * generate key pair
	 */
	private static void generateKeyPair(){
		clientPublicKey=FileOperation.readByteFromFile("./client/public_key.der");
		clientPrivateKey=FileOperation.readByteFromFile("./client/private_key.der");
		
	}
	

	// parse and process user input
	// if 'list', then list all the online users
	// if 'send', then send content to the target user
	public static void processUserInput(String userInput)throws Exception{
		userInput=userInput.toLowerCase();
		if(userInput.equals("list") || userInput.equals("ls")){
			System.out.println("users: " + Database.getInstance().onlineUsers());
		}else if(userInput.toLowerCase().startsWith("send")){
			String[] strs = userInput.split(" ");
			if(strs.length<3){
				System.out.println("\nSend Input Error, Usage:\nsend USER MESSAGE\n\n");
				return;
			}
			String content = userInput.substring(strs[0].length()+strs[1].length()+2);
			c2clientThread.send2client(strs[1],content);
		}else if(userInput.toLowerCase().startsWith("key")){
			String[] strs=userInput.split(" ");
			if(strs.length==2){
				byte[] brr=null;
				if(strs[1].equals(user.getUsername())){
					brr =Client.clientPublicKey;
				}else{
					brr=Database.getInstance().getKey(strs[1]);
				}
				for(int i : brr){
					System.out.print(" "+i+" ");
				}
				System.out.println();
				
			}
		}else{
			System.out.println("\nUser Input Error, Usage:\nlist\nsend USER MESSAGE\n\n");
		}
	}

	// terminate appropriately
	private static synchronized void terminate(){
		if(Client.DEBUG){
			System.out.println("Hi, terminate works");
		}
		if(c2clientThread!=null){
			c2clientThread.terminate();
		}
		if(Client.DEBUG){
			System.out.println("Client now terminated");
		}
	}
}