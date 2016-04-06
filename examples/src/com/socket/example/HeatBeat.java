package com.socket.example;

import java.util.TimerTask;

public class HeatBeat extends TimerTask {

	private Client client = null;

	public HeatBeat(Client client) {
		this.client = client;
	}

	@Override
	public void run() {
		if (client == null || client.getOs() == null) {
			return;
		}
		System.out.println("��������.....");
		client.sendMessage(Message.createHeatBeatMessage());
	}
}
