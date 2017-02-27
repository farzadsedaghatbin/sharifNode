package com.isc.npsd.sharif.node;

import com.isc.npsd.sharif.node.services.FileProcessor;

/**
 * Created by A_Ahmady on 2/26/2017.
 */
public class ServiceRunner {

    public static Object run(Object... params) {
        Long id = (Long) params[0];
        FileProcessor fileProcessor = AppConfig.getContext().getBean(FileProcessor.class);
        fileProcessor.process(id);
        return null;
    }
}
