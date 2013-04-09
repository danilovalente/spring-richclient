/*
 * Copyright 2002-2007 the original author or authors.
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
package org.springframework.richclient.application.statusbar.support;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.factory.AbstractControlFactory;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.progress.ProgressMonitor;
import org.springframework.util.StringUtils;

/**
 * <code>ProgressMonitor</code> implementation that handles its own controls:
 * <ul>
 * <li>a <code>JProgressBar</code> to show the progress to the user</li>
 * <li>optionally a <code>JButton</code> to allow the user to cancel the
 * current task</li>
 * </ul>
 * <p>
 * Initally the progress bar and button are hidden, and shown when a task is
 * running longer than the <code>delayProgress</code> property (default is 500
 * ms).
 *
 * @author Peter De Bruycker
 */
public class StatusBarProgressMonitor extends AbstractControlFactory implements
		ProgressMonitor {

	/** Progress bar creation is delayed by this ms */
	public static final int DEFAULT_DELAY_PROGRESS = 500;

	public static final int UNKNOWN = -1;

	private JButton cancelButton;

	private boolean cancelEnabled = true;

	private Icon cancelIcon;

	private JPanel control;

	private boolean isCanceled;

	private JProgressBar progressBar;

	private long startTime;

	private String taskName;

	private int delayProgress = DEFAULT_DELAY_PROGRESS;

	protected JButton createCancelButton() {
		JButton cancelButton = new JButton();
		cancelButton.setBorderPainted(false);
		cancelButton.setIcon(getCancelIcon());
		cancelButton.setMargin(new Insets(0, 0, 0, 0));

		return cancelButton;
	}

	protected JComponent createControl() {
		control = new JPanel(new BorderLayout());

		cancelButton = createCancelButton();
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("Requesting task cancellation...");
				setCanceled(true);
			}
		});
		progressBar = createProgressBar();

		control.add(progressBar);
		control.add(cancelButton, BorderLayout.LINE_END);

        Border bevelBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED, UIManager
                .getColor("controlHighlight"), UIManager.getColor("controlShadow"));
        Border emptyBorder = BorderFactory.createEmptyBorder(1, 3, 1, 3);
        control.setBorder(BorderFactory.createCompoundBorder(bevelBorder, emptyBorder));

		// initially hide the control
		hideProgress();

		return control;
	}

	protected JProgressBar createProgressBar() {
		JProgressBar progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(200, 17));
		progressBar.setStringPainted(true);

		return progressBar;
	}

	public void done() {
		startTime = 0;
		if (progressBar != null) {
			progressBar.setValue(progressBar.getMaximum());
			progressBar.setString("");
		}
		hideProgress();
	}

	public Icon getCancelIcon() {
		if (cancelIcon == null) {
			cancelIcon = ((IconSource) ApplicationServicesLocator.services()
					.getService(IconSource.class)).getIcon("cancel.icon");
		}

		return cancelIcon;
	}

	protected JProgressBar getProgressBar() {
		return progressBar;
	}

	private void hideButton() {
		cancelButton.setEnabled(cancelEnabled);
		cancelButton.setVisible(false);
	}

	protected void hideProgress() {
		if (progressBar.isVisible()) {
			progressBar.setVisible(false);
			cancelButton.setVisible(false);
		}
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public void setCanceled(boolean b) {
		isCanceled = b;
		cancelButton.setEnabled(!b);
	}

	public void setCancelEnabled(boolean enabled) {
		cancelEnabled = enabled;
		if (progressBar.isVisible() && !cancelButton.isVisible() && enabled) {
			showButton();
		} else {
			hideButton();
		}
	}

	public void setCancelIcon(Icon icon) {
		cancelIcon = icon;
		if (cancelButton != null) {
			cancelButton.setIcon(icon);
		}
	}

	private void showButton() {
		cancelButton.setEnabled(cancelEnabled);
		cancelButton.setVisible(true);
	}

	private void showProgress() {
		if (!progressBar.isVisible()) {
			if (cancelEnabled) {
				showButton();
			}
			progressBar.setVisible(true);
		}
	}

	public void subTaskStarted(String name) {
		String text;
		if (name.length() == 0) {
			text = name;
		} else {
			if (StringUtils.hasText(taskName)) {
				text = taskName + " - " + name;
			} else {
				text = name;
			}
		}
		progressBar.setString(text);
	}

	public void taskStarted(String name, int totalWork) {
		startTime = System.currentTimeMillis();
		isCanceled = false;
		if (totalWork == UNKNOWN) {
			progressBar.setIndeterminate(true);
		} else {
			progressBar.setIndeterminate(false);
			progressBar.setMaximum(totalWork);
			progressBar.setValue(0);
		}
		taskName = name;
		progressBar.setString(taskName);
		showProgress();
	}

	public void worked(int work) {
		if (!progressBar.isVisible()) {
			if ((System.currentTimeMillis() - startTime) > delayProgress) {
				control.setVisible(true);
			}
		}
		progressBar.setValue((int) work);

		if (progressBar.isStringPainted()) {
			progressBar.setString(((int) work) + "%");
		}
	}

	public void setDelayProgress(int delayProgress) {
		this.delayProgress = delayProgress;
	}
}
