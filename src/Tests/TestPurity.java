package Tests;

import java.util.LinkedList;

import Apriori.Apriori;
import Apriori.Pattern;
import Apriori.PatternSet;

public class TestPurity {
	public static void main(String[] args) {
		Apriori a = new Apriori(50, "topic-1.txt");
		PatternSet ps = a.mining();
		LinkedList<Pattern> list = ps.toList();
		Pattern p = list.get(1);
		//p.frequencyInTopic(a.getRecords());
		System.out.println(p.frequencyInTopic(a.getRecords()));
		System.out.println(p.getSup());
	}
}