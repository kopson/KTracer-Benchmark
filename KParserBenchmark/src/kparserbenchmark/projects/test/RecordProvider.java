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

import java.util.ArrayList;
import java.util.List;

/**
 * Record provider creates data model for table view
 * 
 * @author Kopson
 */
public enum RecordProvider {
	INSTANCE;

	/** List of records */
	private List<Record> records;

	/**
	 * The constructor
	 */
	private RecordProvider() {
		records = new ArrayList<Record>();

		// TODO: Add database bindings here
		records.add(new Record(1, "Log test 1", "TEST_1", (long) 98761234));
		records.add(new Record(2, "Log test 2", "TEST_2", (long) 98761244));
		records.add(new Record(3, "Log test 3", "TEST_3", (long) 98761534));
		records.add(new Record(4, "Log test 4", "TEST_4", (long) 98761734));
		records.add(new Record(5, "Log test 5", "TEST_5", (long) 98768234));
	}

	/********** Getters/Setters **********/
	public List<Record> getRecords() {
		return records;
	}
	/*************************************/
}
