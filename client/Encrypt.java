package client;


import javax.crypto.*;

import java.security.*;
import java.security.spec.*;
import java.io.*;

public class Encrypt {
    // byte representation of parameters and IV
    byte[] iv, cipherText, publicKey, plainText, privateKey, signature, aesKeyEncyrpted;

	public Encrypt(byte[] publicKey,byte[] privateKey ){
		this.publicKey=publicKey;
		this.privateKey=privateKey;
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
  		private byte[] aesEncrypt(byte[] plaintext,Key aesKey) throws Exception{
  			byte[] cipherText=null;
			plainText = plaintext;
			Cipher secCipher = Cipher.getInstance("AES");
			// setup IV key with random data and encrypt the file using AES key.
			secCipher.init(Cipher.ENCRYPT_MODE, aesKey);
			iv = secCipher.getIV();
			cipherText = secCipher.doFinal(plainText);
			return cipherText;
  		}





}
