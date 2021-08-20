package com.evemap.dbinteraction;

import java.sql.*;
import java.util.List;

import com.evemap.CalculateRow.SovData;
import com.evemap.objects.Alliance;

public class DBPersister {

	private java.sql.Connection db;
	
	public DBPersister(java.sql.Connection database){
		this.db = database;
	};
	
	public ResultSet getAllianceInformation() throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append(DBConstants.ID_COL + ", ");
		statement.append(DBConstants.NAME_COL + ", ");
		statement.append(DBConstants.COLOR_COL + " ");
		statement.append("from ");
		statement.append(DBConstants.EVEALLIANCES_TABLE + " ");
		statement.append("order by ");
		statement.append(DBConstants.ONMAP_COL + " desc, ");
		statement.append(DBConstants.NAME_COL + " desc" + ";");
		return doGetOnStringBuffer(statement);
	}
	
	public ResultSet getSolarSystemInformation() throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append(DBConstants.X_COL + ", ");
		statement.append(DBConstants.Z_COL + ", ");
		statement.append(DBConstants.SOLARSYSTEMID_COL + ", ");
		statement.append(DBConstants.ALLIANCEID_COL + ", ");
		statement.append(DBConstants.STANTION_COL + ", ");
		statement.append(DBConstants.CONSTELLATIONID_COL + ", ");
		statement.append(DBConstants.REGIONID_COL + ", ");
		statement.append(DBConstants.SOVEREIGNTYLEVEL_COL);
		statement.append(" from ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + ";");
		return doGetOnStringBuffer(statement);
	}
	
	public ResultSet getSolarSystemSovereignty() throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append("r." + DBConstants.X_COL + ", ");
		statement.append("r." + DBConstants.Z_COL);
		statement.append(" from ");
		statement.append(DBConstants.SOVCHANGELOG_TABLE);
		statement.append(" s left join ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE);
		statement.append(" r on ");
		statement.append("s." + DBConstants.SYSTEMID_COL + "=");
		statement.append("r." + DBConstants.SOLARSYSTEMID_COL + ";");
		return doGetOnStringBuffer(statement);
	}
	
	public ResultSet getJumpMapInformation() throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append(DBConstants.FROMSOLARSYSTEMID_COL + ", ");
		statement.append(DBConstants.TOSOLARSYSTEMID_COL);
		statement.append(" from ");
		statement.append(DBConstants.MAPSOLARSYSTEMJUMPS_TABLE + ";");
		return doGetOnStringBuffer(statement);
	}
	
	public ResultSet getTextualInformation() throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append("r." + DBConstants.X_COL + ", ");
		statement.append("r." + DBConstants.Z_COL + ", ");
		statement.append("r." + DBConstants.REGIONNAME_COL);
		statement.append(" from ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + " s ");
		statement.append(" left join ");
		statement.append(DBConstants.MAPREGIONS_TABLE + " r ");
		statement.append(" on ");
		statement.append("r." + DBConstants.REGIONID_COL + "=");
		statement.append("s." + DBConstants.REGIONID_COL);
		statement.append(" /*WHERE ");
		statement.append("s." + DBConstants.ALLIANCEID_COL + "<>0*/");
		statement.append(" group by ");
		statement.append("r." + DBConstants.REGIONID_COL + ";");
		return doGetOnStringBuffer(statement);
	}
	
	public ResultSet getNPCInfluences() throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append(DBConstants.ID_COL + ", ");
		statement.append(DBConstants.NAME_COL + ", ");
		statement.append(DBConstants.SYSTEMID_COL + ", ");
		statement.append(DBConstants.INFLUENCE_COL);
		statement.append(" from ");
		statement.append(DBConstants.NPCALLIANCES_COL + ";");
		return doGetOnStringBuffer(statement);
	}
	
