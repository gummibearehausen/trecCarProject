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

public class EvalReadfile {
	 String data_path;
	 String data_result;
	 String data_truth;
	 static Map<String, Set<String> >truth;
	 Map<String, ArrayList<String>> ranklistByquery;
	 ArrayList<Double> aver_eval;
	
//	@SuppressWarnings("static-access")
	public EvalReadfile(String data_path, String data_truth,Map<String, ArrayList<String>> ranklistByquery) throws NumberFormatException, IOException{
		this.data_path=data_path;			
		this.data_truth= data_truth;
		this.ranklistByquery=ranklistByquery;
		

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
			
						Metrics eval_query = new Metrics(ranklistByquery.get(queryId),queryId, groundtruth,5000,5);			
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
		System.out.println("MAP: "+result.get(0));
		System.out.println("precision at 5: "+result.get(1));
		System.out.println("precision at R: "+result.get(3));
		
	
}

	

}