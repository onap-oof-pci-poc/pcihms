/*******************************************************************************
 * ============LICENSE_START=======================================================
 * pcims
 *  ================================================================================
 *  Copyright (C) 2018 Wipro Limited.
 *  ==============================================================================
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   ============LICENSE_END=========================================================
 ******************************************************************************/

package com.wipro.www.pcims.child;

import com.wipro.www.pcims.Configuration;
import com.wipro.www.pcims.dao.ClusterDetailsRepository;
import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.model.ThreadId;
import com.wipro.www.pcims.restclient.AsyncResponseBody;
import com.wipro.www.pcims.utils.BeanUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class ChildThread implements Runnable {

    private BlockingQueue<List<String>> childStatusUpdate;
    private BlockingQueue<FapServiceList> queue = new LinkedBlockingQueue<>();
    // static BlockingQueue<AsyncResponseBody> asynchronousResponse = new
    // LinkedBlockingQueue<>();
    private static Map<Long, AsyncResponseBody> responseMap = new HashMap<>();
    private Graph cluster;
    private ThreadId threadId;
    FapServiceList fapServiceList = new FapServiceList();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ChildThread.class);

    /**
     * Constructor with parameters.
     */
    public ChildThread(BlockingQueue<List<String>> childStatusUpdate, Graph cluster,
            BlockingQueue<FapServiceList> queue, ThreadId threadId) {
        super();
        this.childStatusUpdate = childStatusUpdate;
        this.queue = queue;
        this.threadId = threadId;
        this.cluster = cluster;
    }

    /**
     * Puts notification in queue.
     */
    public void putInQueue(FapServiceList fapserviceList) {
        try {
            queue.put(fapserviceList);
        } catch (InterruptedException e) {
            log.error(" The Thread is Interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Puts notification in queue with notify.
     */
    public void putInQueueWithNotify(FapServiceList fapserviceList) {
        synchronized (queue) {
            try {
                queue.put(fapserviceList);
                queue.notifyAll();
            } catch (InterruptedException e) {
                log.error(" The Thread is Interrupted", e);
                Thread.currentThread().interrupt();
            }

        }

    }

    /**
     * Puts response in queue.
     */
    public static void putResponse(Long threadId, AsyncResponseBody obj) {
        synchronized (responseMap) {
            responseMap.put(threadId, obj);
        }

    }

    public static Map<Long, AsyncResponseBody> getResponseMap() {
        return responseMap;
    }

    @Override
    public void run() {

        threadId.setChildThreadId(Thread.currentThread().getId());
        synchronized (threadId) {
            threadId.notifyAll();
        }

        MDC.put("logFileName", Thread.currentThread().getName());
        log.debug("Starting child thread");

        try {
            fapServiceList = queue.take();
            if (log.isDebugEnabled()) {
                log.debug("fapServicelist: {}", fapServiceList);
            }
        } catch (InterruptedException e1) {
            log.error("InterruptedException is {}", e1);
            Thread.currentThread().interrupt();
        }

        ClusterFormation clusterFormation = new ClusterFormation(queue);
        StateOof oof = new StateOof(childStatusUpdate);
        ClusterModification clusterModification = new ClusterModification();
        Detection detect = new Detection();

        try {
            String networkId = fapServiceList.getCellConfig().getLte().getRan().getNeighborListInUse()
                    .getLteNeighborListInUseLteCell().get(0).getPlmnid();

            Boolean done = false;

            while (!done) {

                Map<String, ArrayList<Integer>> collisionConfusionResult = detect.detectCollisionConfusion(cluster);
                Boolean trigger = clusterFormation.triggerOrWait(collisionConfusionResult);

                if (!trigger) {
                    collisionConfusionResult = clusterFormation.waitForNotification(collisionConfusionResult, cluster);
                }
                oof.triggerOof(collisionConfusionResult, networkId);

                if (isNotificationsBuffered()) {
                    List<FapServiceList> fapServiceLists = bufferNotification();
                    for (FapServiceList fapService : fapServiceLists) {
                        cluster = clusterModification.clustermod(cluster, fapService);
                    }
                    String cellPciNeighbourString = cluster.getPciNeighbourJson();
                    UUID clusterId = cluster.getGraphId();
                    ClusterDetailsRepository clusterDetailsRepository = BeanUtil
                            .getBean(ClusterDetailsRepository.class);
                    clusterDetailsRepository.updateCluster(cellPciNeighbourString, clusterId.toString());

                } else {
                    done = true;
                }

            }

        } catch (Exception e) {
            log.error("{}", e);
        }

        cleanup();
    }

    private boolean isNotificationsBuffered() {
        synchronized (queue) {

            try {
                while (queue.isEmpty()) {
                    queue.wait();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }

    /**
     * cleanup resources.
     */
    private void cleanup() {
        log.debug("cleaning up database and killing child thread");
        ClusterDetailsRepository clusterDetailsRepository = BeanUtil.getBean(ClusterDetailsRepository.class);
        clusterDetailsRepository.deleteByChildThreadId(threadId.getChildThreadId());
        log.debug("Child thread :{} {}", Thread.currentThread().getId(), "completed");
        MDC.remove("logFileName");

    }

    /**
     * Buffer Notification.
     */
    public List<FapServiceList> bufferNotification() {

        // Processing Buffered notifications

        List<FapServiceList> fapServiceLists = new ArrayList<>();

        Configuration config = Configuration.getInstance();

        int bufferTime = config.getBufferTime();

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        log.debug("Current time {}", currentTime);

        Timestamp laterTime = new Timestamp(System.currentTimeMillis());
        log.debug("Later time {}", laterTime);

        long difference = laterTime.getTime() - currentTime.getTime();
        while (difference < bufferTime) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("InterruptedException {}", e);
                Thread.currentThread().interrupt();

            }
            laterTime = new Timestamp(System.currentTimeMillis());
            difference = laterTime.getTime() - currentTime.getTime();

            log.debug("Timer has run for  seconds {}", difference);

            if (!queue.isEmpty()) {
                FapServiceList fapService;
                fapService = queue.poll();
                fapServiceLists.add(fapService);
            }
        }
        return fapServiceLists;
    }

}
