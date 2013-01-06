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

import kparserbenchmark.commands.OpenProjectAction;
import kparserbenchmark.consoleView.ConsolePanel;
import kparserbenchmark.projectexplorer.ProjectExplorer;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
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
	 * Open Console view Action
	 * TODO: It doesn't work!
	 */
	private ViewAction openConsoleAction;
	
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

		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);
		
		openProjectExplorerAction = new ViewAction(window, ProjectExplorer.ID, "ProjectExplorer");
		openConsoleAction = new ViewAction(window, ConsolePanel.ID, "Console");
		register(openProjectExplorerAction);
		register(openConsoleAction);
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
	private class ViewAction extends Action implements IWorkbenchAction{
		
		//Window
		private IWorkbenchWindow window;
		
		//ViewId;
		private String viewID;
		
		/**
		 * The constructor
		 * 
		 * @param window Window
		 * @param id View ID
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
