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

import kparserbenchmark.projectexplorer.ProjectExplorer;
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
import org.eclipse.ui.IViewPart;

/**
 * Create new file wizard's page
 * 
 * Review history: Rev 1: [18.01.2013] Kopson: STATUS: Complete
 * 
 * @author Kopson
 */
public class NewProjectFilePage extends WizardPage {

	/** File name */
	private Text fileName;

	/** File path */
	private Text filePath;

	/** Global error flag in dialog input data. If set - blocks finish button */
	private int invalidData;

	/** If true it is a file wizard page, if false - directory wizard page */
	private boolean isFileWizard;
	/**
	 * Error types. Each error type can enable one bit in invalidData. Dialog
	 * can be closed only if all error bits are disabled
	 */
	private static final int NO_ERROR = 0x0000;
	private static final int NAME_ERROR = 0x000F;
	private static final int PATH_ERROR = 0x00F0;

	/** String constants */
	private static final String fileNameValidator = "Please use only letters and digits";
	private static final String fileNameDupVal = " with this name already exists";
	private static final String description2 = "Set name";
	private static final String description3 = "Set path";
	private static final String description4 = "Invalid input data";
	private static final String description = "Create new ";

	/**
	 * The constructor
	 */
	public NewProjectFilePage(boolean isFile) {
		super(isFile ? "New File" : "New Directory");
		isFileWizard = isFile;
		setTitle(isFile ? "New File" : "New Directory");
		setDescription(description + (isFile ? "file" : "directory"));
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		ControlDecoration nameDecor = createProjectName(container);
		ControlDecoration pathDecor = createProjectPath(container, nameDecor);
		createFileBrowser(container, nameDecor, pathDecor);

		setControl(container);
		setPageComplete(false);
		invalidData = NO_ERROR;
	}

	/**
	 * Create file browser
	 * 
	 * @param container
	 *            Widget controller
	 * @param nameDecor
	 *            name decorator
	 * @param pathDecor
	 *            path decorator
	 */
	private void createFileBrowser(Composite container,
			final ControlDecoration nameDecor, final ControlDecoration pathDecor) {
		new Label(container, SWT.NONE);
		Composite fileContainer = new Composite(container, SWT.SINGLE
				| SWT.BORDER);
		fileContainer.setLayout(new GridLayout());

		FileBrowser browser = new FileBrowser(fileContainer, Workspace
				.getInstance().getPath());
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		fileContainer.setLayoutData(gridData);
		browser.getTree().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browser.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				File selectedNode = (File) ((IStructuredSelection) event
						.getSelection()).getFirstElement();

				if (selectedNode.isFile() && isFileWizard) {
					fileName.setText(selectedNode.getName());
					checkName(nameDecor, fileName.getText());
				} else if (selectedNode.isDirectory() && isFileWizard) {
					filePath.setText(selectedNode.getPath());
					checkPath(pathDecor, nameDecor, filePath.getText());
				} else if (selectedNode.isDirectory()
						&& !isFileWizard
						&& selectedNode.getParent().equals(
								Workspace.getInstance().getPath())) {
					filePath.setText(selectedNode.getPath());
					checkPath(pathDecor, nameDecor, filePath.getText());
				} else if (selectedNode.isDirectory()
						&& !isFileWizard) {
					fileName.setText(selectedNode.getName());
					checkName(nameDecor, fileName.getText());
				}
			}
		});
	}

	/**
	 * Create file name
	 * 
	 * @param container
	 *            Widget controller
	 * @return Returns controller decoration
	 */
	private ControlDecoration createProjectName(Composite container) {
		Label nameLabel = new Label(container, SWT.NULL);
		nameLabel.setText("File name: ");

		fileName = new Text(container, SWT.BORDER | SWT.SINGLE);
		fileName.setText("");

		final ControlDecoration nameDecorator = KWindow.createLabelDecoration(
				fileName, fileNameValidator);

		fileName.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				checkName(nameDecorator, ((Text) e.getSource()).getText());
			}

		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fileName.setLayoutData(gd);
		return nameDecorator;
	}

	/**
	 * Create file path
	 * 
	 * @param container
	 *            Widget controller
	 * @param nameDecorator name decorator
	 * @return Returns controller decoration
	 */
	private ControlDecoration createProjectPath(Composite container, final ControlDecoration nameDecorator) {
		Label filePathLabel = new Label(container, SWT.NULL);
		filePathLabel.setText("Path: ");

		Composite subContainer = new Composite(container, SWT.NULL);
		GridLayout subLayout = new GridLayout();
		subLayout.numColumns = 3;
		subContainer.setLayout(subLayout);

		IViewPart view = KWindow.getView(ProjectExplorer.ID);
		ProjectNode currSelection = ((ProjectExplorer) view).getSelectedNode();

		filePath = new Text(subContainer, SWT.BORDER | SWT.SINGLE);
		ProjectNode currProject = Workspace.getCurrProject();
		String basePath;

		if (currSelection != null)
			basePath = currSelection.getPathName();
		else if (currProject != null)
			basePath = currProject.getPathName();
		else
			basePath = Workspace.getInstance().getPath();

		filePath.setText(basePath);
		filePath.setEnabled(true);

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
				checkPath(pathDecorator, nameDecorator, ((Text) e.getSource()).getText());
			}

		});

		filePath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		subContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return pathDecorator;
	}

	/**
	 * Check if file name is valid
	 * 
	 * @param txtDecorator
	 *            Field validation decoration
	 * @param source
	 *            String to check
	 */
	private void checkName(ControlDecoration txtDecorator, String source) {
		if (source != null) {
			KFile f = new KFile(filePath.getText() + File.separator + source);
			try {
				if (f.isNameValid(isFileWizard)) {
					txtDecorator.hide();
					invalidData &= ~NAME_ERROR;
				}
			} catch (DuplicatedPathException e1) {
				txtDecorator.setDescriptionText((isFileWizard ? "File" : "Directory") + fileNameDupVal);
				txtDecorator.show();
				invalidData |= NAME_ERROR;
			} catch (InvalidPathException e1) {
				txtDecorator.setDescriptionText(fileNameValidator);
				txtDecorator.show();
				invalidData |= NAME_ERROR;
			}
		}
		checkIsComplete();
	}

	/**
	 * Check if project path is valid
	 * 
	 * @param pathDecorator
	 *            Field validation decoration
	 * @param source
	 *            String to check
	 */
	private void checkPath(ControlDecoration pathDecorator, ControlDecoration nameDecorator, String source) {
		if (source != null) {
			KFile f = new KFile(source);
			try {
				if (f.isPathNameValid()) {
					pathDecorator.hide();
					invalidData &= ~PATH_ERROR; // Turn off error bit
				}
			} catch (DuplicatedPathException e1) {
				pathDecorator.hide();
				invalidData &= ~PATH_ERROR; // Turn off error bit
			} catch (InvalidPathException e1) {
				pathDecorator.setDescriptionText(fileNameValidator);
				pathDecorator.show();
				invalidData |= PATH_ERROR; // Turn on error bit
			}
		}
		checkName(nameDecorator, fileName.getText());
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
			setDescription(description + (isFileWizard ? "file" : "directory"));
			setPageComplete(true);
		} else
			setPageComplete(false);
		return ret;
	}

	/**
	 * Get full file name
	 * 
	 * @return Returns path and file name
	 */
	public String getFileName() {
		return filePath.getText() + File.separator + fileName.getText();
	}
}