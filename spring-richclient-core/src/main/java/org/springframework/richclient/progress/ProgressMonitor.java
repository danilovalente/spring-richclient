/*
 * Copyright 2002-2006 the original author or authors.
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
package org.springframework.richclient.progress;

/**
 * A interface for monitoring task progress.
 * 
 * @author Keith Donald
 */
public interface ProgressMonitor {

    /**
     * Notifies that the main task is beginning.
     * 
     * @param name
     *            the name (or description) of the main task
     * @param totalWork
     *            the total number of work units into which the main task is
     *            been subdivided. If the value is 0 or UNKNOWN the
     *            implemenation is free to indicate progress in a way which
     *            doesn't require the total number of work units in advance. In
     *            general users should use the UNKNOWN value if they don't know
     *            the total amount of work units.
     */
    public void taskStarted(String name, int totalWork);

    /**
     * Notifies that a subtask of the main task is beginning. Subtasks are
     * optional; the main task might not have subtasks.
     * 
     * @param name
     *            the name (or description) of the subtask
     */
    public void subTaskStarted(String name);

    /**
     * Notifies that a percentage of the work has been completed. This is called
     * by clients when the work is performed and is used to update the progress
     * monitor.
     * 
     * @param work
     *            the percentage complete (0..100)
     */
    public void worked(int work);

    /**
     * Notifies that the work is done; that is, either the main task is
     * completed or the user cancelled it.
     * 
     * done() can be called more than once; an implementation should be prepared
     * to handle this case.
     */
    public void done();

    /**
     * Returns true if the user does some UI action to cancel this operation.
     * (like hitting the Cancel button on the progress dialog). The long running
     * operation typically polls isCanceled().
     */
    public boolean isCanceled();

    /**
     * Attempts to cancel the monitored task.
     */
    public void setCanceled(boolean b);

}