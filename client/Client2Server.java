package client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;



public class Client2Server{
	
	static private Client2Server instance = null;
	static private String hostname= "";
	static private int port=0;
	static private int timeout=10000;
	
	TCPConnection connection =null;
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
	public static Client2Server getInstance(User user){
		if(instance==null){
			instance=new Client2Server();
		}
		instance.user=user;
		return instance;
	}
	// authenticate	 the user
	public  boolean authTheUser(){
		if(user==null){
			System.out.println("User is null");
			return false;
		}
		connection = TCPConnection.setUpConnection(hostname, port,timeout);
		//start auth...
		byte[] message=null;
		try {
			message = ("authentication: Client listenn on port:"+Client.clientPort+':' + user.getUsername() +":").getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] barr=new byte[message.length+user.getHashedKey().length];
		System.arraycopy(message, 0, barr, 0, message.length);
		System.arraycopy(user.getHashedKey(), 0, barr, message.length, user.getHashedKey().length);
		connection.sendBytes(barr);
		String rec = connection.readMessage();
		if(!rec.toLowerCase().equals("authentication:true")){
			this.connectionTerminate();
			if(Client.DEBUG){
				System.out.println("Auth Failed: Server Can't be authered   "+rec);
			}
			return false;
		}
		// ...auth done
		//	this.connectionTerminate();
		return true;
	}
	public void connectionTerminate(){
		if(this.connection!=null){
			this.connection.terminate();
		}
		this.connection=null;
	}


	protected boolean requestUpdateUsersInfo(){
		this.authTheUser();
		String message=connection.readMessage();
		if(message!=null && message.startsWith("UserIP:") && message.length()>"UserIP:;".length()){
			UserIPDatabase.getInstance().insertUsers(message.substring(message.indexOf("UserIP:")+7));
		}else{
			return false;
		}
		System.out.println("Server: " + message);
		connection.terminate();
		return true;
	}
}