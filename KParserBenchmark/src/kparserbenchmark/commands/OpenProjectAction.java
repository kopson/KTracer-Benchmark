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

import java.util.logging.Level;
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
 * Open closed project and close opened project actions implementation
 * 
 * @author kopson
 */
public class OpenProjectAction extends Action implements ISelectionListener,
		IWorkbenchAction {

	// Logger instance
	private final static Logger LOG = Logger.getLogger(OpenProjectAction.class
			.getName());

	// Window
	private final IWorkbenchWindow window;

	// Action ID
	public final static String ID = "KParserBenchmark.commands.openProjectAction";

	// Selection
	private IStructuredSelection selection;

	/**
	 * The constructor
	 * 
	 * @param window
	 */
	public OpenProjectAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("&Open");
		setToolTipText("Open project");
		window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// selection containing elements
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
			if (this.selection.size() == 1
					&& this.selection.getFirstElement() instanceof Project) {
				Project proj = (Project) this.selection.getFirstElement();
				KWindow.getStatusLine(KWindow
						.getView(ProjectExplorer.ID)).setMessage(proj.getName());
				if (proj.getCurrStatus() == Status.CLOSED) {
					setEnabled(true);
					setText("&Open");
					setToolTipText("Open project");
					return;
				} else if (proj.getCurrStatus() == Status.OPENED) {
					setEnabled(true);
					setText("&Close");
					setToolTipText("Close project");
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
		Project proj = (Project) this.selection.getFirstElement();
		if (proj == null) {
			LOG.log(Level.SEVERE,
					"Null pointer while executing open project method");
			return;
		}
		
		ProjectExplorer view = (ProjectExplorer) KWindow
		.getView(ProjectExplorer.ID);
		
		if (proj.getCurrStatus() == Status.CLOSED) {
			if (!proj.open()) {
				LOG.log(Level.SEVERE, "Failed to execute open project method");
				return;
			}
		} else if (proj.getCurrStatus() == Status.OPENED) {
			if(!proj.close()) {
				LOG.log(Level.SEVERE, "Failed to execute close project method");
				return;
			}
		}
		view.refreshProj();
	}
}
