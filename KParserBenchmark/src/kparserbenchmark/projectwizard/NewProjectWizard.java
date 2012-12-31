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

package kparserbenchmark.projectwizard;

import kparserbenchmark.projectexplorer.Project;

import org.eclipse.jface.wizard.Wizard;

/**
 * Creates new project wizard
 * 
 * @author kopson
 */
public class NewProjectWizard extends Wizard {

	// Wizard pages
	protected NewProjectPage one;

	// Created project
	private Project proj;

	/**
	 * The constructor
	 */
	public NewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		one = new NewProjectPage();
		addPage(one);
	}

	@Override
	public boolean performFinish() {
		proj = new Project(one.getProjectName(), one.getProjectType(),
				one.getProjectPath(), one.getProjectSummary(),
				one.getProjectDescription());
		return true;
	}

	public Project getProj() {
		return proj;
	}
}