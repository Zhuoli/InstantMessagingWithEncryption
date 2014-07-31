package server;


import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.Arrays;


public class Task implements Runnable{


    private ClientHandler clientHandler = null;
	private int id = 0;
	private String ip=null;
	private EncryptDatabase encrypt=null;
	private int nounce=0;
	private byte[] clientPublicKey=null;
	public Task(Socket socket, int id,int timeout){
		SecureRandom random = new SecureRandom();
		nounce = random.nextInt();
		this.id = id;
		try {
			socket.setSoTimeout(timeout);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			if(Server.DEBUG){
				e.printStackTrace();
			}
			System.err.println("Error during set socket timeout");
			System.exit(-1);
		}
		clientHandler= new ClientHandler(socket,this);
	}

	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		System.out.println("Welcome new users, ID: " + id +'\n');
		ip=clientHandler.getClientIPAddress();
		byte[] line=null;
		byte[] bytes=null;
		// read client public key
		line = clientHandler.readBytes();
		if(!(new String(Arrays.copyOfRange(line, 0, 4))).startsWith("key")){
			clientHandler.sendMessage("authentication:false");
			return;
		}else{
			clientPublicKey=Arrays.copyOfRange(line, 4, line.length);
		}
		encrypt=new EncryptDatabase(clientPublicKey,Server.privateKey);
		// send random number
	    bytes=encrypt.getEncryptedMessage(int2byte(nounce++));
	    clientHandler.sendBytes(bytes);
	    line=clientHandler.readBytes();
	    byte[] decipher =  (new DecryptDataBase(clientPublicKey,Server.privateKey,line)).decrypt();
	    int r=(new BigInteger(decipher)).intValue();
	    if(r!=nounce){
	    	System.out.println("nounce auth failed: \n" + r);
	    	System.out.println();
	    	return;
	    }
		// read auth info.
		line = clientHandler.readBytes();
		line = decryptWithNounce(line);
		String head =new String(Arrays.copyOfRange(line, 0, line.length-32));
		byte[] hashcode =Arrays.copyOfRange(line, line.length-32, line.length);
		System.out.println("Client id: " + id + ":  "  +head );
		if(head.startsWith("authentication")){
			if(this.authUser(head,hashcode,clientPublicKey)){
				byte[] AuthTrue="authentication:true".getBytes();
				System.out.println("\nauthentication:true");
				sendWithEncryptNounce(AuthTrue);
			}else{
				byte[] AuthFalse="authentication:false".getBytes();
				System.out.println("\nauthentication:frue");
				sendWithEncryptNounce(AuthFalse);
				this.terminate();
				return;
			}
		}else{
			System.out.println("Client didn't start with authentication, gonna drop connection. "+line);
			clientHandler.sendMessage("Please authenticate yourself");
			this.terminate();
			return;
		}
		// send on line users -> ip key
		this.sendClientUserIP();
		this.sendClientUserTICKET(clientPublicKey);
		this.terminate();
	}
	private void sendClientUserTICKET(byte[] clientPublicKey){
		String[] usrs=UserIPDatabase.getInstance().getOnlineUsers().split(";");
		for(String user : usrs){
			// send user name
			byte[] bytes = ("Ticket:"+user).getBytes();
			sendWithEncryptNounce(bytes);
			// send ticket
			bytes = UserIPDatabase.getInstance().getTICKET(user,clientPublicKey,Server.privateKey);
			sendWithEncryptNounce(bytes);
		}
		sendWithEncryptNounce("TDone".getBytes());
	}
	private void sendClientUserIP(){
		String message="UserIP:";
		message+=UserIPDatabase.getInstance().getOnlineUserIPs();
		byte[] bytes=message.getBytes();
		sendWithEncryptNounce(bytes);
	}
	private boolean authUser(String line,byte[] hashcode,byte[] key){
		String[] strs = line.split(":");
		if(strs.length<4){
			System.out.println("Input format error: " + line);
			return false;
		}else{
			if(UsersInfoDatabase.getInstance().authUser(strs[3].trim(), hashcode)){
				// update user -> ip hash map
				UserIPDatabase.getInstance().update(strs[3].trim(), ip,strs[2].trim(),key);
				return true;
			}else{
				return false;
			}
			
		}
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
	

	private byte[] chuncateNounce(byte[] line){
		byte[] nounceByte = Arrays.copyOfRange(line, 0, 4);
		if(nounce++!=(new BigInteger(nounceByte)).intValue()){
			System.out.println("Nounce not equal: " + nounce +" != "+(new BigInteger(nounceByte)).intValue());
			return null;
		}
		return Arrays.copyOfRange(line, 4, line.length);
	}
	protected byte[] combineBytes(byte[] a,byte[] b){
		byte[] barr=new byte[a.length+b.length];
		System.arraycopy(a, 0, barr, 0, a.length);
		System.arraycopy(b, 0, barr, a.length, b.length);
		return barr;
	}

	private void sendWithEncryptNounce(byte[] message){
		byte[] bytes= combineBytes(int2byte(nounce++),message);
		bytes = encrypt.getEncryptedMessage(bytes);
		clientHandler.sendBytes(bytes);
		
	}
	private byte[] decryptWithNounce(byte[] bytes){
		byte[] decipher = (new DecryptDataBase(clientPublicKey,Server.privateKey,bytes)).decrypt();
		return chuncateNounce(decipher);
	}


	protected void terminate(){
		System.out.println("Client id: " + id + " quit the chart");
		clientHandler.terminate();
	}
	
}
