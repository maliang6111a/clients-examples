package com.socket.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.socket.example.Client.HandlerCallBack;

public class Reader implements Runnable {

	private Socket socket = null;

	private HandlerCallBack callBack;

	private InputStream is = null;

	public Reader(InputStream is, HandlerCallBack callBack) throws IOException {
		this.callBack = callBack;
		this.is = is;
	}

	@Override
	public void run() {
		try {
			byte[] buff = new byte[4 * 1024];
			while (is != null && is.read(buff) > -1) {
				Message msg = new Message();
				if (msg.unpack(buff)) {
					callBack.handler(msg);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
