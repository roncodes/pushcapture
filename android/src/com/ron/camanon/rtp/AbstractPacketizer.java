package com.ron.camanon.rtp;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

/**
 * 
 * Each packetizer inherits from this one and therefore uses RTP and UDP
 *
 */
abstract public class AbstractPacketizer {
	
	protected static final int rtphl = RtpSocket.RTP_HEADER_LENGTH;
	
	protected RtpSocket socket = null;
	protected InputStream is = null;
	protected boolean running = false;
	protected byte[] buffer;
	
	public AbstractPacketizer() throws IOException {
		socket = new RtpSocket();
		buffer = socket.getBuffer();
	}	
	
	public AbstractPacketizer(InputStream is) {
		super();
		this.is = is;
	}
	
	public RtpSocket getRtpSocket() {
		return socket;
	}
	
	public void setInputStream(InputStream is) {
		this.is = is;
	}
	
	public void setTimeToLive(int ttl) throws IOException {
		socket.setTimeToLive(ttl);
	}
	
	public void setDestination(InetAddress dest, int dport) {
		socket.setDestination(dest, dport);
	}
	
	public abstract void start() throws IOException;
	
	public abstract void stop();
	
    // Useful for debug
    protected static String printBuffer(byte[] buffer, int start,int end) {
    	String str = "";
    	for (int i=start;i<end;i++) str+=","+Integer.toHexString(buffer[i]&0xFF);
    	return str;
    }
	
}
