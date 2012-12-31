package com.ron.camanon.streaming.audio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.ron.camanon.streaming.Stream;
import android.annotation.SuppressLint;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.util.Log;

/**
 * Encapsulation of the new rtp package from API 12
 * No working :(
 */
@SuppressLint("NewApi")
public class GenericAudioStream implements Stream {

	public final static String TAG = "GenericAudioStream";
	
	private AudioStream audioStream;
	private AudioGroup audioGroup;
	private InetAddress destination;
	private int port;
	
	@SuppressLint("NewApi")
	public GenericAudioStream() {
		try {
			audioGroup = new AudioGroup();
			audioGroup.setMode(AudioGroup.MODE_NORMAL);
			audioStream = new AudioStream(InetAddress.getLocalHost());
			audioStream.setCodec(AudioCodec.AMR);
			audioStream.setMode(RtpStream.MODE_SEND_ONLY);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void prepare() {

	}

	public void start() throws IllegalStateException {
		//audioStream.join(null);
		audioStream.associate(destination, port);
		audioStream.join(audioGroup);
		
	}

	public void stop() {
		audioStream.join(null);
		audioGroup.setMode(AudioGroup.MODE_ON_HOLD);
	}

	public void release() {
		audioStream.release();
		audioGroup = null;
	}
	
	public String generateSessionDescriptor() throws IllegalStateException, IOException {
		AudioCodec codec = audioStream.getCodec();
		Log.d(TAG, "Codec: rtmap:\""+codec.rtpmap+"\""+" ftmp:\""+codec.fmtp+"\" type:\""+codec.type+"\"");
		return "m=audio "+String.valueOf(getDestinationPort())+" RTP/AVP "+codec.type+"\r\n" +
				   "a=rtpmap:"+codec.type+" "+codec.rtpmap+"\r\n" +
				   "a=fmtp:"+codec.type+" "+codec.fmtp+";\r\n";
	}

	public void setTimeToLive(int ttl) {
		// FIXME: is it possible to change the TTL here ?
	}
	
	public void setDestination(InetAddress dest, int dport) {
		this.destination = dest;
		this.port = dport;
	}

	public int getDestinationPort() {
		return port;
	}
	
	public int getLocalPort() {
		return audioStream.getLocalPort();
	}

	public int getSSRC() {
		return 0;
	}

	public boolean isStreaming() {
		return audioStream.isBusy();
	}

}
