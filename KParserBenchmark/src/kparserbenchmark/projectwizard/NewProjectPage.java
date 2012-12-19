package kparserbenchmark.projectwizard;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kparserbenchmark.KTrace;
import kparserbenchmark.KWindow;
import kparserbenchmark.projectexplorer.Project;
import kparserbenchmark.projectexplorer.Workspace;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard class for creating new project
 * 
 * @author root
 */
public class NewProjectPage extends WizardPage {

	// Project attributes
	private Text projectName;
	private Project.Types projectType;
	private Text projectPath;
	private Text projectDescription;
	private Text projectSummary;

	//Project types radio buttons
	private Button[] projectTypes;
	
	//Global error in dialog input data
	private boolean invalidData;
	
	// Widget container
	private Composite container;

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
	private static final String projectNameDupVal = "Project with this name already exists";
	
	/**
	 * The constructor
	 */
	public NewProjectPage() {
		super(title);
		setTitle(title);
		setDescription(description2);
		invalidData = false;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;

		createProjectName();
		createProjectPath();
		createProjectType();
		createProjectDescriptionAndSummary();
		
		// Required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
	}

	/*
	 * Create project name selection
	 */
	private void createProjectName() {
		Label projectNameLabel = new Label(container, SWT.NULL);
		projectNameLabel.setText(nameLabel);
		projectName = new Text(container, SWT.BORDER | SWT.SINGLE);

		final ControlDecoration txtDecorator = new ControlDecoration(
				projectName, SWT.TOP | SWT.RIGHT);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		Image img = fieldDecoration.getImage();
		txtDecorator.setImage(img);
		txtDecorator.setDescriptionText(projectNameValidator);
		// hiding it initially
		txtDecorator.hide();

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
				Text text = (Text) e.getSource();
				String string = text.getText();
				Pattern pattern = Pattern.compile(".*\\W+.*");
				Matcher matcher = pattern.matcher(string);
				if(new File(projectPath.getText() + File.separator + string).exists()) {
					txtDecorator.setDescriptionText(projectNameDupVal);
					txtDecorator.show();
					invalidData = true;
				}
				else {
					if (matcher.find()) {
						txtDecorator.show();
						invalidData = true;
					} else {
						txtDecorator.hide();
						invalidData = false;
						setDescription(description);
					}
				}
				checkIsComplete();
			}

		});

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectName.setLayoutData(gd);
	}

	/*
	 * Create project path selection
	 */
	private void createProjectPath() {
		Label projectDefaultPathLabel = new Label(container, SWT.NULL);
		projectDefaultPathLabel.setText("");
		final Button checkButton = new Button(container, SWT.CHECK);
		checkButton.setText(defaultPathLabel);
		
		Label projectPathLabel = new Label(container, SWT.NULL);
		projectPathLabel.setText(pathLabel);
		
		Composite subContainer = new Composite(container, SWT.NULL);
		projectPath = new Text(subContainer, SWT.BORDER | SWT.SINGLE);
		projectPath.setText(Workspace.getInstance().getPath());
		projectPath.setEnabled(false);
		
		checkButton.setSelection(true);
		checkButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (checkButton.getSelection()) {
					projectPath.setEnabled(false);
				} else
					projectPath.setEnabled(true);
			}
		});

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		subContainer.setLayout(layout);

		// Clicking the button will allow the user
		// to select a directory
		Button button = new Button(subContainer, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				String dir = KWindow.openDirectoryDialog(projectPath.getText());
				if (dir != null) {
					projectPath.setText(dir);
				}
			}
		});

		projectPath.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				checkIsComplete();
			}

		});

		projectPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		subContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/*
	 * Create project type radio button selection
	 */
	private void createProjectType() {
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

	/*
	 * Create project description and summary controls
	 */
	private void createProjectDescriptionAndSummary() {
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
	
	/*
	 * Check if all mandatory attributes were set
	 */
	private boolean checkIsComplete() {
		boolean ret = true;

		if (invalidData) {
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
				if(i == 0)
					projectType = Project.Types.SCHEDULER;
				else if(i == 1)
					projectType = Project.Types.TEST;
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

	public String getProjectName() {
		return projectName.getText();
	}
	
	public Project.Types getProjectType() {
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
}