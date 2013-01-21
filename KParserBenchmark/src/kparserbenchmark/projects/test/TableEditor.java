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

package kparserbenchmark.projects.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kparserbenchmark.editor.ScriptEditorInput;
import kparserbenchmark.projectexplorer.ProjectLeaf;
import kparserbenchmark.utils.KImage;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

/**
 * Table data editor
 * 
 * @author Kopson
 */
public class TableEditor extends EditorPart {

	/** Table editor ID */
	public static final String ID = "KParserBenchmark.projects.test.TableEditor";

	/** Column comparator */
	private TableViewerComparator comparator;

	/** Record filter */
	private RecordFilter nameFilter;

	/** Record time filter */
	private TimeFilter timeFilter;

	/** Editor body */
	private TableViewer viewer;

	private Text startTimeText;
	private Text endTimeText;
	private boolean noVerify;

	/** Editor input file */
	@SuppressWarnings("unused")
	private ProjectLeaf inputFile;

	@Override
	public void doSave(IProgressMonitor monitor) {
		return;
	}

	@Override
	public void doSaveAs() {
		return;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(getScriptEditorName());
		inputFile = ((ScriptEditorInput) input).getItem();
		comparator = new TableViewerComparator();
		noVerify = false;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		createMenu(parent);
		createViewer(parent);
	}

