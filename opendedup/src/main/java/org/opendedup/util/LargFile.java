package org.opendedup.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

public class LargFile {
	public static void writeFile(String path, int size,int bs) throws IOException {
		long len = 1024L * 1024L * 1024L * size;
		long sz = 0;
		File log = new File(path+".log");
		File f = new File(path);
		java.io.FileWriter writer = new FileWriter(log);
		FileOutputStream str = new FileOutputStream(f, true);
		Random rnd = new Random();
		byte[] b = new byte[bs];
		System.out.println("1:" + len);
		long time = System.currentTimeMillis();
		int writes = 0;
		int interval = (32768*10000)/bs;
		while (sz < len) {
			rnd.nextBytes(b);
			ByteBuffer buf = ByteBuffer.wrap(b);
			str.getChannel().write(buf);
			sz = sz + b.length;
			if (writes > interval) {
				float mb = (float) (writes * bs) / (1024 * 1024);
				float duration = (float) (System.currentTimeMillis() - time) / 1000;
				float mbps = mb / duration;
				System.out.println(mbps + " (mb/s)");
				writer.write(Float.toString(mbps) + "\n");
				time = System.currentTimeMillis();
				writes = 0;

			} else {
				writes++;
			}
		}
		writer.flush();
		writer.close();
	}
	

	public static void main(String[] args) throws IOException {
			writeFile("/media/dedup/test.bin", 10,1048576);
	}

}
