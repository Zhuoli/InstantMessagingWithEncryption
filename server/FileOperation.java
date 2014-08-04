package server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileOperation {
	// read bytes from a file
	protected static byte[] readByteFromFile(String fileName) {
		File f = new File(fileName);
		if(!f.isFile()){
			f=new File("./src"+fileName.substring(1));
		}
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
			System.err.println("read file error: "+System.getProperty("user.dir")+'/'+fileName);
			System.exit(0);
		};
		
		return buffer;
		
	}
}
