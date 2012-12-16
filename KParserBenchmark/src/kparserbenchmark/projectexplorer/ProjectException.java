package kparserbenchmark.projectexplorer;

public class ProjectException extends Exception {

	/**
	 * ID
	 */
	private static final long serialVersionUID = 4963310258257883191L;

	private String exceptionMsg;
	
	public ProjectException(Exception e) {
		exceptionMsg = e.getMessage();	
}

	@Override
	public String getMessage() {
		return "Invalid project: " + exceptionMsg;
	}

}
