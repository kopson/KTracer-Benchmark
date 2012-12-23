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
import java.util.logging.Level;
import java.util.logging.Logger;

import kparserbenchmark.application.Application;
import kparserbenchmark.utils.IProcessFinished;
import kparserbenchmark.utils.KDate;
import kparserbenchmark.utils.KFile;
import kparserbenchmark.utils.KImage;
import kparserbenchmark.utils.KProcess;
import kparserbenchmark.utils.KTrace;
import kparserbenchmark.utils.KWindow;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

/**
 * Log console View
 * 
 * @author kopson
 */
public class ConsolePanel extends ViewPart {

	/** Logger instance */
	private static final Logger LOG = Logger.getLogger(KWindow.class.getName());

	/** View ID */
	public static final String ID = "KParserBenchmark.console.LogConsole";

	/** Console input text area */
	private Console mTextArea;

	/** Display used to change console text color */
	private Display mDisplay;

	/** Console special characters. Displayed in different color. */
	private final String PUNCTUATION = "(),;{}";

	/** Font color */
	private Color mColorMask = null;

	/**
	 * Keyboard text control key blocking flags
	 */
	private enum KeyBlocked {
		UNBLOCK, DELETE, ALL
	}

	/** Blocking flag */
	private KeyBlocked mKeyBlocked;

	/** Default console log file name */
	private static final String logFileName = "Console.log";

	/** Number of founded kCommand characters */
	private int inCommand;

	/** System process to run */
	private KProcess sysProc;

	/** Stop executing process action - can be enabled or disabled */
	private Action stopAction;

	private boolean stopProcess;

