package RetrievalSys;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import treccarData.Data;
import treccarReadData.DeserializeData;

public class Queries {

	private String query_file_name;
	private String query_file_dir;
	private String outlines;
	private File  outlinFile;
	private boolean useFile = false;

	public Queries(String q_f_name,  String q_f_dir ){
		this.query_file_dir=q_f_dir;
		this.query_file_name=q_f_name;
		this.outlines = this.query_file_dir+this.query_file_name;

	}
	public Queries(File outlineFile){
	    this.outlinFile = outlineFile;
	    useFile = true;
	}

	public ArrayList<SySQuery> readQueries() throws IOException{

		ArrayList<SySQuery> Q= new ArrayList<SySQuery>();

        final FileInputStream fileInputStream;
		if (useFile){
			fileInputStream = new FileInputStream(this.outlinFile);
		} else {
			fileInputStream = new FileInputStream(new File(outlines));
		}
		for(Data.Page page: DeserializeData.iterableAnnotations(fileInputStream)) {
			SySQuery q = new SySQuery(page.getPageId(),page.getPageName());
			Q.add(q);
		}
		return Q;
	}
}

