package com.evemap.objects;

import java.awt.*;


public class Jump {
	SolarSystem from, to;
	private final Color sJump = new Color(0, 0, 0xFF, 0x30);
	private final Color cJump = new Color(0xFF, 0, 0, 0x30);
	private final Color rJump = new Color(0xFF, 0, 0xFF, 0x30);

	/**
	 *
	 * @param from - that the jump comes from
	 * @param to - Solar System that the jump goes to
	 */
	public Jump(SolarSystem from, SolarSystem to){
		this.from = from;
		this.to = to;
	}

	/**
	 * 	Create the Jump line on the map
	 * @param g Graphics object to Operate upon
	 */
	public void draw(Graphics g){
		if(from.getConstelationID() == to.getConstelationID()){
			g.setColor(sJump);
		}else if(from.getRegionID() == to.getRegionID()){
			g.setColor(cJump);
		}else{
			g.setColor(rJump);
		}
		g.drawLine(from.x, from.y, to.x, to.y);
	}
}
