package com.id.cash.module.starwin.common;

/**
 * Created by linchen on 2018/4/10.
 */

public class StarWinFactory {
    private static ILogger logger = null;

    public static ILogger getLogger() {
        if (logger == null) {
            // Logger needs to be "static" to retain the configuration throughout the app
            logger = new Logger();
        }
        return logger;
    }
}
