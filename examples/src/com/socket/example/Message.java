package com.socket.example;

import java.util.Arrays;

class Version {
	public static final int BUFVERSION = 1;
	public static final int BUFAUTH = 3;
}

public class Message {

	public static final int HEAD_SIZE = 5;

	public byte version; // 1

	public int msg_len; // 4

	public Object body;

	public byte[] pack() {

		if (body == null) {
			return null;
		}

		int pos = 0;
		byte[] buf = new byte[64 * 1024];
		buf[pos] = version;
		pos++;

		if (version == Version.BUFVERSION) {
			IMMessage im = (IMMessage) body;
			byte[] bs = im.pack();
			BytePacket.writeInt32(bs.length, buf, pos);
			pos += 4;
			System.arraycopy(bs, 0, buf, pos, bs.length);
			return Arrays.copyOf(buf, HEAD_SIZE + bs.length);
		}
		return null;
	}

	public boolean unpack(byte[] data) {

		if (data.length < 29) {
			return false;
		}

		int pos = 0;
		this.version = data[pos];
		pos++;
		this.msg_len = BytePacket.readInt32(data, pos);

		pos += 4;
		if (version == Version.BUFVERSION) {
			IMMessage im = new IMMessage();
			im.setSender(BytePacket.readInt64(data, pos));

			pos += 8;
			im.setReceiver(BytePacket.readInt64(data, pos));

			pos += 8;
			im.setTimestamp(BytePacket.readInt32(data, pos));

			pos += 4;
			im.setMsgLocalID(BytePacket.readInt32(data, pos));

			pos += 4;
			try {
				im.setContent(new String(data, pos, data.length - 29, "UTF-8"));
				this.body = im;
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;

	}
}
