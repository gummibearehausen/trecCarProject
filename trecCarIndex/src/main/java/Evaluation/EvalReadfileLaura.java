package Evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EvalReadfileLaura {
	

	 String data_path;
	 String data_result;
	 String data_truth;
	 String run_file;
	 static Map<String, Set<String> >truth;
	 Map<String, ArrayList<String>> ranklistByquery;
	 ArrayList<Double> aver_eval;
	 int map_at_k;
	 int pr_at_k;
	 
	
//	@SuppressWarnings("static-access")
	public EvalReadfileLaura(String data_path, String data_truth,String runfile,int map_at_k, int pr_at_k) throws NumberFormatException, IOException{
		this.data_path=data_path;			
		this.data_truth= data_truth;
		this.ranklistByquery=ReadSearchResult(runfile);;
		this.map_at_k=map_at_k;
		this.pr_at_k=pr_at_k;
		
		Map<String, Set<String> >groundtruth = new HashMap<String, Set<String>>();

		String filename1 = data_path+data_truth;
		String line;
		InputStream is = new FileInputStream(new File(filename1));
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		
		while ((line =br.readLine()) != null){			    
				String[] parsedLine = line.split(" ");
				String sectionId = parsedLine[0];
				String docId = parsedLine[2];

					if(groundtruth.containsKey(sectionId)){
						groundtruth.get(sectionId).add(docId);
					}else{ 
						Set<String> trueDocId= new HashSet <String>();
						trueDocId.add(docId);
						groundtruth.put(sectionId, trueDocId);}				
		}

		
		br.close();	
		ArrayList<Double>ave_eval = new ArrayList<Double>();
	
		int num_of_query = ranklistByquery.keySet().size();		
		for(String queryId:ranklistByquery.keySet()){			
			
						Metrics eval_query = new Metrics(ranklistByquery.get(queryId),queryId, groundtruth,map_at_k,pr_at_k);			
						ArrayList<Double> query_result =eval_query.getResult();
						
						for(int i=0;i<query_result.size(); i++){
							if(ave_eval.size()< query_result.size()){
								ave_eval.add(query_result.get(i));
							}else{
								ave_eval.set(i,ave_eval.get(i)+query_result.get(i));
							}
						}
					}
		for(int j = 0; j< ave_eval.size(); j++){
			ave_eval.set(j, ave_eval.get(j)/num_of_query);
		}
		this.aver_eval= ave_eval;
	}

	public EvalReadfileLaura(String data_path, File truthFile,String runfile,int map_at_k, int pr_at_k) throws NumberFormatException, IOException{
		this.data_path=data_path;
//		this.data_truth= data_truth;
		this.ranklistByquery=ReadSearchResult(runfile);;
		this.map_at_k=map_at_k;
		this.pr_at_k=pr_at_k;

		Map<String, Set<String> >groundtruth = new HashMap<String, Set<String>>();

		String filename1 = data_path+data_truth;
		String line;
//		InputStream is = new FileInputStream(new File(filename1));
        InputStream is = new FileInputStream(truthFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		while ((line =br.readLine()) != null){
			String[] parsedLine = line.split(" ");
			String sectionId = parsedLine[0];
			String docId = parsedLine[2];

			if(groundtruth.containsKey(sectionId)){
				groundtruth.get(sectionId).add(docId);
			}else{
				Set<String> trueDocId= new HashSet <String>();
				trueDocId.add(docId);
				groundtruth.put(sectionId, trueDocId);}
		}


		br.close();
		ArrayList<Double>ave_eval = new ArrayList<Double>();

		int num_of_query = ranklistByquery.keySet().size();
		for(String queryId:ranklistByquery.keySet()){

			Metrics eval_query = new Metrics(ranklistByquery.get(queryId),queryId, groundtruth,map_at_k,pr_at_k);
			ArrayList<Double> query_result =eval_query.getResult();

			for(int i=0;i<query_result.size(); i++){
				if(ave_eval.size()< query_result.size()){
					ave_eval.add(query_result.get(i));
				}else{
					ave_eval.set(i,ave_eval.get(i)+query_result.get(i));
				}
			}
		}
		for(int j = 0; j< ave_eval.size(); j++){
			ave_eval.set(j, ave_eval.get(j)/num_of_query);
		}
		this.aver_eval= ave_eval;
	}

	public void printEval(){
			ArrayList<Double> result=this.aver_eval;
			System.out.println();
			System.out.printf("MAP@%d: "+result.get(0),this.map_at_k);
			System.out.println();
			System.out.printf("precision at %d: "+result.get(1),this.pr_at_k);
			System.out.println();
			System.out.println("precision at R: "+result.get(3));
			System.out.println("mrr: "+result.get(4));
		
	}


	
  public static Map<String, ArrayList<String>> ReadSearchResult(String WriteFileName) throws IOException{
	  Map<String, ArrayList<String>>rankDocListQuies= new HashMap<String, ArrayList<String>>();
	  String WriteFileDirRead ="tempSearchResult/";
	  InputStream is = new FileInputStream(new File(WriteFileDirRead+WriteFileName));
		BufferedReader bufferReading = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line =bufferReading.readLine()) != null){		
//				System.out.println(line);
			String[] ParsedLine= line.trim().split(" ");
//				System.out.println(ParsedLine);
			String queryId = ParsedLine[0];
			int doc_rank = Integer.valueOf(ParsedLine[3]);
			String passage_id = ParsedLine[2];
			if(rankDocListQuies.containsKey(queryId)){
				rankDocListQuies.get(queryId).add(passage_id);					
			}else{
				ArrayList<String> docIds = new ArrayList<String>();
				docIds.add(passage_id);
				rankDocListQuies.put(queryId, docIds);
			}
			
		}	  
		bufferReading.close();
		return rankDocListQuies;
  }
}
