# NLP

#### General notes

##### Lexing and parsing

The parsing of documents into words (tokens) was implemented using a state machine approach,
with different characters  or character combinations from the input stream effecting the transitions between states.

The <code>Lexer</code> class  a character stream <code>Reader</code> as input and parses it into a stream of Tokens (words), assigning each token to a 
sentence number. A Token <code>Iterator</code> is returned, so the input stream can be parsed on as needed/lazy basis, without reading the entire stream in memory.
Certain states/transitions result in producing matched tokens, which are returned on from the Iterator's next() call. Characters from the input stream are read into a small buffer, in a way that allows one character 
look-ahead at any time.

Once the input stream is lexed into tokens, they are parsed by the <code>Parser</code> class into the Java object structure, consisting of <code>Documents</code>, <code>Sentences</code>, and lists of words (<code>Token</code>s).

##### Tokens

Tokens represent the words themselves, and also any surrounding context such as parentheses, apostrophes, etc. The surrounding context is represented as a string of "before" and "after" symbols, corresponding to the symbols coming before and after a word, as determined by the <code>Lexer</code>.
For example, a string like ```(Bob's)``` would be parsed into a token structure like :

```word=Bobs, before symbol=(, after symbol=')```

and further processing (e.g. recognizing named entities) can use that information.

##### Named entity recognition

The <code>NamedEntitiesTagger</code> class processes tokens inside sentences, and looks for :
1. Exact matches with its dictionary of named entities (tokens must match all the parts (words) of the named entity exactly, in the right order). Tokens will then be tagged with this named entity.
2. Inexact matches - when exact matches could not be found, if a token matches any part of a named entity, it will be tagged with that named entity.

So for example, tokens : ```[James, Clerk, Maxwell]``` will match the named entity ```James Clerk Maxwell``` exactly, while
the token ```Maxwell's```, (which will be parsed into content=Maxwells, after symbol='), will match the ```James Clerk Maxwell``` named entity inexactly.
#### Some assumptions that were made 

1. Documents are in UTF-8 encoding
2. The dots ine ellipsis (...) do not signify end of sentences, nor are they words.
3. Hyphenated words are one word
4. Decimal point in numbers is not an end of sentence. So "Pi is approximately 3.14159." would be considered one sentence. 
5. Decimal numbers between 0 and 1 are preceded by a 0, so "Test 0.3 sides " is interpreted as one sentence, but 
   "Test .3 sides" is two ("Test", and "3 sides")
6. Numbers are considered as words for the purposes of the parsed schema.

#### Limitations/alternative approaches
The lexer implementation could have been done using simpler string parsing and/or regular expressions, though the finite state approach is possibly more maintenable and cleaner and more amenable to changes. It could also have been implemented by specifying a grammar and using recursive descent parsing or similar, but for this project this would probably been have too much.

The named entity recognition that was implemented can of course be extended to detect more matches, such as tokens that don't quite match syntactically but are semantically similar.

#### Example structure of the output schema

```
<documents>
    <document name="document name">
        <sentence num=1>
           <word before="(" after=")" entity="Carl Benjamin Boyer">Boyer</word>
           <word> </word>
           ...
        </sentence>
        <sentence num=2>
        </sentence>
        ...
        <sentence num=n>
        </sentence>
    </document>
</documents>
```
