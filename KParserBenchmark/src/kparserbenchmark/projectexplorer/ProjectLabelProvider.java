package kparserbenchmark.projectexplorer;

import kparserbenchmark.projectexplorer.Project.Status;
import kparserbenchmark.utils.KImage;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

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

		if (element instanceof Project) {
			Project project = (Project) element;
			text.append(project.getName());
			if (project.getCurrStatus() == Status.OPENED)
				cell.setImage(KImage.getImageDescriptor(
						KImage.IMG_PROJECT_OPENED).createImage());
			else if (project.getCurrStatus() == Status.CLOSED)
				cell.setImage(KImage.getImageDescriptor(
						KImage.IMG_PROJECT_CLOSED).createImage());
			text.append(" (" + project.getElements().size() + ") ",
					StyledString.COUNTER_STYLER);
			@SuppressWarnings("unused")
			Project p = Workspace.getCurrProject();
			if (project.equals(Workspace.getCurrProject()))
				cell.setFont(JFaceResources.getFontRegistry().getBold(
						JFaceResources.DEFAULT_FONT));
			else {
				cell.setFont(JFaceResources.getFontRegistry().get(
						JFaceResources.DEFAULT_FONT));
			}
		} else {
			Category category = (Category) element;
			text.append(category.getName());
			cell.setImage(KImage.getImageDescriptor(KImage.IMG_PROJECT_FILE)
					.createImage());
		}
		cell.setText(text.toString());
		cell.setStyleRanges(text.getStyleRanges());
		super.update(cell);
	}
}