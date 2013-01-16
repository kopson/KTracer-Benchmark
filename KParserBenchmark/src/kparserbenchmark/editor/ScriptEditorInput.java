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

import kparserbenchmark.projectexplorer.ProjectLeaf;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * Describes script editor input.
 * 
 * @author kopson
 *
 */
public class ScriptEditorInput implements IEditorInput {
	
	/**
	 * The name of currently selected file.
	 */
	private ProjectLeaf participant;

	/**
	 * The constructor.
	 * 
	 * @param participant
	 */
	public ScriptEditorInput(ProjectLeaf participant) {
		super();
		Assert.isNotNull(participant);
		this.participant = participant;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return participant.getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj))
			return true;
		if (!(obj instanceof ScriptEditorInput))
			return false;
		ScriptEditorInput other = (ScriptEditorInput) obj;
		return this.participant.equals(other.participant);
	}

	@Override
	public int hashCode() {
		return participant.hashCode();
	}

	@Override
	public String getToolTipText() {
		return participant.getPath();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public ProjectLeaf getCategory() {
		return participant;
	}
}
