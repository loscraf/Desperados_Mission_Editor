package desperados.dvd.elements;

import java.util.List;

public class Elements {
	private Element[] elements;

	public Element[] getElements() {
		return elements;
	}

	public void setElements(List<Element> list) {
		if (list == null) return;
		this.elements = new Element[list.size()];
		this.elements = list.toArray(this.elements);
	}
}
