package org.openjfx.infrastructure;

import java.io.IOException;
import java.util.logging.*;

public class Log {
    private final static Logger LOGGER = Logger.getLogger("firmadorlogger");
    private final static String fileLogger = "./firmador.log";

    public Log() {
      init();
    }

    public void info(String message) {
        LOGGER.log(Level.INFO, message);
    }


    public void warning(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    public void error(String message) {
        LOGGER.log(Level.SEVERE, message);
    }

    private void init () {
        Handler consoleHandler = new ConsoleHandler();
        Handler fileHandler = null;
        try {
            fileHandler = new FileHandler(fileLogger, true);


            //present data
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);

            //get all messages
            LOGGER.addHandler(consoleHandler);
            LOGGER.addHandler(fileHandler);

            //all messages
            consoleHandler.setLevel(Level.ALL);
            fileHandler.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
