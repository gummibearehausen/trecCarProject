package DMM;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;

public class DMM
{
	int K;
	double alpha;
	double beta;
	int iterNum;
	ArrayList<Document> dataset;
	
	HashMap<String, Integer> wordToIdMap;
	int V;
	DmmDocumentSet documentSet;
	Map<String, ArrayList<DmmDocument>>  topic_clusters;
	
	public DMM(int K, double alpha, double beta, int iterNum, ArrayList<Document> dataset)
	{
		this.K = K;
		this.alpha = alpha;
		this.beta = beta;
		this.iterNum = iterNum;
		this.dataset = dataset;
		this.wordToIdMap = new HashMap<String, Integer>();
		this.topic_clusters= new HashMap<String, ArrayList<DmmDocument>>();
		
	}
	public  void getTopicModeling() throws Exception
	{
		int K = 50;
		double alpha = 0.1;
		double beta = 0.2;
		int iterNum = 10;
		
			DMM gsdmm = new DMM(K, alpha, beta, iterNum,dataset);
			
			long startTime = System.currentTimeMillis();				
			gsdmm.getDocuments();
			long endTime = System.currentTimeMillis();
			System.out.println("getDocuments Time Used:" + (endTime-startTime)/1000.0 + "s");
			
			startTime = System.currentTimeMillis();	
			gsdmm.runGSDMM();
			
			endTime = System.currentTimeMillis();
			System.out.println("gibbsSampling Time Used:" + (endTime-startTime)/1000.0 + "s");
			
		
	}
	
	public void getDocuments() throws Exception{
		documentSet = new DmmDocumentSet(dataset, wordToIdMap);
		V = wordToIdMap.size();
	}
	
	public void runGSDMM() throws Exception
	{
		String ParametersStr = "K"+K+"iterNum"+ iterNum +"alpha" + String.format("%.3f", alpha)
								+ "beta" + String.format("%.3f", beta);
		Model model = new Model(K, V, iterNum,alpha, beta,   ParametersStr);
		model.intialize(documentSet);
		model.gibbsSampling(documentSet);
		model.output(documentSet);
		this.topic_clusters = model.topic_clusters;
		showTopicModelingResult(topic_clusters);
		
	}
   public Map<String, ArrayList<DmmDocument>> getTopicCluster(){
	   return this.topic_clusters;
   }
   public void showTopicModelingResult(Map<String, ArrayList<DmmDocument>> topic_clusters ){
	   int topic_reassign_id=1;
	   for(String k:topic_clusters.keySet()){
		   System.out.println("-----"+topic_reassign_id+"------");
		   topic_reassign_id+=1;
		   int num_of_doc = 1;
		   
		   for(DmmDocument doc: topic_clusters.get(k)){
			   System.out.println(num_of_doc +" "+doc.getParaIext());
			   num_of_doc+=1;
		   }
		   System.out.println();
	   }
   }
}
