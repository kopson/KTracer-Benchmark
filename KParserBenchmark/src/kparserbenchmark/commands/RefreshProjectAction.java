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

package kparserbenchmark.commands;

import java.util.logging.Logger;

import kparserbenchmark.projectexplorer.ProjectNode;
import kparserbenchmark.projectexplorer.ProjectNode.Status;
import kparserbenchmark.projectexplorer.ProjectExplorer;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * Refresh project action
 * 
 * @author kopson
 */
public class RefreshProjectAction extends Action implements ISelectionListener,
		IWorkbenchAction {

	// Logger instance
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(RefreshProjectAction.class
			.getName());

	// Window
	private final IWorkbenchWindow window;

	// Action ID
	public final static String ID = "KParserBenchmark.commands.refreshProjectAction";

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
					&& this.selection.getFirstElement() instanceof ProjectNode) {
				ProjectNode proj = (ProjectNode) this.selection.getFirstElement();
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
