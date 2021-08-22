package com.evemap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.evemap.dbinteraction.*;
import com.evemap.objects.*;

public class DataManager {

	private String soveregntyDate;
	private long time;
	
	private DBPersister dbPersister;
	private SysSovPersistor p;
	
	//Hashtable of all of the alliances
	private HashMap<Integer, Alliance> alliances = new HashMap<Integer, Alliance>();
	//	Hashtable of the solar systems
	private HashMap<Integer, SolarSystem> systems = new HashMap<Integer, SolarSystem>(250);
	//	Vector of all the systems with sov claims
	private List<SolarSystem> systemsSov = new ArrayList<SolarSystem>();
	//	Hashtable of all the stargates for distributing inflence
	private HashMap<SolarSystem, List<SolarSystem>> jumpsTable = new HashMap<SolarSystem, List<SolarSystem>>();
	
	//Colortable for the alliance
	private Vector<Color> colorTable = new Vector<Color>(); 
	
	private BufferedImage outputImage = new BufferedImage(MapConstants.HORIZONTAL_SIZE, MapConstants.VERTICAL_SIZE, BufferedImage.TYPE_INT_ARGB);
	private Graphics2D graphicsManager = outputImage.createGraphics();
	private JPanel imagePanel;
	
	private Connection[] db;
	
