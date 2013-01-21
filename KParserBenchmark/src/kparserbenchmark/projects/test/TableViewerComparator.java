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

package kparserbenchmark.projects.test;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * Comparator for table columns
 * 
 * @author Kopson
 */
public class TableViewerComparator extends ViewerComparator {

	/** Changes column sorting direction when column header is selected */
	private int propertyIndex;

	/** Sorting order flag: desc = 1, asc = 0 */
	private static final int DESCENDING = 1;

	/** Current sorting direction */
	private int direction = DESCENDING;

	/**
	 * The constructor
	 */
	public TableViewerComparator() {
		this.propertyIndex = 0;
		direction = 1 - DESCENDING;
	}

	/**
	 * Get sorting direction
	 * 
	 * @return Returns sorting direction
	 */
	public int getDirection() {
		return direction == DESCENDING ? SWT.DOWN : SWT.UP;
	}

	/**
	 * Set column direction
	 * 
	 * @param column
	 *            column index
	 */
	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	/** Set column compare rules */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Record rec1 = (Record) e1;
		Record rec2 = (Record) e2;

		int rc = 0;
		switch (propertyIndex) {
		case 0:
			if (((Record) e1).getLogId() < ((Record) e2).getLogId())
				rc = -1;
			else if (((Record) e1).getLogId() == ((Record) e2).getLogId())
				rc = 0;
			else
				rc = 1;
			break;
		case 1:
			rc = rec1.getLogName().compareTo(rec2.getLogName());
			break;
		case 2:
			rc = rec1.getLogType().compareTo(rec2.getLogType());
			break;
		case 3:
			if (((Record) e1).getTimestamp() < ((Record) e2).getTimestamp())
				rc = -1;
			else if (((Record) e1).getTimestamp() == ((Record) e2)
					.getTimestamp())
				rc = 0;
			else
				rc = 1;
			break;
		default:
			rc = 0;
			break;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}
}
