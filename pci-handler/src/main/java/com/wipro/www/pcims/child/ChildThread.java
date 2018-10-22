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

import com.wipro.www.pcims.model.FapServiceList;
import com.wipro.www.pcims.restclient.AsyncResponseBody;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;

public class ChildThread implements Runnable {

    static BlockingQueue<FapServiceList> queue = new LinkedBlockingQueue<>();
    static BlockingQueue<AsyncResponseBody> asynchronousResponse = new LinkedBlockingQueue<>();
    private ClusterFormation clusterFormation;
    FapServiceList fapServiceList = new FapServiceList();
    static long childThreadId;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ChildThread.class);

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
     * Puts response in queue.
     */
    public void putResponse(AsyncResponseBody obj) {
        synchronized (ChildThread.asynchronousResponse) {
            try {
                asynchronousResponse.put(obj);
                notify();
            } catch (InterruptedException e) {
                log.error("The Thread is Interrupted", e);
                Thread.currentThread().interrupt();

            }
        }

    }

    @Override
    public void run() {
        childThreadId = Thread.currentThread().getId();

        try {
            fapServiceList = queue.take();
        } catch (InterruptedException e1) {
            log.error("InterruptedException is {}", e1);
            Thread.currentThread().interrupt();

        }

        Graph cluster = clusterFormation.clusterForm(fapServiceList);

        try {
            String networkId = fapServiceList.getCellConfig().getLte().getRan().getNeighborListInUse()
                    .getLteNeighborListInUseLteCell().get(0).getPlmnid();
            clusterFormation.triggerOrWait(cluster, networkId);
        } catch (Exception e) {
            log.error("{}", e);
        }

    }

}
