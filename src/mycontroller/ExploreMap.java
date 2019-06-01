/**
 * The University of Melbourne
 * SWEN30006 Software Modelling and Design
 * FileName: ExploreMap.java
 *
 *
 * This class is used to record the real map we have observed
 * 
 * @author  Chenyang Lu, Leewei Kuo, Xueting Tan
 * @StudentID 951933, 932975, 948775
 * @Username  chenyangl5, leeweik1, xuetingt
 * 
 * @Date  18/10/2018 
 */
package mycontroller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import world.World;
import tiles.MapTile;
import utilities.Coordinate;


public class ExploreMap {
	private HashMap<Coordinate, RecordTile> newMap = new HashMap<Coordinate, RecordTile>();
	public static ExploreMap instance;
	
	public ExploreMap() {
		getInitialMap();
	}
	
	public static synchronized ExploreMap getInstance() {
		if(instance == null) {
			instance = new ExploreMap();
			return instance;
		}else {
			return instance;
		}
	}
	
	/**
	 * update the map with currentView, set explored variable of 
	 * RecordTile to true
	 * 
	 * @param currentView the currentView of car
	 */
	public synchronized void updateMap(HashMap<Coordinate, MapTile> currentView) {
		Iterator iter = currentView.entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Coordinate key = (Coordinate) entry.getKey();
			MapTile val = (MapTile) entry.getValue();
			if(newMap.containsKey(key) && !newMap.get(key).getExplored()) {
				RecordTile rt = new RecordTile(val);
				newMap.put(key, rt);
			}
		}
	}
	
	/**
	 * initialize the map with walls and roads,
	 * the explored variable of RecordTile is 
	 * set to false
	 */
	private synchronized void getInitialMap(){
		HashMap<Coordinate, MapTile> map = World.getMap();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Coordinate key = (Coordinate) entry.getKey();
			MapTile val = (MapTile) entry.getValue();
			RecordTile rt = new RecordTile(val);
			rt.setExplored(false);
			newMap.put(key, rt);
		}
	}
	
	public synchronized HashMap<Coordinate, RecordTile> getExploredMap(){
		return newMap;
	}
}