	/**
	 * Create table menu
	 * 
	 * @param parent
	 *            parent controller
	 */
	private void createMenu(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout(8, false);
		container.setLayout(layout);
		GridData gridData = new GridData(SWT.FILL, SWT.NONE, true, false);
		gridData.horizontalSpan = 2;
		container.setLayoutData(gridData);

		Label searchLabel = new Label(container, SWT.NONE);
		searchLabel.setText("Search: ");
		final Text searchText = new Text(container, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
		// New to support the search
		searchText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				nameFilter.setSearchText(searchText.getText());
				viewer.refresh();
			}
		});

		Label startTimeLabel = new Label(container, SWT.NONE);
		startTimeLabel.setText("Time from: ");

		startTimeText = new Text(container, SWT.BORDER | SWT.SEARCH);
		startTimeText
				.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		startTimeText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				timeFilter.setStartTime((startTimeText.getText() == "" || endTimeText
						.getText() == null) ? "0" : startTimeText.getText());
				viewer.refresh();
			}
		});
		// startTimeText.setText("0");
		startTimeText.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent event) {
				if (event.character == SWT.BS
						|| event.keyCode == SWT.ARROW_LEFT
						|| event.keyCode == SWT.ARROW_RIGHT
						|| event.keyCode == SWT.DEL) {
					event.doit = true;
					return;
				}
				/*if (startTimeText.getText().length() == 1
						&& startTimeText.getText().charAt(0) == '0') {
					event.doit = false;
					return;
				}*/
				if (!noVerify) {
					if (!('0' <= event.character && event.character <= '9')) {
						event.doit = false;
						return;
					}
				} else {
					noVerify = false;
				}
			}
		});

		Label endTimeLabel = new Label(container, SWT.NONE);
		endTimeLabel.setText("Time to: ");

		endTimeText = new Text(container, SWT.BORDER | SWT.SEARCH);
		endTimeText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		endTimeText.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent ke) {
				timeFilter.setEndTime((endTimeText.getText() == "" || endTimeText
						.getText() == null) ? Long.toString(Long.MAX_VALUE)
						: endTimeText.getText());
				viewer.refresh();
			}
		});
		// endTimeText.setText("0");
		endTimeText.addVerifyListener(new VerifyListener() {

			@Override
			public void verifyText(VerifyEvent event) {
				if (event.character == SWT.BS
						|| event.keyCode == SWT.ARROW_LEFT
						|| event.keyCode == SWT.ARROW_RIGHT
						|| event.keyCode == SWT.DEL) {
					event.doit = true;
					return;
				}
				/*if (endTimeText.getText().length() == 1
						&& endTimeText.getText().charAt(0) == '0') {
					event.doit = false;
					return;
				}*/
				if (!noVerify) {
					if (!('0' <= event.character && event.character <= '9')) {
						event.doit = false;
						return;
					}
				} else {
					noVerify = false;
				}
			}
		});
	}

	/**
	 * Create table
	 * 
	 * @param parent
	 *            parent controller
	 */
	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		// Get the content for the viewer, setInput will call getElements in the
		// contentProvider
		viewer.setInput(RecordProvider.INSTANCE.getRecords());
		// Make the selection available to other views
		getSite().setSelectionProvider(viewer);
		// Set the sorter for the table

		// Sort according to due date
		viewer.setComparator(comparator);

		nameFilter = new RecordFilter();
		timeFilter = new TimeFilter();
		viewer.addFilter(nameFilter);
		viewer.addFilter(timeFilter);

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			boolean toggleTime = false;

			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object selection = event.getSelection();
				if (selection instanceof StructuredSelection) {
					Record rec = (Record) ((StructuredSelection) selection)
							.getFirstElement();
					noVerify = true;
					if (toggleTime) {
						endTimeText.setText(Long.toString(rec.getTimestamp()));
						timeFilter.setEndTime(endTimeText.getText());
					} else {
						startTimeText.setText(Long.toString(rec.getTimestamp()));
						timeFilter.setStartTime(startTimeText.getText());
					}
					toggleTime = !toggleTime;
					viewer.refresh();
				}
			}
		});
	}

	// The createMenuItem method add per column a
	// new MenuItem to the menu
	private void createMenuItem(Menu parent, final TableColumn column) {
		final MenuItem itemName = new MenuItem(parent, SWT.CHECK);
		itemName.setText(column.getText());
		itemName.setSelection(column.getResizable());
		itemName.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				if (itemName.getSelection()) {
					column.setWidth(150);
					column.setResizable(true);
				} else {
					column.setWidth(0);
					column.setResizable(false);
				}
			}
		});
	}

	/**
	 * Create columns for the table
	 * 
	 * @param parent
	 *            parent controller
	 */
	private void createColumns(final Composite parent) {
		String[] titles = { "ID", "Log Name", "Log Type", "Timestamp" };
		int[] bounds = { 50, 150, 100, 100 };

		// Define the menu and assign to the table
		Menu headerMenu = new Menu(viewer.getTable());
		viewer.getTable().setMenu(headerMenu);

		int columnNo = 0;
		TableViewerColumn col = createTableViewerColumn(titles[columnNo],
				bounds[columnNo], columnNo);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Record p = (Record) element;
				return Integer.toString(p.getLogId());
			}
		});
		createMenuItem(headerMenu, col.getColumn());

		++columnNo;
		col = createTableViewerColumn(titles[columnNo], bounds[columnNo],
				columnNo);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Record p = (Record) element;
				return p.getLogName();
			}
		});
		createMenuItem(headerMenu, col.getColumn());

		++columnNo;
		col = createTableViewerColumn(titles[columnNo], bounds[columnNo],
				columnNo);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Record p = (Record) element;
				return p.getLogType();
			}
		});
		createMenuItem(headerMenu, col.getColumn());

		++columnNo;
		col = createTableViewerColumn(titles[columnNo], bounds[columnNo],
				columnNo);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Record p = (Record) element;
				return Long.toString(p.getTimestamp());
			}
		});
		createMenuItem(headerMenu, col.getColumn());
	}

	/**
	 * Create single column
	 * 
	 * @param title
	 * @param bound
	 * @param colNumber
	 * 
	 * @return Returns table column
	 */
	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		column.addSelectionListener(getSelectionAdapter(column, colNumber));
		return viewerColumn;
	}

	/**
	 * Get current sorting state
	 * 
	 * @param column
	 *            table column
	 * @param index
	 *            column index
	 * 
	 * @return Returns selection listener
	 */
	private SelectionAdapter getSelectionAdapter(final TableColumn column,
			final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	@Override
	public void setFocus() {
		if (viewer != null)
			viewer.getControl().setFocus();
	}

	/**
	 * Return script editor name.
	 * 
	 * @return Returns script editor name.
	 */
	private String getScriptEditorName() {
		return ((ScriptEditorInput) getEditorInput()).getName();
	}
}
