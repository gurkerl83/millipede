/*
 * Created on 01.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.consumer;

import quickbase.exception.AbortVisitException;
import quickbase.exception.BasicFileOperationDatabaseException;
import quickbase.exception.IExceptionHandlingStrategy;
import quickbase.exception.SerializationDatabaseException;
import quickbase.internal.entries.AddEntry;
import quickbase.internal.entries.HashFunction;
import quickbase.internal.files.DataFiles;
import quickbase.internal.index.Index;
import quickbase.serializer.ISerializer;
import quickbase.service.IVisitor;

public class VisitConsumer<K, V> extends AbstractConsumer<K, V> {

    private ISerializer<K> keySerializer;
    private ISerializer<V> valueSerializer;
    private IVisitor<K, V> visitor;

    public VisitConsumer(HashFunction<K> hash, IExceptionHandlingStrategy strategy, ISerializer<K> keySerializer, ISerializer<V> valueSerializer, IVisitor<K, V> visitor, Index index, DataFiles data) {
        super(hash, strategy, index, data);
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
        this.visitor = visitor;
    }

    protected void visit(AddEntry entry) throws SerializationDatabaseException, AbortVisitException {
        visitor.visit(keySerializer.fromBytes(entry.getKey(), 0), valueSerializer.fromBytes(entry.getValue(), 0));
    }

    public void flush() throws BasicFileOperationDatabaseException {
    }

}
