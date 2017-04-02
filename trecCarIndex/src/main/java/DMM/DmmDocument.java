package DMM;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DmmDocument implements Comparable<DmmDocument> {
	int[] wordIdArray;
	int[] wordFreArray;
	int wordNum = 0;
	int topicTag;
	int origin_rank;
	double new_rank;
	String docId;
	String docText;
	
	public DmmDocument(String text, String docId, int orign_rank, HashMap<String, Integer> wordToIdMap)
	{   
		this.docText=text;
		this.docId=docId;
		this.origin_rank=orign_rank;
		this.new_rank =0.0;
		int V = wordToIdMap.size();
		HashMap<Integer, Integer> wordFreMap = new HashMap<Integer, Integer>(); 
		StringTokenizer st = new StringTokenizer(text);
		String token;
		int tokenId;
		
		
		while(st.hasMoreTokens()){
			token = st.nextToken();
			if (!wordToIdMap.containsKey(token)) {
				tokenId = V++;
				wordToIdMap.put(token, tokenId);
			} else {
				tokenId = wordToIdMap.get(token);
			}
			
			if (!wordFreMap.containsKey(tokenId)){
				wordFreMap.put(tokenId, 1);
			}else{
				wordFreMap.put(tokenId, wordFreMap.get(tokenId) + 1);
			}
		}
		
		wordNum = wordFreMap.size();
		wordIdArray = new int[wordNum];
		wordFreArray = new int[wordNum];
		int w = 0;
		for(Map.Entry<Integer, Integer> word: wordFreMap.entrySet()){
			wordIdArray[w] = word.getKey();
			wordFreArray[w] = word.getValue();
			w++;
		}
	}
	public void setNewRanking(double rank){
		this.new_rank=rank;
	}
	public double getNewRanking(){
		return this.new_rank;
	}
	public void setTopicTag(int topic){
		topicTag = topic;
	}
	public int getTopicTag(){
		return topicTag;
	}
	public String getParaId(){
		return docId;
	}
	
	public String getParaText(){
		return docText;
	}
	public int getOriginRank(){
		return origin_rank;
	}
	
	// hao xiang mei yong
	public int compareTo(DmmDocument Dmmdoc) {

		Double compareNewRanking = Dmmdoc.getNewRanking();

		//ascending order
		if (this.new_rank>compareNewRanking){
			return 1;
		}else if (this.new_rank==compareNewRanking){
			return 0;
		}else{
			return -1;
		}

		//descending order
		//return compareQuantity - this.quantity;

	}
//	public static Comparator<DmmDocument> DmmDocumentComparator
//    = new Comparator<DmmDocument>(){
////	public static Comparator<DmmDocument> DmmDocumentNewRankingComparator= new Comparator<DmmDocument>(){
//		@Override
//	    public int compare(DmmDocument DmmDoc1, DmmDocument DmmDoc2) {
//
//	      Double DmmNewRank1 = DmmDoc1.getNewRanking();
//	      Double  DmmNewRank2 =  DmmDoc2.getNewRanking();
//
//	      //ascending order
//	      return Double.compareTo(DmmNewRank2);
//	    }
//	};




	
}

