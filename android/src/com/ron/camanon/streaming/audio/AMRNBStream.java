package com.ron.camanon.streaming.audio;

import java.io.IOException;

import com.ron.camanon.rtp.AMRNBPacketizer;
import com.ron.camanon.streaming.MediaStream;
import android.annotation.SuppressLint;
import android.media.MediaRecorder;

/**
 * This will stream AMRNB from the mic over RTP
 * Just call setDestination(), prepare() & start()
 */
@SuppressLint("NewApi")
public class AMRNBStream extends MediaStream {

	public AMRNBStream() throws IOException {
		super();
		
		this.packetizer = new AMRNBPacketizer();
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void prepare() throws IllegalStateException, IOException {
		setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		setAudioChannels(1);
		super.prepare();
	}
	
	public String generateSessionDescriptor() {
		return "m=audio "+String.valueOf(getDestinationPort())+" RTP/AVP 96\r\n" +
				   "b=AS:128\r\n" +
				   "b=RR:0\r\n" +
				   "a=rtpmap:96 AMR/8000\r\n" +
				   "a=fmtp:96 octet-align=1;\r\n";
	}
	
}
