package server;


import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class Task implements Runnable{


    private ClientHandler clientHandler = null;
	private int id = 0;
	private String ip=null;
	public Task(Socket socket, int id,int timeout){
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
		line = clientHandler.readBytes();
		String head =new String(Arrays.copyOfRange(line, 0, line.length-32));
		byte[] hashcode =Arrays.copyOfRange(line, line.length-32, line.length);
		System.out.println("Client id: " + id + ":  "  +head );
		if(head.startsWith("authentication")){
			if(this.authUser(head,hashcode)){
				System.out.println("authentication:true");
				clientHandler.sendMessage("authentication:true");
			}else{
				System.out.println("authentication:false");
				clientHandler.sendMessage("authentication:false");
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
		this.terminate();
	}
	private void sendClientUserIP(){
		String message="UserIP:";
		message+=UserIPDatabase.getInstance().getOnlineUserIPs();
		clientHandler.sendMessage(message);
	}
	private boolean authUser(String line,byte[] hashcode){
		String[] strs = line.split(":");
		if(strs.length<4){
			System.out.println("Input format error: " + line);
			return false;
		}else{
			if(UsersInfoDatabase.getInstance().authUser(strs[3].trim(), hashcode)){
				// update user -> ip hash map
				UserIPDatabase.getInstance().update(strs[3].trim(), ip,strs[2].trim());
				return true;
			}else{
				return false;
			}
			
		}
	}
	protected void terminate(){
		System.out.println("Client id: " + id + " quit the chart");
		clientHandler.terminate();
	}

}
