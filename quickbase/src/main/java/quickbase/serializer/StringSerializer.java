package quickbase.serializer;


public class StringSerializer implements ISerializer<String> {

	public String fromBytes(byte[] data, int offset) {
		return new String(data, offset, data.length - offset);
	}

	public byte[] toBytes(String key) {
		return key.getBytes();
	}

}
