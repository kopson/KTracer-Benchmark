package kparserbenchmark.application;

import kparserbenchmark.commands.OpenProjectAction;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * Creates and fills main menu items
 * 
 * @author kopson
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Open project Action
	private OpenProjectAction openProjectAction;
	private IAction preferencesAction;

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
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File", "KParserBenchmark.fileMenu");
		menuBar.add(fileMenu);
		MenuManager projectMenu = new MenuManager("&Project", "KParserBenchmark.projectMenu");
		projectMenu.add(openProjectAction);
		menuBar.add(projectMenu);
		MenuManager toolsMenu = new MenuManager("&Tools", "KParserBenchmark.toolsMenu");
		toolsMenu.add(preferencesAction);
		menuBar.add(toolsMenu);
	}
}
