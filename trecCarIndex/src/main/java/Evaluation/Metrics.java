
package Evaluation;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class Metrics {
	

	ArrayList <String> ranklist;
	ArrayList <Double> result;
	String query;
	Map<String, Set<String>>truth;
	int map_k;
	int pr_k;
	int rankLength;	

	ArrayList<ArrayList<Double>> metricOutput;
	int top_k;
	int num_rel_ret;
	public Metrics (ArrayList<String> ranklist, String query, Map<String, Set<String> >truth, int map_k, int pr_k){

		this.ranklist =ranklist;
		this.truth = truth;	
		this.result = metric_methods(query,map_k, pr_k);
		this.map_k = map_k;
		this.pr_k =pr_k;  
		this.num_rel_ret =0;
		
	}
	
	public  ArrayList<Double> metric_methods(String query, int map_k,int pr_k){
		ArrayList<Double> eval = new ArrayList<Double>();
		
		double MAP_a_q=0.0;
		double precisionAtX=0.0;
		double recallAtX=0.0;
		double precisionAtR=0.0;

		Set<String> truth_rel = truth.get(query);
		int num_of_true =  truth_rel.size();	
		int num_rel_retr = 0;
		for (int rank=0; rank< ranklist.size();rank++){	
			String docId= ranklist.get(rank);
			if(truth_rel.contains(docId)){
				num_rel_retr+=1;
				if(rank+1<=map_k){
					MAP_a_q += (double)num_rel_retr/(1+rank);
					}
				if(rank+1<=pr_k){
					precisionAtX+=1.0;
					recallAtX+=1.0;
					}
				if(rank+1<=num_of_true){
					precisionAtR+=1.0;
					}	
			}		
		}	
		
		eval.add(MAP_a_q/(num_of_true*1.0));
		if(pr_k < ranklist.size()){
		eval.add(precisionAtX/(pr_k*1.0));}else{
			eval.add(precisionAtX/ranklist.size());
		}
		eval.add(recallAtX/num_of_true);
		eval.add(precisionAtR/num_of_true);
		
		return eval;
	}
	
	public ArrayList<Double> getResult(){
		return this.result;
	}
	
	public  ArrayList <Double> printResult(){
		System.out.printf("MAP@%d is "+this.result.get(0)+"\n",this.map_k);
		System.out.printf("Precision@%d is "+this.result.get(1)+"\n",this.pr_k);
		System.out.printf("recall@%d is "+this.result.get(2)+"\n",this.pr_k);
		System.out.printf("Precision@R is "+this.result.get(3)+"\n");
		return this.result;
	}
	
   public int getNum_rel_retrieve(){
	   return this.num_rel_ret;
   }

}