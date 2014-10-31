package Tests;

import Apriori.Pattern;


public class TestPattern {
	public static void main(String[] args) {
		Integer[] data1 = {1,2,3};
		Pattern p1 = new Pattern(data1);
		Integer[] data2 = {2,3};
		Pattern p2 = new Pattern(data2);
		System.out.println(p2.isSubPatternOf(p1));
		System.out.println(p2.equals(p1));
	}
}
