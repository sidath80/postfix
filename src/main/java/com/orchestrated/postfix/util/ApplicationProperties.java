package com.orchestrated.postfix.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

/**
* The ApplicationProperties is a utility class.
* This will load  all properties from the application.properties file.
*
* @author  Sidath Dassanayake
* @version 0.0.1
* @since   2016-09-27
*/


public class ApplicationProperties {
	
	private static ApplicationProperties applicationProperties;

	private ApplicationProperties() {

	}

	public static ApplicationProperties getInstance() {

		if (applicationProperties == null) {
			applicationProperties = new ApplicationProperties();
		}

		return applicationProperties;
	}
	
	public Properties load(){
		
		Properties prop = new Properties();
		InputStream input = null;
		try {
			File propertyFile = new File(this.getClass().getResource("/application.properties").toURI());
			input = new FileInputStream(propertyFile);
			prop.load(input);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
}
