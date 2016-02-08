package com.nlp;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;

import com.nlp.ner.NamedEntitiesTagger;
import com.nlp.ner.NamedEntity;
import com.nlp.schema.Document;
import com.nlp.schema.Sentence;
import com.nlp.schema.Token;

/**
 * 
 * The main driver
 *
 * Parses the document nlp_data.txt and outputs a list of proper nouns
 * (named entities).
 * 
 *  Also, outputs a modified xml structure where tokens that form a part of
 *  a named entity are flagged with that named entity.  
 *
 */

public class Driver {

	public static void main(String s[]) throws Exception {
		Reader reader = new InputStreamReader(new FileInputStream("nlp_data.txt"), "UTF-8");
		Parser parser = new Parser(reader, "nlp_data");		
		Document document = parser.parseDocument();
		
		// tag the tokens that match the named entities
		NamedEntitiesTagger nerTagger = new NamedEntitiesTagger("NER.txt");
		for(Sentence sentence : document.getSentences())nerTagger.tagExact(sentence.getWords());
		for(Sentence sentence : document.getSentences())nerTagger.tagInexact(sentence.getWords());
				
		System.out.println("Recognized named entities :\n");
		printNamedEntities(document);
		
		System.out.println("\nXml : \n");
		
		StringBuilder sb = new StringBuilder(); 
		document.toXml(sb, "");
		System.out.println(sb.toString());
		reader.close();
	}
	
	private static void printNamedEntities(Document doc) {
		HashSet<NamedEntity> set = new HashSet<NamedEntity>();
		
		for(Sentence sentence : doc.getSentences()){
			for(Token word : sentence.getWords()) {
				if(word.getNamedEntities()!=null) {
					for(NamedEntity entity : word.getNamedEntities())set.add(entity);
				}
			}
		}
		
		for(NamedEntity entity : set)System.out.println(entity);
	}

}
