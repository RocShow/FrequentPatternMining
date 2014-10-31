package Apriori;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Apriori {
	
	interface LineHandler{
		void process(String line);
	}
	
	private static final int dictSize = 12220;
	public static int minSup;
	
	public static String[] dict;
	private static LinkedList<Record> records;
	private String dictFile = "vocab.txt";
	private String recordsFile;
	
	public Apriori(int min, String fileName){
		records = new LinkedList<Record>();
		recordsFile = fileName;
		dict = new String[dictSize];
		minSup = min;
		
		readDict();
		readRecords();
	}
	
	public Apriori(float min, String fileName){
		records = new LinkedList<Record>();
		recordsFile = fileName;
		dict = new String[dictSize];

		readDict();
		readRecords();
		
		minSup = (int)(records.size() * min);
		
	}
	
	public static LinkedList<Record> getRecords(){
		return records;
	}
	
	private void readDict(){
		int lineCount = 0;
		try(BufferedReader br = new BufferedReader(new FileReader(dictFile))) {
			String line = "";
			while ((line = br.readLine()) != null){
				dict[lineCount++] = line.split("\t")[1];
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readRecords(){
		try(BufferedReader br = new BufferedReader(new FileReader(recordsFile))) {
			String line = "";
			while ((line = br.readLine()) != null){
				records.push(new Record(line));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public PatternSet getL1candidates(){
		HashMap<Pattern, Integer> temp = new HashMap<Pattern, Integer>();
		PatternSet result = new PatternSet();
		Iterator<Record> it = records.iterator();
		Integer[] words;
		while(it.hasNext()){
			words = it.next().getTerms();
			for(int w : words){
				Pattern p = new Pattern(w);
				int count = temp.get(p) == null ? 1 : temp.get(p) + 1;
				p.setSup(count);
				temp.put(p, count);
				if (count >= minSup){
					result.put(p);
				}
			}
		}
		return result;
	}
	
//	public HashMap<Integer, Integer> getL1candidates1(){
//		HashMap<Integer,Integer> temp = new HashMap<Integer,Integer>();
//		HashMap<Integer,Integer> result = new HashMap<Integer,Integer>();
//		Iterator<Record> it = records.iterator();
//		Integer[] words;
//		while(it.hasNext()){
//			words = it.next().getTerms();
//			for(int w : words){
//				Integer p = w;
//				int count = temp.get(p) == null ? 1 : temp.get(p) + 1;
//				temp.put(p, count);
//				if (count > minSup){
//					result.put(p, count);
//				}
//			}
//		}
//		return result;
//	}
	
	public PatternSet mining(){
		PatternSet result = new PatternSet();
		int total = 0;
		//System.out.println("Mining L" + i++);
		PatternSet patterns = getL1candidates().getNextLevelPatternSet();
		while(patterns.size() > 1){
			//System.out.println("Mining Finish, got " + patterns.size() + " patterns.");
			total += patterns.size();
			result.addAll(patterns);
			patterns = patterns.getNextLevelPatternSet();
			//System.out.println();
		}
		if(patterns.size() == 1){
			total += patterns.size();
			result.addAll(patterns);
		}
		System.out.println("Total: " + total + " patterns.");
		return result;
	}
	
	public PatternSet miningClosedPattern(PatternSet ps){
		PatternSet result = new PatternSet();
		LinkedList<Pattern> list = new LinkedList<Pattern>();
		HashSet<Pattern> helper = new HashSet<Pattern>();
		Iterator<Pattern> it = ps.keySet().iterator();
		while(it.hasNext()){
			Pattern p = it.next();
			boolean toAdd = true;
			for(int i = 0; i < list.size();){
				Pattern pInResults = list.get(i);
				if(p.getSup() == pInResults.getSup() && p.isSubPatternOf(pInResults)){
					toAdd = false;
					break;
				}
				if(pInResults.getSup() == p.getSup() && pInResults.isSubPatternOf(p)){
					if(helper.contains(p)){
						System.out.println("Pruned:" + pInResults.toString());
						list.remove(i);
						helper.remove(pInResults);
					} else {
						System.out.println("Pruned:" + pInResults.toString());
						list.set(i, p);
						helper.remove(pInResults);
						helper.add(p);
						toAdd = false;
						i++;
					}
				} else {
					i++;
				}
			}
			if(toAdd){
				helper.add(p);
				list.add(p);
			}
			
		}
		it = list.iterator();
		while(it.hasNext()){
			result.put(it.next());
		}
		return result;
	}
	
	public PatternSet miningMaxPattern(PatternSet ps){
		PatternSet result = new PatternSet();
		LinkedList<Pattern> list = new LinkedList<Pattern>();
		HashSet<Pattern> helper = new HashSet<Pattern>();
		Iterator<Pattern> it = ps.keySet().iterator();
		while(it.hasNext()){
			Pattern p = it.next();
			boolean toAdd = true;
			for(int i = 0; i < list.size();){
				Pattern pInResults = list.get(i);
				if(p.isSubPatternOf(pInResults)){
					System.out.println("Pruned:" + p.toString());
					toAdd = false;
					break;
				}
				if(pInResults.isSubPatternOf(p)){
					if(helper.contains(p)){
						System.out.println("Pruned:" + pInResults.toString());
						list.remove(i);
						helper.remove(pInResults);
					} else {
						System.out.println("Pruned:" + pInResults.toString());
						list.set(i, p);
						helper.remove(pInResults);
						helper.add(p);
						toAdd = false;
						i++;
					}
				} else {
					i++;
				}
			}
			if(toAdd){
				helper.add(p);
				list.add(p);
			}
			
		}
		it = list.iterator();
		while(it.hasNext()){
			result.put(it.next());
		}
		return result;
	}
	
	public static void main(String[] args) {		
		for(int i = 0; i < 5; i++){
			Apriori a = new Apriori(0.005f, "topic-" + i + ".txt");
			PatternSet ps = a.mining();
			PatternSet cps = a.miningClosedPattern(ps);
			PatternSet mps = a.miningMaxPattern(ps);
			ps.writeToFile("pattern-" + i + ".txt");
			cps.writeToFile("closed/closed-" + i + ".txt");
			mps.writeToFile("max/max-" + i + ".txt");
			System.out.println("Mining topic-" + i + " Finished.");
		}
		System.out.println("\n\n***********Finished***********");
//		
//		Apriori a2 = new Apriori(0.005f, "topic-2.txt","pattern-2.txt");
//		PatternSet ps2 = a2.mining();
//		a2.writeToFile(ps2);
//		
//		Apriori a3 = new Apriori(0.005f, "topic-3.txt","pattern-3.txt");
//		PatternSet ps3 = a3.mining();
//		a3.writeToFile(ps3);
//		
//		Apriori a4 = new Apriori(0.005f, "topic-4.txt","pattern-4.txt");
//		PatternSet ps4 = a4.mining();
//		a4.writeToFile(ps4);
		
	}
}
