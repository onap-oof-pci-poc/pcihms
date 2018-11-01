package com.wipro.www.pcims;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MainThreadComponent {

    private static Logger log = LoggerFactory.getLogger(MainThreadComponent.class);

    private ExecutorService pool;

    public ExecutorService getPool() {
        return pool;
    }

    /**
     * main thread initialization.
     */
    public void init(NewNotification newNotification) {
        log.debug("initializing main thread");
        log.debug("initializing executors");
        Configuration configuration = Configuration.getInstance();
        int maximumClusters = configuration.getMaximumClusters();
        log.debug("pool creating");
        pool = Executors.newFixedThreadPool(maximumClusters);
        log.debug("pool created");
        Thread thread = new Thread(new MainThread(newNotification));
        thread.start();
    }
}
