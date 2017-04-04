package RetrievalSys;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import treccarData.Data;
import treccarReadData.DeserializeData;


/**
 * Queries.java - reads queries from the outlinefile.
 */

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

	public static ArrayList<SySQuery> getQueries_( Data.Section section) {
    if ( section.getChildSections().size() == 0 ) {
      ArrayList out = new ArrayList<SySQuery>();
      out.add( new SySQuery(section.getHeading(), section.getHeadingId()));
      return out;
    }

    ArrayList<SySQuery> out = new ArrayList<>();
    for (Data.Section s : section.getChildSections()) {
      for ( SySQuery q : getQueries_(s) ) {
        out.add( new SySQuery( section.getHeading() + " " + q.getQueryText(),
                            section.getHeadingId() + "/" + q.getQueryId()) );
      }
    }
    return out;
  }

  public static ArrayList<SySQuery> getQueries( File fin ) {

    ArrayList<SySQuery> out = new ArrayList<>();

    try {
			FileInputStream fstream = new FileInputStream( fin );
      for (Data.Page p : DeserializeData.iterableAnnotations(fstream)) {

          for (Data.Section s : p.getChildSections()) {
            for (SySQuery q : getQueries_(s)) {
              out.add( new SySQuery( p.getPageName() + " " + q.getQueryText(),
                                  p.getPageId() + "/" + q.getQueryId())) ;
            }
          }
      }
    } catch (Exception e) {System.err.println(e);}
      out.stream().parallel().forEach(q ->  { q.expandQuery(); System.err.println(q.getQueryText()); });
    return out;
  }

	public ArrayList<SySQuery> readQueries() throws IOException{
		final FileInputStream fstream;
		if (useFile){
			return getQueries( this.outlinFile );
		}
		return getQueries( new File( outlines ) );
	}
}
