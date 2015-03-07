package synapticloop.templar.gen;


public class SimpleLogger {
	private static final String INFO = "[INFO]:  ";
	private static final String ERROR = "[ERROR]: ";
	private static final String FATAL = "[FATAL]: ";
	private static final String WARN = "[WARN]:  ";

	private static void log(String type, String... messages) {
		System.out.print(type);
		for (int i = 0; i < messages.length; i++) {
			if(i != 0) { System.out.print(" "); }
			System.out.print(messages[i]);
		}
		System.out.println();
	}

	private static void logException(String type, String message, Exception exception, boolean printStackTrace) {
		log(FATAL, message, "Exception[", exception.getClass().getCanonicalName(), "] message was:", exception.getMessage());
		if(printStackTrace) {
			exception.printStackTrace();
		}
	}

	public static void logFatal(String message) { log(FATAL, message); }
	public static void logFatal(String message, Exception exception) { logException(FATAL, message, exception, true);}

	public static void logError(String message) { log(ERROR, message); }
	public static void logError(String message, Exception exception) { logException(ERROR, message, exception, true); }

	public static void logWarn(String message) { log(WARN, message); }
	public static void logWarn(String message, Exception exception) { logException(WARN, message, exception, false); }

	public static void logInfo(String message) { log(INFO, message); }
	public static void logInfo(String message, Exception exception) { logException(INFO, message, exception, false); }
}
