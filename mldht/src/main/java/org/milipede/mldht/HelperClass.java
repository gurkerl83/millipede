/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.milipede.mldht;

import lbms.plugins.mldht.azureus.MlDHTPlugin;
import lbms.plugins.mldht.kad.DHT.DHTtype;
import lbms.plugins.mldht.kad.DHTStatsListener;
import lbms.plugins.mldht.kad.KBucket;

/**
 *
 * @author gurkerl
 */
public class HelperClass {

    private boolean isCreated = false;
    private boolean isActivated = false;
    private boolean isRunning = false;
    private DHTStatsListener dhtStatsListener = null;
    private final DHTtype type = DHTtype.IPV4_DHT;

    MlDHTPlugin plugin = MlDHTPlugin.getInstance();

    public void activate() {
        
        isRunning = plugin.getDHT(type).isRunning();
        if (!isCreated || !isRunning || isActivated) {
            return;
        }
        plugin.getDHT(type).addStatsListener(dhtStatsListener);
        if (plugin.getDHT(type).isRunning()) {
            KBucket[] buck = plugin.getDHT(type).getNode().getBuckets();
//            System.out.println(buck.toString());
        }
        updateDHTRunStatus();
        isActivated = true;
    }

    public void deactivate() {
        if (!isActivated) {
            return;
        }
        plugin.getDHT(type).removeStatsListener(dhtStatsListener);
//		rtc.setBucketList(null);

        isActivated = false;
    }


    public void delete () {
		deactivate();
//		peerCount = null;
//		taskCount = null;
//		dhtRunStatus = null;
//		dhtStartStop = null;
//		rtc.dispose();
//		if (donationImg != null) {
//			donationImg.dispose();
//			donationImg = null;
//		}
		isCreated = false;
    }

    public void updateDHTRunStatus() {
        isRunning = plugin.getDHT(type).isRunning();
//		if (dhtRunStatus != null && !dhtRunStatus.isDisposed()) {
//			dhtRunStatus.setText((plugin.getDHT(type).isRunning()) ? "Running"
//					: "Stopped");
//		}

//		if (dhtStartStop != null && !dhtStartStop.isDisposed()) {
//			dhtStartStop.setText((plugin.getDHT(type).isRunning()) ? "Stop"
//					: "Start");
//		}

        System.out.println((plugin.getDHT(type).isRunning() ? plugin.getDHT(type).getOurID().toString()
                : "XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX XXXXXXXX"));
    }
}
