package kparserbenchmark.intro;

import kparserbenchmark.projectexplorer.FileBrowser;
import kparserbenchmark.projectexplorer.ProjectExplorer;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Main perspective
 * 
 * @author kopson
 */
public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
	    layout.addView(ProjectExplorer.ID, IPageLayout.LEFT, 0.5f, layout.getEditorArea());
	    layout.addView(FileBrowser.ID, IPageLayout.RIGHT, 0.5f, ProjectExplorer.ID);
	}
}
