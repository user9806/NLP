package com.nlp;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.nlp.Tasks.Task;
import com.nlp.ner.NamedEntitiesTagger;
import com.nlp.schema.Document;
import com.nlp.schema.Documents;
import com.nlp.schema.Sentence;


/**
 *  This class is a utility class for submitting tasks
 *  to be executed in parallel, and for waiting for
 *  those tasks to complete and retrieving their results. 
 *  
 *  It also includes a method to create the tasks from a list
 *  of files in a zip file.
 */

public class Tasks {

	
	/** A task represents a unit of work to parse a document
	 * into a document object.
	 * 	 
	 * @author lportnoy
	 *
	 */
	
	static class Task implements Callable<Document> {
		InputStream is;
		String name;		
		NamedEntitiesTagger tagger;
		Future<Document> future;

		public Task(InputStream is, String name, NamedEntitiesTagger tagger) {
			this.is = is;
			this.name = name;
			this.tagger = tagger;
		}

		/**  Parses the document and tags the named entities in it. 
		 * 
		 */
		
		public Document call() throws Exception {
			Reader reader = new InputStreamReader(is, "UTF-8");
			Parser parser = new Parser(reader, name);
			Document document = parser.parseDocument();
			
			// tag the tokens that match the named entities			
			for(Sentence sentence : document.getSentences()){
				tagger.tagExact(sentence.getWords());
				tagger.tagInexact(sentence.getWords());
			}
			
			reader.close();
			return document;
		}

		public void setFuture(Future<Document> future) {
			this.future=future;
		}
	}
	
	
	/** Create the tasks by assigning each nlp_data/*.txt file in the zip file
	 *  to a task
	 *
	 * @param zipFile
	 * @return
	 * @throws Exception
	 */
	
	public static List<Task> getTasksFromZip(ZipFile zipFile, NamedEntitiesTagger tagger) throws Exception {
		List<Task> ret = new ArrayList<Task>();		
		
		Enumeration<?> entries = zipFile.entries();		
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) entries.nextElement();
			
			String name = zipEntry.getName();
			if (name.startsWith("nlp_data/") && name.endsWith(".txt")) {
				Task task = new Task(zipFile.getInputStream(zipEntry), name, tagger);
				ret.add(task);				
			}			
		}
		
		return ret;
	}
	
	
	/** Submit the tasks to the executor
	 * 
	 * @param executor
	 * @param tasks
	 */
	
	public static void submitTasks(ExecutorService executor, List<Task> tasks) {
		for(Task task : tasks)task.setFuture(executor.submit(task));		
	}
	
	/** Waits for the completion of the tasks
	 * @param tasks 
	 * @return A documents object containing the list of documents processed by the tasks
	 */
	
	public static Documents waitForCompletion(List<Task> tasks) {
		Documents documents = new Documents();
		for(Task task : tasks) {
			Future<Document> future = task.future;
			try {
				Document document = future.get();			    
				documents.addDocument(document);
			} catch(Exception e) {				
			}
		}
		return documents;
	}




	
}
