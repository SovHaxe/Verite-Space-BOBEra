package com.evemap;

import java.awt.Color;
import java.sql.Connection;
import java.util.*;

import com.evemap.dbinteraction.DBPersister;
import com.evemap.objects.*;

public class CalculateRow extends Thread {

	private final double INSENSITIVITY = 500;
	private final double VALIDINF = 0.023;

	private InfluenceCalculator map;

	Map<Alliance, Double> totalInf = new HashMap<Alliance, Double>();

//	Current influence state
	private int x_from, x_to, currentRow = 0;
	private double max = 0.0;

	private double[] prevInf = new double[0];
	private boolean[] curBorder = new boolean[0];
	private Alliance[] curRow = new Alliance[0];      
	private Alliance[] prevRow = null;

	private Integer[][] oldColors;
	private boolean useOldColors = false;

	private Alliance[][] sovMap;
	
	private Map<Integer, Alliance> alliances;

	private List<SovData> group;
	private Connection cn;

	public CalculateRow(int x_from, int x_to, InfluenceCalculator map, Connection cn){
		this.x_from = x_from;
		this.x_to = x_to;
		this.map = map;
		this.cn = cn;
		sovMap = new Alliance[MapConstants.HORIZONTAL_SIZE/MapConstants.SAMPLE_RATE][MapConstants.VERTICAL_SIZE/MapConstants.SAMPLE_RATE];
		group = new ArrayList<SovData>(75);
	}

	public void run(){
		curRow = new Alliance[MapConstants.HORIZONTAL_SIZE/MapConstants.THREADPOOL_SIZE];
		List<SolarSystem> sSov = new ArrayList<SolarSystem>();

		for(SolarSystem solarsys : map.getSystemsSov())
			sSov.add(solarsys);
		while (true){
			currentRow++;
			for (int x = x_from; x < x_to; x++){
				totalInf.clear();

				getTotalInfluenceforPoint(x, currentRow, sSov, totalInf);

				max = 0.0;
				Alliance best = getAllianceWithHighestInfluence(totalInf, false);

				int q = x - x_from;
				if (best != null){
					if (best.getColor() == null){
						if(best.isNpc()){
							synchronized (map.getColorTable()) {
								best.setColor(Color.BLACK, map.getColorTable());
							}
						} else {
							synchronized (map.getColorTable()) {
								best.setColor(map.nextColor(), map.getColorTable());
							}
						}
						map.saveColor(best);
					}
					best.translate(x, currentRow);
					best.incrementCount();
				}
				
				if (best != null && (x % MapConstants.SAMPLE_RATE == 0) && (currentRow % MapConstants.SAMPLE_RATE) == 0 ){					
					sovMap[x/MapConstants.SAMPLE_RATE][currentRow / MapConstants.SAMPLE_RATE] = best.isNpc() ? null : best;
				}

				curRow[q] = best;
				//draw the prev row point
				if (currentRow > 0)	{
					Alliance prevAlliance = prevRow[q];
					
					if(prevAlliance != null && !prevAlliance.isNpc()){
						saveAllianceAtPosition(x, currentRow, prevAlliance.getId());
						drawSolidWithBorder(q, x, currentRow, best, curBorder, prevRow, prevInf);
						if(useOldColors){
							if(oldColors[x][currentRow] == null || !(prevAlliance.getId() == oldColors[x][currentRow]))
								if(oldColors[x][currentRow] != null)
									drawLined(q, x, currentRow, alliances.get(oldColors[x][currentRow]).getColor(), prevInf);
						}
					}
				}
				prevInf[q] = max;
				curBorder[q] = currentRow==0|| prevRow[q] == null && best != null || prevRow[q] != null && best == null || prevRow[q] != null && !prevRow[q].equals(best);
			}

			System.arraycopy(curRow, 0, prevRow, 0, curRow.length);

			if(currentRow == MapConstants.VERTICAL_SIZE-1)
				break;

			map.repaint();
		}
		
		flush();
		System.arraycopy(prevInf, 0, map.getPrevInf(), x_from, x_to-x_from-1);
		System.arraycopy(curRow, 0, map.getPrevRow(), x_from, x_to-x_from);
		synchronized(map.getSovMap()){
			for(int x = 0; x < sovMap.length; x++){
				for(int y = 0; y < sovMap[0].length; y++){
					if(sovMap[x][y] != null){
						map.getSovMap()[x][y] = sovMap[x][y];
					}
				}
			}
		}
	}

