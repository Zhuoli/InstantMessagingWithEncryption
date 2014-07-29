package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;



public class Client2Server implements Runnable{
	
	static private Client2Server instance = null;
	static private String hostname= "";
	static private int port=0;
	static private int timeout=10000;
	static private int SocketInterval = 60000;
	protected Thread t = null;
	
	TCPConnection connection =null;
	Integer[] authState=null;
	private User user=null;
	
	private Client2Server(){
		String[] settingPaths={"./src/client/setting.conf","./client/setting.conf"};
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
			hostname = br.readLine().split(":")[1].trim();
			port=Integer.parseInt(br.readLine().split(":")[1].trim());
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){
				e.printStackTrace();
			}
			System.err.println("Client configure file:" + System.getProperty("user.dir") + "/setting.conf not exist.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){
				e.printStackTrace();
			}
			System.err.println("Client configure file 'setting.conf' format not correct:\nhostname: IP\nport: number");
		}
	}
	public static Client2Server getInstance(Integer[] authState,User user){
		if(instance==null){
			instance=new Client2Server();
			instance.t=new Thread(instance);
			instance.t.start();
		}
		instance.user=user;
		instance.authState=authState;
		return instance;
	}
	// authenticate	 the user
	public  int authTheUser(){
		if(user==null){
			System.out.println("User is null");
			return -1;
		}
		connection = TCPConnection.setUpConnection(hostname, port,timeout);
		//start auth...
		connection.sendMessage("authentication: Client listenn on port:"+Client.clientPort+':' + user.getUsername() +":"+user.password);
		String rec = connection.readMessage();
		if(!rec.toLowerCase().equals("authentication:true")){
			this.connectionTerminate();
			if(Client.DEBUG){
				System.out.println("Auth Failed: Server Can't be authered   "+rec);
			}
			return 0;
		}
		// ...auth done
		//	this.connectionTerminate();
		return 1;
	}
	public void connectionTerminate(){
		if(this.connection!=null){
			this.connection.terminate();
		}
		this.connection=null;
	}

	@Override
	public void run() {
		authState[0]=this.authTheUser();
		synchronized(this.authState){
			this.authState.notifyAll();
		}
		String message=null;
		while(!Thread.interrupted() && connection!=null && (message=connection.readMessage())!=null){
			if(message.startsWith("UserIP:") && message.length()>"UserIP:;".length()){
				UserIPDatabase.getInstance().insertUsers(message.substring(message.indexOf("UserIP:")+7));
			}
			System.out.println("Server: " + message);
			connection.terminate();
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				break;
			}
			this.authTheUser();
		}
		if(Client.DEBUG){
			System.out.println("Client to server Thread interrupted, gonna quit");
		}
		this.connectionTerminate();
	}
}