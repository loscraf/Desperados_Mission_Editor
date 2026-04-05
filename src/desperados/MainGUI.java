package desperados;

import desperados.ui.EditorWindow;
import desperados.util.PropertiesHandler;

public class MainGUI {

	private EditorWindow editorWindow;

	public MainGUI() {
		PropertiesHandler.initProperties();
		editorWindow = new EditorWindow(this);
		editorWindow.run();
	}

	public static void main(String[] args) {
		new MainGUI();
	}

	public void readDvdFile(String path) {
		editorWindow.loadDvdData(path);
	}
}
