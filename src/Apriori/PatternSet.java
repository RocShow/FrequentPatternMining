package Apriori;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;


public class PatternSet {
	private HashSet<Pattern> patterns;
	private static LinkedList<Record> records;
	private static final int minSup = Apriori.minSup;
	
	public PatternSet(){
		patterns = new HashSet<Pattern>();
		records = Apriori.getRecords();
	}
	
	public void put(Pattern key){
		patterns.remove(key);
		patterns.add(key);
	}
	
//	public Integer get(Pattern key){
//		return (Integer)patterns.get(key);
//	}
	
	public boolean contain(Pattern key){
		return patterns.contains(key);
	}
	
	public Set<Pattern> keySet(){
		return patterns;
	}
	
	public int size(){
		return patterns.size();
	}
	
	public Pattern[] getPatterns(){
		Pattern[] result = new Pattern[patterns.size()];
		patterns.toArray(result);
		return result;
	}
	
	public PatternSet getNextLevelPatternSet(){
		PatternSet result = new PatternSet();
		Pattern[] set = new Pattern[patterns.size()]; 
		patterns.toArray(set);
		for(int i = 0; i < set.length; i++){
			for(int j = i + 1; j < set.length; j++){
				Pattern p = Pattern.getNextLevelPattern(set[i], set[j]);
				if (p == null || !p.isValid(this)){
					continue;
				}
				if(result.contain(p)){
					continue;
				}
				Iterator<Record> it = records.iterator();
				//int count = 0;
				while (it.hasNext()){
					Record r = it.next();
					//count = count + (r.containPattern(p) ? 1 : 0);
					if(r.containPattern(p)){
						p.addSup();
					}
				}
				if (p.getSup() >= minSup){
					System.out.println("Got Pattern: " + p.toString());
					result.put(p);
				}
			}
		}
		return result;
	}
	
	public LinkedList<Pattern> toSortedList(){
		LinkedList<Pattern> list = toList();
		Collections.sort(list, new Comparator<Pattern>() {
			public int compare(Pattern o1, Pattern o2) {
				return o2.getSup() - o1.getSup();
			}
		});
		return list;
	}
	
	public LinkedList<Pattern> toList(){
		LinkedList<Pattern> list = new LinkedList<Pattern>();
		Iterator<Pattern> it = patterns.iterator();
		while(it.hasNext()){
			list.add(it.next());
		}
		return list;
	}
	
	public void addAll(PatternSet ps){
		Iterator<Pattern> it = ps.keySet().iterator();
		while(it.hasNext()){
			Pattern p = it.next();
			patterns.add(p);
		}
	}
	
	public void sort(){
		
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		Iterator<Pattern> it = patterns.iterator();
		while (it.hasNext()){
			Pattern p = it.next();
			sb.append(p.toString() + "\n");
		}
		return sb.toString();
	}
	
	public String toSortedString(){
		StringBuffer sb = new StringBuffer();
		Iterator<Pattern> it = toSortedList().iterator();
		while (it.hasNext()){
			Pattern p = it.next();
			sb.append(p.toString() + "\n");
		}
		return sb.toString();
	}
	
	public String toSortedStringBySupPurity(){
		StringBuffer sb = new StringBuffer();
		Iterator<Pattern> it = toSortedListBySupPurity().iterator();
		while (it.hasNext()){
			Pattern p = it.next();
			sb.append(p.toStringBySupPurity() + "\n");
		}
		return sb.toString();
	}

	public void writeToFile(String outputFile){
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))){
			bw.write(toSortedString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writePurityToFile(String outputFile){
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))){
			bw.write(toSortedStringBySupPurity());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public LinkedList<Pattern> toSortedListBySupPurity(){
		LinkedList<Pattern> list = toList();
		Collections.sort(list, new Comparator<Pattern>() {
			public int compare(Pattern o1, Pattern o2) {
				double diff = o2.getSupPurity() - o1.getSupPurity();
				if(diff > 0){
					return 1;
				} else if(diff == 0){
					return 0;
				} else {
					return -1;
				}
			}
		});
		return list;
	}
	
	public LinkedList<Pattern> getPurity(int topic, LinkedList<Record> t1,LinkedList<Record> t2,LinkedList<Record> t3,LinkedList<Record> t4){
		Iterator<Pattern> it = patterns.iterator();
		while(it.hasNext()){
			it.next().getPurity(topic, t1, t2, t3, t4);
		}
		return toSortedListBySupPurity();
	}
}
