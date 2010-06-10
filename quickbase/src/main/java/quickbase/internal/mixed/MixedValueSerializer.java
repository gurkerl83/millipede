/*
 * Created on 03.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.mixed;

import quickbase.serializer.ISerializer;

public class MixedValueSerializer implements ISerializer<MixedValue> {

    public MixedValue fromBytes(byte[] data, int offset) {
        assert offset == 0;
        return new MixedValue(data);
    }

    public byte[] toBytes(MixedValue value) {
        return value.getBytes();
    }

}
