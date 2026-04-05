package desperados.dvd.ai;

import java.util.ArrayList;
import java.util.List;

public class AiEntry01 extends AiEntry {

	public List<AiEntry00> list;

	public AiEntry01() {
		type = 1;
	}

	public void addEntry(AiEntry00 entry) {
		if (list == null) {
			list = new ArrayList<AiEntry00>();
		}
		list.add(entry);
	}

	@Override
	public String toString() {
		String str = super.toString();
		for (AiEntry00 e : list) {
			str += e.toString();
		}
		return str;
	}
}
