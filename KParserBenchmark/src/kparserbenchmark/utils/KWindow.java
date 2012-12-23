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

package kparserbenchmark.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import kparserbenchmark.editor.ScriptEditor;
import kparserbenchmark.editor.ScriptEditorInput;
import kparserbenchmark.projectexplorer.Category;
import kparserbenchmark.projectexplorer.Workspace;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
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

	// String constants
	private static final String fileDialogTitle = "Select File";
	private static final String pathDialogTitle = "Select directory";
	private static final String pathDialogDescription = "Select directory";

	// Logger instance
	private static final Logger LOGe = Logger.getLogger(KWindow.class.getName());

	/** Set *.txt file extensions */
	public static final int TXT = 0x000F;
	
	/** Set *.log file extensions */
	public static final int LOG = 0x00F0;
	
	/** Set all file extensions */
	public static final int ALL = 0xFFFF;

	/**
	 * This class should be never instantiate
	 */
	private KWindow() {
	}

	/**
	 * Get workbench page in view
	 * 
	 * @param view
	 *            View
	 * @return Returns workbench page
	 */
	public static IWorkbenchPage getPage(ViewPart view) {
		return view.getViewSite().getPage();
	}

	/**
	 * Get workbench page in command
	 * 
	 * @param event
	 *            Command's execution event
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
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage();
	}

	/**
	 * Open script editor for file
	 * 
	 * @param page
	 *            Where to open editor
	 * @param participant
	 *            File input
	 * @return Returns opened editor
	 */
	public static IEditorPart openEditor(IWorkbenchPage page,
			Category participant) {
		try {
			return page.openEditor(new ScriptEditorInput(participant),
					ScriptEditor.ID);
		} catch (PartInitException e) {
			displayError(e.getMessage());
			LOGe.log(Level.SEVERE, e.getMessage());
		}
		return null;
	}

	/**
	 * Get view by ID
	 * 
	 * @param viewID
	 *            View ID
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
	 * @param view
	 *            Workbench View
	 * @return Returns status line manager
	 */
	public static IStatusLineManager getStatusLine(IViewPart view) {
		return view.getViewSite().getActionBars().getStatusLineManager();
	}

	/**
	 * Display error dialog
	 * 
	 * @param errorMsg
	 *            Error message
	 */
	public static void displayError(String errorMsg) {
		MessageDialog.openError(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), "Error", errorMsg);
	}

	/**
	 * Open directory dialog
	 * 
	 * @param filter
	 *            Starting directory
	 * @return Returns path to selected directory
	 */
	public static String openDirectoryDialog(String filter) {
		DirectoryDialog dlg = new DirectoryDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell());
		dlg.setFilterPath(filter);

		dlg.setText(pathDialogTitle);
		dlg.setMessage(pathDialogDescription);
		return dlg.open();
	}

	/**
	 * Create new Open or Save As... file dialog
	 * 
	 * @param filter
	 *            Starting directory. If null use Workspace directory.
	 * @param name
	 *            Name of the file to save. If name is null create Open dialog
	 *            otherwise create Save As... dialog
	 * @param ext
	 *            File extension types as defined in KWindow
	 * @return Returns path to selected file
	 */
	private static String fileDialog(String filter, String name, int ext) {
		int dialogFlags = 0;
		if (name != null)
			dialogFlags = SWT.SAVE;
		else
			dialogFlags = SWT.OPEN;

		if (filter == null) {
			filter = Workspace.getInstance().getPath();
		}
		
		FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), dialogFlags);
		fileDialog.setFilterPath(filter);
		fileDialog.setText(fileDialogTitle);
		if (name != null)
			fileDialog.setFileName(name);

		List<String> extentions = new ArrayList<String>();
		List<String> extentionNames = new ArrayList<String>();

		if (ext == ALL) {
			extentions.add("*.*");
			extentionNames.add("Allfiles");
		}

		if ((ext & TXT) == 1) {
			extentions.add("*.txt");
			extentionNames.add("Textfiles(*.txt)");
		}
		
		if ((ext & LOG) == 1) {
			extentions.add("*.log");
			extentionNames.add("Logfiles(*.log)");
		}
		
		fileDialog.setFilterExtensions(extentions.toArray(new String[extentions
				.size()]));
		fileDialog.setFilterNames(extentionNames
				.toArray(new String[extentionNames.size()]));
		return fileDialog.open();
	}

	/**
	 * Open file dialog
	 * 
	 * @param filter
	 *            Starting directory
	 * @param ext
	 *            File extension types as defined in KWindow
	 * @return Returns path to selected file
	 */
	public static String openFileDialog(String filter, int ext) {
		return fileDialog(filter, null, ext);
	}

	/**
	 * Save As... file dialog
	 * 
	 * @param filter
	 *            Starting directory
	 * @param name
	 *            Name of the file to save
	 * @param ext
	 *            File extension types as defined in KWindow
	 * @return Returns path to selected file
	 */
	public static String saveFileDialog(String filter, String name, int ext) {
		return fileDialog(filter, name, ext);
	}
}
