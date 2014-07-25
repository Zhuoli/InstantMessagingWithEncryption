package test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketSonnectionTest implements Runnable{

	static String host="129.10.117.100";
	static int port = 2048;
	static Socket socket=null;
	static BufferedReader in = null;
	static DataOutputStream out = null;
	
	public static void main(String[] argvs){

		BufferedReader userin = new BufferedReader(new InputStreamReader(System.in));
		try {
		  socket= new Socket(host,port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
	  	   in =new BufferedReader(new InputStreamReader(socket.getInputStream()));
	  	   out = new DataOutputStream(socket.getOutputStream());
		}catch(Exception e){

			e.printStackTrace();
		}
		Thread t = new Thread(new SocketSonnectionTest());
		t.start();
		while(true){
			System.out.println("Please input message:");
			String message=null;
			try {
				 message = userin.readLine();
				out.writeBytes(message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void run() {
		String message;
		System.out.println("Listen start");
		while(true){
			try {
				message = in.readLine();
				System.out.println("Server: "+message);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
