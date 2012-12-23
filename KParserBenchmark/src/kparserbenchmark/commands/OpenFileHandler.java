package kparserbenchmark.commands;

import java.io.File;

import kparserbenchmark.application.Application;
import kparserbenchmark.projectexplorer.Category;
import kparserbenchmark.projectexplorer.Project;
import kparserbenchmark.projectexplorer.Workspace;
import kparserbenchmark.utils.KWindow;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

public class OpenFileHandler implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Project p = Application.getCurrProject();
		String file;
		if(p != null)
			file = KWindow.openFileDialog(p.getPath(), KWindow.ALL);
		else
			file = KWindow.openFileDialog(Workspace.getInstance().getPath(), KWindow.ALL);
		if(file != null)
			KWindow.openEditor(KWindow.getPage(), new Category(null, file,
				new File(file).getName()));
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

}
