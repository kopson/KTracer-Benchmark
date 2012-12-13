package kparserbenchmark.projectexplorer;

import kparserbenchmark.commands.OpenProjectAction;
import kparserbenchmark.commands.RefreshProjectAction;
import kparserbenchmark.projectexplorer.Project.Status;

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
import org.eclipse.ui.part.ViewPart;

/**
 * Project explorer view
 * 
 * @author root
 */
public class ProjectExplorer extends ViewPart {

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
			if(o instanceof Project && ((Project) o).getCurrStatus() == Status.OPENED)
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
		viewer = new ProjectTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ProjectContentProvider());
		viewer.setLabelProvider(new ProjectLabelProvider());
		// Expand the tree
		viewer.setAutoExpandLevel(1);
		// Provide the input to the ContentProvider
		viewer.setInput(new ProjectModel());

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				TreeViewer viewer = (TreeViewer) event.getViewer();
				IStructuredSelection thisSelection = (IStructuredSelection) event
						.getSelection();
				Object selectedNode = thisSelection.getFirstElement();
				viewer.setExpandedState(selectedNode,
						!viewer.getExpandedState(selectedNode));
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

		// Create new context menu for tree viewer
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		viewer.getControl().setMenu(
				menuManager.createContextMenu(viewer.getControl()));
		getSite().registerContextMenu(menuManager, viewer);
		getSite().setSelectionProvider(viewer);

		// Get some actions from main menu and add them to context menu
		IWorkbenchWindow window = getSite().getPage().getWorkbenchWindow();
		menuManager.add(new OpenProjectAction(window));
		menuManager.add(new RefreshProjectAction(window));
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
	}
	
}
