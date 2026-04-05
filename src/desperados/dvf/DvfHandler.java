package desperados.dvf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import desperados.exception.DvfReadException;
import desperados.ui.EditorWindow;

public class DvfHandler {

	private Map<String, DvfHeader> dvfFiles = new HashMap<>();

	public DvfHeader getDvf(String name) throws DvfReadException {
		if (dvfFiles.containsKey(name)) {
			return dvfFiles.get(name);
		}
		return readDvf(name);
	}

	private DvfHeader readDvf(String name) throws DvfReadException {
		File file = new File(EditorWindow.gameDir + "\\data\\characters\\" + name + ".dvf");
		if (file.exists()) {
			return readDvf(name, file);
		} else {
			file = new File(EditorWindow.gameDir + "\\data\\animations\\" + name + ".dvf");
			if (file.exists()) {
				return readDvf(name, file);
			}
		}
		throw new DvfReadException("Unable to read DVF! File does not exist!");
	}

	private DvfHeader readDvf(String name, File file) throws DvfReadException {
		DvfReader reader = new DvfReader();
		DvfHeader header = reader.readFile(file);
		dvfFiles.put(name, header);
		return header;
	}
}