	private void drawSolidWithBorder(int q, int x, int y, Alliance best, boolean[] cborder, Alliance[] cprevRow, double[] cprevInf){
		boolean border = cborder[q]|| cprevRow[q] == null && best != null || cprevRow[q] != null && best == null || 
		!best.equals(cprevRow[q]) ||q > 0 && !cprevRow[q].equals(cprevRow[q-1])||(cprevRow.length > q+1 && !cprevRow[q].equals(cprevRow[q+1]));

		int alpha = Math.min(190, (int)(Math.log(Math.log(cprevInf[q]+1.0)+1.0)*700));

		Color c = new Color(cprevRow[q].getColor().getRed(), cprevRow[q].getColor().getGreen(), cprevRow[q].getColor().getBlue(), 
				(border ? Math.max(0x48, alpha) : alpha));

		synchronized(map.getGraphicsManager()){
			map.getGraphicsManager().setColor(c);
			map.getGraphicsManager().fillRect(x, y - 1, 1, 1);
		}
	}

	private void drawLined(int q, int x, int y, Color co, double[] cprevInf){
		int alpha = Math.min(190, (int)(Math.log(Math.log(cprevInf[q]+1.0)+1.0)*700));
		Color c = new Color(co.getRed(), co.getGreen(), co.getBlue(), alpha);
		drawColorLined(x, y, c);
	}

	private void drawColorLined(int x, int y, Color c){
		synchronized(map.getGraphicsManager()){
			map.getGraphicsManager().setColor(c);
			drawSovChanged(x, y - 1);
		}
	}

	private void drawSovChanged(int x, int y) {
		int slant = 5;
		if((((y%slant) + x) % slant) == 0)
			synchronized(map.getGraphicsManager()){
				map.getGraphicsManager().fillRect(x, y, 1, 1);
			}
	}

	private void getTotalInfluenceforPoint(int x, int y, List<SolarSystem> sSov, Map<Alliance, Double> totalInfluence){
		int dx, dy, len2;
		Double d;
		for(SolarSystem ss : sSov){
			dx = x - ss.x;

			if (dx > 400 || dx < -400) 
				continue;
			dy = y - ss.y;

			if (dy > 400 || dy < -400) 
				continue;
			len2 = dx * dx + dy * dy;

			if (len2 > 160000)
				continue;
			
			for(int loc = 0; loc < ss.alliances.length; loc++){
				d = totalInfluence.get(ss.alliances[loc]);
				totalInfluence.put(ss.alliances[loc], Double.valueOf((d == null ? 0 : d.doubleValue())) + 
						ss.influences[loc] / (INSENSITIVITY + len2));
			}
		}
	}
	
	private Alliance getAllianceWithHighestInfluence(Map<Alliance, Double> totalInfluence, boolean isOld){
		Alliance best = null;
		Double d;
		double priMax = max;
		for(Alliance al : totalInfluence.keySet()){
			d = totalInfluence.get(al);
			if (d.doubleValue() > priMax){
				priMax = d.doubleValue();
				best = al;
			}
		}
		if (priMax < VALIDINF) best = null;
		if(isOld){
		} else {
			max = priMax;
		}
		return best;
	}

	public void setOldSovs(Integer[][] oldSovs) {
		this.oldColors = oldSovs;
		useOldColors = true;
	}

	public void setupRowData(double[] prevInf, boolean[] curBorder, Alliance[] curRow, Alliance[] prevRow){
		this.prevInf = prevInf;
		this.curBorder = curBorder;
		this.curRow = curRow;
		this.prevRow = prevRow;
	}

	public void setAlliances(HashMap<Integer, Alliance> alliances) {
		this.alliances = alliances;
	}
	
	private void saveAllianceAtPosition(int x, int y, int c) {
		group.add(new SovData(x, y, c){
			
		});
		if(group.size() > 49){
			DBPersister.updateOldAlliances(group, cn);
			group.clear();
		}
	}
	
	private void flush() {
		DBPersister.updateOldAlliances(group, cn);
		group.clear();
	}
	
//	Holder Class for sovData Information
	public class SovData {
		public int x, y, al;
		public SovData(int x, int y, int al){
			this.x = x;
			this.y = y;
			this.al = al;
		}		
	}
}