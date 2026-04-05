package desperados.scb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ScbOperand {

	public static enum Flag { NONE("none"), CLASS("class_var"), VOL("vol_var"), TEMP("var");
		private String name;
		
		private Flag(String name) {
			this.name = name;
		}
		
		public String toString(){
			return name;
		}
	}

	private Flag flag;
	private int value;
	
	private boolean isFloat;
	private float fvalue;

	public ScbOperand(byte b1, byte b2) {
		value = (b1 & 0xFF) | (b2 & 0xFF) << 8;
		setFlag();
		value = value >> 2;
	}

	public ScbOperand(byte b1, byte b2, byte b3, byte b4) {
		flag = Flag.NONE;
		value = (b1 & 0xFF) | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24;
	}

	public ScbOperand(byte b1, byte b2, byte b3, byte b4, boolean isFloat, boolean isParam) {
		if (isFloat) {
			this.isFloat = true;
			flag = Flag.NONE;
			
			byte[] bytes = new byte[]{b1, b2, b3, b4};
			fvalue = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();
			return;
		}
		
		/*
		if (isParam) {
			flag = Flag.NONE;
			value = (b1 & 0xFF) | (b2 & 0xFF) << 8 | (b3 & 0xFF) << 16 | (b4 & 0xFF) << 24;
			value = value >> 2;
		}
		*/
	}

	private void setFlag() {
		switch (value & 0xC000) {
		case 0x4000:
			flag = Flag.CLASS;
			break;
		case 0x8000:
			flag = Flag.VOL;
			break;
		case 0xC000:
			flag = Flag.TEMP;
			break;
		default:
			flag = Flag.NONE;
		}
		
		value = value & 0x3FFF;
	}

	public String toString() {
		if (flag == Flag.NONE) {
			if (isFloat) {
				return "" + fvalue + "f";
			}
			return "" + value;
		}
		return String.format("%s_%d", flag.toString(), value);
	}

	public int getValue() {
		return value;
	}
}
