package irtm.starcraft.game;

import java.util.ArrayList;

/**
 * Class representing a single instruction in a build order for StarCraft
 *
 * @author Dennis Soemers
 */
public class StarcraftBuildOrderInstruction {
	
	/**
	 * Types of instructions that can be recognized in build order lists
	 *
	 * @author Dennis Soemers
	 */
	public enum InstructionTypes{
		UNKNOWN,	// should probably never be used
		BUILDING,	// create a building
		UNIT,		// create a unit
		SCOUT		// send some unit scouting
	}
	
	private InstructionTypes type;
	private String instructionText;
	private ArrayList<StarcraftPrecondition> preconditions;
	
	public StarcraftBuildOrderInstruction(InstructionTypes type, String instructionText, ArrayList<StarcraftPrecondition> preconditions){
		this.type = type;
		this.instructionText = instructionText;
		this.preconditions = preconditions;
	}
	
	public String getInstructionText(){
		return instructionText;
	}
	
	public ArrayList<StarcraftPrecondition> getPreconditions(){
		return preconditions;
	}
	
	public InstructionTypes getType(){
		return type;
	}

}
