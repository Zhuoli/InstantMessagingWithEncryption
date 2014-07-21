package client;

import java.io.IOException;
import java.net.Socket;

public class Client {
	static boolean DEBUG=true;
	public static void main(String[] argvs){
		TCPConnection MyClient = TCPConnection.getInstance();
		User user = User.login();
	}
}