package desperados.dvd.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.ramps.Ramp;

public class LaddersAndStairs extends Chunk {

	@Override
	public void readChunk() throws IOException {
		
		short numEntries = stream.readShort();
		List<Ramp> ramps = new ArrayList<Ramp>(numEntries);
		
		for (int i = 0; i < numEntries; i++) {
			short x1 = stream.readShort();
			short y1 = stream.readShort();
			short x2 = stream.readShort();
			short y2 = stream.readShort();
			short unk1 = stream.readShort();
			short unk2 = stream.readShort();
			short unk3 = stream.readShort();
			
			Ramp r = new Ramp(x1, y1, x2, y2, unk1, unk2, unk3);
			ramps.add(r);
		}
	}
}
