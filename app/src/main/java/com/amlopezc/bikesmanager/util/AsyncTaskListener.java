package com.amlopezc.bikesmanager.util;

/**
 * To monitor async tasks.
 * When a async task finishes, the process will be processed.
 */
public interface AsyncTaskListener<T> {
    void processServerResult(T result, int operation);
}
