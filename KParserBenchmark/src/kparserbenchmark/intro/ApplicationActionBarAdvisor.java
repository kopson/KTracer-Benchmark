package kparserbenchmark.intro;

import kparserbenchmark.commands.OpenProjectAction;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * Creates and fills main menu items
 * 
 * @author kopson
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Open project Action
	private OpenProjectAction openAction;

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
		openAction = new OpenProjectAction(window);
		register(openAction);
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager mainMenu = new MenuManager("&Project", "KParserBenchmark.projectMenu");
		mainMenu.add(openAction);
		menuBar.add(mainMenu);
	}
}
