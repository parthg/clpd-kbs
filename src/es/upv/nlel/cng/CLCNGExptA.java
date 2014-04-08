package es.upv.nlel.cng;

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

public class CLCNGExptA {
	TerrierWrapper terrier;
	FileOutputStream fos ;
	PrintStream p ;
	static double alpha = 0.7;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, RecognitionException, TokenStreamException {
		CLCNGExptA cng = new CLCNGExptA();
		
		// The source file containing n-grams of the text generated from PrepareCollectionExptA.java
		String soDir = "output/cng-source/";
		String suDir = "path-to/clpd-data/ExptA/en/";
		cng.terrier = new TerrierWrapper("<path-to-your-terrier-3.5>");
		
		cng.terrier.setIndex("/home/parth/workspace/terrier-3.5/var/index/pan11-expta/", "es");
		
		if(!new File("<path-to-your-terrier-3.5>/var/index/pan11-expta/es.docid.map").exists()) {
			System.out.print("Indexing...");
			cng.terrier.prepareIndex(soDir, "txt", "es", false, false);
			System.out.println("Done!");
		}
		
		cng.terrier.loadIndex("<path-to-your-terrier-3.5>/var/index/pan11-expta/", "es");
		
		System.out.println(cng.terrier.getDimension());

		String lang = "es";

		Preprocess pre = new Preprocess(lang);
		
		// Output result file
		cng.fos = new FileOutputStream("output/cng_exptA.txt");
		cng.p = new PrintStream(cng.fos);
		
		// Load DONE queries
		File doneDir = new File("output/es_detail_cng/");
		doneDir.mkdirs();
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
	    		
	    		String query = pre.makeNGramText(susp.toString(), 4);
			    		
	    		cng.calculateSimilarity(su.getName(),query);
			}// if closed
			else
				System.out.println(su + "\t" + "is already Done!");

		}// for closed

		cng.p.close();
		cng.fos.close();
	}
	public void calculateSimilarity(String suName, String text) throws RecognitionException, TokenStreamException, IOException {

		String query = this.terrier.tokenizeTerrier(text);
		try {
			ResultSet rs = this.terrier.getResultSet(query, "Hiemstra_LM", "false", "en", false, false);
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
					this.p.println(
							suName + " " +
							this.terrier.idMap.get(j) + " " +
							scoreMap.get(j));
					N++;
				}
				else 
					break;
			}
		}
		catch(Exception e) {
			System.out.println("There was a problem in:\n" +
					suName + "\n");
		}
	}
}