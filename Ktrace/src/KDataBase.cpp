/*
 * KdataBase.cpp
 *
 *  Created on: Oct 19, 2012
 *      Author: pkopka
 */

#include <boost/regex.hpp>
#include <boost/algorithm/string.hpp>
#include <iomanip>

#include "Ktrace.h"
#include "KDataBase.h"

using namespace std;
using namespace boost;

/* Initialize database attributes */
KDataBase::KDataBase(): database(NULL) {
	dbName = "KDB";
	dbMajorVersion = "0";
	dbMinorVersion = "1";
	dbFileName = dbName + "_v_" + dbMajorVersion + "_" + dbMinorVersion + ".sqlite";
	propertiesName = ".kdatabase.properties";
}

/* Empty */
KDataBase::~KDataBase() {
}

/* Results of last query */
SQLRows KDataBase::results;

bool KDataBase::recreate() {
	if(!fileExists(dbFileName.c_str())) {
		return false;
	}

	if(!removeFile(dbFileName.c_str())) {
		return false;
	}

	return open();
}

/* Open a database. If the database does not exist yet, it will be created. */
bool KDataBase::open() {
	return open(dbFileName.c_str());
}

const string propertiesList[] = {	"databaseName",
									"databaseFile",
									"datbaseVersion",
									"databaseCreateDate",
									"createUser" };
enum PropertyIdx {
	DbName,
	DbFile,
	DbVersion,
	DbDate,
	DbUser
};

/* As above */
bool KDataBase::open(const char* name) {

	KFile properties;
	string str;
	int idx = 0;
	if(!fileExists(propertiesName)) {
		properties.openFile(propertiesName, "c");
		properties.setPermission("r+a");
		str = propertiesList[idx++] + "=" + dbName + "\n";
		properties.writeStr(str.c_str());
		str = propertiesList[idx++] + "=" + dbFileName + "\n";
		properties.writeStr(str.c_str());
		str = propertiesList[idx++] + "=" + dbMajorVersion + "." + dbMinorVersion + "\n";
		properties.writeStr(str.c_str());
		str = propertiesList[idx++] + "=" + getCurrDate("DD/MM/YYYY") + "\n";
		properties.writeStr(str.c_str());
		str = propertiesList[idx++] + "=" + getCurrUser() + "\n";
		properties.writeStr(str.c_str());
	}

	if (sqlite3_open(name, &database) != SQLITE_OK) {
		kprint(E, _SQLITE_ERROR, "While creating database %s", name);
		return false;
	}

	dbName = name;
	kdebug("Opened database %s\n", dbFileName.c_str());
	return true;
}

/* Query types */
enum QERY {
	INSERT,
	CREATE,
	DELETE,
	UPDATE,
	SELECT
};

/* Constants for sqlValidator function */
const char *createValidators[] = {"CREATE", "TABLE", "INT", "TEXT", "FLOAT"};
enum CValIndex {
	CREATE_IDX,
	TABLE_IDX,
	INT_IDX,
	TEXT_IDX,
	FLOAT_IDX
};

/* Validate query statement from stmString string using xxxValidators
 * constants and retrieve statement members into stmElements vector:
 * CREATE: {TABLE_NAME}
 * INSERT: ...
 *
 */
bool sqlValidator(QERY q, const char* stmString, vector<string> *stmElements) {
	stmElements->clear();

	vector<string> tokens;
	split(tokens, stmString, is_any_of(" ,();\t\n"), token_compress_on);

	switch (q) {
		case CREATE: {
			for(vector<string>::iterator it = tokens.begin(); it != tokens.end(); ++it) {
				if((*it).size() == 0) {
					continue;
				}

				if ((*it).compare(createValidators[TABLE_IDX]) == 0) {
					stmElements->push_back(*(it + 1));
				} else if (	(*it).compare(createValidators[TEXT_IDX]) == 0 ||
							(*it).compare(createValidators[INT_IDX]) == 0 ||
							(*it).compare(createValidators[FLOAT_IDX]) == 0) {
					stmElements->push_back(*(it - 1));
				}
			}
			break;
		}
		case INSERT:
			break;
		case UPDATE:
			return false;
		case SELECT:
			break;
		case DELETE:
			break;
		default :
			break;
	}

	if (stmElements->size() < 1) {
		return false;
	}
	return true;
}

/* Check if table exists */
bool KDataBase::isTableExists(string name) {
	string stmt = "\tSELECT name FROM sqlite_master WHERE type='table' AND name='";
	stmt += name;
	stmt += "';";

   	if (!query(stmt.c_str())) {
        return false;
    }

   	if(results.size() > 0 && results[0]["name"].compare(name) == 0)
   		return true;
   	return false;
}

/* Create new table from string statement. If truncate flag is on and
 * table already exists - destroys it and it's entity
 */
