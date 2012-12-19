package kparserbenchmark.intro;

import java.io.File;

import kparserbenchmark.projectexplorer.Project;
import kparserbenchmark.projectexplorer.Workspace;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 * 
 * @author kopson
 */
public class Application implements IApplication {
	
	//Current active project
	private static Project currProject;
	
	//Default workspace name
	private static final String KWorkspace = "kworkspace";
	
	@Override
	public Object start(IApplicationContext context) {
		Workspace.getInstance().createDefaultWorkspace(System.getProperty("user.home") + File.separator + KWorkspace);
		
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}

	@Override
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}

	public static Project getCurrProject() {
		return currProject;
	}

	public static void setCurrProject(Project currProject) {
		Application.currProject = currProject;
	}
}
