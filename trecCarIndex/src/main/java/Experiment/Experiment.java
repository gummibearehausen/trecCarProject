package Experiment;
import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;


import RetrievalSys.*;

public class Experiment {
	
	public static void main(String[] arg) throws IOException, ParseException{
		  String file_dir ="spritzer-v1/";
		  
		  String outline_name= "spritzer.cbor.outlines";
		  String qrel_name = "spritzer.cbor.article.qrels";
		  int hitsPerPage = 10;
		  
		  String indexPath = "indexfile/";
		  Queries Q = new Queries(outline_name,file_dir);
		  B_Searcher.searchEngine(Q, qrel_name,indexPath,hitsPerPage,file_dir);

	}
	  

}
