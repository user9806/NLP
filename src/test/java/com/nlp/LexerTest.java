package com.nlp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;

import com.nlp.schema.Token;

public class LexerTest {
	
	@Test 
	public void testEmpty() {
		List<Token> tokens = lex("");
		assertTrue(equals(tokens, new Token[]{null}));
	}
	
	@Test 
	public void testNilAtEnd() {
		List<Token> tokens = lex("Test ");
		assertTrue(equals(tokens, new Token[]{new Token("Test", 0), null}));
	}
	
	@Test 
	public void testNewLines() {		
		List<Token> tokens = lex("One \n\n\n two \n\n  three");
		assertTrue(equals(tokens, new Token[]{new Token("One", 0), new Token("two", 0), new Token("three", 0)}));
	}
	
	@Test 
	public void testSentenceBreak() {		
		List<Token> tokens = lex("One. Two. Three. Four");
		assertTrue(equals(tokens, new Token[]{new Token("One", 0), new Token("Two", 1), new Token("Three", 2), new Token("Four", 3)}));
	}
	
	@Test 
	public void testEllipsis() {		
		List<Token> tokens = lex("One. ... Two. Three. ... Four");
		assertTrue(equals(tokens, new Token[]{new Token("One", 0), new Token("Two", 1), new Token("Three", 2), new Token("Four", 3)}));
	}
	
	@Test 
	public void testPeriodsAtWordEnd() {		
		List<Token> tokens = lex("One... Two. Three. Four");
		assertTrue(equals(tokens, new Token[]{new Token("One", 0), new Token("Two", 1), new Token("Three", 2), new Token("Four", 3)}));
	}
	
	@Test 
	public void testNumbers() {		
		List<Token> tokens = lex("One 3.141529 Two 0.98 Three. Four");
		assertTrue(equals(tokens, new Token[]{new Token("One", 0), new Token("3.141529", 0), new Token("Two", 0), new Token("0.98", 0), new Token("Three", 0), new Token("Four", 1)}));
	}
	
	@Test 
	public void testNumbersWithNoLeadingZero() {		
		List<Token> tokens = lex("One Two .98 Three. Four");
		assertTrue(equals(tokens, new Token[]{new Token("One", 0), new Token("Two", 0), new Token("98", 1), new Token("Three", 1), new Token("Four", 2)}));
	}
	
	@Test 
	public void testNumbersWithDotButNoDecimalDigit() {		
		List<Token> tokens = lex("One Two 3. Three. Four");
		assertTrue(equals(tokens, new Token[]{new Token("One", 0), new Token("Two", 0), new Token("3", 0), new Token("Three", 1), new Token("Four", 2)}));
	}
	
	@Test 
	public void testSurroundingSymbols() {		
		List<Token> tokens = lex("(One) (two) \"three\" Joe's");
		assertTrue(equals(tokens, new Token[]{new Token("One", "(", ")", 0), new Token("two", "(", ")", 0), new Token("three", "\"", "\"", 0), new Token("Joes", "", "'", 0)}));
	}
	
	@Test 
	public void testMoreSurroundingSymbols() {		
		List<Token> tokens = lex(";;One' :two\" \"three\" Joe's");
		assertTrue(equals(tokens, new Token[]{new Token("One", ";;", "'", 0), new Token("two", ":", "\"", 0), new Token("three", "\"", "\"", 0), new Token("Joes", "", "'", 0)}));
	}
	
	@Test 
	public void testHyphens() {		
		List<Token> tokens = lex("This is a hyphenated-word");
		assertTrue(equals(tokens, new Token[]{new Token("This", 0), new Token("is",  0), new Token("a", 0), new Token("hyphenated-word", 0)}));
	}
	
	
	@Test 
	public void testPunctuation() {		
		List<Token> tokens = lex("A question? Yes! Sentence 3. Four");
		assertTrue(equals(tokens, new Token[]{new Token("A", 0), new Token("question", 0), new Token("Yes", 1), new Token("Sentence", 2), new Token("3", 2), new Token("Four", 3)}));
	}
	
	@Test 
	public void testCombination() {		
		List<Token> tokens = lex("Test ...(A') .3 0.14...\n\n\n word.");
		assertTrue(equals(tokens, new Token[]{new Token("Test", 0), new Token("A", "(","')", 0), new Token("3", 1), new Token("0.14", 1), new Token("word", 2), null}));
	}
	
	
	private List<Token> lex(String string) {
		ArrayList<Token> ret = new ArrayList<Token>();		
		
		Lexer lex = new Lexer(string);		
		Iterator<Token> it = lex.iterator();
		
		while(it.hasNext())ret.add(it.next());
		return ret;
	}
	
	private boolean equals(List<Token> tokens, Token ...expectedTokens){
		if(tokens.size()!=expectedTokens.length)return false;
		
		for(int i=0;i<tokens.size();i++){
			Token t = tokens.get(i);
			Token expected = expectedTokens[i];
			boolean equal = (t==null && expected==null) || t.equals(expected);
			if(!equal)return false;			
		}
		return true;
	}
}
