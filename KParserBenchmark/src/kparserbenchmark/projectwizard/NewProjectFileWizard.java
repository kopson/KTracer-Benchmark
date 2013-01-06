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

import java.io.File;
import java.io.IOException;

import kparserbenchmark.projectexplorer.ProjectExplorer;
import kparserbenchmark.utils.KImage;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

/**
 * Creates new project file wizard
 * 
 * @author kopson
 */
public class NewProjectFileWizard extends Wizard implements
		org.eclipse.ui.INewWizard {

	// The command ID
	public static final String ID = "KParserBenchmark.NewFileWizard";

	// Wizard pages
	protected NewProjectFilePage one;

	/**
	 * The constructor
	 */
	public NewProjectFileWizard() {
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
		try {
			if (fileName == null || !f.createNewFile())
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
		} catch (IOException e) {
			KWindow.getStatusLine(pe).setMessage(
					KImage.getImage(KImage.IMG_ERR_STATUS),
					"Error creating file");
		}
		return false;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}