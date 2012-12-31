package com.ron.camanon.rtp;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import android.os.SystemClock;
import android.util.Log;

/**
 *   RFC 3984
 *   
 *   H264 streaming over RTP
 *   
 *   Must be fed with an InputStream containing h.264
 *   NAL units must be preceded by their length (4 bytes)
 *   Stream must start with mpeg4 or 3gpp header, it will be skipped
 *   
 */
@SuppressWarnings("unused")
public class H264Packetizer extends AbstractPacketizer implements Runnable{
	
	public final static String TAG = "H264Packetizer";
	
	private final static int MAXPACKETSIZE = 1400;
	
	private Thread t = null;
	private int naluLength = 0;
	private long ts = 0, delay = 0;
	private Statistics stats = new Statistics();

	public H264Packetizer() throws IOException {
		super();
	}
	
	public void start() throws IOException {
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
	
	public void stop() {
		try {
			is.close();
		} catch (IOException ignore) {}
		t.interrupt();
		// We wait until the packetizer thread returns
		try {
			t.join();
		} catch (InterruptedException e) {}
		t = null;
	}
	
	public void run() {
		
		long duration = 0, oldtime = 0;
		
		// This will skip the MPEG4 header if this step fails we can't stream anything :(
		try {
			byte buffer[] = new byte[4];
			// Skip all atoms preceding mdat atom
			while (true) {
				while (is.read() != 'm');
				is.read(buffer,0,3);
				if (buffer[0] == 'd' && buffer[1] == 'a' && buffer[2] == 't') break;
			}
		} catch (IOException e) {
			Log.e(TAG,"Couldn't skip mp4 header :/");
			return;
		}
		
		// Here we read a NAL unit in the input stream and we send it
		try {
			while (!Thread.interrupted()) {
				
				// We measure how long it takes to receive the NAL unit from the phone
				oldtime = SystemClock.elapsedRealtime();
				send();
				duration = SystemClock.elapsedRealtime() - oldtime;

				// Calculates the average duration of a NAL unit
				stats.push(duration);
				delay = stats.average();
				
			}
		} catch (IOException e) {
		} catch (InterruptedException e) {}
		
		Log.d(TAG,"H264 packetizer stopped !");
		
	}
	
	// Reads a NAL unit in the FIFO and sends it
	// If it is too big, we split it in FU-A units (RFC 3984)
	private void send() throws IOException, InterruptedException {
		int sum = 1, len = 0, type;

		// Read NAL unit length (4 bytes)
		fill(rtphl,4);
		naluLength = buffer[rtphl+3]&0xFF | (buffer[rtphl+2]&0xFF)<<8 | (buffer[rtphl+1]&0xFF)<<16 | (buffer[rtphl]&0xFF)<<24;

		// Read NAL unit header (1 byte)
		fill(rtphl, 1);
		// NAL unit type
		type = buffer[rtphl]&0x1F;
		
		ts += delay;
		socket.updateTimestamp(ts*90);

		//Log.d(TAG,"- Nal unit length: " + naluLength + " delay: "+delay+" type: "+type);

		// Small NAL unit => Single NAL unit 
		if (naluLength<=MAXPACKETSIZE-rtphl-2) {
			len = fill(rtphl+1,  naluLength-1  );
			socket.markNextPacket();
			socket.send(naluLength+rtphl);
			//Log.d(TAG,"----- Single NAL unit - len:"+len+" header:"+printBuffer(rtphl,rtphl+3)+" delay: "+delay+" newDelay: "+newDelay);
		}
		// Large NAL unit => Split nal unit 
		else {

			// Set FU-A header
			buffer[rtphl+1] = (byte) (buffer[rtphl] & 0x1F);  // FU header type
			buffer[rtphl+1] += 0x80; // Start bit
			// Set FU-A indicator
			buffer[rtphl] = (byte) ((buffer[rtphl] & 0x60) & 0xFF); // FU indicator NRI
			buffer[rtphl] += 28;

			while (sum < naluLength) {
				if ((len = fill(rtphl+2,  naluLength-sum > MAXPACKETSIZE-rtphl-2 ? MAXPACKETSIZE-rtphl-2 : naluLength-sum  ))<0) return; sum += len;
				// Last packet before next NAL
				if (sum >= naluLength) {
					// End bit on
					buffer[rtphl+1] += 0x40;
					socket.markNextPacket();
				}
				socket.send(len+rtphl+2);
				// Switch start bit
				buffer[rtphl+1] = (byte) (buffer[rtphl+1] & 0x7F); 
				//Log.d(TAG,"----- FU-A unit, sum:"+sum);
			}
		}
	}

	private int fill(int offset,int length) throws IOException {
		
		int sum = 0, len;
		
		while (sum<length) {
			len = is.read(buffer, offset+sum, length-sum);
			if (len<0) {
				throw new IOException("End of stream");
			}
			else sum+=len;
		}
		
		return sum;
			
	}
	
	private static class Statistics {
		
		public final static int COUNT=50;
		private float m = 0, q = 0;
		
		public void push(long duration) {
			m = (m*q+duration)/(q+1);
			if (q<COUNT) q++;
		}

		public long average() {
			return (long)m;
		}

	}
	
}