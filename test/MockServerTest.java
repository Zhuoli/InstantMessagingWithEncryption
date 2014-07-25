package test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MockServerTest {
	public static void main(String[] argv){
		ServerSocket serverSocket=null;
		try {
			 serverSocket = new ServerSocket(2048);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			try {
				Socket socket = serverSocket.accept();
				System.out.println("New Client come in");
				BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				String message=null;
				System.out.println("reading...");
				while((message=in.readLine())!=null){
					message = in.readLine();
					System.out.println("Client: " + message);
					out.writeBytes("Hello, Client, this is a message from Server... " + message +'\n');
				}
				System.out.println("Read null, connection dropped");
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
