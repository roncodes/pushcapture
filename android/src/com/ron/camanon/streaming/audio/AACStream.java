package com.ron.camanon.streaming.audio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.ron.camanon.rtp.AACADTSPacketizer;
import com.ron.camanon.streaming.MediaStream;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

/**
 * This will stream AMRNB from the mic over RTP
 * Just call setDestination(), prepare() & start()
 */
public class AACStream extends MediaStream {

	public final static String TAG = "AACStream";
	
	/** MPEG-4 Audio Object Types supported by ADTS **/
	private String[] AudioObjectTypes = {
			"NULL",							  // 0
			"AAC Main",						  // 1
			"AAC LC (Low Complexity)",		  // 2
			"AAC SSR (Scalable Sample Rate)", // 3
			"AAC LTP (Long Term Prediction)"  // 4	
	};
	
	/** There are 13 supported frequencies by ADTS **/
	private int[] ADTSSamplingRates = {
			96000, // 0
			88200, // 1
			64000, // 2
			48000, // 3
			44100, // 4
			32000, // 5
			24000, // 6
			22050, // 7
			16000, // 8
			12000, // 9
			11025, // 10
			8000,  // 11
			7350,  // 12
			-1,   // 13
			-1,   // 14
			-1,   // 15
	};
	
	/** Default sampling rate **/
	private int requestedSamplingRate = 16000;
	
	private int profile, samplingRateIndex, channel, config;
	
	public AACStream() throws IOException {
		super();
		
		AACADTSPacketizer packetizer = new AACADTSPacketizer();
		this.packetizer = packetizer;
		
	}
	
	public void setAudioSamplingRate(int samplingRate) {
		requestedSamplingRate = samplingRate;
	}
	
	public void prepare() throws IllegalStateException, IOException {
		
		setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		
		// This is completely experimental: AAC_ADTS is not yet visible in the android developer documentation
		// Recording AAC ADTS works on my galaxy SII with this tiny trick
		super.setOutputFormat(6);
		super.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
		super.setAudioChannels(1);
		super.setAudioSamplingRate(requestedSamplingRate);

		super.prepare();
	}

	public String generateSessionDescriptor() throws IllegalStateException, IOException {
		testADTS();
		
		// All the MIME types parameters used here are described in RFC 3640
		// SizeLength: 13 bits will be enough because ADTS uses 13 bits for frame length
		// config: contains the object type + the sampling rate + the channel number
		
		// TODO: streamType always 5 ? profile-level-id always 15 ?
		
		return "m=audio "+String.valueOf(getDestinationPort())+" RTP/AVP 96\r\n" +
			   "b=RR:0\r\n" +
			   "a=rtpmap:96 mpeg4-generic/"+ADTSSamplingRates[samplingRateIndex]+"\r\n" +
			   "a=fmtp:96 streamtype=5; profile-level-id=15; mode=AAC-hbr; config="+Integer.toHexString(config)+"; SizeLength=13; IndexLength=3; IndexDeltaLength=3;\r\n";
	}
	
	/** 
	 * Records a short sample of AAC ADTS from the microphone to find out what the sampling rate really is
	 * On some phone indeed, no error will be reported if the sampling rate used differs from the 
	 * one selected with setAudioSamplingRate 
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	private void testADTS() throws IllegalStateException, IOException {

		final String TESTFILE = Environment.getExternalStorageDirectory().getPath()+"/spydroid-test.adts";
		
		// The structure of an ADTS packet is described here: http://wiki.multimedia.cx/index.php?title=ADTS
		
		// ADTS header is 7 or 9 bytes long
		byte[] buffer = new byte[9];
		
		// That means the H264Stream will behave as a regular MediaRecorder object
		// it will not start the packetizer thread and can be used to save video in a file
		setMode(MODE_DEFAULT);
		setOutputFile(TESTFILE);
		
		prepare();
		start();
		
		// We record for 1 sec
		// TODO: use the MediaRecorder.OnInfoListener
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		stop();
		setMode(MODE_STREAMING);
		
		File file = new File(TESTFILE);
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		
		// ADTS packets start with a sync word: 12bits set to 1
		while (true) {
			if ( (raf.readByte()&0xFF) == 0xFF ) {
				buffer[0] = raf.readByte();
				if ( (buffer[0]&0xF0) == 0xF0) break;
			}
		}
		
		raf.read(buffer,1,5);
		
		samplingRateIndex = (buffer[1]&0x3C) >> 2;
		profile = ( (buffer[1]&0xC0) >> 6 ) + 1 ;
		channel = (buffer[1]&0x01) << 2 | (buffer[2]&0xC0) >> 6 ;
		
		// 5 bits for the object type / 4 bits for the sampling rate / 4 bits for the channel / padding
		config = profile<<11 | samplingRateIndex<<7 | channel<<3;
		
		Log.i(TAG,"MPEG VERSION: " + ( (buffer[0]&0x08) >> 3 ) );
		Log.i(TAG,"PROTECTION: " + (buffer[0]&0x01) );
		Log.i(TAG,"PROFILE: " + AudioObjectTypes[ profile ] );
		Log.i(TAG,"SAMPLING FREQUENCY: " + ADTSSamplingRates[samplingRateIndex] );
		Log.i(TAG,"CHANNEL: " + channel );
		
		raf.close();
		
		if (!file.delete()) Log.e(TAG,"Temp file could not be erased");
		
	}
	
}
