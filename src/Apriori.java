import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
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
	private String outputFile;
	
	public Apriori(int min, String fileName, String output){
		records = new LinkedList<Record>();
		recordsFile = fileName;
		dict = new String[dictSize];
		outputFile = output;
		minSup = min;
		
		readDict();
		readRecords();
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
	
	public void writeToFile(PatternSet ps){
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))){
			bw.write(ps.toSortedString());
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
	
	
	public static void main(String[] args) {
		Apriori a = new Apriori(50, "topic-0.txt","pattern-0.txt");
		PatternSet ps = a.mining();
		a.writeToFile(ps);
//		System.out.println(ps.toSortedString());
//		int i = 0;
//		PatternSet p1 = a.getL1candidates();
//		//System.out.println(p1.size());
//		System.out.println(p1.toString());
//		//System.out.println(a.getL1candidates1().size());
//		PatternSet p2 = p1.getNextLevelPatternSet();
//		System.out.println("L2:");
//		System.out.println(p2.toString());
//		PatternSet p3 = p2.getNextLevelPatternSet();
//		System.out.println("L3:");
//		System.out.println(p3.toString());
	}
}
