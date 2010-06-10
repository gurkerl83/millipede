/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.milipede.mldht;

import com.aelitis.azureus.core.AzureusCore;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import lbms.plugins.mldht.azureus.MlDHTPlugin;
import lbms.plugins.mldht.kad.AnnounceTask;
import lbms.plugins.mldht.kad.DHT.DHTtype;
import lbms.plugins.mldht.kad.DHTStatsListener;
import lbms.plugins.mldht.kad.KBucketEntry;
import lbms.plugins.mldht.kad.KBucketEntryAndToken;
import lbms.plugins.mldht.kad.Key;
import lbms.plugins.mldht.kad.NodeLookup;
import lbms.plugins.mldht.kad.Task;
import lbms.plugins.mldht.kad.TaskListener;
import lbms.plugins.mldht.kad.TaskManager;
import lbms.plugins.mldht.kad.messages.GetPeersRequest;
import org.gudy.azureus2.core3.global.GlobalManager;
import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTManager;
import org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author gurkerl
 */
public class Main implements BundleActivator{ //implements Runnable{

    private MlDHTPlugin plugin = MlDHTPlugin.getInstance();;
    private boolean isCreated = false;
    private boolean isActivated = false;
    private boolean isRunning = false;
    private DHTStatsListener dhtStatsListener = null;
    private final DHTtype type = null;
    private SortedSet<Key> set;

    public void run() {
    // TODO code application logic here
        int a = 2;

        plugin.initialize();
        plugin.initializationComplete();

        HelperClass helper = new HelperClass();


//        if (plugin.getDHT(type).isRunning()) {
        if (a==1) {
					plugin.stopDHT();
					helper.deactivate();
				} else {
					plugin.startDHT();
					helper.activate();
				}
				helper.updateDHTRunStatus();


    }

    public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        int a = 2;

        MlDHTPlugin plugin = MlDHTPlugin.getInstance();
        plugin.initialize();
        plugin.initializationComplete();

        HelperClass helper = new HelperClass();

        String s = "2E443D0DF062EDE0BF88B4B9F7A88C6CF7F107DA";
//        if (plugin.getDHT(type).isRunning()) {
        if (a==1) {
					plugin.stopDHT();
					helper.deactivate();
				} else {
					plugin.startDHT();
					helper.activate();

//                                        			helper.updateDHTRunStatus();

                                AnnounceTask t = plugin.getDHT(DHTtype.IPV4_DHT).announce(SimpleSHA1.SHA1(s), 49001);
                                TaskManager tm = plugin.getDHT(DHTtype.IPV4_DHT).getTaskManager();

                                Task[] activeTasks = tm.getActiveTasks();
                                Task[] queuedTasks = tm.getQueuedTasks();
//                                System.out.println("active tasks: " + activeTasks.length);
//                                System.out.println("queued tasks: " + queuedTasks.length);
                                for (Task task : activeTasks) {
//                                    System.out.println("Task id: " + task.getTaskID());
//                                    System.out.println("Task info: " + task.getInfo());
                                }
                                for (Task task : queuedTasks) {
//                                    System.out.println(task.getTaskID());
                                }

                                t.addListener(new TaskListener() {

            @Override
            public void finished(Task t) {
//                System.out.println("Task " + t + " finished");
            }

                                });

				}


    }

    @Override
    public void start(BundleContext context) throws Exception {

        plugin = MlDHTPlugin.getInstance();
        plugin.initialize();
        plugin.initializationComplete();

        ServiceReference mainlineRef = context.getServiceReference(MainlineDHTManager.class.getName());
        MainlineDHTManager mainline = (MainlineDHTManager) context.getService(mainlineRef);

        System.out.println("MainlineDHTManager: " + mainline);
        
        mainline.setProvider(plugin.mlDHTProvider);


        ServiceReference coreRef = context.getServiceReference(AzureusCore.class.getName());
        AzureusCore core = (AzureusCore) context.getService(coreRef);
        System.out.println("Core: " + core);

//        ServiceReference globalRef = context.getServiceReference(GlobalManager.class.getName());
//        GlobalManager globalMgr = (GlobalManager) context.getService(globalRef);

        core.getGlobalManager().setMainlineDHTProvider((org.gudy.azureus2.plugins.dht.mainline.MainlineDHTProvider)plugin.mlDHTProvider);



        int a = 2;

        
//        HelperClass helper = new HelperClass();
//
//        String s = "2E443D0DF062EDE0BF88B4B9F7A88C6CF7F107DA";
//
////        if (plugin.getDHT(type).isRunning()) {
//        if (a==1) {
//					plugin.stopDHT();
//					helper.deactivate();
//				} else {
//					plugin.startDHT();
//					helper.activate();
//
//
//				}
//
////                                        			helper.updateDHTRunStatus();
//
//                                AnnounceTask t = plugin.getDHT(DHTtype.IPV4_DHT).announce(SimpleSHA1.SHA1(s), 6881);
//                                TaskManager tm = plugin.getDHT(DHTtype.IPV4_DHT).getTaskManager();
//
//                                Task[] activeTasks = tm.getActiveTasks();
//                                Task[] queuedTasks = tm.getQueuedTasks();
//                                System.out.println("active tasks: " + activeTasks.length);
//                                System.out.println("queued tasks: " + queuedTasks.length);
//                                for (Task task : activeTasks) {
//                                    System.out.println("Task id: " + task.getTaskID());
//                                    System.out.println("Task info: " + task.getInfo());
//                                }
//                                for (Task task : queuedTasks) {
//                                    System.out.println(task.getTaskID());
//                                }
//
//                                t.addListener(new TaskListener() {
//
//            @Override
//            public void finished(Task t) {
//                System.out.println("Task " + t + " finished");
////                try {
//                    String s = "2E443D0DF062EDE0BF88B4B9F7A88C6CF7F107DA";
//
////                    final NodeLookup nlu = plugin.getDHT(DHTtype.IPV4_DHT).findNode(new Key(SimpleSHA1.SHA1(s)));
////                    nlu.addListener(new TaskListener() {
////
////                        @Override
////                        public void finished(Task t) {
////
////                            System.out.println("Nodelookup complete");
////                            set = nlu.closestSet;
////                            System.out.println("Nodelookup set toArray: " + set.toArray().toString());
////                            System.out.println("Nodelookup size: " + set.size());
////
////                            byte[] token = new byte[10];
////                            token[1] = 22;
////                            t.addToTodo(new KBucketEntryAndToken(new KBucketEntry(), token));
////
////                            GetPeersRequest req = new GetPeersRequest(plugin.getDHT(DHTtype.IPV4_DHT).getOurID(), t.getTargetKey());
////                            plugin.getDHT(DHTtype.IPV4_DHT).getPeers(req);
////
////                        }
////                   });
////                } catch (NoSuchAlgorithmException ex) {
////                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
////                } catch (UnsupportedEncodingException ex) {
////                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
////                }
//
//
//            }
//
//
//
//                                });


    }

    @Override
    public void stop(BundleContext arg0) throws Exception {
        MlDHTPlugin.getInstance().closedownInitiated();
    }


    
}
