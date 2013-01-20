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

package kparserbenchmark.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import kparserbenchmark.editor.EditorAreaDropAdapter;
import kparserbenchmark.projectwizard.NewDirectoryWizard;
import kparserbenchmark.projectwizard.NewFileWizard;
import kparserbenchmark.projectwizard.NewProjectWizard;
import kparserbenchmark.utils.KImage;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.part.EditorInputTransfer;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

/**
 * Manages creating main window of application
 * 
 * @author kopson
 */
@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	/**
	 *  Status line image
	 */
	private Image statusImage;

	/**
	 * The constructor
	 * 
	 * @param configurer
	 */
	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(800, 600));
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(true);
		configurer.setShowMenuBar(true);

		configurer.addEditorAreaTransfer(EditorInputTransfer.getInstance());
		configurer.configureEditorAreaDropListener(
		new EditorAreaDropAdapter(configurer.getWindow()));

	}

	@Override
	public void postWindowOpen() {
		statusImage = KImage.getImageDescriptor(KImage.IMG_OK_STATUS)
				.createImage();

		// remove default "General" category from File > New menu.
		AbstractExtensionWizardRegistry wizardRegistry = (AbstractExtensionWizardRegistry) PlatformUI
				.getWorkbench().getNewWizardRegistry();
		IWizardCategory[] categories = PlatformUI.getWorkbench()
				.getNewWizardRegistry().getRootCategory().getCategories();

		for (IWizardDescriptor wizard : getAllWizards(categories)) {
			WorkbenchWizardElement wizardElement = (WorkbenchWizardElement) wizard;
			if (!allowedWizard(wizardElement.getId())) {
				wizardRegistry.removeExtension(wizardElement
						.getConfigurationElement().getDeclaringExtension(),
						new Object[] { wizardElement });
			}
		}
	}

	/**
	 * Check if wizard is allowed to display
	 * 
	 * @param wizardId
	 *            Id of wizard to check
	 * @return Returns true if wizard is allowed to be displayed
	 */
	private boolean allowedWizard(String wizardId) {
		return FILE_NEW__ALLOWED_WIZARDS.contains(wizardId);
	}

	/**
	 * List of wizards allowed to display in File > New menu
	 */
	private static final List<String> FILE_NEW__ALLOWED_WIZARDS = Collections
			.unmodifiableList(Arrays.asList(new String[] {
			// "org.eclipse.ui.Basic",// Basic wizards
			NewProjectWizard.ID, // User wizards
			NewFileWizard.ID, // User wizards
			NewDirectoryWizard.ID, // User wizards
			}));

	/**
	 * Method where you want to collect all the wizards that may reside in any
	 * of the categories. Categories are hierarchical so must go down deep
	 * recursively after the wizards.
	 * 
	 * @param categories Default eclipse wizard categories
	 * @return Returns all wizards
	 */
	private IWizardDescriptor[] getAllWizards(IWizardCategory... categories) {
		List<IWizardDescriptor> results = new ArrayList<IWizardDescriptor>();
		for (IWizardCategory wizardCategory : categories) {
			results.addAll(Arrays.asList(wizardCategory.getWizards()));
			results.addAll(Arrays.asList(getAllWizards(wizardCategory
					.getCategories())));
		}
		return results.toArray(new IWizardDescriptor[0]);
	}

	@Override
	public void dispose() {
		if (statusImage != null)
			statusImage.dispose();
	}
}
