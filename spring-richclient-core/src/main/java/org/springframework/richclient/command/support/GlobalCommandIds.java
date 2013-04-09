/*
 * Copyright 2002-2004 the original author or authors.
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
package org.springframework.richclient.command.support;

/**
 * Static constants for well-known, global Commands.
 * 
 * @author Keith Donald
 */
public class GlobalCommandIds {
    public static final String CUT = "cutCommand";

    public static final String COPY = "copyCommand";

    public static final String PASTE = "pasteCommand";

    public static final String UNDO = "undoCommand";

    public static final String REDO = "redoCommand";

    public static final String SAVE = "saveCommand";

    public static final String SAVE_AS = "saveAsCommand";

    public static final String SELECT_ALL = "selectAllCommand";

    public static final String DELETE = "deleteCommand";

    public static final String PROPERTIES = "propertiesCommand";

    public static final String RUN = "runCommand";

    private GlobalCommandIds() {
    }
}