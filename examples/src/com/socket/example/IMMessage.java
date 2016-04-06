package com.socket.example;

import java.util.Arrays;

public class IMMessage implements Protocol {
	/**
	 * @date 2016年4月6日 下午12:09:14
	 */
	private static final long serialVersionUID = 6760064746333057698L;

	public long sender; // 8
	public long receiver; // 8
	public int timestamp; // 4
	public int msgLocalID; // 4
	public String content;

	@Override
	public byte[] pack() {
		int pos = 0;
		byte[] buf = new byte[64 * 1024];

		BytePacket.writeInt64(getSender(), buf, pos);

		pos += 8;
		BytePacket.writeInt64(getReceiver(), buf, pos);

		pos += 8;
		BytePacket.writeInt32(getTimestamp(), buf, pos);

		pos += 4;
		BytePacket.writeInt32(getMsgLocalID(), buf, pos);

		pos += 4;
		try {
			byte[] c = getContent().getBytes("UTF-8");

			if (c.length + 24 >= 32 * 1024) {
				return null;
			}
			System.arraycopy(c, 0, buf, pos, c.length);
			return Arrays.copyOf(buf, 24 + c.length);
		} catch (Exception e) {
			return null;
		}
	}

	public long getSender() {
		return sender;
	}

	public void setSender(long sender) {
		this.sender = sender;
	}

	public long getReceiver() {
		return receiver;
	}

	public void setReceiver(long receiver) {
		this.receiver = receiver;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getMsgLocalID() {
		return msgLocalID;
	}

	public void setMsgLocalID(int msgLocalID) {
		this.msgLocalID = msgLocalID;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
