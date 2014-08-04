package client;

import java.util.HashMap;

public class Database {
	private HashMap<String,String> user_IP = null;
	private HashMap<String,byte[]> user_TICKET=null;
	private HashMap<String,byte[]> user_KEY = null;
	private static Database instance = null;
	private Database(){
		user_IP=new HashMap<String,String>();
		user_TICKET = new HashMap<String,byte[]>();
		user_KEY = new HashMap<String,byte[]>();
	}
	public static Database getInstance(){
		if(instance==null){
			instance = new Database();
		}
		return instance;
	}
	
	public DestinationUser getDestUser(String targetName){
		DestinationUser obj = new DestinationUser();
		obj.setIP(this.getIP(targetName));
		obj.setPort(this.getPort(targetName));
		obj.setTicket(this.getTICKET(targetName));
		
		return obj;
	}
	
	public String onlineUsers(){
		String users = "";
		for(String user:user_IP.keySet()){
			users+=user+';';
		}
		return users;
	}
	
	public boolean hasThisUser(String name){
		return user_IP.containsKey(name);
	}
	
	protected void putTICKET(String name,byte[] t){
		user_TICKET.put(name, t);
	}
	protected void putKEY(String name, byte[] k){
		user_KEY.put(name, k);
	}
	protected byte[] getTICKET(String name){
		byte[] bytes=null;
		if(user_TICKET.containsKey(name)){
			bytes=user_TICKET.get(name);
		}
		return bytes;
	}
	protected byte[] getKEY(String name){
		byte[] bytes=null;
		if(user_KEY.containsKey(name)){
			bytes=user_KEY.get(name);
		}
		return bytes;
	}
	
	public String getIP(String name){
		String ip=user_IP.get(name).split(":")[0];
		return ip;
	}
	public int getPort(String name){
		return Integer.parseInt(user_IP.get(name).split(":")[1]);
		
	}
	protected void update() throws Exception{
		Client.c2server.requestUpdateUsersInfo();
	}
	
	protected void insertUsers(String message){
		String[] pairs = message.split(";");
		synchronized(user_IP){
		for(String pair : pairs){
			String[] strs = pair.split("/");
			user_IP.put(strs[0], strs[1]);
		}
		}
	}
	public byte[] getKey(String userName){
		byte[] ret=null;
		if(user_KEY.containsKey(userName)){
			ret = user_KEY.get(userName);
		}
		return ret;
	}

}
