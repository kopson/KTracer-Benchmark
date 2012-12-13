package kparserbenchmark.projectwizard;

import kparserbenchmark.projectexplorer.Project;

import org.eclipse.jface.wizard.Wizard;

/**
 * Creates new project wizard
 * 
 * @author kopson
 */
public class NewProjectWizard extends Wizard {

	// Wizard pages
	protected NewProjectPage one;
	protected ProjectPropertiesPage two;

	// Created project
	private Project proj;

	/**
	 * The constructor
	 */
	public NewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		one = new NewProjectPage();
		two = new ProjectPropertiesPage();
		addPage(one);
		addPage(two);
	}

	@Override
	public boolean performFinish() {
		proj = new Project(one.getProjectName(), one.getProjectType(),
				one.getProjectPath(), one.getProjectSummary(),
				one.getProjectDescription());
		return true;
	}

	public Project getProj() {
		return proj;
	}
}