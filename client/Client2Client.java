package client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;

public class Client2Client implements Runnable{
	static  Integer clientPort=0;
	static private Client2Client instance =null;
	private Object clientPortSynchronism = null;
	User user = null;
	int timeout=1000;
	protected Thread t = null;
	protected ServerSocket serverSocket=null;
	
	
	private Client2Client(User user,Object clientPortSynchronism){
		this.user=user;
		this.clientPortSynchronism=clientPortSynchronism;
	}
	public static Client2Client getInstance(User user,Object clientPortSynchronism){
		if(instance==null){
			instance=new Client2Client(user,clientPortSynchronism);
			instance.t = new Thread(instance);
			instance.t.start();
		}
		return instance;
	}
	/**
	 * Send content to the target user
	 * @param targetName
	 * @param content
	 * @return true if send succeed, else false
	 */
	public boolean send2client(String targetName, String content)throws Exception{
		int[] nounce ={0};
		nounce[0] = (new SecureRandom()).nextInt();
		if(!Database.getInstance().hasThisUser(targetName)){
			Database.getInstance().update();
		}
		if(!Database.getInstance().hasThisUser(targetName)){
			System.out.println("Sending ERROR: The use: '" + targetName + "' is not online currentlly");
			return false;
		}
		// get target user object which contain the target IP, port, ticket, public key
		DestinationUser destUser = Database.getInstance().getDestUser(targetName);
		TCPConnection connection = null;
		try {
			connection=TCPConnection.setUpConnection(destUser.getIP(),destUser.getPort(), timeout);
		} catch (Exception e){
			System.out.println("Client " + targetName +" has off line.");
			return false;
		}
		authEncryptAndSendContent(content,connection,destUser,nounce);
		System.out.println(user.getUsername() + " to " +targetName + ": "+content);
		connection.terminate();
		return true;
	}
	private boolean authEncryptAndSendContent(String content,TCPConnection connection,DestinationUser destUser,int[] nounce){
		Encrypt encrypt=null;
		byte[] bytes=null;
		byte[] decipher=null;
		// say hello 
		connection.sendMessage("hello from :"+Client.user.getUsername());
		if(!connection.readMessage().equals("ok")){
			return false;
		}
		// send ticket
		connection.sendBytes(destUser.getTicket());
		if(!connection.readMessage().equals("ok")){
			return false;
		}
		// read public key
		destUser.setPublicKey(connection.readBytes());
		// init encrypt instance
		encrypt=new Encrypt(destUser.getPublicKey(),Client.clientPrivateKey);
		//send nounce
		bytes = encrypt.getEncryptedMessage(StringBytesSwitch.int2byte(nounce[0]++));
		connection.sendBytes(bytes);
		//read nounce
		bytes = connection.readBytes();
		decipher = (new Decrypt(destUser.getPublicKey(),Client.clientPrivateKey,bytes)).decrypt();
		int r=(new BigInteger(decipher)).intValue();
	    if(r!=nounce[0]){
	    	System.out.println("nounce auth failed: \n" + r +':'+ nounce);
	    	System.out.println();
	    	return false;
	    }
	    // send message
	    SecureLayer.sendEncryptWithNounce(encrypt,content.getBytes(),nounce, connection);
	    return true;
	}
	
	
	@Override
	public void run() {
		Client2Client.clientPort=22048;
		Socket tcpSocket;
		while(true){
			try {
			 serverSocket=new ServerSocket(Client2Client.clientPort);
			 break;
			 } catch (IOException e) {
				 Client2Client.clientPort++;
				 continue;
			}		// register the terminate Thread
		  }
		synchronized(this.clientPortSynchronism){
			this.clientPortSynchronism.notifyAll();
		}
		try {
			while(!Thread.interrupted()){
					tcpSocket = serverSocket.accept();
					if(tcpSocket==null){
						continue;
					}
					TCPConnection connection = TCPConnection.setUpConnection(tcpSocket, 2000);
					readContent(connection);
					connection.terminate();
				}
			
		 } catch (IOException e) {
		
		 }
		if(Client.DEBUG){
			System.out.println("Client2Client Thread interrupted, gonna quit");
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Client.DEBUG){	
				e.printStackTrace();
			}
		}
		
	}

	private void readContent(TCPConnection connection) throws IOException{
		byte[] ticket=null, target_pub_key=null,bytes=null;
		int[] nounce={0};
		String name=null;
		Encrypt encrypt=null;
		String message = connection.readMessage();
		if(!message.startsWith("hello")){
			return ;
		}
		name=message.split(":")[1].trim();
		// send ok
		connection.sendMessage("ok");
		// read ticket
		ticket=connection.readBytes();
		Decrypt decrypt = new Decrypt(Client2Server.serverKey,Client.clientPrivateKey,ticket);
		target_pub_key =decrypt.decrypt();
		// send ok
		connection.sendMessage("ok");
		// send public key
		connection.sendBytes(Client.clientPublicKey);
		// instance encrypt 
		encrypt=new Encrypt(target_pub_key,Client.clientPrivateKey);
		// read nounce
		bytes=connection.readBytes();
		bytes = (new Decrypt(target_pub_key,Client.clientPrivateKey,bytes)).decrypt();
		nounce[0]=(new BigInteger(bytes)).intValue();
		//send nounce
		bytes=encrypt.getEncryptedMessage(StringBytesSwitch.int2byte(++nounce[0]));
		connection.sendBytes(bytes);
		//read content
		bytes=connection.readBytes();
		bytes= SecureLayer.decryptWithNounce(target_pub_key, bytes, nounce);
		System.out.println(name+":  " + new String(bytes));
	}
	
	public void terminate(){
		try {
			if(serverSocket!=null){
				serverSocket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
