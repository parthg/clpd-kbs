package es.upv.nlel.preprocess;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import es.nlel.text.globalization.Diacritics;
import es.nlel.text.sentence.SentenceEn;

public class Preprocess {
	public String lang = "";
	public Preprocess(String l) {
		lang = l;
	}
	public String[] segmentSent(String file) throws IOException {
		SentenceEn sen_en = new SentenceEn();
		
        String[] sentence = sen_en.getSentences(file);
        return sentence;
	}
	
	public List<String> createStepParagraphs(String path, 
			String filename, 
			int n, 
			int step, 
			String[] sents, 
			String orgFile, 
			boolean cng) throws IOException {
		int offset=0;
		int prevOffset = 0;
//		System.out.println(sents.length);
		List<String> l = new ArrayList<String>();
		for(int i=0; i<(sents.length - n);) {
			int count =0;
			String para="";
			while(count < n) {
//				System.out.println(i+count);
				para += sents[i+count];
				para +=" ";
				count++;
			}
			
			offset = orgFile.indexOf(sents[i], prevOffset);
			
//			System.out.println(offset);
			FileOutputStream fos1 = new FileOutputStream(path+filename+"_"+offset+"_"+para.length()+".txt");
        	PrintStream p1 = new PrintStream(fos1);
        	
        	l.add(path+filename+"_"+offset+"_"+para.length()+".txt");
        	// Write it in individual file with offset and length as well as
        	// original filename information.
        	if(cng)
        		p1.print(this.makeNGramText(para, 4));
        	else
        		p1.print(para);
        	
        	p1.close();
        	fos1.close();
        	
 //       	for(int j=0;j<step;j++) {
 //       		offset+=sents[i+j].length();
 //       	}
        	prevOffset = offset - 500;
        	i=i+step;
		}
		
		return l;
	}
	
	public List<String> createStepParagraphs(String path, String filename, 
			int n, 
			int step, 
			String[] sents, 
			int[] offsets, 
			int[] lengths) throws IOException {
		int offset=0;
//		int prevOffset = 0;
//		System.out.println(sents.length);
		List<String> l = new ArrayList<String>();
		for(int i=0; i<(sents.length - n);) {
			int count =0;
			int paraLength = 0;
			String para="";
			while(count < n) {
//				System.out.println(i+count);
				para += sents[i+count];
				para +=" ";
				paraLength+=lengths[i+count];
				count++;
			}
			
//			offset = orgFile.indexOf(sents[i], prevOffset);
			
//			System.out.println(offset);
			FileOutputStream fos1 = new FileOutputStream(path+filename+"_"+offsets[i]+"_"+paraLength+".txt");
        	PrintStream p1 = new PrintStream(fos1);
        	
        	l.add(path+filename+"_"+offsets[i]+"_"+paraLength+".txt");
        	// Write it in individual file with offset and length as well as
        	// original filename information.
        	p1.print(para);
        	
        	p1.close();
        	fos1.close();
        	
 //       	for(int j=0;j<step;j++) {
 //       		offset+=sents[i+j].length();
 //       	}
//        	prevOffset = offset - 500;
        	i=i+step;
		}
		
		return l;
	}
	
	public String removeDiacritics(String s) {
		return Diacritics.removeDiacritics(s);
	}
	public String makeNGramText(String s, int n) {
		String text = s.toLowerCase();
		if(!this.lang.equals("en"))
			text = this.removeDiacritics(text);
		
		text = text.replaceAll("\\s+", "8");
		
		text = text.replaceAll("[^a-zA-Z0-9]", "");
		if(!text.startsWith("8"))
			text = "8"+text;
		if(!text.endsWith("8"))
			text=text+"8";
		char[] chars = text.toCharArray();
		
		String t="";
		String token="";
		int i=0;
		while(chars.length-i>=n) {
			token="";
			for(int j=i;j<(i+n);j++)
				token+=chars[j];
			i++;
			t+=(token+" ");
		}
		return t;
	}
	public static void main(String[] args) {
		Preprocess preprocess  = new Preprocess("es");
//		String s = "My name is Parth, what's yours?";
		String s = "éste es, un ejemplo muy poco complicado con-guión t8 y... contiene sólo 3 sencillas oraciones. ¿Como ésta?";
		System.out.println(s+"\n"+preprocess.makeNGramText(s,4));
	}
}