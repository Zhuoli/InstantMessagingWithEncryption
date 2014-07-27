package client;


public class Client {
	static boolean DEBUG=false;
	static Client2Server c2serverThread=null;
	static Client2Client c2clientThread=null;
	protected static int clientPort=0;
	
	// terminate App. properly in case of user interrupting
	static class ExitHandler extends Thread{
		private ExitHandler(){
			super("Exit Handler");
		}
		public void run(){
			Client.terminate();
		}
		public static ExitHandler getInstance(){
			return new ExitHandler();
		}
	}
	
	public static void main(String[] argvs){
		System.out.println("Welcome to the Encypted Instant Messaging App.\nClient Running...");
		Integer[] authState = {-1};
		User user = User.login();
		// register the terminate Thread
		//Runtime.getRuntime().addShutdownHook(Client.ExitHandler.getInstance());
		try{
			c2serverThread =Client2Server.getInstance(authState,user);
			c2clientThread =Client2Client.getInstance(user);
			// wait for authentication
			synchronized(authState){
				authState.wait(2000);
			}
			// if auth. failed
			if(authState[0]!=1){
				System.out.println("User name or password not correct, please try again");
				terminate();
			}else{
				System.out.println("Auth succeed");
				// set up a listen socket port to connection from other clients
				// interact console
				while(true){
					String userInput = user.getUserInput();
					Client.processUserInput(userInput);
				}
			}
		}catch(Exception e){
			System.err.println("Exception occured, client gonna quit");
			terminate();
		}

	}
	
	// parse and process user input
	// if 'list', then list all the online users
	// if 'send', then send content to the target user
	public static void processUserInput(String userInput){
		userInput=userInput.toLowerCase();
		if(userInput.equals("list") || userInput.equals("ls")){
			System.out.println("users: " + UserIPDatabase.getInstance().onlineUsers());
		}else if(userInput.toLowerCase().startsWith("send")){
			String[] strs = userInput.split(" ");
			if(strs.length<3){
				System.out.println("\nSend Input Error, Usage:\nsend USER MESSAGE\n\n");
				return;
			}
			String content = userInput.substring(strs[0].length()+strs[1].length()+2);
			c2clientThread.send2client(strs[1],content);
		}else{
			System.out.println("\nUser Input Error, Usage:\nlist\nsend USER MESSAGE\n\n");
		}
	}

	private static synchronized void terminate(){
		if(Client.DEBUG){
			System.out.println("Hi, terminate works");
		}
		if(c2serverThread!=null){
			c2serverThread.t.interrupt();
			try {
				c2serverThread.wait(2000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
			}
		}
		if(c2clientThread!=null){
			c2clientThread.terminate();
		}
		if(Client.DEBUG){
			System.out.println("Client now terminated");
		}
	}
}