/*
 * Created on 03.12.2007
 * Caleido AG, All Rights Reserved
 * Author: Luzius Meisser
 */
package quickbase.internal.mixed;

import quickbase.serializer.ISerializer;

public class MixedKeySerializer implements ISerializer<MixedKey> {

    public MixedKey fromBytes(byte[] data, int offset) {
        assert offset == 0;
        return new MixedKey(data);
    }

    public byte[] toBytes(MixedKey key) {
        return key.getBytes();
    }

}
