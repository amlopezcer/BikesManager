package com.amlopezc.bikesmanager.util;

/**
 * To monitor async tasks.
 * When a async task finishes, the process will be processed.
 */
public interface AsyncTaskListener<T> {
    //Method to be implemented by the activities which communicates whit the server to process its responses
    void processServerResult(T result, int operation);
}
