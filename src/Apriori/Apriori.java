package Apriori;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
		
	public PatternSet mining(){
		PatternSet result = new PatternSet();
		int total = 0;
		//System.out.println("Mining L" + i++);
		//PatternSet patterns = getL1candidates().getNextLevelPatternSet();
		PatternSet patterns = getL1candidates();
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
		ArrayList<LinkedList<Record>> topics = new ArrayList<LinkedList<Record>>();
		ArrayList<PatternSet> pss = new ArrayList<PatternSet>();
		for(int i = 0; i < 5; i++){
			Apriori a = new Apriori(50, "topic-" + i + ".txt");
			topics.add(a.getRecords());
			PatternSet ps = a.mining();
			pss.add(ps);
			PatternSet cps = a.miningClosedPattern(ps);
			PatternSet mps = a.miningMaxPattern(ps);
			ps.writeToFile("pattern-" + i + ".txt");
			cps.writeToFile("closed/closed-" + i + ".txt");
			mps.writeToFile("max/max-" + i + ".txt");
			System.out.println("Mining topic-" + i + " Finished.");
		}
		System.out.println("\n\n***********Frequent Pattern Mining Finished***********\n\n");
		
		//mining purity
		LinkedList<Record> t0 = topics.get(0);
		LinkedList<Record> t1 = topics.get(1);
		LinkedList<Record> t2 = topics.get(2);
		LinkedList<Record> t3 = topics.get(3);
		LinkedList<Record> t4 = topics.get(4);
		PatternSet pss0 = pss.get(0);
		PatternSet pss1 = pss.get(1);
		PatternSet pss2 = pss.get(2);
		PatternSet pss3 = pss.get(3);
		PatternSet pss4 = pss.get(4);
		System.out.println("Computing purity for topic 0...");
		pss0.getPurity(0, t1, t2, t3, t4);
		System.out.println("Computing purity for topic 1...");
		pss1.getPurity(1, t0, t2, t3, t4);
		System.out.println("Computing purity for topic 2...");
		pss2.getPurity(2, t0, t1, t3, t4);
		System.out.println("Computing purity for topic 3...");
		pss3.getPurity(3, t0, t1, t2, t4);
		System.out.println("Computing purity for topic 4...");
		pss4.getPurity(4, t0, t1, t2, t3);
		pss0.writePurityToFile("purity/purity-0.txt");
		pss1.writePurityToFile("purity/purity-1.txt");
		pss2.writePurityToFile("purity/purity-2.txt");
		pss3.writePurityToFile("purity/purity-3.txt");
		pss4.writePurityToFile("purity/purity-4.txt");
		System.out.println("\n\n***********Computing Finished***********");
	}
}
