package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
	static boolean DEBUG=true;
	
	public static void main(String[] argvs)throws Exception{
		System.out.println("Welcome to the Encypted Instant Messaging App.\nClient Running...");
		User user = User.login();
		if(Client2Server.getInstance().authTheUser(user)){
			// set up a listen socket port to connection from other clients
			Client2Client.getInstance().setUpListen();
			// interact console
			while(true){
				String userInput = user.getUserInput();
				Client.processUserInput(userInput,Client2Client.getInstance(),Client2Server.getInstance());
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
		return "";
	}
}