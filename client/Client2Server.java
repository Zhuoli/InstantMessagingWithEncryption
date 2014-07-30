package client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;



public class Client2Server{
	
	static private Client2Server instance = null;
	static private String hostname= "";
	static private int port=0;
	static private int timeout=10000;
	
	
	protected byte[] serverKey=null;
	TCPConnection connection =null;
	private User user=null;
	private Encrypt encrypt=null;
	private Decrypt decrypt=null;
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
			serverKey=this.readByteFromFile(br.readLine().split(":")[1].trim());
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

		encrypt=new Encrypt(serverKey,Client.clientPrivateKey);
	}
	
	// read bytes from a file
	private  byte[] readByteFromFile(String fileName) {
		File f = new File(fileName);
		byte[] buffer=null;
		try {
			if (f.length() > Integer.MAX_VALUE)
				System.out.println("File is too large");
	
			buffer = new byte[(int) f.length()];
			InputStream ios;
				ios = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(ios);
			dis.readFully(buffer);
			dis.close();
			ios.close();
		} catch (Exception e) {
			System.err.println("read file error: "+fileName);
			System.exit(0);
		};
		
		return buffer;
		
	}
	public static Client2Server getInstance(User user){
		if(instance==null){
			instance=new Client2Server();
		}
		instance.user=user;
		return instance;
	}
	// authenticate	 the user
	/**
	 * C --> S: public key of client
	 * C --> S: encrypted hashed key and name
	 * S --> C: encrypted auth state
	 * @return
	 */
	
	public  boolean authTheUser(){
		if(user==null){
			System.out.println("User is null");
			return false;
		}
		connection = TCPConnection.setUpConnection(hostname, port,timeout);
		//start auth...
		byte[] message=null;
		byte[] barr=null;
		// send public key
		try {
			barr=combineBytes(("key:").getBytes("US-ASCII"),Client.clientPublicKey);
			connection.sendBytes(barr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// read random number
		byte[] bytes = connection.readBytes();
		bytes=(new Decrypt(serverKey,Client.clientPrivateKey,bytes)).decrypt();
		barr=encrypt.getEncryptedMessage(bytes);
		connection.sendBytes(barr);
		// send name and hashedpassword
		try {
			message = ("authentication: Client listenn on port:"+Client.clientPort+':' + user.getUsername() +":").getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		barr = combineBytes(message,user.getHashedKey());
		barr=encrypt.getEncryptedMessage(barr);
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
		//send client public key:
		return true;
	}
	protected byte[] combineBytes(byte[] a,byte[] b){
		byte[] barr=new byte[a.length+b.length];
		System.arraycopy(a, 0, barr, 0, a.length);
		System.arraycopy(b, 0, barr, a.length, b.length);
		return barr;
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