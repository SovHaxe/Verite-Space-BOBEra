package com.evemap;

import java.sql.ResultSet;

import com.evemap.dbinteraction.DBPersister;

public class SysSovPersistor {

	private DBPersister dbPersister;
	
	public SysSovPersistor(DBPersister dbPersister){
		this.dbPersister = dbPersister;
	}
	
	/**
	 * Fills a 2d integer array with alliance ids corresponding to the allaince
	 * with the highest influence at a point.
	 * @return Integer[][] of alliance ID's
	 */
	public Integer[][] getOldSysSovs() {
		Integer [][] data = null;
		try{
			data = new Integer[MapConstants.HORIZONTAL_SIZE][MapConstants.VERTICAL_SIZE];
			ResultSet rows = dbPersister.getSavedRows();
			while(rows.next()){
				ResultSet rs = dbPersister.getOldAlliances(rows.getInt(1));
				while(rs.next()){
					data[rs.getInt(2)][rs.getInt(3)] = rs.getInt(1);
				}
				rs.close();
			}
		}catch(Exception ex){
			data = null;
		}
		dbPersister.backupOldAllianceInformaiton();
		dbPersister.clearOldAlliances();
		return data;
	}
}
