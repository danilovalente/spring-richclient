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
package org.springframework.richclient.application.docking.jide;

import com.jidesoft.docking.DockingManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.docking.jide.perspective.Perspective;

import java.text.MessageFormat;

/**
 * A simple manager of JIDE layouts that has the ability to save and restore
 * layouts based on page ids and a perspective id, allowing multiple saved
 * layouts per page.
 * 
 * @author Jonny Wray
 *
 */
public class LayoutManager {
    private static final Log logger = LogFactory.getLog(LayoutManager.class);

	private static final String PAGE_LAYOUT = "page_{0}_layout_{1}";
	
	public static boolean isValidLayout(DockingManager manager, String pageId, Perspective perspective){

		String pageLayout = MessageFormat.format(PAGE_LAYOUT, new Object[]{pageId, perspective.getId()});
		return manager.isLayoutAvailable(pageLayout) && 
			manager.isLayoutDataVersionValid(pageLayout);
	}
	
	/**
	 * Loads a the previously saved layout for the current page. If no 
	 * previously persisted layout exists for the given page the built 
	 * in default layout is used.
	 * 
	 * @param manager The docking manager to use
	 * @param pageId The page to get the layout for
	 * @return a boolean saying if the layout requested was previously saved
	 */
	public static boolean loadPageLayoutData(DockingManager manager, String pageId, Perspective perspective){
		manager.beginLoadLayoutData();
		try{
			if(isValidLayout(manager, pageId, perspective)){
				String pageLayout = MessageFormat.format(PAGE_LAYOUT, new Object[]{pageId, perspective.getId()});
				manager.loadLayoutDataFrom(pageLayout);
				logger.debug("Used exisiting layout");
				return true;
			}
			else{
				logger.debug("Using default layout");
				manager.loadLayoutData();
				return false;
			}
		}
		catch(Exception e){
			logger.debug("Using default layout");
			manager.loadLayoutData();
			return false;
		}
	}
	
	/**
	 * Saves the current page layout.
	 * 
	 * @param manager The current docking manager
	 * @param pageId The page to saved the layout for
	 */
	public static void savePageLayoutData(DockingManager manager, String pageId, String perspectiveId){
		logger.debug("Saving layout for page "+pageId);
		manager.saveLayoutDataAs(MessageFormat.format(PAGE_LAYOUT, 
				new Object[]{pageId, perspectiveId}));
	}
}
