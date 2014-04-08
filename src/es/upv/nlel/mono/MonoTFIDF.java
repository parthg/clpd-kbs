package es.upv.nlel.mono;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.terrier.matching.ResultSet;

import antlr.RecognitionException;
import antlr.TokenStreamException;


import es.upv.nlel.preprocess.Preprocess;
import es.upv.nlel.wrapper.TerrierWrapper;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntIntHashMap;

class ValueComparator implements Comparator {

	  Map base;
	  public ValueComparator(Map base) {
	      this.base = base;
	  }

	  public int compare(Object a, Object b) {

	    if((Double)base.get(a) < (Double)base.get(b)) {
	      return 1;
	    } else if((Double)base.get(a) == (Double)base.get(b)) {
	      return 0;
	    } else {
	      return -1;
	    }
	  }
	}

public class MonoTFIDF {
	TerrierWrapper terrier;
	static double alpha = 0.7;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, RecognitionException, TokenStreamException {
		MonoTFIDF mono = new MonoTFIDF();
		
		String prefix = "esmono";
		mono.terrier = new TerrierWrapper("/home/parth/workspace/terrier-3.5/");
		
		// This is very important!!
		mono.terrier.setIndex("/home/parth/workspace/terrier-3.5/var/index/pan11/", "esmono");
		
		if(!new File("/home/parth/workspace/terrier-3.5/var/index/pan11/esmono.docid.map").exists())
			mono.terrier.prepareIndex("/home/parth/workspace/data/clpd-kbs/soParaMono/", "txt", "en", true, true);
		
		mono.terrier.loadIndex("/home/parth/workspace/terrier-3.5/var/index/pan11/", "esmono");
		
		System.out.println(mono.terrier.getDimension());
		
//		THashMap<String,ArrayList<String>> map = new THashMap<String, ArrayList<String>>();
		
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		
		if(!new File("objects/new-expt3-candidates-es.obj").exists()) {
			BufferedReader br = new BufferedReader(new FileReader("etc/es_suso.txt"));
			String line ="";
			while((line=br.readLine())!=null) {
				if(line.length()>0) {
					String[] cols = line.split("\t");
					if(!map.containsKey(cols[0].trim())) {
						ArrayList<String> list = new ArrayList<String>();
						list.add(cols[1].trim());
						map.put(cols[0].trim(), list);
					}
					else {
						if(!map.get(cols[0].trim()).contains(cols[1].trim()))
							map.get(cols[0].trim()).add(cols[1].trim());
					}
				}	
			}
			
			FileOutputStream fos = new FileOutputStream("objects/esExpt2.obj");
			ObjectOutputStream oos =  new ObjectOutputStream(fos);
			
			oos.writeObject(map);
			
			oos.close();
			fos.close();
		}
		else {
			System.out.println("Reading objects/new-expt3-candidates-es.obj");
			FileInputStream fis = new FileInputStream("objects/new-expt3-candidates-es.obj");
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			map = (Map<String, List<String>>) ois.readObject();
			
			ois.close();
			fis.close();
		}
		String lang = "es";
		String outPath = "/home/parth/workspace/data/clpd-kbs/suPara/";
		String paraList = "/home/parth/workspace/data/clpd-kbs/paraListMono/";
		Preprocess pre = new Preprocess(lang);
		
		// Load DONE queries
		
		File doneDir = new File("/home/parth/workspace/clpd-kbs/output/es_detail/");
		File[] doneFiles = doneDir.listFiles();
		
		List<String> done = new ArrayList<String>();
		
		for(int i=0; i<doneFiles.length; i++)
			done.add(doneFiles[i].getName());
		for(String su: map.keySet()) {
			if(!done.contains(su+".out")) {
				System.out.println(su + "\t" + "Processing...!");
				File suFile = new File("/home/parth/workspace/data/CLCorpus/susp/"+su);
				
				BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(suFile),"UTF-8"));
		        
	    		int len_file_su = (int)suFile.length();
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
			
	    		String[] susp_sents = pre.segmentSent(susp.toString().trim());
			
	    		List<String> tempSuList = pre.createStepParagraphs(outPath,suFile.getName(),5,2, susp_sents, susp.toString().trim(), false);
	    		
	    		List<String> tempSoList = new ArrayList<String>();
	    		
	    		for(String t: map.get(su)) {
	    			BufferedReader br = new BufferedReader(new FileReader(paraList+t));
	    			String line = "";
	    			
	    			while((line = br.readLine())!=null) {
	    				tempSoList.add(line.trim());
	    			}
	    		}
	    		mono.calculateSimilarity(su,tempSuList, tempSoList);
			}// if closed
			else
				System.out.println(su + "\t" + "is already Done!");

		}

	}
	
	
	public void calculateSimilarity(String suName, List<String> suList, List<String> soList) throws IOException, RecognitionException, TokenStreamException {
		FileOutputStream fos = new FileOutputStream("output/es_detail/"+suName+".out");
		PrintStream p = new PrintStream(fos);
		for(String su: suList) {
			String query = this.terrier.getQuery(new File(su));
			
			try {
				ResultSet rs = this.terrier.getResultSet(query, "TF_IDF", "false", "en", true, true);
				Map<Integer, Double> scoreMap = new HashMap<Integer, Double>();
				
				/*String[] q = query.split(" ");
				Map<String, Integer> qTF = new HashMap<String, Integer>();
				for(String qTerm: q) {
					if(!qTF.containsKey(qTerm))
						qTF.put(qTerm, 1);
					else
						qTF.put(qTerm, (qTF.get(qTerm)+1));
				}*/
				int[] docid = rs.getDocids();
				double[] scores = rs.getScores();
				TIntDoubleHashMap rsMap = new TIntDoubleHashMap();
				for(int i=0; i<docid.length; i++)
					rsMap.put(docid[i], scores[i]);
				for(String so: soList) {
					int soId = this.terrier.idReverseMap.get(so.substring(so.lastIndexOf("/")+1));
					
					/*double score = 0.0;
					int dl = this.terrier.getDocLength(soId);
					TIntIntHashMap docTFMap = this.terrier.docTF(soId);
					for(String t: qTF.keySet()) {
						int tid = this.terrier.getTermId(t);
						
						if(tid!=-1) {
							if(docTFMap.contains(tid)) {
								double x  = 1+(alpha* qTF.get(t)*(docTFMap.get(tid))) + (1-alpha) * (this.terrier.getNormTF(t));
								score+= Math.log(x);
							}
						}
					}
					scoreMap.put(soId, (score/q.length));*/
					if(rsMap.contains(soId))
						scoreMap.put(soId, rsMap.get(soId));
				}
				
				
				ValueComparator bvc =  new ValueComparator(scoreMap);
				TreeMap<Integer,Double> sorted_map = new TreeMap<Integer, Double>(bvc);
		        sorted_map.putAll(scoreMap);
		        int N=0;
				for(int j : sorted_map.keySet()) {
					if(N<50) {
//						System.out.println(
//								map.get(i).get(j) + " " +
//										files_index_src.get(j) + " " +
//										files_index_susp.get(i));
						p.println(
								scoreMap.get(j) + " " +
								this.terrier.idMap.get(j) + " " +
								su.substring(su.lastIndexOf("/")+1));
						
						N++;
					}
					else 
						break;
				}
			}
			catch(Exception e) {
				System.out.println("There was a problem in:\n" +
						su + "\n");
			}
		}
		p.close();
		fos.close();
	}
}