package es.upv.nlel.eval;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class AnalyseRecall {
	public static void main(String[] args) throws IOException {
		
		// Give the generated output file from MonoTFIDFExptA or CLCNGExptA
		String inFile = "output/mono_exptA.txt";
		BufferedReader br = new BufferedReader(new FileReader(inFile));
		
		TIntObjectHashMap<TIntIntHashMap> map = new TIntObjectHashMap<TIntIntHashMap>();
		String line = "";

		int[] bins = {1,2,3,4,5,10,20,50};
		int[] recall = new int[bins.length];
		
		List<Integer> longCases = new ArrayList<Integer>();
		List<Integer> mediumCases = new ArrayList<Integer>();
		List<Integer> shortCases = new ArrayList<Integer>();
		
		List<Integer> automatic = new ArrayList<Integer>();
		List<Integer> manual = new ArrayList<Integer>();
		
		int[] recallLong = new int[bins.length];
		int[] recallType = new int[2];
		
		int[] recallMedium = new int[bins.length];
		int[] recallShort = new int[bins.length];
		
		int[] recallAuto = new int[bins.length];
		int[] recallManual = new int[bins.length];
		
		int count =0;
		while((line = br.readLine())!=null) {
			String[] cols = line.split(" ");
			
			int doc  = getId(cols[0].trim());
			int retrieved = getId(cols[1].trim());
			
			
			
			if(!map.contains(doc)) {
				int len = getLength(cols[0].trim());
				
				if(len>=5000)
					longCases.add(doc);
				else if(len>=750 && len<5000)
					mediumCases.add(doc);
				else 
					shortCases.add(doc);
				
				boolean isManual = getManual(cols[0].trim());
				if(isManual)
					manual.add(doc);
				else
					automatic.add(doc);
				
				count = 0;
				TIntIntHashMap inner = new TIntIntHashMap();
				inner.put(retrieved, (count+1));
				map.put(doc, inner);
				count++;
			}
			else {
				map.get(doc).put(retrieved, (count+1));
				count++;
			}
		}
		
		
		for(int i: map.keys()) {
			int pos = -1;
			for(int j: map.get(i).keys()) {
				if(i==j) {
					pos = map.get(i).get(j);
					for(int b=0; b<bins.length; b++) {
						if(pos<=bins[b]) {
							recall[b]++;
							
							if(longCases.contains(i))
								recallLong[b]++;
							else if(mediumCases.contains(i))
								recallMedium[b]++;
							else
								recallShort[b]++;
							
							if(manual.contains(i))
								recallManual[b]++; // Manual
							else
								recallAuto[b]++; // Automatic
						
						
						}
					}
					
					
					break;
				}
			}
		}

		
		for(int i=0;i<bins.length; i++)
			System.out.println(((double)recall[i]/(double)map.size()));
		
		System.out.println("\n");
		
		for(int i=0; i<recallLong.length; i++)
			System.out.println(((double)recallLong[i]/(double)longCases.size()));
		
		System.out.println("\n");
		
		for(int i=0; i<recallMedium.length; i++)
			System.out.println(((double)recallMedium[i]/(double)mediumCases.size()));
		
		System.out.println("\n");
		
		for(int i=0; i<recallShort.length; i++)
			System.out.println(((double)recallShort[i]/(double)shortCases.size()));
		
		System.out.println("\n");
		
		for(int i=0; i<recallAuto.length; i++)
			System.out.println(((double)recallAuto[i]/(double)automatic.size()));
		
		System.out.println("\n");
		
		for(int i=0; i<recallManual.length; i++)
			System.out.println(((double)recallManual[i]/(double)manual.size()));
		
	}
	
	public static int getId(String s) {
		int i=-1;
		i = Integer.parseInt(s.substring((s.lastIndexOf("_")+1), s.lastIndexOf(".txt")));
		return i;
	}
	
	public static int getLength(String s) {
		int len = -1;
		String[] cols = s.split("_");
		len = Integer.parseInt(cols[2].trim());
		return len;
	}
	
	public static boolean getManual(String s) {
		boolean manual = false;
		String[] cols = s.split("_");
		manual = Boolean.parseBoolean(cols[3].trim());
		return manual;
	}
}