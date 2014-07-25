package client;

import java.util.HashMap;


public class Client {
	static boolean DEBUG=true;
	static Thread c2serverThread=null;
	static Thread c2clientThread=null;
	static int clientPort=0;
	static volatile HashMap<String,String> users = new HashMap<String,String>();
	
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
		Runtime.getRuntime().addShutdownHook(Client.ExitHandler.getInstance());
		try{
			c2serverThread = new Thread(Client2Server.getInstance(authState,user));
			c2serverThread.start();
			synchronized(authState){
				authState.wait(3000);
			}
			if(authState[0]==1){
				// set up a listen socket port to connection from other clients
				c2clientThread = Client2Client.getInstance().setUpListen();
				// interact console
				while(true){
					//String userInput = user.getUserInput();
					//Client.processUserInput(userInput,Client2Client.getInstance(),Client2Server.getInstance());
				}
			}else{
				System.out.println("User name or password not correct, please try again");
				c2serverThread.interrupt();
			}
		}catch(Exception e){
			System.err.println("Exception occured, client gonna quit");
			if(c2serverThread!=null){
				c2serverThread.interrupt();
			}
		}

	}
	
	// parse and process user input
	// if 'list', then list all the online users
	// if 'send', then send content to the target user
	public static void processUserInput(String userInput,Client2Client client2client,Client2Server client2server){
		if(userInput.toLowerCase().equals("list")){
			System.out.println(Client.listOnlineUsers());
		}else if(userInput.toLowerCase().startsWith("send")){
			String[] strs = userInput.split(" ");
			if(strs.length<3){
				System.out.println("\nSend Input Error, Usage:\nsend USER MESSAGE\n\n");
			}
			String content = userInput.substring(strs[0].length()+strs[1].length()+2);
			client2client.send2client(strs[1],content);
		}else{
			System.out.println("\nUser Input Error, Usage:\nlist\nsend USER MESSAGE\n\n");
		}
	}
	public static String listOnlineUsers(){
		String ret="";
		synchronized(users){
			for(String user : users.keySet()){
				ret=ret+user+'\n';
			}
		}
		return ret;
	}
	private static synchronized void terminate(){
		if(Client.DEBUG){
			System.out.println("Hi, terminate works");
		}
		if(c2serverThread!=null){
			c2serverThread.interrupt();
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
			c2clientThread.interrupt();
			try{
				c2clientThread.wait(2000);
			}catch(Exception e){
				if(Client.DEBUG){
					e.printStackTrace();
				}
			}
		}
		if(Client.DEBUG){
			System.out.println("Client now terminated");
		}
	}
}