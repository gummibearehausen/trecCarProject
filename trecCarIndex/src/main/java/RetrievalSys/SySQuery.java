package RetrievalSys;

public class SySQuery {
	private String Query_id;
	private String Query_text;
	
	public SySQuery(String Q_id, String Q_text){
		this.Query_id = Q_id;
		this.Query_text = Q_text;
			
	}
	
	public String getQueryText(){
		return Query_text;
	}
	
	
	public String getQueryId(){
		return Query_id;
	}

}
