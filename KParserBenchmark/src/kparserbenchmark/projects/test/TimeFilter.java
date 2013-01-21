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
import org.eclipse.jface.viewers.ViewerFilter;

public class TimeFilter extends ViewerFilter {

	private long start;
	private long end;

	public TimeFilter() {
		start = 0;
		end = 0;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		Record rec = (Record) element;
		boolean firstCheck = false;
		boolean secondCheck = false;

		if (start == 0 || start > 0 && rec.getTimestamp() >= start)
			firstCheck = true;
		if (end == 0 || end > 0 && rec.getTimestamp() <= end)
			secondCheck = true;
		return firstCheck && secondCheck;
	}

	public void setStartTime(String text) {
		try {
			start = Long.parseLong(text);
			if (start < 0)
				start = 0;
		} catch (NumberFormatException e) {
			start = 0;
		}
	}

	public String getStartTime() {
		return Long.toString(start);
	}

	public void setEndTime(String text) {
		try {
			end = Long.parseLong(text);
			if (end < 0)
				end = 0;
		} catch (NumberFormatException e) {
			end = 0;
		}
	}

	public String getEndTime() {
		return Long.toString(end);
	}

}
