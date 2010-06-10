/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.consumer;

import quickbase.internal.entries.Entry;
import quickbase.internal.entries.HashFunction;
import quickbase.internal.index.Index;


public class RecoveryConsumer<K, V> implements IEntryConsumer {

    private HashFunction<K> hash;
    private Index index;
    
    public RecoveryConsumer(HashFunction<K> hash, Index index) {
        this.hash = hash;
        this.index = index;
    }

    public boolean consume(Entry entry, long pos) {
        this.index.put(hash.getHash(entry), (int)pos);
        entry.doStats(index.getStats());
        return true;
    }

    public void flush() {
    }

    public boolean needsValues() {
        return false;
    }

}
