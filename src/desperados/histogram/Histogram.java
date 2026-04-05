package desperados.histogram;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import desperados.dvd.elements.Accessory;
import desperados.dvd.elements.AccessoryExtra;
import desperados.dvd.elements.AccessoryExtraGattling;
import desperados.dvd.elements.Alive;
import desperados.dvd.elements.Animation;
import desperados.dvd.elements.Element;
import desperados.dvd.elements.Item;
import desperados.dvd.elements.NPC;

public class Histogram {

	private List<Short> accessoryHorseId = new ArrayList<Short>();
	private List<Short> accessoryS2 = new ArrayList<Short>();
	private List<Short> accessoryS3 = new ArrayList<Short>();
	private List<Short> accessoryS4 = new ArrayList<Short>();
	private List<Short> accessoryS5 = new ArrayList<Short>();
	private List<Short> accessoryS6 = new ArrayList<Short>();
	private List<Short> accessoryGattlingInfo1 = new ArrayList<Short>();
	private List<Short> accessoryGattlingInfo2 = new ArrayList<Short>();

	private List<Short> animationU1 = new ArrayList<Short>();
	private List<Byte> animationU2 = new ArrayList<Byte>();
	private List<Byte> animationU3 = new ArrayList<Byte>();
	private List<Byte> animationU4 = new ArrayList<Byte>();
	private List<Short> itemU5 = new ArrayList<Short>();
	private List<Short> itemU6 = new ArrayList<Short>();
	private List<Short> itemU7 = new ArrayList<Short>();
	private List<Integer> itemU8  = new ArrayList<Integer>();

	private List<Short> aliveU1 = new ArrayList<Short>();
	private List<Short> aliveU2 = new ArrayList<Short>();
	private List<Short> aliveU3 = new ArrayList<Short>();
	private List<Byte> aliveU4 = new ArrayList<Byte>();

	private List<Short> npcN2 = new ArrayList<Short>();
	private List<Byte> npcDrunkLevel = new ArrayList<Byte>();

	public void addAccessory(short horseId) {
		accessoryHorseId.add(horseId);
	}

	@SuppressWarnings("rawtypes")
	private Map<String, List>lists = new LinkedHashMap<String, List>();

	public  Histogram() {
		lists.put("Accessory\thorseId", accessoryHorseId);
		lists.put("Accessory\ts2", accessoryS2);
		lists.put("Accessory\ts3", accessoryS3);
		lists.put("Accessory\ts4", accessoryS4);
		lists.put("Accessory\ts5", accessoryS5);
		lists.put("Accessory\tgattlingInfo1", accessoryGattlingInfo1);
		lists.put("Accessory\tgattlingInfo2", accessoryGattlingInfo2);
		lists.put("Animation\tu1", animationU1);
		lists.put("Animation\tu2", animationU2);
		lists.put("Animation\tu3", animationU3);
		lists.put("Animation\tu4", animationU4);
		lists.put("Item\tu5", itemU5);
		lists.put("Item\tu6", itemU6);
		lists.put("Item\tu7", itemU7);
		lists.put("Item\tu8", itemU8);
		lists.put("Alive\tu1", aliveU1);
		lists.put("Alive\tu2", aliveU2);
		lists.put("Alive\tu3", aliveU3);
		lists.put("Alive\tu4", aliveU4);
		lists.put("NPC\tn2", npcN2);
		lists.put("NPC\tn3", npcDrunkLevel);
	}

	public void addElement(Element element) {
		if (element instanceof Accessory) {
			accessoryHorseId.add(((Accessory) element).getHorseId());
			AccessoryExtra extra = ((Accessory) element).getExtraInfo();
			if (extra != null) {
				accessoryS2.add(extra.getS2());
				accessoryS3.add(extra.getS3());
				accessoryS4.add(extra.getS4());
				accessoryS5.add(extra.getS5());
				accessoryS6.add(extra.getS6());
				AccessoryExtraGattling gattling = extra.getGattlingInfo();
				if (gattling != null) {
					accessoryGattlingInfo1.add(gattling.getGattlingInfo1());
					accessoryGattlingInfo2.add(gattling.getGattlingInfo2());
				}
			}
		} else if (element instanceof Item) {
			itemU5.add(((Item) element).getU5());
			itemU6.add(((Item) element).getU6());
			itemU7.add(((Item) element).getU7());
			itemU8.add(((Item) element).getU8());
		}
		if (element instanceof Animation) {
			animationU1.add(((Animation) element).getU1());
			animationU2.add(((Animation) element).getU2());
			animationU3.add(((Animation) element).getU3());
			animationU4.add(((Animation) element).getU4());
		} else if (element instanceof NPC) {
			npcN2.add(((NPC) element).getTiredness());
			npcDrunkLevel.add(((NPC) element).getDrunkLevel());
		}
		if (element instanceof Alive) {
			aliveU1.add(((Alive) element).getU1());
			aliveU2.add(((Alive) element).getU2());
			aliveU3.add(((Alive) element).getU3());
			aliveU4.add(((Alive) element).getU4());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getData() {
		String s = "";
		
		for (Map.Entry<String, List> entry : lists.entrySet()) {
			String id = entry.getKey();
			s += id + "\n";
			List<Number> list = entry.getValue();
			
			Map<Integer, Integer> histo = createHistogram(list);
			
			String k = "class:";
			String v = "amount:";
			SortedSet<Integer> keys = new TreeSet<Integer>(histo.keySet());
			for (int key : keys) { 
				int value = histo.get(key);
				k += "\t" + key;
				v += "\t" + value;
			}
			s += v + "\n" + k + "\n\n";
		}
		return s;
	}

	public Map<Integer, Integer> createHistogram(List<Number> list) {
		Map<Integer, Integer> histo = new TreeMap<Integer, Integer>();
		for (int i = 0; i < list.size(); i++) {
			int n = list.get(i).intValue();
			if (histo.containsKey(n)) {
				histo.put(n, histo.get(n) + 1);
			} else {
				histo.put(n, 1);
			}
		}
		return histo;
	}

	public void addElements(Element[] elements) {
		for (Element e : elements) {
			addElement(e);
		}
	}
}
