package org.openjfx.infrastructure;

import java.util.logging.Logger;
import java.util.logging.Level;

public class Log {
    private final static Logger LOGGER = Logger.getGlobal();

    public void infoLog(String message) {
        LOGGER.log(Level.INFO, "Process");

    }
}
