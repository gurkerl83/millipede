/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.cyberduck.service;

import ch.cyberduck.core.Transfer;

/**
 *
 * @author gurkerl
 */
public interface TransferControllerService {

    /**
     * Add this item to the list; select it and scroll the view to make it visible
     *
     * @param transfer
     */
    void addTransfer(final Transfer transfer);

    boolean isSingleton();

    /**
     * Remove this item form the list
     *
     * @param transfer
     */
    void removeTransfer(final Transfer transfer);

    void setFilterField(String filterField);

    void setLocalField(String localField);

    void setUrlField(String urlField);

    /**
     * @param transfer
     */
    void startTransfer(final Transfer transfer);

}
