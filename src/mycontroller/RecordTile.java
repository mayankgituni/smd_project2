/**
 * The University of Melbourne
 * SWEN30006 Software Modelling and Design
 * FileName: RecordTile.java
 *
 *
 * This class is used to pack the tile in ExploreMap
 * 
 *
 * @author  Chenyang Lu, Leewei Kuo, Xueting Tan
 * @StudentID 951933, 932975, 948775
 * @Username  chenyangl5, leeweik1, xuetingt
 * 
 * @Date  18/10/2018 
 */

package mycontroller;

import tiles.MapTile;


public class RecordTile {
	
	private MapTile mapTile;
	
	//whether the mapTile is explored or not
	private boolean explored;
	
	/**
	 * @param mapTile the mapTile we observed
	 */
	public RecordTile(MapTile mapTile){
		this.mapTile = mapTile;
		this.explored = true;
	}
	
	/**
	 * 
	 * @return the mapTile according to the ExploreMap
	 */
	public MapTile getMapTile(){
		return mapTile;
	}
	
	/**
	 * 
	 * @return true if we have explored this mapTile
	 */
	public boolean getExplored() {
		return explored;
	}
	
	public void setExplored(boolean b) {
		explored = b;
	}
}