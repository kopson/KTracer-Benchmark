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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import kparserbenchmark.utils.KWindow;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Project Explorer content model
 * 
 * @author kopson
 */
public final class Workspace {

	// Logger instance
	private final static Logger LOG = Logger.getLogger(Workspace.class
			.getName());

	// List of projects
	private List<Project> projects;

	// Workspace path
	private String path;

	// Implementation of Singleton Pattern
	private static Workspace Instance;

	// Current active project
	private static Project currProject;

	// Default workspace name
	public static final String KWorkspace = "kworkspace";

	// String constants
	private static final String invProj_err1 = "Invalid project(s) in workspace";

	// Workspace keeps error value internally because if occurs errors during
	// loading projects
	// from workspace we cannot update status line - it is not created yet. We
	// have to put off this until ProjectExplorer view will be created.
	private int hasErrors;

	/**
	 * Private constructor
	 */
	private Workspace() {

		KWindow.getPrefs().addPropertyChangeListener(
			new IPropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty() == "MySTRING1") {
						path = event.getNewValue().toString();
					}
				}
			});
	}

	/**
	 * Implementation of Singleton Pattern
	 * 
	 * @return
	 */
	public static synchronized Workspace getInstance() {
		if (Instance == null) {
			Instance = new Workspace();
		}
		return Instance;
	}

	/**
	 * Sets application's projects default location
	 */
	public void createDefaultWorkspace(String newPath) {
		Assert.isNotNull(newPath);
		path = newPath;
		File f = new File(path);
		if (!f.exists())
			f.mkdir();
		hasErrors = 0;
	}

	/**
	 * Get projects from workspace
	 * 
	 * @return
	 */
	public List<Project> setProjects() {
		projects = new ArrayList<Project>();

		File f = new File(path);
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				if (c.isDirectory()) {
					try {
						Project project = new Project();
						project.init(c.getAbsolutePath());
						if (project.getCurrStatus() == Project.Status.OPENED) {
							for (File c1 : c.listFiles()) {
								if (c1.isFile()) {
									Category file = new Category(project,
											c1.getAbsolutePath(), c1.getName());
									project.getElements().add(file);
								}
							}
						}
						projects.add(project);
					} catch (ProjectException e) {
						LOG.log(Level.WARNING, e.getMessage());
						hasErrors = 1;
					}
				}
			}
		}

		return projects;
	}

	/**
	 * Return error message
	 * 
	 * @return Returns error message or null if no errors occurs
	 */
	public String getError() {
		switch (hasErrors) {
		case 1:
			return invProj_err1;
		default:
			return null;
		}
	}

	public List<Project> getProjects() {
		return projects;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static Project getCurrProject() {
		return currProject;
	}

	public void setCurrProject(Project project) {
		currProject = project;
	}
}