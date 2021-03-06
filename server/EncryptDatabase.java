package server;


import javax.crypto.*;

import java.security.*;
import java.security.spec.*;
import java.io.*;

public class EncryptDatabase {
    // byte representation of parameters and IV
    byte[] iv, cipherText, publicKey, plainText, privateKey, signature, aesKeyEncyrpted;

	public EncryptDatabase(byte[] publicKey,byte[] privateKey ){
		this.publicKey=publicKey;
		this.privateKey=privateKey;
	}
	
   protected void encrypt2file(String plaintext, String output_file){

		// init RSA keys

			byte[] encrypted_message = getEncryptedMessage(plaintext);
			// write crypted data 2 file
			try {
				write2file(output_file,encrypted_message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   }
   
   protected byte[] getEncryptedMessage(String plain_text){
	   Key aesKey;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		try {
	        /*********** Symmetric Encryption *************/	
			// Symmetric (AES) key generation
			KeyGenerator aesKeyGen;
				aesKeyGen = KeyGenerator.getInstance("AES");
			aesKey = aesKeyGen.generateKey();
			cipherText=aesEncrypt(plain_text.getBytes(),aesKey);
			/*************  RSA Encryption *****************/
			rsaEncrypt(aesKey);

			outputStream.write(signature);
			outputStream.write(aesKeyEncyrpted);
			outputStream.write(cipherText);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return outputStream.toByteArray();
   }
   protected byte[] getEncryptedMessage(byte[] plain_text){
	   Key aesKey;

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
		try {
	        /*********** Symmetric Encryption *************/	
			// Symmetric (AES) key generation
			KeyGenerator aesKeyGen;
				aesKeyGen = KeyGenerator.getInstance("AES");
			aesKey = aesKeyGen.generateKey();
			cipherText=aesEncrypt(plain_text,aesKey);
			/*************  RSA Encryption *****************/
			rsaEncrypt(aesKey);

			outputStream.write(signature);
			outputStream.write(aesKeyEncyrpted);
			outputStream.write(cipherText);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   return outputStream.toByteArray();
   }
  		protected void write2file(String output_file,byte[] message){
  			/** write to file **/
            try {
				writeByteToFile(new File(output_file),message);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println("Write failed");
				e.printStackTrace();
				System.exit(0);
			}
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
  		private void rsaEncrypt( Key aesKey) throws Exception{
  			Cipher publicChiper = Cipher.getInstance("RSA");
			Signature sig = Signature.getInstance("SHA512withRSA");
			KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec privateSpec;
			X509EncodedKeySpec publicSpec;
			PrivateKey prvKey;
			PublicKey pubKey;

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
  		private byte[] aesEncrypt(byte[] bytes,Key aesKey) throws Exception{
  			byte[] cipherText=null;
			plainText = bytes;
			Cipher secCipher = Cipher.getInstance("AES");
			// setup IV key with random data and encrypt the file using AES key.
			secCipher.init(Cipher.ENCRYPT_MODE, aesKey);
			iv = secCipher.getIV();
			cipherText = secCipher.doFinal(plainText);
			return cipherText;
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

}
