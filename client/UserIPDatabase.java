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
	
	public String getIP(String name){
		System.out.println("enter getIpMethod");
		String ip=user_IP.get(name);
		return ip;
	}
	private void update(String name, String ip){
		synchronized(user_IP){
			user_IP.put(name, ip);
		}
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
}
