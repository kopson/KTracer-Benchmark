package kparserbenchmark.projectexplorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kparserbenchmark.KFile;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Controls life cycle of the project
 */
public class Project {

	// Obligatory attributes
	private String name;
	private Types type;
	private String path;

	// Optional attributes
	private String summary;
	private String description;

	// Project's file list
	private List<Category> elements = new ArrayList<Category>();

	/**
	 * Project statuses
	 */
	public static enum Types {
		UNKNOWN, SCHEDULER, TEST
	}

	/**
	 * Project statuses
	 */
	public static enum Status {
		UNKNOWN, OPENED, CLOSED, DELETED
	}

	// Project properties
	public static enum Properties {
		Name, Type, Path, Summary, Description, State
	}

	// Project status
	private Status currStatus;

	// Project property file
	private KFile propFile;

	// String constants
	private static final String errorDup = "Project already exists in selected directory";
	private static final String errorCreat = "Can't create project in selected directory";

	// Properties file name
	private static final String properties = ".properties";

	public Project() {
		name = "";
		type = Types.UNKNOWN;
		path = "";
		summary = "";
		description = "";
	}

	/**
	 * The simple constructor
	 * 
	 * @param name
	 * @param type
	 * @param path
	 */
	public Project(String name, Types type, String path) {
		this.setName(name);
		this.setType(type);
		this.setPath(path);
		this.currStatus = Status.OPENED;
	}

	/**
	 * The full constructor
	 * 
	 * @param name
	 * @param type
	 * @param path
	 * @param summary
	 * @param description
	 */
	public Project(String name, Types type, String path, String summary,
			String description) {
		this.setName(name);
		this.setType(type);
		this.setPath(path);
		this.summary = summary;
		this.description = description;
		this.currStatus = Status.OPENED;
	}

	/**
	 * Open closed project
	 * 
	 * @return success status
	 */
	public boolean open() {
		assert (currStatus == Status.CLOSED);
		currStatus = Status.OPENED;
		if(propFile == null) {
			String projPath = path + File.separator + name;
			propFile = new KFile(projPath + File.separator + properties);
		}
		return propFile.writeProperty(Properties.State, currStatus.toString());
	}

	/**
	 * Close opened project
	 * 
	 * @return success status
	 */
	public boolean close() {
		assert (currStatus == Status.OPENED);
		currStatus = Status.CLOSED;
		if(propFile == null) {
			String projPath = path + File.separator + name;
			propFile = new KFile(projPath + File.separator + properties);
		}
		return propFile.writeProperty(Properties.State, currStatus.toString());
	}

	/**
	 * Create and initialize project location
	 * 
	 * @return success status
	 */
	public boolean create() {
		assert (!path.equals(""));
		String projPath = path + File.separator + name;
		propFile = new KFile(projPath + File.separator + properties);
		File projDir = new File(projPath);
		if (propFile.exists()) {
			MessageDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "Error", errorDup);
			return false;
		}
		if (projDir.exists() && projDir.isDirectory()) {
			MessageDialog.openError(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), "Error", errorDup);
			return false;
		}

		int fail = 0;
		fail += projDir.mkdir() == true ? 0 : 1;

		try {
			if (!propFile.createNewFile() || !propFile.canWrite()) {
				MessageDialog.openError(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), "Error",
						errorCreat);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		fail += propFile.writeProperty(Properties.Name, name) == true ? 0 : 1;
		fail += propFile.writeProperty(Properties.Path, path) == true ? 0 : 1;
		fail += propFile.writeProperty(Properties.Type, type.toString()) == true ? 0
				: 1;
		fail += propFile.writeProperty(Properties.Summary, summary) == true ? 0
				: 1;
		fail += propFile.writeProperty(Properties.Description, description) == true ? 0
				: 1;
		fail += propFile.writeProperty(Properties.State, currStatus.toString()) == true ? 0
				: 1;

		return fail == 0 ? true : false;
	}

	/**
	 * Deletes project
	 * 
	 * @param hard
	 *            - Deletes project content from workspace
	 * @return success status
	 */
	public boolean delete(boolean hard) {
		String projPath = path + File.separator + name;

		if (hard) {
			try {
				KFile.removeDirectoryRecursive(projPath);
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			currStatus = Status.DELETED;
			return propFile.writeProperty(Properties.State,
					currStatus.toString());
		}

		return false;
	}

	/**
	 * Init project from property file
	 * 
	 * @param absolutePath
	 * @return
	 */
	public boolean init(String absolutePath) {
		String propFile = absolutePath + File.separator + properties;
		KFile properties = new KFile(propFile);
		if (properties.exists()) {
			name = properties.readProperty(Properties.Name);
			path = properties.readProperty(Properties.Path);
			summary = properties.readProperty(Properties.Summary);
			description = properties.readProperty(Properties.Description);
			currStatus = Status.valueOf(properties
					.readProperty(Properties.State));
			type = Types.valueOf(properties.readProperty(Properties.Type));
		} else {
			return false;
		}

		return true;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Types getType() {
		return type;
	}

	public void setType(Types type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Status getCurrStatus() {
		return currStatus;
	}

	public List<Category> getElements() {
		return elements;
	}
}
