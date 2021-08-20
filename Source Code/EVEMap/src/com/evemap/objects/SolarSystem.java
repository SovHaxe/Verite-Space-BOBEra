package com.evemap.objects;

import java.awt.Point;

import com.evemap.*;

public class SolarSystem extends Point implements Comparable<SolarSystem>{

    private int id, constelationID, regionID, sovLevel;
    private Alliance sovereignty;

//  Use arrays in order to greatly speed up execution time of CalculateRow
    public Alliance[] alliances; 
    public double[] influences;
    
    private boolean station;
    
    public SolarSystem(){}
    
    public SolarSystem(SolarSystem ss){
    	this(ss.getId(), ss.getConstelationID(), ss.getRegionID(), ss.getSovLevel(), ss.hasStation(), ss.x, ss.y, ss.getSovereignty());
    	alliances = new Alliance[ss.alliances.length];
    	System.arraycopy(ss.alliances, 0, alliances, 0, alliances.length);
    	influences = new double[ss.influences.length];
    	System.arraycopy(ss.influences, 0, influences, 0, influences.length);
    }
    
    public SolarSystem(int id, int constelationID, int regionID, int sovLevel, boolean hasStation, int x, int y, Alliance sovereignty){
    	super(x, y);
    	this.id = id;
    	this.constelationID = constelationID;
    	this.regionID = regionID;
    	this.sovLevel = sovLevel;
    	this.station = hasStation;
    	this.sovereignty = sovereignty;
    }
    
    public void addInfluence(Alliance al, double value){
    	if(alliances == null){
    		alliances = new Alliance[1];
    		influences = new double[1];
    		alliances[0] = al;
        	influences[0] = value;
        	return;
    	}
    	boolean exists = false;
    	Alliance[] tempalliances = new Alliance[alliances.length];
    	double[] tempinfluences = new double[influences.length];
    	System.arraycopy(alliances, 0, tempalliances, 0, alliances.length);
    	System.arraycopy(influences, 0, tempinfluences, 0, influences.length);
    	for(int loc = 0; loc < alliances.length; loc++)
    		if(alliances[loc].equals(al)){
    			exists = true;
    			influences[loc] += value;
    			break;
    		} 
    	if(!exists){
    		alliances = new Alliance[alliances.length + 1];
    		influences = new double[influences.length + 1];
    		System.arraycopy(tempalliances, 0, alliances, 0, tempalliances.length);
        	System.arraycopy(tempinfluences, 0, influences, 0, tempinfluences.length);
        	alliances[alliances.length - 1] = al;
        	influences[influences.length - 1] = value;
    	}
    }
    
    public void draw(InfluenceCalculator map){
    	if(sovereignty != null && sovereignty.getColor() == null){
    		sovereignty.setColor(map.nextColor(), map.getColorTable());
    		map.saveColor(sovereignty);
    	}
        map.getGraphicsManager().setColor(sovereignty != null ? sovereignty.getStarColor() : MapConstants.STAR_COLOR);
        if(station){
        	map.getGraphicsManager().fillOval(x-1, y-1, 2, 2);
        	/*if(sovLevel==5)
            {
            	int[] xarray = {x-3,x+3,x};
            	int[] yarray = {y-3,y-3,y+3};
            	map.getGraphicsManager().drawPolygon(xarray,yarray,3);
            }
        	else*/
        		map.getGraphicsManager().drawRect(x-2, y-2, 4, 4);
        }else if (sovereignty != null){
        	map.getGraphicsManager().fillOval(x-2, y-2, 4, 4);
        }else{
        	map.getGraphicsManager().fillOval(x-1, y-1, 2, 2);
        }
    }

	public boolean hasStation() {
		return station;
	}

	public Alliance getSovereignty() {
		return sovereignty;
	}

	public int getId() {
		return id;
	}

	public int getConstelationID() {
		return constelationID;
	}

	public int getRegionID() {
		return regionID;
	}

	public int getSovLevel() {
		return sovLevel;
	}

	@Override
	public int compareTo(SolarSystem o) {
		return (id == o.getId() ? 1 : -1);
	}
	
	@Override
	public int hashCode(){
		return id;
	}
	
	@Override
    public String toString(){
    	return id + " [Region: " + regionID + ", Const: " + constelationID + "] Sov: " + " by " + sovereignty.getName() + 
    	(station ? "with" : "without") + " Station.";
    }
}
