package kparserbenchmark.projectexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import kparserbenchmark.intro.Application;

/**
 * Project Explorer content model
 * 
 * @author root
 */
public class ProjectModel {

	// List of projects
	private List<Project> projects;

	/**
	 * Get projects from workspace
	 * 
	 * @return
	 */
	public List<Project> setProjects() {
		projects = new ArrayList<Project>();
		String workspace = Application.getDefaultWorkspace();

		File f = new File(workspace);
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				if (c.isDirectory()) {
					Project project = new Project();
					project.init(c.getAbsolutePath());
					if (project.getCurrStatus() == Project.Status.OPENED) {
						for (File c1 : c.listFiles()) {
							Category file = new Category(project, c1.getAbsolutePath(), c1.getName());
							project.getElements().add(file);
						}
					}
					projects.add(project);
				}
			}
		}

		return projects;
	}

	public List<Project> getProjects() {
		return projects;
	}

}