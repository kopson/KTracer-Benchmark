package kparserbenchmark;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Image utilities class
 * 
 * @author kopson
 */
public class KImage {

	//Image icons constants
	public static final String IMG_PROJECT_OPENED = "/icons/open_24.png";
	public static final String IMG_PROJECT_CLOSED = "/icons/box_24.png";
	public static final String IMG_PROJECT_FILE = "/icons/document_24.png";
	public static final String IMG_OK_STATUS = "/icons/tick_16.png";
	public static final String IMG_ERR_STATUS = "/icons/cross_16.png";
	
	//Resource images
	private static Image okStatusImage = getImageDescriptor(IMG_OK_STATUS).createImage();
	private static Image errStatusImage =  getImageDescriptor(IMG_ERR_STATUS).createImage();
	
	/**
	 * This class should be never instantiate
	 */
	private KImage() {
	}

	/**
	 * Return shared image eg. ISharedImages.IMG_OBJ_FOLDER
	 * 
	 * @param image
	 * @return
	 */
	public static Image getSharedImage(String image) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(image);
	}

	/**
	 * Returns image descriptor from project's resources
	 * 
	 * @param id
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String id) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				id);
	}

	/**
	 * Returns image descriptor from project's resources
	 * 
	 * @param id
	 * @return
	 */
	public static ImageDescriptor createImageDescriptorFor(String id) {
		URL url = Platform.getBundle(Activator.PLUGIN_ID).getEntry(id);
		return ImageDescriptor.createFromURL(url);
	}

	/**
	 * Draws image on window surface
	 * 
	 * @param parent
	 */
	public static Canvas drawImage(Composite parent, final Image image,
			final int x, final int y) {
		// Create the canvas for drawing
		Canvas canvas = new Canvas(parent, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.drawImage(image, x, y);
			}
		});
		return canvas;
	}

	/**
	 * Resize image
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image resize(Image image, int width, int height) {
		return resize(image, width, height, false);
	}

	/**
	 * Resize image
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @param dispose
	 * @return
	 */
	public static Image resize(Image image, int width, int height,
			boolean dispose) {
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width,
				image.getBounds().height, 0, 0, width, height);
		if (dispose)
			gc.dispose();
		image.dispose(); // don't forget about me!
		return scaled;
	}

	/**
	 * Return resource image
	 * 
	 * @param img
	 * @return
	 */
	public static Image getImage(String img) {
		if (img.equals(IMG_OK_STATUS))
			return okStatusImage;
		else if (img.equals(IMG_ERR_STATUS))
			return errStatusImage;
		else
			return null;
	}
}
