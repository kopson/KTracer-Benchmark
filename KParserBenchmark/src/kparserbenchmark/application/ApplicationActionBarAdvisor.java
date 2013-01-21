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

package kparserbenchmark.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kparserbenchmark.commands.OpenProjectAction;
import kparserbenchmark.consoleView.ConsolePanel;
import kparserbenchmark.projectexplorer.ProjectExplorer;
import kparserbenchmark.projects.test.Record;
import kparserbenchmark.projects.test.TableEditor;
import kparserbenchmark.utils.KImage;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * Creates and fills main menu items
 * 
 * @author kopson
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	/**
	 * Open project Action
	 */
	private OpenProjectAction openProjectAction;

	/**
	 * Open preference window Action
	 */
	private IAction preferencesAction;

	/**
	 * Open ProjectExplorer view Action
	 */
	private ViewAction openProjectExplorerAction;

	/**
	 * Open Console view Action TODO: It doesn't work!
	 */
	private ViewAction openConsoleAction;

	public static CopyAction copyAction;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.ActionBarAdvisor
	 */
	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(IWorkbenchWindow window) {
		openProjectAction = new OpenProjectAction(window);
		register(openProjectAction);

		copyAction = new CopyAction();
		register(copyAction);
		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);

		openProjectExplorerAction = new ViewAction(window, ProjectExplorer.ID,
				"ProjectExplorer");
		openConsoleAction = new ViewAction(window, ConsolePanel.ID, "Console");
		register(openProjectExplorerAction);
		register(openConsoleAction);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
		// super.fillCoolBar(coolBar);
		IToolBarManager toolbar = new ToolBarManager(coolBar.getStyle()
				| SWT.BOTTOM);
		coolBar.add(toolbar);

		ActionContributionItem copyeditorCI = new ActionContributionItem(
				copyAction);
		copyeditorCI.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolbar.add(copyeditorCI);
		toolbar.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File",
				"KParserBenchmark.fileMenu");
		menuBar.add(fileMenu);
		MenuManager projectMenu = new MenuManager("&Project",
				"KParserBenchmark.projectMenu");
		projectMenu.add(openProjectAction);
		menuBar.add(projectMenu);
		MenuManager toolsMenu = new MenuManager("&Tools",
				"KParserBenchmark.toolsMenu");
		MenuManager toolsViewMenu = new MenuManager("&View",
				"KParserBenchmark.toolsViewMenu");
		toolsViewMenu.add(openProjectExplorerAction);
		toolsViewMenu.add(openConsoleAction);
		toolsMenu.add(toolsViewMenu);
		toolsMenu.add(preferencesAction);
		menuBar.add(toolsMenu);
	}

	/**
	 * Template for show View Actions
	 */
	private class CopyAction extends Action implements IWorkbenchAction,
			ISelectionListener {

		public CopyAction() {
			setText("Copy");
			setId("KParserBenchmark.commands.CopyAction");
			setEnabled(false);
			setToolTipText("Copy editor input");
			setImageDescriptor(KImage
					.getImageDescriptor(KImage.IMG_STOP_CONSOLE));
			setDisabledImageDescriptor(KImage
					.getImageDescriptor(KImage.IMG_STOP_CONSOLE));
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			IEditorPart editor = window.getActivePage().getActiveEditor();
			Clipboard cb = new Clipboard(Display.getDefault());
			ISelection selection = editor.getSite().getSelectionProvider()
					.getSelection();
			List<Record> personList = new ArrayList<Record>();
			if (selection != null && selection instanceof IStructuredSelection) {
				IStructuredSelection sel = (IStructuredSelection) selection;
				for (Iterator<Record> iterator = sel.iterator(); iterator
						.hasNext();) {
					Record person = iterator.next();
					personList.add(person);
				}
			}
			StringBuilder sb = new StringBuilder();
			for (Record record : personList) {
				sb.append(record.toString());
			}
			TextTransfer textTransfer = TextTransfer.getInstance();
			cb.setContents(new Object[] { sb.toString() },
					new Transfer[] { textTransfer });

		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if (part instanceof TableEditor) {
				if (selection != null
						&& selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					if (sel.getFirstElement() instanceof Record) {
						setEnabled(true);
						return;
					}
				}
			}
			setEnabled(false);
		}
	};

	/**
	 * Template for show View Actions
	 */
	private class ViewAction extends Action implements IWorkbenchAction {

		// Window
		private IWorkbenchWindow window;

		// ViewId;
		private String viewID;

		/**
		 * The constructor
		 * 
		 * @param window
		 *            Window
		 * @param id
		 *            View ID
		 */
		public ViewAction(IWorkbenchWindow window, String id, String name) {
			this.window = window;
			viewID = id;
			setId("KParserBenchmark.commands." + name);
			setText(name);
			setToolTipText("Open " + name + " view");
		}

		@Override
		public void run() {
			KWindow.showView(window, viewID);
		}

		@Override
		public void dispose() {
		}
	}
}
