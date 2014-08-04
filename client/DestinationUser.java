package client;

public class DestinationUser {

	private String IP=null;
	private int port = 0;
	private byte[] ticket=null;
	private byte[] publickKey=null;
	
	// set
	protected void setIP(String ip){
		this.IP=ip;
	}
	protected void setPort(int port){
		this.port=port;
	}
	protected void setTicket(byte[] ticket){
		this.ticket=ticket;
	}
	protected void setPublicKey(byte[] pubKey){
		this.publickKey=pubKey;
	}
	
	// get 
	protected String getIP(){
		return this.IP;
	}
	protected int getPort(){
		return this.port;
	}
	protected byte[] getTicket(){
		return this.ticket;
	}
	protected byte[] getPublicKey(){
		return this.publickKey;
	}
}
