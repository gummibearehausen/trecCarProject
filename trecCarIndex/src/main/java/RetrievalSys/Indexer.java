package RetrievalSys;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import co.nstant.in.cbor.CborException;
import treccarData.Data;
import treccarReadData.DeserializeData;

public class Indexer {




    public static IndexWriter indexWriterCreator(String indexPath, int create_or_append) throws IOException{

        Analyzer analyzer = new StandardAnalyzer();

        // 1. create the index

        FSDirectory directory = FSDirectory.open(new File(indexPath));

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
        if (create_or_append ==0){config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);}else{
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);}
        config.setRAMBufferSizeMB(64);
        config.setMaxBufferedDocs(4000);
        IndexWriter w = new IndexWriter(directory, config);
        return w;

    }



    public static void addDoc(IndexWriter w, String content, String paraId, String entities) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("text", content, Field.Store.YES));

        // use a string field for isbn because we don't want it tokenized
        doc.add(new StringField("paraID", paraId, Field.Store.YES));
        doc.add(new StringField("entities", entities, Field.Store.YES));
        w.addDocument(doc);
    }
    public static void addCluster(IndexWriter w, String content, String topicId) throws IOException {
        Document cluster = new Document();
        cluster.add(new TextField("text", content, Field.Store.YES));

        // use a string field for isbn because we don't want it tokenized
        cluster.add(new StringField("topicID", topicId, Field.Store.YES));
        w.addDocument(cluster);
    }
    public static  void indexParas(String paragraphs,String indexPath) throws CborException, IOException{
        IndexWriter index_writer =indexWriterCreator( indexPath,0);
        final FileInputStream fileInputStream2 = new FileInputStream(new File(paragraphs));
        for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream2)) {
            String index_paraId = p.getParaId();
            String index_paraText = p.getTextOnly();
            String index_entities=String.join("\t", p.getEntitiesOnly());
            addDoc(index_writer,index_paraText,index_paraId,index_entities);
            System.out.println("Indexing: "+index_paraId);
        }
        index_writer.close();
    }

    public static  void indexParas(File paragraphsFile , String indexPath) throws CborException, IOException{
        IndexWriter index_writer =indexWriterCreator( indexPath,0);
        final FileInputStream fileInputStream2 = new FileInputStream(paragraphsFile);
        for(Data.Paragraph p: DeserializeData.iterableParagraphs(fileInputStream2)) {
            String index_paraId = p.getParaId();
            String index_paraText = p.getTextOnly();
            String index_entities=String.join("\t", p.getEntitiesOnly());
            addDoc(index_writer,index_paraText,index_paraId,index_entities);
            System.out.println("Indexing: " + index_paraId);
        }
        index_writer.close();
    }
}