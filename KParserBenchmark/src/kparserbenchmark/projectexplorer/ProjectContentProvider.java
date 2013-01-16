package kparserbenchmark.projectexplorer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Project Explorer content provider
 * 
 * @author kopson
 */
public class ProjectContentProvider implements ITreeContentProvider {

	//Content's provider data - list of all workspace projects
	private Workspace model;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.model = (Workspace) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return model.setProjects().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ProjectNode) {
			ProjectNode project = (ProjectNode) parentElement;
			return project.getChildren();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ProjectNode) {
			return true;
		}
		return false;
	}

}