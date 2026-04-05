package desperados.scb;

import java.util.HashMap;

public class ScbFunctionSignature {

	private static HashMap<String, int[]> signatures = new HashMap<String, int[]>();
	static {
		// Class StartUp
		signatures.put("InitializeStartUp", new int[]{2, 4, 8});
		signatures.put("Briefing", new int[]{2, 4, 4});
		signatures.put("HourGlass", new int[]{2, 4, 8});
		signatures.put("CheckVictoryCondition", new int[]{2, 4, 4});
		
		signatures.put("Initialize", new int[]{2, 4, 4});
		// Class <WAYS.Waypoint>
		signatures.put("ReachPoint", new int[]{2, 4, 8});
		// Class <ELEM.Actor>
		signatures.put("AIStateChange", new int[]{4, 4, 16});
		signatures.put("ActionChange", new int[]{3, 4, 12});
		signatures.put("FilterEvent", new int[]{3, 4, 12});
		// Class <ELEM.Item>
		signatures.put("Hit", new int[]{2, 4, 8});
		signatures.put("Stabbed", new int[]{2, 4, 8});
		signatures.put("Shooted", new int[]{2, 4, 8});
		signatures.put("Gazed", new int[]{2, 4, 8});
		signatures.put("Blinded", new int[]{2, 4, 8});
		signatures.put("Dagger", new int[]{2, 4, 8});
		signatures.put("Blasted", new int[]{2, 4, 8});
		signatures.put("Clicked", new int[]{2, 4, 8});
		signatures.put("ReallyBlasted", new int[]{2, 4, 8});
		// Class <SCRP.Zone>
		signatures.put("EnterZone", new int[]{2, 4, 8});
		signatures.put("ExitZone", new int[]{2, 4, 8});
	}

	static int[] lookUp(ScbClass scbClass, String functionName) {
		if (scbClass.getName().equals("StartUp")) {
			if (functionName.equals("Initialize")) {
				functionName = "InitializeStartUp";
			}
		}
		
		int[] values = signatures.get(functionName);
		if (values == null) {
			values = new int[]{2, 0, 4};
		}
		
		return values;
	}
}
