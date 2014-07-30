package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



/** 
 *  An interactive interface for Administrator to control this service
 * @author zhuoli
 * -- ls: list the users
 * -- password username password:  change user's password or create a new user with this password
 * -- del username: delete this user
 * -- ip username: show the ip address of this user
 * */
public class AdminInteractive implements Runnable{
	BufferedReader  in = null;
	static AdminInteractive instance =null;
	Thread t = null;
	private AdminInteractive(){
		in = new BufferedReader(new InputStreamReader(System.in));
		this.t=new Thread(this);
		this.t.start();
	}
	public static AdminInteractive getInstance(){
		if(instance ==null){
			instance=new AdminInteractive();
		}
		return instance;
	}
	@Override
	public void run() {
		//System.out.println();
		String input=null;
		while(!Thread.interrupted()){
			try {
				input=in.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			parseInput(input.toLowerCase().trim());
		}
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Admin. Interactive Thread Interrupted");
	}
	
	private void parseInput(String input){
		if(input.startsWith("ls") || input.startsWith("list")){
			this.showUsers();
		}else if(input.startsWith("password")){
			this.changePassword(input);
		}else if(input.startsWith("ip")){
			this.showIP(input);
		}else if(input.startsWith("del ")){
			this.delUser(input);
		}else if(input.startsWith("key ")){
			this.showKey(input);
		}else{
			return;
		}
		
	}
	private void showKey(String input){
		String[] strs = input.split(" ");
		if(strs.length==2){
			String username = strs[1];
			byte[] bytes = UserIPDatabase.getInstance().getKey(username);
			for(int i : bytes){
				System.out.print(" "+i+" ");
			}
			System.out.println();
		}
	}
	
	private void delUser(String input){
		String[] strs = input.split(" ");
		String username = strs[1];
		UsersInfoDatabase.getInstance().delUser(username);
	}
	private void showIP(String input){
		String[] strs = input.split(" ");
		String ip = "";
		if(strs.length==2){
			ip=UserIPDatabase.getInstance().getIP(strs[1].trim());
			System.out.println(ip);
		}
		
	}
	private void changePassword(String input){
		String[] strs=input.split(" ");
		if(strs.length==3){
			if(strs[2].length()<8){
				System.out.println("New passowrd should not less than 8 letters, please try again");
				return;
			}
			UsersInfoDatabase.getInstance().changePassword(strs[1].trim(), strs[2].trim());
			System.out.println("New User has been added: "+strs[1].trim());
		}
	}
	
	private void showUsers(){
		String users = UsersInfoDatabase.getInstance().getUsers();
		System.out.println(users);
	}

}
