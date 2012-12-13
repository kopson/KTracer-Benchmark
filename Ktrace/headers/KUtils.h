/*
 * KUtils.h
 *
 *  Created on: Nov 7, 2012
 *      Author: kopson
 *
 * Provides some useful general-purpose functions.
 */

#ifndef KUTILS_H_
#define KUTILS_H_

#include <string>

std::string getCurrDate(const char *format);
std::string getCurrUser();
std::string getSystemVersion();
std::string getKernelVersion();
std::string getCPUInfo();
std::string getRAMInfo();

#endif /* KUTILS_H_ */
