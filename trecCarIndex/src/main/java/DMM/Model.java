package DMM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Model{
	int K; 
	double alpha;
	double beta;
	
	String ParametersStr;
	int V; 
	int D; 
	int iterNum; 
	double alpha0;
	double beta0;
	double[][] phi_zv;
	int[] z; 
	int[] m_z; 
	int[][] n_zv;
	int[] n_z; 
	double smallDouble = 1e-150;
	double largeDouble = 1e150;
	Map<String, ArrayList<DmmDocument>> topic_clusters;

	public Model(int K, int V, int iterNum, double alpha, double beta, 
			 String ParametersStr)
	{
		this.topic_clusters= new HashMap<String, ArrayList<DmmDocument>>();
		this.ParametersStr = ParametersStr;
		this.alpha = alpha;
		this.beta = beta;
		this.K = K;
		this.V = V;
		this.iterNum = iterNum;
		this.alpha0 = K * alpha;
		this.beta0 = V * beta;
		
		this.m_z = new int[K];
		this.n_z = new int[K];
		this.n_zv = new int[K][V];
		for(int k = 0; k < K; k++){
			this.n_z[k] = 0;
			this.m_z[k] = 0;
			for(int t = 0; t < V; t++){
				this.n_zv[k][t] = 0;
			}
		}
	}
	public void intialize(DmmDocumentSet documentSet)
	{
		D = documentSet.D;
		z = new int[D];
		for(int d = 0; d < D; d++){
			DmmDocument document = documentSet.documents.get(d);
			int cluster = (int) (K * Math.random());
			z[d] = cluster;
			m_z[cluster]++;
			for(int w = 0; w < document.wordNum; w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				n_zv[cluster][wordNo] += wordFre; 
				n_z[cluster] += wordFre; 
			}
		}
	}
	public void gibbsSampling(DmmDocumentSet documentSet)
	{
		for(int i = 0; i < iterNum; i++){
			for(int d = 0; d < D; d++){
				DmmDocument document = documentSet.documents.get(d);
				int cluster = z[d];
				m_z[cluster]--;
				for(int w = 0; w < document.wordNum; w++){
					int wordNo = document.wordIdArray[w];
					int wordFre = document.wordFreArray[w];
					n_zv[cluster][wordNo] -= wordFre;
					n_z[cluster] -= wordFre;
				}

				cluster = sampleCluster(d, document);				
				z[d] = cluster;
				m_z[cluster]++;
				for(int w = 0; w < document.wordNum; w++){
					int wordNo = document.wordIdArray[w];
					int wordFre = document.wordFreArray[w];
					n_zv[cluster][wordNo] += wordFre; 
					n_z[cluster] += wordFre; 
				}
			}
		}
	}

	private int sampleCluster(int d, DmmDocument document)
	{ 
		double[] prob = new double[K];
		int[] overflowCount = new int[K];

		for(int k = 0; k < K; k++){
			prob[k] = (m_z[k] + alpha) / (D - 1 + alpha0);
			double valueOfRule2 = 1.0;
			int i = 0;
			for(int w=0; w < document.wordNum; w++){
				int wordNo = document.wordIdArray[w];
				int wordFre = document.wordFreArray[w];
				for(int j = 0; j < wordFre; j++){
					if(valueOfRule2 < smallDouble){
						overflowCount[k]--;
						valueOfRule2 *= largeDouble;
					}
					valueOfRule2 *= (n_zv[k][wordNo] + beta + j) 
							 / (n_z[k] + beta0 + i);
					i++;
				}
			}
			prob[k] *= valueOfRule2;			
		}
		
		reComputeProbs(prob, overflowCount, K);

		for(int k = 1; k < K; k++){
			prob[k] += prob[k - 1];
		}
		double thred = Math.random() * prob[K - 1];
		int kChoosed;
		for(kChoosed = 0; kChoosed < K; kChoosed++){
			if(thred < prob[kChoosed]){
				break;
			}
		}
		
		return kChoosed;
	}
	
	private void reComputeProbs(double[] prob, int[] overflowCount, int K)
	{
		int max = Integer.MIN_VALUE;
		for(int k = 0; k < K; k++){
			if(overflowCount[k] > max && prob[k] > 0){
				max = overflowCount[k];
			}
		}
		
		for(int k = 0; k < K; k++){			
			if(prob[k] > 0){
				prob[k] = prob[k] * Math.pow(largeDouble, overflowCount[k] - max);
			}
		}		
	}

	public void output(DmmDocumentSet documentSet) throws Exception
	{
		for(int d = 0; d < documentSet.D; d++){			
			DmmDocument para= documentSet.getDmmDocuments().get(d);
			int topic = z[d];
			para.topicTag=topic;
			ArrayList<DmmDocument> docsInCluster = topic_clusters.get(String.valueOf(topic));
			if (topic_clusters.containsKey(String.valueOf(topic))){
				
				docsInCluster.add(para);
			}else{
				ArrayList<DmmDocument> topicDocsArray = new ArrayList<DmmDocument>();
				topicDocsArray.add(para);
				topic_clusters.put((String.valueOf(topic)), topicDocsArray);
			}
	
		}
	}
	

	public Map<String, ArrayList<DmmDocument>> outputClusteringResult(DmmDocumentSet documentSet) throws Exception
	{    
//         for(String k: topic_clusters.keySet()){
//        	 System.out.println(k);
//        	 System.out.println(topic_clusters.get(k));
//        	 System.out.println();
//         }
		return topic_clusters;
	}
 
}

