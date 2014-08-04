package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class User {
	////
	private String name="";
	private byte[] hashKey=null;
	private BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
	// user instance
	private static User user=null;
	private User(String name, byte[] hashcode){
		this.name=name;
		this.hashKey=hashcode;
	}
	
	public static User login(){
		System.out.println("Welcome to the Encypted Instant Messaging App.\nClient Running...");
		if(User.user ==null){
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please input user name, type 'return' when finish");
			try {
				String name;
				name = in.readLine();
				System.out.println("Please input user password");
				String password=in.readLine();
				byte[] hashcode = hashIt(password);
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
	//	System.out.println("digest length:"+messageDigest.getDigestLength());
		byte[] digests= messageDigest.digest();
//		System.out.println("digest length:"+messageDigest.getDigestLength());
		return digests;
		
	}
	public byte[] getHashedKey(){
		//byte[] h =  {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32};
		//return h;
//		byte[] bar = new byte[hashKey.length];
//		System.out.println("user hashed byte array length:"+ user.hashKey.length);
//		for(int i=0;i<hashKey.length;i++){
//			bar[i]=hashKey[i];
//			System.out.print("  "+i);
//		}
//		return bar;
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
		
