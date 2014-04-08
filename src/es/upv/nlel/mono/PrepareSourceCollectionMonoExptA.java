package es.upv.nlel.mono;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import es.upv.nlel.preprocess.Preprocess;

public class PrepareSourceCollectionMonoExptA {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		String lang = "es";
		
		Preprocess pre = new Preprocess(lang);
		File sourceDir = new File("/home/parth/workspace/data/CLCorpus/source/");
		File[] files = sourceDir.listFiles();
		
		String outDir = "/home/parth/workspace/data/CLCorpus/mono-source/";
		
		for(File f: files) {
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"));
	        
    		int len_file_so = (int)f.length();
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
    		}
        	        
    		br1.close();
    		
    		String nGrams = pre.makeNGramText(source.toString(), 4);
    		
    		FileOutputStream fos = new FileOutputStream(outDir+f.getName());
    		PrintStream p = new PrintStream(fos);
    		
    		p.print(nGrams);
    		
    		p.close();
    		fos.close();
		
		}
	}
}