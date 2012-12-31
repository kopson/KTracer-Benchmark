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

import java.io.File;
import java.io.IOException;

import kparserbenchmark.projectexplorer.ProjectExplorer;
import kparserbenchmark.projectwizard.NewProjectFileWizard;
import kparserbenchmark.utils.KImage;
import kparserbenchmark.utils.KWindow;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

/**
 * Opens: create new project wizard
 * 
 * @author kopson
 */
public class ProjectFileWizardHandler extends AbstractHandler implements
		IHandler {

	// The command ID
	public static final String ID = "KParserBenchmark.commands.ProjectFileWizardHandler";

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NewProjectFileWizard fileWizard = new NewProjectFileWizard();
		WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), fileWizard);
		ProjectExplorer pe = (ProjectExplorer) KWindow
				.getView(ProjectExplorer.ID);

		if (wizardDialog.open() == Window.OK) {
			String fileName = fileWizard.getFile();
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
				}
			} catch (IOException e) {
				KWindow.getStatusLine(pe).setMessage(
						KImage.getImage(KImage.IMG_ERR_STATUS),
						"Error creating file");
			}
		} else {
			KWindow.getStatusLine(pe).setMessage(
					KImage.getImage(KImage.IMG_CANCEL_STATUS),
					"Cancel creating file");
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub
	}

}
