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
		
		int OriginalRank=1;
		for(Document d: dataset){
			D++;
			
			String ParaText = d.get("text");
			String ParaId = d.get("paraID");
			
			DmmDocument document = new DmmDocument(ParaText, ParaId, OriginalRank, wordToIdMap);
			documents.add(document);
			OriginalRank+=1;
		}
		
		
	}

	public ArrayList<DmmDocument> getDmmDocuments(){
		return documents;
	}
}
