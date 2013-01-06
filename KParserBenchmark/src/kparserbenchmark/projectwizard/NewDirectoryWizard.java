package kparserbenchmark.projectwizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

public class NewDirectoryWizard extends Wizard  implements org.eclipse.ui.INewWizard {

	// Wizard pages
	protected NewProjectFilePage one;

	// Created file name
	protected String name;
	
	/**
	 * The constructor
	 */
	public NewDirectoryWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		one = new NewProjectFilePage();
		addPage(one);
	}

	@Override
	public boolean performFinish() {
		name = one.getFileName();
		
		return true;
	}

	public String getFile() {
		return name;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}

}
