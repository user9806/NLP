package com.nlp.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A document represents a collection of sentences, typically from a single file/input source
 *
 */

public class Document {
	
	String name;
	List<Sentence> sentences;
	
	public Document(String name) {
		this.name = name;
		this.sentences = new ArrayList<Sentence>();
	}
	
	public void addSentence(Sentence sentence) {
		sentences.add(sentence);
	}
	
	public List<Sentence> getSentences() {
		return sentences;
	}
	
	public String getName() {
		return name;
	}
	
	
	/** Outputs an xml representation of this document to the given StringBuilder.
	 * 
	 * @param sb      StringBuilder to print to
	 * @param indent  indentation to use
	 */
	
	public void toXml(StringBuilder sb, String indent) {
		sb.append(indent).append("<document name=\"").append(name).append("\">\n");
		String newIndent = "    " + indent;
		for(Sentence sentence : sentences){
			sb.append(indent);
			sentence.toXml(sb,  newIndent);
			sb.append("\n");
		}
		sb.append(indent).append("</document>");
	}
}
