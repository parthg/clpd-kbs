package es.upv.nlel.mono;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import es.upv.nlel.preprocess.Preprocess;

public class PrepareSourceCollectionMono {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String lang = "es";
		
		Preprocess pre = new Preprocess(lang);
		File sourceDir = new File("/home/parth/workspace/data/CLCorpus/source-trans/");
		File[] files = sourceDir.listFiles(new FilenameFilter() {
		    public boolean accept(File sourceDir, String name) {
		        return name.toLowerCase().endsWith("es.txt.en");
		    }
		});
		
		String outPath = "/home/parth/workspace/data/clpd-kbs/soParaMono/";
		String paraListDir = "/home/parth/workspace/data/clpd-kbs/paraListMono/";
		
		for(File f: files) {
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
			
			String line = "";
			
			int n = count(f);
			int[] lengths = new int[n];
			int[] offsets = new int[n];
			String[] sents = new String[n];
			
			int lineNo = 0;
			while((line=br1.readLine())!=null) {
				String[] cols = line.split("\t");
				if(cols.length==4){
					offsets[lineNo] = Integer.parseInt(cols[1].trim());
					lengths[lineNo] = Integer.parseInt(cols[2].trim());
					sents[lineNo] = cols[3].trim();
					lineNo++;
				}	
			}
	        
    		/*int len_file_so = (int)f.length();
//        	System.out.println("Length of the File: "+ len_file_so);	
    		char buf2[] = new char[len_file_so];
    		br1.read(buf2,0,len_file_so);
        
    		// Here we read the whole file in a StringBuffer.
    		StringBuffer source = new StringBuffer();
    		for (int j = 0; j < len_file_so; j++){
    			if(buf2[j]=='\n')
    				source.append((char)' ');
    			else
    				source.append((char)buf2[j]);
    		}*/
        	        
    		br1.close();
		
//    		String[] source_sents = pre.segmentSent(source.toString().trim());
		
    		List<String> tempSoList = pre.createStepParagraphs(outPath,f.getName().replace("es.txt.en", "txt"), 5, 2, sents, offsets, lengths);
    		
    		FileOutputStream fos = new FileOutputStream(paraListDir+f.getName().replace("es.txt.en", "txt"));
    		PrintStream p = new PrintStream(fos);
    		
    		for(String s: tempSoList) {
    			p.println(s);
    		}
    		
    		p.close();
    		fos.close();
		
		}	
	}
	public static int count(File filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
}