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

package kparserbenchmark.consoleView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import kparserbenchmark.utils.IProcessFinished;
import kparserbenchmark.utils.KDate;
import kparserbenchmark.utils.KProcess;
import kparserbenchmark.utils.KTrace;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Console windows
 * 
 * @author kopson
 */
public class Console extends StyledText {

	/** Logger instance */
	private static final Logger LOG = Logger.getLogger(Console.class.getName());

	/** Number of founded kCommand characters */
	private int inCommand;

	/** Font color */
	private Color mColorMask = null;

	/** Display used to change console text color */
	private Display mDisplay;

	/** Console special characters. Displayed in different color. */
	private String mPunctuation;

	private boolean stopProcess;

	/** Console listeners list */
	List<IConsoleListener> consoleListeners;

	/**
	 * Keyboard text control key blocking flags
	 */
	private enum KeyBlocked {
		UNBLOCK, DELETE, ALL
	}

	/**
	 * Process events handled by IConsoleListener interface
	 */
	private enum ProcessEvents {
		PROCESS_STARTED, PROCESS_FINISHED
	}

	/** Blocking flag */
	private KeyBlocked mKeyBlocked;

	/** System process to run */
	private KProcess sysProc;

	/**
	 * The console constructor
	 * 
	 * @param parent
	 *            Parent panel
	 * @param punctuation
	 *            Console special characters
	 */
	public Console(Composite parent, String punctuation) {
		super(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		consoleListeners = new ArrayList<IConsoleListener>();

		this.mPunctuation = punctuation;
		mDisplay = parent.getDisplay();
		Assert.isNotNull(mDisplay);
		stopProcess = false;
		inCommand = 0;
		newLine();

		addExtendedModifyListener(new ExtendedModifyListener() {

			/* Change color of special characters entered into text area */
			@Override
			public void modifyText(ExtendedModifyEvent event) {
				int end = event.start + event.length - 1;

				if (event.start <= end) {
					String text = getText(event.start, end);
					java.util.List<StyleRange> ranges = new java.util.ArrayList<StyleRange>();

					for (int i = 0, n = text.length(); i < n; i++) {
						if (mColorMask != null) {
							ranges.add(new StyleRange(event.start + i, 1,
									mColorMask, null));
						} else if (mPunctuation.indexOf(text.charAt(i)) > -1) {
							ranges.add(new StyleRange(event.start + i, 1,
									mDisplay.getSystemColor(SWT.COLOR_BLUE),
									null, SWT.BOLD));
						}

						// Check if user enters kCommand
						if (inCommand == 0 && isCursorOnBegin()
								&& text.charAt(i) == KTrace.kCommand.charAt(0)) {
							++inCommand;
						} else if (inCommand > 0
								&& inCommand < KTrace.kCommand.length()
								&& text.charAt(i) == KTrace.kCommand
										.charAt(inCommand)) {
							++inCommand;
						} else {
							inCommand = 0;
						}

						// If kCommand string found change text style
						if (inCommand == KTrace.kCommand.length()) {
							ranges.add(new StyleRange(
									end - inCommand + 1,
									inCommand,
									mDisplay.getSystemColor(SWT.COLOR_DARK_MAGENTA),
									null, SWT.BOLD));
							inCommand = 0;
						}
					}

					if (!ranges.isEmpty()) {
						replaceStyleRanges(event.start, event.length,
								ranges.toArray(new StyleRange[0]));
					}
				}
			}
		});

		addKeyListener(new KeyListener() {

			// Execute entered command on "Enter"
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					try {
						sysProc = new KProcess();
						final String command = getLine(getLineCount() - 2)
								.substring(KDate.getDateLen());

						sysProc.addProcessFinishListener(new IProcessFinished() {

							@Override
							public void processFinished() {
								notifyListeners(ProcessEvents.PROCESS_FINISHED);
								if (!stopProcess
										&& sysProc.getExitVal() != -999) {
									LOG.log(Level.INFO, "Process: " + command
											+ " finished with status: "
											+ sysProc.getExitVal());
								} else {
									stopProcess = true;
								}
								waitForProcess();
							}
						});
						sysProc.execute(command);
						notifyListeners(ProcessEvents.PROCESS_STARTED);

					} catch (IllegalArgumentException e1) {
						mColorMask = mDisplay.getSystemColor(SWT.COLOR_RED);
						append("\nERROR: " + e1.getMessage() + "\n");
					} catch (StringIndexOutOfBoundsException e3) {
						LOG.log(Level.SEVERE, e3.getMessage());
					} finally {
						mColorMask = null;
						// Block user input until previous command returns with
						// its output
						mKeyBlocked = KeyBlocked.ALL;
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// null
			}
		});

		addVerifyKeyListener(new VerifyKeyListener() {

			@Override
			public void verifyKey(VerifyEvent event) {
				if ((event.keyCode == SWT.BS || event.keyCode == SWT.ARROW_LEFT)
						&& mKeyBlocked == KeyBlocked.DELETE
						|| mKeyBlocked == KeyBlocked.ALL
						|| event.keyCode == SWT.ARROW_UP)
					event.doit = false;
			}
		});

		addCaretListener(new CaretListener() {

			@Override
			public void caretMoved(CaretEvent event) {
				int offset = event.caretOffset;
				int line = getLineAtOffset(offset);
				if (offset - getOffsetAtLine(line) < KDate.getDateLen()) {
					mKeyBlocked = KeyBlocked.DELETE;
				} else if (line != getLineCount() - 1) {
					mKeyBlocked = KeyBlocked.DELETE;
				} else if (offset - getOffsetAtLine(line) == KDate.getDateLen()) {
					mKeyBlocked = KeyBlocked.ALL;
				} else
					mKeyBlocked = KeyBlocked.UNBLOCK;
			}
		});
	}

	/**
	 * Update Console window with process output after process execution
	 * finished. All updates must be delegated to UI Thread
	 */
	private void waitForProcess() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					if (sysProc != null && !stopProcess) {
						String s = null;
						mColorMask = mDisplay
								.getSystemColor(SWT.COLOR_DARK_BLUE);
						while ((s = sysProc.getStdInput().readLine()) != null) {
							append(s + "\n");
						}

						mColorMask = mDisplay
								.getSystemColor(SWT.COLOR_DARK_RED);
						while ((s = sysProc.getStdError().readLine()) != null) {
							append(s + "\n");
						}
					}
				} catch (IOException e2) {
					mColorMask = mDisplay.getSystemColor(SWT.COLOR_RED);
					if (e2.getMessage().contains("error=2"))
						append("\nERROR: Command not found\n");
					else
						append("\nERROR: " + e2.getMessage() + "\n");
				} finally {
					mColorMask = null;
					mKeyBlocked = KeyBlocked.UNBLOCK;
					append("\n");
					newLine();
					stopProcess = false;
				}
			}
		});
	}

	/**
	 * Add new line and set cursor on a default position
	 */
	public void newLine() {
		append(KDate.now());
		setSelection(getCharCount());
	}

	/**
	 * Clears editor text
	 */
	public void clear() {
		setText("");
		newLine();
	}

	/**
	 * Check cursor position
	 * 
	 * @return Returns true if cursor is on beginig possition
	 */
	private boolean isCursorOnBegin() {
		return KDate.now().length() + 1 == getCharCount();
	}

	/**
	 * Stop executing process
	 */
	public void stopProcess() {
		if (sysProc != null)
			sysProc.stopExec();
		stopProcess = true;
	}

	/**
	 * Add new console listener
	 * 
	 * @param consoleListener
	 *            Listener object
	 */
	public void addConsoleListener(IConsoleListener consoleListener) {
		if (consoleListener != null)
			consoleListeners.add(consoleListener);
	}

	/** Notify console listeners */
	private void notifyListeners(ProcessEvents event) {
		switch (event) {
		case PROCESS_FINISHED:
			for (IConsoleListener consoleListener : consoleListeners) {
				consoleListener.processFinished();
			}
			break;
		case PROCESS_STARTED:
			for (IConsoleListener consoleListener : consoleListeners) {
				consoleListener.processStarted();
			}
			break;
		default:
			assert (false);
		}

	}
}
