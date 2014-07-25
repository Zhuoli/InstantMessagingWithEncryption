package server;

import java.util.HashMap;

public class Database {
	static Database instance=null;
	public  HashMap<String,String> users=null;
	
	private Database(){
		users = new HashMap<String,String>();
		users.put("tony", "12345678");
		users.put("marry", "12345678");
		users.put("meili", "12345678");
		users.put("roze", "12345678");
	}
	
	public static Database getInstance(){
		if(instance == null){
			instance = new Database();
		}
		return instance;
	}
	public boolean authUser(String name, String password){
		if(users.containsKey(name)){
			return users.get(name).equals(password);
		}
		return false;
	}
}
