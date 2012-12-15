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

package kparserbenchmark.commands;

import java.io.FileNotFoundException;

import kparserbenchmark.KFile;
import kparserbenchmark.KImage;
import kparserbenchmark.intro.Application;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Action to change default workspace
 * 
 * @author kopson
 */
public class SetWorkspaceHandler implements IHandler {

	// String constants
	public static final String pathDialogTitle = "Set workspace";
	public static final String pathDialogDescription = "Set new default workspace";

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
		Shell s = Display.getCurrent().getActiveShell();
		OKCancelDialog dialog = new OKCancelDialog(s);

		String oldWorkspace = Application.getDefaultWorkspace();
		if (dialog.open() == Window.OK) {
			String newWorkspace = dialog.getNewWorkspace();
			if (!newWorkspace.equals(oldWorkspace)) {
				if (dialog.getCheckStatus()) {
					try {
						KFile.removeDirectoryRecursive(oldWorkspace);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Application.setDefaultWorkspace(newWorkspace);
				Application.createDefaultWorkspace();
			}
		}
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

	/**
	 * Dialog with OK and Cancel buttons
	 * 
	 * @author root
	 */
	public class OKCancelDialog extends Dialog {

		// New workspace path
		private Text nameText;

		// Check if delete previous workspace
		private Button checkButton;
		private Button okButton;
		private Button warningButton;
		private Label warning;
		private Image image;

		// Save return data because all components are disposed after Dialog.open()
		private String name;
		private boolean checked;

		// String constants
		public static final String deleteOldWorkspace = "Remove previous location and all its content";
		public static final String newWorkspaceLocation = "New location: ";
		public static final String title = "Change workspace location";
		public static final String nameTextValidator = "Invalid path";
		public static final String workspaceNotEmptyWarning = "Old workspace location contains data!";

		/**
		 * The constructor
		 * 
		 * @param parentShell
		 */
		public OKCancelDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL
					| SWT.RESIZE);
			parentShell.setText(title);
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			okButton = createButton(parent, IDialogConstants.OK_ID,
					IDialogConstants.OK_LABEL, true);
			createButton(parent, IDialogConstants.CANCEL_ID,
					IDialogConstants.CANCEL_LABEL, false);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);
			Composite subContainer = new Composite(container, SWT.NONE);

			GridLayout subLayout = new GridLayout();
			subLayout.numColumns = 4;
			subContainer.setLayout(subLayout);

			Label nameLabel = new Label(subContainer, SWT.NONE);
			nameLabel.setText(newWorkspaceLocation);

			nameText = new Text(subContainer, SWT.BORDER);
			final ControlDecoration txtDecorator = new ControlDecoration(
					nameText, SWT.TOP | SWT.RIGHT);
			FieldDecoration fieldDecoration = FieldDecorationRegistry
					.getDefault().getFieldDecoration(
							FieldDecorationRegistry.DEC_ERROR);
			Image img = fieldDecoration.getImage();
			txtDecorator.setImage(img);
			txtDecorator.setDescriptionText(nameTextValidator);
			// hiding it initially
			txtDecorator.hide();

			nameText.setText(Application.getDefaultWorkspace());
			name = nameText.getText();
			nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameText.addKeyListener(new KeyListener() {

				@Override
				public void keyReleased(KeyEvent arg0) {
					if (KFile.isPathVaild(nameText.getText())) {
						txtDecorator.hide();
						checkWarning();
						okButton.setEnabled(true);
					} else {
						txtDecorator.show();
						okButton.setEnabled(false);
					}
				}

				@Override
				public void keyPressed(KeyEvent arg0) {
					// TODO Auto-generated method stub

				}
			});
			new Label(subContainer, SWT.NONE);
			Button button = new Button(subContainer, SWT.PUSH);
			button.setText("Browse...");
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dlg = new DirectoryDialog(PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getShell());
					dlg.setFilterPath(nameText.getText());

					dlg.setText(pathDialogTitle);
					dlg.setMessage(pathDialogDescription);

					String dir = dlg.open();
					if (dir != null) {
						if (KFile.isPathVaild(dir)) {
							nameText.setText(dir);
							txtDecorator.hide();
								checkWarning();
							okButton.setEnabled(true);
						} else {
							txtDecorator.show();
							okButton.setEnabled(false);
						}
					}
				}
			});

			new Label(subContainer, SWT.NONE);
			checkButton = new Button(subContainer, SWT.CHECK);
			checkButton.setSelection(false);
			checked = checkButton.getSelection();
			checkButton.setText(deleteOldWorkspace);
			new Label(subContainer, SWT.NONE);
			new Label(subContainer, SWT.NONE);

			if (image == null)
				image = new Image(Display.getDefault(), Display.getDefault()
						.getSystemImage(SWT.ICON_WARNING), SWT.IMAGE_COPY);

			//TODO: Change warning button with directly drawn image
			warningButton = new Button(subContainer, SWT.PUSH);
			if (!image.isDisposed()) {
				image = KImage.resize(image,
						(int) ((double) image.getBounds().width / 1.5),
						(int) ((double) image.getBounds().height / 1.5));
				warningButton.setImage(image);
			}
			warningButton.setVisible(false);
			warningButton.setTouchEnabled(false);
			warningButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
					true, true));
			warning = new Label(subContainer, SWT.NONE);
			warning.setText(workspaceNotEmptyWarning);
			warning.setVisible(false);

			checkButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (KFile.isPathVaild(nameText.getText()))
						checkWarning();
				}
			});
			Label line = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
			line.setLayoutData(new GridData(SWT.FILL, SWT.END, true, true));

			return container;
		}

		//Check if warning button should be displayed
		private void checkWarning() {
			if(checkButton.getSelection() && KFile.checkWorkspaceNotEmpty(nameText.getText())) {
				warning.setVisible(true);
				warningButton.setVisible(true);
			} else {
				warning.setVisible(false);
				warningButton.setVisible(false);
			}
		}
		
		/**
		 * Set dialog return values
		 */
		private void saveInput() {
			name = nameText.getText();
			checked = checkButton.getSelection();
		}

		@Override
		protected void okPressed() {
			saveInput();
			super.okPressed();
		}

		public String getNewWorkspace() {
			return name;
		}

		public boolean getCheckStatus() {
			return checked;
		}
	}
}
