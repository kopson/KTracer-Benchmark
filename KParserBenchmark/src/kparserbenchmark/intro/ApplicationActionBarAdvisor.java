package kparserbenchmark.intro;

import kparserbenchmark.commands.OpenProjectAction;
import kparserbenchmark.commands.ScriptEditorAction;

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
	private OpenProjectAction openProjectAction;
	private ScriptEditorAction openEditorAction;

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
		
		openEditorAction = new ScriptEditorAction(window);
		register(openEditorAction);
	}

	@Override
	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File", "KParserBenchmark.fileMenu");
		fileMenu.add(openEditorAction);
		menuBar.add(fileMenu);
		MenuManager projectMenu = new MenuManager("&Project", "KParserBenchmark.projectMenu");
		projectMenu.add(openProjectAction);
		menuBar.add(projectMenu);
	}
}
