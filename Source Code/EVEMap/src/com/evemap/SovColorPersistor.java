package com.evemap;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.JFileChooser;

public class SovColorPersistor {

	private Frame parent;
	private File dataFile = new File(System.getProperty("user.dir"));
	
	private volatile Color[][] toSave;
	
	public SovColorPersistor(Frame parent){
		this.parent = parent;
	}
	
	public void setSaveSize(int x, int y){
		toSave = new Color[x][y];
	}
	
	public synchronized void saveColoratPosition(int x, int y, Color c) throws NullPointerException{		
		toSave[x][y] = c;
	}
	
	public void saveColorInformation(){
		try {
			if(dataFile == null){
				dataFile = new File(System.getProperty("user.dir") + "/SovColorData.sov");
				dataFile.createNewFile();
			}
	        BufferedWriter out = new BufferedWriter(new FileWriter(dataFile));
	       
	        out.write(toSave.length + "\n");
	        out.write(toSave[0].length + "\n");
	        for(int x = 0; x < toSave.length; x++)
	        	for(int y = 0; y < toSave[0].length; y++)
	        		if(toSave[x][y] != null)
	        			out.write(x + " " + y + " " + toSave[x][y].getRGB() + "\n");	        
	        out.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	public Color[][] getOldSovColors() throws FileNotFoundException {
		Color [][] data;
		JFileChooser fc = new JFileChooser(dataFile);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.showOpenDialog(parent);
		dataFile = fc.getSelectedFile();
		if (dataFile == null)
			return null;
		 Scanner scanner = new Scanner(dataFile);
		    try {
		    	int x = 0, y = 0;
		    	try {
					if(scanner.hasNextLine())
						x = Integer.parseInt(scanner.nextLine());
					if(scanner.hasNextLine())
						y = Integer.parseInt(scanner.nextLine());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return null;
				}
		    	data = new Color[x][y];
		      while ( scanner.hasNextLine() ){
		        if(!processLine(scanner.nextLine(), data))
		        	return null;
		      }
		    }
		    finally {
		      scanner.close();
		    }

		return data;
	}
	
	private boolean processLine(String nextLine, Color[][] data) {
		StringTokenizer st = new StringTokenizer(nextLine);
		try {
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			Color c = new Color(Integer.parseInt(st.nextToken()));//, 16));
			data[x][y] = c;
//			data[x][y] = new Color(c.getRed(), c.getGreen(), c.getBlue(), Integer.parseInt(st.nextToken()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
