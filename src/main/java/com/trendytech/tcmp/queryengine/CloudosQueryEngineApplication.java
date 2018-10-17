package com.trendytech.tcmp.queryengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.polaris.base.web.exceptionhandler.EnableDefaultExceptionHandler;
import com.polaris.base.web.requestlog.EnableRequestLog;

@SpringBootApplication
@EnableScheduling
@EnableRequestLog
@EnableDefaultExceptionHandler
@ServletComponentScan("com.polaris.base.web")
public class CloudosQueryEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudosQueryEngineApplication.class, args);
    }
}
