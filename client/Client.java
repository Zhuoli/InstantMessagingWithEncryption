package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
	static boolean DEBUG=true;
	
	public static void main(String[] argvs)throws Exception{
		User user = User.login();
		Client2Server.getInstance().authTheUser(user);

	}
}