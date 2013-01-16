package kparserbenchmark.commands;

import kparserbenchmark.projectexplorer.ProjectLeaf;
import kparserbenchmark.projectexplorer.GadgetTransfer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;

/**
 * Action for copying a selection of gadgets to the clipboard
 */
public class CopyFileAction extends Action {
	protected Clipboard clipboard;
	protected StructuredViewer viewer;

	public CopyFileAction(StructuredViewer viewer, Clipboard clipboard) {
		super("Copy");
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
	}
}