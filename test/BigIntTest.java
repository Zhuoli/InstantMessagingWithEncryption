package test;

import java.util.HashMap;
import java.util.Set;


public class BigIntTest {

	public static void main(String[] args){
		HashMap as = new HashMap(3);
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		Set<String> set = map.keySet();
		set.add("d");
		for(String s : map.keySet()){
			System.out.println(s);
		}
	}
}
