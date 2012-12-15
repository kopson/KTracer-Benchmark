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

package kparserbenchmark;

/**
 * Utilities class for Ktrace application
 * 
 * @author kopson
 */
public class KTrace {

	/** Scheduler tracing project type */
	public static final int PROJECT_TYPE_SCHEDULER 	= 0;
	/** Test proect type */
	public static final int PROJECT_TYPE_TEST 		= 1;
	
	/** Project type names */
	public static final String[] ProjectTypes = {
		"Scheduler",
		"Test"
	};
	
	/**
	 * Return number of project types
	 * 
	 * @return Returns number of project types
	 */
	public static int getTypesSize() {
		return ProjectTypes.length;
	}
}
