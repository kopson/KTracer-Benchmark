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
 * Review history: 
 * Rev 1: [18.01.2013] Kopson:
 * 		STATUS: Complete
 * 
 * @author kopson
 */
public class NewFileWizard extends Wizard implements org.eclipse.ui.INewWizard {

	/** The wizard ID */
	public static final String ID = "KParserBenchmark.NewFileWizard";

	/** First wizard page */
	protected NewProjectFilePage first;

	/**
	 * The constructor
	 */
	public NewFileWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		first = new NewProjectFilePage(true);
		addPage(first);
	}

	@Override
	public boolean performFinish() {
		ProjectExplorer pe = (ProjectExplorer) KWindow
				.getView(ProjectExplorer.ID);

		String fileName = first.getFileName();
		File f = new File(fileName);
		try {
			if (fileName == null || !f.createNewFile())
				throw new IOException();
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