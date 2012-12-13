/*
 * KParser.h
 *
 *  Created on: Nov 1, 2012
 *      Author: root
 */

#ifndef KPARSER_H_
#define KPARSER_H_

#include "KDataBase.h"

#define TYPE_FIELD_NO 4
#define STATEMENT_SIZE = 2048;

typedef std::string ProjectId;

enum SchedulerLogTypes {
	SCHED_SWITCH = 12,
	SCHED_WAKEUP = 10
};

class KParser {

public:
	KParser(KFile *file);
	virtual ~KParser();
	bool parseScheduler(ProjectId id);
	bool isValid();
	void printRecords();

	 std::string CreateSchedulerTable;
	 static std::string SchedulerTableName;
	 std::string InsertSchedWakeupLog;
	 std::string InsertSchedSwitchLog;
	 std::string SchedulerTrimmer;

private:
	void initConstants();
	KDataBase *getDatabase();

	KDataBase kdb;
	bool valid;
	KFile* parseFile;

	//Put "1" on all indexes that goes into TEXT type column, "0" otherwise
	int wakeupLogFormatter[SCHED_WAKEUP];
	int switchLogFormatter[SCHED_SWITCH];
};

#endif /* KPARSER_H_ */
