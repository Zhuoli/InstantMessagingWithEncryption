package client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;



public class Client2Server{
	
	static private Client2Server instance = null;
	static private String hostname= "";
	static private int port=0;
	static private int timeout=10000;
	static protected byte[] serverKey=null;
	
	
	TCPConnection connection =null;
	private User user=null;
	private Encrypt encrypt=null;
	private int[] nounce = {0};
	
	private Client2Server(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(getSuitableFile()));
			hostname = br.readLine().split(":")[1].trim();
			port=Integer.parseInt(br.readLine().split(":")[1].trim());
			serverKey=FileOperation.readByteFromFile(br.readLine().split(":")[1].trim());
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
	
	private String getSuitableFile(){
		String[] settingPaths={"./src/client/setting.conf","./client/setting.conf"};
		String settingPath="";
		for(String path : settingPaths){
			File file = new File(path);
			if(file.isFile()){
				settingPath=path;
				break;
			}
		}
		if(settingPath.equals("")){
			System.err.println("Didn't find the config file in: \n"+settingPaths[0]+'\n'+settingPaths[1]);
		}
		return settingPath;
	}
	

	public static Client2Server getInstance(User user){
		if(instance==null){
			instance=new Client2Server();
		}
		instance.user=user;
		return instance;
	}

	
	/**
	 * authenticate	 the user
	 * C --> S: public key of client
	 * C --> S: encrypted hashed key and name
	 * S --> C: encrypted auth state
	 * @return
	 */
	
	public  boolean authTheUser() throws Exception{
		if(user==null){
			System.out.println("User is null");
			return false;
		}
		try{
			connection = TCPConnection.setUpConnection(hostname, port,timeout);
		}catch(Exception e){
			System.err.println("Failed to open server's Socket!");
			throw e;
		}
		//start auth...
		byte[] message=null;
		byte[] barr=null;
		byte[] bytes=null;
		// send public key
		try {
			barr=StringBytesSwitch.combineBytes(("key:").getBytes("US-ASCII"),Client.clientPublicKey);
			connection.sendBytes(barr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// read random number
		bytes=connection.readBytes();
		bytes=(new Decrypt(serverKey,Client.clientPrivateKey,bytes)).decrypt();
		nounce[0] = (new BigInteger(bytes)).intValue();
		barr=encrypt.getEncryptedMessage(StringBytesSwitch.int2byte(++nounce[0]));
		connection.sendBytes(barr);
		// send name and hashedpassword
		try {
			message = ("authentication: Client listenn on port:"+Client2Client.clientPort+':' + user.getUsername() +":").getBytes("US-ASCII");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		barr = StringBytesSwitch.combineBytes(message,user.getHashedKey());
		SecureLayer.sendEncryptWithNounce(encrypt,barr,nounce,connection);
		bytes = connection.readBytes();
		String rec = new String(SecureLayer.decryptWithNounce(serverKey,bytes,nounce));
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
	
	

	
	




	
	public void connectionTerminate(){
		if(this.connection!=null){
			this.connection.terminate();
		}
		this.connection=null;
	}


	protected boolean requestUpdateUsersInfo() throws Exception{
		if(!this.authTheUser()){
			return false;
		}
		byte[] bytes=connection.readBytes();
		bytes=SecureLayer.decryptWithNounce(serverKey,bytes,nounce);
		String message = new String(bytes);
	//	System.out.println(message);
		if(message!=null && message.startsWith("UserIP:") && message.length()>"UserIP:;".length()){
			Database.getInstance().insertUsers(message.substring(message.indexOf("UserIP:")+7));
		}else{
			return false;
		}
		while(connection!=null && (bytes=connection.readBytes())!=null){
			bytes=SecureLayer.decryptWithNounce(serverKey,bytes,nounce);
			String head = (new String(bytes)).toLowerCase();
			//System.out.println(head);
			if(head.startsWith("ticket:")){
				String name = head.split(":")[1];
				byte[] ticket=connection.readBytes();
				ticket=SecureLayer.decryptWithNounce(serverKey,ticket,nounce);
				Database.getInstance().putTICKET(name, ticket);
			}else{
				//System.out.println("Got: "+head);
				break;
			}
		}
		if(connection==null){
			System.out.println("connection null");
		}else if(bytes==null){
			System.out.println("bytes null");
		}
		//System.out.println("Server: " + message);
		connection.terminate();
		return true;
	}
}