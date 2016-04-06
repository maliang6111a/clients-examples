package com.socket.example;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class AuthMessage implements Serializable {

	/**
	 * 
	 * @date 2016年4月6日 下午12:09:50
	 * @author maliang
	 */
	private static final long serialVersionUID = -9194308717326541891L;

	private String authId;
	private String authPwd;

	public byte[] pack() {
		int pos = 0;
		byte[] buf = new byte[64 * 1024];
		try {
			byte[] idbs = authId.getBytes("UTF-8");
			BytePacket.writeInt32(idbs.length, buf, pos);
			pos += 4;

			byte[] pwdbs = authPwd.getBytes("UTF-8");
			BytePacket.writeInt32(pwdbs.length, buf, pos);
			pos += 4;

			if (idbs.length + pwdbs.length > 3 * 1024) {
				return null;
			}
			// System.arraycopy(src, srcPos, dest, destPos, length);
			System.arraycopy(idbs, 0, buf, pos, idbs.length);
			pos += idbs.length;
			System.arraycopy(pwdbs, 0, buf, pos, pwdbs.length);
			byte[] tmp = Arrays.copyOf(buf, 8 + idbs.length + pwdbs.length);
			return tmp;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getAuthPwd() {
		return authPwd;
	}

	public void setAuthPwd(String authPwd) {
		this.authPwd = authPwd;
	}

}
