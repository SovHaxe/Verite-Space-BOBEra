package com.evemap.objects;

import java.awt.*;

public class TitleVectorItem extends Point{
	private int pixelCount;
	private String allianceName;
	private Color nameColor;
	
	public TitleVectorItem(String allianceName, Color nameColor){
		this.allianceName = allianceName;
		this.nameColor = nameColor;
	}

	public int getPixelCount() {
		return pixelCount;
	}

	public String getAllianceName() {
		return allianceName;
	}

	public Color getNameColor() {
		return nameColor;
	}

	public void setPixelCount(int pixelCount) {
		this.pixelCount = pixelCount;
	}
}
