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
import kparserbenchmark.utils.InvalidPathException;
import kparserbenchmark.utils.KFile;
import kparserbenchmark.utils.KTrace;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.fieldassist.ControlDecoration;
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

/**
 * Wizard class for creating new project 
 * 
 * Review history: 
 * Rev 1: [19.01.2013] Kopson: 
 * 		STATUS: Complete
 * 
 * @author Kopson
 */
public class NewProjectPage extends WizardPage {

	// Project attributes
	private Text projectName;
	private Text projectPath;
	private ProjectNode.ProjectTypes projectType;
	private Text projectDescription;
	private Text projectSummary;

	// Project types radio buttons
	private Button[] projectTypes;

	// Global error in dialog input data
	private int invalidData;

	// Error flags
	private static final int NO_ERROR = 0x0000;
	private static final int NAME_ERROR = 0x000F;
	private static final int PATH_ERROR = 0x00F0;

	// Page labels
	private static final String title = "New project wizard";
	private static final String description = "Create new KTrace project";
	private static final String description2 = "Set KTrace project name";
	private static final String description3 = "Set KTrace project path";
	private static final String description4 = "Invalid input data";

	private static final String nameLabel = "Project name: ";
	private static final String typeLabel = "Project type: ";
	private static final String pathLabel = "Project path: ";
	private static final String defaultPathLabel = "Select default location";
	private static final String descriptionLabel = "Project description: ";
	private static final String summaryLabel = "Project summary: ";
	private static final String projectNameValidator = "Please use only letters and digits";
	private static final String projectPathValidator = "Path does not exist";
	private static final String projectNameDupVal = "Project with this name already exists";

	/**
	 * The constructor
	 */
	public NewProjectPage() {
		super(title);
		setTitle(title);
		setDescription(description2);
		invalidData = NO_ERROR;
	}

