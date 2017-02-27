package com.isc.npsd.sharif.node.services;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by A_Ahmady on 02/28/2016.
 */

@Component
public class LogService {

    Logger logger = Logger.getLogger(LogService.class.getName());

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
