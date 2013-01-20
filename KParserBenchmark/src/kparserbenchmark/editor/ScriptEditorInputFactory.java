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

package kparserbenchmark.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

/**
 * Remeber opened items in editor
 * 
 * @author Kopson
 *
 */
public class ScriptEditorInputFactory implements IElementFactory {

	/** Id */
	public static final String ID = "KParserBenchmark.editor.ScriptEditorInputFactory";

	@Override
	public IAdaptable createElement(IMemento memento) {
		String itemName = memento.getString(ScriptEditorInput.KEY_NAME);
		if (itemName != null) {
			/*ProjectLeaf[] projectLeafs = Workspace.getInstance().getChildren();
			for (ProjectLeaf pl : projectLeafs) {
				if (pl.getText().equals(itemName)) {
					return new ScriptEditorInput(pl);
				}
			}*/
		}
		return null;
	}
}
