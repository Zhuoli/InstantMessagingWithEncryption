package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;



public class Client2Server  {
	
	static private Client2Server instance = null;
	static private String hostname= "";
	static private int port=0;
	static private int timeout=2000;
	static private boolean close=false;
	TCPConnection connection =null;
	
	private User user=null;
	
	private Client2Server(){
	}
	public static Client2Server getInstance(){
		if(instance==null){
			instance=new Client2Server();
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
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
				System.err.println("Client configure file:" + System.getProperty("user.dir") + "/setting.conf not exist.");
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				if(Client.DEBUG){
					e.printStackTrace();
				}
				System.err.println("Client configure file 'setting.conf' format not correct:\nhostname: IP\nport: number");
				return null;
			}
		}
		return instance;
	}
	// authenticate	 the user
	public  boolean authTheUser(User user){
		if(user==null){
			System.out.println("User is null");
		}
		this.user=user;
		if(connection==null){
			connection = TCPConnection.setUpConnection(hostname, port,timeout);
		}
		//start auth...
		connection.sendMessage("my public key, Random number");
		String rec = connection.readMessage();
		if(rec!="Random Number"){
			this.connectionTerminate();
			System.out.println("Auth Failed: Server Can't be authered   "+rec);
			return false;
		}
		connection.sendMessage(user.getUsername()+" " + user.getHashedKey());
		rec=connection.readMessage();
		if(rec!="TRUE"){
			this.connectionTerminate();
			System.out.println("Auth Failed: User Can't be authered   " +rec);
			return false;
		}
		// ...auth done
		this.connectionTerminate();
		return true;
	}
	public void connectionTerminate(){
		if(this.connection!=null){
			this.connection.close();
		}
		this.connection=null;
	}
}