package client;

public class Client2Client {
	static private Client2Client instance =null;
	private Client2Client(){
		
	}
	public static Client2Client getInstance(){
		if(instance==null){
			instance=new Client2Client();
		}
		return instance;
	}
	
	public static boolean setUpListen(){
		
		return true;
	}
	public boolean send2client(String targetName, String content){
		
		return true;
	}
}
