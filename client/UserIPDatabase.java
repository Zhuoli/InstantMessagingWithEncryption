package client;

import java.util.HashMap;

public class UserIPDatabase {
	private HashMap<String,String> user_IP = null;
	private static UserIPDatabase instance = null;
	private UserIPDatabase(){
		user_IP=new HashMap<String,String>();
	}
	public static UserIPDatabase getInstance(){
		if(instance==null){
			instance = new UserIPDatabase();
		}
		return instance;
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
	
	public String getIP(String name){
		String ip=user_IP.get(name).split(":")[0];
		return ip;
	}
	public int getPort(String name){
		return Integer.parseInt(user_IP.get(name).split(":")[1]);
		
	}
	protected void update(){
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
//		synchronized(user_key){
//			if(user_key.containsKey(userName)){
//				ret = user_key.get(userName);
//			}
//		}
		return ret;
	}

}
