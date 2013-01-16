/*******************************************************************************
 Copyright (c) 2012 kopson kopson.piko@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 *******************************************************************************/

package kparserbenchmark.projectexplorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import kparserbenchmark.utils.KFile;
import kparserbenchmark.utils.KWindow;

/**
 * Controls life cycle of the project
 * 
 * Review history: Rev 1: [15.01.2013] Kopson:
 * 
 * @author Kopson
 */
public class ProjectNode extends ProjectItem {

	// Logger instance
	private static final Logger LOG = Logger.getLogger(ProjectNode.class
			.getName());

	/******* Obligatory attributes *******/
	// Project type
	private ProjectTypes projectType;
	// Project status
	private Status currStatus;
	// Project property file
	private KFile propFile;
	/*************************************/

	/******* Optional attributes *******/
	// Project summary
	private String summary;
	// Project description
	private String description;

	/*************************************/

	/**
	 * Project types
	 */
	public static enum ProjectTypes {
		UNKNOWN, // Project wit no type
		SCHEDULER, // Scheduler monitor
		TEST // For test purposes only
	}

	/**
	 * Project statuses used for changing UI interaction in ProjectExplorer view
	 */
	public static enum Status {
		UNKNOWN, // Project should newer reach this status
		OPENED, // Project is opened
		CLOSED, // Project is closed
		DELETED // Project is removed from ProjectExplorer view
	}

	/**
	 * Project properties
	 */
	public static enum Properties {
		Name, Type, Path, Summary, Description, State
	}

	// Error string constants
	private static final String errorDup = "Project already exists in selected directory";
	private static final String errorCreat = "Can't create project in selected directory";

	/**
	 * Default constructor
	 */
	public ProjectNode() {
		super(ItemTypes.NODE);
		projectType = ProjectTypes.UNKNOWN;
		summary = "";
		description = "";
		currStatus = Status.UNKNOWN;
	}

	/**
	 * The simple constructor
	 * 
	 * @param name
	 * @param type
	 * @param path
	 */
	public ProjectNode(String name, ProjectTypes projectType, String path) {
		super(ItemTypes.NODE, Workspace.getInstance(), path, name);
		this.projectType = projectType;
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
	public ProjectNode(String name, ProjectTypes projectType, String path,
			String summary, String description) {
		super(ItemTypes.NODE, Workspace.getInstance(), path, name);
		this.projectType = projectType;
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
		if (propFile == null) {
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
		if (propFile == null) {
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
			KWindow.displayError(errorDup);
			LOG.log(Level.SEVERE, errorDup);
			return false;
		}
		if (projDir.exists() && projDir.isDirectory()) {
			KWindow.displayError(errorDup);
			LOG.log(Level.SEVERE, errorDup);
			return false;
		}

		int fail = 0;
		fail += projDir.mkdir() == true ? 0 : 1;

		try {
			if (!propFile.createNewFile() || !propFile.canWrite()) {
				KWindow.displayError(errorCreat);
				LOG.log(Level.SEVERE, errorCreat);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		fail += propFile.writeProperty(Properties.Name, name) == true ? 0 : 1;
		fail += propFile.writeProperty(Properties.Path, path) == true ? 0 : 1;
		fail += propFile.writeProperty(Properties.Type, projectType.toString()) == true ? 0
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
	 * Open ask for delete dialog
	 */
	public void checkDelete() {
		boolean hard = KWindow.openQuestionDialog("Delete Project",
				"Remove project source from workspace?");
		delete(hard);
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
	 * @throws ProjectException
	 */
	public boolean init(String absolutePath) throws ProjectException {
		try {
			String propFile = absolutePath + File.separator + properties;
			KFile properties = new KFile(propFile);
			if (properties.exists()) {
				name = properties.readProperty(Properties.Name);
				path = properties.readProperty(Properties.Path);
				summary = properties.readProperty(Properties.Summary);
				description = properties.readProperty(Properties.Description);
				currStatus = Status.valueOf(properties
						.readProperty(Properties.State));
				projectType = ProjectTypes.valueOf(properties
						.readProperty(Properties.Type));
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new ProjectException(e);
		}
		return true;
	}

	/********** Getters/Setters **********/
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

	public ProjectTypes getProjectType() {
		return projectType;
	}

	public Status getCurrStatus() {
		return currStatus;
	}

	/*************************************/

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof ProjectNode) {
			if (((ProjectNode) obj).name.equals(this.name)
					&& ((ProjectNode) obj).path.equals(this.path)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return super.toString() + "Type: " + projectType.name() + "\nStatus: "
				+ currStatus + "\nProperty File: " + propFile
				+ "\nDescription: " + description + "\nSummary: " + summary;
	}

	@Override
	public int hashCode() {
		return name.hashCode() + children.hashCode();
	}
}
