package alone.in.deepspace;

import java.util.ArrayList;
import java.util.List;

public class StatsData {

	public List<Integer> 	values;
	public String 			label;

	public StatsData(String label) {
		this.label = label;
		this.values = new ArrayList<Integer>();
	}
	
	public void add(int value) {
		values.add(value);
	}

}
