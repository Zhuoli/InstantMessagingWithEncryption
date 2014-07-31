package client;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Decrypt {
	static int KEY_LEN=256;
	static boolean DEBUG = false;
	byte[] signature=null;
	byte[] cipher_text=null;
	byte[] aesKeyEncyrpted=null;

	byte[] privateKey=null,publicKey=null;

     /** Init ***/

     
     protected Decrypt(byte[] publicKey,byte[] privateKey,byte[] cipher_text){

    	 this.publicKey=publicKey;
    	 this.privateKey=privateKey;
    	 this.cipher_text=cipher_text;
    	 try{
    		 getKeyAndCipher(cipher_text);      	// get public key
    	 }catch(Exception e){
    		 System.err.println("create Decrypt object failed, cipher size: " +cipher_text.length);
    		 if(cipher_text==null){
    			 System.err.println("cipher_text is null");
    		 }
    		 System.err.println(e.getMessage());
    		 System.exit(0);
    	 }
     }
     

     protected byte[] decrypt(){
    	byte[] plaintext=null;
 		try{
			// Public, private and signature instances
	
			Cipher secCipher = Cipher.getInstance("AES");
			SecretKey aesKey=null;
			// verify and decrypt aes key
	    	//System.out.println("decrypt function");
				aesKey=getSecKey();
			// decryt cipher text
		    //	System.out.println("decrypt function");
	     	secCipher.init(Cipher.DECRYPT_MODE,aesKey);
	     	 plaintext=secCipher.doFinal(cipher_text);
	     //	System.out.println("decrypt function");
//     	   if(Client.DEBUG)
//	     	for(byte b : plaintext){
//	     		System.out.print((char)b);
//	     	}
		}catch(Exception e){
			System.out.println("decrypt error");
			System.out.println(e.getMessage());
			System.exit(0);
		}
     	return plaintext;

     }
     private SecretKey getSecKey()throws Exception{
		Signature sig = Signature.getInstance("SHA512withRSA");
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
     	Cipher publicChiper = Cipher.getInstance("RSA");
		X509EncodedKeySpec publicSpec;
		PKCS8EncodedKeySpec privateSpec;
		PrivateKey prvKey;
		PublicKey pubKey;
		SecretKey aesKey=null;
	    
		privateSpec = new PKCS8EncodedKeySpec(privateKey);
		publicSpec = new X509EncodedKeySpec(publicKey);
		prvKey = rsaKeyFactory.generatePrivate(privateSpec);
		pubKey = rsaKeyFactory.generatePublic(publicSpec);
     	// verify
     	if(!verify(sig,pubKey)){
     		System.out.println("Verify Failed");
     		System.exit(0);
     	}
     	//Decrypt
     	publicChiper.init(Cipher.UNWRAP_MODE, prvKey);
     	aesKey = (SecretKey)publicChiper.unwrap(aesKeyEncyrpted,"AES",Cipher.SECRET_KEY);
     	return aesKey;

     }
     private boolean verify(Signature sig,PublicKey pubKey){
     	if(sig==null || pubKey==null){
     		System.out.println("verify() input error.");
     		System.exit(0);
     	}

     	try{
	     	//This method puts the Signature object in the VERIFY state.
	     	sig.initVerify(pubKey);
	     	//The data is passed to the object by calling one of the update methods:
	     	sig.update(cipher_text);
	     	sig.update(this.aesKeyEncyrpted);
	     	boolean ok=sig.verify(signature);
     		return ok;
	     }catch(Exception e){
	     	System.out.println("Sig verify error: " + e.getMessage());
	     	System.exit(0);
	     }
	     return true;
     }
	 public  void getKeyAndCipher(final byte[] bytes){
		 	//System.out.println("getKeyAndCipher ");
	      this.signature=Arrays.copyOfRange(bytes,0,KEY_LEN);
			// System.out.println("getKeyAndCipher ");
	      this.aesKeyEncyrpted=Arrays.copyOfRange(bytes,KEY_LEN,2*KEY_LEN);
			// System.out.println("getKeyAndCipher ");
	      this.cipher_text=Arrays.copyOfRange(bytes,2*KEY_LEN,bytes.length);
			// System.out.println("getKeyAndCipher ");

	      if(DEBUG){
		      System.out.println("Signature Hex:");
		      for(byte b : signature){
		        System.out.print(String.format("%02X ",b));
		      }   
		      System.out.println();
		      System.out.println("aesKeyEncyrpted Hex:");
		      for(byte b : aesKeyEncyrpted){
		        System.out.print(String.format("%02X ",b));
		      }   
		      System.out.println();
		      System.out.println("Cipher_text Hex:");
		      for(byte b : cipher_text){
		        System.out.print(String.format("%02X ",b));
		      }   
		      System.out.println();
		  }

	  }
	  private static byte[] readByteFromFile(File f) throws Exception{

	    if(f.length() > Integer.MAX_VALUE)
	      System.out.println("File is too large");

	    byte[] buffer = new byte[(int)f.length()];
	    InputStream ios = new FileInputStream(f);
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
	  private static boolean isDecryptInputError(String public_key_filename, String private_key_filename,String cipher_filename){
            if(!new File(public_key_filename).isFile()){
              System.out.println("File: " + public_key_filename + " not exist or not a file");
              return true;
            }   
            if(!new File(private_key_filename).isFile()){
              System.out.println("File: " + private_key_filename + " not exist or not a file");
              return true;
            }  
            if(!new File(cipher_filename).isFile()){
              System.out.println("File: " + cipher_filename + " not exist or not a file");
              return true;
            }  

          return false;
        }  
}