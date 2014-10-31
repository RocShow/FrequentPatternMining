package Tests;

import Apriori.Apriori;
import Apriori.PatternSet;

public class TestClosedPattern {
	public static void main(String[] args) {
		Apriori a = new Apriori(2, "TestFiles/closed.txt");
		PatternSet frequent = a.mining();
		PatternSet closed = a.miningClosedPattern(frequent);
		System.out.println(closed.toSortedString());
	}
}
