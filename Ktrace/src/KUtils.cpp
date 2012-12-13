/*
 * KUtils.cpp
 *
 *  Created on: Nov 7, 2012
 *      Author: kopson
 */

#include "KUtils.h"
#include "Ktrace.h"

#include <ctime>
#include <sstream>
#include <pwd.h>

using namespace std;

string getCurrDate(const char *format) {
	if (format == NULL) {
		kprint(E, 0, "Format error in %s\n", "getCurrDate()");
		return NULL;
	}

	int i = 0;
	int state = 0;
	stringstream ss;
	time_t t = time(0);   // get time now
	struct tm *now = localtime(&t);

	while (format[i] != '\0') {
		if (format[i] == 'D') {
			if (++state == 1) {
				;
			} else if (state == 2) {
				ss << now->tm_mday;
				state = 0;
			} else {
				kprint(E, 0, "Format error in %s\n", "getCurrDate()");
				return NULL;
			}
		} else if (format[i] == 'M') {
			if(++state == 1) {
				;
			} else if (state == 2) {
				ss << (now->tm_mon + 1);
				state = 0;
			} else {
				kprint(E, 0, "Format error in %s\n", "getCurrDate()");
				return NULL;
			}
		} else if (format[i] == 'Y') {
			if(++state == 1 || state == 2 || state == 3) {
				;
			} else if (state == 4) {
				ss << (now->tm_year + 1900);
				state = 0;
			} else {
				kprint(E, 0, "Format error in %s\n", "getCurrDate()");
				return NULL;
			}
		} else {
			if (state != 0){
				kprint(E, 0, "Format error in %s\n", "getCurrDate()");
				return NULL;
			}
			ss << format[i];
		}
		++i;
	}
	return ss.str();
}

string getCurrUser() {
	stringstream ss;

	register struct passwd *pw;
	register uid_t uid;

	uid = geteuid();
	pw = getpwuid(uid);
	if (pw) {
		ss << pw->pw_name;
	} else {
		kprint(E, 0, "Cannot find user name for UID %u\n", (unsigned) uid);
	}
	return ss.str();
}

string getHostName() {
	stringstream ss;
	//TODO: Add implementation
	return ss.str();
}

string getSystemVersion() {
	stringstream ss;
	//TODO: Add implementation
	return ss.str();
}

string getKernelVersion() {
	stringstream ss;
	//TODO: Add implementation
	return ss.str();
}

string getCPUInfo() {
	stringstream ss;
	//TODO: Add implementation
	return ss.str();
}

string getRAMInfo() {
	stringstream ss;
	//TODO: Add implementation
	return ss.str();
}

