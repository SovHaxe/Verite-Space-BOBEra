package com.evemap.objects;

import java.awt.Color;
import java.util.List;

public class Alliance implements Comparable<Alliance>{
	 
	 private int id;
     private String name;
     private Color color = null, nameColor = null, starColor = null;
     private boolean npc = false;
     private long count = 0;
     long x = 0, y =0;

	/**
	 *
	 * @param name - Name of the Alliance
	 * @param id - Id of the Alliance
	 * @param nameColor - Color for the Alliance
	 * @param starColor - What color the individual
	 * @param npc - Is an NPC alliance
	 */
	public Alliance(String name, int id, Color nameColor, Color starColor, boolean npc){
    	 this(name, id);
    	 this.nameColor = nameColor;
    	 this.starColor = starColor;
    	 this.npc = npc;
     }

	/**
	 *
	 * @param name
	 * @param id
	 */
	public Alliance(String name, int id){
    	 this.name = name;
    	 this.id = id;
     }


     public void setColor(Color c, List<Color> colorTable){
    	 color = new Color(c.getRGB());
    	 float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    	 hsb[2] = 1;
    	 nameColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    	 hsb[1] = Math.max(0.2f, hsb[1]*.7f);
    	 starColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    	 colorTable.add(color);
     }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getNameColor() {
		return nameColor;
	}

	public Color getStarColor() {
		return starColor;
	}

	public void setNameColor(Color nameColor) {
		this.nameColor = nameColor;
	}

	public void setStarColor(Color starColor) {
		this.starColor = starColor;
	}

	public Color getColor() {
		return color;
	}

	public boolean isNpc() {
		return npc;
	}

	public void setNpc(boolean npc) {
		this.npc = npc;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
	public void translate(long x1, long y1){
		this.x += x1;
		this.y += y1;
	}
	
	public void incrementCount(){
		count++;
	}

	@Override
	public int compareTo(Alliance o) {
		if(o == null) return -1;
		return id == o.getId() ? 1 : -1;
	}
}
