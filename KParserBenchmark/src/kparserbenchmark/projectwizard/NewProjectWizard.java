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

package kparserbenchmark.projectwizard;

import kparserbenchmark.projectexplorer.ProjectNode;
import kparserbenchmark.projectexplorer.ProjectExplorer;
import kparserbenchmark.projectexplorer.Workspace;
import kparserbenchmark.utils.KImage;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;

/**
 * Creates new project wizard
 * 
 * Review history: 
 * Rev 1: [19.01.2013] Kopson: 
 * 		STATUS: Complete
 * 
 * @author Kopson
 */
public class NewProjectWizard extends Wizard implements
		org.eclipse.ui.INewWizard {

	/** The wizard ID */
	public static final String ID = "KParserBenchmark.NewProjectWizard";

	/** Wizard page */
	protected NewProjectPage first;

	/**
	 * The constructor
	 */
	public NewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		first = new NewProjectPage();
		addPage(first);
	}

	@Override
	public boolean performFinish() {
		ProjectNode proj = new ProjectNode(first.getProjectName(),
				first.getProjectType(), first.getProjectPath(),
				first.getProjectSummary(), first.getProjectDescription());

		if (proj == null || !proj.create())
			KWindow.getStatusLine(KWindow.getView(ProjectExplorer.ID))
					.setMessage(KImage.getImage(KImage.IMG_ERR_STATUS),
							"Error creating project");
		else {
			Workspace.getInstance().setCurrProject(proj);

			IViewPart view = KWindow.getView(ProjectExplorer.ID);
			((ProjectExplorer) view).refreshView();
			KWindow.getStatusLine(KWindow.getView(ProjectExplorer.ID))
					.setMessage(
							KImage.getImage(KImage.IMG_OK_STATUS),
							"Project " + proj.getName()
									+ " created successfully");
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}