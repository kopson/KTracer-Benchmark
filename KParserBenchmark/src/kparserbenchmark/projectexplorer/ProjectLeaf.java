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

package kparserbenchmark.projectexplorer;

import kparserbenchmark.utils.KFile;

/**
 * Project's file representation in ProjectExplorer
 * 
 * Review history:
 * Rev 1: [15.01.2013] Kopson:
 * 		TODO: Add synchronization with file-system mechanism.
 * 		TODO: Add synchronization validation decoration to file icon.  
 * 		TODO: Add copy/move/link/hard-remove/soft-remove operations
 * 
 * @author Kopson
 */
public class ProjectLeaf extends ProjectItem {

	/**
	 * The constructor
	 * 
	 * @param type
	 * @param parent
	 * @param path
	 * @param name
	 */
	public ProjectLeaf(ItemTypes type, ProjectItem parent, String path, String name) {
		super(type, parent, path, name);
	}

	/**
	 * The constructor for files opened from file-system
	 * 
	 * @param parent
	 * @param path
	 * @param name
	 */
	public ProjectLeaf(ProjectItem parent, String path, String name) {
		super(ItemTypes.RAW_FILE, parent, path, name);
	}

	/**
	 * Based on this method the IEditorInput will determine if the corresponding
	 * editor is already open or if a new editor must be opened.
	 * 
	 * @param obj
	 *            Object to compare with
	 * @return Returns true if two objects point to the same file. File can be
	 *         opened:<br>
	 *         - in context of the project. In this case we are comparing file's
	 *         parent project and file name<br>
	 *         - as stand-alone file. In this case we are comparing file's path
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if (obj instanceof ProjectLeaf) {
			if (this.parent != null && ((ProjectLeaf) obj).parent != null) {
				if (((ProjectLeaf) obj).parent.equals(this.parent)) {
					return ((ProjectLeaf) obj).name.equals(this.name);
				} else {
					return false;
				}
			} else {
				return ((ProjectLeaf) obj).path.equals(this.path);
			}
		} else {
			return false;
		}
	}

	/**
	 * Return file text
	 * 
	 * @return Returns file text
	 */
	public String getText() {
		return new KFile(path).getText();
	}
}