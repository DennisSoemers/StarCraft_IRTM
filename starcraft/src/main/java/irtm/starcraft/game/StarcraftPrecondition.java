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
		NOTHING,
		SUPPLY,
		GAS,
		MINERALS
	}
	
	private PreconditionTypes type;
	private int value;
	
	public StarcraftPrecondition(PreconditionTypes type, int value){
		this.type = type;
		this.value = value;
	}
	
	public PreconditionTypes getType(){
		return type;
	}
	
	public int getValue(){
		return value;
	}

}
