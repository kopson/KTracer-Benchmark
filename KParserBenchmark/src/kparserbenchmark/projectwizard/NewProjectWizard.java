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

import kparserbenchmark.application.Application;
import kparserbenchmark.projectexplorer.Project;
import kparserbenchmark.projectexplorer.ProjectExplorer;
import kparserbenchmark.projectexplorer.Workspace;
import kparserbenchmark.utils.KImage;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * Creates new project wizard
 * 
 * @author kopson
 */
public class NewProjectWizard extends Wizard implements
		org.eclipse.ui.INewWizard {

	// The command ID
	public static final String ID = "KParserBenchmark.NewProjectWizard";

	// Wizard pages
	protected NewProjectPage one;

	/**
	 * The constructor
	 */
	public NewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		one = new NewProjectPage();
		addPage(one);
	}

	@Override
	public boolean performFinish() {
		Project proj = new Project(one.getProjectName(), one.getProjectType(),
				one.getProjectPath(), one.getProjectSummary(),
				one.getProjectDescription());

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
		// TODO Auto-generated method stub
	}
}