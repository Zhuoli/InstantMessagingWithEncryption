package server;


import java.net.Socket;

public class Task implements Runnable{


    private ClientSocket clientSocket = null;
	private int id = 0;
	public Task(Socket connection, int id){
		clientSocket= new ClientSocket(connection);
		this.id = id;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		clientSocket.writeMessage("Welcome new users, ID: " + id +'\n');

		String line=null;
		while((line = clientSocket.readMessage())!=null)
		{ 
			System.out.println("Client id: " + id + ":  "  + line);
			clientSocket.writeMessage("Message received: " + line+ '\n');
		}
		System.out.println("Client id: " + id + " quit the chart");
		clientSocket.close();
	}

}
