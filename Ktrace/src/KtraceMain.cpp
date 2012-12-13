/*
 * KtraceMain.cpp
 *
 *  Created on: 07-10-2012
 *      Author: kopson
 */

#include <cstring>
#include <errno.h>
#include <fcntl.h>
#include <signal.h>
#include <stdarg.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>

#include "Ktrace.h"

static const size_t BUF_SIZE = 256;

/* Global state */
static bool g_traceAborted 			= false;
static int 	g_traceDurationSeconds 	= 5;

// Print the command usage help to stderr.
static void showHelp(const char *cmd) {
	fprintf(stderr, "usage: %s [options]\n", cmd);
	fprintf(stderr, "options include:\n"
					"  -b N            use a trace buffer size of N KB\n"
					"  -c              trace into a circular buffer\n"
					"  -d              trace disk I/O\n"
					"  -f              trace CPU frequency changes\n"
					"  -i              trace CPU idle\n"
					"  -l              trace CPU frequency governor load\n"
					"  -s              trace the kernel scheduler switches\n"
					"  -t N            trace for N seconds [defualt 5]\n"
					"  -w              trace the kernel workqueue\n"
					"  -z              compress the trace dump\n"
					"  -o <path>	   output file path\n");
}

//Handle user abort signals from commandline
static void handleSignal(int signo) {
	g_traceAborted = true;
}

static void registerSigHandler() {
	struct sigaction sa;
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = 0;
	sa.sa_handler = handleSignal;
	sigaction(SIGHUP, &sa, NULL);
	sigaction(SIGINT, &sa, NULL);
	sigaction(SIGQUIT, &sa, NULL);
	sigaction(SIGTERM, &sa, NULL);
}

bool clearTrace() {
	return clearFile(ktrace::k_tracePath);
}

#ifdef MAIN
int main(int argc, char **argv) {
	bool isRoot = (getuid() == 0);
	char output[BUF_SIZE];

	if (argc == 1 || (argc == 2 && 0 == strcmp(argv[1], "--help"))) {
		showHelp(argv[0]);
		exit(0);
	}

	for (;;) {
		int ret;

		if ((ret = getopt(argc, argv, "b:cidflst:wzo:")) < 0) {
			break;
		}

		switch(ret) {
			case 'b':
				g_traceBufferSizeKB = atoi(optarg);
				break;
			case 'c':
				g_traceOverwrite = true;
				break;
			case 'i':
				g_traceCpuIdle = true;
				break;
			case 'l':
				g_traceGovernorLoad = true;
				break;
			case 'd':
				if (!isRoot) {
					fprintf(stderr, "error: tracing disk activity requires root privileges\n");
					exit(1);
				}
				g_traceDisk = true;
				break;
			case 'f':
				g_traceCpuFrequency = true;
				break;
			case 's':
				g_traceSchedSwitch = true;
				break;
			case 't':
				g_traceDurationSeconds = atoi(optarg);
				break;
			case 'w':
				if (!isRoot) {
					fprintf(stderr, "error: tracing kernel work queues requires root privileges\n");
					exit(1);
				}
				g_traceWorkqueue = true;
				break;
			case 'z':
				g_compress = true;
				break;
			case 'o':
				if(strlen(optarg) >= BUF_SIZE) {
					fprintf(stderr, "error: path too long\n");
					exit(1);
				}
				strncpy(output, optarg, BUF_SIZE);
				g_outputTraceFD = 0;
				break;
			default:
				fprintf(stderr, "\n");
				showHelp(argv[0]);
				exit(-1);
				break;
		}
	}

	registerSigHandler();

	bool ok = ktrace::startTrace(isRoot);

	if (ok) {
		kprint(I, 0, "Capturing trace...%s\n", "");
		fflush(stdout);

		// We clear the trace after starting it because tracing gets enabled for
		// each CPU individually in the kernel. Having the beginning of the trace
		// contain entries from only one CPU can cause "begin" entries without a
		// matching "end" entry to show up if a task gets migrated from one CPU to
		// another.
		ok = clearTrace();

		if (ok) {
			// Sleep to allow the trace to be captured.
			struct timespec timeLeft;
			timeLeft.tv_sec = g_traceDurationSeconds;
			timeLeft.tv_nsec = 0;
			do {
				if (g_traceAborted) {
					break;
				}
			} while (nanosleep(&timeLeft, &timeLeft) == -1 && errno == EINTR);
		}
	}

	// Stop the trace and restore the default settings.
	ktrace::stopTrace(isRoot);

	if (ok) {
		if (!g_traceAborted) {
			kprint(I, 0, "Done...%s\n", "");
			ktrace::dumpTrace(output);
		} else {
			kprint(W, 0, "Done...%s\n", "");
		}
		clearTrace();
	} else {
		kprint(E, 0, "Unable to start tracing...%s\n", "");
	}

	// Reset the trace buffer size to 1.
	ktrace::setTraceBufferSizeKB(1);

	return g_traceAborted ? 1 : 0;

}
#endif



