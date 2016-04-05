package com.socket.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

	// 消息处理回调函数
	public interface HandlerCallBack {
		public void handler(Message msg);
	}

	private String HOST = "127.0.0.1";

	private int PORT = 9997;

	private int nextConn = 10; // 10s

	private int maxConn = 5;// 5

	private Socket socket = null;

	private OutputStream os = null;

	private InputStream is = null;

	private Client() {

	}

	public static Client Builder() {
		return new Client();
	}

	public Client connection(String addr, int port) {
		if (addr != null && !addr.equals("") && port > 0) {
			this.HOST = addr;
			this.PORT = port;
		}
		try {
			socket = new Socket(HOST, PORT);
			socket.setKeepAlive(true);
			socket.setSoTimeout(40000);
			os = socket.getOutputStream();

			// 发送认证信息
			sendMessage(Message.createAuthMessage("123", "ok"));

			is = socket.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	// 重连机制
	// 下次连接间隔时间 s
	// 总计可以重连的次数
	// TODO
	public Client reConnectionSet(int nextConn, int maxConn) {
		this.nextConn = nextConn;
		this.maxConn = maxConn;
		return this;
	}

	// 启动线程读取信息
	public Client startRead(final HandlerCallBack back) {

		try {
			new Thread(new Reader(is, back)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return this;
	}

	// 信息发送
	public void sendMessage(Message msg) {
		if (socket == null || os == null) {
			return;
		}
		if (msg == null) {
			return;
		}
		try {
			byte[] bs = msg.pack();

			if (msg.version == Version.BUFAUTH && bs.length > 9) {
				os.write(bs);
				os.flush();
			}
			if (msg.version == Version.BUFVERSION && bs.length > 29) {
				os.write(bs);
				os.flush();
			}
			socket.setSoTimeout(40000);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
