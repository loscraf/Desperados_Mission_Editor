package desperados.dvd.chunk;

import java.io.IOException;
import java.util.Locale;

public class Move extends Chunk  {

	private void readChunk2() throws IOException {
		
		short numUnknown1 = stream.readShort();
		System.out.println(numUnknown1 + "\t# numUnknown1\n");
		
		short numBlocksTotal = stream.readShort();
		System.out.println(numBlocksTotal + "\t# numBlocksTotal\n");
		
		short numHeaderBlocks = stream.readShort();
		System.out.println(numHeaderBlocks + "\t# numHeaderBlocks");
		
		for (int i = 0; i < numHeaderBlocks; i++) {
			
			if (i != 0) {
				int seperator = stream.readInt();
				assert (seperator == 0);
			}
			
			short numCoords = stream.readShort();
			System.out.println("\n" + numCoords + "\t# map borders");
			
			for (int j = 0; j < numCoords; j++) {
				short x = stream.readShort();
				short y = stream.readShort();
				System.out.println(x + ", " + y);
			}
		}
		
		short numUnknownCoords = stream.readShort();
		System.out.println("\n" + numUnknownCoords + "\t# some lines"); // maybe pathfinding hints?
		
		for (int i = 0; i < numUnknownCoords; i++) {
			short a = stream.readShort();
			short b = stream.readShort();
			short c = stream.readShort();
			short d = stream.readShort();
			System.out.println("line " + a + ", " + b + ", " + c + ", " + d);
		}
		
		short numDataBlocks = stream.readShort();
		System.out.println("\n" + numDataBlocks + "\t# blocked areas");
		
		assert (numBlocksTotal == numHeaderBlocks + numDataBlocks);
		
		for (int i = 0; i < numDataBlocks; i++) {
			short numCoords = stream.readShort();
			System.out.println("\n" + numCoords + "\t# numCoords");
			
			for (int j = 0; j < numCoords; j++) {
				short x = stream.readShort();
				short y = stream.readShort();
				System.out.println(x + ", " + y);
			}
		}
		
		for (int h = 0; h < numUnknown1 - 1; h++) {
			
			short numAreasTotal = stream.readShort();
			System.out.println("\n" + numAreasTotal + "\t# numAreasTotal");
			
			short numAreasGrouped = stream.readShort();
			System.out.println("\n" + numAreasGrouped + "\t# numAreasGrouped");
			
			if (numAreasTotal == 0) {
				break;
			}
			
			for (int i = 0; i < numAreasGrouped; i++) {
				short numCoords = stream.readShort();
				System.out.println("\n" + numCoords + "\t# numCoords (walkable area above ground level)");
				
				for (int j = 0; j < numCoords; j++) {
					short x = stream.readShort();
					short y = stream.readShort();
					System.out.println(x + ", " + y);
				}
				
				short extra1 = stream.readShort();
				short extra2 = stream.readShort();
				System.out.println(extra1 + ", " + extra2 + " # extra");
				
				for (int j = 0; j < extra2; j++) {
					short numCoords2 = stream.readShort();
					System.out.println(numCoords2 + "\t# numCoords (subtracted area)");
					
					for (int k = 0; k < numCoords2; k++) {
						short x = stream.readShort();
						short y = stream.readShort();
						System.out.println(x + ", " + y);
					}
				}
			}
		
		}
		
		short numUnknown2b = stream.readShort();
		System.out.println("\n" + numUnknown2b + "\t# numUnknown2b");
		
		for (int i = 0; i < numUnknown2b; i++) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			System.out.println(String.format(Locale.US, "%f, %f", x, y));
		}
		
		
		short s1 = stream.readShort();
		short s2 = stream.readShort();
		System.out.println(s1 + ", " + s2 + " # s1, s2");
		
		int[] counter = new int[]{23,3,1,0,0,0,0,0,0,0};
		
		for (int h = 0; h < 3; h++) {
			
			System.out.println("\n# next block");
			System.out.println("pos: " + Integer.toHexString(stream.getPosition()));
			
			short s3 = stream.readShort();
			short s4 = stream.readShort();
			System.out.println(s3 + ", " + s4 + " # s1, s2");
			
			for (int i = 0; i < counter[h]; i++) {
				short numCoords = stream.readShort();
				for (int j = 0; j < numCoords; j++) {
					stream.skip(6);
					short x = stream.readShort();
					short y = stream.readShort();
					System.out.println(x + ", " + y);
					stream.skip(8);
					short numIndex = stream.readShort();
					stream.skip(numIndex * 2);
				}
				System.out.println();
			}
		}
		
		for (int k = 0; k < 100; k++) {
			short x = stream.readShort();
			short y = stream.readShort();
			System.out.println(x + ", " + y);
		}
		
		
		
