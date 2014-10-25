import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

public class Pattern {
	private Integer[] terms;
	private int sup;
	private String[] dict = Apriori.dict;
	
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
		sb.append(sup + " [");
		for(int i : terms){
			if(i == terms.length - 1)
				break;
			sb.append(dict[i] + " ");
		}
		sb.append(dict[terms[terms.length - 1]] +"]");
		return sb.toString();
	}
}
