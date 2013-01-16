package kparserbenchmark.commands;

import kparserbenchmark.projectexplorer.ProjectLeaf;
import kparserbenchmark.projectexplorer.GadgetTransfer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;

/**
 * Class for cutting a selection of gadgets from a view, and placing them on the
 * clipboard.
 */
public class CutFileAction extends Action {
	protected Clipboard clipboard;
	protected StructuredViewer viewer;

	public CutFileAction(StructuredViewer viewer, Clipboard clipboard) {
		super("Cut");
		this.viewer = viewer;
		this.clipboard = clipboard;
	}

	@SuppressWarnings("unchecked")
	public void run() {
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		ProjectLeaf[] gadgets = (ProjectLeaf[]) sel.toList().toArray(
				new ProjectLeaf[sel.size()]);
		clipboard.setContents(new Object[] { gadgets },
				new Transfer[] { GadgetTransfer.getInstance() });
		for (int i = 0; i < gadgets.length; i++) {
			gadgets[i].setParent(null);
		}
		viewer.refresh();
	}
}