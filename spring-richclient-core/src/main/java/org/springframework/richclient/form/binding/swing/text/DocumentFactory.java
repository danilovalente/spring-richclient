package org.springframework.richclient.form.binding.swing.text;

import javax.swing.text.Document;

/**
 * Adds the functionality to create a new {@link Document} to be used in a {@link javax.swing.text.JTextComponent}.
 *
 * @author Lieven Doclo
 * @author Jan Hoskens
 */
public interface DocumentFactory {

    /**
     * Create a new {@link Document} to be used in a {@link javax.swing.text.JTextComponent}.
     *
     * @return an implementation of Document.
     */
    Document createDocument();
}
