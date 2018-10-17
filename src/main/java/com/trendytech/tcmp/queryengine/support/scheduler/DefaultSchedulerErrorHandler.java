package com.trendytech.tcmp.queryengine.support.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class DefaultSchedulerErrorHandler implements ErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSchedulerErrorHandler.class);

    @Override
    public void handleError(Throwable t) {
        LOGGER.error(t.getMessage(), t);
    }

}
