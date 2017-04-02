package RetrievalSys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import DMM.*;

public class SearchResult {
	private ScoreDoc[] DocHits;
	private IndexSearcher DocSearcher;
	private ArrayList<String> RankDocId;
	private ArrayList<Document> RankLuceneDoc;
	public SearchResult(ScoreDoc[] hits, IndexSearcher searcher ) throws IOException{
		this.DocHits = hits;
		this.DocSearcher = searcher;
		this.RankDocId = new ArrayList<String>();
		this.RankLuceneDoc = new ArrayList<Document>();
	    setRankList();
	}
	
	public void  setRankList() throws IOException{
        for(int i=0;i<DocHits.length;++i) {     	
            int docId = DocHits[i].doc;
            Document d = DocSearcher.doc(docId);
//            System.out.println((i + 1) + ". "+ DocHits[i]+" "+ d.get("text"));           
            String paraId= d.get("paraID");
            RankDocId.add(paraId);         
            RankLuceneDoc.add(d);
        }	
        
	}
	
	public  ArrayList<String> getRankDocId(){
		return RankDocId;
	}
	public void ClusteringResult() throws Exception{
		String tempIndexPath = "tempIndexFile/";
		DMM dmm = new DMM(5,0.1,0.1,5, RankLuceneDoc);
		dmm.getDocuments();
		dmm.runGSDMM();
		System.out.print(dmm.getTopicCluster());
		
//		Map<String, ArrayList<DmmDocument>> topic_clusters =dmm.getTopicCluster();
//		for(String k: topic_clusters.keySet()){
//       	 System.out.println("$$$$$"+k);
//       	 System.out.println(topic_clusters.get(k));
//       	 System.out.println();
//        }
	}
	public ArrayList<Document> getRankLuceneDoc(){
		return RankLuceneDoc;
	}
}
