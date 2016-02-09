package com.nlp;

import java.io.Reader;
import java.util.Iterator;

import com.nlp.schema.Document;
import com.nlp.schema.Sentence;
import com.nlp.schema.Token;


/**
 * The parser takes in a Reader for a document and that document's name,
 * and parses it into a <code>Document</code> data structure (which consists
 * of a list of sentences, which in turn consists of individual word tokens). 
 */

public class Parser {

	Reader reader;
	Lexer lex;
	String documentName;
	
	public Parser(Reader reader, String documentName) {
		this.reader = reader;
		this.lex = new Lexer(reader);
		this.documentName = documentName;
	}
	
	public Document parseDocument() {
		Document doc = new Document(documentName);
		
	    Iterator<Token> it = lex.iterator();
    	
    	int lastSentenceNum = -1;
    	Sentence sentence = null;
    	
	    while(it.hasNext()) {
    		Token token = it.next();
    		if(token!=null) {
    			int sentenceNum = token.getSentenceNumber();
    			
    			if(sentenceNum!=lastSentenceNum) {
    				if(sentence!=null)doc.addSentence(sentence);
    				sentence = new Sentence(sentenceNum); 
    			}
    			
    			sentence.addWord(token);
    			lastSentenceNum = sentenceNum;
    		}
    	}
	    
	    if(sentence!=null)doc.addSentence(sentence);
	    
	    return doc;
	}
}
