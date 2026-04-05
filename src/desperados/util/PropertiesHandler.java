package desperados.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {

	private static Properties properties;
	private static String propertiesPath;

	public static void initProperties() {
		propertiesPath = "." + File.separator + "DesperadosTools.properties";
		File file = new File(propertiesPath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
		if (properties == null) return null;
		return properties.getProperty(key);
	}

	public static void setProperty(String key, String value) {
		if (properties == null) return;
		properties.setProperty(key, value);
	}

	public static void storeProperties() {
		if (properties == null) return;
		try {
			properties.store(new FileWriter(propertiesPath), null);
		} catch (IOException e) {}
	}
}
