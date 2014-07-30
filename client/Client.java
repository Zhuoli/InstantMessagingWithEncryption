package client;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;


public class Client {
	static boolean DEBUG=false;
	static Client2Server c2server=null;
	static Client2Client c2clientThread=null;
	static  Integer clientPort=0;
	static byte[] clientPublicKey=null;
	static byte[] clientPrivateKey=null;
	
	static User user = null;
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
		user = User.login();
		generateKeyPair();
		// register the terminate Thread
		//Runtime.getRuntime().addShutdownHook(Client.ExitHandler.getInstance());
		try{
			c2clientThread =Client2Client.getInstance(user);
			Thread.sleep(1000);
			c2server =Client2Server.getInstance(user);
			boolean state = c2server.authTheUser();
			// if auth. failed
			if(!state){
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
	private static void generateKeyPair(){
		 try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	        keyGen.initialize(2048);
	        clientPublicKey = keyGen.genKeyPair().getPublic().getEncoded();
	        clientPrivateKey=keyGen.generateKeyPair().getPrivate().getEncoded();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		}else if(userInput.toLowerCase().startsWith("key")){
			String[] strs=userInput.split(" ");
			if(strs.length==2){
				byte[] brr=null;
				if(strs[1].equals(user.getUsername())){
					brr =Client.clientPublicKey;
				}else{
					brr=UserIPDatabase.getInstance().getKey(strs[1]);
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