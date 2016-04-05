package com.socket.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

	// ��Ϣ����ص�����
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

			// ������֤��Ϣ
			sendMessage(Message.createAuthMessage("123", "ok"));

			is = socket.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	// ��������
	// �´����Ӽ��ʱ�� s
	// �ܼƿ��������Ĵ���
	// TODO
	public Client reConnectionSet(int nextConn, int maxConn) {
		this.nextConn = nextConn;
		this.maxConn = maxConn;
		return this;
	}

	// �����̶߳�ȡ��Ϣ
	public Client startRead(final HandlerCallBack back) {

		try {
			new Thread(new Reader(is, back)).start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return this;
	}

	// ��Ϣ����
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
