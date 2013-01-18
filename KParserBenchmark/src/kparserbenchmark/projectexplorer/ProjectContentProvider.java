package kparserbenchmark.projectexplorer;

import kparserbenchmark.projectexplorer.ProjectItem.ItemTypes;
import kparserbenchmark.projectexplorer.ProjectNode.Status;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Project Explorer content provider
 * 
 * @author kopson
 */
public class ProjectContentProvider implements ITreeContentProvider {

	// Content's provider data - list of all workspace projects
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
		return ((ProjectItem) parentElement).getChildren();
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ProjectNode
				&& !(((ProjectNode) element).getCurrStatus() == Status.OPENED))
			return false;
		if (((ProjectItem) element).getType() == ItemTypes.FOLDER
				|| ((ProjectItem) element).getType() == ItemTypes.NODE) {
			return ((ProjectItem) element).getChildren() != null;
		}
		return false;
	}

}