	@SuppressWarnings("unused")
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		ControlDecoration nameDecor = createProjectName(container);
		ControlDecoration pathDecor = createProjectPath(container, nameDecor);
		createProjectType(container);
		createProjectDescriptionAndSummary(container);

		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
	}

	/**
	 * Create project name selection
	 * 
	 * @param container
	 *            Parent controller
	 * @return Returns name field decoration
	 */
	private ControlDecoration createProjectName(Composite container) {
		Label projectNameLabel = new Label(container, SWT.NULL);
		projectNameLabel.setText(nameLabel);
		projectName = new Text(container, SWT.BORDER | SWT.SINGLE);

		final ControlDecoration nameDecorator = KWindow.createLabelDecoration(
				projectName, projectNameValidator);

		projectName.setText("");
		projectName.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			/**
			 * Check if project name is not duplicated, invalid or incomplete
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				checkName(nameDecorator, ((Text) e.getSource()).getText());
			}
		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectName.setLayoutData(gd);
		return nameDecorator;
	}

	/**
	 * Create project path selection
	 * 
	 * @param container
	 *            Parent controller
	 * @param nameDecorator
	 *            name field decorator
	 * 
	 * @return Returns path field decoration
	 */
	private ControlDecoration createProjectPath(Composite container,
			final ControlDecoration nameDecorator) {
		Label projectDefaultPathLabel = new Label(container, SWT.NULL);
		projectDefaultPathLabel.setText("");

		final Button checkButton = new Button(container, SWT.CHECK);
		checkButton.setText(defaultPathLabel);

		Label projectPathLabel = new Label(container, SWT.NULL);
		projectPathLabel.setText(pathLabel);

		Composite subContainer = new Composite(container, SWT.NULL);
		GridLayout subLayout = new GridLayout();
		subLayout.numColumns = 3;
		subContainer.setLayout(subLayout);

		projectPath = new Text(subContainer, SWT.BORDER | SWT.SINGLE);
		projectPath.setText(Workspace.getInstance().getPath());
		projectPath.setEnabled(false);

		final ControlDecoration pathDecorator = KWindow.createLabelDecoration(
				projectPath, projectNameValidator);

		checkButton.setSelection(true);
		checkButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (checkButton.getSelection()) {
					projectPath.setEnabled(false);
					pathDecorator.hide();
					invalidData &= ~PATH_ERROR;
					checkIsComplete();
				} else {
					projectPath.setEnabled(true);
					checkPath(pathDecorator, nameDecorator,
							projectPath.getText());
				}
			}
		});
		new Label(subContainer, SWT.NONE);
		// Clicking the button will allow the user
		// to select a directory
		Button button = new Button(subContainer, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String dir = KWindow.openDirectoryDialog(projectPath.getText());
				if (dir != null) {
					projectPath.setText(dir);
					pathDecorator.hide();
					invalidData &= ~PATH_ERROR;
					checkIsComplete();
				}
			}
		});

		projectPath.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				checkPath(pathDecorator, nameDecorator,
						((Text) e.getSource()).getText());
			}

		});

		projectPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
			KFile f = new KFile(projectPath.getText() + File.separator + source);
			try {
				if (f.isNameValid(false)) {
					txtDecorator.hide();
					invalidData &= ~NAME_ERROR;
				}
			} catch (DuplicatedPathException e1) {
				txtDecorator.setDescriptionText(projectNameDupVal);
				txtDecorator.show();
				invalidData |= NAME_ERROR;
			} catch (InvalidPathException e1) {
				txtDecorator.setDescriptionText(projectNameValidator);
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
	private void checkPath(ControlDecoration pathDecorator,
			ControlDecoration nameDecorator, String source) {
		if (source != null) {
			KFile f = new KFile(source);
			try {
				if (f.isPathNameValid(false)) {
					pathDecorator.hide();
					invalidData &= ~PATH_ERROR; // Turn off error bit
				}
			} catch (DuplicatedPathException e1) {
				pathDecorator.hide();
				invalidData &= ~PATH_ERROR; // Turn off error bit
			} catch (InvalidPathException e1) {
				pathDecorator.setDescriptionText(projectPathValidator);
				pathDecorator.show();
				invalidData |= PATH_ERROR; // Turn on error bit
			}
		}
		checkName(nameDecorator, projectName.getText());
		checkIsComplete();
	}

	/**
	 * Create project type radio button selection
	 * 
	 * @param container
	 *            parent controller
	 */
	private void createProjectType(Composite container) {
		Label projectTypeLabel = new Label(container, SWT.NULL);
		projectTypeLabel.setText(typeLabel);

		Composite buttonContainer = new Composite(container, SWT.NULL);
		GridLayout buttonLayout = new GridLayout();
		buttonContainer.setLayout(buttonLayout);
		buttonLayout.numColumns = 4;

		projectTypes = new Button[KTrace.getTypesSize()];
		for (int i = 0; i < KTrace.getTypesSize(); ++i) {
			projectTypes[i] = new Button(buttonContainer, SWT.RADIO);
			projectTypes[i].setText(KTrace.ProjectTypes[i]);
		}
		projectTypes[0].setSelection(true);
	}

	/**
	 * Create project description and summary controls
	 * 
	 * @param container
	 *            parent controller
	 */
	private void createProjectDescriptionAndSummary(Composite container) {
		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);

		Label projectDescLabel = new Label(container, SWT.NULL);
		projectDescLabel.setText(descriptionLabel);
		projectDescription = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectDescription.setLayoutData(gd);

		Label projectSummaryLabel = new Label(container, SWT.NULL);
		projectSummaryLabel.setText(summaryLabel);
		projectSummary = new Text(container, SWT.BORDER | SWT.SINGLE);
		projectSummary.setLayoutData(gd);
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

		if (projectName.getText().isEmpty()) {
			setDescription(description2);
			ret = false;
		}

		if (projectPath.getText().isEmpty()) {
			setDescription(description3);
			ret = false;
		}

		int i = 0;
		for (Button button : projectTypes) {
			if (button.getSelection()) {
				if (i == 0)
					projectType = ProjectNode.ProjectTypes.SCHEDULER;
				else if (i == 1)
					projectType = ProjectNode.ProjectTypes.TEST;
			}
			++i;
		}

		if (ret) {
			setDescription(description);
			setPageComplete(true);
		} else
			setPageComplete(false);
		return ret;
	}

	/********** Getters/Setters **********/
	public String getProjectName() {
		return projectName.getText();
	}

	public ProjectNode.ProjectTypes getProjectType() {
		return projectType;
	}

	public String getProjectPath() {
		return projectPath.getText();
	}

	public String getProjectDescription() {
		return projectDescription.getText();
	}

	public String getProjectSummary() {
		return projectSummary.getText();
	}
	/*************************************/
}