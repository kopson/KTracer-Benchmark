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

import kparserbenchmark.projectexplorer.Category;
import kparserbenchmark.projectexplorer.Project;
import kparserbenchmark.projectexplorer.Workspace;
import kparserbenchmark.utils.KWindow;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

/**
 * Open file action handler impementation
 * 
 * @author kopson
 */
public class OpenFileHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Project p = Workspace.getCurrProject();
		String file;
		if(p != null)
			file = KWindow.openFileDialog(p.getPath(), KWindow.ALL);
		else
			file = KWindow.openFileDialog(Workspace.getInstance().getPath(), KWindow.ALL);
		if(file != null)
			KWindow.openEditor(KWindow.getPage(), new Category(null, file,
				new File(file).getName()));
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
	}
}
