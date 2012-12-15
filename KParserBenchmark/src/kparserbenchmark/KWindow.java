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

package kparserbenchmark;

import kparserbenchmark.editor.ScriptEditor;
import kparserbenchmark.editor.ScriptEditorInput;
import kparserbenchmark.projectexplorer.Category;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

/**
 * Application utilities class
 * 
 * @author kopson
 */
public class KWindow {

	/**
	 * This class should be never instantiate
	 */
	private KWindow() {
	}

	/**
	 * Get workbench page in view
	 * 
	 * @param view View
	 * @return Returns workbench page
	 */
	public static IWorkbenchPage getPage(ViewPart view) {
		return view.getViewSite().getPage();
	}
	
	/**
	 * Get workbench page in command
	 * 
	 * @param event Command's execution event
	 * @return Returns workbench page
	 */
	public static IWorkbenchPage getPage(ExecutionEvent event) {
		return HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
	}
	
	/**
	 * Get workbench page from anyplace
	 * 
	 * @return Returns workbench page
	 */
	public static IWorkbenchPage getPage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
	}
	
	/**
	 * Open script editor for file
	 * 
	 * @param page Where to open editor 
	 * @param participant File input
	 * @return Returns opened editor
	 */
	public static IEditorPart openEditor(IWorkbenchPage page, Category participant) {
		try {
			return page.openEditor(new ScriptEditorInput(participant), ScriptEditor.ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * Get view by ID
	 * 
	 * @param viewID View ID
	 * @return Returns the view or NULL if view is not found
	 */
	public static IViewPart getView(String viewID) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		return page.findView(viewID);
	}

	/**
	 * Get status line from a view. Use this function to change status line<br>
	 * message.
	 * 
	 * @param view Workbench View 
	 * @return Returns status line manager
	 */
	public static IStatusLineManager getStatusLine(IViewPart view) {
		return view.getViewSite().getActionBars().getStatusLineManager(); 
	}
}
