package RetrievalSys;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import DMM.*;


public class SearchResult {
	private String Query_text;
	private ScoreDoc[] DocHits;
	private IndexSearcher DocSearcher;
	private ArrayList<String> RankDocId;
	private ArrayList<String> NewRankDocId;
	private ArrayList<Document> RankLuceneDoc;
	private Map<String,Integer> RankCluster;
	private double lambda;
	public SearchResult(String Q_text, ScoreDoc[] hits, IndexSearcher searcher, double lambda) throws IOException{
		this.Query_text=Q_text;
		this.DocHits = hits;
		this.DocSearcher = searcher;
		this.RankDocId = new ArrayList<String>();
		this.NewRankDocId = new ArrayList<String>();
		this.lambda=lambda;
		this.RankLuceneDoc = new ArrayList<Document>();
		this.RankCluster = new HashMap<String,Integer>();
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
	public  ArrayList<String> getNewRankDocId() throws Exception{
		ClusteringResult();
		return NewRankDocId;
	}
	public void ClusteringResult() throws Exception{
		String tempIndexPath = "indexcluster/";
		DMM dmm = new DMM(15,0.1,0.1,20, RankLuceneDoc);
//		long startTime = System.currentTimeMillis();
		dmm.getDocuments();
//		long endTime = System.currentTimeMillis();
//		System.out.println("getDocuments Time Used:" + (endTime-startTime)/1000.0 + "s");
//		startTime = System.currentTimeMillis();
		dmm.runGSDMM();
//		endTime = System.currentTimeMillis();
//		System.out.println("gibbsSampling Time Used:" + (endTime-startTime)/1000.0 + "s");
		
		Map <String, ArrayList<DmmDocument>> topic_docs =dmm.getTopicCluster();
		System.out.println("Number_of_topic clusters found: "+ topic_docs.size());
		Map <String, ArrayList<String>> topic_docs_texts=dmm.getTopicClusterText();
		IndexWriter clusterIndexer = Indexer.indexWriterCreator(tempIndexPath, 0);
		for(String topic_key :topic_docs_texts.keySet() ){
			String cluster_text = String.join(" ", topic_docs_texts.get(topic_key));
//			System.out.println(cluster_text);
			Indexer.addCluster(clusterIndexer,cluster_text,topic_key);
		}
		clusterIndexer.close();
		
		  Analyzer cluster_analyzer = new StandardAnalyzer();
		     
	      int cluster_modelNum = 2;
	      Directory cluster_directory = FSDirectory.open(new File (tempIndexPath));
	      DirectoryReader cluster_reader = DirectoryReader.open(cluster_directory);
	      IndexSearcher cluster_searcher = new IndexSearcher(cluster_reader);
	      if(cluster_modelNum == 2){
////	      	searcher.setSimilarity(new BM25Similarity());
	    	  cluster_searcher.setSimilarity(new BM25Similarity());
	      }else {
	      	MySimilarity cluster_similarity = new MySimilarity(new DefaultSimilarity());
	      	cluster_searcher.setSimilarity(cluster_similarity);
	    }
		  
	  
	       	System.out.println(Query_text);
	        Query cluster_q = new QueryParser("text", cluster_analyzer).parse(Query_text);
	     
	        TopDocs cluster_docs = cluster_searcher.search(cluster_q, 2000);
	        ScoreDoc[] cluster_hits = cluster_docs.scoreDocs;
	        
	        for(int j=0;j<cluster_hits.length;++j) {     	
	            int cluster_Id = cluster_hits[j].doc;
	            Document retr_cluster = cluster_searcher.doc(cluster_Id);
	            String cluster_topic_Id= retr_cluster.get("topicID");
//	            System.out.println((j + 1) + ". "+ cluster_hits[j]+" "+ cluster_topic_Id);           
	            RankCluster.put(cluster_topic_Id, j+1);
	      
	        }	
	        cluster_reader.close();
	        ArrayList<DmmDocument> DmmDocumentSetWithTopicTags = dmm.getDmmDocumentSetWithTopicTags();
//	        System.out.println(dmm.getDmmDocumentSetWithTopicTags());
	        
	        for(int m=0; m<DmmDocumentSetWithTopicTags.size();m++){
	        	DmmDocument DmmDocWithTag= DmmDocumentSetWithTopicTags.get(m);
	        	int document_pseudo_rank= DmmDocWithTag.getOriginRank();
	        	int document_topic_tag = DmmDocWithTag.getTopicTag();
	        	
	        	int cluster_of_document_rank = RankCluster.get(Integer.toString(document_topic_tag));
	        	double new_doc_rank=document_pseudo_rank*lambda+cluster_of_document_rank*(1-lambda);
	        	DmmDocWithTag.setNewRanking(new_doc_rank);
	        }
//	        List<ArrayList<DmmDocument>> DmmDocumentSetWithTopicTagsAsList = new  ArrayList<>(Arrays.asList(DmmDocumentSetWithTopicTags));
	        Collections.sort(DmmDocumentSetWithTopicTags, new DmmDocementComparator());
	        
	        for( DmmDocument d: DmmDocumentSetWithTopicTags){
	        	NewRankDocId.add(d.getParaId());
//	        	System.out.println(d.getParaId());
//	        	System.out.println(d.getNewRanking());
	        };
//	        System.out.println(NewRankDocId);
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