package de.uni_leipzig.simba.boa.backend.logging;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

public class LoggingConfigurator {

	private static LoggingConfigurator INSTANCE = null;
	
	private static String CONFIG_FILE;
	
	private LoggingConfigurator() { /* singleton */ }
		
	/**
	 * @return the instance of this singleton
	 */
	public static LoggingConfigurator getInstance() {
		
		if ( LoggingConfigurator.INSTANCE == null ) {
			
			LoggingConfigurator.INSTANCE = new LoggingConfigurator();
		}
		return INSTANCE;
	}
	
	/**
	 * Initializes the logging system.
	 * Sets the settings from the config file with the
	 * help of the org.apache.log4j.xml.DOMConfigurator. 
	 */
	public void init() {
		
		try {
			
			String path = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
			path = path.substring(5, path.indexOf("WEB-INF"));
			CONFIG_FILE = path + "WEB-INF/config/log4j.xml";
			
			DOMConfigurator.configure(CONFIG_FILE);
		}
		catch ( FactoryConfigurationError fce ) {
			
			fce.printStackTrace();
		}
		Logging.logInfo("Logging successfully initialized.", LoggingConfigurator.class);
	}

	/**
	 * Destroy method.
	 * <br>
	 * Calls org.apache.log4j.LogManager.shutdown()
	 */
	public void destroy() {
		
		LogManager.shutdown();
	}
}
