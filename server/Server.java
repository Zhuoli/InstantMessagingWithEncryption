package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
	static final int SERVER_MAX_NUM=10;
	static final boolean DEBUG = false;
	static int count=0;
	static ExecutorService executor=null;
	static AdminInteractive admin = null;
	static int PortNumber=0;
	static int clientPort=0;
	static String public_key_filename=null;
	static String private_key_filename=null;
	// terminate App. properly in case of user interrupting
	static class ExitHandler extends Thread{
		private ExitHandler(){
			super("Exit Handler");
		}
		public void run(){
			executor.shutdownNow();
			terminate();
			System.out.println("Server Control-C interrupted");
		}
		public static ExitHandler getInstance(){
			return new ExitHandler();
		}
	}
	public static void main(String[] argvs){

		System.out.println("Welcome to the Encypted Instant Messaging App.\nServer Running...");
	    executor = Executors.newFixedThreadPool(SERVER_MAX_NUM);
	    readConfigFile();
		TCPServer tcpServer= TCPServer.getServer();

		// register the terminate Thread
		Runtime.getRuntime().addShutdownHook(Server.ExitHandler.getInstance());
		admin = AdminInteractive.getInstance();
		while(true){
			Socket tcpSocket=tcpServer.accept();
			if(tcpSocket==null){
				continue;
			}
			System.out.println("New user has joined");
			executor.submit(new Task(tcpSocket, count++,10000));
		 }
	}
	// termination handler
	public static void terminate(){
		// store memory data to disk
		UsersInfoDatabase.getInstance().terminate();
	}
	
	private static void readConfigFile(){
		String[] settingPaths={"./src/server/setting.conf","./server/setting.conf"};
		String settingPath=null;
		for(String path : settingPaths){
			File file = new File(path);
			if(file.isFile()){
				settingPath=path;
				break;
			}
		}
		if(settingPath==null){
			System.out.println("Setting.conf file missed: './server/setting.conf'");
			System.exit(0);
		}
		try {
			BufferedReader br = new BufferedReader(new FileReader(settingPath));
			PortNumber=Integer.parseInt(br.readLine().split(":")[1].trim());
			clientPort=Integer.parseInt(br.readLine().split(":")[1].trim());
			public_key_filename = br.readLine().split(":")[1].trim();
			if(!isFile(public_key_filename)){
				System.err.println("Public Key file not exist,  "+ public_key_filename);
				System.exit(0);
			}
			private_key_filename= br.readLine().split(":")[1].trim();
			if(!isFile(private_key_filename)){
				System.err.println("private Key file not exist,  "+ private_key_filename);
				System.exit(0);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			if(Server.DEBUG){
				e.printStackTrace();
			}
			System.err.println("Client configure file:" + System.getProperty("user.dir") + "/setting.conf not exist.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(Server.DEBUG){
				e.printStackTrace();
			}
			System.err.println("Client configure file 'setting.conf' format not correct:\nhostname: IP\nport: number");
		}
	}
    /**********/
    private static boolean isFile(String a){

        if(!new File(a).isFile()){
          return false;
        }
      return true;
    }
}
