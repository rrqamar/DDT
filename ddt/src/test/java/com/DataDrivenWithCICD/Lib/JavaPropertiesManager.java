package com.DataDrivenWithCICD.Lib;


	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.InputStream;
	import java.io.OutputStream;
	import java.util.Properties;

	import org.apache.log4j.Logger;

	public class JavaPropertiesManager {

		final static Logger logger = Logger.getLogger(JavaPropertiesManager.class);
		private String propertiesFile;
		private Properties prop;
		private OutputStream output;
		private InputStream input;

		public JavaPropertiesManager(String propertiesFilePath) {
			propertiesFile = propertiesFilePath;
			prop = new Properties();
		}

		public String readProperty(String key) throws Exception {
			String value = null;
			try {
				input = new FileInputStream(propertiesFile);
				prop.load(input);
				value = prop.getProperty(key);
			} catch (Exception e) {			
				logger.error(e.getMessage(), e);
			} finally {
				if (input != null) {
					input.close();
				}
			}
			return value;
		}

		public void setProperty(String key, String value) {
			try {
				output = new FileOutputStream(propertiesFile);
				prop.setProperty(key, value);
				prop.store(output, null);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

