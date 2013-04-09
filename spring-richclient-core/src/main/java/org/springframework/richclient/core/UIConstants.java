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
package org.springframework.richclient.core;

/**
 * Constants for swing user interface classes.
 * 
 * @author Keith Donald
 */
public class UIConstants {
    private UIConstants() {
    }

    public static final String ELLIPSIS = "...";

    /**
     * The Java look and Feel standard for one screen space between GUI
     * Components.
     */
    public static final int ONE_SPACE = 5;

    /**
     * The Java look and Feel standard for two screen spaces between GUI
     * Components.
     */
    public static final int TWO_SPACES = 11;

    /**
     * The Java look and Feel standard for three screen spaces between GUI
     * Components.
     */
    public static final int THREE_SPACES = 17;

    /**
     * The Java look and Feel standard for border spacing.
     */
    public static final int STANDARD_BORDER = TWO_SPACES;

    /**
     * Symbolic name for absence of keystroke mask.
     */
    public static final int NO_KEYSTROKE_MASK = 0;

    /**
     * Suggested width for a <code>JTextField</code>
     */
    public static final int SIMPLE_FIELD_WIDTH = 20;

    /**
     * Suggested width for a <code>JTextField</code> storing a file path.
     */
    public static final int FILE_PATH_FIELD_WIDTH = 30;

    /**
     * Maximum length for some <code>JLabel</code>s, beyond which the text
     * will be truncated.
     */
    public static final int MAX_LABEL_LENGTH = 35;

}