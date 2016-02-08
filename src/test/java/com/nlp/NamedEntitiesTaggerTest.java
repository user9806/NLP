package com.nlp;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.nlp.ner.NamedEntitiesTagger;
import com.nlp.ner.NamedEntity;
import com.nlp.schema.Token;

public class NamedEntitiesTaggerTest {

	NamedEntitiesTagger nerTagger;
	
	@Before
	public void init() {		
		try {
			StringReader reader = new StringReader("John\nBob\nAlice\nEurope\nNewton Raphson");
			nerTagger = new NamedEntitiesTagger(reader);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void testSingleWordEntity() {		
		Token[] tokens = new Token[]{new Token("Test", 0), new Token("Bob",  0), new Token("Alice", 0), new Token("hyphenated-word", 0)};
		tag(tokens);
		
		assertTrue(!hasNamedEntity(tokens[0]));
		assertTrue(hasNamedEntity(tokens[1],"Bob"));
		assertTrue(hasNamedEntity(tokens[2],"Alice"));
		assertTrue(!hasNamedEntity(tokens[3]));		
		
	}
	
	@Test
	public void testMultiWordEntity() {		
		Token[] tokens = new Token[]{new Token("Test", 0), new Token("Newton",  0), new Token("Raphson", 0), new Token("Word", 0)};
		tag(tokens);
		
		assertTrue(!hasNamedEntity(tokens[0]));
		assertTrue(hasNamedEntity(tokens[1],"Newton Raphson"));
		assertTrue(hasNamedEntity(tokens[2],"Newton Raphson"));
		assertTrue(!hasNamedEntity(tokens[3]));		
		
	}
	
	@Test
	public void testInexactPossessive() {		
		Token[] tokens = new Token[]{new Token("Bob","", "'", 0)};
		nerTagger.tagInexact(Arrays.asList(tokens));
				
		assertTrue(hasNamedEntity(tokens[0],"Bob"));		
	}
	
	@Test
	public void testInexactAnyWord() {		
		Token[] tokens = new Token[]{new Token("Raphson", 0)};
		nerTagger.tagInexact(Arrays.asList(tokens));
				
		assertTrue(hasNamedEntity(tokens[0],"Newton Raphson"));		
	}
	
	private boolean hasNamedEntity(Token token, String string) {
		if(token.getNamedEntities()==null)return false;
		for(NamedEntity entity : token.getNamedEntities())if(entity.toString().equals(string))return true;
		return false;
	}
	
	private boolean hasNamedEntity(Token token) {
		return token.getNamedEntities()!=null;
	}

	private void tag(Token ...tokens) {
		nerTagger.tagExact(Arrays.asList(tokens));			
	}
}
