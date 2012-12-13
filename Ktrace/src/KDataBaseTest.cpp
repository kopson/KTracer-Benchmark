/*
 * KDataBaseTest.cpp
 *
 *  Created on: Oct 21, 2012
 *      Author: pkopka
 */

#include "KDataBase.h"
#include "Ktrace.h"
#include <iostream>

#ifdef TEST_DATABASE

int main(int argc, char **argv) {

	KDataBase kdb;

	if(!kdb.open("KDB_TEST.sqlite")) {
		fprintf(stderr, "Error while executing OPEN\n");
	} else {
		fprintf(stdout, "Success while executing OPEN\n");
	}

	if(!kdb.createTable("CREATE TABLE tb1(col1 varchar(30), col2 smallint)")) {
		fprintf(stderr, "Error while executing CREATETABLE\n");
	} else {
		fprintf(stdout, "Success while executing CREATETABLE\n");
	}

	if(!kdb.insert("INSERT INTO tb1(col1, col2) VALUES('test', 1)")) {
			fprintf(stderr, "Error while executing INSERT\n");
	} else {
		fprintf(stdout, "Success while executing INSERT\n");
	}

	if(!kdb.insert("INSERT INTO tb1(col1, col2) VALUES('test2', 2)")) {
		fprintf(stderr, "Error while executing INSERT\n");
	} else {
		fprintf(stdout, "Success while executing INSERT\n");
	}

	if(!kdb.select("SELECT * FROM tb1")) {
		fprintf(stderr, "Error while executing SELECT\n");
	} else {
		fprintf(stdout, "Success while executing SELECT\n");
	}

	if(!kdb.save()) {
		fprintf(stderr, "Error while executing SAVE\n");
	} else {
		fprintf(stdout, "Success while executing SAVE\n");
	}
}
#endif



