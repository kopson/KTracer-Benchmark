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
 * @author kopson
 */
public class Category {

	// Project's file name
	private String name;

	// Project's file path
	private String path;

	// Project that this file belongs to
	private Project parent;

	/**
	 * The constructor
	 * 
	 * @param parent
	 *            File's parent project
	 * @param path
	 *            File's path
	 * @param name
	 *            File's name
	 */
	public Category(Project parent, String path, String name) {
		// Assert.isNotNull(parent);
		this.parent = parent;
		this.path = path;
		this.name = name;
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

		if (obj instanceof Category) {
			if (this.parent != null && ((Category) obj).parent != null) {
				if (((Category) obj).parent.equals(this.parent)) {
					return ((Category) obj).name.equals(this.name);
				} else {
					return false;
				}
			} else {
				return ((Category) obj).path.equals(this.path);
			}
		} else {
			return false;
		}
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	/**
	 * Return parent's path
	 * 
	 * @return Returns parent's path
	 */
	public String getParentPath() {
		if (parent != null)
			return parent.getPath();
		return null;
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