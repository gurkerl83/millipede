/*
 * Created on 03.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.mixed;

import quickbase.exception.AbortVisitException;
import quickbase.exception.SerializationDatabaseException;
import quickbase.serializer.ISerializer;
import quickbase.service.IVisitor;


public class MixedVisitor<K, V> implements IVisitor<MixedKey, MixedValue> {
    
    private byte id;
    private IVisitor<K, V> visitor;
    private ISerializer<K> keySerializer;
    private ISerializer<V> valueSerializer;
    
    public MixedVisitor(byte id, ISerializer<K> keySerializer, ISerializer<V> valueSerializer, IVisitor<K, V> visitor) {
        this.id = id;
        this.visitor = visitor;
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    public void visit(MixedKey key, MixedValue value) throws SerializationDatabaseException, AbortVisitException {
        if (key.getId() == id){
            visitor.visit(key.getWrapped(keySerializer), value.getWrapped(valueSerializer));
        }
    }

}
