package org.springframework.richclient.exceptionhandling;

/**
 * Uses 1.5 API.
 * @author Geoffrey De Smet
 * @since 0.3
 */
public abstract class AbstractRegisterableExceptionHandler implements Thread.UncaughtExceptionHandler,
        RegisterableExceptionHandler {

    /**
     * Registers the exception handler for all threads and the event thread specifically.
     */
    public void registerExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        AwtExceptionHandlerAdapterHack.registerExceptionHandler(this);
    }

}
