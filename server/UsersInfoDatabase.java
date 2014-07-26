package server;

import java.util.HashMap;

public class UsersInfoDatabase {
	static UsersInfoDatabase instance=null;
	public  HashMap<String,String> users=null;
	
	private UsersInfoDatabase(){
		users = new HashMap<String,String>();
		users.put("tony", "12345678");
		users.put("marry", "12345678");
		users.put("meili", "12345678");
		users.put("roze", "12345678");
	}
	
	public static UsersInfoDatabase getInstance(){
		if(instance == null){
			instance = new UsersInfoDatabase();
		}
		return instance;
	}
	public boolean authUser(String name, String password){
		System.out.println("Users in database");
		synchronized(users){
			for(String usr : users.keySet()){
				System.out.print(usr+'\t');
			}
			System.out.println('\n'+name+"  " +password);
			if(users.containsKey(name)){
				return users.get(name).equals(password);
			}
		}
		System.out.println("user name: " + name + " not exists");
		return false;
	}
	
	protected void changePassword(String name, String password){
		synchronized(users){
			users.put(name, password);
		}
		
	}
	protected void createNewUser(String name, String password){
		synchronized(users){
			users.put(name, password);
		}
	}
	protected boolean hasUser(String name){
		boolean f=false;
		synchronized(users){
			f= users.containsKey(name);
		}
		return f;
	}
	protected String getUsers(){
		String ret="";
		synchronized(users){
			for(String user : users.keySet()){
				ret+='\t'+user+';';
			}
		}
		return ret;
	}
}
