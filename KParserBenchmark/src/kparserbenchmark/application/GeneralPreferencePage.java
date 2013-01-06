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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * General preference page
 * 
 * @author kopson
 */
public class GeneralPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/** Logger instance */
	private static final Logger LOG = Logger
			.getLogger(GeneralPreferencePage.class.getName());
	
	/**
	 * Preference store object
	 */
	private ScopedPreferenceStore preferences;

	/**
	 * Preferences ID
	 */
	public static final String DEFAULT_WORKSPACE = "default_workspace";
	public static final String PATH = "PATH";
	public static final String BOOLEAN_VALUE = "BOOLEAN_VALUE";
	public static final String CHOISE = "CHOICE";
	public static final String MY_STRING_1 = "MySTRING1";
	public static final String MY_STRING_2 = "MySTRING2";
	
	@SuppressWarnings("deprecation")
	public GeneralPreferencePage() {
		super(GRID);
		preferences = new ScopedPreferenceStore(new ConfigurationScope(),
				Activator.PLUGIN_ID);
		setPreferenceStore(preferences);
	}

	@Override
	public void init(IWorkbench workbench) {
		 setDescription("An application default preferences");
	}

	
	@Override
	public boolean performOk() {
		try {
			preferences.save();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, 
					"Unable to save general preference page");
		}
		return super.performOk();
	}

	@Override
	protected void createFieldEditors() {
		//TODO: add application preferences here
		BooleanFieldEditor boolEditor = new BooleanFieldEditor(
				DEFAULT_WORKSPACE, "Change default workspace",
				getFieldEditorParent());
		addField(boolEditor);

		addField(new DirectoryFieldEditor(PATH, "&Directory preference:",
		        getFieldEditorParent()));
		    addField(new BooleanFieldEditor(BOOLEAN_VALUE,
		        "&An example of a boolean preference", getFieldEditorParent()));

		    addField(new RadioGroupFieldEditor(CHOISE,
		        "An example of a multiple-choice preference", 1,
		        new String[][] { { "&Choice 1", "choice1" },
		            { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
		    addField(new StringFieldEditor(MY_STRING_1, "A &text preference:",
		        getFieldEditorParent()));
		    addField(new StringFieldEditor(MY_STRING_2, "A &text preference:",
		        getFieldEditorParent()));
	}

}
