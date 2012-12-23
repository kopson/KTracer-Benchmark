package kparserbenchmark.application;

import kparserbenchmark.utils.KImage;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * Manages creating main window of application
 * 
 * @author kopson
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	//Status line image
	Image statusImage;
	
	/**
	 * The constructor
	 * 
	 * @param configurer
	 */
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(700, 550));
        configurer.setShowCoolBar(false);
        configurer.setShowStatusLine(true);
        configurer.setShowMenuBar(true);
        //configurer.setTitle("KTracer");
    }
    
    @Override
    public void postWindowOpen() {
    	statusImage = KImage.getImageDescriptor(KImage.IMG_OK_STATUS).createImage();
		/*IStatusLineManager statusline = getWindowConfigurer()
				.getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage(statusImage, "KParserBenchmark started");*/
	}
    
    @Override
    	public void dispose() {
    	if (statusImage != null)
    	statusImage.dispose();
    	}

}
