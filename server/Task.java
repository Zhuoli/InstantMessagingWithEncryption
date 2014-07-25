package server;


import java.net.Socket;
import java.net.SocketException;

public class Task implements Runnable{


    private ClientHandler clientHandler = null;
	private int id = 0;
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

		String line=null;
//		while((line = clientHandler.readMessage())!=null )
//		{ 
//			System.out.println("Client id: " + id + ":  "  + line);
//			line=line.toLowerCase();
//			if(line.startsWith("authentication")){
//				if(this.authUser(line)){
//					System.out.println("authentication:true");
//					clientHandler.writeMessage("authentication:true");
//				}else{
//
//					System.out.println("authentication:false");
//					clientHandler.writeMessage("authentication:false");
//				}
//			}else{
//				clientHandler.writeMessage("Message received: " + line+ '\n');
//			}
//			if(Thread.interrupted()){
//				break;
//			}
//		}
		while((line=clientHandler.readMessage())!=null){
			
			System.out.println("Client id: "+ id + ": " + line);
			System.out.println("Sender: authentication:true" );
			clientHandler.sendMessage("authentication:true");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			clientHandler.sendMessage("authentication:true");
		}
		this.terminate(0);
	}
	private boolean authUser(String line){
		String[] strs = line.split(":");
		if(strs.length<3){
			return false;
		}else{
			return Database.getInstance().authUser(strs[1], strs[2]);
		}
	}
	protected void terminate(int quitNum){
		System.out.println("Client id: " + id + " quit the chart");
		clientHandler.terminate();
		System.exit(quitNum);
	}

}
