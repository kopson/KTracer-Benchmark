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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.*;
import java.util.Iterator;

import kparserbenchmark.projectexplorer.ProjectItem.ItemTypes;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;

/**
 * Supports dragging gadgets from a structured viewer.
 * 
 * @author Kopson
 */
public class GadgetDragListener extends DragSourceAdapter {

	// Tree viewer
	private StructuredViewer viewer;

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
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void dragFinished(DragSourceEvent event) {
		System.out.println("dragFinished");
		if (!event.doit)
			return;
		// if the gadget was moved, remove it from the source viewer
		if (event.detail == DND.DROP_MOVE) {
			System.out.println("DROP_MOVE");
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			for (Iterator it = selection.iterator(); it.hasNext();) {
				ProjectLeaf child = (ProjectLeaf) it.next();
				System.out.println("Remove " + child.getPath());
				ProjectNode parent = (ProjectNode) (child.getParent());
				System.out.println("Paste " +parent.getPath() + File.separator + child.getName());
				try {
					Files.move(Paths.get(child.getPath()), Paths.get(parent.getPath() + File.separator + child.getName()),
							REPLACE_EXISTING);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				parent.removeChild(child, false);
			}
			viewer.refresh();
		} else {
			System.out.println(" NOT DROP_MOVE");
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
		}
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
