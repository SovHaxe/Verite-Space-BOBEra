package com.evemap;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class StarMapGeneratorFrame extends JFrame {

	private DataManager data;
	
	public StarMapGeneratorFrame(DataManager datam){	
		this.data = datam;
		/*this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2-this.getWidth()/2, 
				(Toolkit.getDefaultToolkit().getScreenSize().height)/2-this.getHeight()/2);*/
		this.setLocation(200,100);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				data.writeToFile(false);
				System.exit(0);
			}
		});
		this.add(data.getImagePanel());
		this.setSize(MapConstants.HORIZONTAL_SIZE/2, MapConstants.VERTICAL_SIZE/2);
		this.setVisible(true);
	}
}
