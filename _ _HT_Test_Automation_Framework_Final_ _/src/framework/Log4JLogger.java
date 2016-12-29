package framework;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 
 * @author focalpt
 *
 */

public class Log4JLogger {
	
	public static Logger initLog4JLogging() throws IOException, FileNotFoundException{
		Logger log = Logger.getLogger(Log4JLogger.class);
		Properties props = new Properties();
		props.load(new FileInputStream(System.getProperty("user.dir") + "\\props\\log4j.properties"));
		PropertyConfigurator.configure(props);
		System.setProperty("log4j.configuration", "set") ;
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.Jdk14Logger");
		System.out.println("IN initLog4JLogging() constructor...loading and configuring log4j.properties...");	
		return log;
	}

}