//	select mapsolarsystems.* from sovchangelog, mapsolarsystems where
//	sovchangelog.systemID = mapsolarsystems.solarsystemID;
//	and sovchangelog.fromAllianceID != 0
	public ResultSet getSystemsWithSovChanges(boolean isto) throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + "." + DBConstants.X_COL + ", ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + "." + DBConstants.Z_COL + ", ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + "." + DBConstants.SOLARSYSTEMID_COL + ", ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + "." + DBConstants.ALLIANCEID_COL + ", ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + "." + DBConstants.STANTION_COL + ", ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + "." + DBConstants.CONSTELLATIONID_COL + ", ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + "." + DBConstants.REGIONID_COL + ", ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + "." + DBConstants.SOVEREIGNTYLEVEL_COL + ", ");
		if(isto){
			statement.append(DBConstants.SOVCHANGELOG_TABLE + "." + DBConstants.TOALLIANCEID_COL);
		} else {
			statement.append(DBConstants.SOVCHANGELOG_TABLE + "." + DBConstants.FROMALLIANCEID_COL);
		}		
		statement.append(" from ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + ", ");
		statement.append(DBConstants.SOVCHANGELOG_TABLE);
		statement.append(" where ");
		statement.append(DBConstants.SOVCHANGELOG_TABLE);
		statement.append("." + DBConstants.SYSTEMID_COL + " = ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE);
		statement.append("." + DBConstants.SOLARSYSTEMID_COL);
		statement.append(" and ");
		statement.append(DBConstants.SOVCHANGELOG_TABLE + ".");
		if(isto){
			statement.append(DBConstants.TOALLIANCEID_COL);
		} else {
			statement.append(DBConstants.FROMALLIANCEID_COL);
		}
		statement.append("!= 0;");
		return doGetOnStringBuffer(statement);
	}
	
//	String sql=
//	"SELECT l.fromAllianceID, l.toAllianceID, s.solarSystemName, r.regionName, s.stantion " +
//	"FROM sovchangelog l " +
//	"LEFT JOIN mapsolarsystems s ON s.solarSystemID=l.systemID " +
//	"LEFT JOIN mapregions r ON s.regionID=r.regionID " +
//	"ORDER BY r.z, r.x";
//	System.out.println(statement.toString());
	public ResultSet getSoverentyChanges() throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append("l." + DBConstants.FROMALLIANCEID_COL + ", ");
		statement.append("l." + DBConstants.TOALLIANCEID_COL + ", ");
		statement.append("s." + DBConstants.SOLARSYSTEMNAME_COL + ", ");
		statement.append("r." + DBConstants.REGIONNAME_COL + ", ");
		statement.append("s." + DBConstants.STANTION_COL + ", ");
		statement.append("s." + DBConstants.GRIDREF_COL);
		statement.append(" from ");
		statement.append(DBConstants.SOVCHANGELOG_TABLE + " l");
		statement.append(" left join ");
		statement.append(DBConstants.MAPSOLARSYSTEMS_TABLE + " s");
		statement.append(" on ");
		statement.append("s." + DBConstants.SOLARSYSTEMID_COL + "=");
		statement.append("l." + DBConstants.SYSTEMID_COL);
		statement.append(" left join ");
		statement.append(DBConstants.MAPREGIONS_TABLE + " r");
		statement.append(" on ");
		statement.append("s." + DBConstants.REGIONID_COL + "=");
		statement.append("r." + DBConstants.REGIONID_COL);
		statement.append(" order by ");
		statement.append("r." + DBConstants.Z_COL + ", ");
		statement.append("r." + DBConstants.X_COL + ";");
		return doGetOnStringBuffer(statement);
	}
	
	public void updateAllianceColors() throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("update ");
		statement.append(DBConstants.EVEALLIANCES_TABLE);
		statement.append(" set ");
		statement.append(DBConstants.ONMAP_COL);
		statement.append("=0;");
		doSetOnStringBuffer(statement);
	}
	
	public void updateAlliancesColorInTable(String r, String g, String b, Alliance al){
		try{			
			StringBuffer statement = new StringBuffer();
			statement.append("update ");
			statement.append(DBConstants.EVEALLIANCES_TABLE);
			statement.append(" set ");
			statement.append(DBConstants.COLOR_COL + "='");
			statement.append(r);
			statement.append(g);
			statement.append(b);
			statement.append("' where ");
			statement.append(DBConstants.ID_COL + "=");
			statement.append(al.getId() + ";");
			doSetOnStringBuffer(statement);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
//	select y from sov_change_information group by y having count(*) > 1;
	public ResultSet getSavedRows() throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append(DBConstants.Y_COL);
		statement.append(" from ");
		statement.append(DBConstants.SOVCHANGE_TABLE);
		statement.append(" group by ");
		statement.append(DBConstants.Y_COL);
		statement.append(" having count(*) > 1;");
		return doGetOnStringBuffer(statement);
	}
	
	public ResultSet getOldAlliances(int row) throws SQLException{
		StringBuffer statement = new StringBuffer();
		statement.append("select ");
		statement.append(DBConstants.ALLIANCE_COL + ", ");
		statement.append(DBConstants.X_COL + ", ");
		statement.append(DBConstants.Y_COL);
		statement.append(" from ");
		statement.append(DBConstants.SOVCHANGE_TABLE);
		statement.append(" where ");
		statement.append(DBConstants.Y_COL + "='");
		statement.append(row + "'");
		statement.append(";");
		return doGetOnStringBuffer(statement);
	}
	
	public void backupOldAllianceInformaiton(){
		clearOldAlliances(DBConstants.SOVCHANGE_TABLE_OLD);
		try{			
			StringBuffer statement = new StringBuffer();
			statement.append("INSERT INTO ");
			statement.append(DBConstants.SOVCHANGE_TABLE_OLD);
			statement.append(" SELECT * FROM ");
			statement.append(DBConstants.SOVCHANGE_TABLE);
			statement.append(";");
			doSetOnStringBuffer(statement);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void updateOldAlliances(List<SovData> data, Connection cn){
		if(data.isEmpty())
			return;
		try{			
			StringBuffer statement = new StringBuffer();
			statement.append("replace ");
			statement.append(DBConstants.SOVCHANGE_TABLE);
			statement.append(" (");
			statement.append(DBConstants.ALLIANCE_COL + ", ");
			statement.append(DBConstants.X_COL + ", ");
			statement.append(DBConstants.Y_COL + ") ");
			statement.append("values ");
			for(SovData d : data){
				statement.append("(" + d.al + ", ");
				statement.append(d.x + ", ");
				statement.append(d.y + ")");
				statement.append(", ");
			}
			statement.delete(statement.length()-2, statement.length()-1);
			statement.append(";");
			Statement st = cn.createStatement();
			st.executeUpdate(statement.toString());
			st.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void clearOldAlliances(){
		clearOldAlliances(DBConstants.SOVCHANGE_TABLE);
	}
	
	private void clearOldAlliances(String table){
		try{			
			StringBuffer statement = new StringBuffer();
			statement.append("delete from ");
			statement.append(table);
			statement.append(";");
			doSetOnStringBuffer(statement);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private ResultSet doGetOnStringBuffer(StringBuffer sb) throws SQLException{
		Statement st = db.createStatement();
		ResultSet rs =  st.executeQuery(sb.toString());
		return rs;
	}
	
	private void doSetOnStringBuffer(StringBuffer sb) throws SQLException{
		Statement st = db.createStatement();
		st.executeUpdate(sb.toString());
		st.close();
	}
}
