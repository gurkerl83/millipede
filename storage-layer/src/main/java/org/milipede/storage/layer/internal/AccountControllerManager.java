/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.milipede.storage.layer.internal;

import org.milipede.storage.layer.AccountControllerManagerService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.milipede.storage.layer.Controllable;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author gurkerl
 */
public class AccountControllerManager implements AccountControllerManagerService {

    Map<String, Controllable> controllers = new HashMap<String, Controllable>();

    void activate() {
        for (Controllable controller : controllers.values()) {
            if (controller.isStable()) {
                controller.doControl();
            } else {
                System.out.println(controller + "is not stable");
            }
        }
    }
    
    public void bind(ServiceReference reference) {
        System.out.println("ReferenceListener, service bound: " + reference);
            controllers.put((String) reference.getProperty("provider"), (Controllable) (reference));
    }

    public void unbind(ServiceReference reference) {
        System.out.println("ReferenceListener, service unbound: " + reference);
        controllers.remove((String) reference.getProperty("provider"));
    }

    public String toString() {
        return "ReferenceListener";
    }
}
