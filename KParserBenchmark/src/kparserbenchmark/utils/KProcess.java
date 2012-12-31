/*******************************************************************************
 Copyright (c) 2012 kopson kopson.piko@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 *******************************************************************************/

package kparserbenchmark.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Execute external command as a separate process in nonblocking mode
 * 
 * @author kopson
 */
public class KProcess extends Thread {

	/** Logger instance */
	private static final Logger LOG = Logger
			.getLogger(KProcess.class.getName());

	/** External process */
	private Process process;

	/** External process command */
	private String processCommand;

	/** External process command arguments */
	private String[] processArgs;

	/** External process result */
	private int exitVal;

	/** User stop process flag */
	private boolean stopProcess;

	/** External process input stream */
	private BufferedReader input;
	
	/** External process error stream */
	private BufferedReader error;
	
	/** External process output stream */
	private BufferedWriter output;
	
	/**
	 * Event listeners list
	 */
	private final List<IProcessFinished> processListeners;
	
	public KProcess() {
		super();
		processListeners = new ArrayList<IProcessFinished>();
	}

	/** Execute external command */
	public void execute(String command) {
		this.processCommand = command;
		this.stopProcess = false;
		this.setName("ExternalProcessTest");
		this.start();
	}

	@Override
	public void run() {
		try {
			// Execute string command processCommand as background process ...
			process = Runtime.getRuntime().exec(processCommand);
			input = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			error = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			output = new BufferedWriter(new OutputStreamWriter(
					process.getOutputStream()));
			while (IsRunning()) {
				Thread.sleep(300);
			}
			if (stopProcess) {
				process.destroy();
				exitVal = -998;
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage());
			exitVal = -999;
		} finally {
			notifyListeners();
		}
	}

	/**
	 * Add new process listener
	 * 
	 * @param processListener
	 *            Listener object
	 */
	public void addProcessFinishListener(IProcessFinished processListener) {
		if (processListener != null)
			processListeners.add(processListener);
	}

	/**
	 * Notify all listeners about process execution ending
	 */
	private void notifyListeners() {
		for (IProcessFinished processListener : processListeners) {
			processListener.processFinished();
		}
	}

	/**
	 * If the process is still running, a call to exitValue() will throw an
	 * IllegalThreadStateException exception.
	 * 
	 * @return Returns true if process is still running or false if process has
	 *         finished or user stops it
	 */
	private boolean IsRunning() {
		boolean isRunning = false;
		try {
			exitVal = process.exitValue();
		} catch (IllegalThreadStateException e) {
			isRunning = true;
		}
		return isRunning && !stopProcess;
	}

	/**
	 * Redirect process standard input
	 * 
	 * @return Returns process standard input or null if process is not started
	 */
	public BufferedReader getStdInput() {
		if (process != null && input != null)
			return input;
		else {
			LOG.log(Level.SEVERE, "Process is not started!");
			return null;
		}
	}

	/**
	 * Redirect process standard error input
	 * 
	 * @return Returns process standard error input or null if process is not
	 *         started
	 */
	public BufferedReader getStdError() {
		if (process != null)
			return error;
		else {
			LOG.log(Level.SEVERE, "Process is not started!");
			return null;
		}
	}

	/**
	 * Redirect process standard output
	 * 
	 * @return Returns process standard output input or null if process is not
	 *         started
	 */
	public BufferedWriter getStdOutput() {
		if (process != null)
			return output;
		else {
			LOG.log(Level.SEVERE, "Process is not started!");
			return null;
		}
	}

	/**
	 * Stop process manually
	 */
	public void stopExec() {
		stopProcess = true;
	}

	public int getExitVal() {
		return exitVal;
	}
}
