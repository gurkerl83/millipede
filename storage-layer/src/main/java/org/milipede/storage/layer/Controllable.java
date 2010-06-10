/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.milipede.storage.layer;

/**
 *
 * @author gurkerl
 */
public interface Controllable extends AccountControllerService {

    void doControl();
    boolean isStable();

}
