package kparserbenchmark.commands;

import java.util.logging.Logger;

import kparserbenchmark.KWindow;
import kparserbenchmark.projectexplorer.Project;
import kparserbenchmark.projectexplorer.Project.Status;
import kparserbenchmark.projectexplorer.ProjectExplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * Refresh project
 * 
 * @author kopson
 */
public class RefreshProjectAction extends Action implements ISelectionListener,
		IWorkbenchAction {

	// Logger instance
	private final static Logger LOG = Logger.getLogger(RefreshProjectAction.class
			.getName());

	// Window
	private final IWorkbenchWindow window;

	// Action ID
	public final static String ID = "KParserBenchmark.refreshProjectAction";

	// Selection
	private IStructuredSelection selection;

	/**
	 * The constructor
	 * 
	 * @param window
	 */
	public RefreshProjectAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Refresh");
		setToolTipText("Refresh project");
		window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
			if (this.selection.size() == 1
					&& this.selection.getFirstElement() instanceof Project) {
				Project proj = (Project) this.selection.getFirstElement();
				if (proj.getCurrStatus() == Status.OPENED) {
					setEnabled(true);
					return;
				}
			}
		}
		// other selections (e.g., containing text or of other kinds,
		// multi-selections, selections of closed project)
		setEnabled(false);
	}

	@Override
	public void run() {		
		ProjectExplorer view = (ProjectExplorer) KWindow
				.getView(ProjectExplorer.ID);
		view.refreshProj();
	}
}
