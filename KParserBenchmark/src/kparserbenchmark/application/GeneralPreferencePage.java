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

public class GeneralPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/** Logger instance */
	private static final Logger LOG = Logger
			.getLogger(GeneralPreferencePage.class.getName());
	
	private ScopedPreferenceStore preferences;

	public static final String DEFAULT_WORKSPACE = "default_workspace";

	@SuppressWarnings("deprecation")
	public GeneralPreferencePage() {
		super(GRID);
		preferences = new ScopedPreferenceStore(new ConfigurationScope(),
				Activator.PLUGIN_ID);
		setPreferenceStore(preferences);
	}

	@Override
	public void init(IWorkbench workbench) {
		 setDescription("A demonstration of a preference page implementation");
	}

	
	@Override
	public boolean performOk() {
		try {
			preferences.save();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, 
					"Unable to save general preference page preferences");
		}
		return super.performOk();

	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor boolEditor = new BooleanFieldEditor(
				DEFAULT_WORKSPACE, "Change default workspace",
				getFieldEditorParent());
		addField(boolEditor);

		addField(new DirectoryFieldEditor("PATH", "&Directory preference:",
		        getFieldEditorParent()));
		    addField(new BooleanFieldEditor("BOOLEAN_VALUE",
		        "&An example of a boolean preference", getFieldEditorParent()));

		    addField(new RadioGroupFieldEditor("CHOICE",
		        "An example of a multiple-choice preference", 1,
		        new String[][] { { "&Choice 1", "choice1" },
		            { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
		    addField(new StringFieldEditor("MySTRING1", "A &text preference:",
		        getFieldEditorParent()));
		    addField(new StringFieldEditor("MySTRING2", "A &text preference:",
		        getFieldEditorParent()));
	}

}
