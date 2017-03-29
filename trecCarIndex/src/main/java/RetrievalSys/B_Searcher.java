package RetrievalSys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.queryparser.classic.ParseException;
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
import treccarData.Data;
import treccarReadData.DeserializeData;

public class B_Searcher {
	
  public static void searchEngine(Queries queries, String eval_f_name, String IndexPath, int hitsPerPage, String eval_file_dir) throws IOException, ParseException{
	  

	  
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
      	B_MySimilarity similarity = new B_MySimilarity(new DefaultSimilarity());
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
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". "+ hits[i]+" "+ d.get("text"));
            
            String paraId= d.get("paraID");
			if(ranklistByquery.containsKey(queryId)){
				ranklistByquery.get(queryId).add(paraId);
			}else{
				ArrayList<String> ranklist= new ArrayList<String>();
				ranklist.add(paraId);
				ranklistByquery.put(queryId, ranklist);
			}
        }
        
        
 

      	
      }
    	
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