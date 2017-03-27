package RetrievalSys;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import treccarData.Data;

import treccarReadData.DeserializeData;


/**
 * Queries.jave - read all queries and add them to the query list.
 * Created by behzadb on 3/27/17.
 */
public class Queries {

    private String query_file_name;
    private String query_file_dir;
    private String outlines;
    public Queries(String q_f_name,  String q_f_dir ){
        this.query_file_dir=q_f_dir;
        this.query_file_name=q_f_name;
        this.outlines = this.query_file_dir + this.query_file_name;


    }
    public ArrayList<SySQuery> readQueries() throws IOException{
        ArrayList<SySQuery> sQueryArrayList= new ArrayList<SySQuery>();

        final FileInputStream fileInputStream = new FileInputStream(new File(outlines));
        for(Data.Page page: DeserializeData.iterableAnnotations(fileInputStream)) {
            SySQuery sQuery = new SySQuery(page.getPageId(),page.getPageName());
            sQueryArrayList.add(sQuery);
        }
        return sQueryArrayList;
    }
}
