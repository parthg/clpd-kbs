package es.upv.nlel.corpus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PrintCandidates {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		
		FileInputStream fis = new FileInputStream("objects/esExpt2.obj");
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		map = (Map<String, List<String>>) ois.readObject();
		
		ois.close();
		fis.close();
		
		//this is to write it to a file in a readable format.
		FileOutputStream fos =  new FileOutputStream("objects/es-candidates-exptB.txt");
		PrintStream p = new PrintStream(fos);
		for(String susp: map.keySet()) {
			p.print(susp + " -- [");
			for(Iterator<String> iter= map.get(susp).iterator(); iter.hasNext();) {
				String source = iter.next();
				p.print(source+ ((iter.hasNext())?",":""));
			}
			p.println("]");
		}
		p.close();
		fos.close();
				
	}
}