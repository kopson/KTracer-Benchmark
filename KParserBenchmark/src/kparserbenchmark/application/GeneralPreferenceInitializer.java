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

import kparserbenchmark.utils.KWindow;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Sets deault values for preferences view
 * 
 * @author kopson
 */
public class GeneralPreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * The constructor
	 */
	public GeneralPreferenceInitializer() {
		super();
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = KWindow.getPrefs();
		if (store != null) {
			//TODO: Add deault preferences here
			store.setDefault(GeneralPreferencePage.MY_STRING_1, "http://www.vogella.com");
		}
	}

}
