package client;

import java.math.BigInteger;
import java.util.Arrays;

public class StringBytesSwitch {
	protected static byte[] int2byte(int input){
		byte[] conv = new byte[4];
		conv[3] = (byte) (input & 0xff);
		input >>= 8;
		conv[2] = (byte) (input & 0xff);
		input >>= 8;
		conv[1] = (byte) (input & 0xff);
		input >>= 8;
		conv[0] = (byte) input;
		return conv;
	}
	
	protected static byte[] combineBytes(byte[] a,byte[] b){
		byte[] barr=new byte[a.length+b.length];
		System.arraycopy(a, 0, barr, 0, a.length);
		System.arraycopy(b, 0, barr, a.length, b.length);
		return barr;
	}
	protected static byte[] chuncateNounce(byte[] line,int[] nounce){
		byte[] nounceByte = Arrays.copyOfRange(line, 0, 4);
		if(nounce[0]++!=(new BigInteger(nounceByte)).intValue()){
			System.out.println("Nounce not equal: " + nounce[0] +" != "+(new BigInteger(nounceByte)).intValue());
			return null;
		}
		return Arrays.copyOfRange(line, 4, line.length);
	}
}
