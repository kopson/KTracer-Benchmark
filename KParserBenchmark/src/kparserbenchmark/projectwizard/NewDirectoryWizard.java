package kparserbenchmark.projectwizard;

import java.io.File;

import kparserbenchmark.projectexplorer.ProjectExplorer;
import kparserbenchmark.utils.KImage;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

public class NewDirectoryWizard extends Wizard  implements org.eclipse.ui.INewWizard {

	// The command ID
	public static final String ID = "KParserBenchmark.NewDirectoryWizard";
	
	// Wizard pages
	protected NewProjectFilePage one;
	
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
		ProjectExplorer pe = (ProjectExplorer) KWindow
				.getView(ProjectExplorer.ID);

		String fileName = one.getFileName();
		File f = new File(fileName);
		if (fileName == null || !f.mkdir())
			KWindow.getStatusLine(pe).setMessage(
					KImage.getImage(KImage.IMG_ERR_STATUS),
					"Error creating file");
		else {
			pe.refreshView();
			KWindow.getStatusLine(pe).setMessage(
					KImage.getImage(KImage.IMG_OK_STATUS),
					"File " + fileName + " created successfully");
			return true;
		}
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}
}
