package test;


public class BigIntTest {

	public static void main(String[] args){
		Integer i=Integer.MAX_VALUE-3;
		System.out.println(Integer.MAX_VALUE);
		System.out.println(Integer.MIN_VALUE);
		for(int j=0;j<10;j++){
			System.out.print(i+"byte size: "+i.byteValue()+"\t");
			i++;
		}
	}
}
