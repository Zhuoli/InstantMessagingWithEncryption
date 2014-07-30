package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
	static byte[] publicKey=null;
	static byte[] privateKey=null;
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
			publicKey = readByteFromFile(br.readLine().split(":")[1].trim());
			privateKey=readByteFromFile( br.readLine().split(":")[1].trim());
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
	// read bytes from a file
	private static  byte[] readByteFromFile(String fileName) {
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
}
