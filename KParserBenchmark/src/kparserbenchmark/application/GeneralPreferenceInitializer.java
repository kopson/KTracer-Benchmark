package kparserbenchmark.application;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class GeneralPreferenceInitializer extends AbstractPreferenceInitializer {

	public GeneralPreferenceInitializer() {
		super();
	}

	@Override
	public void initializeDefaultPreferences() {
		 IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		    store.setDefault("MySTRING1", "http://www.vogella.com");
	}

}
