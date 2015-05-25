package irtm.starcraft.game;

/**
 * A precondition that must be satisfied before some instruction related to StarCraft can be executed
 *
 * @author Dennis Soemers
 */
public class StarcraftPrecondition {
	
	/**
	 * Different types of preconditions that a single instruction can have
	 *
	 * @author Dennis Soemers
	 */
	public enum PreconditionTypes{
		NOTHING,		// this should probably never be used
		SUPPLY,			// indicates that we must have reached a certain supply level
		GAS,			// must have a certain amount of gas available
		MINERALS,		// must have a certain amount of minerals available
		PERCENTAGE		// must have reached a certain percentage completion on a specific unit or building type
	}
	
	private PreconditionTypes type;
	private int value;
	private String unitOrBuildingType;
	
	public StarcraftPrecondition(PreconditionTypes type, int value){
		this.type = type;
		this.value = value;
		this.unitOrBuildingType = null;
	}
	
	public StarcraftPrecondition(PreconditionTypes type, int value, String unitOrBuildingType){
		this.type = type;
		this.value = value;
		this.unitOrBuildingType = unitOrBuildingType;
	}
	
	public PreconditionTypes getType(){
		return type;
	}
	
	public String getUnitOrBuildingType(){
		return unitOrBuildingType;
	}
	
	public int getValue(){
		return value;
	}

}
