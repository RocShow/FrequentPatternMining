import java.util.HashSet;

public class Record {
	private HashSet<Integer> terms;

	public Record(String record) {
		terms = new HashSet<Integer>();
		String[] words = record.split(" ");
		for (String s : words) {
			terms.add(Integer.parseInt(s));
		}
	}

	public boolean containPattern(int[] pattern) {
		for (int i : pattern) {
			if (!this.terms.contains(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean containPattern(String[] pattern) {
		for (String s : pattern) {
			if (!this.terms.contains(Integer.parseInt(s))) {
				return false;
			}
		}
		return true;
	}

	public boolean containPattern(String pattern) {
		return containPattern(pattern.split("\t"));
	}
	
	public boolean containPattern(Pattern p){
		Integer[] termsInP = p.getTerms();
		if (terms.size() < termsInP.length){
			return false;
		}
		for (int i : termsInP){
			if(!terms.contains(i)){
				return false;
			}
		}
		return true;
	}
	
	public Integer[] getTerms(){
		Integer[] result = new Integer[terms.size()];
		return terms.toArray(result);
	}
}
