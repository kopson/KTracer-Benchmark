package kparserbenchmark;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Application utilities class
 * 
 * @author kopson
 */
public class KWindow {

	/**
	 * This class should be never instantiate
	 */
	private KWindow() {
	}

	/**
	 * Get view by ID
	 * 
	 * @param viewID
	 * @return the view or null if view is not found
	 */
	public static IViewPart getView(String viewID) {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		return page.findView(viewID);
	}

	/**
	 * Get status line from a view
	 * 
	 * @param view
	 * @return
	 */
	public static IStatusLineManager getStatusLine(IViewPart view) {
		return view.getViewSite().getActionBars().getStatusLineManager(); 
	}
}
