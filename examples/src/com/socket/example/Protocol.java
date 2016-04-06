package com.socket.example;

import java.io.Serializable;

public interface Protocol extends Serializable {
	public byte[] pack();
}
