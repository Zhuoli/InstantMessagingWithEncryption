package server;

import java.util.HashMap;

/*** User IP Daemon
 * 
 *  Used to maintain a hash map of the user->ip relationship
 * @author zhuoli
 *
 */

public class UserIPDatabase {
	static UserIPDatabase instance = null;
	HashMap<String,String> user_IP=null;
	HashMap<String,byte[]> user_key=null;
	private UserIPDatabase(){
		user_IP=new HashMap<String,String>();
		user_key=new HashMap<String,byte[]>();
	}
	
	public static UserIPDatabase getInstance(){
		if(instance==null){
			instance=new UserIPDatabase();
		}
		return instance;
	}
	
	/*********************/
	public void update(String userName, String ip,String port,byte[] key){
		synchronized(user_IP){
			user_IP.put(userName, ip+':'+port);
		}
		user_key.put(userName, key);
	}
	public String getIP(String userName){
		String ret="";
		synchronized(user_IP){
			if(user_IP.containsKey(userName)){
				ret= user_IP.get(userName);
			}
		}
		return ret;
	}
	public byte[] getKey(String userName){
		byte[] ret=null;
		synchronized(user_key){
			if(user_key.containsKey(userName)){
				ret = user_key.get(userName);
			}
		}
		return ret;
	}
	
	public byte[] getTICKET(String user,byte[] content, byte[] privateKey){
		byte[] ret=null;
		synchronized(user_key){
			if(user_key.containsKey(user)){
				byte[] pub_key = user_key.get(user);
				EncryptDatabase encrypt = new EncryptDatabase(pub_key,privateKey);
				ret=encrypt.getEncryptedMessage(content);
			}else{
				System.out.println("Don't have ticket for user: "+user);
			}
		}
		return ret;
		
	}
	public void deleteUser(String userName){
		synchronized(user_IP){
			if(user_IP.containsKey(userName)){
				user_IP.remove(userName);
			}
		}
	}
	public String getOnlineUserIPs(){
		String message="";
		String ip="";
		synchronized(user_IP){
			for(String user : user_IP.keySet()){
				ip=user_IP.get(user);
				message+=user+'/'+ip+';';
			}
		}
		return message;
	}
	public String getOnlineUsers(){
		String message="";
		synchronized(user_IP){
			for(String user : user_IP.keySet()){
				message+=user+";";
			}
		}
		return message;
	}

}