bool KDataBase::create(const char* stmString, bool truncate) {
	vector<string> queryAtributes;
	vector<string>::iterator it;

	if (sqlValidator(CREATE, stmString, &queryAtributes)) {
		it = queryAtributes.begin();
		if (it == queryAtributes.end())
			return false;
	}

	//Check if table already exists
	if (isTableExists((*it))) {
		if(truncate) {
			string dropStatement = "DROP TABLE " + (*it) + ";";
			if (!query(dropStatement.c_str())) {
				return false;
			}
		}
		return true;
	}

	if (!query(stmString)) {
		return false;
	}

	kdebug("Created table %s\n", (*it).c_str());
	return true;
}

bool KDataBase::begin() {
	const char *stmString = "BEGIN;";

	if(!query(stmString)) {
			return false;
		}

		return true;
}

bool KDataBase::commit() {
	const char *stmString = "COMMIT;";

	if(!query(stmString)) {
			return false;
		}

		return true;
}

bool KDataBase::rollback() {
	const char *stmString = "ROLLBACK;";

	if(!query(stmString)) {
			return false;
		}

		return true;
}

/* Select using string statement */
bool KDataBase::insert(const char* stmString) {
	vector<string> queryAtributes;
	vector<string>::iterator it;

	if (sqlValidator(INSERT, stmString, &queryAtributes)) {
		it = queryAtributes.begin();
		if (it == queryAtributes.end())
			return false;
	}

	if(!query(stmString)) {
		return false;
	}

	return true;
}

/* Select using string statement */
bool KDataBase::select(const char* stmString) {
	/*vector<string> queryAtributes;
	if (sqlValidator(SELECT, stmString, &queryAtributes)) {
		vector<string>::iterator it = queryAtributes.begin();
		kdebug("Select from table %s:\n", (*(it++)).c_str());
		for(; it != queryAtributes.end(); ++it) {
			kdebug("\tof columns:  %s:\n", (*it).c_str());
		}
	}*/
	results.clear();

	if(!query(stmString)) {
		return false;
	}

	return true;
}

/* This is the callback function to display the data retrived from database */
int KDataBase::getResults(void *NotUsed, int argc, char **argv, char **szColName) {
	map<string, string> result;
	for(int i = 0; i < argc; i++) {
		if(argv == NULL || argv[i] == NULL)	{
			result[szColName[i]] = "NULL";
		} else {
			result[szColName[i]] = argv[i];
		}
	}
	results.push_back(result);
	return 0;
}

/* Executes query. Results are inserted into results vector*/
bool KDataBase::query(const char* stmString) {
	assert(database != NULL);

	char *szErrMsg = 0;
	int rc = sqlite3_exec(database, stmString, KDataBase::getResults, 0, &szErrMsg);
	if(rc != SQLITE_OK) {
		kprint(W, _SQLITE_ERROR, "While executing query \n%s\n %s", stmString, szErrMsg);
		sqlite3_free(szErrMsg);
		return false;
	}
	return true;
}

/* Save data and close database */
bool KDataBase::save() {
	if (database != NULL && sqlite3_close(database) == SQLITE_OK) {
		kdebug("Closed database %s\n", dbFileName.c_str());
		return true;
	}
	kprint(W, _SQLITE_ERROR, "While closing database %s", dbName.c_str());
	return false;
}

/* Returns pointer to last query results */
SQLRows *KDataBase::getSelectedRows() {
	return &results;
}

/* Returns number of rows returned by last query */
int KDataBase::countSelectedRows() {
	return results.size();
}

/* Prints status of database */
string KDataBase::printStatus() {
	KFile properties;
	string result;
	properties.openFile(propertiesName, "r");
	string date = properties.readProperty(propertiesList[DbDate].c_str());
	string user = properties.readProperty(propertiesList[DbUser].c_str());

	result = "Database: " + dbName + " v." + dbMajorVersion + "." + dbMinorVersion +
			 " created: " + date + " by: " + user;
	return  result;
}

void KDataBase::printSelectedColumns(int size) {
	SQLRows::iterator it = results.begin();
	cout << setw(size) << left << "RowId";
	for(map<std::string, std::string>::iterator iti = it->begin(); iti != it->end(); ++iti) {
		cout << setw(size) << left << iti->first;
	}
	cout << endl;
	cout << setw(size) << left << "-----";
	for(map<std::string, std::string>::iterator iti = it->begin(); iti != it->end(); ++iti) {
		string marks;
		for(unsigned int i = 0; i < iti->first.size(); ++i)
			marks += '-';
		cout << setw(size) << left << marks;
	}
	cout << endl;
}
/* Prints results of last query to std output */
void KDataBase::printSelectedRows(int size) {
	SQLRows::iterator it = results.begin();
	for(int i = 0; it != results.end(); ++it, ++i) {
		map<std::string, std::string>::iterator iti = it->begin();
		cout << setw(size) << left << i;
		for(; iti != it->end(); ++iti) {
			cout << setw(size) << left << iti->second;
		}
		cout << endl;
	}
}
