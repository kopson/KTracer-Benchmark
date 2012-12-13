package kparserbenchmark.commands;

import kparserbenchmark.KImage;
import kparserbenchmark.KWindow;
import kparserbenchmark.intro.Application;
import kparserbenchmark.projectexplorer.Project;
import kparserbenchmark.projectexplorer.ProjectExplorer;
import kparserbenchmark.projectwizard.NewProjectWizard;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Opens new create project wizard
 * 
 * @author kopson
 */
public class ProjectWizardHandler extends AbstractHandler implements IHandler {

	// The command ID
	public static final String ID = "KParserBenchmark.commands.ProjectWizardHandler";
	
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NewProjectWizard projectWizard = new NewProjectWizard();
		WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), projectWizard);
		if (wizardDialog.open() == Window.OK) {
			Project currentProj = projectWizard.getProj();
			if (currentProj == null || !currentProj.create())
				KWindow.getStatusLine(KWindow.getView(ProjectExplorer.ID))
				.setMessage(KImage.getImage(KImage.IMG_ERR_STATUS),
						"Error creating project");
			else {
				Application.setCurrProject(currentProj);
		
				IViewPart view = KWindow.getView(ProjectExplorer.ID);
				((ProjectExplorer) view).refreshView();
				KWindow.getStatusLine(KWindow.getView(ProjectExplorer.ID))
						.setMessage(KImage.getImage(KImage.IMG_OK_STATUS),
								"Project " + currentProj.getName()
										+ " created successfully");
			}
		} else {
			System.out.println("Cancel creating project");
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
	}

}
