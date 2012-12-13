/*
 * Ktrace.h
 *
 *  Created on: Oct 15, 2012
 *      Author: pkopka
 */

#ifndef KTRACE_H_
#define KTRACE_H_

#include <assert.h>
#include <cstddef>
#include <string.h>
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>

#include "FileUtils.h"
#include "KUtils.h"

//#define MAIN 			1
//#define TEST_DATABASE	1
#define TEST_PARSER 	1
///#define TEST_FILE		1

#define SIZEOF(x) (sizeof(x) / sizeof(int))
#define DEBUG_LEVEL 1	//0	//FATALS + ERRORS + WARNINGS + INFO + DEBUG
						//1	//FATALS + ERRORS + WARNINGS + INFO
						//2	//FATALS + ERRORS + WARNINGS
						//3	//FATALS + ERRORS
						//4	//FATALS

#define kdebug(fmt, ...) \
        do { if (DEBUG_LEVEL <= D) fprintf(stdout, "%s:%d:%s(): " fmt, \
		__FILE__, __LINE__, __func__, __VA_ARGS__); } while (0)

/* This funtion should be not used directly */
#define kprinti(fmt, ...) fprintf(stdout, fmt, __VA_ARGS__);
/* This funtion should be not used directly */
#define kprinte(level, err, fmt, ...) fprintf(stderr, "%s:%d:%s(): %s(%d): %s. " fmt "\n", \
		__FILE__, __LINE__, __func__, getLogLevel(level), \
		getErrno(err), getStrerror(err), __VA_ARGS__);
/* Default print function */
#define kprint(level, err, fmt, ...) if (DEBUG_LEVEL <= level) {\
		if(level == I) kprinti(fmt, __VA_ARGS__) else \
		kprinte(level, err, fmt, __VA_ARGS__) /*if(level > W) exit(1);*/ };

/* Error codes */
#define NOT_OPEN 		-999
#define ALREADY_OPEN 	-998
#define SYNTAX_ERROR 	-997
#define BUFFER_OVERFLOW	-996
#define PARSE_ERROR		-995
#define _SQLITE_ERROR 	-994

#define F 		4
#define E 		3
#define W 		2
#define I 		1
#define D 		0

static const char* getLogLevel(int type) {
	switch(type) {
		case F:
			return "FATAL";
		case E:
			return "ERROR";
		case W:
			return "WARNING";
		case I:
			return "INFO";
		default:
			return "DEBUG";
	}
}

static int getErrno(int errn) {
	if (errn < 0)
		return errn;
	else
		return errno;
}

static const char* getStrerror(int errn) {
	switch(errn) {
		case NOT_OPEN:
			return "File is not open";
		case ALREADY_OPEN:
			return "File already opened";
		case SYNTAX_ERROR:
			return "Syntax error";
		case BUFFER_OVERFLOW:
			return "Buffer overflow";
		case PARSE_ERROR:
			return "Parse error";
		case _SQLITE_ERROR:
			return "SQLITE";
		default:
			return strerror(errno);
	}
}

#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

extern bool 	g_compress;
extern int 		g_traceBufferSizeKB;
extern bool 	g_traceOverwrite;
extern bool 	g_traceCpuIdle;
extern bool 	g_traceGovernorLoad;
extern bool 	g_traceDisk;
extern bool 	g_traceCpuFrequency;
extern bool 	g_traceSchedSwitch;
extern bool 	g_traceWorkqueue;
extern int 		g_outputTraceFD;

namespace ktrace {

/* Sys file paths */
static const char* k_traceClockPath 			= "/sys/kernel/debug/tracing/trace_clock";
static const char* k_traceBufferSizePath 		= "/sys/kernel/debug/tracing/buffer_size_kb";
static const char* k_tracingOverwriteEnablePath = "/sys/kernel/debug/tracing/options/overwrite";
static const char* k_schedSwitchEnablePath 		= "/sys/kernel/debug/tracing/events/sched/sched_switch/enable";
static const char* k_schedWakeupEnablePath 		= "/sys/kernel/debug/tracing/events/sched/sched_wakeup/enable";
static const char* k_cpuFreqEnablePath 			= "/sys/kernel/debug/tracing/events/power/cpu_frequency/enable";
static const char* k_cpuIdleEnablePath 			= "/sys/kernel/debug/tracing/events/power/cpu_idle/enable";
static const char* k_governorLoadEnablePath 	= "/sys/kernel/debug/tracing/events/cpufreq_interactive/enable";
static const char* k_workqueueEnablePath 		= "/sys/kernel/debug/tracing/events/workqueue/enable";
static const char* k_diskEnablePaths[] = {
	"/sys/kernel/debug/tracing/events/ext4/ext4_sync_file_enter/enable",
	"/sys/kernel/debug/tracing/events/ext4/ext4_sync_file_exit/enable",
	"/sys/kernel/debug/tracing/events/block/block_rq_issue/enable",
	"/sys/kernel/debug/tracing/events/block/block_rq_complete/enable",
};

static const char* k_tracingOnPath 	= "/sys/kernel/debug/tracing/tracing_on";
static const char* k_tracePath 		= "/sys/kernel/debug/tracing/trace";

bool setKernelOptionEnable(const char* filename, bool enable);
bool setMultipleKernelOptionsEnable(const char** filenames, size_t count, bool enable);
bool setTraceOverwriteEnable(bool enable);
bool setSchedSwitchTracingEnable(bool enable);
bool setCpuFrequencyTracingEnable(bool enable);
bool setCpuIdleTracingEnable(bool enable);
bool setGovernorLoadTracingEnable(bool enable);
bool setWorkqueueTracingEnabled(bool enable);
bool setDiskTracingEnabled(bool enable);
bool setTracingEnabled(bool enable);
bool setTraceBufferSizeKB(int size);
bool setGlobalClockEnable(bool enable);
bool startTrace(bool isRoot);
bool clearTrace();
void stopTrace(bool isRoot);
void dumpTrace(const char* fileName);

} // namespace ktrace

#endif /* KTRACE_H_ */
