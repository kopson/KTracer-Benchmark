package kparserbenchmark.projects.test;

import kparserbenchmark.editor.ScriptEditorInput;
import kparserbenchmark.projectexplorer.ProjectLeaf;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class TableEditor extends EditorPart {

	/**
	 * Table editor ID.
	 */
	public static final String ID = "KParserBenchmark.projects.test.TableEditor";

	/**
	 * Editor body.
	 */
	private TableViewer viewer;

	/** Editor input file */
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

	private void createMenu(Composite parent) {
		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search: ");
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
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

		// Layout the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);
	}

	// This will create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "ID", "Log Name", "Log Type", "Timestamp" };
		int[] bounds = { 50, 150, 100, 100 };

		int columnNo = 0;
		// First column is for the first name
		TableViewerColumn col = createTableViewerColumn(titles[columnNo],
				bounds[columnNo], columnNo);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Record p = (Record) element;
				return Integer.toString(p.getLogId());
			}
		});

		++columnNo;
		// Second column is for the last name
		col = createTableViewerColumn(titles[columnNo], bounds[columnNo],
				columnNo);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Record p = (Record) element;
				return p.getLogName();
			}
		});

		++columnNo;
		// Now the gender
		col = createTableViewerColumn(titles[columnNo], bounds[columnNo],
				columnNo);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Record p = (Record) element;
				return p.getLogType();
			}
		});

		++columnNo;
		// // Now the status married
		col = createTableViewerColumn(titles[columnNo], bounds[columnNo],
				columnNo);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Record p = (Record) element;
				return Long.toString(p.getTimestamp());
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound,
			final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
				SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	@Override
	public void setFocus() {
		if(viewer != null)
			viewer.getControl().setFocus();
	}

	/**
	 * Returns script editor name.
	 * 
	 * @return script editor name.
	 */
	private String getScriptEditorName() {
		return ((ScriptEditorInput) getEditorInput()).getName();
	}
}