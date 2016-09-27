package com.orchestrated.postfix.util;

/**
 *  Application constants.
 * 
 * 
 * @author Sidath Dassanayake
 * @version 0.0.1
 * @since 2016-09-27
 */


public final class AppConstants {

	private AppConstants() {
		// restrict instantiation
	}
	
	public static final String RSV_DATA_FILE_PATH="rsv.data.file.path";
	public static final String CELL_VALIDATION_RULE="[a-z]{1}[0-9]{1,}";
		
}
