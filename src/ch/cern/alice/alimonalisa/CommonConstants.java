package ch.cern.alice.alimonalisa;

/**
 * 
 * A set of constants used by all of the components in this application. To use
 * these constants the components implement the interface.
 */
public final class CommonConstants {

	public CommonConstants() {

		// don't allow the class to be instantiated
	}

	// action for the intent if only one notification is downloaded
	public static final String ONE_NOTIFICATION_ACTION = "ch.cern.alice.alimonalisa.ONE_NOTIFICATION";
	// action for the intent if more then one notifications are downloaded
	public static final String MULTIPLE_NOTIFICATIONS_ACTION = "ch.cern.alice.alimonalisa.MULTIPLE_NOTIFICATIONS";
	// name of the extra argument
	public static final String NOTIFICATION_ID = "id"; 
	
	
	// string constants for the category of notification
	public static final String SERVICES = "Site services"; 
	public static final String STORAGE = "Storages";
	public static final String NETWORK = "Network";
	public static final String PROXY = "Proxies";
	
}
