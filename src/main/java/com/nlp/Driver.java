package com.nlp;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.nlp.schema.Document;

/**
 * 
 * The main driver
 *
 */
public class Driver {

	public static void main(String s[]) throws Exception {
		Reader reader = new InputStreamReader(new FileInputStream("nlp_data.txt"), "UTF-8");
		Parser parser = new Parser(reader, "nlp_data");		
		Document document = parser.parseDocument(reader);
		
		StringBuilder sb = new StringBuilder(); 
		document.toXml(sb, "");
		System.out.println(sb.toString());
	}
}
