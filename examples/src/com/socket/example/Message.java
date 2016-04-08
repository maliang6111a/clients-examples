package com.socket.example;

import java.util.Arrays;

class Version {
	public static final int BUFVERSION = 1;
	public static final int BUFAUTH = 3;
	public static final int HEATBEAT = 4;
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

		byte[] bs = null;

		if (version == Version.BUFVERSION || version == Version.HEATBEAT) {
			IMMessage im = (IMMessage) body;
			bs = im.pack();
		} else if (version == Version.BUFAUTH) {
			AuthMessage amsg = (AuthMessage) body;
			bs = amsg.pack();
		}

		if (bs == null) {
			return null;
		}

		BytePacket.writeInt32(bs.length, buf, pos);
		pos += 4;
		System.arraycopy(bs, 0, buf, pos, bs.length);
		return Arrays.copyOf(buf, HEAD_SIZE + bs.length);

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
				im.setContent(new String(data, pos, msg_len - 24, "UTF-8"));
				this.body = im;
				return true;
			} catch (Exception e) {
				return false;
			}
		}
		return false;

	}

	public static Message createAuthMessage(String authId, String authPwd) {
		AuthMessage message = new AuthMessage();
		message.setAuthId(authId);
		message.setAuthPwd(authPwd);
		Message msg = new Message();
		msg.version = Version.BUFAUTH;
		msg.body = message;
		return msg;

	}

	public static Message createHeatBeatMessage() {
		IMMessage imsg = new IMMessage();

		// 服务端默认 发送跟接收ID<=-1为心跳信息
		imsg.setSender(-1);
		imsg.setReceiver(-1);
		imsg.setContent("ping");
		Message msg = new Message();
		// 这个地方没啥用，现在这样，方便以后扩展心跳协议
		// Version.HEATBEAT
		// 目前协议服务端没有实现
		msg.version = Version.BUFVERSION;
		msg.body = imsg;
		return msg;
	}

}
