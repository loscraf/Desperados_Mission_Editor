package desperados.dvd.chunk;

import java.io.IOException;

import desperados.dvd.DvdContainer;
import desperados.util.LittleEndianInputStream;

public abstract class Chunk {

	protected DvdContainer dvdContainer;
	protected LittleEndianInputStream stream;
	protected String filename;
	protected int blockSize;
	protected int version;

	public void initialize(LittleEndianInputStream stream, String filename, DvdContainer dvdContainer, String chunkId) throws IOException {
		this.dvdContainer = dvdContainer;
		this.stream = stream;
		this.filename = filename;
		blockSize = stream.readInt() - 4;
		version = stream.readInt();
	}

	public void readChunk() throws IOException {
		stream.skip(blockSize);
	}
}
