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

import kparserbenchmark.application.Activator;
import kparserbenchmark.editor.ScriptEditor;
import kparserbenchmark.editor.ScriptEditorInput;
import kparserbenchmark.projectexplorer.Category;
import kparserbenchmark.utils.KImage;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Action for launching script editor.
 * 
 * @author kopson
 *
 */
public class ScriptEditorAction extends Action implements ISelectionListener,
		IWorkbenchAction {

	/**
	 * The action ID.
	 */
	public final static String ID = "KParserBenchmark.commands.runEditorAction";
	
	/**
	 * Logger
	 */
	private static final Logger logger = Logger.getLogger(ScriptEditorAction.class.getSimpleName());
	
	/**
	 * Workbench window.
	 */
	private final IWorkbenchWindow window;

	/**
	 * Currently selected element
	 */
	private IStructuredSelection selection;

	/**
	 * The constructor.
	 * 
	 * @param workbench window
	 */
	public ScriptEditorAction(IWorkbenchWindow window) {
		this.window = window;
		setId(ID);
		setText("Open File...");
		setToolTipText("Open file");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, KImage.IMG_SCRIPT_EDITOR));
		window.getSelectionService().addSelectionListener(this);
	}

	@Override
	public void dispose() {
		window.getSelectionService().removeSelectionListener(this);
	}

	/**
	 * Action is enabled only if selected item is a category item.
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection incoming) {
		if (incoming instanceof IStructuredSelection) {
			selection = (IStructuredSelection) incoming;
			setEnabled(selection.size() == 1
				&& selection.getFirstElement() instanceof Category);
		} else {
			// Other selections, for example containing text or of other kinds.
			setEnabled(false);
		}
	}

	/**
	 * Open script editor and set file name on title bar.
	 */
	@Override
	public void run() {
		Object item = selection.getFirstElement();
		Category entry = (Category) item;
		IWorkbenchPage page = window.getActivePage();
		ScriptEditorInput input = new ScriptEditorInput(entry);
		try {
			page.openEditor(input, ScriptEditor.ID);
		} catch (PartInitException e) {
			logger.log(Level.WARNING, e.getMessage());
		}
	}
}
