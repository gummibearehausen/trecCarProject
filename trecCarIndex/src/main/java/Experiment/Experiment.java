package Experiment;

import RetrievalSys.*;

public class Experiment {
	static int runmodel=1;
	public static void main(String[] arg) throws Exception{
		int MeanPrecision_at_k=1000;
		int Precision_at_k=1000;
		int hitsPerPage = 1000;
		double lambda = 0.7;
		if(runmodel==1){
		  String file_dir ="spritzer-v1/";		  		  
		  String outline_name= "spritzer.cbor.outlines";
		  String qrel_name = "spritzer.cbor.article.qrels";
		  String indexPath = "indexfile/";
  		
		  String para_file_name= "spritzer.cbor.paragraphs";
		  Indexer.indexParas(file_dir+para_file_name,indexPath);
		  Queries Q = new Queries(outline_name,file_dir);
		  Searcher.searchEngine(Q, qrel_name,indexPath,hitsPerPage,file_dir,MeanPrecision_at_k,Precision_at_k,lambda);
		}else if(runmodel==2){
		  String file_dir2 ="test200/"; 
		  String outline_name2= "all.test200.cbor.outlines";
		  String qrel_name2 = "all.test200.cbor.article.qrels";
		  String indexPath2 ="F:/WikiIndexFile/";		  
		  Queries Q2= new Queries(outline_name2,file_dir2);
		  Searcher.searchEngine(Q2, qrel_name2,indexPath2,hitsPerPage,file_dir2,MeanPrecision_at_k,Precision_at_k,lambda);
		}else{
			  
			  String file_dir ="spritzer-v1/";		  		
			  String para_file_name= "spritzer.cbor.paragraphs";
			  String indexPath = "indexfile/";
			  Indexer.indexParas(file_dir+para_file_name,indexPath);
			  String outline_name= "spritzer.cbor.outlines";
			  String qrel_name = "spritzer.cbor.article.qrels";
			  
			  String runfile= "";
			  Queries Q = new Queries(outline_name,file_dir);
			  Searcher.searchEngine(Q, qrel_name,indexPath,hitsPerPage,file_dir,MeanPrecision_at_k,Precision_at_k,lambda);
		}
	}
	  

}
