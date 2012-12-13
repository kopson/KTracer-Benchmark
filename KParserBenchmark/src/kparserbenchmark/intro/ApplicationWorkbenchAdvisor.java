package kparserbenchmark.intro;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Manages initial state of application
 * 
 * @author kopson
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	//Initial perspective ID
	private static final String PERSPECTIVE_ID = "KParserBenchmark.perspective";

	@Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }
    
	@Override
    public void initialize(IWorkbenchConfigurer configurer) {
        super.initialize(configurer);
        //TODO: There is a problem here to save instance state
        //configurer.setSaveAndRestore(true);
    }

	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
}
