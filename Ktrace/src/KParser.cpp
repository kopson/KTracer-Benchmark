/*
 * KParser.cpp
 *
 *  Created on: Nov 1, 2012
 *      Author: root
 */

#include <iostream>
#include <iomanip>

#include "Ktrace.h"
#include "KParser.h"
#include "KDataBase.h"

using namespace std;

string KParser::SchedulerTableName = "Scheduler";

/* Initialize constant strings */
void KParser::initConstants() {
	SchedulerTrimmer = "##; =:; =[; =]; |=; &-; =>;";

	CreateSchedulerTable =
			"\tCREATE TABLE " + SchedulerTableName + "(\n"
				"\t\tLogId \t\t INTEGER PRIMARY KEY AUTOINCREMENT,\n"
				"\t\tPid \t\t INTEGER NOT NULL,\n"
				"\t\tTask \t\t TEXT,\n"
				"\t\tCPU \t\t INTEGER,\n"
				"\t\tTimestamp \t FLOAT,\n"
				"\t\tFunction \t TEXT,\n"
				"\t\tFunType \t INTEGER NOT NULL,\n"
				"\t\tPrevPid \t INTEGER,\n"
				"\t\tPrevTask \t TEXT,\n"
				"\t\tPrevPrio \t INTEGER,\n"
				"\t\tPrevState \t TEXT,\n"
				"\t\tNextPid \t INTEGER,\n"
				"\t\tNextTask \t TEXT,\n"
				"\t\tNextPrio \t INTEGER,\n"
				"\t\tSuccess \t INTEGER CHECK(Success IS NULL OR Success == 0 OR Success = 1),\n"
				"\t\tTargetCPU \t INTEGER,\n"
				"\t\tProjectId \t INTEGER NOT NULL"
			"\t);";

	for (int i = 0; i < SCHED_WAKEUP; ++i)
		wakeupLogFormatter[i] = 0;

	wakeupLogFormatter[0] = 1;
	wakeupLogFormatter[4] = 1;
	wakeupLogFormatter[5] = 1;

	InsertSchedWakeupLog =
			"\tINSERT INTO " + SchedulerTableName + "(\n"
				"\t\tTask,\n"			//0
				"\t\tPid,\n"
				"\t\tCPU,\n"
				"\t\tTimestamp,\n"
				"\t\tFunction,\n"		//4
				"\t\tPrevTask,\n"		//5
				"\t\tPrevPid,\n"
				"\t\tPrevPrio,\n"
				"\t\tSuccess,\n"
				"\t\tTargetCPU,\n"
				"\t\tFunType,\n"
				"\t\tProjectId\n"
			"\t) VALUES (\n";

	for (int i = 0; i < SCHED_SWITCH; ++i)
		switchLogFormatter[i] = 0;

	switchLogFormatter[0] = 1;
	switchLogFormatter[4] = 1;
	switchLogFormatter[5] = 1;
	switchLogFormatter[8] = 1;
	switchLogFormatter[9] = 1;

	InsertSchedSwitchLog =
			"\tINSERT INTO " + SchedulerTableName + "(\n"
				"\t\tTask,\n"			//0
				"\t\tPid,\n"
				"\t\tCPU,\n"
				"\t\tTimestamp,\n"
				"\t\tFunction,\n"		//4
				"\t\tPrevTask,\n"		//5
				"\t\tPrevPid,\n"
				"\t\tPrevPrio,\n"
				"\t\tPrevState,\n"		//8
				"\t\tNextTask,\n"		//9
				"\t\tNextPid,\n"
				"\t\tNextPrio,\n"
				"\t\tFunType,\n"
				"\t\tProjectId\n"
			"\t) VALUES (\n";
}

/* Open database connection, create Scheduler Table and prepare parser's trimmer */
KParser::KParser(KFile *file): valid(true) {
	initConstants();

	if(!kdb.open())
		valid = false;

	if(valid && !kdb.create(CreateSchedulerTable.c_str()))
		valid = false;

	if(file == NULL)
		valid = false;
	else
		parseFile = file;

	if(valid && !parseFile->prepareTrimmer(SchedulerTrimmer))
		valid = false;
}

/* Close database connection and cleanup */
KParser::~KParser() {
	if(!kdb.save())
		valid = false;

	if(valid)
		parseFile->finalizeTrimmer();
}

/* Check status of parser object */
bool KParser::isValid() {
	return valid;
}

/* Reads file and puts parsed fields into scheduler table */
bool KParser::parseScheduler(ProjectId id) {
	if (!valid)
		return false;

	unsigned int rowNumber = 0;
	vector<string> result;
	SchedulerLogTypes logType;
	int parsedRecords = 0;

	if (!kdb.begin()) {
		valid = false;
		return false;
	}
	while (parseFile->readLine(&result)) {
		rowNumber = result.size();

		if (rowNumber > TYPE_FIELD_NO) {
			if (result[TYPE_FIELD_NO].compare("sched_switch") == 0) {
				logType = SCHED_SWITCH;
			} else {
				logType = SCHED_WAKEUP;
			}

			if (logType != rowNumber) {
				kprint(E, PARSE_ERROR, "While parsing %s", parseFile->getFileName());
				valid = false;
				return false;
			} else {
				string statement;

				if (logType == SCHED_SWITCH)
					statement = InsertSchedSwitchLog;
				else if (logType == SCHED_WAKEUP)
					statement = InsertSchedWakeupLog;

				for (unsigned int i = 0; i < rowNumber; ++i) {
					statement += "\t\t";
					if ((logType == SCHED_SWITCH && switchLogFormatter[i]) ||
						(logType == SCHED_WAKEUP && wakeupLogFormatter[i]))
						statement += "'" + result[i] + "'";
					else
						statement += result[i];
					statement += ",\n";
				}
				if (logType == SCHED_SWITCH)
					statement += "\t1,\n";
				else if (logType == SCHED_WAKEUP)
					statement += "\t2,\n";
				statement += "\t" + id + ");";

				if(!kdb.insert(statement.c_str())) {
					valid = false;
					kdb.rollback();
					return false;
				}
				++parsedRecords;
			}

			result.clear();
		}
	}
	if (!kdb.commit()) {
		valid = false;
		kdb.rollback();
		return false;
	}
	kdebug("Parsed: %d records\n", parsedRecords);
	return true;
}

/* Returns database handler - for debug purposes only */
KDataBase *KParser::getDatabase() {
	return &kdb;
}

void KParser::printRecords() {
	int columnSize = 13;
	kdb.printSelectedColumns(columnSize);
	kdb.printSelectedRows(columnSize);
}
