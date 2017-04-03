package Experiment;

import RetrievalSys.*;
import java.io.File;

/**
 * Experiment.java - the main class of prototype 2
 *                  this prototype is working with two methods: 1 - baseline:  BM25
 *                  2 -  Cluster Re-ranking
 */

public class Experiment {
    /**
     * run mode 1 uses sprtzer dataset
     * run mode 2 uses test200
     * run mode 3 uses users argument
     */
    static int runmodel=3;
    public static void main(String[] args) throws Exception{



        if ( args.length != 4 ) {
            System.err.println("Required params: <paragraph> <outlines> <qrels> <output>");
            return;
        }

        File paragraphFile = new File( args[0]);
        File outlineFile = new File( args[1]);
        File qrelsFile = new File( args[2]);
        File runfile = new File( args[3] );

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
        } else {

            String file_dir = qrelsFile.getParent();
            String indexPath = "indexfile/";
            Indexer.indexParas(paragraphFile,indexPath);
            Queries Q = new Queries(outlineFile);
            Searcher.searchEngine(Q, qrelsFile, runfile , indexPath, hitsPerPage, file_dir,MeanPrecision_at_k,Precision_at_k,lambda);
        }
    }
}
