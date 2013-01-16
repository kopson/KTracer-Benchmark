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

package kparserbenchmark.projectwizard;

import java.io.File;

import kparserbenchmark.projectexplorer.ProjectNode;
import kparserbenchmark.projectexplorer.Workspace;
import kparserbenchmark.utils.DuplicatedPathException;
import kparserbenchmark.utils.FileBrowser;
import kparserbenchmark.utils.InvalidPathException;
import kparserbenchmark.utils.KFile;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewProjectFilePage extends WizardPage {

	/** File name */
	private Text fileName;

	/** File path */
	private Text filePath;

	/** Parent control */
	private Composite container;

	// Global error in dialog input data
	private int invalidData;

	// Event source string
	private String string;

	// Error flags
	// private static final int NO_ERROR = 0x0000;
	private static final int NAME_ERROR = 0x000F;
	private static final int PATH_ERROR = 0x00F0;

	/** String constants */
	private static final String fileNameValidator = "Please use only letters and digits";
	private static final String fileNameDupVal = "File with this name already exists";
	private static final String description2 = "Set KTrace project name";
	private static final String description3 = "Set KTrace project path";
	private static final String description4 = "Invalid input data";
	private static final String description = "Create new file";

	/**
	 * The constructor
	 */
	public NewProjectFilePage() {
		super("New File");
		setTitle("New File");
		setDescription(description);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label nameLabel = new Label(container, SWT.NULL);
		nameLabel.setText("File name: ");

		fileName = new Text(container, SWT.BORDER | SWT.SINGLE);
		fileName.setText("");

		final ControlDecoration txtDecorator = KWindow.createLabelDecoration(
				fileName, fileNameValidator);

		fileName.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String string = ((Text) e.getSource()).getText();
				KFile f = new KFile(filePath + File.separator + string);
				try {
					if (f.isNameValid()) {
						txtDecorator.hide();
						invalidData &= ~NAME_ERROR;
					}
				} catch (DuplicatedPathException e1) {
					txtDecorator.setDescriptionText(fileNameDupVal);
					txtDecorator.show();
					invalidData |= NAME_ERROR;
				} catch (InvalidPathException e1) {
					txtDecorator.setDescriptionText(fileNameValidator);
					txtDecorator.show();
					invalidData |= NAME_ERROR;
				}
				checkIsComplete();
			}

		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fileName.setLayoutData(gd);

		Label filePathLabel = new Label(container, SWT.NULL);
		filePathLabel.setText("Path: ");

		Composite subContainer = new Composite(container, SWT.NULL);
		GridLayout subLayout = new GridLayout();
		subLayout.numColumns = 3;
		subContainer.setLayout(subLayout);

		filePath = new Text(subContainer, SWT.BORDER | SWT.SINGLE);
		ProjectNode p = Workspace.getCurrProject();
		String basePath;
		if (p == null)
			basePath = Workspace.getInstance().getPath();
		else
			basePath = p.getPath();

		filePath.setText(basePath);
		filePath.setEnabled(true);
		string = filePath.getText();

		final ControlDecoration pathDecorator = KWindow.createLabelDecoration(
				filePath, fileNameValidator);

		new Label(subContainer, SWT.NONE);
		Button button = new Button(subContainer, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String dir = KWindow.openDirectoryDialog(filePath.getText());
				if (dir != null) {
					filePath.setText(dir);
				}
			}
		});

		filePath.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				string = ((Text) e.getSource()).getText();
				checkPath(pathDecorator);
			}

		});

		filePath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		subContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(container, SWT.NONE);
		Composite fileContainer = new Composite(container, SWT.SINGLE
				| SWT.BORDER);
		fileContainer.setLayout(new GridLayout());
		FileBrowser browser = new FileBrowser(fileContainer, basePath);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		fileContainer.setLayoutData(gridData);
		browser.getTree().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browser.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				File selectedNode = (File) ((IStructuredSelection) event
						.getSelection()).getFirstElement();
				
				if (selectedNode.isFile())
					fileName.setText(selectedNode.getName());
				else
					filePath.setText(selectedNode.getPath());
			}
		});

		setControl(container);
		setPageComplete(false);
	}

	/**
	 * Check if project path is valid
	 * 
	 * @param txtDecorator
	 *            Validation decoration
	 */
	private void checkPath(ControlDecoration txtDecorator) {
		KFile f = new KFile(string);
		try {
			if (f.isPathNameValid()) {
				txtDecorator.hide();
				invalidData &= ~PATH_ERROR;
			}
		} catch (DuplicatedPathException e1) {
			txtDecorator.hide();
			invalidData &= ~PATH_ERROR;
		} catch (InvalidPathException e1) {
			txtDecorator.setDescriptionText(fileNameValidator);
			txtDecorator.show();
			invalidData |= PATH_ERROR;
		}
		checkIsComplete();
	}

	/**
	 * Check if all mandatory attributes were set
	 * 
	 * @return Returns true if all mandatory fields are complete false otherwise
	 */
	private boolean checkIsComplete() {
		boolean ret = true;

		if (invalidData != 0) {
			ret = false;
			setDescription(description4);
		}

		if (filePath.getText().isEmpty()) {
			setDescription(description3);
			ret = false;
		}

		if (fileName.getText().isEmpty()) {
			setDescription(description2);
			ret = false;
		}

		if (ret) {
			setDescription(description);
			setPageComplete(true);
		} else
			setPageComplete(false);
		return ret;
	}

	/**
	 * Get full file name
	 * 
	 * @return Path and file name
	 */
	public String getFileName() {
		return filePath.getText() + File.pathSeparator + fileName.getText();
	}
}