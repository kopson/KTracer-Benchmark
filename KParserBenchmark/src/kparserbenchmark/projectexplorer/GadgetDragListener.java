/*******************************************************************************
 Copyright (c) 2012 kopson kopson.piko@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 *******************************************************************************/

package kparserbenchmark.projectexplorer;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import kparserbenchmark.editor.ScriptEditor;
import kparserbenchmark.editor.ScriptEditorInput;
import kparserbenchmark.projectexplorer.ProjectItem.ItemTypes;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.ui.part.EditorInputTransfer;

/**
 * Supports dragging gadgets from a structured viewer.
 * 
 * @author Kopson
 */
public class GadgetDragListener extends DragSourceAdapter {

	/** Logger instance */
	private static final Logger LOG = Logger.getLogger(GadgetDragListener.class
			.getName());

	/** Tree viewer */
	private StructuredViewer viewer;

	/** Workaround for DND support in eclipse */
	public static Object globalEventData;

	/**
	 * The constructor
	 * 
	 * @param viewer
	 */
	public GadgetDragListener(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * Remove selected items from source tree branch
	 * 
	 * @param event
	 *            drag event
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void dragFinished(DragSourceEvent event) {
		if (!event.doit)
			return;
		/*
		 * if (event.data instanceof EditorInputTransfer.EditorInputData) {
		 * return; }
		 */

		// if the gadget was moved, remove it from the source viewer
		if (event.detail == DND.DROP_MOVE) {
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			for (Iterator it = selection.iterator(); it.hasNext();) {
				ProjectLeaf child = (ProjectLeaf) it.next();
				ProjectNode parent = null;
				if(child.getParent() instanceof ProjectNode) {
					parent = (ProjectNode) (child.getParent());
				} else {
					LOG.log(Level.SEVERE, "Can'f find item's parent node");
					return;
				}
				try {
					Files.move(
							Paths.get(child.getPath()),
							Paths.get(parent.getPath() + File.separator
									+ child.getName()), REPLACE_EXISTING);
				} catch (IOException e) {
					LOG.log(Level.SEVERE, e.getMessage());
				}
				parent.removeChild(child, false);
			}
			viewer.refresh();
		} else {
			// TODO: Add DROP_COPY handling here
		}
	}

	/**
	 * Fill event data with selected items
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		ProjectLeaf[] dragItems = (ProjectLeaf[]) selection.toList().toArray(
				new ProjectLeaf[selection.size()]);
		
		if (GadgetTransfer.getInstance().isSupportedType(event.dataType)) {
			event.data = dragItems;
			return;
		}

		if (EditorInputTransfer.getInstance().isSupportedType(event.dataType)) {
			EditorInputTransfer.EditorInputData[] inputs = new EditorInputTransfer.EditorInputData[dragItems.length];
			EditorInputTransfer.EditorInputData data;
			for (int i = 0; i < dragItems.length; i++) {
				data = EditorInputTransfer.createEditorInputData(
						ScriptEditor.ID, new ScriptEditorInput(dragItems[i]));
				if (data != null) {
					inputs[i] = data;
				}
			}
			event.data = inputs;
			// We create static container for event data because we permanently
			// got null reading event.data in drop() method
			globalEventData = inputs;
			return;
		}
		event.doit = false;
	}

	/**
	 * Allow to drag only ProjectLeaf elements
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void dragStart(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();

		if (viewer.getSelection().isEmpty()) {
			event.doit = false;
			return;
		}

		for (Iterator it = selection.iterator(); it.hasNext();) {
			Object o = it.next();
			if (o instanceof ProjectLeaf) {
				ProjectLeaf pl = (ProjectLeaf) o;
				if (pl.type == ItemTypes.CONFIG_FILE) {
					event.doit = false;
					return;
				}
			} else {
				event.doit = false;
				return;
			}
		}
		event.doit = true;
	}
}
