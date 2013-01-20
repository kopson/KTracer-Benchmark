package kparserbenchmark.projects.test;

import java.util.ArrayList;
import java.util.List;

public enum RecordProvider {
	INSTANCE;

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

	public List<Record> getRecords() {
		return records;
	}
}
