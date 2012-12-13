package kparserbenchmark;

/**
 * Ktrace constants
 * 
 * @author kopson
 */
public class KTrace {

	//Project types
	public static final int PROJECT_TYPE_SCHEDULER 	= 0;
	public static final int PROJECT_TYPE_TEST 		= 1;
	
	//Project type names
	public static final String[] ProjectTypes = {
		"Scheduler",
		"Test"
	};
	
	/**
	 * Returns number of project types
	 * 
	 * @return number of project types
	 */
	public static int getTypesSize() {
		return ProjectTypes.length;
	}
}
