package kparserbenchmark.editor;

import kparserbenchmark.utils.KWindow;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.EditorInputTransfer;

public class EditorAreaDropAdapter extends DropTargetAdapter {

	public EditorAreaDropAdapter(IWorkbenchWindow window) {

	}

	@Override
	public void drop(DropTargetEvent event) {
		System.out.println("Dupa2");
		super.drop(event);
		if (EditorInputTransfer.getInstance().isSupportedType(
				event.currentDataType)) {
			EditorInputTransfer.EditorInputData[] editorInputs = (EditorInputTransfer.EditorInputData[]) event.data;
			for (int i = 0; i < editorInputs.length; i++) {
				IEditorInput editorInput = editorInputs[i].input;
				String editorId = editorInputs[i].editorId;
				KWindow.openEditor(KWindow.getPage(), (ScriptEditorInput) editorInput, editorId);
			}
		}
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		super.dragEnter(event);
	}

	@Override
	public void dropAccept(DropTargetEvent arg0) {
		System.out.println("Dupa233444");
		super.dropAccept(arg0);
	}
	
	
}
