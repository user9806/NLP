package com.nlp.schema;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A list of documents
 *
 */

public class Documents {
	
	List<Document> documents;
	
	public Documents() {
		 documents = new ArrayList<Document>();
	}
	
	public void addDocument(Document document) {
		this.documents.add(document);
	}
	
	public List<Document> getDocuments() {
		return documents;
	}
	
	/** Outputs an xml representation of the documents to the given StringBuilder.
	 * 
	 * @param sb      StringBuilder to print to
	 * @param indent  indentation to use
	 */
	
	public void toXml(StringBuilder sb, String indent) {
		sb.append(indent).append("<documents>\n");
		String newIndent = "    " + indent;
		for(Document document : documents){
			sb.append(indent);
			document.toXml(sb,  newIndent);
			sb.append("\n");
		}
		sb.append(indent).append("</documents>");
	}
}
