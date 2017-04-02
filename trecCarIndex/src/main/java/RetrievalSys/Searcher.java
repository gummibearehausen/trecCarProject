package RetrievalSys;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.index.DirectoryReader;


import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import Evaluation.*;


public class Searcher {
//	private Directory directory;
//	private DirectoryReader reader;
//	private IndexSearcher searcher;
//	private String IndexPath;
//  
//  public Searcher(String IndexPath) throws IOException{
//	  this.IndexPath= IndexPath;
//	  this.directory=FSDirectory.open(new File (IndexPath));
//	  this.reader=DirectoryReader.open(directory);
//	  this.searcher=new IndexSearcher(reader);
//  }
  
	
	
  public static void searchEngine(Queries queries, String eval_f_name, String IndexPath, int hitsPerPage, String eval_file_dir) throws Exception{
	  

	  
	  Analyzer analyzer = new StandardAnalyzer();
	     
      int modelNum = 1;
//      Query q = new QueryParser("Content", analyzer).parse(querystr);

      // 3. search
      
      
      Directory directory = FSDirectory.open(new File (IndexPath));
      DirectoryReader reader = DirectoryReader.open(directory);
      IndexSearcher searcher = new IndexSearcher(reader);
      if(modelNum == 1){
//      	searcher.setSimilarity(new BM25Similarity());
      	searcher.setSimilarity(new BM25Similarity());
      }else if(modelNum ==2){
      	MySimilarity similarity = new MySimilarity(new DefaultSimilarity());
        searcher.setSimilarity(similarity);
    }
	  Map<String, ArrayList<String>> ranklistByquery = new HashMap<String, ArrayList<String>>();
  
      for(SySQuery s :queries.readQueries()){    	
        Query q = new QueryParser("text", analyzer).parse(s.getQueryText());
        String queryId = s.getQueryId();
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        

        // 4. display results
        System.out.printf("\nquery: %s \n",s.getQueryText());
        System.out.println("Found " + hits.length + " hits.");
        
        SearchResult result = new SearchResult(hits,searcher) ;
        
        ArrayList<String> ranklist =result.getRankDocId();
        
        ranklistByquery.put(queryId, ranklist);
        result.ClusteringResult();
             	
      }
    	//evaluation
    	EvalReadfile result = new EvalReadfile(eval_file_dir,eval_f_name,ranklistByquery);
    	result.printEval();
      
      // reader can only be closed when there
      // is no need to access the documents any more.
      reader.close();
  }
  
	  
  
  
//  public static void main(String[] arg) throws IOException, ParseException{
//	  
//
//	  String outline_dir ="spritzer-v1/";
//	  String outline_name= "spritzer.cbor.outlines";
//	  String eval_file_name = "spritzer.cbor.article.qrels";
//	  int hitsPerPage = 10;
//	  
//	  String indexPath = "indexfile/";
//	  Queries Q = new Queries(outline_name,outline_dir);
//	  searchEngine(Q, eval_file_name,indexPath,hitsPerPage);
//
//}
//  
  
}