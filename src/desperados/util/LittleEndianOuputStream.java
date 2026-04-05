package desperados.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class LittleEndianOuputStream {

	private ByteArrayOutputStream stream;

	public LittleEndianOuputStream() throws IOException {
		this.stream = new ByteArrayOutputStream();
	}

	private ByteBuffer getByteBuffer(int size) throws IOException {
		ByteBuffer buf = ByteBuffer.allocate(size);
		buf.order(ByteOrder.LITTLE_ENDIAN);
		return buf;
	}

	public void writeInt(int value) throws IOException {
		stream.write(getByteBuffer(4).putInt(value).array());
	}

	public void writeShort(int value) throws IOException {
		stream.write(getByteBuffer(2).putShort((short) value).array());
	}

	public void writeByte(int value) throws IOException {
		stream.write((byte)value);
	}

	public void writeFloat(Float value) throws IOException {
		stream.write(getByteBuffer(4).putFloat(value).array());
	}

	public void writeBytes(byte[] bytes) throws IOException {
		stream.write(bytes);
	}

	public void writeString(String string) throws IOException {
		writeShort((short)string.length());
		writeBytes(string.getBytes());
	}

	public void writeString2(String string) throws IOException {
		writeBytes(string.getBytes());
	}

	public void writeString(String string, int size) throws IOException {
		byte[] b = new byte[size];
		byte[] s = string.getBytes();
		int length = string.length();
		if (length >= size) length = size - 1;
		System.arraycopy(s, 0, b, 0, length);
	}

	public byte[] getBytes() {
		return stream.toByteArray();
	}
}
