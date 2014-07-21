package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class User {
	private String name="";
	private String hashKey="";
	private BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	// user instance
	private static User user=null;
	private User(String name, String hashcode){
		this.name=name;
		this.hashKey=hashcode;
	}
	
	public static User login(){
		if(User.user ==null){
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please input user name, type 'return' when finish");
			try {
				String name;
				name = in.readLine();
				System.out.println("Please input user password");
				String hashcode = hashIt(in.readLine());
				// initiate user information
				User.user=new User(name,hashcode);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(-1);
			}
		}
		if(Client.DEBUG){
			System.out.println("User Information:");
			System.out.println("User Name:\t" + user.name);
			System.out.println("Hashed Password:\t" + user.hashKey);
		}
		return User.user;
	}
	// Hash a string using SHA256, 
	// Given: PlanText
	// Return: HashedString
	public static String hashIt(String key){
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		messageDigest.update(key.getBytes());
		String encryptedString = new String(messageDigest.digest());
		return encryptedString;
	}
	public String getHashedKey(){
		return user.hashKey;
	}
	public String getUsername(){
		return user.name;
	}
	
	public String getUserInput(){
		try {
			return inFromUser.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		return "";
	}
}
		
