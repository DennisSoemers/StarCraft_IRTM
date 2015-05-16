package irtm.starcraft.game;

import java.util.ArrayList;

/**
 * Class for a single StarCraft Strategy which is obtained by reading
 * and processing a single file.
 * 
 * @author Dennis Soemers
 *
 */
public class StarcraftStrategy {
	
	private String strategyName;
	private ArrayList<StarcraftBuildOrder> buildOrders = new ArrayList<StarcraftBuildOrder>();
	private ArrayList<String> counteredBySoft = new ArrayList<String>();
	private ArrayList<String> counteredByHard = new ArrayList<String>();
	private ArrayList<String> counterToSoft = new ArrayList<String>();
	private ArrayList<String> counterToHard = new ArrayList<String>();
	private ArrayList<String> weakMaps = new ArrayList<String>();
	private ArrayList<String> strongMaps = new ArrayList<String>();
	
	public StarcraftStrategy(String strategyName){
		this.strategyName = strategyName;
	}
	
	public void addBuildOrder(StarcraftBuildOrder buildOrder){
		buildOrders.add(buildOrder);
	}
	
	public void addCounteredBySoft(String softCounter){
		counteredBySoft.add(softCounter);
	}
	
	public void addCounteredByHard(String hardCounter){
		counteredByHard.add(hardCounter);
	}
	
	public void addCounterToSoft(String softCounter){
		counterToSoft.add(softCounter);
	}
	
	public void addCounterToHard(String hardCounter){
		counterToHard.add(hardCounter);
	}
	
	public void addWeakMap(String mapName){
		weakMaps.add(mapName);
	}
	
	public void addStrongMap(String mapName){
		strongMaps.add(mapName);
	}

}
