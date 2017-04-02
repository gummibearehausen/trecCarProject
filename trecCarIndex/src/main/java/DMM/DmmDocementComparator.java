package DMM;

import java.util.Comparator;

public class DmmDocementComparator implements Comparator<DmmDocument> {

	@Override
	public int compare(DmmDocument o1, DmmDocument o2) {
		// TODO Auto-generated method stub
		
		return (int)o1.getNewRanking()-(int)o2.getNewRanking();
	}

}
