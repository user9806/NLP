package com.nlp.ner;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.nlp.schema.Token;


/**
 *  Given a file/input source of named entities, this class tags <code>Token</code>s that form a part of a 
 *  named entity with a reference to that named entity.  
 *
 */

public class NamedEntitiesTagger {

	private BufferedReader reader;
	
	
	/**
	 *  A map from the first word of the named entity to the rest of the words in that entity (e.g. Carl -> NamedEntity[Carl,Benjamin,Boyer])
	 */
	
	private HashMap<String, NamedEntity> dictionaryByFirstWord = new HashMap<String, NamedEntity>();
	
	/**
	 *  A map from any word of the named entity to the rest of the words in that entity (e.g. Carl -> NamedEntity[Carl,Benjamin,Boyer])
	 */	
	
	private HashMap<String, NamedEntity> dictionaryByAnyWord = new HashMap<String, NamedEntity>();
	
	
	/** Create a named entities tagger by loading the list of named entities from a file 
	 * @param file
	 * @throws Exception
	 */
	
	public NamedEntitiesTagger(String file) throws Exception {
		this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		load();
		reader.close();
	}
	
	public NamedEntitiesTagger(Reader reader) throws Exception {
		this.reader = new BufferedReader(reader);
		load();		
	}
	
	private void load() throws IOException {
		String line;
		while((line=reader.readLine())!=null)if(line.trim().length()>0)addLine(line);		
	}
	
	private void addLine(String line) {
		String parts[] = line.split(" ");  // split the named entity into parts, separated by a space 
		NamedEntity entity = new NamedEntity(parts);
		dictionaryByFirstWord.put(parts[0].toUpperCase(), entity);
		
		for(String part : parts)dictionaryByAnyWord.put(part.toUpperCase(), entity);
	}
	
	private NamedEntity getCandidateMatch(String word) {
		return dictionaryByFirstWord.get(word.toUpperCase());
	}
	
	
	/** Scan through the given list of tokens and tag the ones that match
	 *  the named entities exactly  
	 * 
	 * @param tokens The list of tokens to tag
	 */
	
	public void tagExact(List<Token> tokens) {
		for(int i=0;i<tokens.size();i++) {
			Token token = tokens.get(i);
					
			NamedEntity candidateEntity = getCandidateMatch(token.getStr());
			
			// look ahead to see if all the named entity parts exactly (ignoring case) match the tokens ahead
			if(match(tokens, i, candidateEntity)) { 
				tag(tokens, i, candidateEntity);
			}
			
		}
	}
		
	private boolean match(List<Token> tokens, int idx, NamedEntity entity) {
		if(entity==null)return false;
		if (idx + entity.size() > tokens.size())return false;
				
		for (int i = 0; i < entity.size(); i++) {
			if (!entity.getPart(i).equalsIgnoreCase(tokens.get(idx+i).getStr()))return false;
		}
		
		return true;
	}
	
	
	private void tag(List<Token> tokens, int idx, NamedEntity entity) {
		if(entity==null)return;
		if (idx + entity.size() > tokens.size())return;
				
		for (int i = 0; i < entity.size(); i++) {
			tokens.get(idx+i).tagWithNamedEntity(entity);
		}
	}
	
	
	/** Scan through the given list of tokens and tag the ones that potentially (non exactly)
	 *  match the named entities   
	 * 
	 * @param tokens The list of tokens to tag
	 */
	
	public void tagInexact(List<Token> tokens) {
		for(int i=0;i<tokens.size();i++) {
			Token token = tokens.get(i);
			
			// only tag tokens that don't already have exact named entity matches  
			if(token.getNamedEntities()==null) {
			String toMatch = fromPossessive(token);			
			NamedEntity entity = dictionaryByAnyWord.get(toMatch.toUpperCase());
			if(entity!=null)token.tagWithNamedEntity(entity);
			}			
		}
	}
	
	/** Normalize the token's value from possessive to regular
	 *  For example if "Bob's" is a token with content "Bob" and after symbol '
	 *  this function will return Bob 
	 *   
	 * @param token
	 * @return
	 */
	private String fromPossessive(Token token) {		
		String str = token.getStr();
		if (str != null && str.length() > 0) {
			if (token.getAfterSymbol() != null && token.getAfterSymbol().indexOf("'") != -1) {
				if (Character.toLowerCase(str.charAt(str.length() - 1)) == 's')str = str.substring(0, str.length() - 1);				
			}
		}
		
		return str==null?"":str;
	}

}