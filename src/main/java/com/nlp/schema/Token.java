package com.nlp.schema;

import java.util.ArrayList;
import java.util.List;

import com.nlp.ner.NamedEntity;

/**
 *   A token represents a word in a sentence.
 *   
 *   It is associated with a sentence number, and surrounding contextual symbols (before and after).
 *   
 *   For example a string like "(Joe's)" would be represented as a token with content "Joe",
 *   before symbol (  and after symbols '), allowing us to recognize it as both being in parentheses
 *   and having an apostrophe. 
 *
 */

public class Token {
	String str;   // the actual content of the token itself
	int sentenceNumber;
	String beforeSymbol;
	String afterSymbol;
	List<NamedEntity> namedEntities;
	
	public Token(String str, int sentenceNumber) {
		this(str, "", "", sentenceNumber);
	}
	
	public Token(String str, String beforeSymbol, String afterSymbol, int sentenceNumber) {
		this.str = str;
		this.beforeSymbol = beforeSymbol;
		this.afterSymbol = afterSymbol;
		this.sentenceNumber = sentenceNumber;
	}
	
	public String toString() {
		String ret = str;
		ret += ","+ sentenceNumber;
		if(beforeSymbol.length()>0 || afterSymbol.length()>0){ret+=" ["+beforeSymbol+","+afterSymbol+"]";}
		return ret;
	}
	
	public boolean equals(Object other) {
		if(other==null || other.getClass()!=this.getClass())return false;
		Token otherToken = (Token) other;		
		return  str.equals(otherToken.str) &&
				sentenceNumber == otherToken.sentenceNumber &&
				afterSymbol.equals(otherToken.afterSymbol) &&
				beforeSymbol.equals(otherToken.beforeSymbol);
	}
	
	/** Outputs an xml representation of this token to the given StringBuilder.
	 * 
	 * @param sb      StringBuilder to print to
	 * @param indent  indentation to use
	 */
	
	
	public void toXml(StringBuilder sb, String indent) {
		sb.append(indent).
		  append("<word");
		    if(beforeSymbol.length()>0)sb.append(" bef=\"").append(beforeSymbol).append("\"");
		    if(afterSymbol.length()>0)sb.append(" aft=\"").append(afterSymbol).append("\"");
		    if(namedEntities!=null){
		    	sb.append(" entities=\"");
		    	for(int i=0;i<namedEntities.size();i++)sb.append(namedEntities.get(i)).append(i<namedEntities.size()-1?",":"");
		    	sb.append("\"");
		    }
		  sb.append(">").
		append(str).append("</>");			
	}

	public String getStr() {
		return str;
	}

	public int getSentenceNumber() {
		return sentenceNumber;
	}

	public String getBeforeSymbol() {
		return beforeSymbol;
	}

	public String getAfterSymbol() {
		return afterSymbol;
	}
	
	public List<NamedEntity> getNamedEntities() {
		return namedEntities;
	}

	public void tagWithNamedEntity(NamedEntity entity) {		
		if(namedEntities==null)namedEntities = new ArrayList<NamedEntity>();
		namedEntities.add(entity);
	}
}