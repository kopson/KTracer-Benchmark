package kparserbenchmark.projectexplorer;

import kparserbenchmark.projectexplorer.ProjectItem.ItemTypes;
import kparserbenchmark.projectexplorer.ProjectNode.Status;
import kparserbenchmark.utils.KImage;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.ui.ISharedImages;

/**
 * Project explorer label provider
 * 
 * @author kopson
 */
public class ProjectLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {

		Object element = cell.getElement();
		StyledString text = new StyledString();

		if (element instanceof ProjectNode) {
			ProjectNode project = (ProjectNode) element;
			text.append(project.getName());
			if (project.getCurrStatus() == Status.OPENED)
				cell.setImage(KImage.getImageDescriptor(
						KImage.IMG_PROJECT_OPENED).createImage());
			else if (project.getCurrStatus() == Status.CLOSED)
				cell.setImage(KImage.getImageDescriptor(
						KImage.IMG_PROJECT_CLOSED).createImage());
			text.append(" (" + project.getChildrenList().size() + ") ",
					StyledString.COUNTER_STYLER);
			@SuppressWarnings("unused")
			ProjectNode p = Workspace.getCurrProject();
			if (project.equals(Workspace.getCurrProject()))
				cell.setFont(JFaceResources.getFontRegistry().getBold(
						JFaceResources.DEFAULT_FONT));
			else {
				cell.setFont(JFaceResources.getFontRegistry().get(
						JFaceResources.DEFAULT_FONT));
			}
		} else {
			ProjectLeaf category = (ProjectLeaf) element;
			text.append(category.getName());

			switch (category.getType()) {
			case CONFIG_FILE:
				cell.setImage(KImage
						.getImageDescriptor(KImage.IMG_PROJECT_FILE)
						.createImage());
				cell.setFont(JFaceResources.getFontRegistry().getItalic(
						JFaceResources.DEFAULT_FONT));
				break;
			case RAW_FILE:
				cell.setImage(KImage
						.getImageDescriptor(KImage.IMG_PROJECT_FILE)
						.createImage());
				cell.setFont(JFaceResources.getFontRegistry().get(
						JFaceResources.DEFAULT_FONT));
				break;
			case FOLDER:
				cell.setImage(KImage
						.getImageDescriptor(KImage.IMG_PROJECT_FOLDER)
						.createImage());
				cell.setFont(JFaceResources.getFontRegistry().get(
						JFaceResources.DEFAULT_FONT));
				break;
			default:
				cell.setImage(KImage.getSharedImage(ISharedImages.IMG_OBJ_FILE));
				cell.setFont(JFaceResources.getFontRegistry().get(
						JFaceResources.DEFAULT_FONT));
				break;
			}
		}
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
		super.update(cell);
	}
}