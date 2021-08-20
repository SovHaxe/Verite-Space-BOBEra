package com.evemap;

import java.awt.Color;

public class MapConstants {

	public static final Color STAR_COLOR = new Color(0xB0, 0xB0, 0xFF);
	
//	Sample rate for text placement algorithm, samples every sampleRate pixels
	public static final int SAMPLE_RATE = 8; 

//	width
	public static final int HORIZONTAL_SIZE = 928*2;
	
//	height
	public static final int VERTICAL_SIZE = 1024*2;
	
//	vertical offset
	public static final int HORIZONTAL_OFFSET = 208;
	
//	horizontal offset
	public static final int VERTICAL_OFFSET = 0;
	
//	number of threads to use
	public static final int THREADPOOL_SIZE = Runtime.getRuntime().availableProcessors();
	
//	Scaling factor
	public static final double SCALE = 4.8445284569785E17/((VERTICAL_SIZE - 20) / 2.0);
	
}
