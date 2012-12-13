/*
 * UnitTest.h
 *
 *  Created on: Nov 14, 2012
 *      Author: root
 */

#ifndef UNITTEST_H_
#define UNITTEST_H_

#include <string>
#include <iostream>
#include <string.h>

using std::cout;
using std::endl;
using std::cerr;

#define private public
#define BUFFER 128

int testCaseIdx;
char testCaseName[BUFFER];
char testSuiteName[BUFFER];
bool testResult;

void init(const char *testSuite) {
	testCaseIdx = 0;
	testResult = true;
	strncpy(testSuiteName, testSuite, BUFFER);
	cout << "====== " << testSuiteName << " ======" << endl;
}

void start(const char *testCase) {
	++testCaseIdx;
	strncpy(testCaseName, testCase, BUFFER);
}

void stop(int result) {
	if(result == 0)
		cout << "\tTest: " << testCaseName << " result: PASS" << endl;
	else
		cout << "\tTest: " << testCaseName << " result: FAILED at stage: " << result << endl;
	testResult = (result == 0 ? true : false);
}

void report() {
	cout << endl << "============== Summary ==============" << endl;
	cout << "START: " << testCaseIdx << " tests" << endl;
	cout << "RESULT: " << (testResult == true ? "PASS" : "FAIL") << endl << endl;
	testResult = false;
}

#define INIT_TESTS(x) init(x); while(testResult) {
#define TEST_CASE(x) start(x);
#define TEST_END stop(0);
#define TEST_FAIL(x) { stop(x); report(); break; }
#define RUN_TESTS report(); }

#endif /* UNITTEST_H_ */
