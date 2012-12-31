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

import java.util.logging.Logger;

import kparserbenchmark.application.Application;
import kparserbenchmark.utils.KFile;
import kparserbenchmark.utils.KImage;
import kparserbenchmark.utils.KWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Log console View
 * 
 * @author kopson
 */
public class ConsolePanel extends ViewPart implements IConsoleListener {

	/** Logger instance */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ConsolePanel.class.getName());

	/** View ID */
	public static final String ID = "KParserBenchmark.console.LogConsole";

	/** Console input text area */
	private Console mTextArea;

	/** Console special characters. Displayed in different color. */
	private final String PUNCTUATION = "(),;{}";

	/** Default console log file name */
	private static final String logFileName = "Console.log";

	/** Stop executing process action - can be enabled or disabled */
	private Action stopAction;

	@Override
	public void createPartControl(Composite parent) {
		mTextArea = new Console(parent, PUNCTUATION);
		mTextArea.addConsoleListener(this);
		meakePanelActions();
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
				mTextArea.clear();
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
				mTextArea.stopProcess();
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

	@Override
	public void processStarted() {
		stopAction.setEnabled(true);
		
	}

	@Override
	public void processFinished() {
		stopAction.setEnabled(false);
	}
}