		/*
		System.out.println(Integer.toHexString(stream.getPosition()));
		short numUnknown2c = stream.readShort();
		System.out.println(numUnknown2c + "\t# numUnknown2c");
		
		for (int i = 0; i < numUnknown2c; i++) {
			short numCoords = stream.readShort();
			System.out.println("\n" + numCoords + "\t# numCoords");
			
			for (int j = 0; j < numCoords; j++) {
				short x = stream.readShort();
				short y = stream.readShort();
				System.out.println(x + ", " + y);
			}
		}
		
		System.out.println(Integer.toHexString(stream.getPosition()));
		short numUnknown2d = stream.readShort();
		System.out.println("\n" + numUnknown2d + "\t# numUnknown2d");
		
		System.out.println(Integer.toHexString(stream.getPosition()));
		short numUnknown2e = stream.readShort();
		System.out.println(numUnknown2e + "\t# numUnknown2e");
		
		System.out.println(Integer.toHexString(stream.getPosition()));
		short numFloatCoords = stream.readShort();
		System.out.println("\n" + numFloatCoords + "\t# numFloatCoords");
		*/
		
		/*
		
		for (int i = 0; i < numFloatCoords; i++) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			System.out.println(String.format(Locale.US, "%f, %f", x, y));
		}
		
		short numExtraBytes = stream.readShort();
		System.out.println("\n" + numExtraBytes + "\t# numExtraBytes");
		
		short numStrangeBlocks = stream.readShort();
		System.out.println("\n" + numStrangeBlocks + "\t# numStrangeBlocks");
		
		for (int i = 0; i < 2; i++) {
			
			if (i == 1) stream.readShort();
			
			short numStrangeSubBlocks = stream.readShort();
			System.out.println("\n" + numStrangeSubBlocks + "\t# numStrangeSubBlocks");
			
			for (int j = 0; j < numStrangeSubBlocks; j++) {
				
				short numStrangeEntries = stream.readShort();
				System.out.print("\n" + numStrangeEntries + "\t# numStrangeEntries");
				
				for (int k = 0; k < numStrangeEntries; k++) {
					short a = stream.readShort();
					System.out.print("\n" + a + ", ");
					for (int e = 0; e < numExtraBytes; e++) {
						byte b = stream.readByte();
						System.out.print(b + ", ");
					}
					short c1 = stream.readShort();
					short c2 = stream.readShort();
					short c3 = stream.readShort();
					short c4 = stream.readShort();
					short c5 = stream.readShort();
					short c6 = stream.readShort();
					short numD = stream.readShort();
					System.out.print(String.format(
							"%d, %d, %d, %d, %d, %d, %d: ",
							c1, c2, c3, c4, c5, c6, numD));
					
					for (int dCounter = 0; dCounter < numD; dCounter++) {
						short d = stream.readShort();
						if (dCounter != 0) System.out.print(", ");
						System.out.print(d);
					}
				}
			}
		}
		
		short numUnknown4 = stream.readShort();
		System.out.println("\n\n" + numUnknown4 + "\t# numUnknown4");
		
		short numUnknown5 = stream.readShort();
		System.out.print("\n" + numUnknown5 + "\t# numUnknown5");
		
		for (int i = 0; i < numUnknown5; i++) {
			short a1 = stream.readShort();
			short a2 = stream.readShort();
			short a3 = stream.readShort();
			short a4 = stream.readShort();
			short a5 = stream.readShort();
			short a6 = stream.readShort();
			short a7 = stream.readShort();
			short a8 = stream.readShort();
			float f = stream.readFloat();
			short numB = stream.readShort();
			System.out.print(String.format(Locale.US,
					"\n%d, %d, %d, %d, %d, %d, %d, %d, %f, %d: ",
					a1, a2, a3, a4, a5, a6, a7, a8, f, numB));
			
			for (int bCounter = 0; bCounter < numB; bCounter++) {
				short b = stream.readShort();
				if (bCounter != 0) System.out.print(", ");
				System.out.print(b);
			}
		}
		
		short numUnknown6 = stream.readShort();
		System.out.println("\n\n" + numUnknown6 + "\t# numUnknown6");
		
		for (int i = 0; i < numUnknown6; i++) {
			byte a1 = stream.readByte();
			if (a1 == -1) {
				System.out.println(a1);
				continue;
			}
			byte a2 = stream.readByte();
			short a3 = stream.readShort();
			byte a4 = stream.readByte();
			short a5 = stream.readShort();
			byte a6 = stream.readByte();
			System.out.println(String.format(
					"%d, %d, %d, %d, %d, %d",
					a1, a2, a3, a4, a5, a6));
		}
		
		System.out.println("I'm here: " + stream.getPosition());
		
		stream.close();
		System.exit(0);
		*/
		
		/*
		short numA02 = stream.readShort();
		short numB07 = stream.readShort();
		
		short numC03 = stream.readShort();
		
		for (int i = 0; i < numC03; i++) {
			short numLines = stream.readShort();
			
			for (int j = 0; j < numLines; j++) {
				short x = stream.readShort();
				short y = stream.readShort();
				System.out.println(x + "\t" + y);
			}
			if (i < numC03 - 1) {
				stream.readInt();
			}
			System.out.println();
		}
		
		stream.close();
		System.exit(0);
		
		stream.readShort();
		
		short numD04 = stream.readShort();
		
		for (int i = 0; i < numD04; i++) {
			short numLines = stream.readShort();
			
			for (int j = 0; j < numLines; j++) {
				short x = stream.readShort();
				short y = stream.readShort();
				System.out.println(x + "\t" + y);
			}
			System.out.println();
		}
		
		stream.readShort();
		
		stream.close();
		System.exit(0);
		*/
	}
}