	@Override
	public void createPartControl(Composite parent) {

		mDisplay = parent.getDisplay();
		Assert.isNotNull(mDisplay);

		meakePanelActions();

		mTextArea = new Console(parent);

		newLine();
		inCommand = 0;
		stopProcess = false;

		mTextArea.addExtendedModifyListener(new ExtendedModifyListener() {

			/* Change color of special characters entered into text area */
			@Override
			public void modifyText(ExtendedModifyEvent event) {
				int end = event.start + event.length - 1;

				if (event.start <= end) {
					String text = mTextArea.getText(event.start, end);
					java.util.List<StyleRange> ranges = new java.util.ArrayList<StyleRange>();

					for (int i = 0, n = text.length(); i < n; i++) {
						if (mColorMask != null) {
							ranges.add(new StyleRange(event.start + i, 1,
									mColorMask, null));
						} else if (PUNCTUATION.indexOf(text.charAt(i)) > -1) {
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
						mTextArea.replaceStyleRanges(event.start, event.length,
								ranges.toArray(new StyleRange[0]));
					}
				}
			}
		});

		mTextArea.addKeyListener(new KeyListener() {

			// Execute entered command on "Enter"
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					try {
						sysProc = new KProcess();
						final String command = mTextArea.getLine(
								mTextArea.getLineCount() - 2).substring(
								KDate.getDateLen());

						sysProc.addProcessFinishListener(new IProcessFinished() {

							@Override
							public void processFinished() {
								stopAction.setEnabled(false);
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
						stopAction.setEnabled(true);

					} catch (IllegalArgumentException e1) {
						mColorMask = mDisplay.getSystemColor(SWT.COLOR_RED);
						mTextArea.append("\nERROR: " + e1.getMessage() + "\n");
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

		mTextArea.addVerifyKeyListener(new VerifyKeyListener() {

			@Override
			public void verifyKey(VerifyEvent event) {
				if ((event.keyCode == SWT.BS || event.keyCode == SWT.ARROW_LEFT)
						&& mKeyBlocked == KeyBlocked.DELETE
						|| mKeyBlocked == KeyBlocked.ALL
						|| event.keyCode == SWT.ARROW_UP)
					event.doit = false;
			}
		});

		mTextArea.addCaretListener(new CaretListener() {

			@Override
			public void caretMoved(CaretEvent event) {
				int offset = event.caretOffset;
				int line = mTextArea.getLineAtOffset(offset);
				if (offset - mTextArea.getOffsetAtLine(line) < KDate
						.getDateLen()) {
					mKeyBlocked = KeyBlocked.DELETE;
				} else if (line != mTextArea.getLineCount() - 1) {
					mKeyBlocked = KeyBlocked.DELETE;
				} else if (offset - mTextArea.getOffsetAtLine(line) == KDate
						.getDateLen()) {
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
							mTextArea.append(s + "\n");
						}

						mColorMask = mDisplay
								.getSystemColor(SWT.COLOR_DARK_RED);
						while ((s = sysProc.getStdError().readLine()) != null) {
							mTextArea.append(s + "\n");
						}
					}
				} catch (IOException e2) {
					mColorMask = mDisplay.getSystemColor(SWT.COLOR_RED);
					if (e2.getMessage().contains("error=2"))
						mTextArea.append("\nERROR: Command not found\n");
					else
						mTextArea.append("\nERROR: " + e2.getMessage() + "\n");
				} finally {
					mColorMask = null;
					mKeyBlocked = KeyBlocked.UNBLOCK;
					mTextArea.append("\n");
					newLine();
					stopProcess = false;
				}
			}
		});
	}

	/** Make console panel actions */
	private void meakePanelActions() {
		Action saveAction = new Action("Save") {
			// Save selected (or all if nothing was selected) output into log
			// file
			@Override
			public void run() {
				String path = null;

				if (Application.getCurrProject() != null)
					path = Application.getCurrProject().getPath();

				String logFile = KWindow.saveFileDialog(path, logFileName,
						KWindow.LOG);
				if (logFile != null) {
					KFile file = new KFile(logFile);
					String text = mTextArea.getSelectionText();
					if (text == null || text.equals(""))
						text = mTextArea.getText();
					file.setText(text);
				}
			}
		};

		saveAction.setToolTipText("Save log to file");
		saveAction.setImageDescriptor(KImage
				.getImageDescriptor(KImage.IMG_SAVE_CONSOLE));
		saveAction.setDisabledImageDescriptor(KImage
				.getImageDescriptor(KImage.IMG_SAVE_CONSOLE));

		Action clearAction = new Action("Clear") {
			@Override
			public void run() {
				clear();
			}
		};

		clearAction.setToolTipText("Clear console input");
		clearAction.setImageDescriptor(KImage
				.getImageDescriptor(KImage.IMG_CLEAR_CONSOLE));
		clearAction.setDisabledImageDescriptor(KImage
				.getImageDescriptor(KImage.IMG_CLEAR_CONSOLE));

		stopAction = new Action("Stop") {
			// Stop executing current task
			@Override
			public void run() {
				if (sysProc != null)
					sysProc.stopExec();
				stopProcess = true;
			}
		};

		stopAction.setEnabled(false);
		stopAction.setToolTipText("Stop executing current command");
		stopAction.setImageDescriptor(KImage
				.getImageDescriptor(KImage.IMG_STOP_CONSOLE));
		stopAction.setDisabledImageDescriptor(KImage
				.getImageDescriptor(KImage.IMG_STOP_CONSOLE));

		ToolBarManager toolBar = (ToolBarManager) getViewSite().getActionBars()
				.getToolBarManager();
		toolBar.add(stopAction);
		toolBar.add(saveAction);
		toolBar.add(clearAction);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		mTextArea.setFocus();
	}

	/**
	 * Clears editor text
	 */
	public void clear() {
		if (mTextArea != null) {
			mTextArea.setText("");
			newLine();
		}
	}

	/**
	 * Add new line and set cursor on a default position
	 */
	private void newLine() {
		if (mTextArea != null) {
			mTextArea.append(KDate.now());
			mTextArea.setSelection(mTextArea.getCharCount());
		}
	}

	/**
	 * Check cursor position
	 * 
	 * @return Returns true if cursor is on beginig possition
	 */
	private boolean isCursorOnBegin() {
		if (mTextArea != null)
			return KDate.now().length() + 1 == mTextArea.getCharCount();
		else
			return false;
	}
}
