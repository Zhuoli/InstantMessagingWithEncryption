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
	private UserIPDatabase(){
		user_IP=new HashMap<String,String>();
	}
	
	public static UserIPDatabase getInstance(){
		if(instance==null){
			instance=new UserIPDatabase();
		}
		return instance;
	}
	
	/*********************/
	public void update(String userName, String ip){
		synchronized(user_IP){
			user_IP.put(userName, ip);
		}
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

}
