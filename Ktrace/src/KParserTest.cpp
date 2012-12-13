/*
 * KParserTest.cpp
 *
 *  Created on: Nov 2, 2012
 *      Author: root
 */

#include "Ktrace.h"
#include "KParser.h"
#include <string>
#include <iostream>

using namespace std;

#ifdef TEST_PARSER

int main(int argc, char **argv) {
	bool testResult = true;
	string stm;

	const char * fileName = "/home/pkopka/Downloads/test.out";
	{
		KFile testFile(fileName, "c");
		assert(fileExists(fileName));
		assert(testFile.setPermission("r+a"));

		assert(testFile.writeStr("# tracer: nop\n"));
		assert(testFile.writeStr("#\n"));
		assert(testFile.writeStr("#           TASK-PID    CPU#    TIMESTAMP  FUNCTION\n"));
		assert(testFile.writeStr("#              | |       |          |         |\n"));
		assert(testFile.writeStr("Ktrace-9431  [001] 10960.956181: sched_switch: prev_comm"
						  "=Ktrace prev_pid=9431 prev_prio=120 prev_state=S ==> next"
						  "_comm=swapper/1 next_pid=0 next_prio=120\n"));
		assert(testFile.writeStr("<idle>-0     [001] 10960.956187: sched_wakeup: comm=ksoftirqd/1"
						  " pid=8319 prio=120 success=1 target_cpu=001\n"));
		assert(testFile.writeStr("<idle>-0     [001] 10960.956192: sched_switch: prev_comm=swapper/1"
						  " prev_pid=0 prev_prio=120 prev_state=R ==> next_comm=ksoftirqd/1 next"
						  "_pid=8319 next_prio=120\n"));
		assert(testFile.writeStr("ksoftirqd/1-8319  [001] 10960.956198: sched_switch: prev_comm="
						  "ksoftirqd/1 prev_pid=8319 prev_prio=120 prev_state=S ==> next_comm"
						  "=swapper/1 next_pid=0 next_prio=120\n"));
		assert(testFile.writeStr("<idle>-0     [001] 10960.956201: sched_wakeup: comm=ksoftirqd/1"
						  " pid=8319 prio=120 success=1 target_cpu=001\n"));
	}

	KDataBase kdb;
	assert(kdb.recreate());
	cout << kdb.printStatus() << endl << endl;

	KFile testFile(fileName, "r");
	KParser parser(&testFile);

	assert(parser.parseScheduler("1"));
	assert(testFile.deleteFile());
	assert(!fileExists(fileName));

	stm = "SELECT * FROM ";
	stm += parser.SchedulerTableName;
	stm += ";";

	assert(kdb.select((const char *) stm.c_str()));
	int resultSize = kdb.countSelectedRows();
	assert(resultSize == 5);
	parser.printRecords();

	kdb.begin();
	stm = "DROP TABLE ";
	stm += KParser::SchedulerTableName;
	stm += ";";
	assert(kdb.query(stm.c_str()));
	kdb.commit();

	cout << endl << "Test: " << (testResult ? "PASS" : "FAIL") << endl;
	return 0;
}

#endif //TEST_PARSER
