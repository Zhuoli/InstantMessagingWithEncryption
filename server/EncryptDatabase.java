package server;


import javax.crypto.*;

import java.security.*;
import java.security.spec.*;
import java.io.*;

public class EncryptDatabase {
    // byte representation of parameters and IV
    static EncryptDatabase instance = null;
    byte[] iv, cipherText, publicKey, plainText, privateKey, signature, aesKeyEncyrpted;

	public EncryptDatabase( ){
	}
	
	protected static EncryptDatabase getInstance(){
		if(instance==null){
			instance=new EncryptDatabase();
		}
		return instance;
	}
   protected void encrypt2file(String plaintext, String output_file){

		Key aesKey;
		try{
	        /*********** Symmetric Encryption *************/	
			// Symmetric (AES) key generation
			KeyGenerator aesKeyGen = KeyGenerator.getInstance("AES");
			aesKey = aesKeyGen.generateKey();
			cipherText=aesEncrypt(plaintext,aesKey);
			/*************  RSA Encryption *****************/
			rsaEncrypt(Server.public_key_filename,Server.private_key_filename,aesKey);
			// write crypted data 2 file
			write2file(output_file);
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(0);
		}
   }
  		private void write2file(String output_file)throws Exception{
  			/** write to file **/
            writeByteToFile(new File(output_file),signature);
            // write AES key
            appendByteToFile(new File(output_file),aesKeyEncyrpted);
            // write cipher text
            appendByteToFile(new File(output_file),cipherText);
		   if(Server.DEBUG){
		  	  // write signature
			   System.out.println("Signature in HEX");
	           System.out.println("Length: "+ signature.length);
			    for (byte b : signature){
			    	System.out.print(String.format("%02X ", b));
			    }
                System.out.println("\nAES encrypted key in HEX");
                System.out.println("Length: "+ aesKeyEncyrpted.length);
                for (byte b : aesKeyEncyrpted){
              	  System.out.print(String.format("%02X ", b));
                }
         	   System.out.println("\nCiphter Text");
         	   for (byte b : cipherText){
         		   System.out.print(String.format("%02X ", b));
         	   }
         	   System.out.println();
                
           }
  		}
  		private void rsaEncrypt(String public_key_filename, String private_key_filename, Key aesKey) throws Exception{
  			Cipher publicChiper = Cipher.getInstance("RSA");
			Signature sig = Signature.getInstance("SHA512withRSA");
			KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec privateSpec;
			X509EncodedKeySpec publicSpec;
			PrivateKey prvKey;
			PublicKey pubKey;

			// init RSA keys
			privateKey = readByteFromFile(new File(private_key_filename));
			publicKey = readByteFromFile(new File(public_key_filename));
			privateSpec = new PKCS8EncodedKeySpec(privateKey);
			publicSpec = new X509EncodedKeySpec(publicKey);
			prvKey = rsaKeyFactory.generatePrivate(privateSpec);
			pubKey = rsaKeyFactory.generatePublic(publicSpec);

			publicChiper.init(Cipher.WRAP_MODE, pubKey);
	        // encrypt AESkey
			aesKeyEncyrpted = publicChiper.wrap(aesKey);

			sig.initSign(prvKey);
			//sig.update(iv);
			sig.update(cipherText);
			sig.update(aesKeyEncyrpted);
			signature = sig.sign();
  		}
  		// AES encrypt plain text
  		private byte[] aesEncrypt(String plaintext,Key aesKey) throws Exception{
  			byte[] cipherText=null;
			plainText = plaintext.getBytes();
			Cipher secCipher = Cipher.getInstance("AES");
			// setup IV key with random data and encrypt the file using AES key.
			secCipher.init(Cipher.ENCRYPT_MODE, aesKey);
			iv = secCipher.getIV();
			cipherText = secCipher.doFinal(plainText);
			return cipherText;
  		}


	// read bytes from a file
	private static byte[] readByteFromFile(File f) throws Exception {

		if (f.length() > Integer.MAX_VALUE)
			System.out.println("File is too large");

		byte[] buffer = new byte[(int) f.length()];
		InputStream ios = new FileInputStream(f);;
		DataInputStream dis = new DataInputStream(ios);
		dis.readFully(buffer);
		dis.close();
		ios.close();

		return buffer;
	}
        private static boolean writeByteToFile(File f,byte[] bytes) throws Exception{
          FileOutputStream stream = new FileOutputStream(f);
          try{
              stream.write(bytes);
          }finally{
              stream.close();
          }
              return true; 
        }
        private static boolean appendByteToFile(File f,byte[] bytes) throws Exception{
          FileOutputStream stream = new FileOutputStream(f,true);
          try{
              stream.write(bytes);
          }finally{
              stream.close();
          }
              return true; 
        }
}
