package client;

public class SecureLayer {
	static byte[] decryptWithNounce(byte[] target_public_key,byte[] message,int[] nounce){
		Decrypt decrypt = new Decrypt(target_public_key,Client.clientPrivateKey,message);
		byte[] decipher =decrypt.decrypt();
		return StringBytesSwitch.chuncateNounce(decipher,nounce);
	}
	
	static void sendEncryptWithNounce(Encrypt encrypt,byte[] message,int[] nounce, TCPConnection connection){
		byte[] bytes= StringBytesSwitch.combineBytes(StringBytesSwitch.int2byte(nounce[0]++),message);
		bytes=encrypt.getEncryptedMessage(bytes);
		connection.sendBytes(bytes);
	}
}
