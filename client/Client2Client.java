package client;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Arrays;

public class Client2Client implements Runnable{
	static private Client2Client instance =null;
	User user = null;
	int timeout=1000;
	protected Thread t = null;
	protected ServerSocket serverSocket=null;
	
	
	private Client2Client(User user){
		this.user=user;
	}
	public static Client2Client getInstance(User user){
		if(instance==null){
			instance=new Client2Client(user);
			instance.t = new Thread(instance);
			instance.t.start();
		}
		return instance;
	}
	
	public Thread setUpListen(){
		Thread t = new Thread(this);
		t.start();
		return t;
	}
	public boolean send2client(String targetName, String content){
		Encrypt encrypt=null;
		byte[] target_public_key=null;
		byte[] bytes=null;
		byte[] decipher=null;
		int[] nounce ={0};
		SecureRandom random = new SecureRandom();
		nounce[0] = random.nextInt();
		if(!UserIPDatabase.getInstance().hasThisUser(targetName)){
			UserIPDatabase.getInstance().update();
		}
		if(!UserIPDatabase.getInstance().hasThisUser(targetName)){
			System.out.println("Sending ERROR: The use: '" + targetName + "' is not online currentlly");
			return false;
		}
		String ip =UserIPDatabase.getInstance().getIP(targetName);
		int port = UserIPDatabase.getInstance().getPort(targetName);
		byte[] ticket=UserIPDatabase.getInstance().getTICKET(targetName);
		TCPConnection connection = null;
		try {
			connection=TCPConnection.setUpConnection(ip,port, timeout);
		} catch (Exception e){
			System.err.println("Client has off line.");
			return false;
		}
		// say hello 
		connection.sendMessage("hello from :"+Client.user.getUsername());
		if(!connection.readMessage().equals("ok")){
			return false;
		}
		// send ticket
		connection.sendBytes(ticket);
		if(!connection.readMessage().equals("ok")){
			return false;
		}
		// read public key
		target_public_key=connection.readBytes();

		// init encrypt instance
		encrypt=new Encrypt(target_public_key,Client.clientPrivateKey);
		//send nounce
		bytes = encrypt.getEncryptedMessage(int2byte(nounce[0]++));
		connection.sendBytes(bytes);
		//read nounce
		bytes = connection.readBytes();
		decipher = (new Decrypt(target_public_key,Client.clientPrivateKey,bytes)).decrypt();
		int r=(new BigInteger(decipher)).intValue();
	    if(r!=nounce[0]){
	    	System.out.println("nounce auth failed: \n" + r +':'+ nounce);
	    	System.out.println();
	    	return false;
	    }
		// send message
	    sendEncryptWithNounce(encrypt,content.getBytes(),nounce, connection);
		System.out.println(user.getUsername() + " to " +targetName + ": "+content);
		connection.terminate();
		return true;
	}
	public void connectionTerminate(){
	}
	@Override
	public void run() {
		if(Client.DEBUG){
			System.out.println("Client Listening thread running...");
		}
		Client.clientPort=22048;
		while(true){
			try {
			 serverSocket=new ServerSocket(Client.clientPort);
			 break;
			 } catch (IOException e) {
				 Client.clientPort++;
				 continue;
			}		// register the terminate Thread
		  }
		Socket tcpSocket;
		
		try {
			while(!Thread.interrupted()){
					byte[] ticket=null, target_pub_key=null,bytes=null;
					int[] nounce={0};
					String name=null;
					Encrypt encrypt=null;
					tcpSocket = serverSocket.accept();
					if(tcpSocket==null){
						continue;
					}
					TCPConnection connection = TCPConnection.setUpConnection(tcpSocket, 2000);
					String message = connection.readMessage();
					if(!message.startsWith("hello")){
						continue;
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
					bytes=encrypt.getEncryptedMessage(int2byte(++nounce[0]));
					connection.sendBytes(bytes);
					//read content
					bytes=connection.readBytes();
					bytes= decryptWithNounce(target_pub_key, bytes, nounce);
					System.out.println(name+":  " + new String(bytes));
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

	
	
	private byte[] decryptWithNounce(byte[] target_public_key,byte[] message,int[] nounce){
		Decrypt decrypt = new Decrypt(target_public_key,Client.clientPrivateKey,message);
		byte[] decipher =decrypt.decrypt();
		return chuncateNounce(decipher,nounce);
	}
	private void sendEncryptWithNounce(Encrypt encrypt,byte[] message,int[] nounce, TCPConnection connection){
		byte[] bytes= combineBytes(int2byte(nounce[0]++),message);
		bytes=encrypt.getEncryptedMessage(bytes);
		connection.sendBytes(bytes);
	}
	protected byte[] combineBytes(byte[] a,byte[] b){
		byte[] barr=new byte[a.length+b.length];
		System.arraycopy(a, 0, barr, 0, a.length);
		System.arraycopy(b, 0, barr, a.length, b.length);
		return barr;
	}
	
	private byte[] chuncateNounce(byte[] line,int[] nounce){
		byte[] nounceByte = Arrays.copyOfRange(line, 0, 4);
		if(nounce[0]++!=(new BigInteger(nounceByte)).intValue()){
			System.out.println("Nounce not equal: " + nounce[0] +" != "+(new BigInteger(nounceByte)).intValue());
			return null;
		}
		return Arrays.copyOfRange(line, 4, line.length);
	}
	
	private byte[] int2byte(int input){
		byte[] conv = new byte[4];
		conv[3] = (byte) (input & 0xff);
		input >>= 8;
		conv[2] = (byte) (input & 0xff);
		input >>= 8;
		conv[1] = (byte) (input & 0xff);
		input >>= 8;
		conv[0] = (byte) input;
		return conv;
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
