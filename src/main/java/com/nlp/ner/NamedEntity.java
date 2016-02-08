package com.nlp.ner;

public class NamedEntity {

	String parts[];

	public NamedEntity(String parts[]) {
		this.parts = parts;		
	}
	
	public int size() {
		return parts.length;		
	}

	public String getPart(int i) {
		return parts[i];		
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<parts.length;i++)sb.append(parts[i]).append(i<parts.length-1?" ":"");
		return sb.toString();
	}
	
}
