package RetrievalSys;

import java.util.HashSet;
import java.lang.StringBuilder;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.dictionary.Dictionary;
import java.util.Random;

import entity.*;


public class SySQuery {
	private String Query_id;
	private String Query_text;
	private static Dictionary dict;
	public static boolean WORDNET_EXPAND_QUERY = false;
	public static boolean KB_EXPAND_QUERY = false;
	private static Random rand = new Random();

	static {
		try {
				System.err.println("Loading WordNet Dictionary");
				dict = Dictionary.getDefaultResourceInstance();
				System.err.println("WordNet Dictionary Loaded");
		} catch (Exception e) {
				dict = null;
		}


	}

	public SySQuery(String Q_text, String Q_id){
		this.Query_id = Q_id;
		this.Query_text = Q_text;

	}

	public void expandQuery() {
		if ( WORDNET_EXPAND_QUERY )
			wordnetExpandQuery();
		if ( KB_EXPAND_QUERY )
			kbExpandQuery();
		Query_text = Query_text.replaceAll("[/:#]", " ");
	}

	public String getQueryText(){
		return Query_text;
	}


	public String getQueryId(){
		return Query_id;
	}

	private void wordnetExpandQuery() {
		HashSet<String> terms = new HashSet<String>();
		IndexWordSet iws1, iws2, iws3, iws = null;
		String parts[] = Query_text.split("\\s+");
    for ( int i = 0; i < parts.length - 3; i ++) {
      StringBuilder sb = new StringBuilder(parts[i]);
			iws1 = null; iws2 = null; iws3 = null;
			try { iws1 = dict.lookupAllIndexWords(sb.toString()); } catch ( Exception e){ iws1 = null; }
      sb.append(" " + parts[i+1]);
      try { iws2 = dict.lookupAllIndexWords(sb.toString()); } catch ( Exception e){ iws2 = null; }
      sb.append(" " + parts[i+2]);
      try { iws3 = dict.lookupAllIndexWords(sb.toString()); } catch ( Exception e){ iws3 = null; }
      sb.append(" ");

      if ( iws3 != null ) {
				iws = iws3;
				break;
      } else if ( iws2 != null ) {
				iws = iws2;
				break;
      } else if ( iws1 != null ) {
				iws = iws1;
				break;
      }
    }

		if ( iws == null )
			return;

    StringBuilder sb = new StringBuilder();
    terms.add(Query_text);

    for (net.sf.extjwnl.data.IndexWord word : iws.getIndexWordCollection()) {
      for ( net.sf.extjwnl.data.Synset syn : word.getSenses() ) {
        for (net.sf.extjwnl.data.Word w : syn.getWords()) {
          terms.add( w.getLemma().toLowerCase() );
        }
      }
    }

    for (String s : terms) {
      // if ( rand.nextDouble() < 0.2 ) {
        sb.append(s); sb.append(" ");
      // }
    }

    this.Query_text = sb.toString();
	}


	private void kbExpandQuery( ) {
    String parts[] = Query_text.split("\\s+");
    for ( int i = 0; i < parts.length - 3; i ++) {
      StringBuilder sb = new StringBuilder(parts[i]);
      EntityDict.EntityResult unigram = EntityHelper.entityDict.lookup(sb.toString());
      sb.append(" " + parts[i+1]);
      EntityDict.EntityResult bigram = EntityHelper.entityDict.lookup(sb.toString());
      sb.append(" " + parts[i+2]);
      EntityDict.EntityResult trigram = EntityHelper.entityDict.lookup(sb.toString());
      sb.append(" ");

      if ( trigram != null ) {
        parts[i] = sb.toString() + trigram.getKBNode().toShortString();
        parts[i+1] = "";
        parts[i+2] = "";
        i += 2;
        break;
      } else if ( bigram != null ) {
        parts[i] = sb.toString() + bigram.getKBNode().toShortString();
        parts[i+1] = "";
        i ++;
        break;
      } else if ( unigram != null ) {
        parts[i] = sb.toString() + unigram.getKBNode().toShortString();
        break;
      }
    }

    // System.err.print(query + "  ==>  ");
    Query_text = String.join( " ", parts ).replaceAll("[\\(\\)\\\\/#:]|:\\w+:", "");
    // System.err.println( query );
  }

}
