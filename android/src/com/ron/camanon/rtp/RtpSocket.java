package com.ron.camanon.rtp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Random;


@SuppressWarnings("unused")
public class RtpSocket {

	private MulticastSocket usock;
	private DatagramPacket upack;
	
	private byte[] buffer = new byte[MTU];
	private int seq = 0;
	private boolean upts = false;
	private int ssrc;
	private int port = -1;
	
	public static final int RTP_HEADER_LENGTH = 12;
	public static final int MTU = 1500;
	
	public RtpSocket(byte[] buffer, InetAddress dest, int dport) {
		upack.setPort(dport);
		upack.setAddress(dest);
	}
	
	public RtpSocket() throws IOException {
		
		/*							     Version(2)  Padding(0)					 					*/
		/*									 ^		  ^			Extension(0)						*/
		/*									 |		  |				^								*/
		/*									 | --------				|								*/
		/*									 | |---------------------								*/
		/*									 | ||  -----------------------> Source Identifier(0)	*/
		/*									 | ||  |												*/
		buffer[0] = (byte) Integer.parseInt("10000000",2);
		
		/* Payload Type */
		buffer[1] = (byte) 96;
		
		/* Byte 2,3        ->  Sequence Number                   */
		/* Byte 4,5,6,7    ->  Timestamp                         */
		/* Byte 8,9,10,11  ->  Sync Source Identifier            */
		setLong((ssrc=(new Random()).nextInt()),8,12);
		
		usock = new MulticastSocket();
		upack = new DatagramPacket(buffer, 1);

	}

	public void close() {
		usock.close();
	}
	
	public void setSSRC(int ssrc) {
		this.ssrc = ssrc; 
		setLong(ssrc,8,12);
	}
	
	public int getSSRC() {
		return ssrc;
	}
	
	public void setTimeToLive(int ttl) throws IOException {
		usock.setTimeToLive(ttl);
	}
	
	public void setDestination(InetAddress dest, int dport) {
		port = dport;
		upack.setPort(dport);
		upack.setAddress(dest);
	}
	
	public byte[] getBuffer() {
		return buffer;
	}
	
	public int getPort() {
		return port;
	}

	public int getLocalPort() {
		return usock.getLocalPort();
	}
	
	/* Send RTP packet over the network */
	public void send(int length) throws IOException {
		
		updateSequence();
		upack.setLength(length);
		usock.send(upack);
		
		if (upts) {
			upts = false;
			buffer[1] -= 0x80;
		}
		
	}
	
	private void updateSequence() {
		setLong(++seq, 2, 4);
	}
	
	public void updateTimestamp(long timestamp) {
		setLong(timestamp, 4, 8);
	}
	
	public void markNextPacket() {
		upts = true;
		buffer[1] += 0x80; // Mark next packet
	}
	
	private void setLong(long n, int begin, int end) {
		for (end--; end >= begin; end--) {
			buffer[end] = (byte) (n % 256);
			n >>= 8;
		}
	}	
	
}
