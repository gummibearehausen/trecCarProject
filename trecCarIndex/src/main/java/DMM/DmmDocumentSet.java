package DMM;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.document.Document;


public class DmmDocumentSet{
	int D = 0;
	ArrayList<DmmDocument> documents = new ArrayList<DmmDocument>();
	
	public DmmDocumentSet(ArrayList<Document> dataset, HashMap<String, Integer> wordToIdMap) 
			 					throws Exception
	{
		
		
		for(Document d: dataset){
			D++;
			
			String ParaText = d.get("text");
			String ParaId = d.get("paraID");
			
			DmmDocument document = new DmmDocument(ParaText, ParaId, wordToIdMap);
			documents.add(document);
		}
		
		
	}

	public ArrayList<DmmDocument> getDmmDocuments(){
		return documents;
	}
}
