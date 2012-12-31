package com.ron.camanon.mp4;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Finds SPS & PPS parameters in mp4 file
 */
@SuppressWarnings("unused")
public class MP4Config {

	private final StsdBox stsdBox; 
	private final MP4Parser mp4Parser;
	
	/**
	 * Finds sps & pps parameters inside a .mp4
	 * @param path Path to the file to analyze
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public MP4Config (String path) throws IOException, FileNotFoundException {
		
		// We open the mp4 file
		mp4Parser = new MP4Parser(path);

		// We parse it
		try {
			mp4Parser.parse();
		} catch (IOException ignore) {
			// Maybe enough of the file has been parsed and we can get the stsd box
		}
		
		// We find the stsdBox
		stsdBox = mp4Parser.getStsdBox();
		
		// We're done !
		mp4Parser.close();
		
	}
	
	public String getProfileLevel() {
		return stsdBox.getProfileLevel();
	}
	
	public String getB64PPS() {
		return stsdBox.getB64PPS();
	}
	
	public String getB64SPS() {
		return stsdBox.getB64SPS();
	}
	
}