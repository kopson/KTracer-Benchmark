package kparserbenchmark.projectexplorer;

import kparserbenchmark.KFile;

import org.eclipse.core.runtime.Assert;

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
	 * @param parent File's parent project
	 * @param path File's path
	 * @param name File's name
	 */
	public Category(Project parent, String path, String name) {
		Assert.isNotNull(parent);
		this.parent = parent;
		this.path = path;
		this.name = name;
	}

	/**
	 * Based on this method the IEditorInput will determine if the corresponding
	 * editor is already open or if a new editor must be opened.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;

		if (obj instanceof Category) {
			if (((Category) obj).parent.equals(this.parent)) {
				return ((Category) obj).name.equals(this.name);
			} else {
				return false;
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
	 * Return file text
	 * 
	 * @return Returns file text
	 */	
	public String getText() {
		return new KFile(path).getText();
	}
}