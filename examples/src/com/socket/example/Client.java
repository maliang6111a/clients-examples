package com.socket.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Timer;

public class Client {

	// 消息处理回调函数
	public interface HandlerCallBack {
		public void handler(Message msg);
	}

	private String HOST = "127.0.0.1";

	private String authId = "default";

	private String authPwd = "default";

	private int PORT = 9997;

	private int nextConn = 10; // 10s

	private int maxConn = 5;// 5

	private int timeOut = 10 * 1000; // 默认超时时间40s

	private int heatBeat = 20 * 1000;// 默认心跳时间30s

	private Socket socket = null;

	private OutputStream os = null;

	private InputStream is = null;

	private Timer heatBeatTimer = null;

	private Client(String authId, String authPwd) {
		this.authId = authId;
		this.authPwd = authPwd;
	}

	/**
	 * 传递认证用户跟密码
	 * 
	 * @param authId
	 * @param authPwd
	 * @return
	 * @date 2016年4月6日 下午12:40:12
	 * @author maliang
	 */
	public static Client Builder(String authId, String authPwd) {
		return new Client(authId, authPwd);
	}

	/**
	 * 设置心跳间隔时间
	 * 
	 * @param hb
	 *            秒
	 * @return
	 * @date 2016年4月6日 下午12:16:43
	 * @author maliang
	 */
	public Client setHeatBeat(int hb) {
		this.heatBeat = hb * 1000;
		return this;
	}

	/**
	 * 设置超时时间
	 * 
	 * @param to
	 *            秒
	 * @return
	 * @date 2016年4月6日 下午12:16:57
	 * @author maliang
	 */
	public Client setTimeOut(int to) {
		this.timeOut = to * 1000;
		return this;
	}

	public Client connection(String addr, int port) {
		if (addr != null && !addr.equals("") && port > 0) {
			this.HOST = addr;
			this.PORT = port;
		}
		try {
			socket = new Socket(HOST, PORT);
			socket.setKeepAlive(true);
			// 超时时间默认
			socket.setSoTimeout(timeOut * 1000);
			os = socket.getOutputStream();

			// 发送认证信息
			sendMessage(Message.createAuthMessage(this.authId, this.authPwd));

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

	public Client startHeatBeat() {
		heatBeatTimer = new Timer();
		heatBeatTimer.schedule(new HeatBeat(this), this.timeOut, this.timeOut);
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

			if (msg.version == Version.HEATBEAT) {
				os.write(bs);
				os.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getHOST() {
		return HOST;
	}

	public int getPORT() {
		return PORT;
	}

	public int getNextConn() {
		return nextConn;
	}

	public int getMaxConn() {
		return maxConn;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public int getHeatBeat() {
		return heatBeat;
	}

	public Socket getSocket() {
		return socket;
	}

	public OutputStream getOs() {
		return os;
	}

	public InputStream getIs() {
		return is;
	}

}
