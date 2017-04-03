package RetrievalSys;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

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


public class Searcher {
    private Directory directory;
    private DirectoryReader reader;
    private IndexSearcher searcher;
    private String IndexPath;
    private int hitsPerPage;
    private String query_text;

    public Searcher(String IndexPath, String query_text, int hitsPerPage) throws IOException{
        this.IndexPath= IndexPath;
        this.directory=FSDirectory.open(new File (IndexPath));
        this.reader=DirectoryReader.open(directory);
        this.searcher=new IndexSearcher(reader);
        this.hitsPerPage=hitsPerPage;

    }
    //
    public void general_searcher() throws IOException, ParseException{
        Analyzer analyzer = new StandardAnalyzer();

        int modelNum = 2;
//      Query q = new QueryParser("Content", analyzer).parse(querystr);

        // 3. search


        Directory directory = FSDirectory.open(new File (IndexPath));
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        if(modelNum == 2){
            searcher.setSimilarity(new BM25Similarity());
        }else if(modelNum ==2){
            MySimilarity similarity = new MySimilarity(new DefaultSimilarity());
            searcher.setSimilarity(similarity);
        }



        Query q = new QueryParser("text", analyzer).parse(query_text);

        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;


    }

    public static void searchEngine(Queries queries, String eval_f_name, String IndexPath, int engine_hitsPerPage, String eval_file_dir,int MAP_at_k, int PR_at_k, double lambda) throws Exception{



        Analyzer engine_analyzer = new StandardAnalyzer();

        int engine_modelNum = 2;
//      Query q = new QueryParser("Content", analyzer).parse(querystr);

        // 3. search


        Directory engine_directory = FSDirectory.open(new File (IndexPath));
        DirectoryReader engine_reader = DirectoryReader.open(engine_directory);
        IndexSearcher searcher = new IndexSearcher(engine_reader);
        if(engine_modelNum == 2){
//      	searcher.setSimilarity(new BM25Similarity());
            searcher.setSimilarity(new BM25Similarity());
        }else if(engine_modelNum ==1){
            MySimilarity similarity = new MySimilarity(new DefaultSimilarity());
            searcher.setSimilarity(similarity);
        }
        Map<String, ArrayList<String>> ranklistByquery = new HashMap<String, ArrayList<String>>();
        Map<String, ArrayList<String>> ranklistByquery2 = new HashMap<String, ArrayList<String>>();
        for(SySQuery s :queries.readQueries()){
            Query q = new QueryParser("text", engine_analyzer).parse(s.getQueryText());
            String queryId = s.getQueryId();
            String query_text = s.getQueryText();
            TopDocs engine_docs = searcher.search(q, engine_hitsPerPage);
            ScoreDoc[] engine_hits = engine_docs.scoreDocs;


            // 4. display results
            System.out.printf("\nquery: %s \n",s.getQueryText());
            System.out.println("Found " + engine_hits.length + " hits.");

            SearchResult result = new SearchResult( query_text,engine_hits,searcher,lambda) ;

            ArrayList<String> ranklist =result.getRankDocId();
            ranklistByquery.put(queryId, ranklist);
            WriteSearchResultIntoFile(ranklistByquery,"runfile_tempResultBM25");

            ArrayList<String> ranklist2 =result.getNewRankDocId();

            ranklistByquery2.put(queryId, ranklist2);
            WriteSearchResultIntoFile(ranklistByquery2,"runfile_clusterRanking");
        }
        //evaluation

        int laura=1;
        if(laura==1){
            EvalReadfile result = new EvalReadfile(eval_file_dir,eval_f_name,ranklistByquery,MAP_at_k,PR_at_k);
            EvalReadfile result2 = new EvalReadfile(eval_file_dir,eval_f_name,ranklistByquery2,MAP_at_k,PR_at_k);
            System.out.println();
            System.out.println("retrieval result of BM25 ( k1 = 1.2, b = 0.75):");
            result.printEval();
            System.out.println();
            System.out.println("retrieval Cluster Ranking: ");
            result2.printEval();
        }else{
            EvalReadfileLaura result = new EvalReadfileLaura(eval_file_dir,eval_f_name,"runfile_tempResultBM25",MAP_at_k,PR_at_k);
            EvalReadfileLaura result2 = new EvalReadfileLaura(eval_file_dir,eval_f_name,"runfile_clusterRanking",MAP_at_k,PR_at_k);

            System.out.println("retrieval result of BM25 ( k1 = 1.2, b = 0.75):");
            result.printEval();
            System.out.println();
            System.out.println("retrieval Cluster Ranking: ");
            result2.printEval();
        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        engine_reader.close();
    }
    public static void searchEngine(Queries queries, File qrels, String IndexPath, int engine_hitsPerPage, String eval_file_dir,int MAP_at_k, int PR_at_k, double lambda) throws Exception{



        Analyzer engine_analyzer = new StandardAnalyzer();

        int engine_modelNum = 2;
//      Query q = new QueryParser("Content", analyzer).parse(querystr);

        // 3. search


        Directory engine_directory = FSDirectory.open(new File (IndexPath));
        DirectoryReader engine_reader = DirectoryReader.open(engine_directory);
        IndexSearcher searcher = new IndexSearcher(engine_reader);
        if(engine_modelNum == 2){
//      	searcher.setSimilarity(new BM25Similarity());
            searcher.setSimilarity(new BM25Similarity());
        }else if(engine_modelNum ==1){
            MySimilarity similarity = new MySimilarity(new DefaultSimilarity());
            searcher.setSimilarity(similarity);
        }
        Map<String, ArrayList<String>> ranklistByquery = new HashMap<String, ArrayList<String>>();
        Map<String, ArrayList<String>> ranklistByquery2 = new HashMap<String, ArrayList<String>>();
        for(SySQuery s :queries.readQueries()){
            Query q = new QueryParser("text", engine_analyzer).parse(s.getQueryText());
            String queryId = s.getQueryId();
            String query_text = s.getQueryText();
            TopDocs engine_docs = searcher.search(q, engine_hitsPerPage);
            ScoreDoc[] engine_hits = engine_docs.scoreDocs;


            // 4. display results
            System.out.printf("\nquery: %s \n",s.getQueryText());
            System.out.println("Found " + engine_hits.length + " hits.");

            SearchResult result = new SearchResult( query_text,engine_hits,searcher,lambda) ;

            ArrayList<String> ranklist =result.getRankDocId();
            ranklistByquery.put(queryId, ranklist);
            WriteSearchResultIntoFile(ranklistByquery,"runfile_tempResultBM25");

            ArrayList<String> ranklist2 =result.getNewRankDocId();

            ranklistByquery2.put(queryId, ranklist2);
            WriteSearchResultIntoFile(ranklistByquery2,"runfile_clusterRanking");
        }
        //evaluation

        int laura=1;
        if(laura==1){
            EvalReadfile result = new EvalReadfile(eval_file_dir,qrels,ranklistByquery,MAP_at_k,PR_at_k);
            EvalReadfile result2 = new EvalReadfile(eval_file_dir,qrels,ranklistByquery2,MAP_at_k,PR_at_k);
            System.out.println();
            System.out.println("retrieval result of BM25 ( k1 = 1.2, b = 0.75):");
            result.printEval();
            System.out.println();
            System.out.println("retrieval Cluster Ranking: ");
            result2.printEval();
        }else{
            EvalReadfileLaura result = new EvalReadfileLaura(eval_file_dir,qrels,"runfile_tempResultBM25",MAP_at_k,PR_at_k);
            EvalReadfileLaura result2 = new EvalReadfileLaura(eval_file_dir,qrels,"runfile_clusterRanking",MAP_at_k,PR_at_k);

            System.out.println("retrieval result of BM25 ( k1 = 1.2, b = 0.75):");
            result.printEval();
            System.out.println();
            System.out.println("retrieval Cluster Ranking: ");
            result2.printEval();
        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        engine_reader.close();
    }

    public static void WriteSearchResultIntoFile(Map<String, ArrayList<String>> DocRanklistQueries, String WriteFileName) throws IOException{
        String WriteFileDir ="tempSearchResult/";
        BufferedWriter writer = new BufferedWriter( new FileWriter( WriteFileDir+WriteFileName));
        for(String QueryAskey:DocRanklistQueries.keySet()){
            ArrayList<String> DocRankIdPerQuery = DocRanklistQueries.get(QueryAskey);
            int docRank=1;
            for(String DocParaId:DocRankIdPerQuery){
                String WriteString =QueryAskey+"\t"+docRank+"\t"+DocParaId+1.0/docRank+"BBTeam"+"\n";
                docRank+=1;
                writer.write( WriteString);
            }
        }
        writer.close();
    }


    public static Map<String, ArrayList<String>> ReadSearchResult(String WriteFileName) throws IOException{
        Map<String, ArrayList<String>>rankDocListQuies= new HashMap<String, ArrayList<String>>();
        String WriteFileDirRead ="tempSearchResult/";
        InputStream is = new FileInputStream(new File(WriteFileDirRead+WriteFileName));
        BufferedReader bufferReading = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line =bufferReading.readLine()) != null){
//				System.out.println(line);
            String[] ParsedLine= line.trim().split("\t");
//				System.out.println(ParsedLine);
            String queryId = ParsedLine[0];
            int doc_rank = Integer.valueOf(ParsedLine[1]);
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
