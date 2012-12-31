package com.ron.camanon.streaming.video;

import java.io.IOException;

import com.ron.camanon.rtp.H263Packetizer;
import com.ron.camanon.rtp.H264Packetizer;
import com.ron.camanon.streaming.MediaStream;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

@SuppressWarnings("unused")
public class H263Stream extends VideoStream {
	
	@SuppressLint("NewApi")
	public H263Stream(int cameraId) throws IOException {
		super(cameraId);
		setVideoEncoder(MediaRecorder.VideoEncoder.H263);
		this.packetizer = new H263Packetizer();
	}
	
	public String generateSessionDescriptor() throws IllegalStateException,
			IOException {

		return "m=video "+String.valueOf(getDestinationPort())+" RTP/AVP 96\r\n" +
				   "b=RR:0\r\n" +
				   "a=rtpmap:96 H263-1998/90000\r\n";
		
	}

}
