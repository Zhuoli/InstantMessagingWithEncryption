package server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class UsersInfoDatabase {
	static UsersInfoDatabase instance=null;
	static String dataName="server/users.dat";
	
	
	EncryptDatabase encrypt = null;
	DecryptDataBase decrypt=null;
	public  HashMap<String,String> users=null;
	
	private UsersInfoDatabase(){
		encrypt = new EncryptDatabase(Server.publicKey,Server.privateKey);
		decrypt = new DecryptDataBase(Server.publicKey,Server.privateKey,readByteFromFile(UsersInfoDatabase.dataName));
		users = readFromFile();
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
	
	// read bytes from a file
	private  byte[] readByteFromFile(String fileName) {
		File f = new File(fileName);
		byte[] buffer=null;
		try {
			if (f.length() > Integer.MAX_VALUE)
				System.out.println("File is too large");
	
			buffer = new byte[(int) f.length()];
			InputStream ios;
				ios = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(ios);
			dis.readFully(buffer);
			dis.close();
			ios.close();
		} catch (Exception e) {
			System.err.println("read file error");
			System.exit(0);
		};
		
		return buffer;
		
	}
	protected HashMap<String,String> readFromFile(){
		HashMap<String,String> users = new HashMap<String,String>();
		String plain_text = new String(decrypt.decrypt());
		//System.out.println("Decrypted file: " + plain_text);
		for(String pair : plain_text.split(";")){
			String[] strs = pair.split(":");
			users.put(strs[0], strs[1]);
		}
		return users;
	}
	protected void encrypt2file(String targetFile){
		String plain_text = this.getUserPasswds();
		byte[] cipher = encrypt.getEncryptedMessage(plain_text);
		encrypt.write2file(targetFile, cipher);
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
	public boolean authUser(String name, byte[] hashcode){
		System.out.println("Users in database");
		synchronized(users){
			for(String usr : users.keySet()){
				System.out.print(usr+'\t');
			}
			if(users.containsKey(name)){
				byte[] userhash = hashIt(users.get(name));
				byte[] apphash = hashcode;
				//System.out.println("\nUser's hashcode:\n"+getAscii(userhash));
				//System.out.println("Login's hashcode:\n"+getAscii(apphash));
				boolean f= Arrays.equals(userhash, apphash);
			//	System.out.println("HashCode equal?: "+f);
				return f;
			}
		}
		System.out.println("user name: " + name + " not exists");
		return false;
		
	}
	private String getAscii(byte[] bytes){
		StringBuilder str = new StringBuilder();
		for(int i=0;i<bytes.length;i++){
			str.append(bytes[i]+" ");
		}
		return str.toString();
	}
	// Hash a string using SHA256, 
	// Given: PlanText
	// Return: hashed 32 len byte
	public static byte[]  hashIt(String key){
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		messageDigest.update(key.getBytes());
		//System.out.println("digest length:"+messageDigest.getDigestLength());
		byte[] digests= messageDigest.digest();
		//System.out.println("digest length:"+messageDigest.getDigestLength());
		return digests;
		
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
	// delete user
	protected void delUser(String name){
		synchronized(name){
			if(users.containsKey(name)){
				users.remove(name);
				encrypt2file(UsersInfoDatabase.dataName);
			}
		}
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
