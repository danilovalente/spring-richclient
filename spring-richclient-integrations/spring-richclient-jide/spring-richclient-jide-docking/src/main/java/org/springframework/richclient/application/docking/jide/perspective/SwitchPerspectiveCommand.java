/*
 * Copyright 2005 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.application.docking.jide.perspective;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.docking.jide.JideApplicationWindow;
import org.springframework.richclient.command.support.ApplicationWindowAwareCommand;


/**
 * Command to change perspectives to the one passed as a parameter
 * 
 * @author Jonny Wray
 *
 */
public class SwitchPerspectiveCommand extends ApplicationWindowAwareCommand{

	private Perspective perspective;
	
	public void setPerspective(Perspective perspective){
		this.perspective = perspective;
	}
	
    protected void doExecuteCommand() {
    	JideApplicationWindow window =
    		(JideApplicationWindow)Application.instance().getActiveWindow();
    	perspective.switchPerspective(window);
    }
}
