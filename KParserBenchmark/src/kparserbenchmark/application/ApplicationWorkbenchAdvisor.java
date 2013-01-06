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

package kparserbenchmark.application;

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
