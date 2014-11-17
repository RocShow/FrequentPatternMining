package Apriori;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Pattern {
	private Integer[] terms;
	private int sup;
	private String[] dict = Apriori.dict;
	private double purity;
	private static int[][] dTable = {
		{10047,17326,17988,17999,17820},
		{17326,9674,17446,17902,17486},
		{17988,17446,9959,18077,17492},
		{17999,17902,18077,10161,17912},
		{17820,17486,17492,17912,9845}
	};
	private static DecimalFormat df = new DecimalFormat("0.0000");
	
	public Pattern(int term, int support) {
		terms = new Integer[1];
		terms[0] = term;
		sup = support;
	}
	
	public Pattern(int term){
		this(term,0);
	}

	public Pattern(Integer[] _terms, int support) {
		terms = _terms;
		Arrays.sort(terms);
		sup = support;
	}
	
	public Pattern(Integer[] _terms){
		this(_terms,0);
	}

	public int size(){
		return terms.length;
	}
	
	public void addSup(){
		sup++;
	}
	
	public void setSup(int _sup){
		sup = _sup;
	}
	
	public int getSup(){
		return sup;
	}
	
	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		for (int i : terms) {
			sb.append(i);
		}
		return sb.toString().hashCode();
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Pattern))
			return false;
		if (obj == this)
			return true;

		Pattern p = (Pattern) obj;
		if (p.getTerms().length != terms.length) {
			return false;
		}
		Integer[] anotherTerms = p.getTerms();
		for (int i = 0; i < terms.length; i++) {
			if (!terms[i].equals(anotherTerms[i])) {
				return false;
			}
		}
		return true;
	}

	public Integer[] getTerms() {
		return terms;
	}

	public static Pattern getNextLevelPattern(Pattern p1, Pattern p2) {
		Integer[] terms1 = p1.getTerms();
		Integer[] terms2 = p2.getTerms();
		if (terms1.length != terms2.length){
			return null;
		}
		
		HashSet<Integer> set = new HashSet<Integer>();
		for (int i : terms1) {
			set.add(i);
		}
		for (int i : terms2) {
			set.add(i);
		}
		if (set.size() != terms1.length + 1) {
			return null;
		}
		Integer[] newTerms = new Integer[set.size()];
		set.toArray(newTerms);
		return new Pattern(newTerms);
	}
	
	private PatternSet getSubPatterns(){
		LinkedList<Integer> backup = new LinkedList<Integer>();
		Collections.addAll(backup, terms);
		PatternSet result = new PatternSet();
		for(int i = 0; i < backup.size(); i++){
			LinkedList<Integer> clone = (LinkedList<Integer>) backup.clone();
			clone.remove(i);
			Integer[] smaller = new Integer[terms.length - 1];
			clone.toArray(smaller);
			result.put(new Pattern(smaller));
		}
		return result;
	}
	
	
	
	public boolean isValid(PatternSet ps){
		Pattern[] sub = getSubPatterns().getPatterns();
		for(Pattern p : sub){
			if (!ps.contain(p)){
				return false;
			}
		}
		return true;
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(sup + " ");
		for(int i = 0; i < terms.length - 1; i++){
			sb.append(dict[terms[i]] + " ");
		}
		sb.append(dict[terms[terms.length - 1]]);
		return sb.toString();
	}
	
	public HashSet<Integer> getTermSet(){
		HashSet<Integer> set = new HashSet<Integer>();
		for(int i = 0; i < terms.length; i++){
			set.add(terms[i]);
		}
		return set;
	}
	
	public boolean isSubPatternOf(Pattern p){
		HashSet<Integer> set = p.getTermSet();
		for(int i = 0; i < terms.length; i++){
			if(!set.contains(terms[i])){
				return false;
			}
		}
		return true;
	}
	
	public int frequencyInTopic(LinkedList<Record> records){
		Iterator<Record> it = records.iterator();
		int count = 0;
		while(it.hasNext()){
			Record r = it.next();
			if(r.containPattern(this)){
				count++;
			}
		}
		return count;
	}
	
	public void getPurity(int topic, LinkedList<Record> t1,LinkedList<Record> t2,LinkedList<Record> t3,LinkedList<Record> t4){
		int[] topics = new int[4];
		for(int i = 0, j = 0; i < 5; i++){
			if(i != topic){
				topics[j++] = i;
			}
		}
		int ftp = sup;
		LinkedList<Double> logFtps = new LinkedList<Double>();
		logFtps.add((double)(ftp + frequencyInTopic(t1))/dTable[topic][topics[0]]);
		logFtps.add((double)(ftp + frequencyInTopic(t2))/dTable[topic][topics[1]]);
		logFtps.add((double)(ftp + frequencyInTopic(t3))/dTable[topic][topics[2]]);
		logFtps.add((double)(ftp + frequencyInTopic(t4))/dTable[topic][topics[3]]);
		Collections.sort(logFtps);
		Double maxLogFtp = logFtps.get(logFtps.size() - 1);
		purity = Math.log((double)ftp/dTable[topic][topic]/maxLogFtp);
	}
	
	public double getPurity(){
		return purity;
	}
	
	public double getSupPurity(){
		return (double) sup * purity;
	}
	
	public String toStringBySupPurity(){
		StringBuffer sb = new StringBuffer();
		sb.append(df.format(getSupPurity()) + " ");
		for(int i = 0; i < terms.length - 1; i++){
			sb.append(dict[terms[i]] + " ");
		}
		sb.append(dict[terms[terms.length - 1]]);
		return sb.toString();
	}
}
