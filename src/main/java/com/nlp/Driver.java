package com.nlp;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.nlp.Tasks.Task;
import com.nlp.ner.NamedEntitiesTagger;
import com.nlp.ner.NamedEntity;
import com.nlp.schema.Document;
import com.nlp.schema.Documents;
import com.nlp.schema.Sentence;
import com.nlp.schema.Token;

/**
 * 
 * The main driver
 *
 * Parses the files in nlp_data.zip in parallel, and outputs a list of proper nouns
 * (named entities) and the xml structure (Documents) for the files.
 *
 */

public class Driver {

	public static void main(String s[]) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
		
		// Get a list of tasks. Each task is a file to parse from the zip file.
		ZipFile zipFile = new ZipFile("nlp_data.zip");		
		List<Task> tasks = Tasks.getTasksFromZip(zipFile, new NamedEntitiesTagger("NER.txt"));
		
		
		// Submit the tasks to the executor service, and get futures
		Tasks.submitTasks(executor, tasks);
				
		// wait for the tasks to complete
		Documents documents = Tasks.waitForCompletion(tasks);
		
		System.out.println("Recognized named entities :");
		for(Document document : documents.getDocuments())printNamedEntities(document);		

		System.out.println("\nDocuments xml representation :\n");
		printOutDocuments(documents);

		executor.shutdown();
		zipFile.close();
	}
	
	private static void printOutDocuments(Documents documents) {
		StringBuilder sb = new StringBuilder(); 
		documents.toXml(sb, "");
		System.out.println(sb.toString());		
	}
	
	private static void printNamedEntities(Document doc) {
		HashSet<NamedEntity> set = new HashSet<NamedEntity>();
		
		for(Sentence sentence : doc.getSentences()){
			for(Token word : sentence.getWords()) {
				if(word.getNamedEntities()!=null) {
					for(NamedEntity entity : word.getNamedEntities())set.add(entity);
				}
			}
		}
		
		for(NamedEntity entity : set)System.out.println(entity);
	}	
}
