package server;

public class Server {
	
	public static void main(String[] argvs){
		TCPServer serverInstance = TCPServer.getInstance();
		while(true){
			String message= serverInstance.readMessage();
			System.out.println(message);
			serverInstance.sendMessage(message.toUpperCase() +'\n');
		}
	}
}
