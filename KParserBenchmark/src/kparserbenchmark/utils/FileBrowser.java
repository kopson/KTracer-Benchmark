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

package kparserbenchmark.utils;

import java.io.File;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;

/**
 * File browser tree
 * 
 * @author kopson
 */
public class FileBrowser extends TreeViewer {
	  
	/**
	 * The constructor
	 * 
	 * @param parent Parent composite
	 * @param path Starting path
	 */
	public FileBrowser(Composite parent, String path) {
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		setContentProvider(new ViewContentProvider());
		setLabelProvider(new ViewLabelProvider());
		File[] files = null;
		if (path == null) 
			files = File.listRoots();
		else {
			files = new File(path).listFiles(); 	
		}
		setInput(files);
	}

	/**
	 * Get selected item
	 * 
	 * @return Returns selected item or null
	 */
	public String getItem() {
		return null;
	}
	
	/**
	 * Content provider
	 * 
	 * @author kopson
	 */
	class ViewContentProvider implements ITreeContentProvider {
		
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return (File[]) inputElement;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			File file = (File) parentElement;
			return file.listFiles();
		}

		@Override
		public Object getParent(Object element) {
			return ((File) element).getParentFile();
		}

		@Override
		public boolean hasChildren(Object element) {
			File file = (File) element;
			if (file.isDirectory()) {
				return true;
			}
			return false;
		}
	}

	/**
	 * Label provider
	 */
	class ViewLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			File file = (File) element;
			String name = file.getName();
			if (name.length() > 0) {
				return name;
			}
			return file.getPath();
		}

		@Override
		public Image getImage(Object element) {
			File file = (File) element;
			if(file.isDirectory()) {
				return KImage.getSharedImage(ISharedImages.IMG_OBJ_FOLDER);
			} else if (file.isFile()) {
				return KImage.getSharedImage(ISharedImages.IMG_OBJ_FILE);
			}
			return null;
		}
	}
}