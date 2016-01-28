package debug;

public class debug {

	private static boolean debug = true;
	
	private debug() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the debug
	 */
	public static boolean isDebug() {
		return debug;
	}

	/**
	 * @param _debug the debug to set
	 */
	public static void setDebug(boolean _debug) {
		debug = _debug;
	}

}
