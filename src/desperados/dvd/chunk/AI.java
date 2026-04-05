package desperados.dvd.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.ai.*;

public class AI extends Chunk {

	@Override
	public void readChunk() throws IOException {
		
		int numElements = stream.readShort();
		
		List<AiEntry> entries = new ArrayList<AiEntry>(numElements);
		
		for (int i = 0; i < numElements; i++) {
			
			AiEntry entry = null;
			
			int type = stream.readByte() & 0xFF;
			
			switch (type) {
			case 0:
				entry = new AiEntry00(new AiPoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort()), stream.readShort());
				break;
			case 1:
				entry = new AiEntry01();
				int count = stream.readShort();
				for (int j = 0; j < count; j++) {
					((AiEntry01) entry).addPoint(new AiPoint(stream.readShort(), stream.readShort(), (short)0, (short)0));
				}
				count = stream.readShort();
				for (int j = 0; j < count; j++) {
					((AiEntry01) entry).addEntry(
						 new AiEntry00(new AiPoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort()), (short)(stream.readByte() & 0xFF))
					);
				}
				break;
			case 2:
				entry = new AiEntry02();
				((AiEntry02) entry).addPoint(new AiPoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort()));
				((AiEntry02) entry).addPoint(new AiPoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort()));
				break;
			case 3:
				entry = new AiEntry03();
				((AiEntry03) entry).addPoint(new AiPoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort()));
				break;
			case 4:
				entry = new AiEntry04(new AiPoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort()), stream.readShort());
				break;
			case 5:
				entry = new AiEntry05();
				count = stream.readShort();
				for (int j = 0; j < count; j++) {
					((AiEntry05) entry).addPoint(new AiPoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort()));
				}
				break;
			case 6:
				entry = new AiEntry06(new AiPoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort()), stream.readShort());
				break;
			case 7:
				entry = new AiEntry07();
				((AiEntry07) entry).addPoint(new AiPoint(stream.readShort(), stream.readShort(), (short)0, (short)0));
				((AiEntry07) entry).addPoint(new AiPoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort()));
				break;
			default:
				System.out.println("unknown type : " + type + "(@" + (stream.getPosition()-1) + ")");
				break;
			}
			
			entries.add(entry);
		}
		
		dvdContainer.setAI(entries);
	}
}
