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

import java.net.URL;

import kparserbenchmark.application.Activator;

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
 * Image utilities class. Provides image create and manipulate functions, paths<br>
 * for resource images and static image objects precreated to avoid memory leaks.
 * 
 * @author kopson
 */
public class KImage {

	//Image icons constants
	public static final String IMG_PROJECT_OPENED = "/icons/open_24.png";
	public static final String IMG_PROJECT_CLOSED = "/icons/box_24.png";
	public static final String IMG_PROJECT_FILE = "/icons/document_24.png";
	public static final String IMG_PROJECT_FOLDER = "/icons/folder_24.png";
	public static final String IMG_SCRIPT_EDITOR = "icons/pencil_16.png";
	public static final String IMG_LOG_CONSOLE = "/icons/terminal_16.png";
	public static final String IMG_CLEAR_CONSOLE = "/icons/trash_16.png";
	public static final String IMG_SAVE_CONSOLE =  "/icons/save_16.png";
	public static final String IMG_STOP_CONSOLE = "/icons/delete_16.png";
	
	//Image icons for status line
	public static final String IMG_INFO_STATUS = "/icons/status/info_16.png";
	public static final String IMG_NEW_STATUS = "/icons/status/add_16.png";
	public static final String IMG_OK_STATUS = "/icons/status/tick_16.png";
	public static final String IMG_ERR_STATUS = "/icons/status/cross_16.png";
	public static final String IMG_CANCEL_STATUS = "/icons/status/delete_16.png";
	public static final String IMG_WARNING_STATUS = "/icons/status/alert_16.png";
	
	//Resource images for status line are pre-created to avoid memory leaks
	private static Image infoStatusImage = getImageDescriptor(IMG_INFO_STATUS).createImage();
	private static Image newStatusImage =  getImageDescriptor(IMG_NEW_STATUS).createImage();
	private static Image okStatusImage = getImageDescriptor(IMG_OK_STATUS).createImage();
	private static Image errStatusImage =  getImageDescriptor(IMG_ERR_STATUS).createImage();
	private static Image cancelStatusImage =  getImageDescriptor(IMG_CANCEL_STATUS).createImage();
	private static Image warningStatusImage =  getImageDescriptor(IMG_WARNING_STATUS).createImage();
	
	/**
	 * This class should be never instantiate
	 */
	private KImage() {
	}

	/**
	 * Return eclipse shared image eg. ISharedImages.IMG_OBJ_FOLDER
	 * 
	 * @param image Shared image id
	 * @return Shared image or NULL if not found
	 */
	public static Image getSharedImage(String image) {
		return PlatformUI.getWorkbench().getSharedImages().getImage(image);
	}

	/**
	 * Return image descriptor from project's resources
	 * 
	 * @param id Image path to icons directory
	 * @return Returns image descriptor or NULL if not found
	 */
	public static ImageDescriptor getImageDescriptor(String id) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				id);
	}

	/**
	 * Return image descriptor from bundle's entry
	 * 
	 * @param id Image path to icons directory
	 * @return Returns image descriptor or NULL if not found
	 */
	public static ImageDescriptor createImageDescriptorFor(String id) {
		URL url = Platform.getBundle(Activator.PLUGIN_ID).getEntry(id);
		return ImageDescriptor.createFromURL(url);
	}

	/**
	 * Draw image on the window surface
	 * 
	 * @param parent Window composite 
	 * @param image Image to draw
	 * @param x X position of image
	 * @param y Y position of image
	 * @return Returns canvas on which image will be drawn
	 */
	public static Canvas drawImage(Composite parent, final Image image,
			final int x, final int y) {
		assert(parent != null && image != null);
		Canvas canvas = new Canvas(parent, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				GC gc = e.gc;
				gc.drawImage(image, x, y);
				gc.dispose();
			}
		});
		return canvas;
	}

	//TODO: Create method to remove image from window surface
	
	/**
	 * Resize image without disposing input image. It may cause memory leaks<br>
	 * 
	 * @param image Image to resize
	 * @param width New image width
	 * @param height New image height
	 * @return Returns resized image
	 */
	public static Image resize(Image image, int width, int height) {
		return resize(image, width, height, false);
	}

	/**
	 * Resize image
	 * 
	 * @param image Image to resize
	 * @param width New image width
	 * @param height New image height
	 * @param dispose If true disposes input image
	 * @return Returns resized image
	 */
	public static Image resize(Image image, int width, int height,
			boolean dispose) {
		assert(width > 0 && height > 0);
		assert(image != null);
		
		Image scaled = new Image(Display.getDefault(), width, height);
		GC gc = new GC(scaled);
		gc.setAntialias(SWT.ON);
		gc.setInterpolation(SWT.HIGH);
		gc.drawImage(image, 0, 0, image.getBounds().width,
				image.getBounds().height, 0, 0, width, height);
		gc.dispose();
		if (dispose)
			image.dispose();
		return scaled;
	}

	/**
	 * Return resource image. Do not dispose this image!
	 * 
	 * @param img Image path
	 * @return Returns static image or NULL if image path is incorrect
	 */
	public static Image getImage(String img) {
		if (img.equals(IMG_OK_STATUS))
			return okStatusImage;
		else if (img.equals(IMG_ERR_STATUS))
			return errStatusImage;
		else if (img.equals(IMG_CANCEL_STATUS))
			return cancelStatusImage;
		else if (img.equals(IMG_INFO_STATUS))
			return infoStatusImage;
		else if (img.equals(IMG_NEW_STATUS))
			return newStatusImage;
		else if (img.equals(IMG_WARNING_STATUS))
			return warningStatusImage;
		else
			return null;
	}
}
