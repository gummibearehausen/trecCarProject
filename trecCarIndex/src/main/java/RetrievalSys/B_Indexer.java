Bckage RetrievalSys;


        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;

        import org.apache.lucene.analysis.Analyzer;
        import org.apache.lucene.analysis.standard.StandardAnalyzer;
        import org.apache.lucene.document.Document;
        import org.apache.lucene.document.Field;
        import org.apache.lucene.document.StringField;
        import org.apache.lucene.document.TextField;
        import org.apache.lucene.index.IndexWriter;
        import org.apache.lucene.index.IndexWriterConfig;
        import org.apache.lucene.queryparser.classic.ParseException;
        import org.apache.lucene.store.FSDirectory;
        import org.apache.lucene.util.Version;

/**
 * B_Indexer.jave - collects, parses, and stores data to facilitate fast and accurate information retrieval.
 *                  uses Lucene toolkit
 * @param indexPath location of indexed data
 * @param dataPath location of data
 *
 */

public class B_Indexer {
    private  static String indexPath;
    private   static  String dataPath;



    public static IndexWriter indexWriterCreator(String indexPath, int create_or_append) throws IOException{

        Analyzer analyzer = new StandardAnalyzer();

        /**
         *  create the index
         */
        FSDirectory directory = FSDirectory.open(new File(indexPath));

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
        if (create_or_append == 0)
        {
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        } else {
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        }
        config.setRAMBufferSizeMB(64);
        config.setMaxBufferedDocs(4000);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        return indexWriter;
    }

    public static void addDoc(IndexWriter indexWriter, String content, String paraId, String entities) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("text", content, Field.Store.YES));
        /**
         * use a string field for isbn because we don't want it tokenize
         */
        doc.add(new StringField("paraID", paraId, Field.Store.YES));
        doc.add(new StringField("entities", entities, Field.Store.YES));
        indexWriter.addDocument(doc);
    }
}
