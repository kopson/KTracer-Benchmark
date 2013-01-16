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

import java.io.File;

import kparserbenchmark.projectexplorer.ProjectLeaf;
import kparserbenchmark.projectexplorer.Workspace;
import kparserbenchmark.utils.KFile;
import kparserbenchmark.utils.KWindow;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * Script editor class.
 * 
 * @author kopson
 * 
 */
public class ScriptEditor extends EditorPart {

	/**
	 * Script editor ID.
	 */
	public static String ID = "KParserBenchmark.editor.mainEditor";

	/**
	 * Editor body.
	 */
	private Text transcript;

	// Editor input file
	private ProjectLeaf inputFile;

	// Is file dirty
	boolean dirty;

	/**
	 * Used to save file in doSave() and doSaveAs() actions
	 * 
	 * @param path
	 *            File path to save
	 */
	private void save(String path) {
		KFile f = new KFile(path);
		f.clear();
		f.setText(transcript.getText());
		dirty = false;
		firePropertyChange(ISaveablePart.PROP_DIRTY);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		save(inputFile.getPath());
	}

	@Override
	public void doSaveAs() {
		String startingPath = inputFile.getParentPath();
		if (startingPath == null)
			startingPath = Workspace.getInstance().getPath();
		String file = KWindow.saveFileDialog(startingPath, inputFile.getName(),
				KWindow.ALL);
		if (file != null) {
			inputFile = new ProjectLeaf(null, file, new File(file).getName());
			setInputWithNotify(new ScriptEditorInput(inputFile));
			setPartName(inputFile.getName());
			firePropertyChange(IEditorPart.PROP_INPUT);
			firePropertyChange(IEditorPart.PROP_TITLE);
			save(file);
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(getScriptEditorName());
		dirty = false;
		inputFile = ((ScriptEditorInput) input).getCategory();
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		top.setLayout(layout);

		transcript = new Text(top, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		transcript.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, true));
		transcript.setEditable(true);
		transcript.setBackground(transcript.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		transcript.setForeground(transcript.getDisplay().getSystemColor(
				SWT.COLOR_BLACK));
		transcript.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent event) {
				super.keyReleased(event);
				setDirty();
			}

			@Override
			public void keyPressed(KeyEvent event) {
				super.keyPressed(event);
				// setDirty();
			}
		});

		transcript.setText(inputFile.getText());
	}

	@Override
	public void setFocus() {
		if (transcript != null && !transcript.isDisposed()) {
			transcript.setFocus();
		}
	}

	/**
	 * Returns script editor name.
	 * 
	 * @return script editor name.
	 */
	private String getScriptEditorName() {
		return ((ScriptEditorInput) getEditorInput()).getName();
	}

	/**
	 * Marks this file for save method
	 */
	private void setDirty() {
		dirty = true;
		firePropertyChange(ISaveablePart.PROP_DIRTY);
	}
}
