package es.upv.nlel.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DetailedAnalysisAlgorithm {
	
	
	
	public static void annotate(String susp, int merge_su_offset, int merge_su_length, 
			String mergeSource, int merge_so_offset, int merge_so_length, PrintStream p1) {

		p1.append("<feature name=\"detected-plagiarism\" this_offset="+"\""+merge_su_offset+"\""+" "
			+"this_length="+"\""+merge_su_length+"\""+" "+"source_reference="+"\""+ mergeSource +
			"\""+" "+"source_offset="+"\""+merge_so_offset+"\""+" "+"source_length="+
			"\""+merge_so_length+"\"/>");
        p1.println();

	}
	
	public static void annotatePost(String susp, int merge_su_offset, int merge_su_length, 
			String mergeSource, int merge_so_offset, int merge_so_length, PrintStream p1, String suspFile, 
			String sourceFile) {
		

		
		p1.append("<feature name=\"detected-plagiarism\" this_offset="+"\""+merge_su_offset+"\""+" "
			+"this_length="+"\""+merge_su_length+"\""+" "+"source_reference="+"\""+ mergeSource +
			"\""+" "+"source_offset="+"\""+merge_so_offset+"\""+" "+"source_length="+
			"\""+merge_so_length+"\"/>");
        p1.println();

	}
	
	public static void main(String[] args) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
		
		String suffix = "cng_exptC";
		String lang = "es";
		File dir = new File("/home/parth/workspace/clpd-kbs/output/"+lang+"_detail"+"_"+suffix);
		File[] files = dir.listFiles();
		
		for(int f = 0 ; f<files.length; f++) {

			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(files[f]),"UTF-8"));
	        
			String name = files[f].getName().substring(0, files[f].getName().indexOf(".txt")) + ".xml1";
			FileOutputStream fos = new FileOutputStream("/home/parth/workspace/clpd-kbs/output/detection"+"_"+suffix+"/"+name);
			PrintStream p = new PrintStream(fos);
			
			System.out.println("Processing: " + name);
			
			Map<String, String> fileContents = new HashMap<String, String>();
 
			int topN=5;
			
			int totalMerge = 0;
			int totalPermissibleMerge = 3;
			
			int topNSuOffset[] = new int[topN];
			int topNSuLength[] = new int[topN];
			
			int topNSoOffset[] = new int[topN];
			int topNSoLength[] = new int[topN];
			
			String[] topNsource = new String[topN];
			
			
			
			String line="";
			
			line = br.readLine();
			String[] s = line.split(" ");
			String tempS[] = new String[3];
			String susp = s[2].substring(0, s[2].indexOf("_"));
			
			p.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			p.println("<document reference=\"" + susp + "\">");
			
			int initSuOffset = Integer.parseInt(s[2].substring((s[2].indexOf("_")+1), s[2].lastIndexOf("_")));
			int initSuLength = Integer.parseInt(s[2].substring((s[2].lastIndexOf("_")+1), s[2].lastIndexOf(".txt")));
			
			String initSource = s[1].substring(0, s[1].indexOf("_"));
			int initSoOffset = Integer.parseInt(s[1].substring((s[1].indexOf("_")+1), s[1].lastIndexOf("_")));
			int initSoLength = Integer.parseInt(s[1].substring((s[1].lastIndexOf("_")+1), s[1].lastIndexOf(".txt")));
			
			
			String previous = susp;
			String current = susp;
			int N=1, count=0;
			
			int window = 2000;
			int skip = 0, step = 4;
			
			String mergeSource="";
			
			int merge_su_offset = 0;
			int merge_su_length = 0;
			
			int merge_so_offset = 0;
			int merge_so_length = 0;
			
			while((line = br.readLine())!=null) {
				
				
				s = line.split(" ");
				
				current = s[2];
				
				int suOffset=0;
				int suLength=0;
				
				if(current.equals(previous) && count < N) {
					count++;
				}
				else if(!previous.equals(current)){
					
					boolean hit =false;
					count = 0;
					previous = s[2];
					
					String source="";
					int soOffset =0;
					int soLength=0;
					
					topNSuOffset[0] = Integer.parseInt(s[2].substring((s[2].indexOf("_")+1), s[2].lastIndexOf("_")));
					topNSuLength[0] = Integer.parseInt(s[2].substring((s[2].lastIndexOf("_")+1), s[2].lastIndexOf(".txt")));
					
					topNsource[0] = s[1].substring(0, s[1].indexOf("_"));
					topNSoOffset[0] = Integer.parseInt(s[1].substring((s[1].indexOf("_")+1), s[1].lastIndexOf("_")));
					topNSoLength[0] = Integer.parseInt(s[1].substring((s[1].lastIndexOf("_")+1), s[1].lastIndexOf(".txt")));
					
					for(int j=1; j<topN; j++) {
						if((line = br.readLine())!=null) {
							tempS = line.split(" ");
							topNsource[j] = tempS[1].substring(0, tempS[1].indexOf("_"));
							
							topNSuOffset[j] = Integer.parseInt(tempS[2].substring((tempS[2].indexOf("_")+1), tempS[2].lastIndexOf("_")));
							topNSuLength[j] = Integer.parseInt(tempS[2].substring((tempS[2].lastIndexOf("_")+1), tempS[2].lastIndexOf(".txt")));
							
							topNSoOffset[j] = Integer.parseInt(tempS[1].substring((tempS[1].indexOf("_")+1), tempS[1].lastIndexOf("_")));
							topNSoLength[j] = Integer.parseInt(tempS[1].substring((tempS[1].lastIndexOf("_")+1), tempS[1].lastIndexOf(".txt")));
						}
					}
					
					for(int j=0; j< topN;j++) {
						
						suOffset = topNSuOffset[j];
						suLength = topNSuLength[j];
						
						source = topNsource[j];
						soOffset = topNSoOffset[j];
						soLength = topNSoLength[j];
						
						
						if(Math.abs((initSoOffset + initSoLength) - soOffset) < window && 
								initSource.equals(source)) {
							if( initSoOffset < soOffset) { // ( ) [ ]   OR ( [ ) ]							
								merge_su_offset = initSuOffset;
								merge_su_length = (suOffset - initSuOffset) + suLength;
								
								merge_so_offset = initSoOffset;
								merge_so_length = (soOffset - initSoOffset) + soLength;
								
								mergeSource = source;
								skip = 0;
								hit=true;
								
								totalMerge++;
								initSuOffset = merge_su_offset;
								initSuLength = merge_su_length;
								
								initSource = mergeSource;
								
								initSoOffset = merge_so_offset;
								initSoLength = merge_so_length;
								
								break;
							}
						}
					
						
						
						/* We check different conditions of merging case where
						 * ( ) represents the initial chunk -- initSo 
						 * [ ] represents the newly identified chunk -- So
						 */
	/*					if(initSoOffset > soOffset && initSoLength < soLength) { // [ ( ) ]
							merge_su_offset = initSuOffset;
							merge_su_length = (suOffset - initSuOffset) + suLength;
							
							merge_so_offset = soOffset;
							merge_so_length = soLength;
							
							mergeSource = source;
							skip=0;
							
						}
						else if(initSoOffset < soOffset  && initSoLength > soLength) { // ( [ ] )
							merge_su_offset = initSuOffset;
							merge_su_length = (suOffset - initSuOffset) + suLength;
							
							merge_so_offset = initSoOffset;
							merge_so_length = initSoLength;
							
							mergeSource = source;
							skip = 0;
						}*/
						
	/*					else if(initSoOffset > soOffset) { // [ ] ( ) [ ( ] )
							merge_su_offset = initSuOffset;
							merge_su_length = (suOffset - initSuOffset) + suLength;
							
							merge_so_offset = soOffset;
							merge_so_length = (initSoOffset - soOffset) + initSoLength;
							
							mergeSource = source;
							skip = 0;
						}*/
						
					} // if (<1500 closed)
					if(hit == false) {
						skip++;
					}
					
					if(skip>step && merge_so_length!=0 && mergeSource != null) {
						if(totalMerge> totalPermissibleMerge)
							if(merge_su_length > 800 && merge_so_length > 800)
								annotate(susp, merge_su_offset, merge_su_length, mergeSource, merge_so_offset, merge_so_length,p);
						
						totalMerge = 0;
						merge_su_offset = 0;
						merge_su_length = 0;
						
						merge_so_offset = 0;
						merge_so_length = 0;
						
						skip = 0;
					}
					
					if(merge_so_length==0) {
						initSuOffset = topNSuOffset[0];
						initSuLength = topNSuLength[0];
						
						initSource = topNsource[0];
						initSoOffset = topNSoOffset[0];
						initSoLength = topNSoLength[0];
					}
//					System.out.println(initSource +" "+soOffset+" "+soLength);
				}
			} // while (readLine closed)
			
			if(merge_so_length!=0 && mergeSource!=null) {
				if(merge_su_length > 500 && merge_so_length > 500)
					annotate(susp, merge_su_offset, merge_su_length, mergeSource, merge_so_offset, merge_so_length,p);
				
				merge_su_offset = 0;
				merge_su_length = 0;
				
				merge_so_offset = 0;
				merge_so_length = 0;
				
				skip = 0;
			}
			p.print("</document>");
			p.close();
			fos.close();
			
			
			name = files[f].getName().substring(0, files[f].getName().indexOf(".txt")) + ".xml";
			fos = new FileOutputStream("/home/parth/workspace/clpd-kbs/output/detection"+"_"+suffix+"/"+name);
			p = new PrintStream(fos);
			
			p.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			p.println("<document reference=\"" + susp + "\">");
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			Document dom = db.parse(new File("/home/parth/workspace/clpd-kbs/output/detection"+"_"+suffix+"/"+name+"1"));
			
			//Document doc = builder.parse("persons.xml");
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			// XPath Query for showing all nodes value
			XPathExpression expr = xpath.compile("//feature[@name='detected-plagiarism']");
			
			Object result = expr.evaluate(dom, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			
			String source="";
			int suOffset=0,suLength =0, soOffset =0, soLength =0;
			
			boolean start = true, merged = false;
			
			initSuOffset = 0;
			initSuLength = 0;
			initSoOffset = 0;
			initSuLength = 0;
			
			System.out.println(nodes.getLength());

			for (int j = 0; j < nodes.getLength(); j++) {
				NamedNodeMap nl = nodes.item(j).getAttributes();
				int length = nl.getLength();
				for( int i1=0; i1<length; i1++) {
					Attr attr = (Attr) nl.item(i1);
					// System.out.println(attr.getName());
					if(attr.getName().equals("this_offset")) {
						suOffset = Integer.parseInt(attr.getValue());
					}
					
					if(attr.getName().equals("this_length")) {
						suLength = Integer.parseInt(attr.getValue());
					}
					if(attr.getName().equals("source_offset")) {
						soOffset = Integer.parseInt(attr.getValue());
					}
					if(attr.getName().equals("source_length")) {
						soLength = Integer.parseInt(attr.getValue());
					}
					if(attr.getName().equals("source_reference")) {
						source = attr.getValue();
					}
				}
				
				System.out.println(suOffset + " " + suLength  + " " + soOffset + " " + soLength);
				
				if(start) {
					initSuOffset = suOffset;
					initSuLength = suLength;
					
					initSoOffset = soOffset;
					initSoLength = soLength;
					
					initSource = source;
					
					start = false;
					
					continue;
				}
				
				if((Math.abs((initSoOffset + initSoLength) - soOffset) < 2500 ||  
						Math.abs((initSuOffset + initSuLength) - suOffset) < 2500 ) &&
						/*Math.abs((initSoOffset + initSoLength) - soOffset) > 0  && */ 
						initSource.equals(source)) {
					/* We check different conditions of merging case where
					 * ( ) represents the initial chunk -- initSo 
					 * [ ] represents the newly identified chunk -- So
					 */
					if(Math.abs((initSoOffset + initSoLength) - soOffset) < 2500 && 
							Math.abs((initSuOffset + initSuLength) - suOffset) < 3500 ) {
						if( initSoOffset < soOffset) { // ( ) [ ]   OR ( [ ) ]
													
							merge_su_offset = initSuOffset;
							merge_su_length = (suOffset - initSuOffset) + suLength;
							
							merge_so_offset = initSoOffset;
							merge_so_length = (soOffset - initSoOffset) + soLength;
							
							mergeSource = source;
							
							initSuOffset = merge_su_offset;
							initSuLength = merge_su_length;
							
							initSource = mergeSource;
							
							initSoOffset = merge_so_offset;
							initSoLength = merge_so_length;
							
							merged = true;
						}
/*						else if(initSoOffset > soOffset && initSoLength < soLength) { // [ ( ) ]
							merge_su_offset = initSuOffset;
							merge_su_length = (suOffset - initSuOffset) + suLength;
							
							merge_so_offset = soOffset;
							merge_so_length = soLength;
							
							mergeSource = source;
							
							initSuOffset = merge_su_offset;
							initSuLength = merge_su_length;
							
							initSource = mergeSource;
							
							initSoOffset = merge_so_offset;
							initSoLength = merge_so_length;
							
							merged = true;
						}
						else if(initSoOffset < soOffset  && initSoLength > soLength) { // ( [ ] )
							merge_su_offset = initSuOffset;
							merge_su_length = (suOffset - initSuOffset) + suLength;
							
							merge_so_offset = initSoOffset;
							merge_so_length = initSoLength;
							
							mergeSource = source;

							initSuOffset = merge_su_offset;
							initSuLength = merge_su_length;
							
							initSource = mergeSource;
							
							initSoOffset = merge_so_offset;
							initSoLength = merge_so_length;
							
							merged = true;
						}					
						else if(initSoOffset > soOffset) { // [ ] ( ) [ ( ] )
							merge_su_offset = initSuOffset;
							merge_su_length = (suOffset - initSuOffset) + suLength;
							
							merge_so_offset = soOffset;
							merge_so_length = (initSoOffset - soOffset) + initSoLength;
							
							mergeSource = source;

							initSuOffset = merge_su_offset;
							initSuLength = merge_su_length;
							
							initSource = mergeSource;
							
							initSoOffset = merge_so_offset;
							initSoLength = merge_so_length;
							
							merged = true;
						}*/
					}
					else if( Math.abs((initSoOffset + initSoLength) - soOffset) < 3500 &&  
								Math.abs((initSuOffset + initSuLength) - suOffset) < 2500 ) {
						if( initSoOffset < soOffset) { // ( ) [ ]   OR ( [ ) ]
													
							merge_su_offset = initSuOffset;
							merge_su_length = (suOffset - initSuOffset) + suLength;
							
							merge_so_offset = initSoOffset;
							merge_so_length = (soOffset - initSoOffset) + soLength;
							
							mergeSource = source;
							
							initSuOffset = merge_su_offset;
							initSuLength = merge_su_length;
							
							initSource = mergeSource;
							
							initSoOffset = merge_so_offset;
							initSoLength = merge_so_length;
							
							merged = true;
						}
					}
					
				}
				else {
					if(merged) {
						annotatePost(susp, merge_su_offset, merge_su_length, mergeSource, 
								merge_so_offset, merge_so_length,p, 
								fileContents.get(name.replace(".xml1", ".txt")), 
								fileContents.get(mergeSource));
						annotate(susp, merge_su_offset, merge_su_length, mergeSource, 
								merge_so_offset, merge_so_length,p);
						merged = false;
						
						merge_su_offset = 0;
						merge_su_length = 0;
						
						merge_so_offset = 0;
						merge_so_length = 0;
						
						mergeSource = "";
						
						initSuOffset = suOffset;
						initSuLength = suLength;
						
						initSoOffset = soOffset;
						initSoLength = soLength;
						
						initSource = source;
						mergeSource = "";
					}
					else {
						
						if(initSuOffset!=0) {
							annotate(susp, initSuOffset, initSuLength, initSource, initSoOffset, 
									initSoLength,p);
						}
						merged = false;
						
						merge_su_offset = 0;
						merge_su_length = 0;
						
						merge_so_offset = 0;
						merge_so_length = 0;
						
						
						initSuOffset = suOffset;
						initSuLength = suLength;
						
						initSoOffset = soOffset;
						initSoLength = soLength;
						
						initSource = source;
						
						mergeSource = "";
					}
				}
				
			}
			
			if(initSuOffset!=0) {
				annotate(susp, initSuOffset, initSuLength, source, initSoOffset, initSoLength,p);
			}
			
			p.print("</document>");
		}
		
		
	}
}