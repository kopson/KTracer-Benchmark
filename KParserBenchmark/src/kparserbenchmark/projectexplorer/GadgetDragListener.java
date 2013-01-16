package kparserbenchmark.projectexplorer;

import java.util.Iterator;

import org.eclipse.ui.part.PluginTransfer;
import org.eclipse.ui.part.PluginTransferData;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.dnd.*;

/**
 * Supports dragging gadgets from a structured viewer.
 */
public class GadgetDragListener extends DragSourceAdapter {
	private StructuredViewer viewer;

	public GadgetDragListener(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Method declared on DragSourceListener
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void dragFinished(DragSourceEvent event) {
		if (!event.doit)
			return;
		// if the gadget was moved, remove it from the source viewer
		if (event.detail == DND.DROP_MOVE) {
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			for (Iterator it = selection.iterator(); it.hasNext();) {
				//((ProjectLeaf) it.next()).setParent(null);
				//TODO: Remove children from project here
			}
			viewer.refresh();
		}
	}

	/**
	 * Method declared on DragSourceListener
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		ProjectLeaf[] gadgets = (ProjectLeaf[]) selection.toList().toArray(
				new ProjectLeaf[selection.size()]);
		if (GadgetTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = gadgets;
		} else if (PluginTransfer.getInstance().isSupportedType(event.dataType)) {
			byte[] data = GadgetTransfer.getInstance().toByteArray(gadgets);
			event.data = new PluginTransferData(
					"org.eclipse.ui.examples.gdt.gadgetDrop", data);
		}
	}

	/**
	 * Method declared on DragSourceListener
	 */
	@Override
	public void dragStart(DragSourceEvent event) {
		event.doit = !viewer.getSelection().isEmpty();
	}
}
