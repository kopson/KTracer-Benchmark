/*
 * KDataBase.h
 *
 *  Created on: Oct 19, 2012
 *      Author: kopson
 */

#ifndef KDATABASE_H_
#define KDATABASE_H_

#include <sqlite3.h>
#include <string>
#include <vector>
#include <map>

typedef std::vector<std::map<std::string, std::string> > SQLRows;

class KDataBase {

public:
	KDataBase();
	virtual ~KDataBase();
	bool open();
	bool recreate();
	bool open(const char* dbName);
	bool save();
	std::string printStatus();

	bool query(const char* stmString);
	bool create(const char* stmString, bool truncate = false);
	bool select(const char* stmString);
	bool insert(const char* stmString);
	bool begin();
	bool commit();
	bool rollback();

	SQLRows *getSelectedRows();
	int countSelectedRows();
	void printSelectedRows(int size);
	void printSelectedColumns(int size);

private:
	static int getResults(void *NotUsed, int argc, char **argv, char **szColName);
	bool isTableExists(std::string name);

	sqlite3 *database;
	std::string dbName;
	std::string dbFileName;
	std::string dbMajorVersion;
	std::string dbMinorVersion;
	const char *propertiesName;

	static SQLRows results;
};

#endif /* KDATABASE_H_ */
