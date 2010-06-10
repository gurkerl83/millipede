package quickbase.serializer;


public class ByteArraySerializer implements ISerializer<byte[]> {

	public byte[] fromBytes(byte[] data, int offset) {
	    if (offset > 0){
	        byte[] temp = new byte[data.length - offset];
	        System.arraycopy(data, offset, temp, 0, temp.length);
	        return temp;
	    } else {
	        return data;
	    }
	}

	public byte[] toBytes(byte[] value) {
		return value;
	}

}
