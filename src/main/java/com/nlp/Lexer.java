package com.nlp;

import java.io.CharArrayReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import com.nlp.schema.Token;

/**    This class is a Lexer that takes a character stream Reader as input 
 *     and parses it into a stream of Tokens (words), assigning each token to a 
 *     sentence number. A Token <code>Iterator</code> is returned, so the input stream 
 *     can be parsed on as needed/lazy basis, without reading the entire stream in memory.
 *     
 *     The Tokens store the word itself, along with information on the surrounding strings 
 *     (e.g. quotation marks, apostrophes, etc.) before and after the word.      
 *     
 *     The actual tokenization is implemented as a State Machine, with different characters  
 *     or character combinations from the input stream effecting the transitions between states.
 *     Certain states/transitions result in producing matched tokens, which are returned on
 *     from the Iterator's next() call.  
 *    
 *     Characters from the input stream are read into a small buffer, in a way that allows one character 
 *     look-ahead at any time.
 **/

public class Lexer implements Iterable<Token> {

    private Reader reader;
    
    // char buffer, stores next n chars
    private char[] cb = new char[1024];

    // current char index, -1 when no char in buffer
    private int p = -1;
    
    // current number of characters in buffer
    private int charsInBuffer = 0;

    // null char
    private static final char nil = 0xffff;

    // The various states of the state machine 
    enum State {
    	BEFORE_WORD,
    	IN_MIDDLE_OF_WORD,
    	DOT_SEQUENCE,
    	IN_MIDDLE_OF_DIGIT
    }
    
    private final Iterator<Token> iter = new Iterator<Token>() {

    	boolean hasNext = true;
    	public boolean hasNext() {
            return hasNext;
        }

    	// stores symbols surrounding (before and after) the word, if any
    	String beforeSymbol="", afterSymbol="";  
        
        int dots = 0;
        
        // keeps track of the sentence number
        int sentenceNumber;  
                 
        // the current state of the state machine
        State state = State.BEFORE_WORD;
        
        // the currently matched word
        StringBuilder sb;
        
        // the token we will return
        Token matchedToken = null; 

        // final state indicator (when about to return a matched token)
        boolean fnl = false;
        
        public Token next() {
            // first invoke
            if (charsInBuffer == 0) {
                readToBuffer(0);
            }
            
            sb = new StringBuilder();
            
			while (!fnl) {
				char c = peek();	
				if(c==nil){hasNext = false; if(sb.length()>0)matchTokenAndGoToState(State.BEFORE_WORD); else return null;}
				
				switch (state) {
				
				case BEFORE_WORD:
					if (isBlank(c)) { // skip over white spaces and newlines						
					} else if (isSurroundingSymbol(c)) {
						beforeSymbol += c;
					} else if (Character.isLetter(c)) {
						sb.append(c);
						state = State.IN_MIDDLE_OF_WORD;
					} else if (Character.isDigit(c)) {
						sb.append(c);
						state = State.IN_MIDDLE_OF_DIGIT;
					}
					else if (c == '.') {
						dots = 1;
						state = State.DOT_SEQUENCE;
					}
					advance();
					break;
				case DOT_SEQUENCE:  // dot sequence in between words
					if(c=='.'){dots++;advance();}
					else if (c!='.') {												
						if (dots == 1)sentenceNumber++; // just one dot (followed by a non dot) indicates an end of a sentence, more than one dot is an ellipsis 'word'						
						state = State.BEFORE_WORD;
					}
					break;
				case IN_MIDDLE_OF_WORD:
					if(isPunctuation(c)) {						
						while(isPunctuation(c=peek())){advance();} // skip remaining punctuation (i.e. dots, exclamation marks, question marks) 
						matchTokenAndGoToState(State.BEFORE_WORD);
						sentenceNumber++;
					}					
					else if(isBlank(c)) {
						matchTokenAndGoToState(State.BEFORE_WORD);
						advance();
					} 
					else if(isSurroundingSymbol(c)) {
						afterSymbol += c;
						advance();
					}
					else {
						sb.append(c);
						advance();
					}
					break;
				case IN_MIDDLE_OF_DIGIT:
					if(Character.isDigit(c))sb.append(c);
					else if(c=='.') {						
						if(Character.isDigit(peekNext()) && sb.indexOf(".")==-1)sb.append(c);     // if this is the first . in number, and the next character after . is a digit, then treat is as a decimal point
						else {state = State.IN_MIDDLE_OF_WORD; continue;}                         // otherwise process as dots in middle of word 
					}
					else matchTokenAndGoToState(State.BEFORE_WORD);
					advance();
					break;
				}
			}
            
			fnl = false;
			return matchedToken;
        }
        
        private void matchTokenAndGoToState(State nextState) {
			matchedToken = new Token(sb.toString(), beforeSymbol, afterSymbol, sentenceNumber);
			state = State.BEFORE_WORD;
			fnl = true;
			beforeSymbol = "";
			afterSymbol = "";
        }
        
        private boolean isBlank(char c) {
			if(c=='\n' || c==' ')return true;
			return false;
		} 
        
        private boolean isSurroundingSymbol(char c) {
			if(c=='(' || c==')' || c==';' || c==':' || c=='\'' || c=='"' || c==',')return true;
			return false;
		}
        
        public boolean isPunctuation(char c) {
        	if(c=='.' || c=='!' || c=='?')return true;
        	return false;
        }

		private void advance() {
        	p++;
        	if (p >= charsInBuffer - 1) {
                cb[0] = cb[p];
            	readToBuffer(1);            	
            }
        }
        
        private void readToBuffer(int offset) {
            try {
                int numRead = reader.read(cb, offset, cb.length-offset);
                if (numRead != -1) {
                    charsInBuffer = offset + numRead;                	
                } else {
                    cb[offset] = nil;
                	charsInBuffer = offset+1;                	
                }
                p=0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        private char peek() {
        	return cb[p];        	
        }
        
        private char peekNext() {
        	return cb[p+1];
        }               

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    };

    /**
     * Lex the given string
     * 
     * @param code
     */
    public Lexer(String code) {
        this.reader = new CharArrayReader(code.toCharArray());
    }

    /**
     * Lex the given reader
     * 
     * @param reader
     */
    public Lexer(Reader reader) {
        this.reader = reader;
    }

    public Iterator<Token> iterator() {
        return this.iter;
    }
    
    public static void main(String s[]) throws Exception {
    	//Lexer lex = new Lexer("Test ...(A') .3 .14\n\n\n");
    	
    	//Reader reader = new InputStreamReader(new FileInputStream("nlp_data.txt"));
    	//Lexer lex = new Lexer(reader);
    	
    	//Lexer lex = new Lexer("Test 3. Two");
    	//Lexer lex = new Lexer("Test...Two");
    	//Lexer lex = new Lexer("Test ...(A') .3 0.14...\n\n\n word.");
    	Lexer lex = new Lexer("Maxwell's equations"); 
    	
    	
    	Iterator<Token> it = lex.iterator();
    	
    	while(it.hasNext()) {
    		Token token = it.next();
    		if(token!=null)System.out.println(token);
    	}
    }
}