package com.nlp.schema;

import java.util.ArrayList;
import java.util.List;


/**
 *  A sentence is a contiguous collection of words in a document.
 *
 */

public class Sentence {
	List<Token> words;                         // The words (tokens) in the sentence
	int num;                                   // sentence number in the document
	
	public Sentence(int num) {
		this.num = num;
		this.words = new ArrayList<Token>();
	}
	
	public void addWord(Token token) {
		words.add(token);		
	}
	
	/** Outputs an xml representation of this sentence to the given StringBuilder.
	 * 
	 * @param sb      StringBuilder to print to
	 * @param indent  indentation to use
	 */
	
	public void toXml(StringBuilder sb, String indent) {
		sb.append(indent).append("<sentence num=").append(num).append(">\n");
		String newIndent = "    " + indent;
		for(Token word : words){
			sb.append(indent);
			word.toXml(sb, newIndent);
			sb.append("\n");
		}
		sb.append(indent).append("</sentence>");
	}
}
