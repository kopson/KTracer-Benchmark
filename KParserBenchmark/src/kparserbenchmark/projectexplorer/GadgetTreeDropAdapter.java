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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

/**
 * Supports dropping gadgets into a tree viewer.
 * 
 * @author Kopson
 */
public class GadgetTreeDropAdapter extends ViewerDropAdapter {

	/**
	 * The constructor
	 * 
	 * @param viewer
	 */
	public GadgetTreeDropAdapter(TreeViewer viewer) {
		super(viewer);
	}

	@Override
	public boolean performDrop(Object data) {
		ProjectItem target = (ProjectItem) getCurrentTarget();
		ProjectNode parent = null;
		if (target == null)
			target = (ProjectItem) getViewer().getInput();

		if (target instanceof ProjectLeaf)
			parent = (ProjectNode) target.getParent();
		else if (target instanceof ProjectNode)
			parent = (ProjectNode) target;
		else
			assert (false);

		System.out.println("performDrop to " + parent.getName());

		ProjectLeaf[] toDrop = (ProjectLeaf[]) data;
		TreeViewer viewer = (TreeViewer) getViewer();
		// cannot drop a gadget onto itself or a child
		//for (int i = 0; i < toDrop.length; i++)
			//if (toDrop[i].getParent().equals(parent))
			//	return false;
		for (int i = 0; i < toDrop.length; i++) {
			toDrop[i].setParent(parent);
			parent.addChild(toDrop[i]);
			//viewer.add(target, toDrop[i]);
			System.out.println("add " + toDrop[i].getName() + " to " + parent.getName());
			//viewer.reveal(toDrop[i]);
			//viewer.refresh();
		}
		return true;
	}

	@Override
	public boolean validateDrop(Object target, int op, TransferData type) {
		return GadgetTransfer.getInstance().isSupportedType(type);
	}
}