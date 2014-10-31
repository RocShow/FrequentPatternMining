package Tests;

import Apriori.Apriori;
import Apriori.PatternSet;

public class TestMaxPattern {
	public static void main(String[] args) {
		Apriori a = new Apriori(2, "TestFiles/max.txt");
		PatternSet frequent = a.mining();
		PatternSet closed = a.miningMaxPattern(frequent);
		System.out.println(closed.toSortedString());
	}
}
