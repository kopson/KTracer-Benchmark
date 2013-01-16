package kparserbenchmark.commands;

import kparserbenchmark.projectexplorer.ProjectLeaf;
import kparserbenchmark.projectexplorer.GadgetTransfer;
import kparserbenchmark.projectexplorer.ProjectNode;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;

/**
 * Action for pasting a selection of gadgets from the clipboard
 * into a tree viewer.
 */
public class PasteFileAction extends Action {
   protected Clipboard clipboard;
   protected StructuredViewer viewer;
   
   public PasteFileAction(StructuredViewer viewer, Clipboard clipboard) {
      super("Paste");
      this.viewer = viewer;
      this.clipboard = clipboard;
   }
   
   public void run() {
      IStructuredSelection sel = (IStructuredSelection)viewer.getSelection();
      //parent of the pasted object is the current selection,
      //or the viewer input if nothing is selected
      ProjectNode parent = (ProjectNode)sel.getFirstElement();
      if (parent == null)
         parent = (ProjectNode)viewer.getInput();
      ProjectLeaf[] gadgets = (ProjectLeaf[])clipboard.getContents(GadgetTransfer.getInstance());
      if (gadgets == null)
         return;
      //cannot drop a gadget onto itself or a child
      //for (int i = 0; i < gadgets.length; i++)
      //   if (gadgets[i].equals(parent) || parent.hasParent(gadgets[i]))
      //      return;
      for (int i = 0; i < gadgets.length; i++) {
         gadgets[i].setParent(parent);
      }
      viewer.refresh();
   }
}