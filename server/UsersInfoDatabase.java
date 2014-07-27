package server;

import java.util.HashMap;

public class UsersInfoDatabase {
	static UsersInfoDatabase instance=null;
	static String dataName="server/users.dat";
	public  HashMap<String,String> users=null;
	
	private UsersInfoDatabase(){
		users = readFromFile();
//		users.put("tony", "12345678");
//		users.put("marry", "12345678");
//		users.put("meili", "12345678");
//		users.put("roze", "12345678");
	}
	
	private String getUserPasswds(){
		String ret="";
		synchronized(users){
			for(String user : users.keySet()){
				ret+=user+':'+users.get(user)+';';
			}
		}
		return ret;
	}
	protected HashMap<String,String> readFromFile(){
		HashMap<String,String> users = new HashMap<String,String>();
		String plain_text = DecryptDataBase.getInstance().decryptFile(UsersInfoDatabase.dataName);
		//System.out.println("Decrypted file: " + plain_text);
		for(String pair : plain_text.split(";")){
			String[] strs = pair.split(":");
			users.put(strs[0], strs[1]);
		}
		return users;
	}
	protected void encrypt2file(String targetFile){
		String plain_text = this.getUserPasswds();
		EncryptDatabase.getInstance().encrypt2file(plain_text, targetFile);
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
	// change user's password
	protected void changePassword(String name, String password){
		synchronized(users){
			users.put(name, password);
		}
		encrypt2file(UsersInfoDatabase.dataName);
		
	}
	// create new user
	protected void createNewUser(String name, String password){
		synchronized(users){
			users.put(name, password);
		}
		encrypt2file(UsersInfoDatabase.dataName);
	}
	// check if this user exists
	protected boolean hasUser(String name){
		boolean f=false;
		synchronized(users){
			f= users.containsKey(name);
		}
		return f;
	}
	// return all the registered users
	protected String getUsers(){
		String ret="";
		synchronized(users){
			for(String user : users.keySet()){
				ret+='\t'+user+';';
			}
		}
		return ret;
	}
	protected void terminate(){
		this.encrypt2file(UsersInfoDatabase.dataName);
	}
}