	public DataManager(Connection[] db, String date){
		soveregntyDate = date;
		this.time = System.currentTimeMillis();
		this.db = db;

		dbPersister = new DBPersister(db[db.length-1]);
		
		try {
			resolveDBInformation();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Create the persistance system as well as begin to handle a window.
	 * @throws SQLException
	 */
	private void resolveDBInformation() throws SQLException {
//			System.out.println(Runtime.getRuntime().freeMemory()/1048576 + "MB");
			p = new SysSovPersistor(dbPersister);
			
			imagePanel = new JPanel();
			imagePanel.setSize(MapConstants.HORIZONTAL_SIZE/2, MapConstants.VERTICAL_SIZE/2);
			imagePanel.setPreferredSize(new Dimension(MapConstants.HORIZONTAL_SIZE/2, MapConstants.VERTICAL_SIZE/2));

			Integer[][] oldColors = p.getOldSysSovs();
						
			parseAllianceInformation(alliances);

			//load systems
			graphicsManager.setColor(new Color(0, 0, 0x0));
			graphicsManager.fillRect(0,0,MapConstants.HORIZONTAL_SIZE,MapConstants.VERTICAL_SIZE);

			parseSolarSystemInformation();
			
			Vector<Jump> jumpsV = parseJumpInformation(systems, jumpsTable);

			//alliance influence
			Vector<SolarSystem> sovOrig = new Vector<SolarSystem>();
			
			for(SolarSystem ss : systemsSov)
				sovOrig.addElement(ss);
			
			for(SolarSystem ss : sovOrig)
				manageInfluence(ss);
			
			sovOrig.clear();
			
			//NPC
			addNPCAlliances(systems);
			
			jumpsTable = null;
			
			InfluenceCalculator influenceMap = new InfluenceCalculator(this, soveregntyDate, dbPersister, jumpsV, db);
			
			if(oldColors != null)
				influenceMap.setOldSysSovs(oldColors);
//			System.out.println(Runtime.getRuntime().freeMemory()/1048576 + "MB");
			new Thread(influenceMap, "Influence Generator Thread").start();
	}
	
	private void parseAllianceInformation(HashMap<Integer, Alliance> all) throws SQLException {
		ResultSet rs = dbPersister.getAllianceInformation();
	    
		Alliance al;

		while(rs.next()){
			al = new Alliance(rs.getString(2), rs.getInt(1));
			int colour = Integer.parseInt(rs.getString(3), 16);
			if(colour >= 128)
				synchronized (colorTable) {
					al.setColor(new Color(colour), colorTable);
				}
			all.put(Integer.valueOf(al.getId()), al);
		}
		rs.close();
	}
	
	private void parseSolarSystemInformation() throws SQLException{
		ResultSet rs = dbPersister.getSolarSystemInformation();

		while(rs.next()){
			SolarSystem ss = new SolarSystem(rs.getInt(3), rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(5) == 1,
					(int) Math.floor(((rs.getDouble(1) / MapConstants.SCALE) + MapConstants.HORIZONTAL_SIZE / 2 + MapConstants.HORIZONTAL_OFFSET)+0.5), 
					(int) Math.floor(((rs.getDouble(2) / MapConstants.SCALE) + MapConstants.VERTICAL_SIZE / 2 + MapConstants.VERTICAL_OFFSET)+0.5),
					alliances.get(Integer.valueOf(rs.getInt(4))));
			systems.put(Integer.valueOf(ss.getId()), ss);
			if(ss.getSovereignty() != null){
				systemsSov.add(ss);
			}
		}
		rs.close();
	}

	/**
	 * Connects to The Persistence store and parses through the Jump objects to a Vector
	 * @param system
	 * @param jumpTable
	 * @return the Vector map of all jumps
	 * @throws SQLException
	 */
	private Vector<Jump> parseJumpInformation(HashMap<Integer, SolarSystem> system, HashMap<SolarSystem, List<SolarSystem>> jumpTable) throws SQLException{
		//load jumps
		ResultSet rs = dbPersister.getJumpMapInformation();
		Vector<Jump> jumpsV = new Vector<Jump>();
		while(rs.next()){
			SolarSystem from = system.get(Integer.valueOf(rs.getInt(1)));
			SolarSystem to = system.get(Integer.valueOf(rs.getInt(2)));
			jumpsV.addElement(new Jump(from, to));
			if(from != null && to != null){
				List<SolarSystem> v = jumpTable.get(from);
				if (v == null){
					v = new Vector<SolarSystem>();
					jumpTable.put(from, v);
				}
				v.add(to);
				v = jumpTable.get(to);
				if(v == null){
					v = new Vector<SolarSystem>();
					jumpTable.put(to, v);
				}
				v.add(from);
			}
		}
		rs.close();
		return jumpsV;
	}
	
	private void manageInfluence(SolarSystem ss){
		double influence = 10.0; //Changed this from 10.0
		int level = 2;
		//Section for influence multipliers for various bonus factors
		if(ss.hasStation()){
			influence *= 6;
			level = 1;
		}
		
		switch(ss.getSovLevel()){
		case 0: influence*=0.5;
		break;
		case 2:	influence*=1.1;
		break;
		case 3:	influence*=1.2;
		break;
		case 4:	influence*=1.4;
		break;
		case 5:	influence*=1.6;
		break;
		}
		addInfluence(ss, influence, ss.getSovereignty(), level, new ArrayList<SolarSystem>());
	}
	
	public void addInfluence(SolarSystem ss, double value, Alliance al, int level, List<SolarSystem> set){
		ss.addInfluence(al, value);
		if(!systemsSov.contains(ss))
			systemsSov.add(ss);
		if(level >= 4) //Changed this from 4 
			return;
		//spread over the jump gates
		List<SolarSystem> arr = jumpsTable.get(ss);
		if(arr == null)
			return;
		for(SolarSystem s : arr){
			if(set.contains(s))
				continue;
			set.add(s);
			addInfluence(s, value * 0.3, al, level + 1, set);
		}
	}
	
	private void addNPCAlliances(HashMap<Integer, SolarSystem> sovSystems) throws SQLException{
		HashMap<Integer, Alliance> npc = new HashMap<Integer, Alliance>();
		Color npcColor = new Color(0,0,0,0);

		ResultSet rs = dbPersister.getNPCInfluences();
		while(rs.next()){
			int id = rs.getInt(1);
			Alliance al = npc.get(Integer.valueOf(id));
			if(al == null){
				al = new Alliance(rs.getString(2), id, npcColor, npcColor, true);
				npc.put(Integer.valueOf(id), al);
			}
			SolarSystem ss = sovSystems.get(Integer.valueOf(rs.getInt(3)));
			if(ss == null)
				continue;
			double influence = rs.getDouble(4);
			addInfluence(ss, influence, al, 4, new ArrayList<SolarSystem>());
		}
	}
	
//	static Random random = new Random();
	//TODO How TF does this work, WHY???

	/**
	 * Potential Quantized color - Potentially Squared distance on an RGB color cube
	 *  > (11:13:14 PM) robbie_zino: looks like it's computing the squared distance on an RGB colour cube
	 *  > (11:13:23 PM) Morkfang: looks like some quantizing tuff
	 *
	 *	Looks like this is an RGB color routine to create a visible pallet and avoid being too near to another color
	 * TODO: Map this out in a flow diagram and calculate
	 * @return {@link Color}
	 */
	public Color nextColor(){
		int max = 0, min = 1000000000, cr = 0, cg = 0, cb = 0;
		for(int r = 0; r < 256; r += 4)
			for(int g = 0; g < 256; g += 4)
				for(int b = 0; b < 256; b += 4){
					if(r + g + b < 256 || r + g + b > 512) //
						continue;
					min = 1000000000;
					for(Color c : colorTable){ // Pull color out from colorTable
						int dred = r - c.getRed();
						int dgreen = g - c.getGreen();
						int dblue = b - c.getBlue();
						int dif = dred * dred + dgreen * dgreen + dblue * dblue;
						if(min > dif) // Is the difference greater then the minimum difference
							min = dif;
					}
					if(max < min){
						max = min;
						cr = r;
						cg = g;
						cb = b;
					}
				}
//		Color.getHSBColor(random.nextFloat(), 1.0F, 1.0F);
		return new Color(cr, cg, cb, 0x90);
	}

	/**
	 * Persist the color of an alliance to db
	 * @param al The Alliance to persist too
	 */
	public void saveColor(Alliance al){
		String r = Integer.toHexString(al.getColor().getRed());
		String g = Integer.toHexString(al.getColor().getGreen());
		String b = Integer.toHexString(al.getColor().getBlue());
		while(r.length() < 2)
			r += "0";
		while(g.length() < 2)
			g += "0";
		while(b.length()<2)
			b += "0";

		dbPersister.updateAlliancesColorInTable(r, g, b, al);
	}
	
	public void writeToFile(boolean wasSuccessful){

		System.out.println(new Time(System.currentTimeMillis() - time).toString().substring(3));
		try{
			/*ImageIO.write(outputImage, "png", new java.io.File("influence_" + soveregntyDate + 
					(wasSuccessful ? "" : "_FAILED") + ".png"));*/
			ImageIO.write(outputImage, "png", new java.io.File("influence.png"));
			System.out.println("Image saved");
		}catch(Exception ex){
			ex.printStackTrace();
		}
		System.exit(0);
	}
	
	public void paint(Graphics graphics) {
		if(graphics == null)
			return;
		graphics.drawImage(outputImage, 0, 0, MapConstants.HORIZONTAL_SIZE/2, MapConstants.VERTICAL_SIZE/2, null);
	}
	
	public synchronized List<SolarSystem> getSystemsSov() {
		return systemsSov;
	}
	public Graphics2D getGraphicsManager() {
		return graphicsManager;
	}
	public synchronized Vector<Color> getColorTable() {
		return colorTable;
	}
	public HashMap<Integer, SolarSystem> getSystems() {
		return systems;
	}
	public HashMap<Integer, Alliance> getAlliances() {
		return alliances;
	}
	public JPanel getImagePanel() {
		return imagePanel;
	}
	public BufferedImage getOutputImage() {
		return outputImage;
	}
}
