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

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import kparserbenchmark.utils.InvalidPathException;

/**
 * Abstract tree node item representation for: ProjectNodes, ProjectFolders,
 * ProjectLeafs and Workspace (ProjectRoot)
 * 
 * Review history:
 * Rev 1: [16.01.2013] Kopson:
 * 		TODO: Add folder handling method
 * 		TODO: Add hide config files option in preferences
 * 
 * @author Kopson
 * 
 */
public abstract class ProjectItem {

	/**
	 * Project item types
	 */
	public static enum ItemTypes {
		UNKNOWN, // Unknown item type
		RAW_FILE, // Raw data file Item
		TBL_FILE, // Table data file item
		FOLDER, // Folder item
		NODE, // Project folder
		ROOT, // Workspace folder
		CONFIG_FILE, // Read only, auto-generated file
	}

	// Logger instance
	private static final Logger LOG = Logger.getLogger(ProjectItem.class
			.getName());

	// Item's file name
	protected String name;

	// Item's file path, doesn't contain item name
	protected String path;

	// Project that this file belongs to or Workspace that this project belongs
	// to
	protected ProjectItem parent;

	// Item type determine it's behaviour
	protected ItemTypes type;

	// Project's file list
	protected List<? super ProjectItem> children;

	// Properties file name
	protected static final String properties = ".properties";

	/**
	 * Common constructor actions
	 * 
	 * @param type
	 *            Item type
	 */
	private void init(ItemTypes type) {
		this.type = type;
		switch (type) {
		case RAW_FILE: // Files should have no children!
			children = null;
			break;
		default:
			children = new ArrayList<ProjectItem>();
			break;
		}
	}

	/**
	 * Default constructor
	 */
	public ProjectItem(ItemTypes type) {
		name = "";
		path = "";
		parent = null;
		init(type);
	}

	/**
	 * The constructor
	 * 
	 * @param type
	 *            Item's type
	 * @param parent
	 *            Item's parent project
	 * @param path
	 *            Item's path
	 * @param name
	 *            Item's name
	 */
	public ProjectItem(ItemTypes type, ProjectItem parent, String path,
			String name) {
		// Generally most of the files are created in context of the project
		// but there can be also files opened from file-system so remember to
		// check if parent is not null before using it
		this.parent = parent;
		this.path = path;
		this.name = name;
		init(type);
	}

	/**
	 * Return parent's path with parent name
	 * 
	 * @return Returns parent's path
	 */
	public String getParentPath() {
		if (parent != null)
			return parent.getPathName();
		return null;
	}

	/**
	 * Rename file. We assume that new name is valid here
	 * 
	 * @param newName
	 *            New name
	 * @throws InvalidPathException
	 */
	public void rename(String newName) throws InvalidPathException {
		if (newName == null || newName.length() == 0)
			throw new InvalidPathException(newName);
		name = newName;
		assert (path != null);
		path = Paths.get(path).getParent() + File.pathSeparator + newName;
	}

	/**
	 * Add new child
	 * 
	 * @param child
	 *            New item
	 * @return Returns add() status
	 */
	public boolean addChild(ProjectItem child) {
		if (child == null) {
			LOG.log(Level.WARNING, "Adding null child");
			return false;
		}
		return children.add(child);
	}

	/**
	 * Remove child
	 * 
	 * @param child
	 *            Item to remove
	 * @param hard
	 *            Remove also from file-system
	 * @return Returns remove() status
	 */
	public boolean removeChild(ProjectItem child, boolean hard) {
		if (child == null) {
			LOG.log(Level.WARNING, "Removing null child");
			return false;
		}
		if (hard) {
			File f = new File(child.getPath());
			f.delete();
		}
		return children.remove(child);
	}

	/**
	 * Get children array
	 * 
	 * @return Returns children array or null
	 */
	public ProjectItem[] getChildren() {
		if(children == null || children.size() == 0)
			return null;
		return (ProjectItem[]) children
				.toArray(new ProjectItem[children.size()]);
	}
	
	/**
	 * Return full path
	 * 
	 * @return Returns full path to item
	 */
	public String getPathName() {
		return path + File.separator + name;
	}
	
	/********** Getters/Setters **********/
	public ProjectItem getParent() {
		return parent;
	}
	
	public void setParent(ProjectItem parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public ItemTypes getType() {
		return type;
	}
	
	public List<? super ProjectItem> getChildrenList() {
		return children;
	}
	/*************************************/

	@Override
	public String toString() {
		String childrenList = "(null)";
		if (children != null) {
			childrenList = children.toString();
		}
		return "Item(" + type.name() + "): " + name + " (" + path + ")\n"
				+ "Parent: " + parent + "\nChildren: [" + childrenList + "]\n";
	}
}
