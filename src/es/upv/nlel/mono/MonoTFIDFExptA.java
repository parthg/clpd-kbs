package es.upv.nlel.mono;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.terrier.matching.ResultSet;

import antlr.RecognitionException;
import antlr.TokenStreamException;


import es.upv.nlel.preprocess.Preprocess;
import es.upv.nlel.wrapper.TerrierWrapper;
import gnu.trove.map.hash.TIntDoubleHashMap;

public class MonoTFIDFExptA {
	TerrierWrapper terrier;
	static double alpha = 0.7;
	
	FileOutputStream fos ;
	PrintStream p ;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, RecognitionException, TokenStreamException {
		MonoTFIDFExptA mono = new MonoTFIDFExptA();
		
		String lang = "en";
		String prefix = "en";
		String soDir = "/home/parth/workspace/data/CLCorpus/pan-cl-cases/es/";
		String suDir = "/home/parth/workspace/data/CLCorpus/pan-cl-cases/en/";
		
		mono.terrier = new TerrierWrapper("/home/parth/workspace/terrier-3.5/");
		
		mono.terrier.setIndex("/home/parth/workspace/terrier-3.5/var/index/pan11-expta/", prefix);
		
		if(!new File("/home/parth/workspace/terrier-3.5/var/index/pan11-expta/"+prefix+".docid.map").exists()) {
			System.out.print("Indexing...");
			mono.terrier.prepareIndex(soDir, "txt", lang, true, true);
			System.out.println("Done!");
		}
		
		mono.terrier.loadIndex("/home/parth/workspace/terrier-3.5/var/index/pan11-expta/", prefix, lang);
		
		System.out.println(mono.terrier.getDimension());

		

		Preprocess pre = new Preprocess(lang);
		
		// Load DONE queries
		
		mono.fos = new FileOutputStream("output/mono_exptA.txt");
		mono.p = new PrintStream(mono.fos);
		
		File doneDir = new File("/home/parth/workspace/clpd-kbs/output/es_detail_mono/");
		File[] doneFiles = doneDir.listFiles();
		
		List<String> done = new ArrayList<String>();
		
		for(int i=0; i<doneFiles.length; i++)
			done.add(doneFiles[i].getName());
		
		File[] suFiles = new File(suDir).listFiles();
		for(File su: suFiles) {
			if(!done.contains(su.getName()+".out")) {
				System.out.println(su.getName() + "\t" + "Processing...!");
				
				BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(su),"UTF-8"));
		        
	    		int len_file_su = (int)su.length();
//	        	System.out.println("Length of the File: "+ len_file_so);	
	    		char buf2[] = new char[len_file_su];
	    		br1.read(buf2,0,len_file_su);
	        
	    		// Here we read the whole file in a StringBuffer.
	    		StringBuffer susp = new StringBuffer();
	    		for (int j = 0; j < len_file_su; j++){
	    			if(buf2[j]=='\n')
	    				susp.append((char)' ');
	    			else
	    				susp.append((char)buf2[j]);
	    		}
	        	        
	    		br1.close();
	    		
	    		String query = mono.terrier.tokenizeTerrier(susp.toString());
			    		
	    		mono.calculateSimilarity(su.getName(),query);
			}// if closed
			else
				System.out.println(su + "\t" + "is already Done!");

		}

	}
	public void calculateSimilarity(String suName, String text) throws RecognitionException, TokenStreamException, IOException {
		String query = this.terrier.tokenizeTerrier(text);
		
		ResultSet rs = this.terrier.getResultSet(query, "TF_IDF", false, 0);
		Map<Integer, Double> scoreMap = new HashMap<Integer, Double>();
		
		int[] docid = rs.getDocids();
		double[] scores = rs.getScores();
		for(int i=0; i<docid.length; i++)
			scoreMap.put(docid[i], scores[i]);
		
		
		ValueComparator bvc =  new ValueComparator(scoreMap);
		TreeMap<Integer,Double> sorted_map = new TreeMap<Integer, Double>(bvc);
        sorted_map.putAll(scoreMap);
        int N=0;
		for(int j : sorted_map.keySet()) {
			if(N<50) {
				p.println(
						suName + " " +
						this.terrier.idMap.get(j) + " " +
						scoreMap.get(j));
				
				N++;
			}
			else 
				break;
		}
	}
}