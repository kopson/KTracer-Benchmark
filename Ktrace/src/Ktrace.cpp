/*
 * Ktrace.cpp
 *
 *  Created on: Oct 15, 2012
 *      Author: pkopka
 */

#include <stdlib.h>
#include <unistd.h>

#include "Ktrace.h"

bool 	g_compress 				= false;
int 	g_traceBufferSizeKB 	= 2048;
bool 	g_traceOverwrite 		= false;
bool 	g_traceCpuIdle 			= false;
bool 	g_traceGovernorLoad 	= false;
bool 	g_traceDisk 			= false;
bool 	g_traceCpuFrequency 	= false;
bool 	g_traceSchedSwitch 		= false;
bool 	g_traceWorkqueue 		= false;
int		g_outputTraceFD			= STDOUT_FILENO;

namespace ktrace {

// Enable or disable a kernel option by writing a "1" or a "0" into a /sys file.
bool setKernelOptionEnable(const char* fileName, bool enable) {
	return KFile(fileName).writeStr(enable ? "1" : "0");
}

// Enable or disable a collection of kernel options by writing a "1" or a "0" into each /sys file.
bool setMultipleKernelOptionsEnable(const char** filenames, size_t count, bool enable) {
	bool result = true;
	for (size_t i = 0; i < count; i++) {
		result &= setKernelOptionEnable(filenames[i], enable);
	}

	return result;
}

// Enable or disable overwriting of the kernel trace buffers.  Disabling this
// will cause tracing to stop once the trace buffers have filled up.
bool setTraceOverwriteEnable(bool enable) {
	return setKernelOptionEnable(ktrace::k_tracingOverwriteEnablePath, enable);
}

// Enable or disable tracing of the kernel scheduler switching.
bool setSchedSwitchTracingEnable(bool enable) {
	bool ok = true;
	ok &= setKernelOptionEnable(ktrace::k_schedSwitchEnablePath, enable);
	ok &= setKernelOptionEnable(ktrace::k_schedWakeupEnablePath, enable);
	return ok;
}

// Enable or disable tracing of the CPU clock frequency.
bool setCpuFrequencyTracingEnable(bool enable) {
	return setKernelOptionEnable(ktrace::k_cpuFreqEnablePath, enable);
}

// Enable or disable tracing of CPU idle events.
bool setCpuIdleTracingEnable(bool enable) {
	return setKernelOptionEnable(ktrace::k_cpuIdleEnablePath, enable);
}

// Enable or disable tracing of the interactive CPU frequency governor's idea of
// the CPU load.
bool setGovernorLoadTracingEnable(bool enable) {
	return setKernelOptionEnable(ktrace::k_governorLoadEnablePath, enable);
}

// Enable or disable tracing of the kernel workqueues.
bool setWorkqueueTracingEnabled(bool enable) {
	return setKernelOptionEnable(ktrace::k_workqueueEnablePath, enable);
}

// Enable or disable tracing of disk I/O.
bool setDiskTracingEnabled(bool enable) {
    return setMultipleKernelOptionsEnable(ktrace::k_diskEnablePaths, NELEM(ktrace::k_diskEnablePaths), enable);
}

// Enable or disable kernel tracing.
bool setTracingEnabled(bool enable) {
	return setKernelOptionEnable(ktrace::k_tracingOnPath, enable);
}

// Set the size of the kernel's trace buffer in kilobytes.
bool setTraceBufferSizeKB(int size) {
	char str[32] = "1";

	if (size < 1) {
		size = 1;
	}
	snprintf(str, 32, "%d", size);
	return KFile(ktrace::k_traceBufferSizePath).writeStr(str);
}

// Enable or disable the kernel's use of the global clock.  Disabling the global
// clock will result in the kernel using a per-CPU local clock.
bool setGlobalClockEnable(bool enable) {
	return KFile(ktrace::k_traceClockPath).writeStr(enable ? "global" : "local");
}

// Enable tracing in the kernel.
bool startTrace(bool isRoot) {
	bool ok = true;

	// Set up the tracing options that don't require root.
	//ok &= setTraceOverwriteEnable(g_traceOverwrite);
	ok &= setSchedSwitchTracingEnable(g_traceSchedSwitch);
	//ok &= setCpuFrequencyTracingEnable(g_traceCpuFrequency);
	//ok &= setCpuIdleTracingEnable(g_traceCpuIdle);
	if (fileExists(ktrace::k_governorLoadEnablePath) || g_traceGovernorLoad) {
		ok &= setGovernorLoadTracingEnable(g_traceGovernorLoad);
	}
	ok &= setTraceBufferSizeKB(g_traceBufferSizeKB);
	ok &= setGlobalClockEnable(true);

	// Set up the tracing options that do require root.  The options that
	// require root should have errored out earlier if we're not running as
	// root.
	if (isRoot) {
		ok &= setWorkqueueTracingEnabled(g_traceWorkqueue);
		ok &= setDiskTracingEnabled(g_traceDisk);
	}

	// Enable tracing.
	ok &= setTracingEnabled(true);

	if (!ok) {
		fprintf(stderr, "error: unable to start trace\n");
	}

	return ok;
}

// Disable tracing in the kernel.
void stopTrace(bool isRoot) {
	// Disable tracing.
	setTracingEnabled(false);

	// Set the options back to their defaults.
	setTraceOverwriteEnable(true);
	setSchedSwitchTracingEnable(false);
	setCpuFrequencyTracingEnable(false);
	if (fileExists(ktrace::k_governorLoadEnablePath)) {
		setGovernorLoadTracingEnable(false);
	}
	setGlobalClockEnable(false);

	if (isRoot) {
		setWorkqueueTracingEnabled(false);
		setDiskTracingEnabled(false);
	}

	// Note that we can't reset the trace buffer size here because that would
	// clear the trace before we've read it.
}

// Read the current kernel trace and write it to stdout.
void dumpTrace(const char* fileName) {

	KFile traceFile(ktrace::k_tracePath, "r+w");
	if(g_outputTraceFD) {
		traceFile.readFile();
		return;
	}

	if (g_compress) {
		traceFile.compressFile(fileName);
	} else {
		traceFile.copyFile(fileName);
	}
}

} // namespace ktrace
