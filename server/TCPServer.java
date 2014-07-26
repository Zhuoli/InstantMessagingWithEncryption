package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPServer {
	static int PortNumber=0;
	static int clientPort=0;
	ServerSocket serverSocket=null;
	private  static TCPServer server=null;
	
	private TCPServer(){
		serverSocket=null;
		try {
			serverSocket = new ServerSocket(PortNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static TCPServer getServer(){
		String[] settingPaths={"./src/server/setting.conf","./server/setting.conf"};
		String settingPath="";
		for(String path : settingPaths){
			File file = new File(path);
			if(file.isFile()){
				settingPath=path;
				break;
			}
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(settingPath));
			PortNumber=Integer.parseInt(br.readLine().split(":")[1].trim());
			clientPort=Integer.parseInt(br.readLine().split(":")[1].trim());
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			if(Server.DEBUG){
				e.printStackTrace();
			}
			System.err.println("Client configure file:" + System.getProperty("user.dir") + "/setting.conf not exist.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Server.DEBUG){
				e.printStackTrace();
			}
			System.err.println("Client configure file 'setting.conf' format not correct:\nhostname: IP\nport: number");
		}
		if(server==null){
			server=new TCPServer();
		}
		return server;
	}
	
	public Socket accept(){
		Socket soc=null;
		try {
			soc=serverSocket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return soc;
	}
	
	
}
