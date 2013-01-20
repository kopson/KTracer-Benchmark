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

package kparserbenchmark.editor;

import java.util.logging.Level;
import java.util.logging.Logger;

import kparserbenchmark.projectexplorer.GadgetDragListener;
import kparserbenchmark.utils.KWindow;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.EditorInputTransfer;

/**
 * Handler for drop events which target is an editor area
 * 
 * @author Kopson
 *
 */
public class EditorAreaDropAdapter extends DropTargetAdapter {

	/** Workbench window */
	@SuppressWarnings("unused")
	private IWorkbenchWindow window;

	/** Logger instance */
	private static final Logger LOG = Logger.getLogger(EditorAreaDropAdapter.class.getName());

	/**
	 * The constructor 
	 * 
	 * @param window workbench window
	 */
	public EditorAreaDropAdapter(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void drop(DropTargetEvent event) {
		if (EditorInputTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			EditorInputTransfer.EditorInputData[] editorInputs = 
					(EditorInputTransfer.EditorInputData[]) event.data;
			EditorInputTransfer.EditorInputData[] globalEditorInputs = 
					(EditorInputTransfer.EditorInputData[]) GadgetDragListener.globalEventData;

			for (int i = 0; i < editorInputs.length; i++) {
				EditorInputTransfer.EditorInputData input = null;
				if (editorInputs[i] != null) {
					input = editorInputs[i];
				} else if (globalEditorInputs[i] != null) {
					input = globalEditorInputs[i];
				} else {
					LOG.log(Level.SEVERE, "No valid event data found");
					return;
				}
					IEditorInput editorInput = input.input;
					String editorId = input.editorId;
					KWindow.openEditor(KWindow.getPage(), (ScriptEditorInput) editorInput, editorId);
			}
			event.detail = DND.DROP_COPY;
		}
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		// DND.DROP_DEFAULT allows drop target to set default operation.
		if (event.detail == DND.DROP_DEFAULT) {
			if ((event.operations & DND.DROP_COPY) != 0) {
				event.detail = DND.DROP_COPY;
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		event.feedback = DND.FEEDBACK_NONE;
	}
}
