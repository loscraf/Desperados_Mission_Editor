package desperados.util;

public class Bitmap {
	
	public static byte[] getHeader(int width, int height) {
		byte[] header = new byte[0x36];
		
		int rowSize = ((8 * 3 * width + 31) / 32) * 4;
		int size = height * rowSize + 54;
		
		header[0] = 'B';
		header[1] = 'M';
		header[10] = 54;
		header[14] = 40;
		header[26] = 1;
		header[28] = 24;
		
		header[2] = (byte)  (size & 0xFF);
		header[3] = (byte) ((size & 0xFF00) >> 8);
		header[4] = (byte) ((size & 0xFF0000) >> 16);
		header[5] = (byte)  (size >> 24);
		
		header[18] = (byte)  (width & 0xFF);
		header[19] = (byte) ((width & 0xFF00) >> 8);
		header[20] = (byte) ((width & 0xFF0000) >> 16);
		header[21] = (byte)  (width >> 24);
		
		header[22] = (byte)  (height & 0xFF);
		header[23] = (byte) ((height & 0xFF00) >> 8);
		header[24] = (byte) ((height & 0xFF0000) >> 16);
		header[25] = (byte)  (height >> 24);
		
		return header;
	}
}
