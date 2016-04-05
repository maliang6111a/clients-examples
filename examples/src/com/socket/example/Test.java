package com.socket.example;

import java.io.IOException;

import com.socket.example.Client.HandlerCallBack;

public class Test {

	public static void main(String[] args) throws IOException {

		IMMessage imsg = new IMMessage();
		imsg.sender = 123;
		imsg.receiver = 123;
		imsg.timestamp = 123;
		imsg.msgLocalID = 123;
		imsg.content = "≤‚ ‘≤‚ ‘≤‚ ‘";

		final Message msg = new Message();
		msg.version = Version.BUFVERSION;
		msg.body = imsg;

		final Client client = Client.Builder().connection("127.0.0.1", 9997).startRead(new HandlerCallBack() {
			@Override
			public void handler(Message msg) {
				if (msg.version == Version.BUFVERSION && msg != null && msg.body != null) {
					IMMessage imsg = (IMMessage) msg.body;
					System.out.println(msg.version + "  " + imsg.content);
				}
			}
		});
		/*
		 * new Timer().schedule(new TimerTask() {
		 * 
		 * @Override public void run() { client.sendMessage(msg); } }, 1000, 2000);
		 */

	}
}
