package kparserbenchmark.projectexplorer;

import java.util.logging.Logger;

import kparserbenchmark.commands.OpenProjectAction;
import kparserbenchmark.commands.RefreshProjectAction;
import kparserbenchmark.commands.ScriptEditorAction;
import kparserbenchmark.projectexplorer.Project.Status;
import kparserbenchmark.utils.KImage;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.part.ViewPart;

/**
 * Project explorer view
 * 
 * @author kopson
 */
public class ProjectExplorer extends ViewPart {

	// Logger instance
	private final static Logger LOG = Logger.getLogger(ProjectExplorer.class
			.getName());

	/**
	 * Tree viewer with custom expanding handling
	 */
	public class ProjectTreeViewer extends TreeViewer {

		/**
		 * The constructor - same as for base class
		 * 
		 * @param parent
		 * @param i
		 */
		public ProjectTreeViewer(Composite parent, int i) {
			super(parent, i);
		}

		@Override
		protected void handleTreeExpand(TreeEvent event) {
			Object o = event.item.getData();
			if (o instanceof Project
					&& ((Project) o).getCurrStatus() == Status.OPENED)
				super.handleTreeExpand(event);
		}

	}

	// View ID
	public static final String ID = "KParserBenchmark.projectExplorer";

	// View main control object
	private ProjectTreeViewer viewer;

	/**
	 * The constructor
	 */
	public ProjectExplorer() {
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new ProjectTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(new ProjectContentProvider());
		viewer.setLabelProvider(new ProjectLabelProvider());
		// Expand the tree
		viewer.setAutoExpandLevel(1);
		// Provide the input to the ContentProvider
		viewer.setInput(Workspace.getInstance());

		// Get some actions from main menu and add them to context menu
		IWorkbenchWindow window = getSite().getPage().getWorkbenchWindow();
		final IWorkbenchAction runEditorAction = new ScriptEditorAction(window);
		final OpenProjectAction openProjectAction = new OpenProjectAction(
				window);
		final RefreshProjectAction refreshProjectAction = new RefreshProjectAction(
				window);

		// Create new context menu for tree viewer
		MenuManager menuManager = new MenuManager();
		viewer.getControl().setMenu(
				menuManager.createContextMenu(viewer.getControl()));
		getSite().registerContextMenu(menuManager, viewer);
		getSite().setSelectionProvider(viewer);

		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				final IStructuredSelection selection = (IStructuredSelection) viewer
						.getSelection();
				if (!selection.isEmpty()) {
					if (selection.getFirstElement() instanceof Project) {
						manager.add(openProjectAction);
						manager.add(refreshProjectAction);
						manager.add(new Separator(
								IWorkbenchActionConstants.MB_ADDITIONS));
					} else if (selection.getFirstElement() instanceof Category) {
						manager.add(runEditorAction);
						manager.add(new Separator(
								IWorkbenchActionConstants.MB_ADDITIONS));
					}
				}
			}
		});

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeViewer viewer = (TreeViewer) event.getViewer();
				IStructuredSelection thisSelection = (IStructuredSelection) event
						.getSelection();
				Object selectedNode = thisSelection.getFirstElement();

				if (selectedNode instanceof Project) {
					viewer.setExpandedState(selectedNode,
							!viewer.getExpandedState(selectedNode));
				} else if (selectedNode instanceof Category) {
					if (runEditorAction.isEnabled())
						runEditorAction.run();
				}
			}
		});

		viewer.getTree().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					final IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
					if (selection.getFirstElement() instanceof Project) {
						// Project o = (Project) selection.getFirstElement();
						// Project Delete the selected element from the model
					}

				}
			}
		});
		updateStatusLine();
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Refresh tree view
	 * 
	 * @param proj
	 */
	public void refreshView() {
		if (viewer != null) {
			viewer.refresh();
		}
		updateStatusLine();
	}

	/**
	 * Refresh tree view element
	 * 
	 * @param proj
	 */
	public void refreshProj() {
		final IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		if (selection.getFirstElement() instanceof Project) {
			Project p = (Project) selection.getFirstElement();
			boolean exp = viewer.getExpandedState(p);
			viewer.refresh();
			viewer.setExpandedState(p, exp);
		}
		updateStatusLine();
	}

	/**
	 * Update status line after reading workspace 
	 */
	private void updateStatusLine() {
		String statusMessage = Workspace.getInstance().getError();
		if (statusMessage != null)
			KWindow.getStatusLine(this).setMessage(
					KImage.getImage(KImage.IMG_WARNING_STATUS), statusMessage);
		else
			KWindow.getStatusLine(this).setMessage(
					KImage.getImage(KImage.IMG_OK_STATUS),
					"Loaded workspace: " + Workspace.getInstance().getPath());
	}
}
